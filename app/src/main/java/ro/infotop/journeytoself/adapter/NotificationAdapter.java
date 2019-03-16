package ro.infotop.journeytoself.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import ro.infotop.journeytoself.R;
import ro.infotop.journeytoself.model.notificationModel.Notification;
import ro.infotop.journeytoself.utils.DateUtils;
import ro.infotop.journeytoself.utils.NewsApiUtils;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {

    private Context context;
    private List<Notification> notifications;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    public void addAll(List<Notification> notifs) {
        notifications.addAll(0, notifs);
    }

    public void clear() {
        notifications.clear();
    }

    public void remove(int index) {
        notifications.remove(index);
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, viewGroup, false);
        return new NotificationHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder notificationHolder, int i) {
        Notification notification = notifications.get(i);
        notificationHolder.when.setText(DateUtils.parseDateAndTime(notification.getWhen()));
        notificationHolder.what.setText(notification.getMessage());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(NewsApiUtils.getDrawableColor(i));
        requestOptions.error(R.drawable.ic_user);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        Glide.with(context)
                .load(notification.getPictureUri())
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(notificationHolder.picture);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public ArrayList<Notification> getNotificationArrayList() {
        return new ArrayList<>(notifications);
    }

    public static class NotificationHolder extends RecyclerView.ViewHolder {

        TextView when;
        TextView what;
        ImageView picture;
        public int index;

        public NotificationHolder(@NonNull View itemView, int index) {
            super(itemView);
            this.index = index;
            when = itemView.findViewById(R.id.when_textview);
            what = itemView.findViewById(R.id.what_textview);
            picture = itemView.findViewById(R.id.notification_imageview);
        }
    }
}
