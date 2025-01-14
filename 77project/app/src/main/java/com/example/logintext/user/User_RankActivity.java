package com.example.logintext.user;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class User_RankActivity extends TabActivity {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference,nReference;
    private FirebaseUser user;

    private ImageButton back;
    private TextView myWalkRankNum, myWalkNickname, myWalked, myTrainRankNum, myTrainNickname, myTrained;

    private SimpleDateFormat format;
    private Calendar time;

    private int myWalkRank, myTrainRank;
    private String uid, format_time, Walkrank, Trainrank,Walkpercent,Trainpercent;
    private ListView walk_listView, train_listView;
    private WalkAdapter walk_adapter;
    private TrainAdapter train_adapter;
    private List<Walking> TempWalkingList = new ArrayList<>();
    private List<Walking> WalkingList = new ArrayList<>();

    private List<Training> TempTrainingList = new ArrayList<>();
    private List<Training> TrainingList = new ArrayList<>();

    private TabHost tabHost;


    class Walking {
        String nickname, walked;
        Walking(String nickname, String walked) {
            this.nickname = nickname;
            this.walked = walked;
        }
    }

    class Training {
        String nickname, trained;
        Training(String nickname, String trained) {
            this.nickname = nickname;
            this.trained = trained;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_ranking);

        tabHost = getTabHost();

        TabHost.TabSpec tabSpecWalk = tabHost.newTabSpec("WALKING").setIndicator("걸음별");
        tabSpecWalk.setContent(R.id.walking);
        tabHost.addTab(tabSpecWalk);

        TabHost.TabSpec tabSpecTrain = tabHost.newTabSpec("TRAINING").setIndicator("점수별");
        tabSpecTrain.setContent(R.id.training);
        tabHost.addTab(tabSpecTrain);

        tabHost.setCurrentTab(0);

        back = (ImageButton) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(User_RankActivity.this, User_MainActivity.class));
                finish();
            }
        });

        myWalkRankNum = (TextView) findViewById(R.id.myWalkRankNum);
        myTrainRankNum = (TextView) findViewById(R.id.myTrainRankNum);
        myWalkNickname = (TextView) findViewById(R.id.myWalkNickname);
        myTrainNickname = (TextView) findViewById(R.id.myTrainNickname);
        myWalked = (TextView) findViewById(R.id.myWalked);
        myTrained = (TextView) findViewById(R.id.myTrained);

        walk_listView = (ListView) findViewById(R.id.walk_item);
        walk_adapter = new WalkAdapter(this);
        walk_listView.setAdapter(walk_adapter);

        train_listView = (ListView) findViewById(R.id.train_item);
        train_adapter = new TrainAdapter(this);
        train_listView.setAdapter(train_adapter);

        format = new SimpleDateFormat ( "yyyybMbd");
        time = Calendar.getInstance();

        format_time = format.format(time.getTime());

        mDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        mReference = mDatabase.getReference("Users").child("user");
        nReference = mReference.child(uid).child("today");


        // limitToFirst(숫자) 한정 메소드
        mReference.orderByChild("rank_walking").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int rank = 1;
                TempWalkingList.clear();
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String myUid = messageData.child("uid").getValue().toString();
                    String name = messageData.child("name").getValue().toString();
                    String walked = messageData.child("rank_walking").getValue().toString();

                    TempWalkingList.add(new Walking(name, walked));

                    if (myUid.equals(uid)) {
                        myWalkRank = rank;
                    }
                    rank++;
                }
                int size = TempWalkingList.size()-1;
                WalkingList.clear();
                for (int i = 0; i <= size; i++) {
                    Walking temp = TempWalkingList.get(size-i);
                    WalkingList.add(i, temp);
                }
                walk_adapter.notifyDataSetChanged();
                Walkrank = String.valueOf(WalkingList.size()-myWalkRank+1);
                int walkpercent = (int) ((double)((WalkingList.size()-myWalkRank+1))/(double)(WalkingList.size())*100);
                Walkpercent = String.valueOf(walkpercent);
                myWalkRankNum.setText((WalkingList.size()-myWalkRank+1)+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "onCancelled", Toast.LENGTH_SHORT);
            }
        });

        mReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String walked = dataSnapshot.child("rank_walking").getValue().toString();
                myWalkNickname.setText(name);
                myWalked.setText(walked);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "onCancelled", Toast.LENGTH_SHORT);
            }
        });

        mReference.orderByChild("rank_training").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int rank = 1;
                TempTrainingList.clear();
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String myUid = messageData.child("uid").getValue().toString();
                    String name = messageData.child("name").getValue().toString();
                    String trained = messageData.child("rank_training").getValue().toString();

                    TempTrainingList.add(new Training(name, trained));

                    if (myUid.equals(uid)) {
                        myTrainRank = rank;
                    }
                    rank++;
                }
                int size = TempTrainingList.size()-1;
                for (int i = 0; i <= size; i++) {
                    Training temp = TempTrainingList.get(size-i);
                    TrainingList.add(i, temp);
                }
                train_adapter.notifyDataSetChanged();
                Trainrank = String.valueOf(TrainingList.size()-myTrainRank+1);
                int trainpercent = (int) ((double)((TrainingList.size()-myTrainRank+1))/(double)(TrainingList.size())*100);
                Trainpercent = String.valueOf(trainpercent);
                myTrainRankNum.setText((TrainingList.size()-myTrainRank+1)+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "onCancelled", Toast.LENGTH_SHORT);
            }
        });

        mReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String trained = dataSnapshot.child("rank_training").getValue().toString();
                myTrainNickname.setText(name);
                myTrained.setText(trained);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "onCancelled", Toast.LENGTH_SHORT);
            }
        });
        nReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Map<String, Object> towalk = new HashMap<>();
                    towalk.put("today_walkrank", Walkrank);
                    towalk.put("today_trainrank", Trainrank);
                    towalk.put("today_walkpercent", Walkpercent);
                    towalk.put("today_trainpercent", Trainpercent);
                    nReference.updateChildren(towalk);
                };
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    class WalkAdapter extends BaseAdapter {
        Context context;
        WalkAdapter (Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return WalkingList.size();
        }

        @Override
        public Object getItem(int position) {
            return WalkingList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.walk_ranking_item, parent, false);
            }
            ((TextView)convertView.findViewById(R.id.rankNum)).setText((position+1)+"");
            ((TextView)convertView.findViewById(R.id.nickname)).setText(WalkingList.get(position).nickname);
            ((TextView)convertView.findViewById(R.id.walked)).setText(WalkingList.get(position).walked);
            return convertView;
        }
    }

    class TrainAdapter extends BaseAdapter {
        Context context;
        TrainAdapter (Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return TrainingList.size();
        }

        @Override
        public Object getItem(int position) {
            return TrainingList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.train_ranking_item, parent, false);
            }
            ((TextView)convertView.findViewById(R.id.rankNum)).setText((position+1)+"");
            ((TextView)convertView.findViewById(R.id.nickname)).setText(TrainingList.get(position).nickname);
            ((TextView)convertView.findViewById(R.id.trained)).setText(TrainingList.get(position).trained);
            return convertView;
        }
    }
}
