package team7.seshealthpatient;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.constraint.Constraints.TAG;

public class ChangePasswordDialog extends DialogPreference {
    private ProgressDialog progressDialog;

    @BindView(R.id.currentPasswordET)
    EditText currentPasswordET;

    @BindView(R.id.newPasswordET)
    EditText newPasswordET;

    @BindView(R.id.newRetypedPasswordET)
    EditText newRetypedPasswordET;

    @Override
    protected View onCreateView(ViewGroup parent) {

        progressDialog = new ProgressDialog(getContext());
        return super.onCreateView(parent);
    }

    @Override
    protected void onBindDialogView(View view) {
        ButterKnife.bind(this, view);
        super.onBindDialogView(view);
    }

    @Override
    protected View onCreateDialogView() {
        return super.onCreateDialogView();
    }

    public ChangePasswordDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_change_password);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult)
            saveNewPassword();
        else
            Toast.makeText(getContext(), "cancel", Toast.LENGTH_SHORT).show();

        super.onDialogClosed(positiveResult);
    }

    private boolean isValidPassword(String target) {
        return target.length() >= 6;
    }

    public void saveNewPassword() {
        String currentPassword = currentPasswordET.getText().toString().trim();
        final String newPassword = newPasswordET.getText().toString().trim();
        String newRetypedPassword = newRetypedPasswordET.getText().toString().trim();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mUser = mAuth.getCurrentUser();

        // Alert users that authenticated with Google that they cannot change their password from this application
        try {
            if (mUser.getProviders().contains("google.com")) {
                Toast.makeText(getContext(), "You cannot change your Google password from here", Toast.LENGTH_LONG).show();
                return;
            }
        } catch(Exception e) {
            Log.d(TAG, e.toString());
        }
        if(!isValidPassword(currentPassword) || !isValidPassword(newPassword) || !isValidPassword(newRetypedPassword)) {
            Toast.makeText(getContext(), "Please fill in all fields with 6 or more characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!newPassword.equals(newRetypedPassword)) {
            Log.d(TAG, "Passwords do not match");
            Toast.makeText(getContext(), "Your new and retyped passwords do not match", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getContext(), "Your current password is incorrect",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "An error occurred, please try again",
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
                            Toast.makeText(getContext(), "Your new password has been set",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "An error occurred during updating the password: " + task.getException());
                            Toast.makeText(getContext(), "An error occurred, please try again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}