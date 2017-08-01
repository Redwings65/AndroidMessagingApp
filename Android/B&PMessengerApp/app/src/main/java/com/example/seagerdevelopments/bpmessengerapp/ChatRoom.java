package com.example.seagerdevelopments.bpmessengerapp;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by brandonseager on 7/31/17.
 */

public class ChatRoom extends AppCompatActivity{
    private Button mSendText;
    private EditText mWriteMessage;
    private TextView mView;
    private String user_name,chat_room;
    private DatabaseReference root;
    private String random_key;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        mSendText = (Button) findViewById(R.id.button);
        mView = (TextView) findViewById(R.id.textView);
        mWriteMessage = (EditText) findViewById(R.id.editText);

        user_name = getIntent().getExtras().get("user_name").toString();
        chat_room = getIntent().getExtras().get("room_name").toString();
        setTitle("Room - "+chat_room);
        root = FirebaseDatabase.getInstance().getReference().child(chat_room);

        mSendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> myMap = new HashMap<String, Object>();
                random_key = root.push().getKey();
                root.updateChildren(myMap);
                DatabaseReference messages_root = root.child(random_key);
                Map<String,Object> myMap2 = new HashMap<String, Object>();
                myMap2.put("name",user_name);
                myMap2.put("message",mWriteMessage.getText().toString());
                messages_root.updateChildren(myMap2);
            }
        });
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_application(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_application(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private String username,message;
   public void append_chat_application(DataSnapshot dataSnapShot){
       Iterator i = dataSnapShot.getChildren().iterator();
       while(i.hasNext()){
           message = (String) ((DataSnapshot)i.next()).getValue();
           username = (String) ((DataSnapshot)i.next()).getValue();

           mView.append(username+" : "+message+"\n");
       }
   }
}
