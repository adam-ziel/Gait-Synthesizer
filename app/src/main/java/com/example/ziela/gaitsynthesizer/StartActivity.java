package com.example.ziela.gaitsynthesizer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


/**
 * This is the activity for the home page. Basically the only thing this class cares about is when to transition to the next
 * activity. We can alter how this home page looks in the XML file labelled activity_start.xml
 */
public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_start );

        View layoutMain = findViewById( R.id.layoutMain );
        // Set on touch listener
        View view;
        view = findViewById(R.id.beginButton);
        view.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent config = new Intent( StartActivity.this, ConfigActivity.class );
                    StartActivity.this.startActivity( config );
                    finish(); //kill this activity I'm done with it.
                }
            }
        );
    }

}