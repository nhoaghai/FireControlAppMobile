package com.vn.firecontrolapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ControlActivity2 extends AppCompatActivity {
    private ImageButton btnLed, btnPower, btnFan;
    private TextView txtPosition, txtTemperature, txtHumidity;
    private ImageView centerImg;
    private ConstraintLayout background;


    @SuppressLint({"MissingInflatedId", "UseSwitchCompatOrMaterialCode"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_control2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");


        DatabaseReference newRef = database.getReference("ESP/ROOM_1");

        txtPosition = findViewById(R.id.txtPosition);
        newRef.child("ROOM").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtPosition.setText("Phòng: " + snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        txtTemperature = findViewById(R.id.txtTemperature);
        newRef.child("TEMPERATURE").addValueEventListener(new ValueEventListener() {
            @SuppressLint({"ResourceAsColor", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(Double.class) >= 31) {
                    txtTemperature.setTextColor(R.color.highTemp);
                } else {
                    txtTemperature.setTextColor(R.color.normalTemp);
                }
                txtTemperature.setText(snapshot.getValue().toString() + "ºC");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        txtHumidity = findViewById(R.id.txtHumidity);
        newRef.child("HUMIDITY").addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "ResourceAsColor"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(Double.class) > 70) {
                    txtHumidity.setTextColor(R.color.highHumid);
                } else if (snapshot.getValue(Double.class) > 10 & snapshot.getValue(Double.class) < 70) {
                    txtHumidity.setTextColor(R.color.normalTemp);
                } else {
                    txtHumidity.setTextColor(R.color.highTemp);
                }
                txtHumidity.setText(snapshot.getValue().toString() + "%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnLed = findViewById(R.id.btnLed);
        btnLed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newRef.child("LED").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (Boolean.parseBoolean(task.getResult().getValue().toString())){
                            Toast.makeText(ControlActivity2.this, "Tắt Đèn", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ControlActivity2.this, "Bật Đèn", Toast.LENGTH_SHORT).show();
                        }

                        newRef.child("LED").setValue(!Boolean.parseBoolean(task.getResult().getValue().toString()));
                    }
                });
            }
        });

        btnFan = findViewById(R.id.btnFan);
        btnFan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newRef.child("FAN").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (Boolean.parseBoolean(task.getResult().getValue().toString())){
                            Toast.makeText(ControlActivity2.this, "Tắt Quạt", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ControlActivity2.this, "Bật Quạt", Toast.LENGTH_SHORT).show();
                        }
                        newRef.child("FAN").setValue(!Boolean.parseBoolean(task.getResult().getValue().toString()));
                    }
                });
            }
        });

        btnPower = findViewById(R.id.btnPower);
        btnPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newRef.child("PUMP").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (Boolean.parseBoolean(task.getResult().getValue().toString())){
                            Toast.makeText(ControlActivity2.this, "Tắt Vòi Phun", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ControlActivity2.this, "Bật Vòi Phun", Toast.LENGTH_SHORT).show();
                        }
                        newRef.child("PUMP").setValue(!Boolean.parseBoolean(task.getResult().getValue().toString()));
                    }
                });
            }
        });

        Switch switchAuto = (Switch)findViewById(R.id.switch_auto);
        switchAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchAuto.isChecked()){
                    newRef.child("AUTO").setValue(true);
                }
                else {
                    newRef.child("AUTO").setValue(false);
                }
            }
        });


        background = findViewById(R.id.main);
        centerImg = findViewById(R.id.centerImg);
        newRef.child("TEMPERATURE").addValueEventListener(new ValueEventListener() {
            @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(Double.class) >= 31) {
                    background.setBackground(getDrawable(R.drawable.color_fire_red));
                    centerImg.setImageResource(R.drawable.flames);
                } else {
                    background.setBackground(getDrawable(R.drawable.color_normal_blue));
                    centerImg.setImageResource(R.drawable.wind);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
