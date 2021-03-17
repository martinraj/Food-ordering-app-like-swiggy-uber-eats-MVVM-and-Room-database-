package com.marty.yummy.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

//Check for internet connectivity.
public class NetworkConnection {
   public static boolean isConnected(Context context) {
      ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo wifiCon = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
      NetworkInfo mobCon = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

      return (wifiCon != null && wifiCon.isConnected()) || (mobCon != null && mobCon.isConnected());
   }
}
