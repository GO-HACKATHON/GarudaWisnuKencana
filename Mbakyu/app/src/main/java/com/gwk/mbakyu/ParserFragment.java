package com.gwk.mbakyu;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Michinggun on 3/26/2017.
 */

public class ParserFragment extends Fragment {
    private Unbinder unbinder;
    @BindView(R.id.parser_message_compression)
    TextView tvMessage;
    @BindView(R.id.parser_edit_text)
    EditText mEditText;


    public static ParserFragment newInstance() {
        ParserFragment fragment = new ParserFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parser, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.parser_traffic_btn)
    void onSendTrafficClick() {
        String temp = "Hello Ayu! I Need traffic information to Go-Jek Headquarters from Sushi Tengoku";

        // create traffic request to be sent to server
        TrafficRequest tr = new TrafficRequest();
        tr.from = "Sushi Tengoku";
        tr.to = "GoJek HQ";
        APIService.send(tr);

        tvMessage.setText(temp);
    }

    @OnClick(R.id.parser_schedule_btn)
    void onSendScheduleClick() {
        String temp = "Hello Ayu! Can you tell me what meetings schedule from 1pm";

        // create traffic request to be sent to server
        ScheduleRequest tr = new ScheduleRequest();
        tr.time = 13;
        tr.showClosest = true;
        APIService.send(tr);

        tvMessage.setText(temp);
    }

    @OnClick(R.id.parser_send_btn)
    void onShowDialog() {
        if (getView() == null) {
            return;
        }

//        new AlertDialog.Builder(getActivity()).setMessage("Success").setNegativeButton("Dismiss", null).show();
        new AlertDialog.Builder(getActivity())
                .setTitle("Send Success")
                .setMessage("Success")
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();


        tvMessage.setText("We just save about 70% of your internet quota compare to another usual application");
    }
}
