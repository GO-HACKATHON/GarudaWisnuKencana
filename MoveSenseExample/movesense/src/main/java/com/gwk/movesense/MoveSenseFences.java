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
import com.google.android.gms.awareness.fence.FenceQueryRequest;
import com.google.android.gms.awareness.fence.FenceQueryResult;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.gwk.movesense.receiver.MoveSenseReceiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Michinggun on 3/25/2017.
 */

public class MoveSenseFences {
    private final Activity mContext;
    private final GoogleApiClient mClient;
    private List<Pair<String, AwarenessFence>> mFences;
    // Declare variables for pending intent and fence receiver.
    private PendingIntent mPendingIntent;
    private OnQueryListener queryListener;

    public MoveSenseFences(@NonNull Activity activity) {
        mFences = new ArrayList<>();
        mContext = activity;
        mClient = new GoogleApiClient.Builder(mContext)
                .addApi(Awareness.API)
                .build();
        mClient.connect();

        Intent intent = new Intent(MoveSenseReceiver.FENCE_RECEIVER_ACTION);
        mPendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
    }

    public void addDetectActivityFence(String key, int fence) {
        mFences.add(new Pair<String, AwarenessFence>(key, DetectedActivityFence.during(fence)));
    }

    public void addHeadphoneFence(String key, int fence) {
        mFences.add(new Pair<String, AwarenessFence>(key, HeadphoneFence.during(fence)));
    }

    public void addAnyFence(String key, AwarenessFence fence) {
        mFences.add(new Pair<String, AwarenessFence>(key, fence));
    }

    public void registerFences() {
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
    public void unregisterFences(final String... fenceKeys) {
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

    public void queryFence(OnQueryListener listener, final String... fenceKeys) {
        if (queryListener == null) {
            queryListener = listener;
        }
        Awareness.FenceApi.queryFences(mClient,
                FenceQueryRequest.forFences(Arrays.asList(fenceKeys)))
                .setResultCallback(new ResultCallback<FenceQueryResult>() {
                    @Override
                    public void onResult(@NonNull FenceQueryResult fenceQueryResult) {
                        if (!fenceQueryResult.getStatus().isSuccess()) {
                            if (queryListener != null) {
                                queryListener.onQueryNotReceived();
                            }
                            for (String fenceKey : fenceKeys) {
                                Log.e(getClass().getName(), "Could not query fence: " + fenceKey);
                            }
                            return;
                        }
                        if (queryListener != null) {
                            queryListener.onQueryReceived(fenceQueryResult);
                        }
                    }
                });
    }

    public interface OnQueryListener {
        void onQueryReceived(FenceQueryResult fenceQueryResult);
        void onQueryNotReceived();
    }

}
