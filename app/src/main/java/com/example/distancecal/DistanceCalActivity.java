package com.example.distancecal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class DistanceCalActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    /**
     * 变量定义
     */
    private static boolean isCeju = false;
    private static boolean isHuizhi = false;//判断用户点击哪个选项
    private SensorManager sensorManager;//传感器管理器
    private Camera mCamera = null;//相机
    private SurfaceView mPreview, mCircle;//预览框与靶心框
    private SurfaceHolder mHolder, circleHolder;
    private Button takePhotoButton;
    private TextView textView;//提示信息
    int height;//存放身高
    double angle;//存放绕X轴旋转角度
    float[] accelerometerValues = new float[3];//保存加速度传感器的值
    float[] magneticValues = new float[3];//保存磁场传感器的值
    float[] r = new float[9];
    float[] values = new float[3];
    private Intent intent;
    private int i = 0;//记录拍摄张数
    private ArrayList<Point> points = new ArrayList<>();//存放坐标集
    private double k=0;//存放倍数


    public static void actionStart(Context context, String input_data, String tips) {
        Intent intent = new Intent(context, DistanceCalActivity.class);
        intent.putExtra("input_data", input_data);
        intent.putExtra("tips", tips);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_camera_activity);
        init();
        click();
    }

    /**
     * 初始化
     */
    public void init() {
        intent = getIntent();

        textView = (TextView) findViewById(R.id.tips);
        textView.setText(intent.getStringExtra("tips"));
        takePhotoButton = (Button) findViewById(R.id.take_photo);

        mPreview = (SurfaceView) findViewById(R.id.preview);
        mHolder = mPreview.getHolder();
        mHolder.addCallback(this);

        mCircle = (SurfaceView) findViewById(R.id.circle);
        circleHolder = mCircle.getHolder();
        circleHolder.addCallback(this);
        mCircle.setZOrderOnTop(true);
        circleHolder.setFormat(PixelFormat.TRANSPARENT);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);//实例化sensorManager
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//加速度传感器创建
        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);//地磁传感器
        sensorManager.registerListener(listener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);//注册传感器
    }


    /**
     * 点击事件
     */
    public void click() {
        if (isCeju) {
            takePhotoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    height = Calculate.getHeight(intent);
                    angle = Calculate.getAngle(accelerometerValues);
                    DistanceActivity.actionStart(DistanceCalActivity.this, height, angle);
                }
            });
        }
        if (isHuizhi) {
            takePhotoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    coordinate();
                    i++;
                    if (i == 9) {
                        HuizhiActivity.actionStart(DistanceCalActivity.this, points,k/9);
                        k=0;
                        for (int i=8;i>=0;i--) {
                            points.remove(i);
                        }
                        i = 0;
                    }
                }
            });
        }
    }

    /**
     * 测距设置
     */
    public static void setCeju() {
        isCeju = true;
        isHuizhi = false;
    }

    /**
     * 绘制设置
     */
    public static void setHuizhi() {
        isCeju = false;
        isHuizhi = true;
    }

    /**
     * 坐标生成
     */
    public void coordinate() {
        int height = Calculate.getHeight(intent);
        double angle = Calculate.getAngle(accelerometerValues);
        int distance = Calculate.distanceCal(height, angle);
        int x=ScreenUtils.getScreenWidth(DistanceCalActivity.this) / 2;
        int y=ScreenUtils.getScreenHeight(DistanceCalActivity.this) / 2;
        int dx = (int) (distance * Math.sin(values[0])/2);//x方向长度
        int dy = (int) (distance * Math.cos(values[0])/2);//y方向长度
        dx = dx + x;
        dy = y-dy ;//以屏幕中心为原点生成坐标
        k+=distance/Math.sqrt((dx-x)*(dx-x)+(dy-y)*(dy-y));
        Point point = new Point(dx, dy);
        points.add(point);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCamera();
            if (mHolder != null) {
                setStartPreview(mCamera, mHolder);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(listener);
        }
    }

    /**
     * 获取Camera对象
     *
     * @return
     */
    private Camera getCamera() {
        Camera camera;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            camera = null;
            e.printStackTrace();
        }
        return camera;
    }

    /**
     * 开始预览相机内容
     */
    private void setStartPreview(Camera camera, SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            //转屏
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);//相机回调制空
            mCamera.stopPreview();//取消相机取景功能
            mCamera.release();//释放相机资源
            mCamera = null;
        }
    }

    /**
     * 绘制靶心
     */
    public void drawCircle() {
        Paint paint = new Paint();//创建画笔
        paint.setStrokeWidth(5);//画笔粗细
        paint.setColor(Color.GREEN);//画笔颜色
        paint.setTextSize(30);//设置文字大小
        //创建画板
        Canvas canvas = circleHolder.lockCanvas();
        if (canvas != null) {
            //设置画布颜色,透明
            canvas.drawColor(PixelFormat.TRANSPARENT, PorterDuff.Mode.CLEAR);
            int dx = ScreenUtils.getScreenWidth(DistanceCalActivity.this) / 2;
            int dy = ScreenUtils.getScreenHeight(DistanceCalActivity.this) / 2;
            //画点
            canvas.drawPoint(dx, dy, paint);
            //设置空心
            paint.setStyle(Paint.Style.STROKE);
            //设置空心宽度
            paint.setStrokeWidth(3);
            //画圆
            canvas.drawCircle(dx, dy, 60, paint);
            //将画布解锁并显示到屏幕上
            circleHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder Holder) {
        drawCircle();
        setStartPreview(mCamera, mHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder Holder, int format, int width, int height) {
        mCamera.stopPreview();
        setStartPreview(mCamera, mHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder Holder) {
        releaseCamera();
    }

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            //判断当前是加速度传感器还是地磁传感器
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                //注意 赋值是要调用clone()方法
                accelerometerValues = sensorEvent.values.clone();
            } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticValues = sensorEvent.values.clone();
            }
            SensorManager.getRotationMatrix(r, null, accelerometerValues, magneticValues);
            SensorManager.getOrientation(r, values);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

}
