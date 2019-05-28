package com.example.mygraph;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
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

import androidx.annotation.RequiresApi;

public class PlotDialog1 extends AppCompatDialogFragment {

    ListView listViewOption;
    TextView textView;
    String[] optionLists = {"Plot latest record", "Select previous record for plot"};
    ArrayAdapter arrayAdapter;

    public void dismissDialog(){
        getFragmentManager().beginTransaction().remove(PlotDialog1.this).commit();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_scan,null);
        listViewOption = view.findViewById(R.id.listViewItem);
        textView = view.findViewById(R.id.textV);
        textView.setText("Options");

        arrayAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,optionLists);
        listViewOption.setAdapter(arrayAdapter);

        listViewOption.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){

                    Cursor result = MainActivity.myDb.getLatestData();
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

                }else if (position == 1){

                    selectOtherRecordDialog();
                    dismissDialog();
                }

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

    private void selectOtherRecordDialog(){
        PlotDialog2 otherRecord = new PlotDialog2();
        otherRecord.show(getFragmentManager(),"other record dialog");
    }

}
