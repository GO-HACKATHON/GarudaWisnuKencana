package com.gwk.movesense.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by Michinggun on 3/25/2017.
 */

public class MoveSenseHelper {
    private static final int MY_PERMISSION = 1033;

    public static String getWeatherConditions(int[] conditions) {
        String weatherState = "";
        for (int state : conditions) {
            switch (state) {
                case Weather.CONDITION_CLEAR:
                    weatherState = weatherState + "Clear";
                    break;
                case Weather.CONDITION_CLOUDY:
                    weatherState = weatherState + "Cloudy";
                    break;
                case Weather.CONDITION_FOGGY:
                    weatherState = weatherState + "Foggy";
                    break;
                case Weather.CONDITION_HAZY:
                    weatherState = weatherState + "Hazy";
                    break;
                case Weather.CONDITION_ICY:
                    weatherState = weatherState + "Icy";
                    break;
                case Weather.CONDITION_RAINY:
                    weatherState = weatherState + "Rainy";
                    break;
                case Weather.CONDITION_SNOWY:
                    weatherState = weatherState + "Snowy";
                    break;
                case Weather.CONDITION_STORMY:
                    weatherState = weatherState + "Stormy";
                    break;
                case Weather.CONDITION_UNKNOWN:
                    weatherState = weatherState + "Unknown";
                    break;
                case Weather.CONDITION_WINDY:
                    weatherState = weatherState + "Windy";
                    break;
            }
        }
        return weatherState;
    }

    public static String getActivityType(int activityType) {
        switch (activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "In Vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "On Bicycle";
            case DetectedActivity.ON_FOOT:
                return "On Foot";
            case DetectedActivity.RUNNING:
                return "Running";
            case DetectedActivity.STILL:
                return "Still";
            case DetectedActivity.TILTING:
                return "Tilting";
            case DetectedActivity.UNKNOWN:
                return "Unknown";
            case DetectedActivity.WALKING:
                return "Walking";
        }
        return "Unknowns";
    }

    public static boolean checkPermission(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    new String[]{permission},
                    MY_PERMISSION
            );
            return false;
        }
        return true;
    }

}
