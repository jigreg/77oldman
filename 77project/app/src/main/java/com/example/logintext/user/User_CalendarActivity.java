package com.example.logintext.user;

import static java.lang.String.format;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.logintext.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class User_CalendarActivity extends AppCompatActivity {

    private String name = null;
    private String uid = null;
    private String walkdata,braindata;
    private float dis;
    private ProgressBar progressBar;

    private TextView myName, walkData, brainData, cal, distance, calorie;
    private ImageButton back;

    private CalendarView calendarView;
    private List<date2> dateList = new ArrayList<>();

    class date2 {
        String time;
        date2(String time) {
            this.time = time;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_calendar);

        myName = (TextView) findViewById(R.id.myName);
        cal = (TextView) findViewById(R.id.cal_Date);
        walkData = (TextView) findViewById(R.id.walkData);
        brainData = (TextView) findViewById(R.id.brainData);
        distance = (TextView) findViewById(R.id.distance);
        calorie = (TextView) findViewById(R.id.calorie);
        back = (ImageButton) findViewById(R.id.back);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //로그인 및 회원가입 엑티비티에서 이름을 받아옴
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users").child("user");
        ref.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.child("name").getValue().toString();
                myName.setText(name+"님의 캘린더");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"onCancelled", Toast.LENGTH_SHORT);
            }
        });


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                cal.setText(format("%d 년 %d 월 %d 일", year, month + 1, dayOfMonth));
                String selectyear = Integer.toString(year);
                String selectmonth = Integer.toString(month + 1);
                String selectday = Integer.toString(dayOfMonth);
                String date = selectyear + selectmonth + selectday;

                //저장된 두뇌훈련 점수 불러오기
                ref.child(uid).child("grade").child("date").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        dateList.clear();
                        for (DataSnapshot messageData : snapshot.getChildren()) {
                            String time = messageData.child("time").getValue().toString();
                            dateList.add(new date2(time));
                        }

                        for (int i = 0; i < dateList.size(); i++) {
                            if (dateList.get(i).time.equals(date)) {
                                braindata = snapshot.child(date).child("cat_grade").getValue().toString();
                                brainData.setText(braindata + " 점");

                                break;
                            } else {
                                brainData.setText("두뇌훈련 해볼까요?");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "onCancelled", Toast.LENGTH_SHORT);
                    }

                });

                //저장된 걸음 수 가져오기

                ref.child(uid).child("walk").child("date").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        dateList.clear();
                        for (DataSnapshot messageData : snapshot.getChildren()) {
                            String time = messageData.child("time").getValue().toString();
                            dateList.add(new date2(time));
                        }

                        for (int i = 0; i < dateList.size(); i++) {
                            if (dateList.get(i).time.equals(date)) {
                                walkdata = snapshot.child(date).child("walking").getValue().toString();
                                walkData.setText(walkdata + " 걸음");

                                dis = Float.parseFloat(walkdata);
                                distance.setText(String.format("%.2f Km ", (dis * 0.0007f)));
                                calorie.setText(String.format("%.2f cal", (dis * 0.0374f)));
                                progressBar.setProgress(Integer.parseInt(walkdata));
                                break;
                            } else {
                                dis = 0;
                                distance.setText(String.format("%.2f Km ", (dis * 0.0007f)));
                                calorie.setText(String.format("%.2f cal", (dis * 0.0374f)));
                                progressBar.setProgress(0);

                                walkData.setText("걸어볼까요?");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "onCancelled", Toast.LENGTH_SHORT);
                    }

                });
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(User_CalendarActivity.this, User_MainActivity.class));
                finish();
            }
        });


    }



}
