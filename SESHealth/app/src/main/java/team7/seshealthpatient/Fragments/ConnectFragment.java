package team7.seshealthpatient.Fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.telecom.ConnectionService;
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
import team7.seshealthpatient.Activities.ProfileActivity;
import team7.seshealthpatient.R;

public class ConnectFragment extends Fragment {

    private final static String TAG = "ConnectFragment";
    private FirebaseUser mUser;
    private FirebaseDatabase database;
    private DatabaseReference usersReference;
    private String name = "";
    final Map<String, String> doctorUidList = new HashMap<>();

    @BindView(R.id.connectET)
    EditText connectET;

    public ConnectFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle("Connect!");
        database = FirebaseDatabase.getInstance();
        usersReference = database.getReference("Users");
        mUser = ((MainActivity)getActivity()).getFirebaseAuth().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_connect, container, false);

        ButterKnife.bind(this, v);

        usersReference.addValueEventListener(new ValueEventListener() {
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

    private void addNewPatient(final String doctorId) {
        final DatabaseReference doctorReference = usersReference.child(doctorUidList.get(doctorId)).child("Patients").child(mUser.getUid());
        Log.d(TAG, "NAME is: " + name);
        doctorReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    Log.d(TAG, dataSnapshot.getValue().toString());
                    Toast.makeText(getActivity(), "A request has already been made to your doctor", Toast.LENGTH_SHORT).show();
                }   else {
                    doctorReference.child("approved").setValue(false);
                    doctorReference.child("name").setValue(name);
                    usersReference.child(mUser.getUid()).child("Doctor").child("UID").setValue(doctorUidList.get(doctorId));
                    usersReference.child(mUser.getUid()).child("Doctor").child("approved").setValue(false);

                    Toast.makeText(getActivity(), "A request has been made to your doctor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.viewDoctorProfileBtn)
    public void viewProfileClicked() {
        usersReference.child(mUser.getUid()).child("Doctor").child("UID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String UID = dataSnapshot.getValue().toString();
                Intent patientPackets = new Intent(getActivity(), ProfileActivity.class);
                patientPackets.putExtra("uid", UID);
                startActivity(patientPackets);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "An error occurred, please restart the app and try again...", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
