package com.example.firebasechatuppgift;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    Button msgButton;
    EditText sendMsg;

    ListView msgList;
    ArrayList<String> msgListArray = new ArrayList<String>();
    ArrayAdapter arrayAdapt;

    String username, selectedTopic, userMsg;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        msgButton = (Button)findViewById(R.id.msgButton);
        sendMsg = (EditText)findViewById(R.id.sendMsg);

        msgList = (ListView)findViewById(R.id.msgList);
        arrayAdapt = new ArrayAdapter(this, android.R.layout.simple_list_item_1, msgListArray);
        msgList.setAdapter(arrayAdapt);

        username = getIntent().getExtras().get("user_name").toString();
        selectedTopic = getIntent().getExtras().get("selected_topic").toString();
        setTitle("Topic: "+selectedTopic);

        reference = FirebaseDatabase.getInstance().getReference().child(selectedTopic);

        msgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sendMsg.toString().isEmpty()){
                    Toast.makeText(ChatActivity.this, "Message can't be empty!", Toast.LENGTH_SHORT).show();
                }else{
                    Map<String, Object> map = new HashMap<String, Object>();
                    userMsg = reference.push().getKey();
                    reference.updateChildren(map);

                    DatabaseReference ref = reference.child(userMsg);
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("msg", sendMsg.getText().toString());
                    map2.put("user", username);
                    ref.updateChildren(map2);
                }

                sendMsg.setText("");

            }
        });

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateConversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateConversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void updateConversation(DataSnapshot dataSnapshot){
        String msg;
        String user;
        String conversation;
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while(iterator.hasNext()){
            msg = (String) ((DataSnapshot)iterator.next()).getValue();
            user = (String) ((DataSnapshot)iterator.next()).getValue();

            conversation = user + ": " + msg;
            arrayAdapt.insert(conversation, 0);
            arrayAdapt.notifyDataSetChanged();
        }
    }
}
