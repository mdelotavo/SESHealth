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

import team7.seshealthpatient.Activities.DiagnosePatientActivity;
import team7.seshealthpatient.R;

public class DiagnosePatientFragment extends Fragment {

    FirebaseUser user;
    String uid;
    ListView listOfPatients;

    public DiagnosePatientFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle("Diagnose");
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_diagnose_patient, container, false);

        listOfPatients = v.findViewById(R.id.list_of_patients);

        final List<String> userList = new ArrayList<>();
        final List<String> userUidList = new ArrayList<>();

        final ListAdapter adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                userList
        );

        listOfPatients.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userList.clear();
                        userUidList.clear();

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String key = child.getKey();

                            String user = (child.child("Profile").child("name").getValue() != null)
                                    ? child.child("Profile").child("name").getValue().toString() : null;

                            String doctor = (child.child("Doctor").child("UID").getValue() != null)
                                    ? child.child("Doctor").child("UID").getValue().toString() : null;

                            if (user != null && uid.equals(doctor)) {
                                userList.add(user);
                                userUidList.add(key);
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
                String userUid = userUidList.get(position);

                Intent intent = new Intent(getActivity(), DiagnosePatientActivity.class);
                intent.putExtra("uid", userUid);
                startActivity(intent);
            }
        });

        return v;
    }
}
