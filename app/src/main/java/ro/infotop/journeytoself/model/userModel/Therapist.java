package ro.infotop.journeytoself.model.userModel;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ro.infotop.journeytoself.commons.Disease;

public class Therapist extends User implements Cloneable {
    private String description;
    private String cabinetAddress;
    private List<Disease> specializations;
    @Nullable
    private List<Patient> patients;
    private long matcherCode;
    /*
        Cand un terapeut va intra in modul de "Vizualizare profil pacient", va avea
        nevoie de instanta pacientului pt a-i debloca informatiile
     */
    private Patient keyPatient;

    public Therapist() {
    }

    public Therapist(int id, String name, String username, String email, String country, String city) {
        super(id, name, username, email, country, city);
    }

    public Therapist(int id, String name, String username, String email, List<Disease> specializations, String country, String city) {
        super(id, name, username, email, country, city);
        this.specializations = specializations;
        calculateMatcherCode(specializations);
    }

    public Therapist(int id, String name, String username, String email, String description, String cabinetAddress, List<Disease> specializations, String country, String city) {
        super(id, name, username, email, country, city);
        this.description = description;
        this.cabinetAddress = cabinetAddress;
        this.specializations = specializations;
        calculateMatcherCode(specializations);
    }

    public Therapist(int id, String name, String username, String email, String description, String cabinetAddress, List<Disease> specializations, @Nullable List<Patient> patients, String country, String city) {
        super(id, name, username, email, country, city);
        this.description = description;
        this.cabinetAddress = cabinetAddress;
        this.specializations = specializations;
        this.patients = patients;
        calculateMatcherCode(specializations);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCabinetAddress() {
        return cabinetAddress;
    }

    public void setCabinetAddress(String cabinetAddress) {
        this.cabinetAddress = cabinetAddress;
    }

    public List<Disease> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(List<Disease> specializations) {
        this.specializations = specializations;
        calculateMatcherCode(specializations);
    }

    @Nullable
    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(@Nullable List<Patient> patients) {
        this.patients = patients;
    }

    public Patient getKeyPatient() {
        return keyPatient;
    }

    public void setKeyPatient(Patient keyPatient) {
        this.keyPatient = keyPatient;
    }

    private void calculateMatcherCode(List<Disease> diseases) {
        if (diseases == null) return;
        matcherCode = Disease.generateMatchingCode(diseases);
    }

    public long getMatcherCode() {
        return matcherCode;
    }

    @Override
    public Object clone() {
        Therapist t = new Therapist(getId(), getName(), getUsername(), getEmail(),
                getDescription(), getCabinetAddress(),
                new ArrayList<>(getSpecializations()),
                getCountry(), getCity()
        );
        return t;
    }
}
