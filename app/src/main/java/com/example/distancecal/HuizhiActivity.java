package com.example.distancecal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class HuizhiActivity extends AppCompatActivity {
    private CustomView huizhi = null;
    private ArrayList<Point> points=new ArrayList<>();
    public static  void actionStart(Context context,ArrayList<Point> points) {
        Intent intent = new Intent(context, HuizhiActivity.class);
        intent.putParcelableArrayListExtra("points",points);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huizhi);
        huizhi = (CustomView) findViewById(R.id.huizhi_view);
        points=getIntent().getParcelableArrayListExtra("points");
        huizhi.setPath(points);
    }
}
