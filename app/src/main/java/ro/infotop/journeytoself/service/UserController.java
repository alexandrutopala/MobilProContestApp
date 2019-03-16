package ro.infotop.journeytoself.service;

import java.util.List;

import ro.infotop.journeytoself.R;
import ro.infotop.journeytoself.exceptions.auth.AuthenticationException;
import ro.infotop.journeytoself.exceptions.auth.EmailInUseException;
import ro.infotop.journeytoself.exceptions.auth.UsernameInUseException;
import ro.infotop.journeytoself.model.userModel.Patient;
import ro.infotop.journeytoself.model.userModel.Therapist;
import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.repository.UserRepo;

public class UserController {
    private static final UserController ourInstance = new UserController();
    private UserRepo userRepo;
    private User loggedUser;

    public static UserController getInstance() {
        return ourInstance;
    }

    private UserController() {
        userRepo = new UserRepo(true);
    }

    public List<Patient> findPatientsByTherapist(Therapist therapist) {
        return userRepo.findPatientsByTherapist(therapist);
    }

    public User loginUser(String usernameOrEmail, String password) {
        User user = userRepo.findByUsernameOrEmail(usernameOrEmail);

        if (user != null && userRepo.testPassword(user, password)) {
            return user;
        }

        throw new AuthenticationException(
                ResourceController.getInstance().getResource().getString(R.string.error_incorrect_credentials)
        );
    }

    public void checkEmailInUse(String email) throws EmailInUseException {
        userRepo.checkEmailInUse(email);
    }

    public void checkUsernameInUse(String username) throws UsernameInUseException {
        userRepo.checkUsernameInUse(username);
    }

    public User registerUser(User user, String pass) throws EmailInUseException, UsernameInUseException {
        userRepo.checkEmailInUse(user.getEmail());
        userRepo.checkUsernameInUse(user.getUsername());
        return userRepo.createUser(user, pass);
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    public List<Therapist> findTherapistsForPatient(Patient user) {
        return userRepo.findTherapistsForPatient(user);
    }

    public void updatePatientsTherapist(Patient patient, Therapist therapist) {
        userRepo.updatePatientsTherapist(patient, therapist);
    }

    public User findUserById(int id) {
        return userRepo.findUserById(id);
    }
}
