package com.example.mygraph;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    static TextView textViewStream;
    Button buttonPlot;
    static Button buttonStop;
    String TAG = "MainActivity";
    static int connectState;
    static public LineChart mChart;
    static public Plot myPlot;
    static ArrayList<Float> recordedData;

    static BluetoothAdapter myBluetoothAdapter;
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static DatabaseHelper myDb;

    private final BroadcastReceiver BroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(myBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, myBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    public void enableBT(){
        if(myBluetoothAdapter == null){
            Log.d(TAG, "enableBT: Does not have BT capabilities.");
        }
        if(!myBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(BroadcastReceiver1, BTIntent);

            while (!myBluetoothAdapter.isEnabled()){
                if (myBluetoothAdapter.isEnabled()){
                    openBTHelperDialog();
                }
            }
        }
    }

    public void disableBT(){
        if(myBluetoothAdapter == null){
            Log.d(TAG, "enableBT: Does not have BT capabilities.");
        }
        if(myBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: disabling BT.");
            myBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(BroadcastReceiver1, BTIntent);
        }
    }

    public void stopConnection(){
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BTDialog2.stopConnection();
                buttonStop.setEnabled(false);
            }
        });
    }

    public void selectPlot(){
        buttonPlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlotDialog();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textV);
        textViewStream = findViewById(R.id.textViewStream);
        buttonPlot = findViewById(R.id.buttonPlot);
        buttonStop = findViewById(R.id.buttonStop);
        connectState = 0;
        buttonStop.setEnabled(false);
        mChart = findViewById(R.id.graphLayout);
        myPlot = new Plot();

        myDb = new DatabaseHelper(this);

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        stopConnection();
        selectPlot();
        myPlot.graphInit(mChart);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.bluetooth_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case R.id.bluetoothMenu:

                if (myBluetoothAdapter.isEnabled()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Turning off Bluetooth?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "onClick: enabling bluetooth.");
                            disableBT();

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openBTHelperDialog();
                        }

                    });
                    builder.setCancelable(true);
                    builder.show();

                }else if (!myBluetoothAdapter.isEnabled()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Turning on Bluetooth?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "onClick: enabling bluetooth.");
                            enableBT();

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }

                    });
                    builder.setCancelable(true);
                    builder.show();
                }

                return true;
            default:
                return false;
        }
    }

    private void openBTHelperDialog() {
        BTDialog1 btDialog1 = new BTDialog1();
        btDialog1.show(getSupportFragmentManager(),"btDialog1 dialog");
    }

    private void openPlotDialog(){
        PlotDialog1 plot = new PlotDialog1();
        plot.show(getSupportFragmentManager(),"plot dialog");
    }
}
