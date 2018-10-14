package team7.seshealthpatient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import team7.seshealthpatient.R;

public class PatientPacketsActivity extends AppCompatActivity {

    private ListView listOfPackets;


    @BindView(R.id.patientPacketToolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_patient_packets);
        ButterKnife.bind(this);

        listOfPackets = findViewById(R.id.list_of_packets);

        toolbar.setTitle("Packet History");

        // Set up the menu button
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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

        listOfPackets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String packetId = packetIdList.get(position);

                Intent packetInfo = new Intent(PatientPacketsActivity.this, PacketInfoActivity.class);
                packetInfo.putExtra("uid", uid);
                packetInfo.putExtra("packetId", packetId);
                startActivity(packetInfo);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
