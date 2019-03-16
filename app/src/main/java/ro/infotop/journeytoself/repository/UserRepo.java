package ro.infotop.journeytoself.repository;

import android.support.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.infotop.journeytoself.commons.Disease;
import ro.infotop.journeytoself.exceptions.auth.EmailInUseException;
import ro.infotop.journeytoself.exceptions.auth.UsernameInUseException;
import ro.infotop.journeytoself.model.therapyModel.TherapyRequest;
import ro.infotop.journeytoself.model.userModel.Patient;
import ro.infotop.journeytoself.model.userModel.Therapist;
import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.service.RequestController;
import ro.infotop.journeytoself.service.ResourceController;
import ro.infotop.journeytoself.utils.AlgorithmicUtils;
import ro.infotop.journeytoself.utils.MatchingManager;

public class UserRepo extends DelayedRepository{
    // mock data
    private static Map<Therapist, List<Patient>> db;
    private static List<Therapist> therapists;
    private static List<Patient> patients;
    private static int userIdGenerator = 0;

    static {
        db = new HashMap<>();
        therapists = new ArrayList<>();
        patients = new ArrayList<>();
        generateMockData();
    }

    public UserRepo() {
        super(false);
    }

    public UserRepo(boolean simulateDelay) {
        super(simulateDelay);
    }

    public List<Patient> findPatientsByTherapist(Therapist therapist) {
        if (simulateDelay) sleep(SHORT_LATENCY);
        return db.get(therapist);
    }

    public Therapist findTherapistById(int id) {
        for (Therapist t : therapists) {
            if (t.getId() == id) {
                return t;
            }
        }

        return null;
    }

    public User findByUsernameOrEmail(String usernameOrEmail) {
        // TODO: connect to real database and retrieve the corresponding user
        for (User t : therapists) {
            if (t.getEmail().equalsIgnoreCase(usernameOrEmail) || t.getUsername().equalsIgnoreCase(usernameOrEmail)) {
                return t;
            }
        }

        for (User p : patients) {
            if (p.getEmail().equalsIgnoreCase(usernameOrEmail) || p.getUsername().equalsIgnoreCase(usernameOrEmail)) {
                TherapyRequest tr = RequestController.getInstance(ResourceController.getInstance().getContext()).findRequestOfPatient((Patient) p);
                if (tr != null) {
                    Therapist t = findTherapistById(tr.getTherapistId());
                    ((Patient) p).setTherapist(t);
                }
                return p;
            }
        }

        return null;
    }

    public boolean testPassword(User user, String password) {
        if (simulateDelay) sleep(DEFAULT_LATENCY);
        // TODO: send this request to a rel server
        if (password.equals("123456")) {
            return true;
        }
        return false;
    }

    public void checkEmailInUse(String email) throws EmailInUseException {
        if (simulateDelay) sleep(SHORT_LATENCY);

        for (Therapist t : db.keySet()) {
            if (t.getEmail().equalsIgnoreCase(email)) {
                throw new EmailInUseException("Email in use");
            }

            for (Patient p : db.get(t)) {
                if (p.getEmail().equalsIgnoreCase(email)) {
                    throw new EmailInUseException("Email in use");
                }
            }
        }
    }

    public void checkUsernameInUse(String username) throws UsernameInUseException {
        if (simulateDelay) sleep(DEFAULT_LATENCY);

        for (Therapist t : db.keySet()) {
            if (t.getUsername().equalsIgnoreCase(username)) {
                throw new UsernameInUseException("username is use");
            }

            for (Patient p : db.get(t)) {
                if (p.getUsername().equalsIgnoreCase(username)) {
                    throw new UsernameInUseException("username is use");
                }
            }
        }
    }

    public User createUser(User user, String pass) throws UsernameInUseException, EmailInUseException {
        if (simulateDelay) sleep(DEFAULT_LATENCY);

        simulateDelay = false;
        try {
            checkEmailInUse(user.getEmail());
            checkUsernameInUse(user.getUsername());
        } catch (EmailInUseException | UsernameInUseException e) {
            throw e;
        } finally {
            simulateDelay = true;
        }

        user.setId(userIdGenerator++);

        if (user instanceof Patient) {
            patients.add((Patient) user);
        } else if (user instanceof Therapist) {
            therapists.add((Therapist) user);
        }

        return user;
    }

