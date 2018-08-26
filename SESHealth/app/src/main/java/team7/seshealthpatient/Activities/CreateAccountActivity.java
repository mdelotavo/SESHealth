package team7.seshealthpatient.Activities;

import team7.seshealthpatient.R;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 1;


    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;
  
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

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(CreateAccountActivity.this, "Oops an error", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


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

    private boolean isValidDOB() {
        Calendar cal = Calendar.getInstance();
        String date = createAccDOB.getText().toString().trim();
        if(date.length() == 0) {
            return false;
        }
        int day = Integer.parseInt(date.substring(0,2));
        int month = Integer.parseInt(date.substring(3,5));
        int year = Integer.parseInt(date.substring(6,10));
        Log.d(TAG, "IsValidDOB being called: " + day + "/" + month + "/" + year);

        if(year <= cal.get(Calendar.YEAR)) {
            if(month <= cal.get(Calendar.MONTH) + 1) {
                if(day <= cal.get(Calendar.DAY_OF_MONTH)) {
                    return true;
                }
            }
        }
        Toast.makeText(CreateAccountActivity.this, R.string.dateOfBirthCheckValidDate_toast, Toast.LENGTH_SHORT).show();
        return false;
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
        Log.d(TAG, "Check all fields being called");
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
        if(!isValidDOB()) {
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

    // Adds user information to Firebase Database attached to their account
    private void addUserInformation() {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Profile");
        String firstName = createAccFirstName.getText().toString().trim();
        String lastName = createAccLastName.getText().toString().trim();
        String gender = createAccGender.getSelectedItem().toString();
        String date = createAccDOB.getText().toString().trim();

        Map userProfile = new HashMap();
        userProfile.put("firstName", firstName);
        userProfile.put("lastName", lastName);
        userProfile.put("dateOfBirth", date);
        userProfile.put("gender", gender);
        userProfile.put("phoneNumber", "");
        userProfile.put("setupComplete", false);

        currentUser.setValue(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.d(TAG, "User's profile was successfully created");
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

    // The initial method called when clicking 'sign in with google' and verifies the google account
    @OnClick(R.id.createAccGoogleBtn)
    public void signInWithGoogle() {
        progressDialog.setMessage(getString(R.string.login_progressDialog));
        progressDialog.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Makes a call to Google to verify the google account and then passes that to FirebaseAuthWithGoogle
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.d(TAG, "Google sign in failed", e);
                Toast.makeText(this, getString(R.string.google_authentication_message_failure), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
    }

    // Makes a call to firebase to sign in with the google account passed in
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            addUserInformationForGoogle();
                        } else {
                            Log.d(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.login_layout), getString(R.string.google_authentication_message_failure), Snackbar.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    // Checks if user authenticating with google has a 'profile' in the db, if not it will create one
    private void addUserInformationForGoogle() {
        String userId = mAuth.getCurrentUser().getUid();
        final DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Profile");
        String user = mAuth.getCurrentUser().getDisplayName().toString();
        int whiteSpace = user.indexOf(" ");
        final String firstName = user.substring(0, whiteSpace);
        final String lastName = user.substring(whiteSpace+1, user.length());

        currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // ...
                long count = dataSnapshot.getChildrenCount();
                Log.d(TAG, "The row count for this profile is: " + count);
                if(count == 0) {
                    Map userProfile = new HashMap();
                    userProfile.put("firstName", firstName);
                    userProfile.put("lastName", lastName);
                    userProfile.put("dateOfBirth", "");
                    userProfile.put("gender", "");
                    userProfile.put("phoneNumber", "");
                    userProfile.put("setupComplete", false);

                    currentUser.setValue(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Log.d(TAG, "User's profile was successfully created");
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Log.d(TAG, "Setting user information failed" + task.getException());
                                Toast.makeText(CreateAccountActivity.this, "Failed to add user information", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "An error occurred with the database: " + databaseError);
            }
        });
    }
}
