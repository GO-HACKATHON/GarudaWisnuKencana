package com.gwk.mbakyu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gwk.timesense.TimeSense;
import com.gwk.timesense.configuration.TSConfiguration;
import com.gwk.timesense.listener.TSListener;
import com.gwk.timesense.rule.TSRule;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Michinggun on 3/26/2017.
 */

public class TimeFragment extends Fragment implements TSListener {
    private Unbinder unbinder;
    @BindView(R.id.time_text) TextView tvTime;
    private String[] morningGreetings = {
            "Good morning, Sunshine!",
            "Rise n’ shine!",
            "Wakey, wakey, eggs and bakey! (Eggs and bacon for breakfast.)",
            "Any morning seeing your sweet face is a good morning, indeed!",
            "I always have a reason to wake up, and that’s simply to say “good morning” to you!"
    };

    private String[] nightGreetings = {
            "Nighty Night.",
            "Go to bed, you sleepy head!",
            "I'll be right here when you wake up.",
            "Sweet dreams.",
            "See ya' in the mornin'!"
    };

    private String[] afternoonGreetings = {
            "Good Afternoon",
            "Let's Lunch dont we?",
            "Ah.. not morning anymore",
            "Sun above you, watchout",
            "Make everything count"
    };

    private String[] eveningGreetings = {
            "Good evening",
            "The evening sings in a voice of amber, the dawn is surely coming.",
            "Make sure your work nearly finish",
            "Lets Prepare for dinner :)",
            "Where the sun?"
    };
    public static TimeFragment newInstance() {
        TimeFragment fragment = new TimeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time, container, false);
        unbinder = ButterKnife.bind(this, view);

        TimeSense.getInstance().setConfiguration(TSConfiguration.defaultConfiguration());
        TimeSense.getInstance().addListener(TSRule.TS_RULE_NAME_MORNING, this);
        TimeSense.getInstance().addListener(TSRule.TS_RULE_NAME_AFTERNOON, this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void timeSenseTriggered() {

    }

    @Override
    public void timeSenseTriggered(String s) {
        switch (s) {
            case TSRule.TS_RULE_NAME_MORNING :
                tvTime.setText(morningGreetings[new Random().nextInt(5)]);
                break;
            case TSRule.TS_RULE_NAME_AFTERNOON :
                tvTime.setText(afternoonGreetings[new Random().nextInt(5)]);
                break;
            case TSRule.TS_RULE_NAME_EVENING :
                tvTime.setText(eveningGreetings[new Random().nextInt(5)]);
                break;
            case TSRule.TS_RULE_NAME_NIGHT:
                tvTime.setText(nightGreetings[new Random().nextInt(5)]);
                break;
        }
    }
}
