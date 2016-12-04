package com.example.ziela.gaitsynthesizer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * This is the activity for the home page to wait for user's button press and then transition to
 * the next activity. Home page layout is located in XML resource file labelled activity_home.xml
 */
public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );
        View v = findViewById( R.id.beginButton ); // get button handle
        v.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                transitionToInputActivity();
            }
        });
    }

    /**
     * Move to next activity, and kill this one
     */
    public void transitionToInputActivity() {
        Intent input = new Intent( HomeActivity.this, InputActivity.class );
        HomeActivity.this.startActivity( input );
        finish();
    }

}