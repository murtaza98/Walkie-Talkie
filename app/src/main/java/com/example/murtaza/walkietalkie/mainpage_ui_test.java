package com.example.murtaza.walkietalkie;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.Arrays;

public class mainpage_ui_test extends AppCompatActivity implements View.OnClickListener {

    private ImageView foundDevice;
    static int count = 0;
    RippleBackground rippleBackground;
    ArrayList<Integer> device_ids = new ArrayList<>();
    ArrayList<TextView> device_txt_view = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage_ui_test);

        rippleBackground=(RippleBackground)findViewById(R.id.content);

        final Handler handler=new Handler();

//        final View device1 = LayoutInflater.from(this).inflate(R.layout.device_icon, null);
//        RippleBackground.LayoutParams params = new RippleBackground.LayoutParams(400,400);
//        params.setMargins((int)(Math.random() * 800), (int)(Math.random() * 1000), 0, 0);
//        device1.setLayoutParams(params);
//        TextView txt_device1 = device1.findViewById(R.id.myImageViewText);
//        txt_device1.setText("new hjghj bjk device1");
//        rippleBackground.addView(device1);
//        device1.setId((int)System.currentTimeMillis()+(int)Math.random()*50);
//        device1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        final View device2 = LayoutInflater.from(this).inflate(R.layout.device_icon, null);
//        params = new RippleBackground.LayoutParams(400,400);
//        params.setMargins((int)(Math.random() * 800), (int)(Math.random() * 1000), 0,0);
//        device2.setLayoutParams(params);
//        TextView txt_device2 = device2.findViewById(R.id.myImageViewText);
//        txt_device2.setText("new hjghj bjk device2 njjk njknk njklnk");
//        rippleBackground.addView(device2);

        View device1 = createNewDevice();
        View device2 = createNewDevice();

//        final TextView device1 = new TextView(this);
//        RippleBackground.LayoutParams params = new RippleBackground.LayoutParams(180,180);
//        params.setMargins(20, 50, 0,0);
//
//        v1.setLayoutParams(params);
//        v1.setBackgroundResource(R.drawable.phone2);
//        v1.setVisibility(View.INVISIBLE);
//        v1.setText("LENOVO K8 Note");
//        rippleBackground.addView(v1);

//        View device2 = LayoutInflater.from(this).inflate(R.layout.device_icon, null);
////        device2.setLayoutParams(new LinearLayout.LayoutParams(
////                64,
////                64));
//        rippleBackground.addView(device2);

        final View[] device_array = {device1, device2};

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
                        foundDevice(device_array[index]);
                        count++;
//                        foundDevice(device1);
                    }
                },1000);
            }
        });
    }

    public View createNewDevice(){
        View device1 = LayoutInflater.from(this).inflate(R.layout.device_icon, null);
        RippleBackground.LayoutParams params = new RippleBackground.LayoutParams(400,400);
        params.setMargins((int)(Math.random() * 1000), (int)(Math.random() * 1000), 0, 0);
        device1.setLayoutParams(params);

        TextView txt_device1 = device1.findViewById(R.id.myImageViewText);
        int device_id = (int)(Math.random()*1000);
        txt_device1.setText(device_id+"");
        device1.setId(device_id);
        device1.setOnClickListener(this);

        rippleBackground.addView(device1);

        device_txt_view.add(txt_device1);
        device_ids.add(device_id);
        return device1;
    }

    private void foundDevice(View foundDevice){
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList=new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        foundDevice.setVisibility(View.VISIBLE);
        animatorSet.start();
    }

    @Override
    public void onClick(View v) {
        int view_id = v.getId();
        if(device_ids.contains(view_id)){
            int idx = device_ids.indexOf(view_id);
            Toast.makeText(getApplicationContext(), idx+" Clicked", Toast.LENGTH_SHORT).show();
        }
    }
}
