package com.gwk.movesense;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.gwk.movesense.receiver.MoveSenseReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michinggun on 3/25/2017.
 */

public class MoveSenseFences {
    private static final long nowMillis = System.currentTimeMillis();
    private static final long oneHourMillis = 1L * 60L * 60L * 1000L;
    private static final String FENCE_RECEIVER_ACTION = "move_sense_receiver";
    private final Activity mContext;
    private final GoogleApiClient mClient;
    private List<Pair<String, AwarenessFence>> mFences;
    // Declare variables for pending intent and fence receiver.
    private PendingIntent mPendingIntent;
    private MoveSenseReceiver mMoveSenseReceiver;

    public MoveSenseFences(@NonNull Activity activity) {
        mContext = activity;
        mClient = new GoogleApiClient.Builder(mContext)
                .addApi(Awareness.API)
                .build();
        mClient.connect();
        mFences = new ArrayList<>();

        Intent intent = new Intent(FENCE_RECEIVER_ACTION);
        mPendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
//        mMoveSenseReceiver = new MoveSenseReceiver();
//        mMoveSenseReceiver.setFenceListener(listener);
//        registerReceiver(myFenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));
    }

    private void addDetectActivityFence(String key, int fence) {
        mFences.add(new Pair<String, AwarenessFence>(key, DetectedActivityFence.during(fence)));
    }

    private void addHeadphoneFence(String key, int fence) {
        mFences.add(new Pair<String, AwarenessFence>(key, HeadphoneFence.during(fence)));
    }

    private void addAnyFence(String key, AwarenessFence fence) {
        mFences.add(new Pair<String, AwarenessFence>(key, fence));
    }

    private void registerFences() {
        FenceUpdateRequest.Builder builder = new FenceUpdateRequest.Builder();
        for (Pair<String, AwarenessFence> fence : mFences) {
            builder.addFence(fence.first, fence.second, mPendingIntent);
        }
        Awareness.FenceApi.updateFences(
                mClient,
                builder.build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(getClass().getName(), "Fence was successfully registered.");
                        } else {
                            Log.e(getClass().getName(), "Fence could not be registered: " + status);
                        }
                    }
                });
    }

    /**
     * Call when onDestroy
     * @param fenceKeys
     */
    private void unregisterFences(final String... fenceKeys) {
        FenceUpdateRequest.Builder builder = new FenceUpdateRequest.Builder();
        for (String fenceKey : fenceKeys) {
            builder.removeFence(fenceKey);
        }
        Awareness.FenceApi.updateFences(
                mClient,
                builder.build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                for (String fenceKey : fenceKeys) {
                    Log.i(getClass().getName(), "Fence " + fenceKey + " successfully removed.");
                }
            }

            @Override
            public void onFailure(@NonNull Status status) {
                for (String fenceKey : fenceKeys) {
                    Log.i(getClass().getName(), "Fence " + fenceKey + " could NOT be removed.");
                }
            }
        });
    }


}
