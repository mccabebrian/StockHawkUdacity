package com.sam_chordas.android.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;

public class ViewStockActivity extends Activity {

  String stockSymbol;
  TextView title;
  LineChart chart;
  ListView stocksList;
  ArrayList<String> changeValues = new ArrayList<>();
  Cursor cursor;
  ArrayList<Entry> valsComp1 = new ArrayList<Entry>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_stock);

    title = (TextView) findViewById(R.id.stock);
    chart = (LineChart) findViewById(R.id.chart);
    stocksList = (ListView) findViewById(R.id.stock_history);

    Bundle extras = getIntent().getExtras();
    stockSymbol = extras.getString(MyStocksActivity.STOCK_POSITION);

    title.setText(stockSymbol);

    cursor = null;

    getValuesFromDB();

    intializeGraph();

    populateHistoryList();
  }

  public void getValuesFromDB(){
    try {
      String[] proj = { "bid_price" };
      cursor = getApplicationContext().getContentResolver()
        .query(QuoteProvider.Quotes.withSymbol(stockSymbol), proj, null, null, null);

      if (cursor.moveToFirst()) {

        while (cursor.isAfterLast() == false) {
          String name = cursor.getString(cursor
            .getColumnIndex("bid_price"));
          if(changeValues.size() >1) {
            if (!(changeValues.get(changeValues.size() - 1).equals(name))) {
              changeValues.add(name);
            }
          }else{
            changeValues.add(name);
          }
          cursor.moveToNext();
        }
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
  }

  public void addStockData(){
    for (int i = 0; i < changeValues.size(); i++){
      String currentValue = changeValues.get(i);
        Float changefloat = Float.parseFloat(currentValue);
        Entry entry = new Entry(changefloat, i);
        valsComp1.add(entry);
    }
  }

  public void intializeGraph(){
    addStockData();

    LineDataSet setComp1 = new LineDataSet(valsComp1, stockSymbol);
    setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);

    ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
    dataSets.add(setComp1);

    ArrayList<String> xVals = new ArrayList<String>();
    for(int i = 0; i<changeValues.size(); i++) {
      xVals.add(i + ".Q");
    }
    XAxis leftAxis = chart.getXAxis();
    leftAxis.setEnabled(false);
    LineData data = new LineData(xVals, dataSets);
    chart.setData(data);
    chart.animateX(3000);
    chart.setPinchZoom(true);
    chart.setDragEnabled(true);
    chart.invalidate();
  }

  public void populateHistoryList(){
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
      android.R.layout.simple_list_item_1, android.R.id.text1, changeValues);

    stocksList.setAdapter(adapter);
  }

}
