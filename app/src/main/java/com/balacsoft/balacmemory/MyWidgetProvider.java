package com.balacsoft.balacmemory;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.File;

public class MyWidgetProvider extends AppWidgetProvider {

    static final long SIZE_KB = 1024L;
    static final long SIZE_MB = SIZE_KB * SIZE_KB;
    static final long SIZE_GB = SIZE_MB * SIZE_KB;

    @SuppressLint({"DefaultLocale", "NewApi"})
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("BalacMemory", "onUpdate - UPDATE");

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.balacmemory);
        // remoteViews.setOnClickPendingIntent(R.id.widget_button, buildButtonPendingIntent(context));

        // Register an onClickListener
        Intent intent = new Intent(context, MyWidgetProvider.class);

        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_button, pendingIntent);
        ComponentName myWidget = new ComponentName(context, MyWidgetProvider.class);
        appWidgetManager.updateAppWidget(myWidget, remoteViews);

        // Update content
        remoteViews.setTextViewText(R.id.textView_RAM, getAvailableMemory(context));
        remoteViews.setTextViewText(R.id.textView_FLASH, getAvailableFlash());
        remoteViews.setTextViewText(R.id.textView_SD, getAvailableExternalMemorySize(context));
        remoteViews.setTextViewText(R.id.textView_RamTotal, getTotalMemory(context));
        remoteViews.setTextViewText(R.id.textView_FlashTotal, getTotalFlash());
        remoteViews.setTextViewText(R.id.textView_SdTotal, getTotalExternalMemorySize(context));

        // Update remote display
        pushWidgetUpdate(context, remoteViews);
    }

    public static PendingIntent buildButtonPendingIntent(Context context) {
        Log.d("BalacMemory", "buildButtonPendingIntent");
        Intent intent = new Intent();
        intent.setAction("com.balacmemory.intent.action.REFRESH");
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context, MyWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }

    /* ************************************************************************************************************
     * Return LONG values
     */

    public static boolean isSdCardOnDevice(Context context) {
        File[] storages = ContextCompat.getExternalFilesDirs(context, null);

        if (storages.length > 1 && storages[0] != null && storages[1] != null) {
            return true;
        } else {
            return false;
        }
    }

    public static double TotalExternalMemory(Context context) {
        File[] storages = ContextCompat.getExternalFilesDirs(context, null);
        StatFs statFs = new StatFs(storages[1].getAbsolutePath());

        double temp = statFs.getBlockCountLong() * statFs.getBlockSizeLong();
        double Total = temp / SIZE_GB;
        return Total;
    }

    public static double FreeExternalMemory(Context context) {
        File[] storages = ContextCompat.getExternalFilesDirs(context, null);
        StatFs statFs = new StatFs(storages[1].getAbsolutePath());

        double temp = statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
        double Free = temp / SIZE_GB;
        return Free;
    }

    public static String getAvailableExternalMemorySize(Context ctx) {
        String str;

        if (false == isSdCardOnDevice(ctx)) {
            str = "-";
        } else {
            str = String.format("%2.2f Go", FreeExternalMemory(ctx));
        }
        Log.d("BalacMemory", str);
        return str;
    }

    public static String getTotalExternalMemorySize(Context ctx) {
        String str;

        if (false == isSdCardOnDevice(ctx)) {
            str = "-";
        } else {
            str = String.format("%2.2f Go", TotalExternalMemory(ctx));
        }
        Log.d("BalacMemory", str);
        return str;
    }

    /**
     * @return Number of Mega bytes available on External storage
     */
    @SuppressWarnings("deprecation")
    public static long getAvailableSpaceInMB() {
        long availableSpace = -1L;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            availableSpace = (long) stat.getAvailableBlocksLong() * (long) stat.getBlockSizeLong();
        } else {
            availableSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
        }
        return (availableSpace / SIZE_MB);
    }

    @SuppressWarnings("deprecation")
    public static long getTotalExtMemory() {
        long Total;
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            Total = (long) statFs.getBlockCountLong() * (long) statFs.getBlockSizeLong();
        } else {
            Total = (long) statFs.getBlockCount() * (long) statFs.getBlockSize();
        }
        return (Total / SIZE_MB);
    }

    @SuppressWarnings("deprecation")
    private static long getAvailableSpaceInMBinternalFlash() {
        long availableSpace = -1L;
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            availableSpace = (long) stat.getAvailableBlocksLong() * (long) stat.getBlockSizeLong();
        } else {
            availableSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
        }
        return (availableSpace / SIZE_MB);
    }

    @SuppressWarnings("deprecation")
    public static long getTotalIntMemory() {
        long Total;

        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            Total = (long) statFs.getBlockCountLong() * (long) statFs.getBlockSizeLong();
        } else {
            Total = (long) statFs.getBlockCount() * (long) statFs.getBlockSize();
        }
        return (Total / SIZE_MB);
    }

    /* ************************************************************************************************************
     * Return STRING values
     */

    /**
     * @return String containing the Available Mega bytes of RAM
     */
    public static String getAvailableMemory(Context context) {
        MemoryInfo mi = new MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);

        // RAM
        long availableMegs = mi.availMem / SIZE_MB;
        String mem = String.format("%s Mo", availableMegs);
        Log.d("BalacMemory", mem);
        return mem;
    }

    /**
     * @return String containing the Total Mega bytes of RAM
     */
    public static String getTotalMemory(Context context) {
        String memTotal;

        MemoryInfo mi = new MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);

        // If API level is 16 or greater, display also total RAM
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            long totalMegs = mi.totalMem / SIZE_MB;
            memTotal = String.format("%d Mo", totalMegs);
        } else {
            memTotal = "N/A";
        }
        Log.d("BalacMemory", memTotal);
        return memTotal;
    }

    /**
     * @return String containing the available Mega bytes for internal flash storage
     */
    public static String getAvailableFlash() {
        String flash;

        // Compute flash memory
        double sdMemDouble;
        sdMemDouble = getAvailableSpaceInMBinternalFlash();
        sdMemDouble = (double) (sdMemDouble / SIZE_KB);
        flash = String.format("%2.2f Go", sdMemDouble);
        Log.d("BalacMemory", flash);

        return flash;
    }

    /**
     * @return String containing the Total Mega bytes for internal flash storage
     */
    public static String getTotalFlash() {
        Double sdMemDouble;
        String flashTotal;

        sdMemDouble = (double) getTotalIntMemory();
        sdMemDouble = (double) (sdMemDouble / SIZE_KB);
        flashTotal = String.format("%2.2f Go", sdMemDouble);
        Log.d("BalacMemory", flashTotal);

        return flashTotal;
    }

}
