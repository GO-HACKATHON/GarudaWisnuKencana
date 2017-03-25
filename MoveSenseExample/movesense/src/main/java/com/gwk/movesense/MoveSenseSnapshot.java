package com.gwk.movesense;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.gwk.movesense.helper.MoveSenseHelper;

/**
 * Created by Michinggun on 3/25/2017.
 */

public class MoveSenseSnapshot {

    private final Context mContext;
    private GoogleApiClient mClient;

    // Listener
    private OnActivityDetectedListener activityListener;
    private OnLocationDetectedListener locationListener;
    private OnNearbyPlacesDetectedListener nearbyPlacesListener;
    private OnWeatherDetectedListener weatherListener;

    public MoveSenseSnapshot(@NonNull Activity activity) {
        mContext = activity;
        mClient = new GoogleApiClient.Builder(mContext)
                .addApi(Awareness.API)
                .build();
        mClient.connect();
    }

    public void getDetectedActivity(OnActivityDetectedListener listener) {
        if (activityListener == null) {
            activityListener = listener;
        }
        Awareness.SnapshotApi.getDetectedActivity(mClient).setResultCallback(new ResultCallback<DetectedActivityResult>() {
            @Override
            public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                if (activityListener != null) {
                    if (!detectedActivityResult.getStatus().isSuccess()) {
                        activityListener.onActivityNotDetected(detectedActivityResult.getStatus().getStatusMessage());
                        return;
                    }
                    activityListener.onActivityDetected(detectedActivityResult.getActivityRecognitionResult());
                }
            }
        });
    }

    public void getLocation(OnLocationDetectedListener listener) {
        if (locationListener == null) {
            locationListener = listener;
        }
        if (!MoveSenseHelper.checkPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) return;
        Awareness.SnapshotApi.getLocation(mClient).setResultCallback(new ResultCallback<LocationResult>() {
            @Override
            public void onResult(@NonNull LocationResult locationResult) {
                if (locationListener != null) {
                    if (!locationResult.getStatus().isSuccess()) {
                        locationListener.onLocationNotDetected(locationResult.getStatus().getStatusMessage());
                        return;
                    }
                    locationListener.onLocationDetected(locationResult);
                }
            }
        });
    }

    public void getNearbyPlaces(OnNearbyPlacesDetectedListener listener) {
        if (nearbyPlacesListener == null) {
            nearbyPlacesListener = listener;
        }
        if (!MoveSenseHelper.checkPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) return;
        Awareness.SnapshotApi.getPlaces(mClient).setResultCallback(new ResultCallback<PlacesResult>() {
            @Override
            public void onResult(@NonNull PlacesResult placesResult) {
                if (nearbyPlacesListener != null) {
                    if (!placesResult.getStatus().isSuccess()) {
                        nearbyPlacesListener.onNearbyPlacesNotDetected(placesResult.getStatus().getStatusMessage());
                        return;
                    }
                    nearbyPlacesListener.onNearbyPlacesDetected(placesResult);
                }
            }
        });
    }

    public void getWeathers(OnWeatherDetectedListener listener) {
        if (weatherListener == null) {
            weatherListener = listener;
        }
        if (!MoveSenseHelper.checkPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) return;
        Awareness.SnapshotApi.getWeather(mClient)
                .setResultCallback(new ResultCallback<WeatherResult>() {
                    @Override
                    public void onResult(@NonNull WeatherResult weatherResult) {
                        if (weatherListener != null) {
                            if (!weatherResult.getStatus().isSuccess()) {
                                weatherListener.onWeatherNotDetected(weatherResult.getStatus().getStatusMessage());
                                return;
                            }
                            weatherListener.onWeatherDetected(weatherResult);
                        }
                    }
                });
    }

    public interface OnActivityDetectedListener {
        void onActivityNotDetected(String message);
        void onActivityDetected(ActivityRecognitionResult result);
    }

    public interface OnLocationDetectedListener {
        void onLocationNotDetected(String message);
        void onLocationDetected(LocationResult result);
    }

    public interface OnNearbyPlacesDetectedListener {
        void onNearbyPlacesNotDetected(String message);
        void onNearbyPlacesDetected(PlacesResult result);
    }

    public interface OnWeatherDetectedListener {
        void onWeatherNotDetected(String message);
        void onWeatherDetected(WeatherResult result);
    }


}
