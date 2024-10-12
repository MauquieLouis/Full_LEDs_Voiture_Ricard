package com.example.ledb;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.companion.AssociationRequest;
import android.companion.BluetoothDeviceFilter;
import android.companion.CompanionDeviceManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int SELECT_DEVICE_REQUEST_CODE = 0;
    private TextView tv;
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket socket;
    Handler bt_handler;
    int handlerState;
    OutputStream outputStream;
    InputStream inputStream;
    ConnectedThread connectedThread;


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkPermissions();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Textview pour les infos----//
        tv = (TextView) findViewById(R.id.textViewInfoBluetooth);
        //---------------------------//
        ListView lv = (ListView) findViewById(R.id.list_view);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Test if Device support Bluetooth
        if(bluetoothAdapter == null){
            //Device doesn't support Bluetooth
            changeTextViewInfoBluetooth("Device doesn't support Bluetooth", tv);
        }

        //Test if Bluetooth is enabled
        if(!bluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        /*CompanionDeviceManager deviceManager = (CompanionDeviceManager) getSystemService(Context.COMPANION_DEVICE_SERVICE);

        BluetoothDeviceFilter deviceFilter = new BluetoothDeviceFilter.Builder()
                // Match only Bluetooth devices whose name matches the pattern
                .setNamePattern(Pattern.compile("raspberrypi"))
                // Match only Bluetooth devices whose service UUID matches this pattern
                .addServiceUuid(new ParcelUuid(new UUID(0x123abcL,-1L)), null)
                .build();

        AssociationRequest pairingRequest = new AssociationRequest.Builder()
                //Find only devices that match this request filter
                .addDeviceFilter(deviceFilter)
                //Stop scanning as soon as one device matching the filter is found
                .setSingleDevice(true)
                .build();

        //When the app tries to pair with the Bluetooth device, show the appropriate pairing request dialog to the user
        deviceManager.associate(pairingRequest, new CompanionDeviceManager.Callback() {
            //Called When a device is found. Launch the IntentSender so the user can
            //Select the device they want to pair with
            @Override
            public void onDeviceFound(IntentSender chooserLauncher) {
                try{
                    startIntentSenderForResult(chooserLauncher, SELECT_DEVICE_REQUEST_CODE, null, 0,0, 0);
                }catch (IntentSender.SendIntentException e){
                    //Failed to send the intent
                    Log.e("MainActivity", "Failed to send intent");
                }
            }

            @Override
            public void onFailure(CharSequence error) {
                //Handle the failure
                changeTextViewInfoBluetooth("Failure in associate bitche !", tv);
            }


        }, null);*/

        //=========================================================================================//
        //                          GET LIST OF POTENTIALS DEVICE                                  //
        //=========================================================================================//
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0 ){
            List<String> btNameAddr = new ArrayList<String>();
            this.changeTextViewInfoBluetooth((" "),tv);
            for (BluetoothDevice device : pairedDevices){
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                btNameAddr.add(deviceName+" : "+deviceHardwareAddress);

                tv.append(("Name : "+deviceName+" | Address : "+deviceHardwareAddress));
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    btNameAddr
            );
            lv.setAdapter(arrayAdapter);
        }
        //=========================================================================================//

        bt_handler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = new String((byte[]) msg.obj,StandardCharsets.UTF_8);
                    Log.v("JLMZ51 MainActivity", readMessage);
                }
            }
        };
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice("B8:27:EB:12:84:3D");
        UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        try {
            socket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
            outputStream=socket.getOutputStream();
            inputStream=socket.getInputStream();
            connectedThread = new ConnectedThread(socket, bt_handler);
            connectedThread.start();

            //connectedThread.write("Zizi".getBytes(StandardCharsets.UTF_8));
            //connectedThread.write("Zizi2".getBytes(StandardCharsets.UTF_8));
            changeTextViewInfoBluetooth("CONNEXION REUSSIE", tv);
        } catch(Exception e){
            changeTextViewInfoBluetooth("Tentative de Connexion échoué", tv);
        }
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == SELECT_DEVICE_REQUEST_CODE && data != null){
            BluetoothDevice deviceToPair =
                    data.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE);
            if(deviceToPair != null){
                deviceToPair.createBond();
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }


    public void onClickBluetooth(View view){
        //Send a message in bluetooth
        changeTextViewInfoBluetooth("Tentative d'envoie du message 'Zizi2Frerot' ",this.tv);
        connectedThread.write("Zizi2Frerot".getBytes(StandardCharsets.UTF_8));
    }

    public void ConnectBluetoothIfNot(View view){
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice("B8:27:EB:12:84:3D");
        UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        try {
            socket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
            outputStream=socket.getOutputStream();
            inputStream=socket.getInputStream();
            connectedThread = new ConnectedThread(socket, bt_handler);
            connectedThread.start();

            connectedThread.write("ConnectionForce".getBytes(StandardCharsets.UTF_8));
            changeTextViewInfoBluetooth("CONNEXION REUSSIE", tv);
        } catch(Exception e){
            changeTextViewInfoBluetooth("Tentative de Connexion échoué", tv);
        }
    }

    public void onClickInteriorButton(View view){
        String value="Welcome on Interior";
        Intent i = new Intent(MainActivity.this, InteriorActivity.class);
        i.putExtra("msg", value);
        connectedThread.cancel();
        startActivity(i);
    }

    public void onClickExteriorButton(View view){
        String value="Welcome on Exterior";
        Intent i = new Intent(MainActivity.this, ExteriorActivity.class);
        i.putExtra("msg", value);
        connectedThread.cancel();
        startActivity(i);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void checkPermissions(){
        int permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    1
            );
        } else if (permission2 != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_LOCATION,
                    1
            );
        }
    }
    public void changeTextViewInfoBluetooth(String text, TextView tv){
        tv.setText(text);
    }
}