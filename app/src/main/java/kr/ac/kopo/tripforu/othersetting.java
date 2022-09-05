package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class othersetting extends AppCompatActivity {

    Button btnCar, btnPublicTransport, btnBus, btnSubway, btnTrain;
    EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_othersetting);

        btnCar = (Button)findViewById(R.id.btnCar);
        btnPublicTransport = (Button)findViewById(R.id.btnPublicTransport);
        btnBus = (CheckBox)findViewById(R.id.btnBus);
        btnSubway = (CheckBox)findViewById(R.id.btnSubway);
        btnTrain = (CheckBox)findViewById(R.id.btnTrain);

        editText = (EditText) findViewById(R.id.editText);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.btnCar:
                        btnBus.setVisibility(View.INVISIBLE);
                        btnSubway.setVisibility(View.INVISIBLE);
                        btnTrain.setVisibility(View.INVISIBLE);
                        btnCar.setBackgroundResource(R.drawable.button4_sub);
                        btnPublicTransport.setBackgroundResource(R.drawable.button5);
                        break;

                    case R.id.btnPublicTransport:
                        btnBus.setVisibility(View.VISIBLE);
                        btnSubway.setVisibility(View.VISIBLE);
                        btnTrain.setVisibility(View.VISIBLE);
                        btnPublicTransport.setBackgroundResource(R.drawable.button5_sub);
                        btnCar.setBackgroundResource(R.drawable.button4);
                        break;
                }
            }
        };

        btnCar.setOnClickListener(onClickListener);
        btnPublicTransport.setOnClickListener(onClickListener);


    }



}

