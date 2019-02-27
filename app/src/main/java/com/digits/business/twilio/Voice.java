package com.digits.business.twilio;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class Voice extends Service {
    public Voice() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
