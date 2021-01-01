package com.akapps.etutor;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;


public class PopWindow extends Dialog {
    String str1;
    int bl;
    Activity activity;
    public PopWindow(@NonNull Context context, String str1, int bl) {
        super(context);
        this.str1 = str1;
        this.bl = bl;
        activity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_popup);
        TextView textView = findViewById(R.id.textView44);
        textView.setText(str1);
        Button bt1, bt2;
        bt1 = findViewById(R.id.button6);
        bt2 = findViewById(R.id.button50);
        if(bl==0) bt1.setVisibility(View.GONE);
        else if(bl == 1) bt1.setText(R.string.d8);
        else if(bl == 2) bt1.setText(R.string.d9);
        bt1.setOnClickListener(v -> {
            if(bl==1) ((MainEntrypage) activity).autoLogin();
            else if(bl == 2) ((Loginpage) activity).resendVerification();
            dismiss();
        });
        bt2.setOnClickListener(v -> dismiss());
    }
}
