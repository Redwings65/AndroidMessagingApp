package com.example.seagerdevelopments.bpmessengerapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by brandonseager on 7/31/17.
 */

public class ChatRoom extends AppCompatActivity{
    private Button mSendText;
    private static MobileAnalyticsManager analytics;
    private EditText mWriteMessage;
    private TextView mView;
    private String user_name,chat_room;
    private DatabaseReference root;
    private String random_key;
    public final static String NOTIFICATION_DATA = "NOTIFICATION_DATA";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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
                if(!mWriteMessage.equals("")) {
                    Map<String, Object> myMap = new HashMap<String, Object>();
                    random_key = root.push().getKey();
                    root.updateChildren(myMap);
                    DatabaseReference messages_root = root.child(random_key);
                    Map<String, Object> myMap2 = new HashMap<String, Object>();
                    myMap2.put("name", user_name);
                    myMap2.put("message", mWriteMessage.getText().toString());
                    messages_root.updateChildren(myMap2);
                    mWriteMessage.clearComposingText();

                    createNotiication(Calendar.getInstance().getTimeInMillis(),mWriteMessage.getText().toString());
                    mWriteMessage.setText("");
                }
                else{
                    Toast.makeText(getApplicationContext(),"Message cannot be empty",Toast.LENGTH_LONG).show();


                }

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

           mView.append(username+" : "+message+"\n\n");
       }
   }
   private void createNotiication(long time, String text){
    String notificationContent= "This is a notification";
       String notificationTitle= "You just sent a message";
      Bitmap largeicon = BitmapFactory.decodeResource(getResources(),R.drawable.pic);
        int smallicon = R.drawable.pic;


       Intent intent = new Intent(getApplicationContext(),NotificationDetailActivity.class);
       intent.putExtra(NOTIFICATION_DATA,"Detail : "+text);


       intent.setData(Uri.parse("content://"+time));
       PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent, Intent.FILL_IN_ACTION);

       //PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
       NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

       NotificationCompat.Builder builder;
       builder= new NotificationCompat.Builder(getApplicationContext());

       builder.setWhen(time)
               .setContentText(notificationContent)
               .setContentTitle(notificationTitle)
               .setSmallIcon(smallicon)
               .setTicker(notificationTitle)
               .setLargeIcon(largeicon)
               .setDefaults(Notification.DEFAULT_LIGHTS |
               Notification.DEFAULT_SOUND |
               Notification.DEFAULT_VIBRATE)
               .setContentIntent(pendingIntent);

       Notification notification = builder.build();
       notificationManager.notify((int)time,notification);
       //NOTIFICATION_DATA=


   }
    @Override
    protected void onPause() {
        super.onPause();
        if(analytics != null) {
            analytics.getSessionClient().pauseSession();
            analytics.getEventClient().submitEvents();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(analytics != null) {
            analytics.getSessionClient().resumeSession();
        }
    }

}
