package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;

public class ViewStockActivity extends Activity {

  String stockSymbol;
  TextView title;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_stock);

    title = (TextView) findViewById(R.id.stock);

    Bundle extras = getIntent().getExtras();
    stockSymbol = extras.getString(MyStocksActivity.STOCK_POSITION);

    title.setText(stockSymbol);

  }

}
