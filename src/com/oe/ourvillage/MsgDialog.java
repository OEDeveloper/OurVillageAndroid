package com.oe.ourvillage;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;


public class MsgDialog extends Dialog {

    public interface ReadyListener {
        public void ready(String name);
    }

    private class OKListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            MsgDialog.this.dismiss();
            readyListener.ready(String.valueOf(etName.getText()));
        }
    }

    EditText etName;
    private final DecimalFormat format = new DecimalFormat("#0.00000");
    private final String lat, lon;
    private final String name;
    private final ReadyListener readyListener;
    private final String source;

    private final String time;

    public MsgDialog(Context context, String name, String lat, String lon, String time,
                     String source, ReadyListener readyListener) {
        super(context);
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.source = source;
        this.time = time;

        this.readyListener = readyListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.msg_dialog_other);

        Button buttonOK = (Button) findViewById(R.id.Button01);
        TextView latitude = (TextView) findViewById(R.id.lat);
        TextView longitude = (TextView) findViewById(R.id.lon);
        TextView time = (TextView) findViewById(R.id.timeStamp);
        TextView source = (TextView) findViewById(R.id.source);

        latitude.setText(lat);
        longitude.setText(lon);
        time.setText(this.time);
        source.setText(this.source);

        setTitle("Enter a chalk about this picture");

        // populate the states spinners
        Spinner statespinner = (Spinner) findViewById(R.id.stateSpinner);
        ArrayAdapter<CharSequence> sadapter = ArrayAdapter
                                                          .createFromResource(
                                                                              getContext(),
                                                                              R.array.states,
                                                                              android.R.layout.simple_spinner_item);
        sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statespinner.setAdapter(sadapter);

        buttonOK.setOnClickListener(new OKListener());
        etName = (EditText) findViewById(R.id.EditTextMsg);

    }

}
