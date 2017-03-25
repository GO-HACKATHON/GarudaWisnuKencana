package com.gwk.movesenseexample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.TimeFence;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.gwk.movesense.MoveSenseFences;
import com.gwk.movesense.helper.MoveSenseHelper;
import com.gwk.movesense.receiver.MoveSenseReceiver;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Michinggun on 3/25/2017.
 */

public class FencesActivity extends AppCompatActivity implements MoveSenseReceiver.OnFenceDetectedListener {
    private static final String TAG = "FencesActivity";

    public static final String MOVE_FENCE = "moveFence";
    public static final String HEADPHONE_FENCE = "headphoneFence";
    public static final String NIGHT_FENCE = "nightFence";

    @BindView(R.id.fencing_headphone) TextView tvHeadphone;
    @BindView(R.id.fencing_time) TextView tvTime;
    @BindView(R.id.fencing_current_time) TextView tvCurrentTime;
    @BindView(R.id.fencing_move) TextView tvMove;

    //Significant motion sensor
    private SensorManager mSensorManager;
    private Sensor mSensor;
    //private TriggerEventListener mTriggerEventListener;

    private MoveSenseFences mMoveSenseFences;
    private MoveSenseReceiver mMoveSenseReceiver;

    public static Intent newIntent(Context context) {
        return new Intent(context, FencesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fences);
        ButterKnife.bind(this);
        mMoveSenseFences = new MoveSenseFences(this);
        mMoveSenseReceiver = new MoveSenseReceiver();
        mMoveSenseReceiver.setFenceListener(this);
        registerReceiver(mMoveSenseReceiver, new IntentFilter(MoveSenseReceiver.FENCE_RECEIVER_ACTION));

        mMoveSenseFences.addDetectActivityFence(MOVE_FENCE, DetectedActivityFence.ON_FOOT);
        mMoveSenseFences.addHeadphoneFence(HEADPHONE_FENCE, HeadphoneState.PLUGGED_IN);
        if (MoveSenseHelper.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            mMoveSenseFences.addAnyFence(NIGHT_FENCE, TimeFence.inTimeInterval(TimeFence.TIME_INTERVAL_NIGHT));
        }

        // Register the fence to receive callbacks.
        mMoveSenseFences.registerFences();


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
        mMoveSenseFences.unregisterFences(MOVE_FENCE, HEADPHONE_FENCE, NIGHT_FENCE);
        unregisterReceiver(mMoveSenseReceiver);
        super.onDestroy();
    }

    @OnClick(R.id.fencing_button)
    void onGetCurrentStateClick() {

    }

    @Override
    public void onFenceDetected(FenceState state) {
        switch (state.getFenceKey()) {
            case MOVE_FENCE:
                tvMove.setText(state.getPreviousState() + ":" + state.getCurrentState());
                break;
            case HEADPHONE_FENCE:
                tvHeadphone.setText(state.getPreviousState() + ":" + state.getCurrentState());
                break;
            case NIGHT_FENCE:
                tvTime.setText(state.getPreviousState() + ":" + state.getCurrentState());
                break;
        }
    }

    @Override
    public void onFenceNotDetected(FenceState state) {
        switch (state.getFenceKey()) {
            case MOVE_FENCE:
                tvMove.setText("Not Detected");
                break;
            case HEADPHONE_FENCE:
                tvHeadphone.setText("Not Detected");
                break;
            case NIGHT_FENCE:
                tvTime.setText("Not Detected");
                break;
        }
    }
}
