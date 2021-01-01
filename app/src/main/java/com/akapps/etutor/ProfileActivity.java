package com.akapps.etutor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String uid, name;
    ImageView imageView;
    TextView no_review, pro_details, cont_info, reviews, address, education, name_text, go_back;
    TabLayout tabLayout;
    ScrollView scrollView;
    GridView gridView;
    Button review_button;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    StorageReference stRef = FirebaseStorage.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imageView = findViewById(R.id.imageView8);
        tabLayout = findViewById(R.id.tabLayout);
        name_text = findViewById(R.id.textView67);
        review_button = findViewById(R.id.button37);
        scrollView = findViewById(R.id.scroll);
        no_review = findViewById(R.id.textView72);
        pro_details = findViewById(R.id.textView69);
        cont_info = findViewById(R.id.textView71);
        address = findViewById(R.id.textView74);
        reviews = findViewById(R.id.textView73);
        education = findViewById(R.id.textView70);
        go_back = findViewById(R.id.textView9);
        reviews.setVisibility(View.GONE);
        no_review.setVisibility(View.GONE);
        gridView = findViewById(R.id.grid_mol);
        gridView.setVisibility(View.GONE);
        review_button.setVisibility(View.GONE);
        sharedPreferences = this.getSharedPreferences("com.akapps.etutor", Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("PlayerIDkk", "aa");
        name = sharedPreferences.getString("Namekkk", "aa");

        review_button.setOnClickListener(v -> {
            ProgressScreen progressScreen = new ProgressScreen(ProfileActivity.this);
            progressScreen.show();
            myRef.child("RatingReviews").child(uid).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.exists()){
                        progressScreen.dismiss();
                        PopupReview popupReview = new PopupReview(ProfileActivity.this, null);
                        popupReview.show();
                    }
                    else {
                        progressScreen.dismiss();
                        String hj = Objects.requireNonNull(snapshot.getValue()).toString();
                        PopupReview popupReview = new PopupReview(ProfileActivity.this, hj);
                        popupReview.show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        if(gridView.getVisibility()== View.VISIBLE) gridView.setVisibility(View.GONE);
                        if(reviews.getVisibility()== View.VISIBLE) reviews.setVisibility(View.GONE);
                        if(review_button.getVisibility()== View.VISIBLE) review_button.setVisibility(View.GONE);
                        if(no_review.getVisibility()== View.VISIBLE) no_review.setVisibility(View.GONE);
                        if(scrollView.getVisibility()== View.GONE) scrollView.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        if(scrollView.getVisibility() == View.VISIBLE) scrollView.setVisibility(View.GONE);
                        if(reviews.getVisibility() == View.GONE) reviews.setVisibility(View.VISIBLE);
                        if(review_button.getVisibility() == View.GONE) review_button.setVisibility(View.VISIBLE);
                        if(no_review.getVisibility() == View.VISIBLE) no_review.setVisibility(View.GONE);
                        if(gridView.getVisibility() == View.VISIBLE) gridView.setVisibility(View.GONE);
                        loadReviews();
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
        if(uid.equals("aa")){
            String str = "Something is wrong! Please Try Again....";
            PopWindow popWindow = new PopWindow(ProfileActivity.this, str, 0);
            popWindow.show();
            popWindow.setOnDismissListener(dialog -> finish());
        }
        else{
            ProgressScreen progressScreen = new ProgressScreen(ProfileActivity.this);
            progressScreen.show();
            if(name.equals("aa") || name.equals("MyName")){
                myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()) name = "User";
                        else name = Objects.requireNonNull(snapshot.getValue()).toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }


            myRef.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.exists()){
                        progressScreen.dismiss();
                        String strl = "Profile Load Failed! Try Again Later.";
                        PopWindow popWindow = new PopWindow(ProfileActivity.this, strl, 0);
                        popWindow.show();
                        popWindow.setOnDismissListener(dialog -> finish());
                    }
                    else {
                        Map<String, String> map = (Map<String, String>) snapshot.getValue();
                        if(map == null){
                            progressScreen.dismiss();
                            String strl = "Profile Load Failed! Try Again Later.";
                            PopWindow popWindow = new PopWindow(ProfileActivity.this, strl, 0);
                            popWindow.show();
                            popWindow.setOnDismissListener(dialog -> finish());
                            return;
                        }
                        String t1 = map.get("Name");
                        String t2 = map.get("Email");
                        String t3 = map.get("PhoneNumber");
                        String t4 = map.get("Type");
                        String t5 = map.get("Caddress");
                        name_text.setText(t1);
                        sharedPreferences.edit().putString("Name", t1).apply();
                        address.setText(t2);
                        education.setText(t5);
                        pro_details.setText(t3);
                        if(t4!= null && t4.equals("0")){
                            String t6 = map.get("educat");
                            cont_info.setText(t6);
                        }
                        else cont_info.setVisibility(View.GONE);
                        progressScreen.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressScreen.dismiss();
                    String strl = "Error! "+error.getMessage();
                    PopWindow popWindow = new PopWindow(ProfileActivity.this, strl, 0);
                    popWindow.show();
                    popWindow.setOnDismissListener(dialog -> finish());
                }
            });

            final long ONE_MEGABYTE = 1024 * 1024;
            stRef.child("ProfilePic").child(uid).getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bmp);
            });


        }

        go_back.setOnClickListener(v -> finish());
    }

    void loadReviews()
    {
        myRef.child("RatingReviews").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    if(no_review.getVisibility() == View.GONE) no_review.setVisibility(View.VISIBLE);
                    if(gridView.getVisibility() == View.VISIBLE) gridView.setVisibility(View.GONE);
                }
                else {
                    ArrayList<String> arrayList = new ArrayList<>();
                    Map<String, String> map = (Map<String, String>) snapshot.getValue();
                    if(map == null){
                        if(no_review.getVisibility() == View.GONE) no_review.setVisibility(View.VISIBLE);
                        if(gridView.getVisibility() == View.VISIBLE) gridView.setVisibility(View.GONE);
                        return;
                    }
                    for(Map.Entry<String, String> entry: map.entrySet()){
                        arrayList.add(entry.getValue());
                    }
                    if(gridView.getVisibility() == View.GONE) gridView.setVisibility(View.VISIBLE);
                    if(no_review.getVisibility() == View.VISIBLE) no_review.setVisibility(View.GONE);
                    CustomVar customVar = new CustomVar(arrayList);
                    gridView.setAdapter(customVar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private class CustomVar extends BaseAdapter {
        ArrayList<String> list_ara;

        public CustomVar(ArrayList<String> list_ara) {
            this.list_ara = list_ara;
        }

        @Override
        public int getCount() {
            return list_ara.size();
        }

        @Override
        public Object getItem(int position) {
            return list_ara.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View view = getLayoutInflater().inflate(R.layout.grid_rating, null);
            String klk = list_ara.get(position);
            String[] lkp = klk.split("#");
            RatingBar ratingBar = view.findViewById(R.id.ratingBar2);
            TextView textView1 = view.findViewById(R.id.textView65);
            TextView textView2 = view.findViewById(R.id.textView66);
            textView1.setText(lkp[0]);
            textView2.setText(lkp[1]);
            ratingBar.setNumStars(5);
            ratingBar.setRating(Float.parseFloat(lkp[2]));
            return view;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }




    class PopupReview extends Dialog{
        String ttt;
        public PopupReview(@NonNull Context context, String ttt) {
            super(context);
            this.ttt = ttt;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.rating_layout);
            this.setCancelable(false);
            RatingBar ratingBar = findViewById(R.id.ratingBar);
            ratingBar.setNumStars(5);
            EditText editText = findViewById(R.id.editText13);
            Button bt1, bt2;
            bt1 = findViewById(R.id.button34);
            bt2 = findViewById(R.id.button35);
            if(ttt!= null){
                String[] pl = ttt.split("#");
                editText.setText(pl[0]);
                ratingBar.setRating(Float.parseFloat(pl[2]));
            }
            bt1.setOnClickListener(v -> {
                String txt = editText.getText().toString();
                txt = txt.replaceAll("#", "");
                if(txt.isEmpty()){
                    editText.setError("Required!");
                }
                else {
                    String sty = txt+ "#by "+name+ "#"+ ratingBar.getRating();
                    myRef.child("RatingReviews").child(uid).child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).setValue(sty);
                    loadReviews();
                    dismiss();
                }
            });
            bt2.setOnClickListener(v-> dismiss());

        }
    }
}