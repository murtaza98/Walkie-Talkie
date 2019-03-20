package com.example.murtaza.walkietalkie;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;

public class mainpage_ui_test extends AppCompatActivity {

    private ImageView foundDevice;
    static int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage_ui_test);

        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);

        final Handler handler=new Handler();

        View device1 = LayoutInflater.from(this).inflate(R.layout.device_icon, null);
//        device1.setLayoutParams(new LinearLayout.LayoutParams(
//                64,
//                64));
        rippleBackground.addView(device1);
        View device2 = LayoutInflater.from(this).inflate(R.layout.device_icon, null);
//        device2.setLayoutParams(new LinearLayout.LayoutParams(
//                64,
//                64));
        rippleBackground.addView(device2);

        final View[] device_array = {device1, device2};
        final Float[] float_value1 = {0f, 0f};
        final Float[] float_value2 = {1.2f, 1.2f};
        final Float[] float_value3 = {1f, 1f};

//        foundDevice=(ImageView)findViewById(R.id.foundDevice);
        ImageView button=(ImageView)findViewById(R.id.centerImage);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rippleBackground.startRippleAnimation();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int index = count % device_array.length;
                        foundDevice(device_array[index], float_value1[index], float_value2[index], float_value3[index]);
                        count++;
                    }
                },2000);
            }
        });
    }

    private void foundDevice(View foundDevice, float value1, float value2, float value3){
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList=new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleX", value1, value2, value3);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleY", value1, value2, value3);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        foundDevice.setVisibility(View.VISIBLE);
        animatorSet.start();
    }
}
