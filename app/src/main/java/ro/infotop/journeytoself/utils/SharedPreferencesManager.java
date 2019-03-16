package ro.infotop.journeytoself.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ro.infotop.journeytoself.commons.Disease;
import ro.infotop.journeytoself.exceptions.InvalidKindOfUserException;
import ro.infotop.journeytoself.model.userModel.Patient;
import ro.infotop.journeytoself.model.userModel.Therapist;
import ro.infotop.journeytoself.model.userModel.User;

public final class SharedPreferencesManager {
    private final static String CREDENTIALS_PREFERENCES_BUNDLE = "credentialsBundle";
    private final static String SEPARATOR = "!";

    public final static String REMEMBER_CREDENTIALS_PREFERENCE = "rememberCredentials";
    public final static String REQUEST_AUTHENTICATION_PREFERENCE = "requestPasswordOrFingerprint";
    public final static String LAST_SUCCESSFUL_LOGGED_USER = "loggedUsername"; // kept as JSON
    public final static String USER_KIND = "usersKind";
    public final static String USER_PASSWORD = "userpassword";
    public final static String USER_DISEASES = "userDiseases";

    private static final class Kind {
        public static final int INVALID = -1;
        public static final int PATIENT = 0;
        public static final int THERAPIST = 1;
    }

    private static Boolean rememberCredentials;
    private static Boolean requestAuthentication;
    private static User loggedUser;

    private SharedPreferencesManager() {
    }

    public static void setRememberCredentials(@NotNull Context context, boolean rememberCredentials) {
        SharedPreferencesManager.rememberCredentials = rememberCredentials;

        SharedPreferences preferences = context.getSharedPreferences(CREDENTIALS_PREFERENCES_BUNDLE, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = preferences.edit();

        preferencesEditor.putBoolean(REMEMBER_CREDENTIALS_PREFERENCE, rememberCredentials);
        preferencesEditor.apply();
    }

    public static boolean isRememberingCredentials(Context context) {
        if (rememberCredentials != null) {
            return rememberCredentials;
        }

        SharedPreferences preferences = context.getSharedPreferences(CREDENTIALS_PREFERENCES_BUNDLE, Context.MODE_PRIVATE);
        rememberCredentials = preferences.getBoolean(REMEMBER_CREDENTIALS_PREFERENCE, false);
        return rememberCredentials;
    }

    public static void setRequestAuthentication(@NotNull Context context, boolean request) {
        requestAuthentication = request;

        SharedPreferences preferences = context.getSharedPreferences(CREDENTIALS_PREFERENCES_BUNDLE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(REQUEST_AUTHENTICATION_PREFERENCE, request);

        editor.apply();
    }

    public static boolean isRequestingAuthentication(Context context) {
        if (requestAuthentication != null) {
            return requestAuthentication;
        }

        SharedPreferences preferences = context.getSharedPreferences(CREDENTIALS_PREFERENCES_BUNDLE, Context.MODE_PRIVATE);
        requestAuthentication = preferences.getBoolean(REQUEST_AUTHENTICATION_PREFERENCE, false);
        return requestAuthentication;
    }

    public static void setLoggedUser(@NotNull Context context, User user, String pass) throws IOException {
        loggedUser = user;

        SharedPreferences preferences = context.getSharedPreferences(CREDENTIALS_PREFERENCES_BUNDLE, Context.MODE_PRIVATE);

        ObjectMapper mapper = new ObjectMapper();
        String userJson = mapper.writeValueAsString(user);
        int userKind;
        List<Disease> diseases;

        if (user instanceof Patient) {
            userKind = Kind.PATIENT;
            diseases = ((Patient) user).getDiseases();
        } else if (user instanceof Therapist) {
            userKind = Kind.THERAPIST;
            diseases = ((Therapist) user).getSpecializations();
        } else {
            userKind = Kind.INVALID;
            diseases = null;
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LAST_SUCCESSFUL_LOGGED_USER, userJson);
        editor.putInt(USER_KIND, userKind);
        editor.putString(USER_PASSWORD, pass);
        editor.putString(USER_DISEASES, convertDiseasesListToString(diseases));

        editor.apply();
    }

    @Nullable
    public static User getLoggedUser(Context context) throws IOException {
        if (loggedUser != null) {
            return loggedUser;
        }

        SharedPreferences preferences = context.getSharedPreferences(CREDENTIALS_PREFERENCES_BUNDLE, Context.MODE_PRIVATE);

        String userJson = preferences.getString(LAST_SUCCESSFUL_LOGGED_USER, null);

        if (userJson == null) {
            return null;
        }

        int userKind = preferences.getInt(USER_KIND, Kind.INVALID);
        List<Disease> diseases = convertStringToDiseasesList(
                preferences.getString(USER_DISEASES, null)
        );

        if (userKind == Kind.INVALID) {
            throw new InvalidKindOfUserException();
        }

        ObjectMapper mapper = new ObjectMapper();
        if (userKind == Kind.PATIENT) {
            loggedUser = mapper.readValue(userJson, Patient.class);
            ((Patient) loggedUser).setDiseases(diseases);
        } else if (userKind == Kind.THERAPIST) {
            loggedUser = mapper.readValue(userJson, Therapist.class);
            ((Therapist) loggedUser).setSpecializations(diseases);
        }

        return loggedUser;
    }

    public static void removeAllUserPreferences(@NotNull Context context) {
        rememberCredentials = null;
        requestAuthentication = null;
        loggedUser = null;

        SharedPreferences preferences = context.getSharedPreferences(CREDENTIALS_PREFERENCES_BUNDLE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove(LAST_SUCCESSFUL_LOGGED_USER);
        editor.remove(USER_KIND);
        editor.remove(REMEMBER_CREDENTIALS_PREFERENCE);
        editor.remove(REQUEST_AUTHENTICATION_PREFERENCE);
        editor.remove(USER_PASSWORD);
        editor.remove(USER_DISEASES);

        editor.apply();
    }

    public static boolean locallyCheckUserCredentials(@NotNull Context context, @NotNull String pass) {
        SharedPreferences preferences = context.getSharedPreferences(CREDENTIALS_PREFERENCES_BUNDLE, Context.MODE_PRIVATE);

        String storedPass = preferences.getString(USER_PASSWORD, null);

        if (storedPass == null) {
            return false;
        }

        return storedPass.equals(pass);
    }

    private static String convertDiseasesListToString(@Nullable List<Disease> diseases) {
        if (diseases == null || diseases.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int n = diseases.size();

        for (int i = 0; i < n - 1; i++) {
            sb.append(diseases.get(i).ordinal()).append(SEPARATOR);
        }

        sb.append(diseases.get( n - 1 ).ordinal());
        return sb.toString();
    }

    @NotNull
    private static List<Disease> convertStringToDiseasesList(@Nullable String compressed) {
        if (compressed == null) {
            return new ArrayList<>();
        }

        List<Disease> list = new ArrayList<>();
        String [] tokens = compressed.split(SEPARATOR);
        Disease[] allDiseases = Disease.values();

        try {
            for (String token : tokens) {
                int index = Integer.parseInt(token);
                list.add(allDiseases[index]);
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return list;
    }
}
