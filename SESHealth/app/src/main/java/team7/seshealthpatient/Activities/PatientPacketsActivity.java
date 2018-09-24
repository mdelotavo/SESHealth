package team7.seshealthpatient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import team7.seshealthpatient.R;

public class PatientPacketsActivity extends AppCompatActivity {

    ListView listOfPackets;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_patient_packets);
        listOfPackets = findViewById(R.id.list_of_packets);

        final String uid;

        Intent receivedIntent = getIntent();
        uid = receivedIntent.getStringExtra("uid");

        final List<String> packetList = new ArrayList<>();
        final List<String> packetIdList = new ArrayList<>();

        final ListAdapter adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                packetList
        );

        listOfPackets.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Packets")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String key = child.getKey();
                            String timestamp = child.child("Timestamp").getValue() != null
                                    ? child.child("Timestamp").getValue().toString() : null;
                            if (timestamp != null) {
                                packetList.add(timestamp);
                                packetIdList.add(key);
                                listOfPackets.invalidateViews();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
