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
        final ArrayList<Patient> pendingPatientList = new ArrayList<>();

        PatientListAdapter pendingPatientAdapter = new PatientListAdapter(getActivity(), R.layout.adapter_view_pending_patient_layout, pendingPatientList, PatientListFragment.this);
        PatientListAdapter patientAdapter = new PatientListAdapter(getActivity(), R.layout.adapter_view_patient_layout, patientList, PatientListFragment.this);
        listOfPendingPatients.setAdapter(pendingPatientAdapter);
        listOfPatients.setAdapter(patientAdapter);

        reference.child(mUser.getUid()).child("Patients")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        patientList.clear();
                        pendingPatientList.clear();
                        pendingPatientsTV.setVisibility(View.VISIBLE);
                        currentPatientsTV.setVisibility(View.VISIBLE);

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            if (child.exists()) {
                                String patientId = child.getKey();
                                String patientName = (child.child("name").getValue() != null)
                                        ? child.child("name").getValue().toString() : null;
                                Patient patient = new Patient(patientName, patientId);
                                if (child.child("approved").getValue().toString().equals("pending")) {
                                    pendingPatientList.add(patient);
                                    if (pendingPatientsTV.getVisibility() != View.INVISIBLE)
                                        pendingPatientsTV.setVisibility(View.INVISIBLE);
                                } else if (child.child("approved").getValue().toString().equals("accepted")) {
                                    patientList.add(patient);
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

        return v;
    }

    public DatabaseReference getDBReference() {
        return this.reference;
    }
    public FirebaseUser getUser() {
        return mUser;
    }
}
