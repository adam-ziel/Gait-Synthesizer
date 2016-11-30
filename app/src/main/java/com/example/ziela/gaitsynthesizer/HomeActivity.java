package com.example.ziela.gaitsynthesizer;

/**
 * Created by ziela on 11/20/16.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


/**
 * You touch the brackets I'll kill you
 * This is the activity for the home page. Basically the only thing this class cares about is when to transition to the next
 * activity. We can alter how this home page looks in the XML file labelled activity_home.xml */
public class HomeActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        View v = findViewById(R.id.beginButton); // get button handle

        v.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                transitionToInputActivity();
            }
        });
    }

    /**
     * Move to next activity, and kill this one
     */
    public void transitionToInputActivity()
    {
        Intent input = new Intent(HomeActivity.this, InputActivity.class);

        HomeActivity.this.startActivity(input);

        finish();
    }

}