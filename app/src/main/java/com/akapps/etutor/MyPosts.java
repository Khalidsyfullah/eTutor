package com.akapps.etutor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class MyPosts extends Fragment implements OnMapReadyCallback {
    GridView gridView;
    ScrollView scrollView;
    TextView tx1, tx2, num_tex;
    Button bt1;
    private GoogleMap mMap;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Context myContex;
    int mode = 0;
    TeacherPost tpp;
    boolean isSecondpage = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_myposts, container, false);
        gridView = view.findViewById(R.id.gridView);
        scrollView = view.findViewById(R.id.scroll);
        tx1 = view.findViewById(R.id.textView41);
        tx2 = view.findViewById(R.id.textView42);
        num_tex = view.findViewById(R.id.textView40);
        bt1 = view.findViewById(R.id.button26);
        scrollView.setVisibility(View.GONE);
        gridView.setVisibility(View.GONE);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        mode = ((CenterPage) Objects.requireNonNull(getActivity())).getMode();
        showGrid();

        bt1.setOnClickListener(v -> {
            PopupAlertShow popupAlertShow = new PopupAlertShow(myContex, tpp);
            popupAlertShow.show();
        });

        return view;
    }

    public boolean gePagestatus()
    {
        return isSecondpage;
    }

    public void backState()
    {
        isSecondpage = false;
        scrollView.setVisibility(View.GONE);
        num_tex.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.VISIBLE);
    }

    void showGrid()
    {
        myRef.child("Allposts").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    num_tex.setText(R.string.f2);
                }
                else {
                    Map<String, String> map = (Map<String, String>) snapshot.getValue();
                    if(map == null){
                        num_tex.setText(R.string.f2);
                        return;
                    }
                    ArrayList<TeacherPost> list = new ArrayList<>();
                    for(Map.Entry<String, String> entry : map.entrySet()){
                        list.add(new TeacherPost(entry.getKey(), entry.getValue()));
                    }
                    num_tex.setText("Total Posts: "+list.size());
                    gridView.setVisibility(View.VISIBLE);
                    MyCustomAdapter myCustomAdapter = new MyCustomAdapter(list);
                    gridView.setAdapter(myCustomAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myContex = context;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private class MyCustomAdapter extends BaseAdapter{
        ArrayList<TeacherPost> list;

        public MyCustomAdapter(ArrayList<TeacherPost> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = getLayoutInflater();
            @SuppressLint({"ViewHolder", "InflateParams"}) View view = layoutInflater.inflate(R.layout.grid_mypost, null);
            TextView textView = view.findViewById(R.id.textView43);
            Button button = view.findViewById(R.id.button28);
            textView.setText("Post ID: "+list.get(position).getSubjects());
            button.setOnClickListener(v -> loadView(list.get(position)));
            return view;
        }
    }


    void loadView(TeacherPost teacherPost){
        tpp = teacherPost;
        isSecondpage = true;
        ((CenterPage) Objects.requireNonNull(getActivity())).setTextValue("Post Details");
        gridView.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
        num_tex.setVisibility(View.GONE);
        ProgressScreen progressScreen = new ProgressScreen(myContex);
        progressScreen.show();
        String txttt = "Teacher";
        if(mode != 0) txttt = "Student";
        myRef.child("Posts").child(txttt).child(teacherPost.getSubjects()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    String plop = "Something goes wrong to load your post! Try Again To Load it.";
                    progressScreen.dismiss();
                    num_tex.setVisibility(View.VISIBLE);
                    PopWindow popWindow = new PopWindow(myContex, plop, 0);
                    popWindow.show();
                }
                else {
                    Map<String, String> map = (Map<String, String>) snapshot.getValue();
                    if(map == null){
                        progressScreen.dismiss();
                        num_tex.setVisibility(View.VISIBLE);
                        String plop = "Something goes wrong to load your post! Try Again To Load it.";
                        PopWindow popWindow = new PopWindow(myContex, plop, 0);
                        popWindow.show();
                        return;
                    }
                    if(mode == 0){
                        String s1 = map.get("Category");
                        String s3 = map.get("Subject");
                        String s4 = map.get("Day");
                        String s6 = map.get("Salary");
                        String s7 = map.get("AllDetails");
                        String s8 = map.get("Langi");
                        String s9 = map.get("Lati");
                        String formStr = "<b>Preferred Category: </b><br>"+s1+"<br><b>Preferred Subject: </b><br>"+s3+"<br><b>Preferred Day/Week: </b>"+
                            s4+ "<br><b>Preferred Salary Range: </b><br>"+s6+"<br><b>Preferred Location: </b><br>"+s7;
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                            tx2.setText(Html.fromHtml(formStr, Html.FROM_HTML_MODE_COMPACT));
                        }else {
                            tx2.setText(Html.fromHtml(formStr));
                        }
                        tx1.setText("Post ID: "+ teacherPost.getSubjects());
                        if(s8 == null || s9 == null) return;
                        if(mMap!= null){
                            LatLng latLng = new LatLng(Double.parseDouble(s8), Double.parseDouble(s9));
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Your Marked Location");
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            mMap.addMarker(markerOptions);
                        }
                        progressScreen.dismiss();
                    }
                    else {
                        String s1 = map.get("Category");
                        String s2 = map.get("Classyear");
                        String s3 = map.get("Subject");
                        String s4 = map.get("Day");
                        String s5 = map.get("Number");
                        String s6 = map.get("Salary");
                        String s7 = map.get("AllDetails");
                        String s8 = map.get("Langi");
                        String s9 = map.get("Lati");
                        String formStr = "<b>Category: </b><br>"+s1+"<br><b>Class/Year: </b><br>"+s2+"<br><b>Subject: </b><br>"+s3+"<br><b>Required Day/Week: </b><br>"+
                                s4+"<br><b>Number of Student: </b><br>"+s5+"<br><b>Preferred Salary Range: </b><br>"+s6+"<br><b>Preferred Location: </b><br>"+s7;
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                            tx2.setText(Html.fromHtml(formStr, Html.FROM_HTML_MODE_COMPACT));
                        }else {
                            tx2.setText(Html.fromHtml(formStr));
                        }
                        tx1.setText("Post ID: "+ teacherPost.getSubjects());
                        if(s8 == null || s9 == null) return;
                        LatLng latLng = new LatLng(Double.parseDouble(s8), Double.parseDouble(s9));
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Your Marked Location");
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        mMap.addMarker(markerOptions);
                        progressScreen.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressScreen.dismiss();
                num_tex.setVisibility(View.VISIBLE);
                String plop = "Something goes wrong to load your post!"+error.getDetails();
                PopWindow popWindow = new PopWindow(myContex, plop, 0);
                popWindow.show();
            }
        });
    }

    void deleteUserPost(TeacherPost teacherPost)
    {
        try {
            scrollView.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            num_tex.setVisibility(View.VISIBLE);
            myRef.child("Allposts").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child(teacherPost.getCategory()).removeValue();
            String plpo = "Teacher";
            if(mode!= 0) plpo = "Student";
            myRef.child("Posts").child(plpo).child(teacherPost.getSubjects()).removeValue();
            String yui = "Post is Successfully Deleted!";
            PopWindow popWindow = new PopWindow(myContex, yui, 0);
            popWindow.show();
            popWindow.setOnDismissListener(dialog -> showGrid());

        }catch (Exception e){
            String lkp = "Error! "+e.getMessage();
            PopWindow popWindow = new PopWindow(myContex, lkp, 0);
            popWindow.show();
        }
    }

    public class PopupAlertShow extends Dialog{
        TeacherPost teacherPost;
        public PopupAlertShow(@NonNull Context context, TeacherPost teacherPost) {
            super(context);
            this.teacherPost = teacherPost;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.layout_deletepost);
            String drt = "Sure To Delete This Post?\nOnce Deleted, it will be permanently removed from server.";
            TextView textView = findViewById(R.id.textView99);
            textView.setText(drt);
            Button lpo1, lpo2;
            lpo1 = findViewById(R.id.button55);
            lpo2 = findViewById(R.id.button54);
            this.setCancelable(false);
            lpo1.setOnClickListener(v -> {
                deleteUserPost(teacherPost);
                dismiss();
            });

            lpo2.setOnClickListener(v -> dismiss());
        }
    }
}
