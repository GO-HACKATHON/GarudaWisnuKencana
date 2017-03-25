package com.gwk.movesenseexample.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.awareness.fence.FenceState;
import com.gwk.movesenseexample.FencesActivity;

/**
 * Created by Michinggun on 3/25/2017.
 */

public class MyFenceReceiver extends BroadcastReceiver {
    private static final String TAG = "FenceReceiver";
    private OnFenceDetectedListener listener;

    public void setFenceListener(OnFenceDetectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState fenceState = FenceState.extract(intent);

        switch (fenceState.getFenceKey()) {
            case FencesActivity.HEADPHONE_FENCE:
                if (listener != null) {
                    switch (fenceState.getCurrentState()) {
                        case FenceState.TRUE:
                            listener.onHeadphoneStateChanged(true, "Headphones are plugged in.");
                            break;
                        case FenceState.FALSE:
                            listener.onHeadphoneStateChanged(false, "Headphones are NOT plugged in.");
                            break;
                        case FenceState.UNKNOWN:
                            listener.onHeadphoneStateChanged(false, "The headphone fence is in an unknown state.");
                            break;
                    }
                }
                break;
            case FencesActivity.TIME_FENCE:
                if (listener != null) {
                    switch (fenceState.getCurrentState()) {
                        case FenceState.TRUE:
                            listener.onTimeStateChanged(true, "Current Time is Evening");
                            break;
                        case FenceState.FALSE:
                            listener.onTimeStateChanged(false, "Current Time is NOT Evening");
                            break;
                        case FenceState.UNKNOWN:
                            listener.onTimeStateChanged(false, "The Time fence is in an unknown state.");
                            break;
                    }
                }
                break;
            case FencesActivity.CURRENT_TIME_FENCE:
                if (listener != null) {
                    switch (fenceState.getCurrentState()) {
                        case FenceState.TRUE:
                            listener.onCurrentTimeStateChanged(true, "Current Time is NOW");
                            break;
                        case FenceState.FALSE:
                            listener.onCurrentTimeStateChanged(false, "Current Time is NOT now");
                            break;
                        case FenceState.UNKNOWN:
                            listener.onCurrentTimeStateChanged(false, "The Current Time fence is in an unknown state.");
                            break;
                    }
                }
                break;
            case FencesActivity.MOVE_FENCE:
                if (listener != null) {
                    switch (fenceState.getCurrentState()) {
                        case FenceState.TRUE:
                            listener.onMoveStateChanged(true, "You're walking");
                            break;
                        case FenceState.FALSE:
                            listener.onMoveStateChanged(false, "You're not walking");
                            break;
                        case FenceState.UNKNOWN:
                            listener.onMoveStateChanged(false, "You're in an unknown state.");
                            break;
                    }
                }
                break;
        }
    }

    public interface OnFenceDetectedListener {
        void onHeadphoneStateChanged(boolean isDetected, String message);

        void onTimeStateChanged(boolean isDetected, String message);

        void onCurrentTimeStateChanged(boolean isDetected, String message);

        void onMoveStateChanged(boolean isDetected, String message);
    }
}
