package com.balacsoft.balacmemory;

import com.balacsoft.balacmemory.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;


public class MyWidgetIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("BalacMemory", "OnReceive");
		if(intent.getAction().equals("com.balacmemory.intent.action.REFRESH")){
			updateWidgetPictureAndButtonListener(context);
		}
	}

	private void updateWidgetPictureAndButtonListener(Context context) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.balacmemory);
		Log.d("BalacMemory", "OnReceive - UPDATE");

		// Update values
		remoteViews.setTextViewText(R.id.textView_RAM, MyWidgetProvider.getAvailableMemory(context));
		remoteViews.setTextViewText(R.id.textView_FLASH, MyWidgetProvider.getAvailableFlash());
		remoteViews.setTextViewText(R.id.textView_SD, MyWidgetProvider.getAvailableExternalMemorySize(context));
		remoteViews.setTextViewText(R.id.textView_RamTotal, MyWidgetProvider.getTotalMemory(context));
		remoteViews.setTextViewText(R.id.textView_FlashTotal, MyWidgetProvider.getTotalFlash());
		remoteViews.setTextViewText(R.id.textView_SdTotal, MyWidgetProvider.getTotalExternalMemorySize(context));

		//REMEMBER TO ALWAYS REFRESH YOUR BUTTON CLICK LISTENERS!!!
		remoteViews.setOnClickPendingIntent(R.id.widget_button, MyWidgetProvider.buildButtonPendingIntent(context));
		MyWidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
	}

}
