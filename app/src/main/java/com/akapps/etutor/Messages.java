package com.akapps.etutor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class Messages extends Fragment {
    String name, uid;
    SharedPreferences sharedPreferences;
    Context myContex;
    ImageView imageView, pro_pic;
    TextView textView, back_btn, no_msg, pro_name;
    GridView mess, inbox;
    Button btn;
    EditText msg_txt;
    ConstraintLayout constraintLayout;
    ArrayList<StoreMsg> list = new ArrayList<>();
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    StorageReference stRef = FirebaseStorage.getInstance().getReference();
    String myName = "MyName";
    Bitmap bitmap1;
    boolean vali = false;
    String newkey = "ttt";
    String cur_msg = "";
    boolean dirOpen = false;
    boolean isSecondPage = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_messages, container, false);
        imageView = view.findViewById(R.id.imageView6);
        textView = view.findViewById(R.id.textView61);
        mess = view.findViewById(R.id.gridtyy);
        inbox = view.findViewById(R.id.griddetailsmsg);
        back_btn = view.findViewById(R.id.textView57);
        no_msg = view.findViewById(R.id.textView62);
        msg_txt = view.findViewById(R.id.send);
        btn = view.findViewById(R.id.button10);
        pro_pic = view.findViewById(R.id.imageView7);
        pro_name = view.findViewById(R.id.textView63);
        constraintLayout = view.findViewById(R.id.constsa);
        constraintLayout.setVisibility(View.GONE);
        no_msg.setVisibility(View.GONE);
        sharedPreferences = myContex.getSharedPreferences("com.akapps.etutor", Context.MODE_PRIVATE);
        name = sharedPreferences.getString("Name", "No");
        uid = sharedPreferences.getString("Uid", "No");
        if(!name.equals("No") && !uid.equals("No")){
            sharedPreferences.edit().putString("Name", "No").apply();
            sharedPreferences.edit().putString("Uid", "No").apply();
            myRef.child("Messages").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Inbox").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.exists()){
                        vali = true;
                        list.add(new StoreMsg(name, uid));
                    }
                    dirOpen = true;
                    loadView();
                    loadMyProfile();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    loadView();
                }
            });

        }else{
            loadMyProfile();
            loadView();
        }

        back_btn.setOnClickListener(v -> backPressController());


        pro_pic.setOnClickListener(v -> ((MessageActivity) Objects.requireNonNull(getActivity())).startActivityVB());
        pro_name.setOnClickListener(v -> ((MessageActivity) Objects.requireNonNull(getActivity())).startActivityVB());

        btn.setOnClickListener(v -> {
            String msg1 = msg_txt.getText().toString();
            if(msg1.isEmpty()) return;
            if(cur_msg.isEmpty()) return;
            msg_txt.setText("");
            if(msg1.contains("#")) msg1 = msg1.replace("#", "");
            String msg10 = Objects.requireNonNull(mAuth.getCurrentUser()).getUid()+"#"+ msg1+ "#"+ getCurrentTime();
            myRef.child("Chats").child(cur_msg).child("MsgCount").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.exists()){
                        myRef.child("Chats").child(cur_msg).child("MsgCount").setValue("0");
                        myRef.child("Chats").child(cur_msg).child("Msg").child("p0").setValue(msg10);
                        return;
                    }
                    String dfg = Objects.requireNonNull(snapshot.getValue()).toString();
                    Integer plp = Integer.parseInt(dfg) + 1;
                    String lopl = String.valueOf(plp);
                    myRef.child("Chats").child(cur_msg).child("MsgCount").setValue(lopl);
                    myRef.child("Chats").child(cur_msg).child("Msg").child("p"+lopl).setValue(msg10);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        return view;
    }

    public boolean getStat()
    {
        return isSecondPage;
    }

    public void backPressController()
    {
        list.clear();
        loadView();
        dirOpen = false;
        isSecondPage = false;
        ((MessageActivity) Objects.requireNonNull(getActivity())).hideBar(false);
        if(constraintLayout.getVisibility() == View.VISIBLE) constraintLayout.setVisibility(View.GONE);
        if(imageView.getVisibility() == View.GONE) imageView.setVisibility(View.VISIBLE);
        if(textView.getVisibility() == View.GONE) textView.setVisibility(View.VISIBLE);
        if(mess.getVisibility() == View.GONE) mess.setVisibility(View.VISIBLE);
    }

    void loadMyProfile()
    {
        myRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    return;
                }
                myName = Objects.requireNonNull(snapshot.getValue()).toString();
                textView.setText(myName);
                if(vali) setLop();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final long ONE_MEGABYTE = 1024 * 1024;
        stRef.child("ProfilePic").child(mAuth.getCurrentUser().getUid()).getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageView.setImageBitmap(bitmap1);
        }).addOnFailureListener(e -> {

        });
    }

    String getCurrentTime()
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }

    void setLop()
    {
        newkey = myRef.child("Chats").push().getKey();
        myRef.child("Chats").child(newkey).child("MsgCount").setValue("0");
        myRef.child("Messages").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Inbox").child(uid).setValue(name+"#"+newkey);
        myRef.child("Messages").child(uid).child("Inbox").child(mAuth.getCurrentUser().getUid()).setValue(myName+"#"+newkey);
    }
    void loadView()
    {
        myRef.child("Messages").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Inbox").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    if(list.size()>0){
                        if(mess.getVisibility()== View.GONE ) mess.setVisibility(View.VISIBLE);
                        ChatHead chatHead = new ChatHead(list);
                        mess.setAdapter(chatHead);
                        return;
                    }
                    no_msg.setVisibility(View.VISIBLE);
                    mess.setVisibility(View.GONE);
                    return;
                }
                Map<String, String> map = (Map<String, String>) snapshot.getValue();
                if(map == null){
                    if(list.size()>0){
                        if(mess.getVisibility()== View.GONE ) mess.setVisibility(View.VISIBLE);
                        ChatHead chatHead = new ChatHead(list);
                        mess.setAdapter(chatHead);
                        return;
                    }
                    no_msg.setVisibility(View.VISIBLE);
                    mess.setVisibility(View.GONE);
                }
                else {
                    for(Map.Entry<String, String> entry: map.entrySet()){
                        list.add(new StoreMsg(entry.getValue(), entry.getKey()));
                    }
                    if(mess.getVisibility()== View.GONE ) mess.setVisibility(View.VISIBLE);
                    ChatHead chatHead = new ChatHead(list);
                    mess.setAdapter(chatHead);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                no_msg.setVisibility(View.VISIBLE);
                mess.setVisibility(View.GONE);
            }
        });
    }

    void sepChat(String cid, String name, String puid)
    {
        isSecondPage = true;
        ((MessageActivity) Objects.requireNonNull(getActivity())).hideBar(true);
        mess.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        constraintLayout.setVisibility(View.VISIBLE);
        setImage(puid, pro_pic);
        pro_name.setText(name);
        cur_msg = cid;
        sharedPreferences.edit().putString("PlayerIDkk", puid).apply();
        sharedPreferences.edit().putString("Namekkk", myName).apply();
        myRef.child("Chats").child(cid).child("Msg").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!isSecondPage) return;
                if(!snapshot.exists()) return;
                Map<String, String> map = (Map<String, String>) snapshot.getValue();
                if(map == null) return;
                ArrayList<ChatLoader> arrayList = new ArrayList<>();
                for (Map.Entry<String, String> entry : map.entrySet()){
                    String vl = entry.getKey().substring(1);
                    arrayList.add(new ChatLoader(Integer.parseInt(vl), entry.getValue()));

                }
                Collections.sort(arrayList);
                int ghap = arrayList.get(arrayList.size()-1).getValue();
                sharedPreferences.edit().putString(cid, String.valueOf(ghap)).apply();
                ChatMsg chatMsg = new ChatMsg(arrayList);
                inbox.setAdapter(chatMsg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private class ChatMsg extends BaseAdapter{
        ArrayList<ChatLoader> list_array;

        public ChatMsg(ArrayList<ChatLoader> list_array) {
            this.list_array = list_array;
        }

        @Override
        public int getCount() {
            return list_array.size();
        }

        @Override
        public Object getItem(int position) {
            return list_array.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String ss = list_array.get(position).getMsg();
            String[] lpo = ss.split("#");
            if(lpo[0].equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())){
                @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.my_sendones, null);
                TextView tx1 = view.findViewById(R.id.textView25);
                TextView tx2 = view.findViewById(R.id.textView24);
                tx1.setText(lpo[1]);
                tx2.setText(lpo[2]);
                return view;
            }else {
                @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.others_sendones, null);
                TextView tx1 = view.findViewById(R.id.textView25);
                TextView tx2 = view.findViewById(R.id.textView24);
                tx1.setText(lpo[1]);
                tx2.setText(lpo[2]);
                return view;
            }

        }
    }


    void setImage(String upu, ImageView img){

        try {
            final long ONE_MEGABYTE = 1024*1024;
            stRef.child("ProfilePic").child(upu).getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bit = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                img.setImageBitmap(bit);

            }).addOnFailureListener(e -> {

            });
        }catch (Exception ignored){

        }
    }
    void getTextLast(TextView textView, String chatKEY, TextView tex)
    {
        myRef.child("Chats").child(chatKEY).child("MsgCount").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(isSecondPage) return;
                if(!snapshot.exists()){
                    textView.setText("No New Messages!");
                    return;
                }
                String msg_con = Objects.requireNonNull(snapshot.getValue()).toString();
                String my_coun = sharedPreferences.getString(chatKEY, "0");
                boolean flag_val = false;
                if(Integer.parseInt(msg_con)> Integer.parseInt(my_coun)){
                    flag_val = true;
                }
                msg_con = "p"+msg_con;
                loadLastMsg(textView, chatKEY, msg_con, tex, flag_val);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void loadLastMsg(TextView textView, String chatKey, String msgKey, TextView plo, boolean check){
        myRef.child("Chats").child(chatKey).child("Msg").child(msgKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    textView.setText("No Messages");
                }
                else {
                    String last_msg = Objects.requireNonNull(snapshot.getValue()).toString();
                    String[] lists = last_msg.split("#");
                    plo.setText(lists[2]);
                    if(!lists[0].equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())){
                        textView.setText(lists[1]);
                        if(check) textView.setTextColor(Color.parseColor("#DE3163"));
                        else textView.setTextColor(Color.parseColor("#9A9A9A"));
                    }else{
                        textView.setText("you: "+lists[1]);
                        textView.setTextColor(Color.parseColor("#9A9A9A"));
                    }

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

    private class ChatHead extends BaseAdapter{

        ArrayList<StoreMsg> arrayList;

        public ChatHead(ArrayList<StoreMsg> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return getItemId(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = getLayoutInflater();
            @SuppressLint({"ViewHolder", "InflateParams"}) View view = layoutInflater.inflate(R.layout.grid_chathead, null);
            TextView textView = view.findViewById(R.id.textView63);
            TextView textView1 = view.findViewById(R.id.textView79);
            TextView textView2 = view.findViewById(R.id.textView80);
            String[] strings = arrayList.get(position).getName().split("#");
            textView.setText(strings[0]);
            ImageView imageView = view.findViewById(R.id.imageView7);
            setImage(arrayList.get(position).getUid(), imageView);
            if(strings.length<=1){
                getTextLast(textView1, newkey, textView2);
            }
            else{
                getTextLast(textView1, strings[1], textView2);
            }
            textView.setOnClickListener(v -> {
                if(strings.length<=1) sepChat(newkey, strings[0], arrayList.get(position).getUid());
                else sepChat(strings[1], strings[0], arrayList.get(position).getUid());
            });
            textView1.setOnClickListener(v -> {
                if(strings.length<=1) sepChat(newkey, strings[0], arrayList.get(position).getUid());
                else sepChat(strings[1], strings[0], arrayList.get(position).getUid());
            });
            if(dirOpen){
                if(uid!= null && strings.length<=1 && arrayList.get(position).getUid().contains(uid)) sepChat(newkey, strings[0], arrayList.get(position).getUid());
                else if(uid!= null && arrayList.get(position).getUid().contains(uid)) sepChat(strings[1], strings[0], arrayList.get(position).getUid());
            }

            return view;
        }
    }
}
