package team7.seshealthpatient.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;
import team7.seshealthpatient.R;


public class CreateAccountActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        // You need this line on your activity so Butter Knife knows what Activity-View we are referencing
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            finish();
            // determine whether it should be getAppContext or 'this'
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        // A reference to the toolbar, that way we can modify it as we please
        toolbar = findViewById(R.id.createAccount_toolbar);

        // Please try to use more String resources (values -> strings.xml) vs hardcoded Strings.
        toolbar.setTitle(getString(R.string.createAccount_activity_title));
        progressDialog = new ProgressDialog(this);
    }
}
