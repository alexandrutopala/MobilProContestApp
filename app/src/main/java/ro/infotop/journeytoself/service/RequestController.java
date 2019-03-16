package ro.infotop.journeytoself.service;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;

import ro.infotop.journeytoself.R;
import ro.infotop.journeytoself.model.notificationModel.Notification;
import ro.infotop.journeytoself.model.therapyModel.TherapyRequest;
import ro.infotop.journeytoself.model.userModel.Patient;
import ro.infotop.journeytoself.model.userModel.Therapist;
import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.repository.RequestRepo;

public class RequestController {
    private static final RequestController ourInstance = new RequestController();
    private static Context context;

    public static RequestController getInstance(@NotNull  Context context) {
        RequestController.context = context;
        return ourInstance;
    }

    private RequestRepo requestRepo;

    private RequestController() {
        requestRepo = new RequestRepo(true);
    }

    public void sendRequest(User patient, User therapist) {
        TherapyRequest tr = new TherapyRequest(
                Calendar.getInstance().getTime(),
                patient.getId(),
                therapist.getId(),
                TherapyRequest.Status.WAITING
        );

        requestRepo.sendRequest(tr);

        NotificationCenter.pushNotification(
                therapist.getId(),
                new Notification(
                        Calendar.getInstance().getTime(),
                        context.getString(R.string.notification_request_received_from) + " " +  patient.getUsername()
                )
        );
    }

    public void cancelRequest(Patient patient, Therapist therapist, boolean canceledByPatient) {
        requestRepo.deleteRequest(patient.getId(), therapist.getId());

        Notification notification;
        if (canceledByPatient) {
            notification = new Notification(
                    Calendar.getInstance().getTime(),
                    context.getString(R.string.notification_request_cancelled_by) + " " + patient.getUsername()
                    );

            NotificationCenter.pushNotification(therapist, notification);
        } else {
            notification = new Notification(
                    Calendar.getInstance().getTime(),
                    context.getString(R.string.notification_request_cancelled_by) + " " + therapist.getUsername()
            );
            NotificationCenter.pushNotification(patient, notification);
        }

    }

    public void updateRequestStatus(Patient patient, Therapist therapist, TherapyRequest.Status status, boolean updatedByTherapist) {
        requestRepo.updateRequestStatus(patient.getId(), therapist.getId(), status);

        Notification notification;
        if (updatedByTherapist) {
            notification = new Notification(
                    Calendar.getInstance().getTime(),
                    therapist.getUsername() + ": " + context.getString(R.string.notification_request_update) + " " + status
            );
            NotificationCenter.pushNotification(patient, notification);
        } else {
            notification = new Notification(
                    Calendar.getInstance().getTime(),
                    patient.getUsername() + ": " + context.getString(R.string.notification_request_update) + " " + status
            );
            NotificationCenter.pushNotification(therapist, notification);
        }
    }

    public TherapyRequest findSingleRequest(Patient patient, Therapist therapist) {
        return requestRepo.findSingleRequest(patient.getId(), therapist.getId());
    }

    public TherapyRequest findRequestOfPatient(Patient patient) {
        return requestRepo.findRequestOfPatient(patient.getId());
    }

    public List<TherapyRequest> findRequestForTherapist(Therapist therapist) {
        return requestRepo.findRequestForTherapist(therapist.getId());
    }
}
