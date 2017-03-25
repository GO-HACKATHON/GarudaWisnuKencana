package com.gwk.movesenseexample;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.fence.TimeFence;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.gwk.movesenseexample.receiver.MyFenceReceiver;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Michinggun on 3/25/2017.
 */

public class FencesActivity extends AppCompatActivity implements MyFenceReceiver.OnFenceDetectedListener {
    private static final String FENCE_RECEIVER_ACTION = "1002";
    private static final String TAG = "FencesActivity";

    public static final String HEADPHONE_FENCE = "headphoneFence";
    public static final String TIME_FENCE = "timeFence";
    public static final String CURRENT_TIME_FENCE = "currentTimeFence";
    public static final String MOVE_FENCE = "moveFence";

    @BindView(R.id.fencing_headphone) TextView tvHeadphone;
    @BindView(R.id.fencing_time) TextView tvTime;
    @BindView(R.id.fencing_current_time) TextView tvCurrentTime;
    @BindView(R.id.fencing_move) TextView tvMove;

    GoogleApiClient client;
    long nowMillis = System.currentTimeMillis();
    long oneHourMillis = 1L * 60L * 60L * 1000L;

    // Declare variables for pending intent and fence receiver.
    private PendingIntent myPendingIntent;
    private MyFenceReceiver myFenceReceiver;
    AwarenessFence walkingFence = DetectedActivityFence.during(DetectedActivityFence.WALKING);
    AwarenessFence vehicleFence = DetectedActivityFence.during(DetectedActivityFence.IN_VEHICLE);
    AwarenessFence headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);
    AwarenessFence timeFence = TimeFence.inTimeInterval(TimeFence.TIME_INTERVAL_EVENING);
    AwarenessFence currentTimeFence = TimeFence.inInterval(nowMillis, nowMillis + oneHourMillis);


    //Significant motion sensor
    private SensorManager mSensorManager;
    private Sensor mSensor;
    //private TriggerEventListener mTriggerEventListener;

    public static Intent newIntent(Context context) {
        return new Intent(context, FencesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fences);
        ButterKnife.bind(this);
        client = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        client.connect();

        Intent intent = new Intent(FENCE_RECEIVER_ACTION);
        myPendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        myFenceReceiver = new MyFenceReceiver();
        myFenceReceiver.setFenceListener(this);
        registerReceiver(myFenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));


        // Register the fence to receive callbacks.
        // The fence key uniquely identifies the fence.
        Awareness.FenceApi.updateFences(
                client,
                new FenceUpdateRequest.Builder()
                        .addFence(HEADPHONE_FENCE, headphoneFence, myPendingIntent)
                        .addFence(TIME_FENCE, timeFence, myPendingIntent)
                        .addFence(CURRENT_TIME_FENCE, currentTimeFence, myPendingIntent)
                        .addFence(MOVE_FENCE, walkingFence, myPendingIntent)
                        .build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Fence was successfully registered.");
                        } else {
                            Log.e(TAG, "Fence could not be registered: " + status);
                        }
                    }
                });


        // Significant Motion
//        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
//        mTriggerEventListener = new TriggerEventListener() {
//            @Override
//            public void onTrigger(TriggerEvent event) {
//                for (float value : event.values) {
//                    Log.i(TAG, event.sensor.getName() + ": " + value);
//                }
//            }
//        };
//        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // mSensorManager.requestTriggerSensor(mTriggerEventListener, mSensor);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // mSensorManager.cancelTriggerSensor(mTriggerEventListener, mSensor);
    }

    @Override
    protected void onDestroy() {
        unregisterFence(HEADPHONE_FENCE);
        unregisterFence(TIME_FENCE);
        unregisterFence(CURRENT_TIME_FENCE);
        unregisterFence(MOVE_FENCE);
        unregisterReceiver(myFenceReceiver);
        super.onDestroy();
    }

    protected void unregisterFence(final String fenceKey) {
        Awareness.FenceApi.updateFences(
                client,
                new FenceUpdateRequest.Builder()
                        .removeFence(fenceKey)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Log.i(TAG, "Fence " + fenceKey + " successfully removed.");
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Log.i(TAG, "Fence " + fenceKey + " could NOT be removed.");
            }
        });
    }

    @Override
    public void onHeadphoneStateChanged(boolean isDetected, String message) {
        tvHeadphone.setText(String.format(isDetected ? "DETECTED: %s" : "UNDETECTED: %s", message));
    }

    @Override
    public void onTimeStateChanged(boolean isDetected, String message) {
        tvTime.setText(String.format(isDetected ? "DETECTED: %s" : "UNDETECTED: %s", message));
    }

    @Override
    public void onCurrentTimeStateChanged(boolean isDetected, String message) {
        tvCurrentTime.setText(String.format(isDetected ? "DETECTED: %s" : "UNDETECTED: %s", message));
    }

    @Override
    public void onMoveStateChanged(boolean isDetected, String message) {
        tvMove.setText(String.format(isDetected ? "DETECTED: %s" : "UNDETECTED: %s", message));
    }

    @OnClick(R.id.fencing_button)
    void onGetCurrentStateClick() {

    }
}
