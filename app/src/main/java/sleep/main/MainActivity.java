package sleep.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.android.AndroidContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DbHandler dbHandler;

    final String TAG = "HelloWorld";
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandler = new DbHandler(this);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private String getDateString() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Calendar calendar = GregorianCalendar.getInstance();
        return dateFormatter.format(calendar.getTime());
    }

    public void goToSleep(View view) {

        String currentTimeString = getDateString();

        // create an object that contains data for a document
        Map<String, Object> docContent = new HashMap<String, Object>();
        docContent.put("type", "Sleep");
        docContent.put("bedTime", currentTimeString);

        // display the data for the new document
        Log.d(TAG, "docContent=" + String.valueOf(docContent));

        id = dbHandler.createDocument(docContent);
        putInTextView("Tracking sleep with id: " + id);

    }

    private void putInTextView(String value) {
        TextView s = (TextView) findViewById(R.id.textViewEntries);
        s.setText(value);
    }
    public void wakeUp(View v) {

        TextView s = (TextView) findViewById(R.id.textViewEntries);

        Map<String, Object> entry = dbHandler.getDocument(id);
        s.setText("");
        s.append(String.valueOf("Bed time: " + entry.get("bedTime")) + "\n");
        if(entry.containsKey("wakeupTimes")) {
            List<String> wakeups = (List) entry.get("wakeupTimes");
            for (int j = 0; j < wakeups.size(); j++) {
                s.append("\tWoke up: " + wakeups.get(j) + "\n");
            }
        }
        s.append("Wakeup time: " + getDateString() + "\n");
    }

    public void wakeUpNight(View v) {
        dbHandler.addWakeupEntry(id, getDateString());
        putInTextView("Woke up in the night at " + getDateString());
    }
}
