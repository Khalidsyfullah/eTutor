package com.akapps.etutor;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class AlertgameQuit extends Dialog {
    Activity activity;
    String str;

    public AlertgameQuit(@NonNull Context context, String str) {
        super(context);
        this.activity = (Activity) context;
        this.str = str;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_alertgamequit);
        TextView textView = findViewById(R.id.textView99);
        Button bt1 = findViewById(R.id.button55);
        Button bt2 = findViewById(R.id.button54);
        bt1.setOnClickListener(v -> activity.finish());
        bt2.setOnClickListener(v -> dismiss());
        this.setCancelable(false);
        textView.setText(str);
    }

}
