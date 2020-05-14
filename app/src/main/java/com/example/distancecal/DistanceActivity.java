package com.example.distancecal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class DistanceActivity extends BaseActivity {
    private TextView distanceText;
    private TextView heightText;
    private TextView angleText;
    private Intent intent;
    private int height;
    private double angle;
    private int distance;
    public static void actionStart(Context context, int height, double angle) {
        Intent intent = new Intent(context, DistanceActivity.class);
        intent.putExtra("height", height);
        intent.putExtra("angle", angle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distance_layout);
        ActivityCollector.addActivity(this);
        distanceText = (TextView) findViewById(R.id.distance);
        heightText = (TextView) findViewById(R.id.height);
        angleText = (TextView) findViewById(R.id.angle);
        getData();
        distance=Calculate.distanceCal(height,angle);
        heightText.setText("身高是："+height+"cm");
        angleText.setText("角度是："+Math.rint(Math.toDegrees(angle))+"度");
        distanceText.setText("距离是："+distance+"cm");
    }

    public void getData() {
        intent = getIntent();
        height=intent.getIntExtra("height",1);
        angle = intent.getDoubleExtra("angle",1);
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(DistanceActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
