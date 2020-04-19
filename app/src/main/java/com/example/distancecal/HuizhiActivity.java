package com.example.distancecal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class HuizhiActivity extends AppCompatActivity {
    private CustomView huizhi = null;
    private ArrayList<Point> points=new ArrayList<>();
    double k;
    private TextView textView;
    public static  void actionStart(Context context,ArrayList<Point> points,double k) {
        Intent intent = new Intent(context, HuizhiActivity.class);
        intent.putParcelableArrayListExtra("points",points);
        intent.putExtra("k",k);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huizhi);
        huizhi = (CustomView) findViewById(R.id.huizhi_view);
        textView= (TextView) findViewById(R.id.length);
        points=getIntent().getParcelableArrayListExtra("points");
        huizhi.setPath(points);
        tips();
    }

    private void tips() {
        k=getIntent().getDoubleExtra("k",1);
        double length=0;
        String string="";
        for (int i=1;i<9;i++) {
            length=Math.rint(k* Math.sqrt
                    ((points.get(i).x- points.get(i-1).x)*(points.get(i).x- points.get(i-1).x)
                            +(points.get(i).y- points.get(i-1).y)*(points.get(i).y- points.get(i-1).y)));
            string+=length+"cm  ";
        }
        textView.setText("按照拍照顺序，长度分别为："+ string);
    }
}
