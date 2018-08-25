package team7.seshealthpatient.Activities;

import team7.seshealthpatient.R;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateAccountActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @BindView(R.id.createAccEmailET)
    EditText createAccEmail;

    @BindView(R.id.createAccPasswordET)
    EditText createAccPassword;

    @BindView(R.id.createAccFirstNameET)
    EditText createAccFirstName;

    @BindView(R.id.createAccLastNameET)
    EditText createAccLastName;

    @BindView(R.id.createAccGenderSpinner)
    Spinner createAccGender;

    @BindView(R.id.createAccDOBDateTV)
    TextView createAccDOB;

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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genders, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        createAccGender.setAdapter(adapter);

        // Listener for the Date Picker
        createAccDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int day, month, year;
                day = 1;
                month = 0;
                year = cal.get(Calendar.YEAR);

                DatePickerDialog dialog = new DatePickerDialog(
                        CreateAccountActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month++;
                String dayString = day + "";
                String monthString = month + "";

                // Do this to keep the format consistent
                if(dayString.length() == 1) {
                    dayString = "0" + dayString;
                }
                if(monthString.length() == 1) {
                    monthString = "0" + monthString;
                }

                String currentDateString = dayString + "/" + monthString + "/" + year;
                Log.d(TAG, "Logged date as: " + currentDateString);
                createAccDOB.setText(currentDateString);
            }
        };
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

    private boolean checkAllFields() {
        String email = createAccEmail.getText().toString().trim();
        String password = createAccPassword.getText().toString().trim();
        String firstName = createAccFirstName.getText().toString().trim();
        String lastName = createAccLastName.getText().toString().trim();
        String dob = createAccDOB.getText().toString().trim();
        if(!isValidEmail(email)) {
            Toast.makeText(this, getString(R.string.emailCheck_toast), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!isValidPassword(password)) {
            Toast.makeText(this, getString(R.string.passwordCheckLength_toast), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(firstName.length() == 0) {
            Toast.makeText(this, R.string.firstNameCheckLength_toast, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(lastName.length() == 0) {
            Toast.makeText(this, R.string.lastNameCheckLength_toast, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(dob.length() == 0) {
            Toast.makeText(this, R.string.dateOfBirthCheckLength_toast, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @OnClick(R.id.createAccBtn)
    public void createAccount() {
        String email = createAccEmail.getText().toString().trim();
        String password = createAccPassword.getText().toString().trim();
        hideKeyboard();
        if(checkAllFields()) {
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
    }

    private void addUserInformation() {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        String firstName = createAccFirstName.getText().toString().trim();
        String lastName = createAccLastName.getText().toString().trim();
        String gender = createAccGender.getSelectedItem().toString();
        Date dob = new Date();
        try {
            dob = new SimpleDateFormat("dd/mm/yyyy").parse(createAccDOB.getText().toString().trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Map newPost = new HashMap();
        newPost.put("firstName", firstName);
        newPost.put("lastName", lastName);
        newPost.put("dob", dob);
        newPost.put("gender", gender);

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
