package com.app.ats.com.ilocate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by abdulla on 1/8/17.
 */

public class profileact extends Activity {


    Button b2;
    EditText e1, e2, e3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actprof);

        b2 = (Button) findViewById(R.id.button3);
        e1 = (EditText) findViewById(R.id.ed1);
        e2 = (EditText) findViewById(R.id.ed2);
        e3 = (EditText) findViewById(R.id.ed3);


        if (!(getSharedPreferences("e1", MODE_PRIVATE).getString("e1", "0").equals("0")))
            e1.setText(getSharedPreferences("e1", MODE_PRIVATE).getString("e1", "0"));

        if (!(getSharedPreferences("e1", MODE_PRIVATE).getString("e2", "0").equals("0")))
            e2.setText(getSharedPreferences("e1", MODE_PRIVATE).getString("e2", "0"));

        if (!(getSharedPreferences("e1", MODE_PRIVATE).getString("e3", "0").equals("0")))
            e3.setText(getSharedPreferences("e1", MODE_PRIVATE).getString("e3", "0"));


        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreferences("e1", MODE_PRIVATE).edit().putString("e1", e1.getText().toString()).apply();
                getSharedPreferences("e1", MODE_PRIVATE).edit().putString("e2", e2.getText().toString()).apply();
                getSharedPreferences("e1", MODE_PRIVATE).edit().putString("e3", e3.getText().toString()).apply();
                Intent i = new Intent(getApplicationContext(), StartActivity.class);
                startActivity(i);
                finish();


            }
        });

    }
}
