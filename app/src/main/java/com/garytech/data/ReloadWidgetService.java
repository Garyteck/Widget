package com.garytech.data;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 * This service will be triggered by clicking on the errow view of the widget
 * It  will trigger an RELOAD_WIDGET_ACTION event
 */
public class ReloadWidgetService extends IntentService {

    public static final String RELOAD_WIDGET_ACTION = "com.garytech.data.RELOAD_WIDGET_ACTION";
    public static final String RELOAD_WIDGET_IDS_EXTRA = "RELOAD_WIDGET_IDS_EXTRA";

    public ReloadWidgetService() {
        super("ReloadWidgetService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.w("onHandleIntent", "broadcast" + intent.getAction());
        Intent broadcast = new Intent();
        broadcast.setAction(intent.getAction());
        broadcast.putExtra(RELOAD_WIDGET_IDS_EXTRA, intent.getIntArrayExtra(RELOAD_WIDGET_IDS_EXTRA));
        getBaseContext().sendBroadcast(broadcast);
    }


}
