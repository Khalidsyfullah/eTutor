package com.akapps.etutor;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainEntrypage extends AppCompatActivity {
    String s1 = "Sure To Exit This app?";
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_entrypage);
        sharedPreferences = this.getSharedPreferences("com.akapps.etutor", Context.MODE_PRIVATE);
        if(mAuth.getCurrentUser()!= null && mAuth.getCurrentUser().isEmailVerified()){
            String s1 = "Login From Save Status?\nEmail: "+mAuth.getCurrentUser().getEmail();
            boolean fg = sharedPreferences.getBoolean("AutoLOgin", false);
            if(fg){
                autoLogin();
                return;
            }
            PopLogin popLogin = new PopLogin(MainEntrypage.this, s1);
            popLogin.show();
        }
    }

    void showAlert()
    {
        AlertgameQuit alertgameQuit = new AlertgameQuit(MainEntrypage.this, s1);
        alertgameQuit.show();
    }

    @Override
    public void onBackPressed() {
        showAlert();
    }


    public void onTeacherRegisterClicked(View view) {
        Intent intent = new Intent(MainEntrypage.this, StudentRegister.class);
        intent.putExtra("value", 0);
        startActivity(intent);
    }

    public void onStudentRegisterClicked(View view) {
        Intent intent = new Intent(MainEntrypage.this, StudentRegister.class);
        intent.putExtra("value", 1);
        startActivity(intent);
    }

    public void autoLogin()
    {
        ProgressScreen progressScreen = new ProgressScreen(MainEntrypage.this);
        progressScreen.show();
        myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Type").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressScreen.dismiss();
                if(!snapshot.exists()){
                    return;
                }
                String val = Objects.requireNonNull(snapshot.getValue()).toString();
                Intent intent = new Intent(MainEntrypage.this, CenterPage.class);
                if(val.equals("0")){
                    intent.putExtra("value", 0);
                }
                else {
                    intent.putExtra("value", 1);
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressScreen.dismiss();
                String stre = "Database Error! "+error.getDetails();
                PopWindow popWindow = new PopWindow(MainEntrypage.this, stre, 0);
                popWindow.show();
            }
        });
    }

    public void onLoginClicked(View view) {
        startActivity(new Intent(MainEntrypage.this, Loginpage.class));
        finish();
    }



    public class PopLogin extends Dialog{
        String str1;
        public PopLogin(@NonNull Context context, String str1) {
            super(context);
            this.str1 = str1;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.layout_relogin);
            TextView textView = findViewById(R.id.textView44);
            textView.setText(str1);
            CheckBox checkBox = findViewById(R.id.checkBox);
            Button bt1, bt2;
            bt1 = findViewById(R.id.button6);
            bt2 = findViewById(R.id.button50);
            bt1.setOnClickListener(v -> {
                autoLogin();
                sharedPreferences.edit().putBoolean("AutoLOgin", checkBox.isChecked()).apply();
                dismiss();
            });

            bt2.setOnClickListener(v -> dismiss());
        }
    }
}