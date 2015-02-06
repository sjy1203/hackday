package com.example.sjy.timelyclock;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.sjy.timelyclock.util.SensorUtil;
import com.github.adnansm.timelytextview.TimelyView;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SuccessTickView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import info.hoang8f.widget.FButton;


public class GameActivity extends ActionBarActivity {
    private SweetAlertDialog startGame_dialog;
    private SweetAlertDialog nextGame_dialog;
    private SweetAlertDialog failGame_dialog;
    private SensorUtil sensorUtil;
    private static Handler handler = new Handler();
    private TimelyView game_timer;
    private List<Animator> timer_List;
    private AnimatorSet timer_Anima;

    private float resultScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initParams();
        startGameNotify();

    }

    private void initParams() {
        sensorUtil = new SensorUtil(this, handler, 1300, 1800, SensorUtil.LU_MODE);
        timer_List = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            timer_List.add(game_timer.animate(i).setDuration(1000));
        }
        timer_Anima = new AnimatorSet();
        timer_Anima.playSequentially(timer_List);
        timer_Anima.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                resultScore = sensorUtil.getResultSpeed() / sensorUtil.getCount();
                Log.i("count", String.valueOf(sensorUtil.getCount()));
                sensorUtil.unRegeditListener();
//                if (resultScore < 650 && resultScore > 450) {

                nextGame_dialog.setContentText(String.valueOf(resultScore));
                nextGame_dialog.show();

//                } else {
//                    failGame_dialog.show();
//                }
            }
        });
    }

    private void initView() {
        game_timer = (TimelyView) findViewById(R.id.game_timer);


    }

    private void startGameNotify() {
        Log.v("startNotify", "start");
        sensorUtil.clear();
        startGame_dialog = new SweetAlertDialog(this);
        startGame_dialog.setTitleText("Sweet!")
                .setContentText("准备开始５秒测试了").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.cancel();
                sensorUtil.regeditListener();
                timer_Anima.start();
            }
        });
        nextGame_dialog = new SweetAlertDialog(this);
        nextGame_dialog.setTitleText("得分").setCancelText("退出").setConfirmText("重新测试").showCancelButton(true);
        nextGame_dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                Log.v("23333", "2333");

                sweetAlertDialog.cancel();
                startGameNotify();
            }
        });
        startGame_dialog.show();
        failGame_dialog = new SweetAlertDialog(this);
        failGame_dialog.setTitleText("失败").setConfirmText("重新开始")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        startGameNotify();
                        sweetAlertDialog.cancel();
                    }
                });
        Log.v("startNotify", "startafter");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //震动
    private void addVibrator(long ms) {
        try {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
