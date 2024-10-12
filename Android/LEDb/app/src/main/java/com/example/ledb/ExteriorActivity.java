package com.example.ledb;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.skydoves.colorpickerview.ActionMode;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ExteriorActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket socket;
    Handler bt_handler;
    int handlerState;
    OutputStream outputStream;
    InputStream inputStream;
    ConnectedThread connectedThread;
    private ColorPickerView colorPickerView;

    private RadioGroup radioGroup;
    private RadioButton radio1;
    private RadioButton radio2;
    private RadioButton radio3;
    private RadioButton radio4;
    private RadioButton radio5;
    private RadioButton radio6;
    private RadioButton radio7;
    private RadioButton radio8;

    private Switch switchC1;
    private Switch switchC2;

    private SeekBar seekSlide;
    private SeekBar seekTime;

    private Button buttonOff;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exterior);
        //---------------------- GET ELEMENTS FROM LAYOUT --------------------------//
        TextView tv = (TextView) findViewById(R.id.textView2);
        TextView effectText = (TextView) findViewById(R.id.textView9);
        colorPickerView = findViewById(R.id.colorPickerView);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroupE);
        radio1 = (RadioButton) findViewById(R.id.radioButton1E);
        radio2 = (RadioButton) findViewById(R.id.radioButton2E);
        radio3 = (RadioButton) findViewById(R.id.radioButton3E);
        radio4 = (RadioButton) findViewById(R.id.radioButton4E);
        radio5 = (RadioButton) findViewById(R.id.radioButton5E);
        radio6 = (RadioButton) findViewById(R.id.radioButton6E);

        seekSlide = (SeekBar) findViewById(R.id.seekBarSlide);
        seekTime = (SeekBar) findViewById(R.id.seekBarTime);

        switchC1 = (Switch) findViewById(R.id.switch1E);
        switchC2 = (Switch) findViewById(R.id.switch2E);
        switchC1.setChecked(true);
        switchC2.setClickable(false);

        buttonOff = (Button) findViewById(R.id.buttonOffE);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.e("JLMZ510","Radio changed : "+checkedId);
                RadioButton rbut = (RadioButton) findViewById(checkedId);
                effectText.setText(rbut.getText());
                String msg;
                switch(checkedId) {
                    case R.id.radioButton1E:
                        //Case "Couleur Simple"
                        switchC2.setChecked(false);
                        switchC2.setClickable(false);
                        msg = "Ext:Type:SimpleColor:";
                        connectedThread.write(msg.getBytes(StandardCharsets.UTF_8));
                        break;
                    case R.id.radioButton2E:
                        //Case "Slide 1 couleur"
                        msg = "Ext:Type:Slide1:";
                        switchC2.setChecked(false);
                        switchC2.setClickable(false);
                        connectedThread.write(msg.getBytes(StandardCharsets.UTF_8));
                        break;
                    case R.id.radioButton3E:
                        //Case "Slide 2 couleurs"
                        msg = "Ext:Type:Slide2:";
                        switchC2.setClickable(true);
                        connectedThread.write(msg.getBytes(StandardCharsets.UTF_8));
                        break;
                    case R.id.radioButton4E:
                        //Case "Snke 2 couleurs"
                        msg = "Ext:Type:Snake2:";
                        switchC2.setClickable(true);
                        connectedThread.write(msg.getBytes(StandardCharsets.UTF_8));
                        break;
                    case R.id.radioButton5E:
                        //Case "Rainbow degrade"
                        msg = "Ext:Type:DegradeRainow:";
                        switchC2.setClickable(false);
                        connectedThread.write(msg.getBytes(StandardCharsets.UTF_8));
                        break;
                    case R.id.radioButton6E:
                        //Case "degrade"
                        msg = "Ext:Type:Degrade:";
                        switchC2.setClickable(false);
                        connectedThread.write(msg.getBytes(StandardCharsets.UTF_8));
                        break;
                    case R.id.radioButton7E:
                        //Case "degrade"
                        msg = "Ext:Type:Stars:";
                        switchC2.setClickable(false);
                        connectedThread.write(msg.getBytes(StandardCharsets.UTF_8));
                        break;
                    default:
                        break;
                }
            }

        });

        seekTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            double[] times = new double[]{
                    0.0,
                    0.0,
                    0.0001,
                    0.00025,
                    0.0005,
                    0.00075,
                    0.001,
                    0.0025,
                    0.005,
                    0.0075,
                    0.01,
                    0.03,
                    0.05,
                    0.075,
                    0.085,
                    0.095,
                    0.1,
                    0.15,
                    0.2,
                    0.25,
                    0.35,
                    0.45,
                    0.5,
                    0.6,
                    0.7,
                    0.8,
                    0.9
                };
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("JLMZ51","Seek TIME : Function OnPrgressChanged PROGRESS : "+times[progress]);
                String msg = "Ext:Timer:"+times[progress]+":";
                connectedThread.write(msg.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("JLMZ51","Seek TIME : Function On Start Tracking Touch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("JLMZ51","Seek TIME : Function On Stop Tracking Touch");
            }
        });
        seekSlide.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int[] diviseurs = new int[]{1,2,3,4,5,6,9,10,12,15,18,20,30,36,45,60};
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("JLMZ51","Seek Slide : Function OnPrgressChanged PROGRESS : "+diviseurs[progress]);
                String msg="Ext:SlideSize:"+diviseurs[progress]+":";
                connectedThread.write(msg.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("JLMZ51","Seek Slide : Function On Start Tracking Touch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("JLMZ51","Seek Slide : Function On Stop Tracking Touch");
            }
        });

        switchC1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("JLMZ51","Switch 1 selected !!");
                if(isChecked){
                    //Si 1 allumé on éteint 2
                    switchC2.setChecked(false);
                }else{
                    if(switchC2.isClickable()){
                        switchC2.setChecked(true);
                    }else{
                        switchC1.setChecked(true);
                    }
                }
            }
        });
        switchC2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if(isChecked){
                    switchC1.setChecked(false);
                }else{
                    switchC1.setChecked(true);
                }
                Log.e("JLMZ51","Switch 2 selected !!");

            }
        });

        colorPickerView.setColorListener(new ColorEnvelopeListener() {
            @Override
            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                Log.e("JLMZ51 listener : ", "listenerrrrr !!!! colorEnveloppe : R = "+
                        envelope.getArgb()[1]+ " G = "+envelope.getArgb()[2]+" B = "+envelope.getArgb()[3]);
                String colors ;

                if(switchC1.isChecked()){
                    //Si couleur est sélectionné :
                    //Envoyer un message avec la couleur 1
                   colors = "Ext:Color:Color1:"+envelope.getArgb()[1]+"/"+envelope.getArgb()[2]+"/"+envelope.getArgb()[3]+"/";
                }else{
                    //Sinon c'est forcément couleur 2 :
                    //Envoyer un message avec la couleur 2
                    colors = "Ext:Color:Color2:"+envelope.getArgb()[1]+"/"+envelope.getArgb()[2]+"/"+envelope.getArgb()[3]+"/";
                }
                connectedThread.write(colors.getBytes(StandardCharsets.UTF_8));
            }
        });
        ColorPickerView colorPickerView = new ColorPickerView.Builder(getApplicationContext())
                .setPreferenceName("MyColorPicker")
                //.setActionMode(ActionMode.LAST)
                .build();

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String value = extras.getString("msg");
            Log.e("JLMZ51 interieur","AAA");
        }

        //------------------------------------------BLUETOOTH------------------------------------------//
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        bt_handler=new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = new String((byte[]) msg.obj, StandardCharsets.UTF_8);
                    Log.v("JLMZ51 Exterior", readMessage);
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

            //connectedThread.write("Exterieur !!!".getBytes(StandardCharsets.UTF_8));
        } catch(Exception e){
            Log.i("JLMZ51 Exterior", "Transmission Error");
        }
        //---------------------------------------------------------------------------------------------//

    }
    public void turnOffLedE(View view){
        connectedThread.write("Ext:Type:TurnOff:".getBytes(StandardCharsets.UTF_8));
    }

    //-----DOWN-----//
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(null, "In on Key Down");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            connectedThread.cancel();
        }
        return super.onKeyDown(keyCode, event);
    }
}