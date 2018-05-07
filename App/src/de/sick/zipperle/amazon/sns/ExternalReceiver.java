package de.sick.zipperle.amazon.sns;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import de.sick.zipperle.amazon.sns.datastructure.GCMMessage;

public class ExternalReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if(intent!=null){
            Bundle extras = intent.getExtras();
            GCMMessage m = MessageReceivingService.parseNotification(extras, context);


            MessageReceivingService.notifyUser(m);
        }
    }
}

