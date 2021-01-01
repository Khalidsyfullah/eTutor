package com.akapps.etutor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class Profile extends Fragment {
    TextView details, emailText, educationstatus, no_review, post_count;
    LinearLayout ll1, ll2, ll3, ll4;
    MaterialSpinner spinner2;
    EditText name, phonenumber, currentadd, educationalstatus, currentpass, newpass;
    Button editprofile, saveprofile, updatepass, update, forgorpass, cancelbutton, passcancel, updateprofilepic, log_out, post_management, post_details;
    ImageView imageView, profileImage;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    StorageReference stRef = FirebaseStorage.getInstance().getReference();
    Context myContext;
    int mode = 0;
    int flag1 = 0, flag2 = 0;
    TabLayout tabLayout;
    Bitmap bitmap, bitmap1;
    GridView gridView;
    boolean passwordmarker1 = true, passwordmarker2 = true;
    RadioGroup radioGroup;
    String name_v,  password_v, phonenumber_v, gender_v, currentadd_v, peradd_v, edu_des, edu_v;
    private static final int RESULT_LOAD_IMAGE = 7;
    private static final int MY_STORAGE_PERMISSION_CODE = 9;
    boolean isSecondpage = false;
    @SuppressLint({"ClickableViewAccessibility", "IntentReset", "NonConstantResourceId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_profile, container, false);
        tabLayout = view.findViewById(R.id.tabLayout);
        details = view.findViewById(R.id.textView23);
        emailText = view.findViewById(R.id.texty);
        educationstatus = view.findViewById(R.id.textView25);
        post_count = view.findViewById(R.id.textView28);
        spinner2 = view.findViewById(R.id.spinner);
        ll1 = view.findViewById(R.id.lin1);
        ll2 = view.findViewById(R.id.lin2);
        ll3 = view.findViewById(R.id.lin3);
        ll4 = view.findViewById(R.id.lin4);
        name = view.findViewById(R.id.editText3);
        name.setEnabled(false);
        no_review = view.findViewById(R.id.textView64);
        currentadd = view.findViewById(R.id.editText7);
        phonenumber = view.findViewById(R.id.editText5);
        educationalstatus = view.findViewById(R.id.editText8);
        editprofile = view.findViewById(R.id.button8);
        saveprofile = view.findViewById(R.id.button16);
        cancelbutton = view.findViewById(R.id.button17);
        post_management = view.findViewById(R.id.button30);
        log_out = view.findViewById(R.id.button3);
        gridView = view.findViewById(R.id.gridview);
        update = view.findViewById(R.id.button15);
        updatepass = view.findViewById(R.id.button13);
        forgorpass = view.findViewById(R.id.button14);
        passcancel = view.findViewById(R.id.button18);
        post_details = view.findViewById(R.id.button38);
        updateprofilepic = view.findViewById(R.id.button19);
        currentpass = view.findViewById(R.id.editText9);
        newpass = view.findViewById(R.id.editText12);
        imageView = view.findViewById(R.id.imageView4);
        profileImage = view.findViewById(R.id.imageView5);
        radioGroup = view.findViewById(R.id.radiogroup);
        updateprofilepic.setVisibility(View.GONE);
        passcancel.setVisibility(View.GONE);
        update.setVisibility(View.GONE);
        currentpass.setVisibility(View.GONE);
        newpass.setVisibility(View.GONE);
        saveprofile.setVisibility(View.GONE);
        cancelbutton.setVisibility(View.GONE);
        currentadd.setVisibility(View.GONE);
        ll2.setVisibility(View.GONE);
        ll3.setVisibility(View.GONE);
        ll4.setVisibility(View.GONE);
        no_review.setVisibility(View.GONE);
        radioGroup.setVisibility(View.GONE);
        educationstatus.setVisibility(View.GONE);
        educationalstatus.setVisibility(View.GONE);
        spinner2.setVisibility(View.GONE);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.radioButton:
                    flag1 = 0;
                    break;
                case R.id.radioButton2:
                    flag1 = 1;
                    break;
            }
        });

        spinner2.setItems("SSC", "HSC", "BSC-Honours", "BSC-Engineering", "MBBS", "MSC-Honours", "MSC-Engineering", "Others" );
        spinner2.setOnItemSelectedListener((view12, position, id, item) -> flag2 = position);
        controlEnablity(false);
        emailText.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
        mode = ((CenterPage) Objects.requireNonNull(getActivity())).getMode();
        if(mode == 0){
            details.setText(R.string.c17);
        }
        else {
            details.setText(R.string.c16);

        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        if(ll1.getVisibility() == View.GONE) ll1.setVisibility(View.VISIBLE);
                        if(ll2.getVisibility() == View.VISIBLE) ll2.setVisibility(View.GONE);
                        if(ll3.getVisibility() == View.VISIBLE) ll3.setVisibility(View.GONE);
                        if(ll4.getVisibility() == View.VISIBLE) ll4.setVisibility(View.GONE);
                        break;
                    case 1:
                        if(ll1.getVisibility() == View.VISIBLE) ll1.setVisibility(View.GONE);
                        if(ll2.getVisibility() == View.GONE) ll2.setVisibility(View.VISIBLE);
                        if(ll3.getVisibility() == View.VISIBLE) ll3.setVisibility(View.GONE);
                        if(ll4.getVisibility() == View.VISIBLE) ll4.setVisibility(View.GONE);
                        loadRatingReviews();
                        break;
                    case 2:
                        if(ll1.getVisibility() == View.VISIBLE) ll1.setVisibility(View.GONE);
                        if(ll2.getVisibility() == View.VISIBLE) ll2.setVisibility(View.GONE);
                        if(ll3.getVisibility() == View.GONE) ll3.setVisibility(View.VISIBLE);
                        if(ll4.getVisibility() == View.VISIBLE) ll4.setVisibility(View.GONE);
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


        final long ONE_MEGABYTE = 1024 * 1024;
        stRef.child(mAuth.getCurrentUser().getUid()).getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageView.setImageBitmap(bmp);
        }).addOnFailureListener(e -> Toast.makeText(myContext, "ID Card Photo Load Failed!", Toast.LENGTH_SHORT).show());

        stRef.child("ProfilePic").child(mAuth.getCurrentUser().getUid()).getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            profileImage.setImageBitmap(bitmap1);
        }).addOnFailureListener(e -> {

        });

        myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> map = (Map<String, String>) snapshot.getValue();
                if(map == null) return;
                name_v = map.get("Name");
                name.setText(name_v);
                password_v = map.get("Password");
                phonenumber_v = map.get("PhoneNumber");
                phonenumber.setText(phonenumber_v);
                gender_v = map.get("Gender");
                if(gender_v!=null) {
                    flag1 = Integer.parseInt(gender_v);
                    if(flag1 == 0) radioGroup.check(R.id.radioButton);
                    else radioGroup.check(R.id.radioButton2);
                }
                currentadd_v = map.get("Caddress");
                currentadd.setText(currentadd_v);
                if(mode == 0){
                    edu_des = map.get("educat");
                    educationalstatus.setText(edu_des);
                    edu_v = map.get("edulavel");
                    if(edu_v!=null){
                        flag2 = Integer.parseInt(edu_v);
                        spinner2.setSelectedIndex(flag2);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        log_out.setOnClickListener(v -> ((CenterPage) getActivity()).logoutApp());

        post_details.setOnClickListener(v -> ((CenterPage) getActivity()).startMypost());

        editprofile.setOnClickListener(v-> {
            controlEnablity(true);
            tabLayout.setVisibility(View.GONE);
            saveprofile.setVisibility(View.VISIBLE);
            cancelbutton.setVisibility(View.VISIBLE);
            editprofile.setVisibility(View.GONE);
            updateprofilepic.setVisibility(View.VISIBLE);
            radioGroup.setVisibility(View.VISIBLE);
            currentadd.setVisibility(View.VISIBLE);
            log_out.setVisibility(View.GONE);
            post_management.setVisibility(View.GONE);
            if(mode == 0){
                educationstatus.setVisibility(View.VISIBLE);
                educationalstatus.setVisibility(View.VISIBLE);
                spinner2.setVisibility(View.VISIBLE);
            }
        });

        post_management.setOnClickListener(v -> {
            ll1.setVisibility(View.GONE);
            ll4.setVisibility(View.VISIBLE);
            post_details.setVisibility(View.GONE);
            isSecondpage = true;
            details.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            ((CenterPage) getActivity()).setTextValue("Post Management");
            showGrid();
        });

        updateprofilepic.setOnClickListener(v->{
            if(ContextCompat.checkSelfPermission(myContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) myContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_STORAGE_PERMISSION_CODE);
            }
            else {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });

        saveprofile.setOnClickListener(v-> {
            String st2 = phonenumber.getText().toString();
            if(st2.isEmpty()){
                phonenumber.setError("Required!");
                return;
            }
            String st3 = currentadd.getText().toString();
            if(st3.isEmpty()){
                currentadd.setError("Required!");
                return;
            }
            String st4="", st5="";
            if(mode == 0){
                st5 = educationalstatus.getText().toString();
                if(st5.isEmpty()){
                    educationalstatus.setError("Required!");
                    return;
                }
            }
            if(bitmap!=null){
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                if(data.length> (ONE_MEGABYTE/2)){
                    int dfa = data.length /1024;
                    String srtyu = "Profile Picture Size Limit Exceeded!\nMax Acceptable Size: 500 KB\nCurrent Size: "+dfa+" KB";
                    PopWindow popWindow = new PopWindow(myContext, srtyu, 0);
                    popWindow.show();
                    return;
                }
                else stRef.child("ProfilePic").child(mAuth.getCurrentUser().getUid()).putBytes(data);
            }

            controlEnablity(false);
            saveprofile.setVisibility(View.GONE);
            cancelbutton.setVisibility(View.GONE);
            updateprofilepic.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
            currentadd.setVisibility(View.GONE);

            if(!st2.equals(phonenumber_v)){
                phonenumber_v = st2;
                myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("PhoneNumber").setValue(phonenumber_v);
            }
            if(!st3.equals(currentadd_v)){
                currentadd_v = st3;
                myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Caddress").setValue(currentadd_v);
            }
            if(!String.valueOf(flag1).equals(gender_v)){
                gender_v = String.valueOf(flag1);
                myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Gender").setValue(gender_v);
            }
            if(mode == 0){
                if(!st4.equals(peradd_v)){
                    peradd_v = st4;
                    myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Paddress").setValue(peradd_v);
                }
                if(!st5.equals(edu_des)){
                    edu_des = st5;
                    myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("educat").setValue(edu_des);
                }
                if(!String.valueOf(flag2).equals(edu_v)){
                    edu_v = String.valueOf(flag2);
                    myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("edulavel").setValue(edu_v);
                }
                educationstatus.setVisibility(View.GONE);
                educationalstatus.setVisibility(View.GONE);
                spinner2.setVisibility(View.GONE);
            }


            Toast.makeText(myContext, "Successfully Updated!", Toast.LENGTH_LONG).show();
            editprofile.setVisibility(View.VISIBLE);
            post_management.setVisibility(View.VISIBLE);
            log_out.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
        });

        try {
            currentpass.setOnTouchListener((v, event) -> {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (currentpass.getRight() - currentpass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(passwordmarker1){
                            currentpass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_showpassword, 0);
                            currentpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            currentpass.setSelection(currentpass.getText().length());
                            passwordmarker1 = false;
                        }else {
                            currentpass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_hidepassword, 0);
                            currentpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            currentpass.setSelection(currentpass.getText().length());
                            passwordmarker1 = true;
                        }
                        return true;
                    }
                }
                return false;
            });
        }catch (Exception ignored){

        }

        try {
            newpass.setOnTouchListener((v, event) -> {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (newpass.getRight() - newpass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(passwordmarker2){
                            newpass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_showpassword, 0);
                            newpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            newpass.setSelection(newpass.getText().length());
                            passwordmarker2 = false;
                        }else {
                            newpass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_hidepassword, 0);
                            newpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            newpass.setSelection(newpass.getText().length());
                            passwordmarker2 = true;
                        }
                        return true;
                    }
                }
                return false;
            });
        }catch (Exception ignored){

        }

        cancelbutton.setOnClickListener(v -> {
            cancelbutton.setVisibility(View.GONE);
            saveprofile.setVisibility(View.GONE);
            editprofile.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            controlEnablity(false);
            phonenumber.setText(phonenumber_v);
            currentadd.setText(currentadd_v);
            currentadd.setVisibility(View.GONE);
            updateprofilepic.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
            post_management.setVisibility(View.VISIBLE);
            log_out.setVisibility(View.VISIBLE);
            if(mode == 0){
                spinner2.setSelectedIndex(Integer.parseInt(edu_v));
                educationalstatus.setText(edu_des);
                educationstatus.setVisibility(View.GONE);
                educationalstatus.setVisibility(View.GONE);
                spinner2.setVisibility(View.GONE);
            }
        });

        updatepass.setOnClickListener(v -> {
            currentpass.setVisibility(View.VISIBLE);
            newpass.setVisibility(View.VISIBLE);
            updatepass.setVisibility(View.GONE);
            passcancel.setVisibility(View.VISIBLE);
            update.setVisibility(View.VISIBLE);
            forgorpass.setVisibility(View.GONE);
        });

        update.setOnClickListener(v -> {
            String sgt1 = currentpass.getText().toString();
            if(sgt1.isEmpty()){
                currentpass.setError("Required!");
                return;
            }
            if(!sgt1.equals(password_v)){
                currentpass.setError("Password Not Matched!");
                return;
            }
            String sgt2 = newpass.getText().toString();
            if(sgt2.isEmpty()){
                newpass.setError("Required!");
                return;
            }
            if(sgt2.length()<6){
                newpass.setError("At Least 6 Character Required!");
                return;
            }
            mAuth.getCurrentUser().updatePassword(sgt2).addOnSuccessListener(aVoid -> {
                password_v = sgt2;
                myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Password").setValue(password_v);
                Toast.makeText(myContext, "Successfully Updated!", Toast.LENGTH_LONG).show();
                currentpass.setVisibility(View.GONE);
                newpass.setVisibility(View.GONE);
                update.setVisibility(View.GONE);
                passcancel.setVisibility(View.GONE);
                updatepass.setVisibility(View.VISIBLE);
                forgorpass.setVisibility(View.VISIBLE);
            }).addOnFailureListener(e -> Toast.makeText(myContext, "Something Goes Wrong!"+e.getMessage(), Toast.LENGTH_LONG).show());
        });

        passcancel.setOnClickListener(v -> {
            currentpass.setVisibility(View.GONE);
            newpass.setVisibility(View.GONE);
            update.setVisibility(View.GONE);
            passcancel.setVisibility(View.GONE);
            updatepass.setVisibility(View.VISIBLE);
            forgorpass.setVisibility(View.VISIBLE);
        });

        forgorpass.setOnClickListener(v -> {
            String str = "A Password Reset Email Will be Sent To Your Email. You need To Reset Password From There.\nTo Continue, please Long Press" +
                    " the button.";
            PopWindow popWindow = new PopWindow(myContext, str, 0);
            popWindow.show();
        });

        forgorpass.setOnLongClickListener(v -> {
            mAuth.sendPasswordResetEmail(Objects.requireNonNull(mAuth.getCurrentUser().getEmail())).addOnSuccessListener(aVoid -> {
                String str = "A Password Reset Email Has Been sent to your Email. Please Check The Email From There.";
                PopWindow popWindow = new PopWindow(myContext, str, 0);
                popWindow.show();
            }).addOnFailureListener(e -> {
                String str = "Something Goes Wrong To Send The Email!"+e.getMessage();
                PopWindow popWindow = new PopWindow(myContext, str, 0);
                popWindow.show();
            });
            return true;
        });


        return view;
    }

    public boolean getIndicator()
    {
        return isSecondpage;
    }

    public void goLastpage()
    {
        isSecondpage = false;
        tabLayout.setVisibility(View.VISIBLE);
        details.setVisibility(View.VISIBLE);
        if(ll1.getVisibility() == View.GONE) ll1.setVisibility(View.VISIBLE);
        if(ll2.getVisibility() == View.VISIBLE) ll2.setVisibility(View.GONE);
        if(ll3.getVisibility() == View.VISIBLE) ll3.setVisibility(View.GONE);
        if(ll4.getVisibility() == View.VISIBLE) ll4.setVisibility(View.GONE);
    }

    void showGrid()
    {
        myRef.child("Allposts").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    post_count.setText(R.string.f2);
                }
                else {
                    Map<String, String> map = (Map<String, String>) snapshot.getValue();
                    if(map == null){
                        post_count.setText(R.string.f2);
                        return;
                    }
                    ArrayList<TeacherPost> list = new ArrayList<>();
                    for(Map.Entry<String, String> entry : map.entrySet()){
                        list.add(new TeacherPost(entry.getKey(), entry.getValue()));
                    }
                    post_count.setText(String.valueOf(list.size()));
                    post_details.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @SuppressLint("IntentReset")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_STORAGE_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(myContext, "Permission Granted", Toast.LENGTH_LONG).show();
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
            else
            {
                Toast.makeText(myContext, "Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    void loadRatingReviews()
    {
        if(gridView.getVisibility() == View.VISIBLE) gridView.setVisibility(View.GONE);
        if(no_review.getVisibility() == View.VISIBLE) no_review.setVisibility(View.GONE);
        myRef.child("RatingReviews").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    if(gridView.getVisibility() == View.VISIBLE) gridView.setVisibility(View.GONE);
                    if(no_review.getVisibility() == View.GONE) no_review.setVisibility(View.VISIBLE);
                }
                else {
                    if(gridView.getVisibility() == View.GONE) gridView.setVisibility(View.VISIBLE);
                    if(no_review.getVisibility() == View.VISIBLE) no_review.setVisibility(View.GONE);
                    ArrayList<String> arrayList = new ArrayList<>();
                    Map<String, String> map = (Map<String, String>) snapshot.getValue();
                    if(map == null){
                        if(gridView.getVisibility() == View.VISIBLE) gridView.setVisibility(View.GONE);
                        if(no_review.getVisibility() == View.GONE) no_review.setVisibility(View.VISIBLE);
                        return;
                    }
                    for(Map.Entry<String, String> entry: map.entrySet()){
                        arrayList.add(entry.getValue());
                    }
                    CustomVar customVar = new CustomVar(arrayList);
                    gridView.setAdapter(customVar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private class CustomVar extends BaseAdapter{
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
            ratingBar.setIsIndicator(true);
            return view;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null !=data){
            Uri selectedImageUri = data.getData();
            profileImage.setImageURI(selectedImageUri);
            Drawable drawable = profileImage.getDrawable();
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            Toast.makeText(myContext, "You have not selected and image", Toast.LENGTH_SHORT).show();
        }
    }

    void controlEnablity(boolean fl)
    {
        spinner2.setEnabled(fl);
        phonenumber.setEnabled(fl);
        currentadd.setEnabled(fl);
        educationalstatus.setEnabled(fl);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myContext = context;
    }
}
