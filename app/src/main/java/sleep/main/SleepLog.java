package sleep.main;


public class SleepLog {

    private String bedTime;
    private String wakeUpTime;

    public SleepLog(String bedTime, String wakeUpTime) {
        this.bedTime = bedTime;
        this.wakeUpTime = wakeUpTime;
    }

    public String getBedTime() {
        return bedTime;
    }

    public String getWakeUpTime() {
        return wakeUpTime;
    }
}
