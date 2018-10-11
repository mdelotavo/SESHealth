package team7.seshealthpatient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

import team7.seshealthpatient.R;

public class DiagnosePatientActivity extends AppCompatActivity {

    ListView listOfDiagnosisRequests;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_diagnose_patient);

        Intent receivedIntent = getIntent();
        final String uid = receivedIntent.getStringExtra("uid");

        listOfDiagnosisRequests = findViewById(R.id.list_of_diagnosis_requests);

        final List<String> requestList = new ArrayList<>();
        final List<String> requestIdList = new ArrayList<>();
        final List<String> requestSymptoms = new ArrayList<>();

        final ListAdapter adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                requestList
        );

        listOfDiagnosisRequests.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Diagnose")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        requestList.clear();
                        requestIdList.clear();
                        requestSymptoms.clear();

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String key = child.getKey();

                            String description = child.child("description").getValue() != null
                                    ? child.child("description").getValue().toString() : null;

                            String symptoms = child.child("symptoms").getValue() != null
                                    ? child.child("symptoms").getValue().toString() : null;

                            if (description != null) {
                                requestList.add(description);
                                requestIdList.add(key);
                                requestSymptoms.add(symptoms);
                                listOfDiagnosisRequests.invalidateViews();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        listOfDiagnosisRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String description = requestList.get(position);
                String symptoms = requestSymptoms.get(position);

                Intent intent = new Intent(DiagnosePatientActivity.this, DiagnosePatientRequestActivity.class);
                intent.putExtra("description", description);
                intent.putExtra("symptoms", symptoms);
                startActivity(intent);
            }
        });
    }


}
