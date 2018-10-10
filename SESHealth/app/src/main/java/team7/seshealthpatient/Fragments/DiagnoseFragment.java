package team7.seshealthpatient.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import team7.seshealthpatient.Activities.DiagnosisRequest;
import team7.seshealthpatient.Activities.EditDiagnosisRequest;
import team7.seshealthpatient.R;

public class DiagnoseFragment extends Fragment {

    FirebaseUser user;
    String uid;
    ListView listOfDiagnosisRequests;
    Button btnRequest;

    public DiagnoseFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle("Diagnosis Requests");
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_diagnose, container, false);

        listOfDiagnosisRequests = v.findViewById(R.id.list_of_diagnosis_requests);
        btnRequest = v.findViewById(R.id.button_request);

        final List<String> requestList = new ArrayList<>();
        final List<String> requestIdList = new ArrayList<>();
        final List<String> requestSymptoms = new ArrayList<>();

        final ListAdapter adapter = new ArrayAdapter<>(
                getActivity(),
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
                String requestId = requestIdList.get(position);
                String description = requestList.get(position);
                String symptoms = requestSymptoms.get(position);

                Intent intent = new Intent(getActivity(), EditDiagnosisRequest.class);
                intent.putExtra("uid", uid);
                intent.putExtra("requestId", requestId);
                intent.putExtra("description", description);
                intent.putExtra("symptoms", symptoms);
                startActivity(intent);
            }
        });

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DiagnosisRequest.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        return v;
    }
}
