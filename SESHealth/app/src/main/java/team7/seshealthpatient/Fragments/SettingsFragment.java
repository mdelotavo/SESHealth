package team7.seshealthpatient.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.seshealthpatient.Activities.MainActivity;
import team7.seshealthpatient.R;

public class SettingsFragment extends Fragment {
    private static String TAG = "SettingsFragment";
    private ProgressDialog progressDialog;

    @BindView(R.id.settingsCurrentPwET)
    EditText currentPasswordET;

    @BindView(R.id.settingsNewPwET)
    EditText newPasswordET;

    @BindView(R.id.settingsNewRetypedPwET)
    EditText newRetypedPasswordET;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note the use of getActivity() to reference the Activity holding this fragment
        getActivity().setTitle("Settings");
        progressDialog = new ProgressDialog(getActivity());
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
        // Now that the view has been created, we can use butter knife functionality
    }

    private boolean isValidPassword(CharSequence target) {
        return target.length() >= 6;
    }

    @OnClick(R.id.settingsSaveBtn)
    public void saveNewPassword() {
        String currentPassword = currentPasswordET.getText().toString().trim();
        final String newPassword = newPasswordET.getText().toString().trim();
        String newRetypedPassword = newRetypedPasswordET.getText().toString().trim();
        final FirebaseAuth mAuth = ((MainActivity)getActivity()).getFirebaseAuth();
        final FirebaseUser mUser = mAuth.getCurrentUser();

        // Alert users that authenticated with Google that they cannot change their password from this application
        try {
            if (mUser.getProviders().contains("google.com")) {
                Toast.makeText(getActivity(), "You cannot change your Google password from here", Toast.LENGTH_LONG).show();
                return;
            }
        } catch(Exception e) {
            Log.d(TAG, e.toString());
        }
        if(!isValidPassword(currentPassword) || !isValidPassword(newPassword) || !isValidPassword(newRetypedPassword)) {
            Toast.makeText(getActivity(), "Please fill in all fields with 6 or more characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!newPassword.equals(newRetypedPassword)) {
            Log.d(TAG, "Passwords do not match");
            Toast.makeText(getActivity(), "Your new and retyped passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, mUser.getProviderId() + "\n" + mUser.getProviderData().toString() + "\n" + mUser.getProviders().toString());

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
                AuthCredential credential = EmailAuthProvider
                        .getCredential(mUser.getEmail(), currentPassword);

        progressDialog.setMessage("Setting new password, please wait...");
        progressDialog.show();
        // Prompt the user to re-provide their sign-in credentials
            mUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "User successfully re-authenticated");
                            updatePassword(mUser, newPassword);
                        } else {
                            Log.d(TAG, "User was unable to re-authenticate. " + task.getException());
                            progressDialog.dismiss();
                            if(task.getException().toString().startsWith("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException")) {
                                Toast.makeText(getActivity(), "Your current password is incorrect",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "An error occurred, please try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void updatePassword(FirebaseUser user, String password) {
        final FirebaseUser mUser = user;
        mUser.updatePassword(password)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Log.d(TAG, mUser.getEmail() + " has successfully updated their password");
                        Toast.makeText(getActivity(), "Your new password has been set",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "An error occurred during updating the password: " + task.getException());
                        Toast.makeText(getActivity(), "An error occurred, please try again",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }




}
