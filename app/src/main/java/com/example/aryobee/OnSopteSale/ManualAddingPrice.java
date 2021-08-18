package com.example.aryobee.OnSopteSale;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aryobee.R;

public class ManualAddingPrice extends AppCompatActivity {

    EditText Manual_Price;

  TextView total;
    double total_price;
    Button Add_button;
    TextView result;
    int ans=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_adding_price);

        // by ID we can use each component which id is assign in xml file
        Manual_Price=(EditText) findViewById(R.id.manual_price);
        Add_button=(Button) findViewById(R.id.add_button);
        result = (TextView) findViewById(R.id.total_price);

        // Add_button add clicklistener
        Add_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                double num1 = Double.parseDouble(Manual_Price.getText().toString());
                double sum = num1 + total_price;
                result.setText(Double.toString(sum));
            }
        });
    }
}