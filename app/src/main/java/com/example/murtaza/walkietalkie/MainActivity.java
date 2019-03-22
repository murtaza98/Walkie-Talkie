package com.example.murtaza.walkietalkie;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 2;
    private static final int MY_PERMISSIONS_REQUEST_REQUIRED_PERMISSION = 3;
    private static final int SEPRATION_DIST_THRESHOLD = 50;

    private static int device_count = 0;

    RippleBackground rippleBackground;
    ImageView centerDeviceIcon;

    ArrayList<Point> device_points = new ArrayList<>();

    TextView connectionStatus;

    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    public static final int PORT_USED = 9584;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    ArrayList<CustomDevice> custom_peers = new ArrayList<>();

    ServerClass serverClass;
    ClientClass clientClass;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate  (savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermissions();
        initialSetup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menu_inflater = getMenuInflater();
        menu_inflater.inflate(R.menu.main_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.wifi_toggle) {
            toggleWifiState();
        }
        return super.onOptionsItemSelected(item);
    }

    public class ServerClass extends Thread{
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(PORT_USED);
                socket = serverSocket.accept();

                SocketHandler.setSocket(socket);

                startActivity(new Intent(getApplicationContext(), ChatWindow.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ClientClass extends Thread{
        Socket socket;
        String hostAddress;

        ClientClass(InetAddress address){
            this.socket = new Socket();
            this.hostAddress = address.getHostAddress();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAddress, PORT_USED), 500);

                SocketHandler.setSocket(socket);

                startActivity(new Intent(getApplicationContext(), ChatWindow.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    private void initialSetup() {
        // layout files
        connectionStatus = (TextView) findViewById(R.id.connectionStatus);
        rippleBackground = (RippleBackground)findViewById(R.id.content);
        centerDeviceIcon = (ImageView)findViewById(R.id.centerImage);
        // add onClick Listeners
        centerDeviceIcon.setOnClickListener(this);

        // center button position
        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        device_points.add(new Point(size.x / 2, size.y / 2));
        Log.d("MainActivity", size.x + "  " + size.y);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    void checkLocationEnabled(){
        LocationManager lm = (LocationManager)MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.gps_network_not_enabled_title)
                    .setMessage(R.string.gps_network_not_enabled)
                    .setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            MainActivity.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(R.string.Cancel,null)
                    .show();
        }
    }

    @Override
    public void onClick(View v) {
        int view_id = v.getId();

        if(getIndexFromIdPeerList(view_id) != -1){
            int idx = getIndexFromIdPeerList(view_id);
            final WifiP2pDevice device = custom_peers.get(idx).device;
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;

            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getApplicationContext(), "Connected to "+device.deviceName, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(getApplicationContext(), "Error in connecting to "+device.deviceName, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            switch (v.getId()){
                case R.id.centerImage:
                    rippleBackground.startRippleAnimation();
                    checkLocationEnabled();
                    discoverDevices();
                    break;
                default:
                    break;
            }
        }
    }

    private int getIndexFromIdPeerList(int id){
        for(CustomDevice d : custom_peers){
            if(d.id == id){
                return custom_peers.indexOf(d);
            }
        }
        return -1;
    }

    private int checkPeersListByName(String deviceName){
        for(CustomDevice d :custom_peers) {
            if (d.deviceName.equals(deviceName)) {
                return custom_peers.indexOf(d);
            }
        }
        return -1;
    }

    private void discoverDevices() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                connectionStatus.setText("Discovery Started");
            }

            @Override
            public void onFailure(int reason) {
                connectionStatus.setText("Discovery start Failed");
            }
        });
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peersList) {
            Log.d("DEVICE_NAME", "Listener called"+peersList.getDeviceList().size());
            if(peersList.getDeviceList().size() != 0){

                // first make a list of all devices already present
                ArrayList<CustomDevice> device_already_present = new ArrayList<>();

                for(WifiP2pDevice device : peersList.getDeviceList()){
                    int idx = checkPeersListByName(device.deviceName);
                    if(idx != -1){
                        // device already in list
                        device_already_present.add(custom_peers.get(idx));
                    }
                }

                if(device_already_present.size() == peersList.getDeviceList().size()){
                    // all discovered devices already present
                    return;
                }

                // clear previous views
                clear_all_device_icons();

                // this will remove all devices no longer in range
                custom_peers.clear();
                // add all devices in range
                custom_peers.addAll(device_already_present);

                // add all already present devices to the view
                for(CustomDevice d : device_already_present){
                    rippleBackground.addView(d.icon_view);
                }

                for(WifiP2pDevice device : peersList.getDeviceList()) {
                    if (checkPeersListByName(device.deviceName) == -1) {
                        // device not already present
                        View tmp_device = createNewDevice(device.deviceName);
                        rippleBackground.addView(tmp_device);
                        foundDevice(tmp_device);

                        CustomDevice tmp_device_obj = new CustomDevice();
                        tmp_device_obj.deviceName = device.deviceName;
                        tmp_device_obj.id = tmp_device.getId();
                        tmp_device_obj.device = device;
                        tmp_device_obj.icon_view = tmp_device;

                        custom_peers.add(tmp_device_obj);
                    }
                }
            }

            if(peersList.getDeviceList().size() == 0){
                Toast.makeText(getApplicationContext(), "No Peers Found", Toast.LENGTH_SHORT).show();
            }
        }
    };

    void clear_all_device_icons(){
        if(!custom_peers.isEmpty()){
            for(CustomDevice d : custom_peers){
                rippleBackground.removeView(findViewById(d.id));
            }
        }
    }

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            final InetAddress groupOwnerAddress = info.groupOwnerAddress;

            if(info.groupFormed && info.isGroupOwner){
                connectionStatus.setText("HOST");
                serverClass = new ServerClass();
                serverClass.start();
            }else if(info.groupFormed){
                connectionStatus.setText("CLIENT");
                clientClass = new ClientClass(groupOwnerAddress);
                clientClass.start();
            }
        }
    };

    Point generateRandomPosition(){
        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int SCREEN_WIDTH = size.x;
        int SCREEN_HEIGHT = size.y;

        int height_start = SCREEN_HEIGHT / 2 - 300;
        int x = 0;
        int y = 0;

        do{
            x = (int)(Math.random() * SCREEN_WIDTH);
            y = (int)(Math.random() * height_start);
        }while(checkPositionOverlap(new Point(x, y)));

        Point new_point = new Point(x, y);
        device_points.add(new_point);

        return new_point;

    }

    boolean checkPositionOverlap(Point new_p){
    //  if overlap, then return true, else return false
        if(!device_points.isEmpty()){
            for(Point p:device_points){
                int distance = (int)Math.sqrt(Math.pow(new_p.x - p.x, 2) + Math.pow(new_p.y - p.y, 2));
                Log.d(TAG, distance+"");
                if(distance < SEPRATION_DIST_THRESHOLD){
                    return true;
                }
            }
        }
        return false;
    }

    public View createNewDevice(String device_name){
        View device1 = LayoutInflater.from(this).inflate(R.layout.device_icon, null);
        Point new_point = generateRandomPosition();
        RippleBackground.LayoutParams params = new RippleBackground.LayoutParams(350,350);
        params.setMargins(new_point.x, new_point.y, 0, 0);
        device1.setLayoutParams(params);

        TextView txt_device1 = device1.findViewById(R.id.myImageViewText);
        int device_id = (int)System.currentTimeMillis() + device_count++;
        txt_device1.setText(device_name);
        device1.setId(device_id);
        device1.setOnClickListener(this);

        device1.setVisibility(View.INVISIBLE);
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

    private void toggleWifiState() {
        if(wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(false);
            menu.findItem(R.id.wifi_toggle).setTitle("Turn Wifi On");
        }else{
            wifiManager.setWifiEnabled(true);
            menu.findItem(R.id.wifi_toggle).setTitle("Turn Wifi Off");
        }
    }

    public void getPermissions() {
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    MY_PERMISSIONS_REQUEST_REQUIRED_PERMISSION);
        }
    }
}

class CustomDevice{
    int id;
    String deviceName;
    WifiP2pDevice device;
    View icon_view;
    CustomDevice(){

    }
}