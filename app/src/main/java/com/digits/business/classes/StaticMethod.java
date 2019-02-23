package com.digits.business.classes;

import android.content.Context;

public class StaticMethod {


  public   static  boolean ConnectChecked(Context context)
    {

        boolean online = ConnectionStatus.isNetworkAvailable( context );
//                &&  ConnectionStatus.isOnline();

        return online;
    }



}
