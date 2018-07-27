package com.example.pc.androidpayrbcn;

import android.content.Context;
import android.widget.Toast;


// Обработка событий, просто показыват Toast
public class ToastNotification {

    private Context context;

    public ToastNotification(Context context) {
        this.context = context;
    }

    public void showMessage(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
