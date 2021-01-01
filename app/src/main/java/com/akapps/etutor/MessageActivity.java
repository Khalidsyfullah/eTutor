package com.akapps.etutor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MessageActivity extends AppCompatActivity {
    TextView text_det, back_arrow, txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        text_det = findViewById(R.id.textView60);
        back_arrow = findViewById(R.id.textView49);
        txt = findViewById(R.id.textView68);
        getSupportFragmentManager().beginTransaction().add(R.id.frame_lay, new Messages()).commit();
        back_arrow.setOnClickListener(v -> backHandler());

    }

    void backHandler()
    {
        Messages messages = (Messages) getSupportFragmentManager().getFragments().get(0);
        if(messages.getStat()){
            messages.backPressController();
        }
        else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        backHandler();
    }

    public void hideBar(boolean iop)
    {
        if(iop){
            back_arrow.setVisibility(View.GONE);
            text_det.setVisibility(View.GONE);
            txt.setVisibility(View.GONE);
        }
        else {
            back_arrow.setVisibility(View.VISIBLE);
            text_det.setVisibility(View.VISIBLE);
            txt.setVisibility(View.VISIBLE);
        }
    }

    public void startActivityVB()
    {
        Intent intent = new Intent(MessageActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
}