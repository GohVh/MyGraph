package com.example.mygraph;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PlotDialog2 extends AppCompatDialogFragment {

    ListView listViewRecord;
    TextView textView;
    ArrayAdapter arrayAdapter;
    ArrayList<String> recordDate;

    public void dismissDialog(){
        getFragmentManager().beginTransaction().remove(PlotDialog2.this).commit();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_scan,null);

        recordDate = new ArrayList<>();
        listViewRecord = view.findViewById(R.id.listViewItem);
        textView = view.findViewById(R.id.textV);
        textView.setText("Select record based on date and time\nLong click to delete record");

        Cursor result = MainActivity.myDb.getDateList();
        if (result.getCount() == 0){
            Toast.makeText(getActivity(),"Error: No data",Toast.LENGTH_SHORT).show();
            dismissDialog();
        }

        if (result.moveToFirst()){
            recordDate.add(result.getString(result.getColumnIndex("DATE")));
            while(result.moveToNext()){
                recordDate.add(result.getString(result.getColumnIndex("DATE")));
            }
        }

        arrayAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,recordDate);
        listViewRecord.setAdapter(arrayAdapter);

        listViewRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor result = MainActivity.myDb.getData(recordDate.get(position));
                if (result.getCount() == 0){
                    Toast.makeText(getActivity(),"Error: No data",Toast.LENGTH_SHORT).show();
                    dismissDialog();
                    return;
                }

                StringBuffer bufferRecord = new StringBuffer();

                while (result.moveToNext()){
                    bufferRecord.append(result.getString(0));
                }

                MainActivity.recordedData = new ArrayList<>();

                for (String s : bufferRecord.toString().split("\n")) {
                    if (s!=""){
                        MainActivity.recordedData.add(Float.parseFloat(s));
                    }else {
                        Toast.makeText(getActivity(),"Empty record", Toast.LENGTH_SHORT).show();
                    }
                }

                MainActivity.myPlot.graphInit(MainActivity.mChart);
                MainActivity.myPlot.addEntry(MainActivity.mChart,MainActivity.recordedData);

                dismissDialog();
            }
        });

        listViewRecord.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this record?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.myDb.deleteData(recordDate.get(position));
                                recordDate.remove(position);
                                arrayAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }
        });

        builder.setView(view)
                .setCancelable(true);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
