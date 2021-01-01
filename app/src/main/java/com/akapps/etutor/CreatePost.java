package com.akapps.etutor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CreatePost extends Fragment implements OnMapReadyCallback {
    TextView hideText1, hideText2, teacherCategory, subjectCategory;
    MaterialSpinner spinner1, spinner2, spinner3, division, district, spinner4, spinner5, spinner6;
    EditText detailedAdd;
    Button clearBtn1, clearBtn2, currentLocation, addLocation, selectLocation, registerButton, newpost;
    String flag1 = "PrePrimary", flag2 = "Nursery", flag4 = "1", flag5 = "1", flag6 = "Negotiable";
    int mode = 0;
    String divisionname = "BARISAL";
    String districtname = "BARISAL";
    Context myContex;
    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation1;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 97;
    boolean waitingmode = false;
    SharedPreferences sharedPreferences;
    String lang = "0", lant = "0";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_createpost, container, false);
        hideText1 = view.findViewById(R.id.textView38);
        hideText2 = view.findViewById(R.id.textView21);
        teacherCategory = view.findViewById(R.id.textView39);
        subjectCategory = view.findViewById(R.id.textView19);
        detailedAdd = view.findViewById(R.id.editText3);
        clearBtn1 = view.findViewById(R.id.button21);
        clearBtn2 = view.findViewById(R.id.button22);
        spinner1 = view.findViewById(R.id.spinner);
        spinner2 = view.findViewById(R.id.spinner7);
        spinner3 = view.findViewById(R.id.spinner1);
        spinner4 = view.findViewById(R.id.spinner2);
        spinner5 = view.findViewById(R.id.spinner6);
        spinner6 = view.findViewById(R.id.spinner5);
        division = view.findViewById(R.id.spinner3);
        district = view.findViewById(R.id.spinner4);
        currentLocation = view.findViewById(R.id.button20);
        addLocation = view.findViewById(R.id.button23);
        selectLocation = view.findViewById(R.id.button24);
        registerButton = view.findViewById(R.id.button8);
        newpost = view.findViewById(R.id.button25);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(myContex);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        sharedPreferences = myContex.getSharedPreferences("com.akapps.etutor", Context.MODE_PRIVATE);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        newpost.setVisibility(View.GONE);
        mode = ((CenterPage) Objects.requireNonNull(getActivity())).getMode();
        if (mode == 0) {
            spinner2.setVisibility(View.GONE);
            spinner5.setVisibility(View.GONE);
            hideText1.setVisibility(View.GONE);
            hideText2.setVisibility(View.GONE);

        } else {
            teacherCategory.setVisibility(View.GONE);
            clearBtn1.setVisibility(View.GONE);
        }
        spinner1.setItems("PrePrimary", "Primary", "Lower-Secondary", "Secondary", "Higher-secondary", "University");
        spinner1.setOnItemSelectedListener((view1, position, id, item) -> {
            if (mode != 0) {
                flag1 = String.valueOf(item);
                if (position == 0) spinner2.setItems("Nursery");
                else if (position == 1)
                    spinner2.setItems("Class-I", "Class-II", "Class-III", "Class-IV", "Class-V");
                else if (position == 2) spinner2.setItems("Class-VI", "Class-VII", "Class-VIII");
                else if (position == 3) spinner2.setItems("Class-IX", "Class-X");
                else if (position == 4)
                    spinner2.setItems("HSC-1st Year", "HSC-2nd Year", "Admission-Test");
                else
                    spinner2.setItems("University-1st Year", "University-2nd Year", "University-3rd Year", "University-4th Year");
                spinner2.setSelectedIndex(0);
                flag2 = String.valueOf(spinner2.getItems().get(0));
            } else {
                String it = ", " + item;
                String its = teacherCategory.getText().toString();
                if (its.isEmpty() || its.equals("No Category Selected!")) {
                    teacherCategory.setText(String.valueOf(item));
                } else teacherCategory.setText(its + it);
            }
        });
        spinner2.setItems("Nursery");
        spinner2.setOnItemSelectedListener((view12, position, id, item) -> flag2 = String.valueOf(item));

        spinner3.setItems("Bangla", "English", "Math", "General Science", "Physics", "Chemistry", "Higher Math", "Biology", "Accounting",
                "Management", "Economics", "Others");
        spinner3.setOnItemSelectedListener((view13, position, id, item) -> {
            String its = subjectCategory.getText().toString();
            String it = ", " + item;
            if (its.isEmpty() || its.equals("No Subject Selected!")) {
                subjectCategory.setText(String.valueOf(item));
            } else {
                subjectCategory.setText(its + it);
            }
        });

        division.setItems("BARISAL", "CHITTAGONG", "DHAKA", "MYMENSINGH", "KHULNA", "RAJSHAHI", "RANGPUR", "SYLHET");

        division.setOnItemSelectedListener((view14, position, id, item) -> {
            divisionname = String.valueOf(item);
            if (position == 0) {
                district.setItems("BARISAL", "BARGUNA", "BHOLA", "JHALOKATI", "PATUAKHALI", "PIROJPUR");
            } else if (position == 1) {
                district.setItems("CHITTAGONG", "BANDARBAN", "BRAHMANBARIA", "CHANDPUR", "COMILLA", "COX'S BAZAR", "FENI", "KHAGRACHHARI", "LAKSHMIPUR", "NOAKHALI", "RANGAMATI");
            } else if (position == 2) {
                district.setItems("DHAKA", "FARIDPUR", "GAZIPUR", "GOPALGANJ", "KISHOREGONJ", "MADARIPUR", "MANIKGANJ", "MUNSHIGANJ", "NARAYANGANJ", "NARSINGDI", "RAJBARI", "SHARIATPUR", "TANGAIL");
            } else if (position == 3) {
                district.setItems("MYMENSINGH", "NETRAKONA", "JAMALPUR", "SHERPUR");
            } else if (position == 4) {
                district.setItems("KHULNA", "BAGERHAT", "CHUADANGA", "JESSORE", "JHENAIDAH", "KUSHTIA", "MAGURA", "MEHERPUR", "NARAIL", "SATKHIRA");
            } else if (position == 5) {
                district.setItems("RAJSHAHI", "BOGRA", "JOYPURHAT", "NAOGAON", "NATORE", "CHAPAI NABABGANJ", "PABNA", "SIRAJGANJ");
            } else if (position == 6) {
                district.setItems("RANGPUR", "DINAJPUR", "GAIBANDHA", "KURIGRAM", "LALMONIRHAT", "NILPHAMARI", "PANCHAGARH", "THAKURGAON");
            } else if (position == 7) {
                district.setItems("SYLHET", "HABIGANJ", "MAULVIBAZAR", "SUNAMGANJ");
            }
            districtname = divisionname;

        });

        district.setItems("BARISAL", "BARGUNA", "BHOLA", "JHALOKATI", "PATUAKHALI", "PIROJPUR");
        district.setOnItemSelectedListener((view15, position, id, item) -> districtname = String.valueOf(item));

        spinner4.setItems("1", "2", "3", "4", "5", "6", "7");
        spinner4.setOnItemSelectedListener((view16, position, id, item) -> flag4 = String.valueOf(item));

        spinner5.setItems("1", "2", "3", "4-5", "6-8", "8+");
        spinner5.setOnItemSelectedListener((view17, position, id, item) -> flag5 = String.valueOf(item));

        spinner6.setItems("Negotiable", "<2000 Taka/Month", "2000-3000 Taka/Month", "3000-5000 Taka/Month", "5000-8000 Taka/Month", "8000-10000 Taka/Month", "10000+ Taka/Month");
        spinner6.setOnItemSelectedListener((view18, position, id, item) -> flag6 = String.valueOf(item));

        clearBtn1.setOnClickListener(v -> teacherCategory.setText("No Category Selected!"));

        clearBtn2.setOnClickListener(v -> subjectCategory.setText("No Subject Selected!"));

        currentLocation.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(myContex, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) myContex, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            } else {
                fetchLocation();
            }
        });

        addLocation.setOnClickListener(v -> {
            String sdf = detailedAdd.getText().toString();
            if(!sdf.isEmpty()){
                sdf+= ", "+districtname;
                searchLocation(sdf);
            }else {
                searchLocation(districtname);
            }


        });

        selectLocation.setOnClickListener(v -> {
            ((CenterPage) getActivity()).onMapsSelectClicked();
            waitingmode = true;
        });


        registerButton.setOnClickListener(v -> {
            String dial = "Post Successfully Updated To Server. You Can Now Find it in Your Posts.";
            String errt = "Please Enter All Required Info Properly!";
            String detailadd = detailedAdd.getText().toString();
            if(detailadd.isEmpty()){
                detailedAdd.setError("Required!");
                Toast.makeText(myContex, errt, Toast.LENGTH_SHORT).show();
                return;
            }
            String subject = subjectCategory.getText().toString();
            if(subject.isEmpty() || subject.equals("No Subject Selected!")){
                subjectCategory.setError("Select At Least One Subject!");
                Toast.makeText(myContex, errt, Toast.LENGTH_SHORT).show();
                return;
            }
            if(lang.equals("0") && lant.equals("0")){
                String dfg = "You Haven't select Location in Google Map. Please add it First!";
                Toast.makeText(myContex, dfg, Toast.LENGTH_SHORT).show();
                return;
            }
            String alldetails = detailadd+ ", "+districtname+", "+divisionname;
            controlVisiblity(false);
            if(mode == 0){
                String category = teacherCategory.getText().toString();
                if(category.isEmpty() || category.equals("No Category Selected!")){
                    teacherCategory.setError("Select At Least One Category");
                    Toast.makeText(myContex, errt, Toast.LENGTH_SHORT).show();
                    return;
                }
                registerButton.setEnabled(false);
                ProgressScreen progressScreen = new ProgressScreen(myContex);
                progressScreen.show();
                String key = myRef.child("Posts").child("Teacher").push().getKey();
                myRef.child("Allposts").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).push().setValue(key);
                myRef.child("Posts").child("Teacher").child(Objects.requireNonNull(key)).child("Category").setValue(category);
                myRef.child("Posts").child("Teacher").child(Objects.requireNonNull(key)).child("Subject").setValue(subject);
                myRef.child("Posts").child("Teacher").child(Objects.requireNonNull(key)).child("Day").setValue(flag4);
                myRef.child("Posts").child("Teacher").child(Objects.requireNonNull(key)).child("Salary").setValue(flag6);
                myRef.child("Posts").child("Teacher").child(Objects.requireNonNull(key)).child("AllDetails").setValue(alldetails);
                myRef.child("Posts").child("Teacher").child(Objects.requireNonNull(key)).child("Langi").setValue(lang);
                myRef.child("Posts").child("Teacher").child(Objects.requireNonNull(key)).child("Lati").setValue(lant);
                myRef.child("Posts").child("Teacher").child(Objects.requireNonNull(key)).child("ID").setValue(mAuth.getCurrentUser().getUid());
                progressScreen.dismiss();
            }

            else{
                registerButton.setEnabled(false);
                ProgressScreen progressScreen = new ProgressScreen(myContex);
                progressScreen.show();
                String key = myRef.child("Posts").child("Student").push().getKey();
                myRef.child("Allposts").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).push().setValue(key);
                myRef.child("Posts").child("Student").child(Objects.requireNonNull(key)).child("Category").setValue(flag1);
                myRef.child("Posts").child("Student").child(Objects.requireNonNull(key)).child("Classyear").setValue(flag2);
                myRef.child("Posts").child("Student").child(Objects.requireNonNull(key)).child("Subject").setValue(subject);
                myRef.child("Posts").child("Student").child(Objects.requireNonNull(key)).child("Day").setValue(flag4);
                myRef.child("Posts").child("Student").child(Objects.requireNonNull(key)).child("Number").setValue(flag5);
                myRef.child("Posts").child("Student").child(Objects.requireNonNull(key)).child("Salary").setValue(flag6);
                myRef.child("Posts").child("Student").child(Objects.requireNonNull(key)).child("AllDetails").setValue(alldetails);
                myRef.child("Posts").child("Student").child(Objects.requireNonNull(key)).child("Langi").setValue(lang);
                myRef.child("Posts").child("Student").child(Objects.requireNonNull(key)).child("Lati").setValue(lant);
                myRef.child("Posts").child("Student").child(Objects.requireNonNull(key)).child("ID").setValue(mAuth.getCurrentUser().getUid());
                progressScreen.dismiss();
            }
            Toast.makeText(myContex, dial, Toast.LENGTH_SHORT).show();
            newpost.setVisibility(View.VISIBLE);
        });

        newpost.setOnClickListener(v -> {
            newpost.setVisibility(View.GONE);
            registerButton.setEnabled(true);
            controlVisiblity(true);
        });

        return view;
    }

    void controlVisiblity(boolean kl){
        spinner1.setEnabled(kl);
        spinner2.setEnabled(kl);
        division.setEnabled(kl);
        district.setEnabled(kl);
        spinner3.setEnabled(kl);
        spinner4.setEnabled(kl);
        spinner5.setEnabled(kl);
        spinner6.setEnabled(kl);
        detailedAdd.setEnabled(kl);
        currentLocation.setEnabled(kl);
        addLocation.setEnabled(kl);
        selectLocation.setEnabled(kl);
        clearBtn1.setEnabled(kl);
        clearBtn2.setEnabled(kl);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myContex = context;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            }
            else {
                Toast.makeText(myContex, "Permission Denied!", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void fetchLocation()
    {
        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation1 = location;
                mMap.clear();
                lang = String.valueOf(currentLocation1.getLatitude());
                lant = String.valueOf(currentLocation1.getLongitude());
                LatLng latLng = new LatLng(currentLocation1.getLatitude(), currentLocation1.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Location From Address");
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                mMap.addMarker(markerOptions);
            }else {
                Toast.makeText(myContex, "Location Not Found!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchLocation(String pol)
    {
        Geocoder geoCoder = new Geocoder(myContex, Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocationName(pol, 5);
            if(addresses.size()> 0 ){
                double lat = addresses.get(0).getLatitude();
                double lon = addresses.get(0).getLongitude();
                lang = String.valueOf(lat);
                lant = String.valueOf(lon);
                final LatLng user = new LatLng(lat, lon);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(user).title(pol));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 13));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            }
            else{
                Toast.makeText(myContex, "Location Not Found!", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(myContex, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(waitingmode){
            waitingmode = false;
            String l1 = sharedPreferences.getString("p1", "pp");
            String l2 = sharedPreferences.getString("p2", "qq");
            if(l1.equals("pp") && l2.equals("qq")){
                Toast.makeText(myContex, "Location Not Found!", Toast.LENGTH_LONG).show();
            }
            else {
                if(mMap!=null){
                    lang = l1;
                    lant = l2;
                    LatLng latLng1 = new LatLng(Double.parseDouble(l1), Double.parseDouble(l2));
                    mMap.addMarker(new MarkerOptions().position(latLng1).title("Position"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 13));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                }
            }
        }
    }
}