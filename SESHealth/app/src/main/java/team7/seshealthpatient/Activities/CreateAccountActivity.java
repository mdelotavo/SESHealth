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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.seshealthpatient.R;


public class CreateAccountActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
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

        toolbar = findViewById(R.id.createAccount_toolbar);
        toolbar.setTitle(getString(R.string.createAccount_activity_title));
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null) {
            finish();
            // determine whether it should be getAppContext or 'this'
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public boolean isValidPassword(CharSequence target) {
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
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
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
