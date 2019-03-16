package ro.infotop.journeytoself.model.userModel;

import android.util.Log;

import java.util.List;

import ro.infotop.journeytoself.commons.Disease;

public class Patient extends User {
    private static final String TAG = Patient.class.getSimpleName();
    private List<Disease> diseases;
    private Therapist therapist;
    private long matcherCode;

    public Patient() {
    }

    public Patient(int id, String name, String username, String email, String country, String city) {
        super(id, name, username, email, country, city);
    }

    public Patient(int id, String name, String username, String email, List<Disease> diseases, String country, String city) {
        super(id, name, username, email, country, city);
        this.diseases = diseases;
        calculateMatcherCode(diseases);
    }

    public Patient(int id, String name, String username, String email, List<Disease> diseases, Therapist therapist, String country, String city) {
        super(id, name, username, email, country, city);
        this.diseases = diseases;
        this.therapist = therapist;
        calculateMatcherCode(diseases);
    }

    public List<Disease> getDiseases() {
        return diseases;
    }

    public void setDiseases(List<Disease> diseases) {
        this.diseases = diseases;
        calculateMatcherCode(diseases);
    }

    public Therapist getTherapist() {
        return therapist;
    }

    public void setTherapist(Therapist therapist) {
        this.therapist = therapist;
    }

    public long getMatcherCode() {
        return matcherCode;
    }

    private void calculateMatcherCode(List<Disease> diseases) {
        if (diseases == null) return;
        matcherCode = Disease.generateMatchingCode(diseases);
        //TODO: remove this
        Log.i(TAG, getUsername() + " : " + matcherCode);
    }
}
