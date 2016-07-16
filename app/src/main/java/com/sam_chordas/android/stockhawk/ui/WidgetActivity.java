package com.sam_chordas.android.stockhawk.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.ListViewWidgetService;
/**
 * Created by brianm on 02/07/2016.
 */
public class WidgetActivity extends AppWidgetProvider {
  public static final String UPDATE_MEETING_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";

  public static final String EXTRA_ITEM = "com.example.edockh.EXTRA_ITEM";


  public void onReceive(Context context, Intent intent) {

    AppWidgetManager mgr = AppWidgetManager.getInstance(context);

    if (intent.getAction().equals(UPDATE_MEETING_ACTION)) {

      int appWidgetIds[] = mgr.getAppWidgetIds(new ComponentName(context, WidgetActivity.class));

      mgr.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);



    }

    super.onReceive(context, intent);

  }

  public void onUpdate(Context context, AppWidgetManager appWidgetManager,

                       int[] appWidgetIds) {

    for (int i = 0; i < appWidgetIds.length; ++i) {

      Intent intent = new Intent(context, ListViewWidgetService.class);

      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

      intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

      RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

      rv.setRemoteAdapter(R.id.widget_list_view, intent);

      Intent startActivityIntent = new Intent(context, MyStocksActivity.class);
      startActivityIntent.setAction(UPDATE_MEETING_ACTION);

      PendingIntent startActivityPendingIntent =
        PendingIntent.getActivity(context, 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

      rv.setPendingIntentTemplate(R.id.widget_list_view, startActivityPendingIntent);

      appWidgetManager.updateAppWidget(appWidgetIds[i], rv);

    }

    super.onUpdate(context, appWidgetManager, appWidgetIds);

  }

}
