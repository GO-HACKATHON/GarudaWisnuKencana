package com.gwk.movesenseexample;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.gwk.movesense.MoveSenseSnapshot;
import com.gwk.movesense.helper.MoveSenseHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Michinggun on 3/26/2017.
 */

public class HomeFragment extends Fragment implements MoveSenseSnapshot.OnActivityDetectedListener{

    private Unbinder unbinder;
    //Step Counter
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int stepCounter = 0;
    private int counterSteps = 0;
    private int stepDetector = 0;

    @BindView(R.id.home_steps) TextView tvSteps;
    @BindView(R.id.home_activity_status) TextView tvActivityStatus;
    private MoveSenseSnapshot moveSenseSnapshot;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        moveSenseSnapshot = new MoveSenseSnapshot(getActivity());
        // Step Counter
        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
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
                        tvSteps.setText(String.valueOf(stepCounter));
                        moveSenseSnapshot.getDetectedActivity(HomeFragment.this);
                        if (stepCounter == 15) {
                            onShowDialog();
                        }
                        break;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityNotDetected(String message) {

    }

    @Override
    public void onActivityDetected(ActivityRecognitionResult result) {
        if (getView() != null) {
            tvActivityStatus.setText(String.format("You're currently %s", MoveSenseHelper.getActivityType(result.getMostProbableActivity().getType())));
        }
    }

    @OnClick(R.id.home_btn)
    void onShowDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
        dialogBuilder.setView(dialogView);
//        EditText editText = (EditText) dialogView.findViewById(R.id.label_field);
//        editText.setText("test label");
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}
