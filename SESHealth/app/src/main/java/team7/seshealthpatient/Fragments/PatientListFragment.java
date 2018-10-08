package team7.seshealthpatient.Fragments;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.DragEvent;
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
import team7.seshealthpatient.Patient;
import team7.seshealthpatient.PatientListAdapter;
import team7.seshealthpatient.R;

public class PatientListFragment extends Fragment {
    private final String TAG = "PatientListFragment";
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

        final ArrayList<Patient> patientList = new ArrayList<>();
        final ArrayList<Patient> patientUidList = new ArrayList<>();
        final ArrayList<Patient> pendingPatientList = new ArrayList<>();
        final ArrayList<Patient> pendingPatientUidList = new ArrayList<>();

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

        if(getActivity() != null) {
            PatientListAdapter patientAdapter = new PatientListAdapter(getActivity(), R.layout.adapter_view_patient_layout, patientList);
            listOfPatients.setAdapter(patientAdapter);
            listOfPendingPatients.setAdapter(patientAdapter);

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
                                String patientId = child.getKey();
                                String patientName = (child.child("name").getValue() != null)
                                        ? child.child("name").getValue().toString() : null;
                                if (patientName != null) {
                                    Patient patient = new Patient(patientName, patientId);
                                    if (child.child("approved").getValue().toString().equals("pending")) {
                                        pendingPatientList.add(patient);
                                        pendingPatientUidList.add(patient);
                                        if (pendingPatientsTV.getVisibility() != View.INVISIBLE)
                                            pendingPatientsTV.setVisibility(View.INVISIBLE);
                                    } else if (child.child("approved").getValue().toString().equals("accepted")) {
                                        patientList.add(patient);
                                        patientUidList.add(patient);
                                        if (currentPatientsTV.getVisibility() != View.INVISIBLE)
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
                    Patient patientUid = pendingPatientUidList.get(position);
                    reference.child(mUser.getUid()).child("Patients").child(patientUid.getId()).child("approved").setValue("accepted");
                    reference.child(patientUid.getId()).child("Doctor").child("approved").setValue("accepted");
                }
            });

            listOfPendingPatients.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
//                String patientUid = pendingPatientUidList.get(position);
//                Intent patientPackets = new Intent(getActivity(), ProfileActivity.class);
//                patientPackets.putExtra("uid", patientUid);
//                startActivity(patientPackets);
//                return true;
                    Patient patientUid = pendingPatientUidList.get(position);
                    reference.child(mUser.getUid()).child("Patients").child(patientUid.getId()).child("approved").setValue("declined");
                    reference.child(patientUid.getId()).child("Doctor").child("approved").setValue("declined");
                    return true;
                }
            });

            listOfPatients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Patient patientUid = patientUidList.get(position);

                    Intent patientPackets = new Intent(getActivity(), PatientPacketsActivity.class);
                    patientPackets.putExtra("uid", patientUid.getId());
                    startActivity(patientPackets);
                }
            });

            // To view the selected user's profile
            listOfPatients.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Patient patientUid = patientUidList.get(position);
                    Intent patientPackets = new Intent(getActivity(), ProfileActivity.class);
                    patientPackets.putExtra("uid", patientUid.getId());
                    startActivity(patientPackets);
                    return true;
                }
            });
        }

        return v;
    }
}
