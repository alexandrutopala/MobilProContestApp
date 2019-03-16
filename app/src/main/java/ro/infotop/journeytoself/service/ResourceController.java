package ro.infotop.journeytoself.service;

import android.content.Context;
import android.content.res.Resources;

public class ResourceController {
    private static ResourceController ourInstance;
    private Resources resource;
    private Context context;

    public static ResourceController getInstance() {
        if (ourInstance == null) {
            synchronized (ResourceController.class) {
                if (ourInstance == null) {
                    ourInstance = new ResourceController();
                }
            }
        }
        return ourInstance;
    }

    private ResourceController() {
    }

    public Resources getResource() {
        return resource;
    }

    public void setResource(Resources resource) {
        this.resource = resource;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
