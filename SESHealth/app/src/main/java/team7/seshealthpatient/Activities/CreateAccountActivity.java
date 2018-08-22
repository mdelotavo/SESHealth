package team7.seshealthpatient.Activities;

import team7.seshealthpatient.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateAccountActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    @BindView(R.id.createAccEmailET)
    EditText createAccEmailET;

    @BindView(R.id.createAccPasswordET)
    EditText createAccPasswordET;

    @BindView(R.id.createAccFirstNameET)
    EditText createAccFirstName;

    @BindView(R.id.createAccLastNameET)
    EditText createAccLastName;

    private static String TAG = "CreateAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.createAccount_toolbar);
        toolbar.setTitle(getString(R.string.createAccount_activity_title));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean isValidPassword(CharSequence target) {
        return target.length() >= 6;
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if(view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void checkExceptions(Task<AuthResult> task) {
        String exception = task.getException().getClass().toString();
        if(exception.equals(FirebaseAuthUserCollisionException.class.toString())) {
            Log.d(TAG, task.getException().toString());
            Toast.makeText(CreateAccountActivity.this, R.string.exception_email_already_exists, Toast.LENGTH_LONG).show();
        } else if(exception.equals(FirebaseNetworkException.class.toString())) {
            Log.d(TAG, task.getException().toString());
            Toast.makeText(this, R.string.exception_network_connectivity, Toast.LENGTH_LONG).show();
        };
    }

    @OnClick(R.id.createAccBtn)
    public void createAccount() {
        String email = createAccEmailET.getText().toString().trim();
        String password = createAccPasswordET.getText().toString().trim();
        hideKeyboard();

        if(!isValidEmail(email)) {
            Toast.makeText(this, getString(R.string.emailCheck_toast), Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isValidPassword(password)) {
            Toast.makeText(this, getString(R.string.passwordCheckLength_toast), Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage(getString(R.string.create_account_txt));
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail: success");
                            addUserInformation();
                        } else {
                            checkExceptions(task);
                        }
                    }
            });
    }

    private void addUserInformation() {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        String firstName = createAccFirstName.getText().toString().trim();
        String lastName = createAccLastName.getText().toString().trim();
        Map newPost = new HashMap();
        newPost.put("firstName", firstName);
        newPost.put("lastName", lastName);

        currentUser.setValue(newPost).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    sendVerificationEmail();
                } else {
                    Log.d(TAG, "Setting user information failed" + task.getException());
                    Toast.makeText(CreateAccountActivity.this, "Failed to add user information", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendVerificationEmail() {
        mAuth.getCurrentUser()
            .sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Authentication email sent successfully " + task.getResult());
                        Toast.makeText(CreateAccountActivity.this, R.string.email_authentication_message_success, Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    } else {
                        Log.d(TAG, "Authentication email failed to send " + task.getException());
                        Toast.makeText(CreateAccountActivity.this, R.string.email_authentication_message_failure, Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}
