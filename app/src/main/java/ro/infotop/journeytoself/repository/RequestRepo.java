package ro.infotop.journeytoself.repository;

import java.util.ArrayList;
import java.util.List;

import ro.infotop.journeytoself.model.therapyModel.TherapyRequest;
import ro.infotop.journeytoself.utils.DateUtils;

public class RequestRepo extends DelayedRepository {

    private static List<TherapyRequest> mockRequests;

    static  {
        initMockData();
    }

    public RequestRepo(boolean simulateDelay) {
        super(simulateDelay);
    }

    private static void initMockData() {
        mockRequests = new ArrayList<>();

        mockRequests.add(new TherapyRequest(DateUtils.parseStringDateAndTime("06.03.2019-13:43"), 0, 6, TherapyRequest.Status.ACCEPTED));
        mockRequests.add(new TherapyRequest(DateUtils.parseStringDateAndTime("05.03.2019-13:43"), 1, 6, TherapyRequest.Status.WAITING));
//        mockRequests.add(new TherapyRequest(DateUtils.parseStringDateAndTime("03.03.2019-13:43"), 15, 6, TherapyRequest.Status.ACCEPTED));
        mockRequests.add(new TherapyRequest(DateUtils.parseStringDateAndTime("02.03.2019-13:43"), 3, 8, TherapyRequest.Status.WAITING));
//        mockRequests.add(new TherapyRequest(DateUtils.parseStringDateAndTime("01.03.2019-13:43"), 14, 6, TherapyRequest.Status.WAITING));
        mockRequests.add(new TherapyRequest(DateUtils.parseStringDateAndTime("26.02.2019-13:43"), 5, 9, TherapyRequest.Status.WAITING));
//        mockRequests.add(new TherapyRequest(DateUtils.parseStringDateAndTime("16.02.2019-13:43"), 6, 8, TherapyRequest.Status.WAITING));
//        mockRequests.add(new TherapyRequest(DateUtils.parseStringDateAndTime("10.02.2019-13:43"), 7, 8, TherapyRequest.Status.WAITING));
//        mockRequests.add(new TherapyRequest(DateUtils.parseStringDateAndTime("06.02.2019-13:43"), 8, 6, TherapyRequest.Status.WAITING));
//        mockRequests.add(new TherapyRequest(DateUtils.parseStringDateAndTime("03.02.2019-13:43"), 9, 6, TherapyRequest.Status.WAITING));
//        mockRequests.add(new TherapyRequest(DateUtils.parseStringDateAndTime("31.01.2019-13:43"), 10, 6, TherapyRequest.Status.WAITING));
//        mockRequests.add(new TherapyRequest(DateUtils.parseStringDateAndTime("30.01.2019-13:43"), 11, 6, TherapyRequest.Status.EXPIRED));
//        mockRequests.add(new TherapyRequest(DateUtils.parseStringDateAndTime("21.01.2019-13:43"), 12, 6, TherapyRequest.Status.EXPIRED));
//        mockRequests.add(new TherapyRequest(DateUtils.parseStringDateAndTime("20.01.2019-13:43"), 13, 6, TherapyRequest.Status.EXPIRED));
//        mockRequests.add(new TherapyRequest(DateUtils.parseStringDateAndTime("16.01.2019-13:43"), 14, 6, TherapyRequest.Status.EXPIRED));
    }

    public void sendRequest(TherapyRequest tr) {
        if (simulateDelay) sleep(DEFAULT_LATENCY);

        if (mockRequests.contains(tr)) {
            //TODO: throw an exception
            // a patient shouldn't have more than one request
            return;
        }
        mockRequests.add(tr);
    }


    public void deleteRequest(int patientId, int therapistId) {
        TherapyRequest t = findSingleRequest(patientId, therapistId);
        mockRequests.remove(t);
    }

    public TherapyRequest findSingleRequest(int patientId, int therapistId) {
        if (simulateDelay) sleep(DEFAULT_LATENCY);

        for (TherapyRequest t : mockRequests) {
            if (t.getPatientId() == patientId && t.getTherapistId() == therapistId) {
                return t;
            }
        }
        return null;
    }

    public void updateRequestStatus(int patientId, int therapistId, TherapyRequest.Status status) {
        if (simulateDelay) sleep(DEFAULT_LATENCY);

        TherapyRequest t = findSingleRequest(patientId, therapistId);
        if (t != null) {
            t.setStatus(status);
        }
    }

    public TherapyRequest findRequestOfPatient(int patientId) {
        if (simulateDelay) sleep(DEFAULT_LATENCY);

        for (TherapyRequest t : mockRequests) {
            if (t.getPatientId() == patientId) {
                return t;
            }
        }
        return null;
    }

    public List<TherapyRequest> findRequestForTherapist(int therapistId) {
        List<TherapyRequest> requests = new ArrayList<>();

        for (TherapyRequest t : mockRequests) {
            if (t.getTherapistId() == therapistId) {
                requests.add(t);
            }
        }

        return requests;
    }
}
