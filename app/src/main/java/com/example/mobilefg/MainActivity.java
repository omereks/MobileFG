package com.example.mobilefg;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public Model model = new Model("", 0);

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = (TextView)findViewById(R.id.textViewTitle);

        setContentView(R.layout.activity_main);

        EditText editTextIP = (EditText)findViewById(R.id.editTextIP);
        EditText editTextPort = (EditText)findViewById(R.id.editTextPort);
        Button buttonConnect = (Button)findViewById(R.id.buttonConnect);

        SeekBar seekBarThrottle = (SeekBar)findViewById(R.id.seekBarThrottle);
        seekBarThrottle.setMin(0);
        seekBarThrottle.setMax(10);

        SeekBar seekBarRudder = (SeekBar)findViewById(R.id.seekBarRudder);
        seekBarRudder.setMin(-10);
        seekBarRudder.setMax(10);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    model = new Model(editTextIP.getText().toString(), Integer.parseInt(editTextPort.getText().toString()));
                    model.Connect();
                }catch (Exception e) {
                    Log.e("Connection", "conection fail:", e);
                }

            }
        });

        seekBarThrottle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                model.setThrottle((float)progress / 10.0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        seekBarRudder.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                model.setRudder((float)progress / 10.0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }



}