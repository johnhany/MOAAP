package net.johnhany.moaap_chp1_r3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends Activity {
    public static final int MEAN_BLUR = 1;
    public static final int MEDIAN_BLUR = 2;
    public static final int GAUSSIAN_BLUR = 3;
    public static final int SHARPEN = 4;
    public static final int DILATE = 5;
    public static final int ERODE = 6;
    public static final int THRESHOLD = 7;
    public static final int ADAPTIVE_THRESHOLD = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Button bMean, bGaussian, bMedian, bSharpen, bDilate, bErode, bThreshold, bAdaptiveThreshold;
        bMean = (Button)findViewById(R.id.bMean);
        bMedian = (Button)findViewById(R.id.bMedian);
        bGaussian = (Button)findViewById(R.id.bGaussian);
        bSharpen = (Button)findViewById(R.id.bSharpen);
        bDilate = (Button)findViewById(R.id.bDilate);
        bErode = (Button)findViewById(R.id.bErode);
        bThreshold = (Button)findViewById(R.id.bThreshold);
        bAdaptiveThreshold = (Button)findViewById(R.id.bAdaptiveThreshold);

        bMean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("ACTION_MODE", MEAN_BLUR);
                startActivity(i);
            }
        });

        bMedian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("ACTION_MODE", MEDIAN_BLUR);
                startActivity(i);
            }
        });

        bGaussian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("ACTION_MODE", GAUSSIAN_BLUR);
                startActivity(i);
            }
        });

        bSharpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("ACTION_MODE", SHARPEN);
                startActivity(i);
            }
        });

        bDilate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("ACTION_MODE", DILATE);
                startActivity(i);
            }
        });

        bErode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("ACTION_MODE", ERODE);
                startActivity(i);
            }
        });

        bThreshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("ACTION_MODE", THRESHOLD);
                startActivity(i);
            }
        });

        bAdaptiveThreshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("ACTION_MODE", ADAPTIVE_THRESHOLD);
                startActivity(i);
            }
        });
    }
}
