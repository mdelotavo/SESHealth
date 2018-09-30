package team7.seshealthpatient.Fragments;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.seshealthpatient.Activities.MainActivity;
import team7.seshealthpatient.R;


public class ConnectFragment extends Fragment {

    private final static String TAG = "ConnectFragment";
    private FirebaseUser mUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String name = "";
    final Map<String, String> doctorUidList = new HashMap<>();

    @BindView(R.id.connectET)
    EditText connectET;


    public ConnectFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note the use of getActivity() to reference the Activity holding this fragment
        getActivity().setTitle("Connect!");
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");
        mUser = ((MainActivity)getActivity()).getFirebaseAuth().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_connect, container, false);

        // Note how we are telling butter knife to bind during the on create view method
        ButterKnife.bind(this, v);

        FirebaseDatabase.getInstance().getReference().child("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        name = dataSnapshot.child(mUser.getUid()).child("Profile").child("name").getValue().toString();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String key = child.getKey();
                            String doctor = (child.child("Profile").child("name").getValue() != null && child.child("accountType").getValue().toString().equals("doctor"))
                                    ? child.child("Profile").child("name").getValue().toString() : null;
                            if (doctor != null) {
                                Log.d(TAG, key);
                                doctorUidList.put(key.substring(0, 5), key);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Now that the view has been created, we can use butter knife functionality
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_info, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setPatientView() {
        TextView occupation = new TextView(getActivity());
    }

    private void setDoctorView() {
        TextView occupation = new TextView(getActivity());
    }

    @OnClick(R.id.connectBtn)
    public void clicked() {
        String doctorId = connectET.getText().toString().trim();
        Log.d(TAG, doctorUidList.toString());
        if(doctorUidList.containsKey(doctorId)) {
            addNewPatient(doctorId);
        } else {
            Toast.makeText(getActivity(), "Please enter a valid code", Toast.LENGTH_SHORT).show(); // Fix Toast message
            Log.d(TAG, doctorId + " is not found in " + doctorUidList.toString());
        }

    }

    private void addNewPatient(String doctorId) {
        final DatabaseReference ref = reference.child(doctorUidList.get(doctorId)).child("Patients").child(mUser.getUid());
        Log.d(TAG, "NAME is: " + name);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    Log.d(TAG, dataSnapshot.getValue().toString());
                    Toast.makeText(getActivity(), "A request has already been made to your doctor", Toast.LENGTH_SHORT).show();
                }   else {
                    ref.child("approved").setValue(false);
                    ref.child("name").setValue(name);
                    Toast.makeText(getActivity(), "A request has been made to your doctor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
