package com.example.mygraph;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class BTDialog1 extends AppCompatDialogFragment {

    ListView listViewDevice;
    static ArrayList<BluetoothDevice> BTArrayListsPaired;
    static ArrayAdapter BTArrayAdapterPaired;
    static Set<BluetoothDevice> pairedDevices;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BTArrayListsPaired = new ArrayList();

        pairedDevices = MainActivity.myBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size()!=0){
            for (BluetoothDevice device:pairedDevices){
                BTArrayListsPaired.add(device);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_scan,null);
        listViewDevice = view.findViewById(R.id.listViewItem);
        BTArrayAdapterPaired = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,BTArrayListsPaired);
        listViewDevice.setAdapter(BTArrayAdapterPaired);

        listViewDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),BTArrayListsPaired.get(position).toString(),Toast.LENGTH_SHORT).show();
                BTDialog2.myBTDevice = BTArrayListsPaired.get(position);
                BTDialog2.myBluetoothService = new BluetoothService(getActivity());
                BTDialog2.Connect();
                MainActivity.connectState =1;
                MainActivity.buttonStop.setEnabled(true);
                dismissDialog();
            }
        });

        builder.setView(view)
                .setTitle("Connect to Bluetooth Device")
                .setPositiveButton("Scan for other device", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openScan();
                    }
                });

        return builder.create();
    }

    private void openScan() {
        BTDialog2 scan = new BTDialog2();
        scan.show(getFragmentManager(),"scan dialog");
    }

    public void dismissDialog(){
        getFragmentManager().beginTransaction().remove(BTDialog1.this).commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
