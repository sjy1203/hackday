package com.example.sjy.timelyclock.util;

import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

/**
 * Created by xiepengfei on 15-2-6.
 */
public class SensorUtil implements SensorListener {

    private Handler handler;
    private float x, y, z, last_x, last_y, last_z;
    /**
     * 记录最后一次判断的时间
     */
    private long lastUpdate;
    /**
     * 控制精度,值越小灵敏度越高
     */
    private final static int SHAKE_THRESHOLD = 1000;
    /**
     * 检测间隔
     */
    private final static long TIME = 100;

    private SensorManager sensorManager;

    public SensorUtil(Context context, Handler handler) {
        this.handler = handler;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void regeditListener() {
        lastUpdate = System.currentTimeMillis();
        sensorManager.registerListener(this, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unRegeditListener() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    private float resultSpeed = 0;
    private int count = 0;

    public float getResultSpeed() {
        return resultSpeed;
    }

    public void setResultSpeed(float resultSpeed) {
        this.resultSpeed = resultSpeed;
    }

    public int getCount() {
        return count;
    }

    @Override
    public void onSensorChanged(int sensor, float[] values) {
        // TODO Auto-generated method stub
        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            //100毫秒检测一次
            if (curTime - lastUpdate > TIME) {
                long diffTime = curTime - lastUpdate;
                if (diffTime <= 0) diffTime = 1;
                lastUpdate = curTime;
                x = values[SensorManager.DATA_X];
                y = values[SensorManager.DATA_X];
                z = values[SensorManager.DATA_X];
                if (x < 2 || y < 2 || z < 2) {
                    x = 0;
                    y = 0;
                    z = 0;
                }
                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
                resultSpeed += speed;
                count++;
//                this.setResultSpeed(resultSpeed);
                Log.i("x", String.valueOf(x));
                Log.i("y", String.valueOf(y));
                Log.i("z", String.valueOf(z));
                Log.i("resultSpeed", String.valueOf(resultSpeed));
                if (speed > SHAKE_THRESHOLD) {
                    Log.i("lock", "speed = " + speed);
                    lastUpdate += 100;//判断成功的话,x秒内不再判断
                    handler.sendEmptyMessage(MessageID.MESSAGE_SENSOR);
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

}
