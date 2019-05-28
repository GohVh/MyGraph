package com.example.mygraph;

import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class Plot {

    public void graphInit(LineChart myChart){
        myChart.setNoDataText("No data for the moment");
        myChart.setHighlightPerTapEnabled(true);
        myChart.setTouchEnabled(true);
        myChart.setDragEnabled(true);
        myChart.setScaleEnabled(true);
        myChart.setDrawGridBackground(false);
        myChart.setPinchZoom(true);
        myChart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        myChart.setData(data);

        Legend l = myChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = myChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);

        YAxis yl = myChart.getAxisLeft();
        yl.setTextColor(Color.WHITE);
        yl.setAxisMaxValue(60f); //max y range in graph
        yl.setAxisMinValue(0f); //max x range in graph
        yl.setDrawGridLines(true);

        YAxis yl2 = myChart.getAxisRight();
        yl2.setEnabled(false);
    }

    public void addEntry(LineChart myChart, ArrayList<Float> myFloat) {
        LineData data = myChart.getData();

        if (data != null) {
            LineDataSet set = (LineDataSet)data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            if(myFloat==null){
                Log.d("Plot: ","No previous recorded data");
            }else {
                for (int i=0; i<myFloat.size();i++){
                    data.addEntry(new Entry(set.getEntryCount(), (float)myFloat.get(i)), 0);
                }
            }
        }


        myChart.notifyDataSetChanged();
        myChart.setVisibleXRangeMaximum(600);
        myChart.moveViewToX(data.getEntryCount());

    }

    private LineDataSet createSet(){
        LineDataSet set = new LineDataSet(null,"Temperature Reading");
        set.setDrawCircles(false);
        set.setCubicIntensity(0);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setCircleSize(0);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 177));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(10f);

        return set;
    }

    public void clearGraph(LineChart myChart) {
        myChart.clearValues();
        graphInit(myChart);
    }

}
