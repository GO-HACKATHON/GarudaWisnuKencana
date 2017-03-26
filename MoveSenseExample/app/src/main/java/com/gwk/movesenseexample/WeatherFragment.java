package com.gwk.movesenseexample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.gwk.movesense.MoveSenseSnapshot;
import com.gwk.movesense.helper.MoveSenseHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Michinggun on 3/26/2017.
 */

public class WeatherFragment extends Fragment implements MoveSenseSnapshot.OnWeatherDetectedListener {
    private Unbinder unbinder;
    @BindView(R.id.weather_text) TextView tvWeather;
    @BindView(R.id.weather_temp) TextView tvTemp;
    @BindView(R.id.weather_image) ImageView ivWeather;
    private MoveSenseSnapshot moveSenseSnapshot;

    public static WeatherFragment newInstance() {
        WeatherFragment fragment = new WeatherFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        unbinder = ButterKnife.bind(this, view);
        moveSenseSnapshot = new MoveSenseSnapshot(getActivity());
        moveSenseSnapshot.getWeathers(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onWeatherNotDetected(String message) {

    }

    @Override
    public void onWeatherDetected(WeatherResult result) {
        if (getView() != null) {
            Weather weather = result.getWeather();
            tvTemp.setText(weather.getTemperature(Weather.CELSIUS) + " 'C\n"
                    + weather.getFeelsLikeTemperature(Weather.CELSIUS) + " 'C");
            tvWeather.setText(MoveSenseHelper.getWeatherConditions(weather.getConditions()));
            ivWeather.setImageResource(MoveSenseHelper.getWeatherConditions(weather.getConditions()).equalsIgnoreCase("Rainy") ? R.drawable.rain : R.drawable.icon_cloud);
        }
    }

    @OnClick(R.id.weather_update)
    void onUpdateClick() {
        moveSenseSnapshot.getWeathers(this);
    }
}
