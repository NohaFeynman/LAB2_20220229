package com.example.lab2_20220229.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ValidacionConexion {

    public static boolean hayConexionInternet(Context contexto) {
        ConnectivityManager cm = (ConnectivityManager)
                contexto.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo red = cm.getActiveNetworkInfo();
            return red != null && red.isConnected();
        }

        return false;
    }
}