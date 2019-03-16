package ro.infotop.journeytoself.service;

import android.annotation.SuppressLint;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import ro.infotop.journeytoself.model.notificationModel.Notification;
import ro.infotop.journeytoself.model.userModel.User;

public final class NotificationCenter {
    @SuppressLint("UseSparseArrays")
    private static Map<Integer, Deque<Notification>> notificationDb = new HashMap<>();

    private NotificationCenter() {
    }

    public static void pushNotification(User user, Notification notification) {
        pushNotification(user.getId(), notification);
    }

    public static void pushNotification(int userId, Notification notification) {
        if (!notificationDb.containsKey(userId)) {
            notificationDb.put(userId, new ArrayDeque<>());
        }

        notificationDb.get(userId).push(notification);
    }

    public static Deque<Notification> retrieveNotifications(User user) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (notificationDb.get(user.getId()) != null) {
            Deque<Notification> notifs = notificationDb.get(user.getId());
            notificationDb.put(user.getId(), new ArrayDeque<>());
            return notifs;
        }
        return new ArrayDeque<>();
    }

}