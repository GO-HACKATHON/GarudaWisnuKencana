package com.gwk.movesense.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.awareness.fence.FenceState;

/**
 * Created by Michinggun on 3/25/2017.
 */

public class MoveSenseReceiver extends BroadcastReceiver {
    public static final String FENCE_RECEIVER_ACTION = "move_sense_receiver";
    private OnFenceDetectedListener listener;

    public void setFenceListener(OnFenceDetectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState fenceState = FenceState.extract(intent);

        switch (fenceState.getCurrentState()) {
            case FenceState.TRUE:
                if (listener != null) {
                    listener.onFenceDetected(fenceState);
                }
                break;
            case FenceState.FALSE:
            case FenceState.UNKNOWN:
                if (listener != null) {
                    listener.onFenceNotDetected(fenceState);
                }
                break;
        }
    }

    public interface OnFenceDetectedListener {
        void onFenceDetected(FenceState state);
        void onFenceNotDetected(FenceState state);
    }

}
