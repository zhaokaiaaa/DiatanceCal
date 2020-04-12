package com.example.distancecal;

import android.content.Intent;

public class Calculate {

    /**
     * 获取手机旋转角度
     *
     * @return
     */
    public static double getAngle(float[] accelerometerValues) {
        float ax, ay, az;
        double g;
        double cos;
        double angle;//角度
        ax = accelerometerValues[0];
        ay = accelerometerValues[1];
        az = accelerometerValues[2];
        g = Math.sqrt(ax * ax + ay * ay + az * az);
        cos = ay / g;
        angle = Math.acos(cos);
        return angle;
    }

    /**
     * 获取身高
     *
     * @return
     */
    public static int getHeight(Intent intent) {
        String heightS = intent.getStringExtra("input_data");
        int heightD = Integer.parseInt(heightS);
        return (int) Math.rint(heightD);
    }

    /**
     * 计算距离
     *
     * @param height
     * @param angle
     * @return
     */
    public static int distanceCal(int height, double angle) {
        int distance = (int) Math.rint((height - 22) / Math.tan(angle));
        return distance;
    }
}
