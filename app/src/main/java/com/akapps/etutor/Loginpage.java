package com.akapps.etutor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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

public class Loginpage extends AppCompatActivity {
    TextView tx1, tx2;
    EditText ed1, ed2;
    boolean passwordmarker = true;
    String s1 = "Sure To Exit This Page?\nAll the Changes You've Made will be Reset.";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    String email;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);
        tx1 = findViewById(R.id.textView4);
        tx2 = findViewById(R.id.textView16);
        ed1 = findViewById(R.id.editText);
        ed2 = findViewById(R.id.editText2);


        try {
            ed2.setOnTouchListener((v, event) -> {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (ed2.getRight() - ed2.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(passwordmarker){
                            ed2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_showpassword, 0);
                            ed2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            ed2.setSelection(ed2.getText().length());
                            passwordmarker = false;
                        }else {
                            ed2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_hidepassword, 0);
                            ed2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            ed2.setSelection(ed2.getText().length());
                            passwordmarker = true;
                        }
                        return true;
                    }
                }
                return false;
            });
        }catch (Exception ignored){

        }

        tx2.setOnClickListener(v-> showAlert());

    }

    void showAlert()
    {
        if(ed1.getText().toString().isEmpty() && ed2.getText().toString().isEmpty()) {
            startActivity(new Intent(Loginpage.this, MainEntrypage.class));
            finish();
        }
        else {
            AlertClass alertClass = new AlertClass(Loginpage.this, s1);
            alertClass.show();
        }
    }

    @Override
    public void onBackPressed() {
        showAlert();
    }

    public void onForgetPasswordClicked(View view) {
        VerifyClass verifyClass = new VerifyClass(Loginpage.this);
        verifyClass.show();
    }

    public void onLoginClicked(View view) {
        String email = ed1.getText().toString();
        String password = ed2.getText().toString();
        if(email.isEmpty()){
            ed1.setError("Email is Required!");
            return;
        }
        if(password.isEmpty()){
            ed2.setError("Password is Required!");
            return;
        }
        ProgressScreen progressScreen = new ProgressScreen(Loginpage.this);
        progressScreen.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            if(!Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified()){
                progressScreen.dismiss();
                String stry = "Your Email Hasn't Verified Yet. Please Check your Inbox & Verify First. You can request another email.";
                PopWindow popWindow = new PopWindow(Loginpage.this, stry, 2);
                popWindow.show();
            }else {
                myRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("Type").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressScreen.dismiss();
                        if(!snapshot.exists()){
                            return;
                        }
                        String val = Objects.requireNonNull(snapshot.getValue()).toString();
                        Intent intent = new Intent(Loginpage.this, CenterPage.class);
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
                        PopWindow popWindow = new PopWindow(Loginpage.this, stre, 0);
                        popWindow.show();
                    }
                });
            }
        }).addOnFailureListener(e -> {
            progressScreen.dismiss();
            String sdfg = "Login Failed! "+e.getMessage();
            PopWindow popWindow = new PopWindow(Loginpage.this, sdfg, 0);
            popWindow.show();
        });
    }

    public void sendResetEmail()
    {
        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(aVoid -> {
            String str = "A Password Reset Email Has been Sent To Your Email. Please Check Your Email";
            PopWindow popWindow = new PopWindow(Loginpage.this, str, 0);
            popWindow.show();
        }).addOnFailureListener(e -> {
            String str = "Failed! "+e.getMessage();
            PopWindow popWindow = new PopWindow(Loginpage.this, str, 0);
            popWindow.show();
        });
    }


    public void resendVerification()
    {
        if(mAuth.getCurrentUser()==null){
            return;
        }
        mAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(aVoid -> {
            String srtr = "A Verification Mail Has Been Sent To Your Email. Please Check Your Inbox.";
            PopWindow popWindow = new PopWindow(Loginpage.this, srtr, 0);
            popWindow.show();
        }).addOnFailureListener(e -> {
            String str = "Failed! "+e.getMessage();
            PopWindow popWindow = new PopWindow(Loginpage.this, str, 0);
            popWindow.show();
        });
    }

    void startAct()
    {
        startActivity(new Intent(Loginpage.this, MainEntrypage.class));
        finish();
    }

    public class VerifyClass extends Dialog{

        public VerifyClass(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.popup_sendresetmail);
            Button bt = findViewById(R.id.button11);
            Button bt1 = findViewById(R.id.button12);
            EditText ed = findViewById(R.id.editText11);
            this.setCancelable(false);
            bt.setOnClickListener(v -> {
                email = ed.getText().toString();
                if(email.isEmpty()){
                    ed.setError("Required!");
                    return;
                }
                sendResetEmail();
                dismiss();
            });
            bt1.setOnClickListener(v -> dismiss());
        }
    }

    public class AlertClass extends Dialog{
        String str;
        public AlertClass(@NonNull Context context, String str) {
            super(context);
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
            bt1.setOnClickListener(v ->{
                startAct();
                dismiss();
            });

            bt2.setOnClickListener(v -> dismiss());
            this.setCancelable(false);
            textView.setText(str);
        }
    }
}