    private static void generateMockData() {
        Patient p1 = new Patient(userIdGenerator++, "Ion Popescu", "iopop","ion@gmail.com", Arrays.asList(Disease.DEPRESSION, Disease.FAMILY_PROBLEMS), "Romania", "Bucuresti");
        Patient p2 = new Patient(userIdGenerator++, "Gigel Enescu", "gig.ene","gigel91@gmail.com", IllnessesRepo.generateRandomListOfIllnesses(), "Anglia", "Stone Henge");
        Patient p3 = new Patient(userIdGenerator++, "Dorel Parizianu", "parisparis","doreldoru@gmail.com", IllnessesRepo.generateRandomListOfIllnesses(), "Romania", "Iasi");
        Patient p4 = new Patient(userIdGenerator++, "Andrei Enachescu", "andrew","andrei.ena97@yahoo.com", IllnessesRepo.generateRandomListOfIllnesses(), "Germania", "Frankfurt");
        Patient p5 = new Patient(userIdGenerator++, "Matei Gavrilescu", "matrei.gavril","matei.gavril@hotmail.com", IllnessesRepo.generateRandomListOfIllnesses(), "Olanda", "Roterdam");
        Patient p6 = new Patient(userIdGenerator++, "Ionut Alexandrescu", "alex.ion","ion98@gmail.com", IllnessesRepo.generateRandomListOfIllnesses(), "Romania", "Bucurest");

        Therapist t1 = new Therapist(userIdGenerator++, "Florina Smaranda", "flori", "flori.smd@yahoo.com",
                "Atenta si grijulie", "Calea Grivitei, nr. 4",  Arrays.asList(Disease.DEPRESSION, Disease.FAMILY_PROBLEMS, Disease.COUPLE_PROBLEMS), "Romania", "Bucuresti");
        Therapist t2 = new Therapist(userIdGenerator++, "Andrei Samadau", "andy", "andy.sa.ma.dau@yahoo.com",
                "Atenta si grijulie", "Bd. 1 Mai, nr. 41", IllnessesRepo.generateRandomListOfIllnesses(), "Iasi", "Bucuresti");
        Therapist t3 = new Therapist(userIdGenerator++, "Maria Negrescu", "mary", "maria.mari92@yahoo.com",
                "Atenta si grijulie", "Drumul Taberei, nr. 12", IllnessesRepo.generateRandomListOfIllnesses(), "Romania", "Bucuresti");
        Therapist t4 = new Therapist(userIdGenerator++, "George Bucur", "george.bucur", "george.bucu@yahoo.com",
                "Atenta si grijulie", "Calea Grivitei, nr. 23", IllnessesRepo.generateRandomListOfIllnesses(), "Romania", "Bucuresti");

        patients.add(p1); patients.add(p2); patients.add(p3); patients.add(p4); patients.add(p5); patients.add(p6);
        therapists.add(t1); therapists.add(t2); therapists.add(t3); therapists.add(t4);

        db.put(t1, new ArrayList<>(Arrays.asList(p1, p2)));
        db.put(t3, new ArrayList<>(Arrays.asList(p4)));
        db.put(t4, new ArrayList<>(Arrays.asList(p6)));
    }

    public List<Therapist> findTherapistsForPatient(Patient user) {
        if (simulateDelay) sleep(DEFAULT_LATENCY);

        List<Therapist> chosen = new ArrayList<>();

        for (Therapist t : therapists) {
            if (t.getCity().equalsIgnoreCase(user.getCity()) && t.getCountry().equalsIgnoreCase(user.getCountry())) {
                if (MatchingManager.codesAreMatching(user.getMatcherCode(), t.getMatcherCode())) {
                    chosen.add((Therapist) t.clone());
                }
            }
        }
        return chosen;
    }

    public void updatePatientsTherapist(@NotNull Patient patient, @Nullable Therapist therapist) {
        if (simulateDelay) sleep(DEFAULT_LATENCY);

        int index = patients.indexOf(patient);
        Patient p = patients.get(index);
        Therapist prevTherapist = p.getTherapist();

        if (prevTherapist != null && db.get(prevTherapist) != null) {
            db.get(prevTherapist).remove(p);
        }

        if (therapist != null) {
            if (db.containsKey(therapist)) {
                List<Patient> patients = db.get(therapist);
                if (patients == null) {
                    patients = new ArrayList<>();
                }
                patients.add(p);
            } else {
                db.put(therapist, new ArrayList<>(Arrays.asList(p)));
            }
        }

        patient.setTherapist(therapist);
        p.setTherapist(therapist);

    }

    public User findUserById(int id) {
        for (Therapist t : therapists) {
            if (t.getId() == id) {
                return t;
            }
        }

        for (Patient p : patients) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }
}
