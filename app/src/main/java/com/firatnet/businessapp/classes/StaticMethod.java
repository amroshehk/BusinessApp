package com.firatnet.businessapp.classes;

import android.app.Activity;
import android.content.Context;

public class StaticMethod {


  public   static  boolean ConnectChecked(Context context)
    {

        boolean online = ConnectionStatus.isNetworkAvailable( context ) &&
                ConnectionStatus.isOnline();

        return online;
    }
}
