package net.johnhany.moaap_chp5_r3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Button bPyramids, bOptFlowKLT;
        bPyramids = (Button) findViewById(R.id.bPyramids);
        bOptFlowKLT = (Button) findViewById(R.id.bOptFlowKLT);
        bOptFlowKLT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
        bPyramids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PyramidActivity.class);
                startActivity(i);
            }
        });
    }
}
