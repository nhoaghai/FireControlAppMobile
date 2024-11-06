package com.vn.firecontrolapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String notificationTitle;
    private String notificationMessage;
    private FirebaseFirestore db;
    private FirebaseDatabase database;
    private double highestTemp = 0d;
    private double lowestTemp = 0d;
    private ArrayList<Rooms> historyList = new ArrayList<Rooms>();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Room 0 button
        Button btnRoom0 = findViewById(R.id.btn_room_0);
        btnRoom0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ControlActivity.class);
                startActivity(intent);
            }
        });

        //Room 1 button
        Button btnRoom1 = findViewById(R.id.btn_room_1);
        btnRoom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ControlActivity2.class);
                startActivity(intent);
            }
        });

        //Room 2 button
        Button btnRoom2 = findViewById(R.id.btn_room_2);
        btnRoom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ControlActivity.class);
                startActivity(intent);
            }
        });

        //Room 3 button
        Button btnRoom3 = findViewById(R.id.btn_room_3);
        btnRoom3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ControlActivity.class);
                startActivity(intent);
            }
        });


        //HistoryButton
        Button btnGoToHistoryActivity = findViewById(R.id.btn_go_to_history_activity);
        btnGoToHistoryActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("main onclick", "onClick: " + historyList);
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                intent.putExtra("historyList", historyList);
                startActivity(intent);
            }
        });

        //get historyList
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("fire_control_app")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("fire_control_app").document(document.getId())
                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            Log.d("getRoomList", "Room: " + document.getId() + ", HighestTemp: " + documentSnapshot.get("Highest Temperature") + ", LowestTemp: " + documentSnapshot.get("Lowest Temperature"));
                                            Rooms room = new Rooms(document.getId(), Double.parseDouble(documentSnapshot.get("Highest Temperature").toString()), Double.parseDouble(documentSnapshot.get("Lowest Temperature").toString()));
                                            Log.d("getRoomList", "Rooms: " + room);
                                            historyList.add(room);
                                            Log.d("getRoomList", "historyList: " + historyList);
                                        }
                                    });
                        }
                    }
                });


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference newRef = database.getReference();

        newRef.child("/ESP/ROOM_1/TEMPERATURE").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Double.parseDouble(snapshot.getValue().toString()) > 50) {
                    notificationTitle = "Phòng 1: ";
                    notificationMessage = "Cảnh báo nhiệt độ cao: " + snapshot.getValue().toString() + "ºC";
                    sendNotification(notificationTitle, notificationMessage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        newRef.child("/ESP/ROOM_1/TEMPERATURE").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                highestTemp = Double.parseDouble(task.getResult().getValue().toString());
                lowestTemp = Double.parseDouble(task.getResult().getValue().toString());
                storageData(highestTemp, lowestTemp);
            }
        });

        newRef.child("/ESP/ROOM_1/TEMPERATURE").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Double.parseDouble(snapshot.getValue().toString()) > highestTemp) {
                    highestTemp = Double.parseDouble(snapshot.getValue().toString());
                } else if (Double.parseDouble(snapshot.getValue().toString()) < lowestTemp) {
                    lowestTemp = Double.parseDouble(snapshot.getValue().toString());
                }
                storageData(highestTemp, lowestTemp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sendNotification(String notificationTitle, String notificationMessage) {
        Toast.makeText(MainActivity.this, "Hiển thị thông báo", Toast.LENGTH_SHORT).show();

        Intent resultIntent = new Intent(this, ControlActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(getNotificationId(), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification newNotification = new NotificationCompat.Builder(this, NotificationApplication.CHANNEL_ID)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        NotificationManagerCompat.from(this).notify(getNotificationId(), newNotification);
    }

    private int getNotificationId() {
        return (int) new Date().getTime();
    }

    @NonNull
    private String dateFormat() {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd");
        return ft.format(date);
    }

    private void storageData(double highestTemp, double lowestTemp) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("Highest Temperature", highestTemp);
        data.put("Lowest Temperature", lowestTemp);
        db.collection("fire_control_app").document("Room_0-" + dateFormat())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "Successfully", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}