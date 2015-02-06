package com.example.sjy.timelyclock.util;

import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;

/**
 * Created by xiepengfei on 15-2-6.
 */
public class SensorUtil implements SensorListener {

    private Handler handler;
    private float x, y, z, last_x, last_y, last_z;
    private Context mContext;
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

    /**
     * 范围
     */
    private int low, up;
    /**
     * 模式
     */
    public static final int SHAKE_MODE = 0;
    public static final int LU_MODE = 1;
    public static final int UP_DOWN_RIGHT_LEFT_MODE = 2;

    private int gameMode;

    public String getGameMode() {
        String gameModeName = "";
        switch (gameMode) {
            case SHAKE_MODE:
                gameModeName = "摇摆模式";
                break;
            case LU_MODE:
                gameModeName = "手鲁模式";
                break;
            case UP_DOWN_RIGHT_LEFT_MODE:
                gameModeName = "上上下下左左右右模式";
                break;
        }
        return gameModeName;
    }

    private SensorManager sensorManager;

    public SensorUtil(Context context, Handler handler, int low, int up, int mode) {
        mContext = context;
        this.handler = handler;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.low = low;
        this.up = up;
        this.gameMode = mode;
    }

    public void regeditListener() {
        lastUpdate = System.currentTimeMillis();
        sensorManager.registerListener(this, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);
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

    public void clear() {
        this.resultSpeed = 0;
        this.count = 0;
    }

    @Override
    public void onSensorChanged(int sensor, float[] values) {
        // TODO Auto-generated method stub
        switch (gameMode) {
            case SHAKE_MODE:
                shakeGame(sensor, values);
                break;
            case LU_MODE:
                luGame(sensor, values);
                break;
            case UP_DOWN_RIGHT_LEFT_MODE:
                break;
        }

    }


    /**
     * 摇摆模式玩法
     *
     * @param sensor
     * @param values
     */
    private void shakeGame(int sensor, float[] values) {
        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            //100毫秒检测一次
//            if (curTime - lastUpdate > TIME) {
            long diffTime = curTime - lastUpdate;
            if (diffTime <= 0) diffTime = 1;
            lastUpdate = curTime;
            x = values[SensorManager.DATA_X];
            y = values[SensorManager.DATA_Y];
//                z = values[SensorManager.DATA_Z];
            if (x < 2) {
                x = 0;
            }
            if (y < 2) y = 0;
            float speed = (Math.abs(x) + Math.abs(y)) / 1 * 100;
            resultSpeed += speed;
            count++;
            Log.i("x", String.valueOf(x));
            Log.i("y", String.valueOf(y));
            Log.i("z", String.valueOf(z));
            Log.i("resultSpeed", String.valueOf(resultSpeed));
            if (speed > low && speed < up) {
                addVibrator(160);
                Log.i("lock", "speed = " + speed);
                lastUpdate += 100;//判断成功的话,x秒内不再判断
                handler.sendEmptyMessage(MessageID.MESSAGE_SENSOR);
            }

            last_x = x;
            last_y = y;
//                last_z = z;
//            }
        }
    }


    /**
     * lu模式玩法
     *
     * @param sensor
     * @param values
     */
    private void luGame(int sensor, float[] values) {

        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            x = Math.abs(values[SensorManager.DATA_X]);
            y = values[SensorManager.DATA_Y];
            z = Math.abs(values[SensorManager.DATA_Z]);
            Log.i("x", String.valueOf(x));
            Log.i("z", String.valueOf(z));
            if (z > 8 && z < 11) {
                if (x < 10) x = 0;
                if (x == 0) {
                    float speed = Math.abs(y) * 100;
                    resultSpeed += speed;
                    count++;
                    addVibrator(200);
                }

            }
        }
    }

    /**
     * 上下左右模式玩法
     * @param sensor
     * @param values
     */
    private void upDownMode(int sensor, float[] values) {


    }

    //震动
    private void addVibrator(long ms) {
        try {
            Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
