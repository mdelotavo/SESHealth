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

public class PacketInfoActivity extends AppCompatActivity {

    ListView listOfPacketInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_packet_info);
        listOfPacketInfo = findViewById(R.id.list_of_packet_info);

        final String uid, packetId;

        Intent receivedIntent = getIntent();
        uid = receivedIntent.getStringExtra("uid");
        packetId = receivedIntent.getStringExtra("packetId");

        final List<String> packetInfoList = new ArrayList<>();

        final ListAdapter adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                packetInfoList
        );

        listOfPacketInfo.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Packets").child(packetId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String key = child.getKey();
                            String info = child.getValue() != null ? child.getValue().toString() : null;
                            if (info != null) {
                                packetInfoList.add("[" + key + "]\n" + info);
                                listOfPacketInfo.invalidateViews();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
