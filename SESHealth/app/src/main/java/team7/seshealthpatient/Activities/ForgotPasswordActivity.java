package team7.seshealthpatient.Activities;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.seshealthpatient.R;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;

    @BindView(R.id.forgotPasswordET)
    EditText forgotPasswordET;

    private static String TAG = "ForgotPasswordActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.forgotPassword_toolbar);
        toolbar.setTitle(R.string.forgotPassword_activity_title);

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

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if(view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @OnClick(R.id.forgotPasswordBtn)
    public void sendPasswordResetEmail() {
        String email = forgotPasswordET.getText().toString().trim();
        hideKeyboard();
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email successfully sent");
                            Toast.makeText(ForgotPasswordActivity.this, getString(R.string.email_success), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, getString(R.string.email_success), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


}
