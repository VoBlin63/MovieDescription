package ru.buryachenko.moviedescription.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class NetworkStatusCheck extends BroadcastReceiver {

    private NetworkState typeConnect = NetworkState.NO;

    public enum NetworkState {WIFI, MOBILE, NO}

    @Override
    public void onReceive(Context context, Intent intent) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT < 23) {
                final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null) {
                    if (networkInfo.isConnected()) {
                        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                            typeConnect = NetworkState.WIFI;
                        }
                        if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                            typeConnect = NetworkState.MOBILE;
                        }
                    }
                }
            } else {
                final Network n = connectivityManager.getActiveNetwork();

                if (n != null) {
                    final NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(n);
                    if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        typeConnect = NetworkState.MOBILE;
                    }
                    if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        typeConnect = NetworkState.WIFI;
                    }
                }
            }
            AppLog.write("Network connect type is " + typeConnect.name());
        }
    }

    public NetworkState getTypeConnect() {
        return typeConnect;
    }
}
