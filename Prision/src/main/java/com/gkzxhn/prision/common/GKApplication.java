package com.gkzxhn.prision.common;

import android.app.Application;

/**
 * Created by Raleigh.Luo on 17/4/10.
 */

public class GKApplication extends Application{
    private static GKApplication application;
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }
    public static GKApplication getInstance() {
        return application;
    }
}
