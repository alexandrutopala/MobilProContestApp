package ro.infotop.journeytoself.model.therapyModel;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class TherapyRequest implements Serializable {
    public static enum Status {
        ACCEPTED, REJECTED, EXPIRED, WAITING;

        @Override
        public String toString() {
            switch (this) {
                case EXPIRED: return "expirata";
                case WAITING: return "in asteptare";
                case ACCEPTED: return "acceptata";
                case REJECTED: return "refuzata";
            }
            return null;
        }
    }

    private Date timeStamp;
    private int patientId;
    private int therapistId;
    private Status status;

    public TherapyRequest(Date timeStamp, int patientId, int therapistId, Status status) {
        this.timeStamp = timeStamp;
        this.patientId = patientId;
        this.therapistId = therapistId;
        this.status = status;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public int getPatientId() {
        return patientId;
    }

    public int getTherapistId() {
        return therapistId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TherapyRequest)) return false;
        TherapyRequest that = (TherapyRequest) o;
        return patientId == that.patientId &&
                therapistId == that.therapistId &&
                Objects.equals(timeStamp, that.timeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeStamp, patientId, therapistId);
    }
}
