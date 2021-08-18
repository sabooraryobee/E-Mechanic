package com.example.aryobee.OnSopteSale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aryobee.R;

public class OnSpoteSale extends AppCompatActivity {

    OnSpoteSalePrice OnSpoteSalePice;
    TextView total;
    double total_price;
   // EditText Manual_Price;
    Button Next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_spote_sale);

        OnSpoteSalePice = new OnSpoteSalePrice();
        total = findViewById(R.id.total);
        //Manual_Price=(EditText) findViewById(R.id.manual_price);
        //    next = findViewById(R.id.next_button);
        Next = (Button) findViewById(R.id.next_button);


        Next.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
                startActivity(new Intent(OnSpoteSale.this, ManualAddingPrice.class));
            }
        });
    }

    public void onCheckBoxClicked(View view) {

        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.cbb1:
                if (checked)
                    OnSpoteSalePice.setWL(100);
                else
                    OnSpoteSalePice.setWL(0);
                break;

            case R.id.cbb2:
                if (checked)
                    OnSpoteSalePice.setBr(200);
                else
                    OnSpoteSalePice.setBr(0);
                break;

            case R.id.cbb3:
                if (checked)
                    OnSpoteSalePice.setFT(300);
                else
                    OnSpoteSalePice.setFT(0);
                break;
        }
        total.setText("Total Price:"+ calculate_total() + "Af");
    }

    private double calculate_total() {
        total_price = OnSpoteSalePice.getWL() + OnSpoteSalePice.getBr() + OnSpoteSalePice.getFT() + OnSpoteSalePice.Charges;
        return total_price;
    }


}
