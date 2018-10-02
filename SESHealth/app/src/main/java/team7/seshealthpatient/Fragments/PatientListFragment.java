package team7.seshealthpatient.Fragments;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import team7.seshealthpatient.Activities.PatientPacketsActivity;
import team7.seshealthpatient.Activities.ProfileActivity;
import team7.seshealthpatient.R;

public class PatientListFragment extends Fragment {

    FirebaseUser mUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    String uid;
    ListView listOfPatients;
    ListView listOfPendingPatients;

    @BindView(R.id.doctorKeyTV)
    TextView doctorKeyTV;

    @BindView(R.id.pendingPatientsTV)
    TextView pendingPatientsTV;

    @BindView(R.id.currentPatientsTV)
    TextView currentPatientsTV;

    public PatientListFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle("Patient List");

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = mUser.getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_patient_list, container, false);
        ButterKnife.bind(this, v);

        doctorKeyTV.setText(doctorKeyTV.getText() + " " + mUser.getUid().substring(0, 5));
        listOfPatients = v.findViewById(R.id.list_of_patients);
        listOfPendingPatients = v.findViewById(R.id.list_of_pending_patients);

        final List<String> patientList = new ArrayList<>();
        final List<String> patientUidList = new ArrayList<>();
        final List<String> pendingPatientList = new ArrayList<>();
        final List<String> pendingPatientUidList = new ArrayList<>();

        final ListAdapter patientsAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                patientList
        );

        final ListAdapter pendingPatientsAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                pendingPatientList
        );

        listOfPatients.setAdapter(patientsAdapter);
        listOfPendingPatients.setAdapter(pendingPatientsAdapter);

        reference.child(mUser.getUid()).child("Patients")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        patientList.clear();
                        patientUidList.clear();
                        pendingPatientList.clear();
                        pendingPatientUidList.clear();
                        pendingPatientsTV.setVisibility(View.VISIBLE);
                        currentPatientsTV.setVisibility(View.VISIBLE);

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String key = child.getKey();
                            String patient = (child.child("name").getValue() != null)
                                    ? child.child("name").getValue().toString() : null;
                            if (patient != null) {
                                if(child.child("approved").getValue().toString().equals("false")) {
                                    pendingPatientList.add(patient);
                                    pendingPatientUidList.add(key);
                                    if(pendingPatientsTV.getVisibility() != View.INVISIBLE)
                                        pendingPatientsTV.setVisibility(View.INVISIBLE);
                                } else {
                                    patientList.add(patient);
                                    patientUidList.add(key);
                                    if(currentPatientsTV.getVisibility() != View.INVISIBLE)
                                        currentPatientsTV.setVisibility(View.INVISIBLE);
                                }
                                listOfPatients.invalidateViews();
                                listOfPendingPatients.invalidateViews();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        listOfPendingPatients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String patientUid = pendingPatientUidList.get(position);
                reference.child(mUser.getUid()).child("Patients").child(patientUid).child("approved").setValue(true);
            }
        });

        listOfPatients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String patientUid = patientUidList.get(position);

                Intent patientPackets = new Intent(getActivity(), PatientPacketsActivity.class);
                patientPackets.putExtra("uid", patientUid);
                startActivity(patientPackets);
            }
        });

        // To view the selected user's profile
        listOfPatients.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                String patientUid = patientUidList.get(position);
                Intent patientPackets = new Intent(getActivity(), ProfileActivity.class);
                patientPackets.putExtra("uid", patientUid);
                startActivity(patientPackets);
                return true;
            }
        });

        return v;
    }

    private void setTextView() {

    }
}
