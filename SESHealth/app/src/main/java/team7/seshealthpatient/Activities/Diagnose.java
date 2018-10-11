package team7.seshealthpatient.Activities;

import java.util.Date;

public class Diagnose {

    private String description, symptoms;
    private long time;

    public Diagnose(String description, String symptoms) {
        this.description = description;
        this.symptoms = symptoms;
        this.time = new Date().getTime();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
