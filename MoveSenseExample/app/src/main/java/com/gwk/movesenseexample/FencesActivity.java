package com.gwk.movesenseexample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceQueryResult;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceStateMap;
import com.google.android.gms.awareness.fence.TimeFence;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.gwk.movesense.MoveSenseFences;
import com.gwk.movesense.helper.MoveSenseHelper;
import com.gwk.movesense.receiver.MoveSenseReceiver;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.provider.Settings.System.DATE_FORMAT;

/**
 * Created by Michinggun on 3/25/2017.
 */

public class FencesActivity extends AppCompatActivity implements MoveSenseReceiver.OnFenceDetectedListener, MoveSenseFences.OnQueryListener {
    private static final String TAG = "FencesActivity";

    public static final String MOVE_FENCE = "moveFence";
    public static final String HEADPHONE_FENCE = "headphoneFence";
    public static final String NIGHT_FENCE = "nightFence";

    @BindView(R.id.fencing_headphone) TextView tvHeadphone;
    @BindView(R.id.fencing_time) TextView tvTime;
    @BindView(R.id.fencing_current_time) TextView tvCurrentTime;
    @BindView(R.id.fencing_move) TextView tvMove;
    @BindView(R.id.step_sensor) TextView tvStepSensor;

    //Step Counter
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int stepCounter = 0;
    private int counterSteps = 0;
    private int stepDetector = 0;

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

        // Step Counter
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mSensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_STEP_DETECTOR:
                        stepDetector++;
                        //tvStepSensor.setText(stepCounter + "\n" + stepDetector);
                        break;
                    case Sensor.TYPE_STEP_COUNTER:
                        //Since it will return the total number since we registered we need to subtract the initial amount
                        //for the current steps since we opened app
                        if (counterSteps < 1) {
                            // initial value
                            counterSteps = (int) event.values[0];
                        }

                        // Calculate steps taken based on first counter value received.
                        stepCounter = (int) event.values[0] - counterSteps;
                        tvStepSensor.setText(stepCounter + " steps");
                        break;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onDestroy() {
        mMoveSenseFences.unregisterFences(MOVE_FENCE, HEADPHONE_FENCE, NIGHT_FENCE);
        unregisterReceiver(mMoveSenseReceiver);
        super.onDestroy();
    }

    @OnClick(R.id.fencing_button)
    void onGetCurrentStateClick() {
        mMoveSenseFences.queryFence(this, MOVE_FENCE, HEADPHONE_FENCE, NIGHT_FENCE);
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

    @Override
    public void onQueryReceived(FenceQueryResult fenceQueryResult) {
        FenceStateMap map = fenceQueryResult.getFenceStateMap();
        for (String fenceKey : map.getFenceKeys()) {
            FenceState fenceState = map.getFenceState(fenceKey);
            switch (fenceKey) {
                case MOVE_FENCE:
                    tvMove.setText(fenceState.getPreviousState() + ":" + fenceState.getCurrentState() + "\n" + DATE_FORMAT.format(
                            String.valueOf(new Date(fenceState.getLastFenceUpdateTimeMillis()))));
                    break;
                case HEADPHONE_FENCE:
                    tvHeadphone.setText(fenceState.getPreviousState() + ":" + fenceState.getCurrentState() + "\n" + DATE_FORMAT.format(
                            String.valueOf(new Date(fenceState.getLastFenceUpdateTimeMillis()))));
                    break;
                case NIGHT_FENCE:
                    tvTime.setText(fenceState.getPreviousState() + ":" + fenceState.getCurrentState() + "\n" + DATE_FORMAT.format(
                            String.valueOf(new Date(fenceState.getLastFenceUpdateTimeMillis()))));
                    break;
            }
            Log.i(TAG, "Fence " + fenceKey + ": "
                    + fenceState.getCurrentState()
                    + ", was="
                    + fenceState.getPreviousState()
                    + ", lastUpdateTime="
                    + DATE_FORMAT.format(
                    String.valueOf(new Date(fenceState.getLastFenceUpdateTimeMillis()))));
        }
    }

    @Override
    public void onQueryNotReceived() {
        Toast.makeText(this, "Query Failed", Toast.LENGTH_SHORT).show();
    }
}
