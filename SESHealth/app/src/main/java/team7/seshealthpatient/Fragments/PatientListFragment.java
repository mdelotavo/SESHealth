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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import team7.seshealthpatient.Activities.MainActivity;
import team7.seshealthpatient.Activities.PatientPacketsActivity;
import team7.seshealthpatient.Activities.ProfileActivity;
import team7.seshealthpatient.R;

public class PatientListFragment extends Fragment {

    FirebaseUser user;
    String uid;
    ListView listOfPatients;

    public PatientListFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle("Patient List");

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_patient_list, container, false);

        listOfPatients = v.findViewById(R.id.list_of_patients);

        final List<String> patientList = new ArrayList<>();
        final List<String> patientUidList = new ArrayList<>();

        final ListAdapter adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                patientList
        );

        listOfPatients.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String key = child.getKey();
                            String patient = (child.child("Profile").child("name").getValue() != null)
                                    ? child.child("Profile").child("name").getValue().toString() : null;
                            if (patient != null) {
                                patientList.add(patient);
                                patientUidList.add(key);
                                listOfPatients.invalidateViews();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        listOfPatients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String patientUid = patientUidList.get(position);

                Intent patientPackets = new Intent(getActivity(), PatientPacketsActivity.class);
                patientPackets.putExtra("uid", patientUid);
                startActivity(patientPackets);

//                Intent patientPackets = new Intent(getActivity(), ProfileActivity.class);
//                patientPackets.putExtra("uid", patientUid);
//                startActivity(patientPackets);
            }
        });

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
}
