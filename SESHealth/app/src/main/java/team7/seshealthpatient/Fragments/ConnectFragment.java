package team7.seshealthpatient.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
    private String doctorKey = "";
    private String currentDoctorId = "";
    final Map<String, String> doctorUidList = new HashMap<>();

    private boolean hasDoctor = false;
    private AlertDialog.Builder askPatientAlertBuilder;
    private AlertDialog askPatientAlert;

    @BindView(R.id.connectET)
    EditText connectET;

    @BindView(R.id.connectStatusTV)
    TextView connectStatusTV;

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
        listAllDoctors(); // Lists all the accounts with 'accountType' equal to "doctor"
        usersReference.child(mUser.getUid()).child("Doctor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("UID").getValue() != null) {
                    try {
                        Log.d(TAG, dataSnapshot.child("approved").getValue().toString());
                        currentDoctorId = dataSnapshot.child("UID").getValue().toString();
                        switch (dataSnapshot.child("approved").getValue().toString()) {
                            case "declined":
                                connectStatusTV.setText("Declined");
                                break;
                            case "pending":
                                connectStatusTV.setText("Pending");
                                hasDoctor = true;
                                break;
                            case "accepted":
                                connectStatusTV.setText("Approved");
                                hasDoctor = true;
                                break;
                        }
                    } catch(Exception e) {
                        Log.d(TAG, e.toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        askPatientAlertBuilder = new AlertDialog.Builder(getContext());
        askPatientAlertBuilder.setMessage(R.string.connect_check_patient_is_sure);
        askPatientAlertBuilder.setCancelable(true);

        askPatientAlertBuilder.setPositiveButton(
                "Accept",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        replaceDoctor();
                        dialog.cancel();
                    }
                });

        askPatientAlertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });

        askPatientAlert = askPatientAlertBuilder.create();

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


    private void listAllDoctors() {
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    name = dataSnapshot.child(mUser.getUid()).child("Profile").child("name").getValue().toString();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String key = child.getKey();
                        String doctor = (child.child("Profile").child("name").getValue() != null && child.child("accountType").getValue().toString().equals("doctor"))
                                ? child.child("Profile").child("name").getValue().toString() : null;
                        if (doctor != null) {
                            doctorUidList.put(key.substring(0, 5), key);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.connectBtn)
    public void clicked() {
        doctorKey = connectET.getText().toString().trim();
        Log.d(TAG, doctorUidList.toString());
        if (doctorUidList.containsKey(doctorKey) && !hasDoctor) {
            addNewPatient();
        } else if (doctorUidList.containsKey(doctorKey) && hasDoctor) {
            alertPatient();
        } else {
            Toast.makeText(getActivity(), R.string.connect_invalid_key, Toast.LENGTH_SHORT).show(); // Fix Toast message
            Log.d(TAG, doctorKey + " is not found in " + doctorUidList.toString());
        }
    }

    private void alertPatient() {
        askPatientAlert.show();
    }

    private void addNewPatient() {
        final DatabaseReference doctorReference = usersReference.child(doctorUidList.get(doctorKey)).child("Patients").child(mUser.getUid());
        Log.d(TAG, "NAME is: " + name);
        doctorReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null) {
                    createPatientDoctorConnection(doctorReference);
                } else {
                    if(dataSnapshot.child("approved").getValue().toString().equals("declined")) {
                        replaceDoctor();
                    } else if (dataSnapshot.getValue() == null) {
                        createPatientDoctorConnection(doctorReference);
                    } else {
                        Log.d(TAG, dataSnapshot.getValue().toString());
                        Toast.makeText(getActivity(), R.string.connect_request_made, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void replaceDoctor() {
        DatabaseReference newDoctorReference = usersReference.child(doctorUidList.get(doctorKey)).child("Patients").child(mUser.getUid());
        DatabaseReference currentDoctorReference = usersReference.child(currentDoctorId).child("Patients").child(mUser.getUid());

        currentDoctorReference.child("approved").removeValue();
        currentDoctorReference.child("name").removeValue();
        newDoctorReference.child("name").setValue(name);
        newDoctorReference.child("approved").setValue("pending");
        usersReference.child(mUser.getUid()).child("Doctor").child("UID").setValue(doctorUidList.get(doctorKey));
        usersReference.child(mUser.getUid()).child("Doctor").child("approved").setValue("pending");
        Toast.makeText(getActivity(), R.string.connect_with_doctor, Toast.LENGTH_SHORT).show();

    }


    private void createPatientDoctorConnection(DatabaseReference doctorReference) {
        doctorReference.child("name").setValue(name);
        doctorReference.child("approved").setValue("pending");
        usersReference.child(mUser.getUid()).child("Doctor").child("UID").setValue(doctorUidList.get(doctorKey));
        usersReference.child(mUser.getUid()).child("Doctor").child("approved").setValue("pending");
        Toast.makeText(getActivity(), R.string.connect_with_doctor, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.viewDoctorProfileBtn)
    public void viewProfileClicked() {
        usersReference.child(mUser.getUid()).child("Doctor").child("UID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    String UID = dataSnapshot.getValue().toString();
                    Intent patientPackets = new Intent(getActivity(), ProfileActivity.class);
                    patientPackets.putExtra("uid", UID);
                    startActivity(patientPackets);
                } else {
                    Toast.makeText(getActivity(), R.string.connect_no_doctor, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "An error occurred, please restart the app and try again...", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
