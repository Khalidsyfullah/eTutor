package com.akapps.etutor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class StudentRegister extends AppCompatActivity {
    MaterialSpinner spinner1;
    EditText name, email, phonenumber, currentaddress, educationstatus, password, confirmpass;
    TextView textView, textView2, textView3, textView4, textView5, upload_icon, upload_text;
    int flag1 = 0, mode = 0, flag2 = -1;
    Button bt1;
    boolean passwordmarker1 = true, passwordmarker2 = true;
    RadioGroup radioGroup;
    String s1 = "All the changes will be reset, Sure To Exit This Page?";
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    Bitmap photo;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    StorageReference stRef = FirebaseStorage.getInstance().getReference();

    @SuppressLint({"ClickableViewAccessibility", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);
        spinner1 = findViewById(R.id.spinner);
        name = findViewById(R.id.editText3);
        email = findViewById(R.id.editText4);
        radioGroup = findViewById(R.id.radiogroup);
        phonenumber = findViewById(R.id.editText5);
        currentaddress = findViewById(R.id.editText7);
        educationstatus = findViewById(R.id.editText8);
        password = findViewById(R.id.editText9);
        confirmpass = findViewById(R.id.editText10);
        textView = findViewById(R.id.textView7);
        textView2 = findViewById(R.id.textView11);
        textView3 = findViewById(R.id.textView10);
        textView4 = findViewById(R.id.textView8);
        textView5 = findViewById(R.id.textView15);
        upload_icon = findViewById(R.id.textView13);
        upload_text = findViewById(R.id.textView5t);
        imageView = findViewById(R.id.imageView4);
        bt1 = findViewById(R.id.button7);
        bt1.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        if(bundle!= null){
            mode = bundle.getInt("value", 0);
        }


        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.radioButton:
                    flag2 = 0;
                    break;
                case R.id.radioButton2:
                    flag2 = 1;
                    break;
            }
        });


        spinner1.setItems("SSC", "HSC", "BSC-Honours", "BSC-Engineering", "MBBS", "MSC-Honours", "MSC-Engineering", "Others" );
        spinner1.setOnItemSelectedListener((view1, position, id, item) -> flag1 = position);
        if(mode == 1){
            textView.setText(R.string.h8);
            spinner1.setVisibility(View.GONE);
            educationstatus.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
            upload_text.setText(R.string.i6);
        }
        else {
            textView.setText(R.string.h7);
            upload_text.setText(R.string.b6);
        }


        try {
            password.setOnTouchListener((v, event) -> {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(passwordmarker1){
                            password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_showpassword, 0);
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            password.setSelection(password.getText().length());
                            passwordmarker1 = false;
                        }else {
                            password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_hidepassword, 0);
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            password.setSelection(password.getText().length());
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
            confirmpass.setOnTouchListener((v, event) -> {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (confirmpass.getRight() - confirmpass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(passwordmarker2){
                            confirmpass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_showpassword, 0);
                            confirmpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            confirmpass.setSelection(confirmpass.getText().length());
                            passwordmarker2 = false;
                        }else {
                            confirmpass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_hidepassword, 0);
                            confirmpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            confirmpass.setSelection(confirmpass.getText().length());
                            passwordmarker2 = true;
                        }
                        return true;
                    }
                }
                return false;
            });
        }catch (Exception ignored){

        }

        textView5.setOnClickListener(v -> showAlert());


        bt1.setOnClickListener(v -> takePhoto());

        upload_text.setOnClickListener(v -> takePhoto());

        upload_icon.setOnClickListener(v -> takePhoto());
    }

    void takePhoto()
    {
        if(ContextCompat.checkSelfPermission(StudentRegister.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(StudentRegister.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        }
        else
        {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if(imageView.getVisibility() == View.GONE) imageView.setVisibility(View.VISIBLE);
            if(upload_icon.getVisibility() == View.VISIBLE) upload_icon.setVisibility(View.GONE);
            if(upload_text.getVisibility() == View.VISIBLE) upload_text.setVisibility(View.GONE);
            if(bt1.getVisibility() == View.GONE) bt1.setVisibility(View.VISIBLE);
            photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }

        else Toast.makeText(StudentRegister.this, "Something Goes Wrong!", Toast.LENGTH_LONG).show();
    }


    public void onRegistrationClicked(View view){
        String name_v = name.getText().toString();
        String sdfg = "Please Enter All Required Info!";
        if(name_v.isEmpty()){
            name.setError("Required!");
            Toast.makeText(StudentRegister.this, sdfg, Toast.LENGTH_SHORT).show();
            return;
        }
        String email_v = email.getText().toString();
        if(email_v.isEmpty()){
            email.setError("Required!");
            Toast.makeText(StudentRegister.this, sdfg, Toast.LENGTH_SHORT).show();
            return;
        }
        String phonenumber_v = phonenumber.getText().toString();
        if(phonenumber_v.isEmpty()){
            phonenumber.setError("Required!");
            Toast.makeText(StudentRegister.this, sdfg, Toast.LENGTH_SHORT).show();
            return;
        }
        String currentaddress_v = currentaddress.getText().toString();
        if(currentaddress_v.isEmpty()){
            currentaddress.setError("Required!");
            Toast.makeText(StudentRegister.this, sdfg, Toast.LENGTH_SHORT).show();
            return;
        }
        if(flag2 == -1){
            Toast.makeText(StudentRegister.this, "Select Gender!", Toast.LENGTH_SHORT).show();
            return;
        }
        String password_v = password.getText().toString();
        if(password_v.isEmpty()){
            password.setError("Required!");
            Toast.makeText(StudentRegister.this, sdfg, Toast.LENGTH_SHORT).show();
            return;
        }
        if(password_v.length()<6){
            password.setError("Too Short!");
            return;
        }
        String confirmpassword_v = confirmpass.getText().toString();
        if(confirmpassword_v.isEmpty()){
            confirmpass.setError("Required!");
            return;
        }
        if(!password_v.equals(confirmpassword_v)){
            confirmpass.setError("Password Not Same!");
            return;
        }
        if(photo == null){
            String uuii = "Upload a Valid ID Card Photo!";
            PopWindow popWindow = new PopWindow(StudentRegister.this, uuii, 0);
            popWindow.show();
            return;
        }
        String educational_v;
        if(mode== 0){
            educational_v = educationstatus.getText().toString();
            if(educational_v.isEmpty()){
                educationstatus.setError("Required!");
                Toast.makeText(StudentRegister.this, sdfg, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        ProgressScreen progressScreen = new ProgressScreen(StudentRegister.this);
        progressScreen.show();
        mAuth.createUserWithEmailAndPassword(email_v, password_v).addOnSuccessListener(authResult -> {
            myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Name").setValue(name_v);
            myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Email").setValue(email_v);
            myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("PhoneNumber").setValue(phonenumber_v);
            myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Gender").setValue(String.valueOf(flag2));
            myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Caddress").setValue(currentaddress_v);
            myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Password").setValue(password_v);
            if(mode == 0){
                String stp2 = educationstatus.getText().toString();
                myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Type").setValue("0");
                myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("educat").setValue(stp2);
                myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("edulavel").setValue(String.valueOf(flag1));
            }else {
                myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Type").setValue("1");
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            stRef.child(mAuth.getCurrentUser().getUid()).putBytes(data).addOnSuccessListener(taskSnapshot -> {
                progressScreen.dismiss();
                mAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(aVoid -> {
                    String srtr = "Registration is successful. A Verification Mail Has Been Sent To Your Email. Please check your Inbox & verify it.";
                    PopWindow popWindow = new PopWindow(StudentRegister.this, srtr, 0);
                    popWindow.show();
                    popWindow.setOnDismissListener(dialog -> {
                        startActivity(new Intent(StudentRegister.this, Loginpage.class));
                        finish();
                    });
                }).addOnFailureListener(e -> {
                    String srtr = "Registration is Successful, but verification mail send fail! Try Again From Login Page.";
                    PopWindow popWindow = new PopWindow(StudentRegister.this, srtr, 0);
                    popWindow.show();
                    popWindow.setOnDismissListener(dialog -> {
                        startActivity(new Intent(StudentRegister.this, Loginpage.class));
                        finish();
                    });
                });
            }).addOnFailureListener(e -> {
                progressScreen.dismiss();
                String srtr = "ID Card verification Failed!";
                PopWindow popWindow = new PopWindow(StudentRegister.this, srtr, 0);
                popWindow.show();
            });

        }).addOnFailureListener(e -> {
            progressScreen.dismiss();
            String str = "Registration Failed! "+e.getMessage();
            PopWindow popWindow = new PopWindow(StudentRegister.this, str, 0);
            popWindow.show();
        });

    }

    @Override
    public void onBackPressed() {
        showAlert();
    }

    void showAlert()
    {
        if(name.getText().toString().isEmpty() && email.getText().toString().isEmpty() && phonenumber.getText().toString().isEmpty() &&
                currentaddress.getText().toString().isEmpty() && educationstatus.getText().toString().isEmpty()
            && password.getText().toString().isEmpty() && confirmpass.getText().toString().isEmpty()) finish();

        else {
            AlertgameQuit alertgameQuit = new AlertgameQuit(StudentRegister.this, s1);
            alertgameQuit.show();
        }
    }
}
