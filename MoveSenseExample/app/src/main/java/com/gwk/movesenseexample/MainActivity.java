package com.gwk.movesenseexample;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.gwk.movesense.MoveSenseSnapshot;
import com.gwk.movesense.helper.MoveSenseHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.activity) TextView tvActivity;
    @BindView(R.id.location) TextView tvLocation;
    @BindView(R.id.places) TextView tvPlaces;
    @BindView(R.id.weather) TextView tvWeather;
    private MoveSenseSnapshot moveSenseSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        moveSenseSnapshot = new MoveSenseSnapshot(this);
    }

    @OnClick(R.id.activity_btn)
    void onActivityClick() {
        moveSenseSnapshot.getDetectedActivity(new MoveSenseSnapshot.OnActivityDetectedListener() {
            @Override
            public void onActivityNotDetected(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onActivityDetected(ActivityRecognitionResult result) {
                DetectedActivity probableActivity = result.getMostProbableActivity();
                tvActivity.setText(MoveSenseHelper.getActivityType(probableActivity.getType()) + ": " + probableActivity.getConfidence());
                Log.i(TAG, probableActivity.toString());
            }
        });
    }

    @OnClick(R.id.location_btn)
    void onLocationBtnClick() {
        moveSenseSnapshot.getLocation(new MoveSenseSnapshot.OnLocationDetectedListener() {
            @Override
            public void onLocationNotDetected(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLocationDetected(LocationResult result) {
                Location location = result.getLocation();
                tvLocation.setText("Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude());
                Log.i(TAG, "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude());
            }
        });
    }

    @OnClick(R.id.places_btn)
    void onPlacesBtnClick() {
        moveSenseSnapshot.getNearbyPlaces(new MoveSenseSnapshot.OnNearbyPlacesDetectedListener() {
            @Override
            public void onNearbyPlacesNotDetected(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNearbyPlacesDetected(PlacesResult result) {
                List<PlaceLikelihood> placeLikelihoodList = result.getPlaceLikelihoods();
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
        moveSenseSnapshot.getWeathers(new MoveSenseSnapshot.OnWeatherDetectedListener() {
            @Override
            public void onWeatherNotDetected(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWeatherDetected(WeatherResult result) {
                Weather weather = result.getWeather();
                Log.i(TAG, "Weather: " + weather);
                tvWeather.setText("Weather: " + weather.getTemperature(Weather.CELSIUS) + "'C\n"
                        + "Temperature feels like: " + weather.getFeelsLikeTemperature(Weather.CELSIUS) + "'C\n"
                        + "Weather conditions: " + MoveSenseHelper.getWeatherConditions(weather.getConditions())
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
