package com.sam_chordas.android.stockhawk.service;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.WidgetActivity;

import java.util.ArrayList;

/**
 * Created by brianm on 03/07/2016.
 */
public class ListViewWidgetService extends RemoteViewsService {
  public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
    return new ListViewRemoteViewsFactory(this.getApplicationContext(), intent);
  }
}

class ListViewRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

  private Context mContext;

  private ArrayList<String> recordNames;
  private ArrayList<String> recordPrices;
  private ArrayList<String> recordChangeValues;

  private Cursor cursor;

  private final static String CHANGE_COLUMN_NAME = "symbol";
  private final static String BID_PRICE_COLUMN_NAME = "bid_price";
  private final static String SYMBOL_COLUMN_NAME = "symbol";

  public ListViewRemoteViewsFactory(Context context, Intent intent) {
    mContext = context;
  }

  public void onCreate() {
    recordNames = new ArrayList<>();
    recordPrices = new ArrayList<>();
    recordChangeValues = new ArrayList<>();
  }

  public RemoteViews getViewAt(int position) {

    RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

    rv.setTextViewText(R.id.stock_symbol, recordNames.get(position));
    rv.setTextViewText(R.id.bid_price, recordPrices.get(position));
    rv.setTextViewText(R.id.change, recordChangeValues.get(position));

    Bundle extras = new Bundle();

    extras.putInt(WidgetActivity.EXTRA_ITEM, position);

    Intent fillInIntent = new Intent();

    rv.setOnClickFillInIntent(R.id.item_layout, fillInIntent);

    return rv;
  }


  public void onDataSetChanged() {
    Thread thread = new Thread() {
      public void run() {
        getValuesFromDB();
      }
    };
    thread.start();
    try {
      thread.join();
    } catch (InterruptedException e) {
    }
  }

  public int getViewTypeCount() {
    return recordNames.size();
  }

  public long getItemId(int position) {
    return position;
  }

  public void onDestroy() {
  }

  @Override
  public int getCount() {
    return recordNames.size();
  }

  public boolean hasStableIds() {
    return true;
  }

  public RemoteViews getLoadingView() {
    return null;
  }

  public void getValuesFromDB() {
    try {
      cursor = mContext.getContentResolver()
        .query(QuoteProvider.Quotes.getAll(), null, null, null, null);

      if (cursor.moveToFirst()) {
        cursor.moveToNext();
        while (cursor.isAfterLast() == false) {

          String name = cursor.getString(cursor
            .getColumnIndex(SYMBOL_COLUMN_NAME));
          String price = cursor.getString(cursor
            .getColumnIndex(BID_PRICE_COLUMN_NAME));
          String change = cursor.getString(cursor
            .getColumnIndex(CHANGE_COLUMN_NAME));

          if (!doesRecordExist(name)) {
            recordNames.add(name);
            recordPrices.add(price);
            recordChangeValues.add(change);
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

  public boolean doesRecordExist(String name) {
    for (int i = 0; i < recordNames.size(); i++) {
      if (recordNames.get(i).toLowerCase().equals(name.toLowerCase())) {
        return true;
      }
    }
    return false;
  }
}
