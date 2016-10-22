package sleep.main;


import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryOptions;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DbHandler {

    private Manager manager;
    private Database database;
    private String dbName = "sleepdb";
    private String TAG = "Database";

    public DbHandler(Context context) {

        try {
            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
            Log.d (TAG, "Manager created");
        } catch (IOException e) {
            Log.e(TAG, "Cannot create manager object");
            throw new RuntimeException(e);
        }

        // create a name for the database and make sure the name is legal

        if (!Manager.isValidDatabaseName(dbName)) {
            Log.e(TAG, "Bad database name");
            return;
        }

        try {
            database = manager.getDatabase(dbName);
            Log.d (TAG, "Database created");

        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Cannot get database");
            throw new RuntimeException(e);
        }

    }

    public String createDocument(Map<String, Object> content) {

        // create an empty document
        Document document = database.createDocument();


        // add content to document and write the document to the database
        try {
            document.putProperties(content);
            Log.d (TAG, "Document written to database named " + dbName + " with ID = " + document.getId());
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Cannot write document to database", e);
        }

        return document.getId();
    }

    public Map<String, Object> getDocument(String id) {
        return database.getDocument(id).getProperties();
    }

    public void addWakeupEntry(String id, String wakeupTime) {
// retrieve the document from the database
        Document retrievedDocument = database.getDocument(id);

        // display the retrieved document
        Log.d(TAG, "retrievedDocument=" + String.valueOf(retrievedDocument.getProperties()));

        // update the document
        Map<String, Object> updatedProperties = new HashMap<>();

        updatedProperties.putAll(retrievedDocument.getProperties());
        List<String> wakeupTimes = (List)retrievedDocument.getProperties().get("wakeupTimes");
        if(wakeupTimes == null) {
            wakeupTimes = new ArrayList<>();

        }
            wakeupTimes.add(wakeupTime);
        updatedProperties.put ("wakeupTimes", wakeupTimes);

        try {
            retrievedDocument.putProperties(updatedProperties);
            Log.d(TAG, "updated retrievedDocument=" + String.valueOf(retrievedDocument.getProperties()));

        } catch (CouchbaseLiteException e) {
            Log.e (TAG, "Cannot update document", e);
            throw new RuntimeException(e);
        }
    }

    public List<Map<String, Object>> getAllDocuments() {

        Query query = database.createAllDocumentsQuery();

        try {
            List<Map<String, Object>> allDocs = new ArrayList<>();
            QueryEnumerator results = query.run();
            for (Iterator<QueryRow> it = results; it.hasNext();) {
                QueryRow row = it.next();

                //String docId = (String) row.getValue();
                Log.i(TAG, "result: " + String.valueOf(row.getDocument().getProperties()));
                Map<String, Object> entry = new HashMap<>();

                allDocs.add(row.getDocument().getProperties());

            }
            return allDocs;
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }




}
