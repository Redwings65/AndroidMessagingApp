package com.example.seagerdevelopments.bpmessengerapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    private Button mButton;
    private EditText mEditText;
    private ListView mList;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list = new ArrayList<>();
    private String name;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.add_chatroom_button);
        mList = (ListView) findViewById(R.id.listView);
        mEditText = (EditText) findViewById(R.id.add_chatroom);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);

        mList.setAdapter(arrayAdapter);

        request_user();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> myMap = new HashMap<String, Object>();
                myMap.put(mEditText.getText().toString(),"");
                root.updateChildren(myMap);
            }
        });

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }
                list.clear();
                list.addAll(set);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //nothing needs to go here
            }
        });
            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getApplicationContext(), ChatRoom.class);
                    intent.putExtra("room_name",((TextView)view).getText().toString());
                    intent.putExtra("user_name",name);
                    startActivity(intent);
                }
            });


    }//end of OnCreate

    public void request_user(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("enter your name");

        final EditText input = new EditText(this);

        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                name = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancelled", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                request_user();
            }
        });
        builder.show();


    }
}//end of Class
