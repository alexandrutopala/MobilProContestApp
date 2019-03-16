package ro.infotop.journeytoself.model.notificationModel;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

import ro.infotop.journeytoself.utils.DateUtils;

public class Notification implements Serializable, Parcelable {
    private Date when;
    private String message;
    private String pictureUri;

    public Notification(Date when, String message, String pictureUri) {
        this.when = when;
        this.message = message;
        this.pictureUri = pictureUri;
    }

    public Notification(Date when, String message) {
        this.when = when;
        this.message = message;
    }


    protected Notification(Parcel in) {
        when = DateUtils.parseStringDateAndTime(in.readString());
        message = in.readString();
        pictureUri = in.readString();
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    public Date getWhen() {
        return when;
    }

    public void setWhen(Date when) {
        this.when = when;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPictureUri() {
        return pictureUri;
    }

    public void setPictureUri(String pictureUri) {
        this.pictureUri = pictureUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(DateUtils.parseDateAndTime(when));
        dest.writeString(message);
        dest.writeString(pictureUri);
    }
}
