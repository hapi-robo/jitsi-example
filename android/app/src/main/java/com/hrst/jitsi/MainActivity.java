package com.hrst.jitsi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceived(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize server URL
        URL serverURL;
        try {
            serverURL = new URL("https://meet.jit.si"); // Default
//            serverURL = new URL("https://meet.mayfirst.org");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid server URL!");
        }

        // Initialize default options for Jitsi Meet conferences.
        JitsiMeetConferenceOptions defaultOptions
                = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                // When using JaaS, set the obtained JWT here
                //.setToken("MyJWT")
                // Different features flags can be set
                // https://jitsi.github.io/handbook/docs/dev-guide/mobile-feature-flags
                // https://github.com/jitsi/jitsi-meet/blob/master/react/features/base/flags/constants.js
                .setFeatureFlag("add-people.enabled", false)
                .setFeatureFlag("audio-mute.enabled", false)
                .setFeatureFlag("calendar.enabled", false)
                .setFeatureFlag("close-captions.enabled", false)
                .setFeatureFlag("conference-timer.enabled", false)
                .setFeatureFlag("chat.enabled", false)
                .setFeatureFlag("filmstrip.enabled", false)
                .setFeatureFlag("invite.enabled", false)
                .setFeatureFlag("kick-out.enabled", false)
                .setFeatureFlag("live-streaming.enabled", false)
                .setFeatureFlag("meeting-name.enabled", false)
                .setFeatureFlag("meeting-password.enabled", false)
                .setFeatureFlag("notifications.enabled", false)
                .setFeatureFlag("overflow-menu.enabled", false)
                .setFeatureFlag("pip.enabled", false)
                .setFeatureFlag("raise-hand.enabled", false)
                .setFeatureFlag("recording.enabled", false)
                .setFeatureFlag("server-url-change.enabled", false)
                .setFeatureFlag("title-view.enabled", false)
//                .setFeatureFlag("toolbox.alwaysVisible", false)
//                .setFeatureFlag("toolbox.enabled", false)
                .setFeatureFlag("video-share.enabled", false)
                .setFeatureFlag("welcomepage.enabled", false)
                .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);

        // Register broadcast messages
        registerBroadcastMessages();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    public void onButtonClick(View v) {
        String roomId = "4164126087";

        if (roomId.length() > 0) {
            // Build options object for joining the conference. The SDK will merge the default
            // one we set earlier and this one when joining.
            JitsiMeetConferenceOptions options
                    = new JitsiMeetConferenceOptions.Builder()
                    .setRoom(roomId)
                    .build();

            // Launch the new activity with the given options. The launch() method takes care
            // of creating the required Intent and passing the options.
            JitsiMeetActivity.launch(this, options);
        }
    }

    private void registerBroadcastMessages() {
        IntentFilter intentFilter = new IntentFilter();

        /* This registers for every possible event sent from JitsiMeetSDK
           If only some of the events are needed, the for loop can be replaced
           with individual statements:
           ex:  intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.getAction());
                intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.getAction());
                ... other events
         */
        for (BroadcastEvent.Type type : BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.getAction());
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    private void onBroadcastReceived(Intent intent) {
        if (intent != null) {
            BroadcastEvent event = new BroadcastEvent(intent);

            // https://jitsi.github.io/handbook/docs/dev-guide/dev-guide-android-sdk#listening-for-broadcasted-events
            switch (event.getType()) {
                case CONFERENCE_JOINED:
                    Log.i(TAG, "Conference Joined with: " + event.getData().get("url"));
                    break;
                case PARTICIPANT_JOINED:
                    Log.i(TAG, "Participant joined: " + event.getData().get("name"));
                    break;
                case CONFERENCE_TERMINATED:
                    Log.i(TAG, "Conference terminated");
                    break;
            }
        }
    }

    private void hangUp() {
        Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(hangupBroadcastIntent);
    }
}