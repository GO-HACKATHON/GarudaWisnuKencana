package com.gwk.movesenseexample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.gwk.movesenseexample.helper.SnapshotHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSION_LOCATION = 101;
    @BindView(R.id.activity)
    TextView tvActivity;
    @BindView(R.id.location)
    TextView tvLocation;
    @BindView(R.id.places)
    TextView tvPlaces;
    @BindView(R.id.weather)
    TextView tvWeather;

    GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        client = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        client.connect();
    }

    @OnClick(R.id.activity_btn)
    void onActivityClick() {
        Awareness.SnapshotApi.getDetectedActivity(client).setResultCallback(new ResultCallback<DetectedActivityResult>() {
            @Override
            public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                if (!detectedActivityResult.getStatus().isSuccess()) {
                    Log.e(TAG, "Could not get the current activity.");
                    return;
                }
                ActivityRecognitionResult ar = detectedActivityResult.getActivityRecognitionResult();
                DetectedActivity probableActivity = ar.getMostProbableActivity();
                tvActivity.setText(SnapshotHelper.getActivityType(probableActivity.getType()) + ": " + probableActivity.getConfidence());
                Log.i(TAG, probableActivity.toString());
            }
        });
    }

    @OnClick(R.id.location_btn)
    void onLocationBtnClick() {
        if (ContextCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_LOCATION
            );
            return;
        }
        Awareness.SnapshotApi.getLocation(client).setResultCallback(new ResultCallback<LocationResult>() {
            @Override
            public void onResult(@NonNull LocationResult locationResult) {
                if (!locationResult.getStatus().isSuccess()) {
                    Log.e(TAG, "Could not get location.");
                    return;
                }
                Location location = locationResult.getLocation();
                tvLocation.setText("Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude());
                Log.i(TAG, "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude());
            }
        });
    }

    @OnClick(R.id.places_btn)
    void onPlacesBtnClick() {
        if (ContextCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_LOCATION
            );
            return;
        }
        Awareness.SnapshotApi.getPlaces(client).setResultCallback(new ResultCallback<PlacesResult>() {
            @Override
            public void onResult(@NonNull PlacesResult placesResult) {
                if (!placesResult.getStatus().isSuccess()) {
                    Log.e(TAG, "Could not get places.");
                    return;
                }
                List<PlaceLikelihood> placeLikelihoodList = placesResult.getPlaceLikelihoods();
                // Show the top 5 possible location results.
                String temp = "";
                if (placeLikelihoodList != null) {
                    for (PlaceLikelihood placeLikelihood : placeLikelihoodList) {
                        Log.i(TAG, placeLikelihood.getPlace().getName().toString() + ", likelihood: " + placeLikelihood.getLikelihood());
                        temp = temp + "\n" + placeLikelihood.getPlace().getName().toString() + ", likelihood: " + placeLikelihood.getLikelihood();
                    }
                    tvPlaces.setText(temp);
                }
            }
        });
    }

    @OnClick(R.id.weather_btn)
    void onWeatherClick() {
        if (ContextCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_LOCATION
            );
            return;
        }
        Awareness.SnapshotApi.getWeather(client)
                .setResultCallback(new ResultCallback<WeatherResult>() {
                    @Override
                    public void onResult(@NonNull WeatherResult weatherResult) {
                        if (!weatherResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Could not get weather.");
                            return;
                        }
                        Weather weather = weatherResult.getWeather();
                        Log.i(TAG, "Weather: " + weather);
                        tvWeather.setText("Weather: " + weather.getTemperature(Weather.CELSIUS) + "\n"
                                + "Temperature feels like: " + weather.getFeelsLikeTemperature(Weather.CELSIUS) + "\n"
                                + "Weather conditions: " + SnapshotHelper.getWeatherConditions(weather.getConditions())
                        );

                    }
                });
    }

    /**
     * Fences
     */
    @OnClick(R.id.fences_btn)
    void onFencesBtnClick() {
        startActivity(FencesActivity.newIntent(this));
    }
}
