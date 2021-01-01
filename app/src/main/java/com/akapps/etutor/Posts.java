package com.akapps.etutor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Posts extends Fragment implements OnMapReadyCallback {
    TextView post_details, teacher_profile, contact_info, no_post, profile_op, phn_num;
    Button send_msg, filter_btn;
    ScrollView scrollView;
    GridView gridView;
    int mode = 0;
    Context myContex;
    private GoogleMap mMap;
    ImageView imageView;
    int num1 =0, num2 = 0, num3 = 0, num4 = 0, num5 = 0;
    int pum1 =0, pum2 = 0, pum3 = 0, pum4 = 0, pum5 = 0;
    String st1 = "Any", st2 = "Any", st3 = "Any", st4 = "Any", st5 = "Any";
    String pt1 = "Any", pt2 = "Any", pt3 = "Any", pt4 = "Any", pt5 = "Any";
    ArrayList<StudentPost> list = new ArrayList<>();
    SharedPreferences sharedPreferences;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    StorageReference stRef = FirebaseStorage.getInstance().getReference();
    boolean isSecondpage = false;
    String save_uid = "";
    String save_name = "";
    final long ONE_MEGABYTE = 1024 * 1024;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_posts, container, false);
        post_details = view.findViewById(R.id.textView46);
        teacher_profile = view.findViewById(R.id.textView48);
        contact_info = view.findViewById(R.id.textView50);
        no_post = view.findViewById(R.id.textView52);
        profile_op = view.findViewById(R.id.textView47);
        send_msg = view.findViewById(R.id.button29);
        filter_btn = view.findViewById(R.id.button9);
        scrollView = view.findViewById(R.id.scrolll);
        gridView = view.findViewById(R.id.gridLay);
        phn_num = view.findViewById(R.id.textView45);
        imageView = view.findViewById(R.id.imageView9);
        scrollView.setVisibility(View.GONE);
        filter_btn.setVisibility(View.GONE);
        no_post.setVisibility(View.GONE);
        gridView.setVisibility(View.GONE);
        mode = ((CenterPage) Objects.requireNonNull(getActivity())).getMode();
        sharedPreferences = myContex.getSharedPreferences("com.akapps.etutor", Context.MODE_PRIVATE);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        loadView();
        filter_btn.setOnClickListener(v ->{
            FilterClass filterClass = new FilterClass(myContex);
            filterClass.show();
        });

        send_msg.setOnClickListener(v-> {
            sharedPreferences.edit().putString("Name", save_name).apply();
            sharedPreferences.edit().putString("Uid", save_uid).apply();
            ((CenterPage)getActivity()).startMessage();
        });

        return view;
    }

    void loadView()
    {
        if(mode == 0){
            onLoadStatusStudent();
        }
        else {
            onLoadStatusTeacher();
        }
    }

    void onLoadStatusStudent()
    {
        ProgressScreen progressScreen = new ProgressScreen(myContex);
        progressScreen.show();
        myRef.child("Posts").child("Student").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    progressScreen.dismiss();
                    no_post.setVisibility(View.VISIBLE);
                    String styu = "No Post Available Now! Please try Again Later.";
                    PopWindow popWindow = new PopWindow(myContex, styu, 0);
                    popWindow.show();
                    return;
                }
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                    if(map == null){
                        progressScreen.dismiss();
                        no_post.setVisibility(View.VISIBLE);
                        String styu = "No Post Available Now! Please try Again Later.";
                        PopWindow popWindow = new PopWindow(myContex, styu, 0);
                        popWindow.show();
                        return;
                    }
                    String s1 = map.get("Category");
                    String s2 = map.get("Classyear");
                    String s3 = map.get("Subject");
                    String s4 = map.get("Day");
                    String s5 = map.get("Number");
                    String s6 = map.get("Salary");
                    String s7 = map.get("AllDetails");
                    String s8 = map.get("Langi");
                    String s9 = map.get("Lati");
                    String s10 = map.get("ID");
                    String formStr = "<b>Category: </b><br>"+s1+"<br><b>Class/Year: </b><br>"+s2+"<br><b>Subject: </b><br>"+s3+ "<br><b>Salary: </b><br>"+s6+"<br><b>Location: </b><br>"+s7;
                    String form = "<b>Category: </b><br>"+s1+"<br><b>Class/Year: </b><br>"+s2+"<br><b>Subject: </b><br>"+s3+ "<br><b>Days/Week: </b><br>"+s4+ "<br><b>Number of Student: </b><br>"+
                            s5+"<br><b>Salary: </b><br>"+s6+"<br><b>Location: </b><br>"+s7;
                    StudentPost studentPost = new StudentPost(s1, s3, s6, s7, s8, s9, s10, form, formStr);
                    list.add(studentPost);
                }
                filter_btn.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.VISIBLE);
                CustomAdapter customAdapter = new CustomAdapter(list);
                gridView.setAdapter(customAdapter);
                progressScreen.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressScreen.dismiss();
                String styu = "Error! "+error.getMessage();
                PopWindow popWindow = new PopWindow(myContex, styu, 0);
                popWindow.show();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myContex = context;
    }

    void onLoadStatusTeacher()
    {
        ProgressScreen progressScreen = new ProgressScreen(myContex);
        progressScreen.show();
        myRef.child("Posts").child("Teacher").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    progressScreen.dismiss();
                    no_post.setVisibility(View.VISIBLE);
                    String styu = "No Post Available Now! Please try Again Later.";
                    PopWindow popWindow = new PopWindow(myContex, styu, 0);
                    popWindow.show();
                    return;
                }
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                    if(map == null){
                        progressScreen.dismiss();
                        no_post.setVisibility(View.VISIBLE);
                        String styu = "No Post Available Now! Please try Again Later.";
                        PopWindow popWindow = new PopWindow(myContex, styu, 0);
                        popWindow.show();
                        return;
                    }
                    String s1 = map.get("Category");
                    String s3 = map.get("Subject");
                    String s4 = map.get("Day");
                    String s6 = map.get("Salary");
                    String s7 = map.get("AllDetails");
                    String s8 = map.get("Langi");
                    String s9 = map.get("Lati");
                    String s10 = map.get("ID");
                    String formStr = "<b>Category: </b><br>"+s1+"<br><b>Subject: </b><br>"+s3+"<br><b>Salary: </b><br>"+s6+"<br><b>Location: </b><br>"+s7;
                    String form = "<b>Category: </b><br>"+s1+"<br><b>Subject: </b><br>"+s3+"<br><b>Days/Week: </b><br>"+s4+ "<br><b>Salary: </b><br>"+s6
                            +"<br><b>Location: </b><br>"+s7;
                    StudentPost studentPost = new StudentPost(s1, s3, s6, s7, s8, s9, s10, form, formStr);
                    list.add(studentPost);
                }
                filter_btn.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.VISIBLE);
                CustomAdapter customAdapter = new CustomAdapter(list);
                gridView.setAdapter(customAdapter);
                progressScreen.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressScreen.dismiss();
                String styu = "Error! "+error.getMessage();
                PopWindow popWindow = new PopWindow(myContex, styu, 0);
                popWindow.show();
            }
        });
    }

    void loadDetails(StudentPost studentPost)
    {
        isSecondpage = true;
        ProgressScreen progressScreen = new ProgressScreen(myContex);
        progressScreen.show();
        filter_btn.setVisibility(View.GONE);
        gridView.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
        save_uid = studentPost.getUid();
        ((CenterPage) Objects.requireNonNull(getActivity())).setTextValue("Post Details");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            post_details.setText(Html.fromHtml(studentPost.getStructureddata(), Html.FROM_HTML_MODE_COMPACT));
        }else {
            post_details.setText(Html.fromHtml(studentPost.getStructureddata()));
        }
        if(mMap!= null){
            LatLng latLng = new LatLng(Double.parseDouble(studentPost.getLang()), Double.parseDouble(studentPost.getLant()));
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Marked Location");
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            mMap.addMarker(markerOptions);
        }


        stRef.child("ProfilePic").child(studentPost.getUid()).getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageView.setImageBitmap(bmp);
        }).addOnFailureListener(e -> Toast.makeText(myContex, "Profile Picture Load Failed!", Toast.LENGTH_SHORT).show());


        myRef.child("Users").child(studentPost.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    progressScreen.dismiss();
                    String strl = "Profile Load Failed! Try Again Later.";
                    PopWindow popWindow = new PopWindow(myContex, strl, 0);
                    popWindow.show();
                }
                else {
                    Map<String, String> map = (Map<String, String>) snapshot.getValue();
                    if(map == null) return;
                    String t1 = map.get("Name");
                    String t2 = map.get("Email");
                    String t3 = map.get("PhoneNumber");
                    String t7 = map.get("Type");
                    teacher_profile.setText(t1);
                    contact_info.setText(t2);
                    phn_num.setText(t3);
                    save_name = t1;
                    if(t7 != null && t7.equals("0")) profile_op.setText(R.string.f6);
                    else profile_op.setText(R.string.f7);
                    progressScreen.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressScreen.dismiss();
                String strl = "Error! "+error.getMessage();
                PopWindow popWindow = new PopWindow(myContex, strl, 0);
                popWindow.show();
            }
        });

    }

    public boolean returnLoadStatus()
    {
        return isSecondpage;
    }

    public void gobackButton()
    {
        isSecondpage = false;
        gridView.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        filter_btn.setVisibility(View.VISIBLE);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private class CustomAdapter extends BaseAdapter{
        ArrayList<StudentPost> arrayList;

        public CustomAdapter(ArrayList<StudentPost> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = getLayoutInflater();
            @SuppressLint({"ViewHolder", "InflateParams"}) View view = layoutInflater.inflate(R.layout.grid_postholder, null);
            TextView textView = view.findViewById(R.id.textView51);
            Button button = view.findViewById(R.id.button31);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                textView.setText(Html.fromHtml(arrayList.get(position).getShowfirst(), Html.FROM_HTML_MODE_COMPACT));
            }else {
                textView.setText(Html.fromHtml(arrayList.get(position).getShowfirst()));
            }
            button.setOnClickListener(v -> loadDetails(arrayList.get(position)));
            return view;
        }
    }

    void filterResult()
    {
        ArrayList<StudentPost> filterdata = new ArrayList<>();
        for(StudentPost studentPost : list){
            if(!st1.equals("Any")){
                if(!studentPost.getCategory().contains(st1)) continue;
            }
            if(!st2.equals("Any")){
                if(!studentPost.getSubjects().contains(st2)) continue;
            }
            if(!st3.equals("Any")){
                if(!studentPost.getSalaryRange().contains(st3)) continue;
            }
            if(!st4.equals("Any")){
                if(!studentPost.getFulladd().contains(st4)) continue;
            }
            if(!st5.equals("Any")){
                String dfu = st5+", "+st4;
                if(!studentPost.getFulladd().contains(dfu)) continue;
            }
            filterdata.add(studentPost);
        }
        pt1 = st1;
        pt2 = st2;
        pt3 = st3;
        pt4 = st4;
        pt5 = st5;
        pum1 = num1;
        pum2 = num2;
        pum3 = num3;
        pum4 = num4;
        pum5 = num5;
        if(filterdata.size() == 0){
            if(gridView.getVisibility() == View.VISIBLE) gridView.setVisibility(View.GONE);
            if(no_post.getVisibility() == View.GONE) no_post.setVisibility(View.VISIBLE);
        }
        else {
            if(gridView.getVisibility() == View.GONE) gridView.setVisibility(View.VISIBLE);
            if(no_post.getVisibility() == View.VISIBLE) no_post.setVisibility(View.GONE);
            CustomAdapter customAdapter = new CustomAdapter(filterdata);
            gridView.setAdapter(customAdapter);
        }

    }


    public class FilterClass extends Dialog{

        public FilterClass(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.grid_filterview);
            this.setCancelable(false);
            Button discard, apply;

            discard = findViewById(R.id.button32);
            apply = findViewById(R.id.button33);
            MaterialSpinner spinner = findViewById(R.id.spinner);
            MaterialSpinner spinner1 = findViewById(R.id.spinner1);
            MaterialSpinner spinner2 = findViewById(R.id.spinner2);
            MaterialSpinner division = findViewById(R.id.spinner3);
            MaterialSpinner district = findViewById(R.id.spinner4);
            spinner.setItems("Any" ,"PrePrimary", "Primary", "Lower-Secondary", "Secondary", "Higher-Secondary", "University");
            spinner.setOnItemSelectedListener((view, position, id, item) -> {
                st1 = String.valueOf(item);
                num1 = position;
            });
            spinner1.setItems("Any" ,"Bangla", "English", "Math", "General Science", "Physics", "Chemistry", "Higher Math", "Biology", "Accounting",
                    "Management", "Economics", "Others");
            spinner1.setOnItemSelectedListener((view, position, id, item) -> {
                st2 = String.valueOf(item);
                num2 = position;
            });
            spinner2.setItems("Any" ,"Negotiable", "<2000 Taka/Month", "2000-3000 Taka/Month", "3000-5000 Taka/Month", "5000-8000 Taka/Month", "8000-10000 Taka/Month", "10000+ Taka/Month");
            spinner2.setOnItemSelectedListener((view, position, id, item) -> {
                st3 = String.valueOf(item);
                num3 = position;
            });
            division.setItems("Any" ,"BARISAL", "CHITTAGONG", "DHAKA", "MYMENSINGH", "KHULNA", "RAJSHAHI", "RANGPUR", "SYLHET");
            division.setOnItemSelectedListener((view14, position, id, item) -> {
                st4 = String.valueOf(item);
                num4 = position;
                if(position == 0){
                    district.setItems("Any");
                } else if (position == 1) {
                    district.setItems("Any", "BARISAL", "BARGUNA", "BHOLA", "JHALOKATI", "PATUAKHALI", "PIROJPUR");
                } else if (position == 2) {
                    district.setItems("Any", "CHITTAGONG", "BANDARBAN", "BRAHMANBARIA", "CHANDPUR", "COMILLA", "COX'S BAZAR", "FENI", "KHAGRACHHARI", "LAKSHMIPUR", "NOAKHALI", "RANGAMATI");
                } else if (position == 3) {
                    district.setItems("Any", "DHAKA", "FARIDPUR", "GAZIPUR", "GOPALGANJ", "KISHOREGONJ", "MADARIPUR", "MANIKGANJ", "MUNSHIGANJ", "NARAYANGANJ", "NARSINGDI", "RAJBARI", "SHARIATPUR", "TANGAIL");
                } else if (position == 4) {
                    district.setItems("Any", "MYMENSINGH", "NETRAKONA", "JAMALPUR", "SHERPUR");
                } else if (position == 5) {
                    district.setItems("Any", "KHULNA", "BAGERHAT", "CHUADANGA", "JESSORE", "JHENAIDAH", "KUSHTIA", "MAGURA", "MEHERPUR", "NARAIL", "SATKHIRA");
                } else if (position == 6) {
                    district.setItems("Any", "RAJSHAHI", "BOGRA", "JOYPURHAT", "NAOGAON", "NATORE", "CHAPAI NABABGANJ", "PABNA", "SIRAJGANJ");
                } else if (position == 7) {
                    district.setItems("Any", "RANGPUR", "DINAJPUR", "GAIBANDHA", "KURIGRAM", "LALMONIRHAT", "NILPHAMARI", "PANCHAGARH", "THAKURGAON");
                } else if (position == 8) {
                    district.setItems("Any", "SYLHET", "HABIGANJ", "MAULVIBAZAR", "SUNAMGANJ");
                }
                st5 = "Any";
                num5 = 0;
            });

            if(num4 == 0){
                district.setItems("Any");
            } else if (num4 == 1) {
                district.setItems("Any", "BARISAL", "BARGUNA", "BHOLA", "JHALOKATI", "PATUAKHALI", "PIROJPUR");
            } else if (num4 == 2) {
                district.setItems("Any", "CHITTAGONG", "BANDARBAN", "BRAHMANBARIA", "CHANDPUR", "COMILLA", "COX'S BAZAR", "FENI", "KHAGRACHHARI", "LAKSHMIPUR", "NOAKHALI", "RANGAMATI");
            } else if (num4 == 3) {
                district.setItems("Any", "DHAKA", "FARIDPUR", "GAZIPUR", "GOPALGANJ", "KISHOREGONJ", "MADARIPUR", "MANIKGANJ", "MUNSHIGANJ", "NARAYANGANJ", "NARSINGDI", "RAJBARI", "SHARIATPUR", "TANGAIL");
            } else if (num4 == 4) {
                district.setItems("Any", "MYMENSINGH", "NETRAKONA", "JAMALPUR", "SHERPUR");
            } else if (num4 == 5) {
                district.setItems("Any", "KHULNA", "BAGERHAT", "CHUADANGA", "JESSORE", "JHENAIDAH", "KUSHTIA", "MAGURA", "MEHERPUR", "NARAIL", "SATKHIRA");
            } else if (num4 == 6) {
                district.setItems("Any", "RAJSHAHI", "BOGRA", "JOYPURHAT", "NAOGAON", "NATORE", "CHAPAI NABABGANJ", "PABNA", "SIRAJGANJ");
            } else if (num4 == 7) {
                district.setItems("Any", "RANGPUR", "DINAJPUR", "GAIBANDHA", "KURIGRAM", "LALMONIRHAT", "NILPHAMARI", "PANCHAGARH", "THAKURGAON");
            } else {
                district.setItems("Any", "SYLHET", "HABIGANJ", "MAULVIBAZAR", "SUNAMGANJ");
            }
            district.setOnItemSelectedListener((view15, position, id, item) -> {
                st5 = String.valueOf(item);
                num5 = position;
            });
            spinner.setSelectedIndex(pum1);
            spinner1.setSelectedIndex(pum2);
            spinner2.setSelectedIndex(pum3);
            division.setSelectedIndex(pum4);
            district.setSelectedIndex(pum5);
            discard.setOnClickListener(v -> {
                st1 = pt1;
                st2 = pt2;
                st3 = pt3;
                st4 = pt4;
                st5 = pt5;
                dismiss();
            });
            apply.setOnClickListener(v -> {
                if(!pt1.equals(st1) || !pt2.equals(st2) || !pt3.equals(st3) || !pt4.equals(st4) || !pt5.equals(st5)) filterResult();
                dismiss();
            });
        }
    }
}
