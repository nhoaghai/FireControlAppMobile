package com.vn.firecontrolapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class HistoryActivity extends AppCompatActivity {

    public ArrayList<Rooms> historyList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        String listSerializedToJson = getIntent().getExtras().getString("historyList");
        historyList = (ArrayList<Rooms>) getIntent().getSerializableExtra("historyList");

        Log.d("History", "historyList: " + historyList);
        Log.d("History", "room1: " + historyList.get(0).getRoom());

        ListView historyView = findViewById(R.id.historyView);

        CustomArrayAdapter customArrayAdapter = new CustomArrayAdapter(HistoryActivity.this, R.layout.layout_row_item, historyList);

        historyView.setAdapter(customArrayAdapter);
    }
}