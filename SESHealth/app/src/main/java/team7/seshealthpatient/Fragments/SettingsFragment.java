package team7.seshealthpatient.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

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

    public SettingsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        setOnPreferenceClickListeners();
        initAlertBuilders();

        // Note the use of getActivity() to reference the Activity holding this fragment
        getActivity().setTitle("Settings");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, v);
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
        aboutAlertBuilder.setMessage("This application was developed by Software Engineering Studio 1B Team 7");
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
}
