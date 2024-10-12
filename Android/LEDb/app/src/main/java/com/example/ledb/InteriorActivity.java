package com.example.ledb;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.skydoves.colorpickerview.listeners.ColorListener;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class InteriorActivity extends AppCompatActivity {


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

    private RadioGroup radioGroupCalandre;
    private RadioButton radio1Cal;
    private RadioButton radio2Cal;
    private Switch switchCalandre;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interior);
        //---------------------- GET ELEMENTS FROM LAYOUT --------------------------//
        TextView tv = (TextView) findViewById(R.id.textView2);
        TextView effectText = (TextView) findViewById(R.id.textView9);
        colorPickerView = findViewById(R.id.colorPickerView);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroupI);
        radio1 = (RadioButton) findViewById(R.id.radioButton1I);
        radio2 = (RadioButton) findViewById(R.id.radioButton2I);
        radio3 = (RadioButton) findViewById(R.id.radioButton3I);
        radio4 = (RadioButton) findViewById(R.id.radioButton4I);
        radio5 = (RadioButton) findViewById(R.id.radioButton5I);
        radio6 = (RadioButton) findViewById(R.id.radioButton6I);

        seekSlide = (SeekBar) findViewById(R.id.seekBarSlideI);
        seekTime = (SeekBar) findViewById(R.id.seekBarTimeI);

        switchC1 = (Switch) findViewById(R.id.switch1I);
        switchC2 = (Switch) findViewById(R.id.switch2I);
        switchC1.setChecked(true);
        switchC2.setClickable(false);

        //-------------------CALANDRE----------------//
        radioGroupCalandre = (RadioGroup) findViewById(R.id.radioGroupCalandre);
        radio1Cal = (RadioButton) findViewById(R.id.radioButton1Calandre);
        radio2Cal = (RadioButton) findViewById(R.id.radioButton2Calandre);
        switchCalandre = (Switch) findViewById(R.id.switchCalandre);
        //--------------------------------------------------------------------------//
        /*.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(int color, boolean fromUser) {
                Log.e("JLMZ51 listener : ", "listenerrrrr !!!! color : "+color);
            }
        });*/
        radioGroupCalandre.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(switchCalandre.isChecked()){
                    String msg;
                    switch (checkedId) {
                        case R.id.radioButton1Calandre:
                            Log.e("JLMZ51", "button Police !!");
                            //Envoyer message Allumer en mode Police
                            msg = "Int:Cal:Police:";
                            connectedThread.write(msg.getBytes(StandardCharsets.UTF_8));
                            break;
                        case R.id.radioButton2Calandre:
                            Log.e("JLMZ51", "button Synchro !!");
                            //Envoyer message Synchroniser
                            msg = "Int:Cal:Synchro:";
                            connectedThread.write(msg.getBytes(StandardCharsets.UTF_8));
                            break;
                        default:
                            break;
                    }
                }else{
                    //Envoyer message Extinction au cas ou
                    String msg = "Int:Cal:TurnOff:";
                    connectedThread.write(msg.getBytes(StandardCharsets.UTF_8));
                }
            }
        });
        for (int i =0; i<radioGroupCalandre.getChildCount();i++){
            ((RadioButton)radioGroupCalandre.getChildAt(i)).setEnabled(false);
        }

        switchCalandre.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                for (int i =0; i<radioGroupCalandre.getChildCount();i++){
                    ((RadioButton)radioGroupCalandre.getChildAt(i)).setEnabled(isChecked);
                }
                if(isChecked){
                    //Si c'est check on allume,
                    //Envoyer message : Autoriser l'allumage de la calandre
                    String msg = "Int:Cal:TurnOn:";
                    connectedThread.write(msg.getBytes(StandardCharsets.UTF_8));
                    Log.e("JLMZ51","CHeck calandre activate");
                }else{
                    //Sinon on éteint
                    //Envoyer message Eteindre Calandre !PrIoRiTaIrE! URGENT !!
                    radioGroupCalandre.clearCheck();
                    String msg = "Int:Cal:TurnOff:";
                    connectedThread.write(msg.getBytes(StandardCharsets.UTF_8));
                    Log.e("JLMZ51", "Check calandre désactivé");
                }
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.e("JLMZ510","Radio changed : "+checkedId);
                RadioButton rbut = (RadioButton) findViewById(checkedId);
                effectText.setText(rbut.getText());
                String msg;
                switch(checkedId) {
                    case R.id.radioButton1I:
                        //Case "Couleur Simple"
                        switchC2.setChecked(false);
                        switchC2.setClickable(false);
                        msg = "Int:Type:SimpleColor:";
                        connectedThread.write(msg.getBytes(StandardCharsets.UTF_8));
                        break;
                    case R.id.radioButton2I:
                        //Case "Slide 1 couleur"
                        msg = "Int:Type:Slide1:";
                        switchC2.setChecked(false);
                        switchC2.setClickable(false);
                        connectedThread.write(msg.getBytes(StandardCharsets.UTF_8));
                        break;
                    case R.id.radioButton3I:
                        //Case "Slide 2 couleurs"
                        msg = "Int:Type:Slide2:";
                        switchC2.setClickable(true);
                        connectedThread.write(msg.getBytes(StandardCharsets.UTF_8));
                        break;
                    case R.id.radioButton4I:
                        //Case "Snke 2 couleurs"
                        msg = "Int:Type:Snake2:";
                        switchC2.setClickable(true);
                        connectedThread.write(msg.getBytes(StandardCharsets.UTF_8));
                        break;
                    case R.id.radioButton5I:
                        //Case "Rainbow degrade"
                        msg = "Int:Type:DegradeRainow:";
                        switchC2.setClickable(false);
                        connectedThread.write(msg.getBytes(StandardCharsets.UTF_8));
                        break;
                    case R.id.radioButton6I:
                        //Case "degrade"
                        msg = "Int:Type:Degrade:";
                        switchC2.setClickable(false);
                        connectedThread.write(msg.getBytes(StandardCharsets.UTF_8));
                        break;
                    case R.id.radioButton7I:
                        //Case "degrade"
                        msg = "Int:Type:Stars:";
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
                String msg = "Int:Timer:"+times[progress]+":";
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
                String msg="Int:SlideSize:"+diviseurs[progress]+":";
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
                    colors = "Int:Color:Color1:"+envelope.getArgb()[1]+"/"+envelope.getArgb()[2]+"/"+envelope.getArgb()[3]+"/";
                }else{
                    //Sinon c'est forcément couleur 2 :
                    //Envoyer un message avec la couleur 2
                    colors = "Int:Color:Color2:"+envelope.getArgb()[1]+"/"+envelope.getArgb()[2]+"/"+envelope.getArgb()[3]+"/";
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

            //connectedThread.write("Interior".getBytes(StandardCharsets.UTF_8));
        } catch(Exception e){
            Log.i("JLMZ51 MainActivity", "Transmission Error");
        }
        //---------------------------------------------------------------------------------------------//
    }

    public void turnOffLedI(View view){
        connectedThread.write("Int:Type:TurnOff:".getBytes(StandardCharsets.UTF_8));
    }

    public void changeText(View view){
        TextView tv = (TextView) findViewById(R.id.textView2);
        ColorEnvelope colorEnvelope = colorPickerView.getColorEnvelope();
        int[] color = colorEnvelope.getArgb();
        tv.setText("rgb  =  idk : "+color[0]+" // r : "+color[1]+" // g : "+color[2] + " // b : "+color[3]);
        sendColor(view);
    }
    public void sendColor(View view){
        ColorEnvelope colorEnvelope = colorPickerView.getColorEnvelope();
        int[] color = colorEnvelope.getArgb();
        String colors = color[1]+"/"+color[2]+"/"+color[3]+"/";
        //connectedThread.write(colors.getBytes(StandardCharsets.UTF_8));
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