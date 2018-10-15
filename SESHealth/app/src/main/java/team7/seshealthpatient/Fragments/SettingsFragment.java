package team7.seshealthpatient.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.ButterKnife;
import team7.seshealthpatient.Activities.EditInfoActivity;
import team7.seshealthpatient.Activities.LoginActivity;
import team7.seshealthpatient.Activities.MainActivity;
import team7.seshealthpatient.R;

public class SettingsFragment extends PreferenceFragment {

    private static String TAG = "SettingsFragment";
    private AlertDialog.Builder logoutAlertBuilder;
    private AlertDialog logoutAlert;
    private AlertDialog.Builder aboutAlertBuilder;
    private AlertDialog aboutAlert;
    private String[] doctorInformation;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String[] doctorProfile;
    private String currentDoctorUID;

    public SettingsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        doctorProfile = new String[4];

        setOnPreferenceClickListeners();
        initAlertBuilders();

        doctorInformation = ((MainActivity) getActivity()).getDoctorProfile();

        // Note the use of getActivity() to reference the Activity holding this fragment
        getActivity().setTitle("Settings");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, v);

        setDoctorProfileSummary();

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setOnPreferenceClickListeners() {
        Preference editUserInfoPreference = findPreference("editUserInfoPreference");
        editUserInfoPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getContext(), EditInfoActivity.class));
                return false;
            }
        });

        Preference logOutPreference = findPreference("logOutPreference");
        logOutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                logoutAlert.show();
                return false;
            }
        });

        Preference aboutPreference = findPreference("aboutPreference");
        aboutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                aboutAlert.show();
                return false;
            }
        });
    }

    public void initAlertBuilders() {
        logoutAlertBuilder = new AlertDialog.Builder(getContext());
        logoutAlertBuilder.setMessage("Are you sure you want to log out?");
        logoutAlertBuilder.setCancelable(true);

        logoutAlertBuilder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                });

        logoutAlertBuilder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        logoutAlert = logoutAlertBuilder.create();

        aboutAlertBuilder = new AlertDialog.Builder(getContext());
        aboutAlertBuilder.setMessage("This application was developed by the Software Engineering Studio 1B Team 7");
        aboutAlertBuilder.setCancelable(true);

        aboutAlertBuilder.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        aboutAlert = aboutAlertBuilder.create();
    }

    public String doctorSummary() {
        return "Location: " + doctorInformation[1] + "\n" + "Occupation: " + doctorInformation[2] + "\n" + "Key: " + doctorInformation[3];
    }

    public String doctorInfo() {
        return "Location: " + doctorInformation[1] + "\n" + "Occupation: " + doctorInformation[2];
    }

    public void setDoctorProfileSummary() {
        if (((MainActivity) getActivity()).getAccountType().equals("doctor")) {
            getPreferenceScreen().removePreference(getPreferenceManager().findPreference("editUserInfoPreference"));
            getPreferenceScreen().removePreference(getPreferenceManager().findPreference("connectPasswordPreference"));
            reference.child("Profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    findPreference("doctorSummaryPreference").setTitle(doctorInformation[0]);
                    findPreference("doctorSummaryPreference").setSummary(doctorSummary());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (((MainActivity) getActivity()).getAccountType().equals("patient")) {
            getPreferenceScreen().removePreference(getPreferenceManager().findPreference("editDoctorInfoPreference"));
            reference.child("Doctor").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null || dataSnapshot.child("approved").getValue().toString().equals("pending")) {
                        doctorProfile[0] = "no doctor";
                        getPreferenceScreen().removePreference(getPreferenceManager().findPreference("doctorSummaryPreferenceGroup"));
                    } else {
                        currentDoctorUID = dataSnapshot.child("UID").getValue().toString();
                        doctorProfile[3] = dataSnapshot.child("UID").getValue().toString().substring(0, 5);
                        setDoctorProfile("name", 0, false);
                        setDoctorProfile("location", 1, false);
                        setDoctorProfile("occupation", 2, false);
                        onCreate(new Bundle());
                        getPreferenceScreen().removePreference(getPreferenceManager().findPreference("editDoctorInfoPreference"));
                        findPreference("doctorSummaryPreferenceGroup").setTitle("Current Doctor");
                        findPreference("doctorSummaryPreference").setTitle(doctorInformation[0]);
                        findPreference("doctorSummaryPreference").setSummary(doctorInfo());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void setDoctorProfile(String childKey, final int index, boolean isDoctor) {
        if (isDoctor) {
            reference.child("Profile").child(childKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null)
                        doctorProfile[index] = dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            DatabaseReference databaseReference = database.getReference("Users").child(currentDoctorUID);
            databaseReference.child("Profile").child(childKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null)
                        doctorProfile[index] = dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
