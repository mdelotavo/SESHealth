package team7.seshealthpatient;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConnectDoctorDialog extends DialogPreference {

    private final static String TAG = "ConnectDialog";
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

    @BindView(R.id.doctorIDET)
    EditText doctorIDET;

    @Override
    protected View onCreateView(ViewGroup parent) {

        database = FirebaseDatabase.getInstance();
        usersReference = database.getReference("Users");
        mUser = FirebaseAuth.getInstance().getCurrentUser();

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

        return super.onCreateView(parent);
    }

    @Override
    protected void onBindDialogView(View view) {
        ButterKnife.bind(this, view);
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
                                break;
                            case "pending":
                                hasDoctor = true;
                                break;
                            case "accepted":
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

        super.onBindDialogView(view);
    }

    @Override
    protected View onCreateDialogView() {
        return super.onCreateDialogView();
    }

    public ConnectDoctorDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_connect_doctor);
        setDialogMessage("Enter the 5 character key code provided to you by your doctor");
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult)
            connectToDocotor();

        super.onDialogClosed(positiveResult);
    }

    public void connectToDocotor() {
        doctorKey = doctorIDET.getText().toString().trim();
        Log.d(TAG, doctorUidList.toString());
        if (doctorUidList.containsKey(doctorKey) && !hasDoctor) {
            addNewPatient();
        } else if (doctorUidList.containsKey(doctorKey) && hasDoctor) {
            alertPatient();
        } else {
            Toast.makeText(getContext(), R.string.connect_invalid_key, Toast.LENGTH_SHORT).show(); // Fix Toast message
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
                        Toast.makeText(getContext(), R.string.connect_request_made, Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getContext(), R.string.connect_with_doctor, Toast.LENGTH_SHORT).show();

    }


    private void createPatientDoctorConnection(DatabaseReference doctorReference) {
        doctorReference.child("name").setValue(name);
        doctorReference.child("approved").setValue("pending");
        usersReference.child(mUser.getUid()).child("Doctor").child("UID").setValue(doctorUidList.get(doctorKey));
        usersReference.child(mUser.getUid()).child("Doctor").child("approved").setValue("pending");
        Toast.makeText(getContext(), R.string.connect_with_doctor, Toast.LENGTH_SHORT).show();
    }


    private void listAllDoctors() {
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child(mUser.getUid()).child("Profile").child("name").getValue().toString();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if(child.exists()) {
                        String key = child.getKey();
                        String doctor = (child.child("Profile").child("name").getValue() != null && child.child("accountType").getValue().toString().equals("doctor"))
                                ? child.child("Profile").child("name").getValue().toString() : null;
                        if (doctor != null) {
                            Log.d(TAG, key);
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

}