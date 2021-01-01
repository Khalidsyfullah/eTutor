package com.akapps.etutor;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Objects;

public class CenterPage extends AppCompatActivity {
    TabLayout tabLayout;
    TextView msg_text, msg_count, progress_show, back_text;
    int mode = 0;
    int t_count = 0;
    int message_unseen = 0;
    SharedPreferences sharedPreferences;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_page);
        tabLayout = findViewById(R.id.tabLayout);
        msg_text = findViewById(R.id.textView29);
        msg_count = findViewById(R.id.textView30);
        back_text = findViewById(R.id.textView32);
        progress_show = findViewById(R.id.textView6);
        sharedPreferences = this.getSharedPreferences("com.akapps.etutor", Context.MODE_PRIVATE);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            mode = bundle.getInt("value", 0);
        }
        if(mode == 0) progress_show.setText(R.string.g16);
        else progress_show.setText(R.string.g17);


        getSupportFragmentManager().beginTransaction().add(R.id.frame, new Posts()).commit();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (tab.getPosition()){
                    case 0:
                        if(mode == 0) progress_show.setText(R.string.g16);
                        else progress_show.setText(R.string.g17);
                        fragmentTransaction.replace(R.id.frame, new Posts());
                        fragmentTransaction.commit();
                        t_count = 0;
                        break;
                    case 1:
                        progress_show.setText(R.string.h1);
                        fragmentTransaction.replace(R.id.frame, new CreatePost());
                        fragmentTransaction.commit();
                        t_count = 1;
                        break;
                    case 2:
                        progress_show.setText(R.string.h2);
                        fragmentTransaction.replace(R.id.frame, new Profile());
                        fragmentTransaction.commit();
                        t_count = 2;
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        msg_text.setOnClickListener(v -> startActivity(new Intent(CenterPage.this, MessageActivity.class)));

        back_text.setOnClickListener(v -> backButtonAction());


    }


    public void startMessage()
    {
        startActivity(new Intent(CenterPage.this, MessageActivity.class));
    }

    void checkVal(String vt)
    {
        myRef.child("Chats").child(vt).child("MsgCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) return;
                String dfgh = sharedPreferences.getString(vt, "pp");
                String pl = Objects.requireNonNull(snapshot.getValue()).toString();
                if(dfgh.equals("pp") || Integer.parseInt(dfgh)> Integer.parseInt(pl)){
                    message_unseen++;
                    msg_count.setText(String.valueOf(message_unseen));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void loadMessageStatus()
    {
        message_unseen = 0;
        msg_count.setText("");
        myRef.child("Messages").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Inbox").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    return;
                }
                Map<String, String> map = (Map<String, String>) snapshot.getValue();
                if(map == null){
                    return;
                }
                for (Map.Entry<String, String> entry : map.entrySet()){
                    String[] tui = entry.getValue().split("#");
                    checkVal(tui[1]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMessageStatus();
    }

    @Override
    public void onBackPressed() {
        backButtonAction();
    }


    void backButtonAction()
    {
        if(t_count == 0){
            Posts posts= (Posts) getSupportFragmentManager().getFragments().get(0);
            if(posts.returnLoadStatus()){
                posts.gobackButton();
                if(mode == 0) progress_show.setText(R.string.g16);
                else progress_show.setText(R.string.g17);
            }
            else {
                String sy = "Are You Sure To Exit The App?";
                AlertgameQuit alertgameQuit = new AlertgameQuit(CenterPage.this, sy);
                alertgameQuit.show();
            }
        }
        else if(t_count == 1){
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            if(tab!=null) tab.select();
            if(mode == 0) progress_show.setText(R.string.g16);
            else progress_show.setText(R.string.g17);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, new Posts());
            fragmentTransaction.commit();
            t_count = 0;

        }
        else if(t_count == 2){
            Profile profile = (Profile) getSupportFragmentManager().getFragments().get(0);
            if(profile.getIndicator()){
                setTextValue("My Profile");
                profile.goLastpage();
            }
            else {
                TabLayout.Tab tab = tabLayout.getTabAt(1);
                if(tab!=null) tab.select();
                progress_show.setText(R.string.h1);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, new CreatePost());
                fragmentTransaction.commit();
                t_count = 1;
            }
        }

        else if(t_count == 3){
            MyPosts myPosts = (MyPosts) getSupportFragmentManager().getFragments().get(0);
            if(myPosts.gePagestatus()){
                myPosts.backState();
                progress_show.setText(R.string.h17);
            }
            else {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, new Profile());
                fragmentTransaction.commit();
                progress_show.setText(R.string.h2);
                t_count = 2;
            }
        }
    }

    public void logoutApp()
    {
        String s1 = "Sure To Logout From App?\nYou've To Login Again Into The App.";
        AlertLogout alertLogout = new AlertLogout(CenterPage.this, s1);
        alertLogout.show();
    }



    public void onMapsSelectClicked()
    {
        startActivity(new Intent(CenterPage.this, MapsActivity.class));
    }

    public int getMode()
    {
        return mode;
    }

    public void setTextValue(String text)
    {
        progress_show.setText(text);
    }


    public void startMypost()
    {
        progress_show.setText(R.string.h17);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, new MyPosts());
        fragmentTransaction.commit();
        t_count = 3;
    }

    void alertLogoutLL()
    {
        mAuth.signOut();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(CenterPage.this, MainEntrypage.class));
        finish();
    }


    public class AlertLogout extends Dialog {
        String str;

        public AlertLogout(@NonNull Context context, String str) {
            super(context);
            this.str = str;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.popup_logoutapp);
            TextView textView = findViewById(R.id.textView99);
            Button bt1 = findViewById(R.id.button55);
            Button bt2 = findViewById(R.id.button54);
            bt1.setOnClickListener(v -> alertLogoutLL());
            bt2.setOnClickListener(v -> dismiss());
            this.setCancelable(false);
            textView.setText(str);
        }
    }
}