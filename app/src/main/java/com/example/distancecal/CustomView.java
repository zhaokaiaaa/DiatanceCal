package com.example.distancecal;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class CustomView extends View {

    Paint paint;
    private Path path;

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void setPath(ArrayList<Point> points) {
        int dx = points.get(0).x;
        int dy = points.get(0).y;
        path = new Path();
        //设置起点
        path.moveTo(dx, dy);
        for (int i = 1; i < 9; i++) {
            dx = points.get(i).x;
            dy = points.get(i).y;
            //path路径连接至某点
            path.lineTo(dx, dy);
        }
        //path路径的最后一个点与起点连接
        path.close();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setViewPaint();
        canvas.drawPath(path,paint);
    }
    public void setViewPaint() {
        //绘制风格
        paint = new Paint();
        //去锯齿
        paint.setAntiAlias(true);
        //设置绘制颜色
        paint.setColor(getResources().getColor(android.R.color.holo_blue_light));
        paint.setStrokeWidth(10);//画笔粗细
        //为了方便看Path的路径效果
        //设置绘制风格为空心
        paint.setStyle(Paint.Style.STROKE);
        //设置空心边框的宽度
        paint.setStrokeWidth(10);
    }
}
