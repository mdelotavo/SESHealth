package team7.seshealthpatient.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.seshealthpatient.R;


public class CreateAccountActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    @BindView(R.id.createEmailET)
    EditText createEmailET;

    @BindView(R.id.createPasswordET)
    EditText createPasswordET;


    private static String TAG = "CreateAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified()) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        };

        toolbar = findViewById(R.id.createAccount_toolbar);
        toolbar.setTitle(getString(R.string.createAccount_activity_title));
        progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean isValidPassword(CharSequence target) {
        return target.length() >= 6;
    }

    @OnClick(R.id.createAccountBtn)
    public void createAccount() {
        String email = createEmailET.getText().toString().trim();
        String password = createPasswordET.getText().toString().trim();
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
                            Log.d(TAG, "createUserWithEmail:success");
                            sendVerificationEmail();
                            Toast.makeText(CreateAccountActivity.this, R.string.email_authentication_message, Toast.LENGTH_LONG).show();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
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
                        } else {
                            Log.d(TAG, "Authentication email failed to send " + task.getException());
                            Toast.makeText(CreateAccountActivity.this, "Email failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @OnClick(R.id.navToLoginBtn)
    public void navToLogin() {
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
