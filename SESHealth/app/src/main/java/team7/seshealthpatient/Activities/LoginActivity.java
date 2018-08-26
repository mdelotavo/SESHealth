package team7.seshealthpatient.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.seshealthpatient.R;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class: LoginActivity
 * Extends: {@link AppCompatActivity}
 * Author: Carlos Tirado < Carlos.TiradoCorts@uts.edu.au> and YOU!
 * Description:
 * <p>
 * Welcome to the first class in the project. I will be leaving some comments like this through all
 * the classes I write in order to help you get a hold on the project. Here I took the liberty of
 * creating an empty Log In activity for you to fill in the details of how your log in is
 * gonna work. Please, Modify Accordingly!
 * <p>
 */
public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final int RC_SIGN_IN = 1;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;

    /**
     * Use the @BindView annotation so Butter Knife can search for that view, and cast it for you
     * (in this case it will get casted to Edit Text)
     */
    @BindView(R.id.logoMain)
    ImageView logoMain;

    @BindView(R.id.loginEmailET)
    EditText loginEmailET;

    /**
     * If you want to know more about Butter Knife, please, see the link I left at the build.gradle
     * file.
     */
    @BindView(R.id.loginPasswordET)
    EditText loginPasswordET;

    @BindView(R.id.forgotPwTV)
    TextView forgotPwText;

    @BindView(R.id.googleBtn)
    SignInButton mGoogleBtn;

    /**
     * It is helpful to create a tag for every activity/fragment. It will be easier to understand
     * log messages by having different tags on different places.
     */
    private static String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // You need this line on your activity so Butter Knife knows what Activity-View we are referencing
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null && isUserVerified()) {
                    progressDialog.setMessage(getString(R.string.login_progressDialog));
                    progressDialog.show();
                    mAuth.removeAuthStateListener(mAuthStateListener);
                    setupCompletedCheck();
                }
            }
        };

        // A reference to the toolbar, that way we can modify it as we please
        Toolbar toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);

        // Please try to use more String resources (values -> strings.xml) vs hardcoded Strings.
        setTitle(R.string.login_activity_title);
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
                        Toast.makeText(LoginActivity.this, "Oops an error", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //Placeholder image (update with logo when we have one)
        String logoName = "health_icon_1.png";
        try {
            InputStream stream = getAssets().open(logoName);
            Drawable d = Drawable.createFromStream(stream, null);
            logoMain.setImageDrawable(d);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    // Navigation method to 'CreateAccountActivity'
    @OnClick(R.id.navToRegisterBtn)
    public void navToRegisterPage() {
        startActivity(new Intent(this, CreateAccountActivity.class));
    }

    // Navigation method to 'ForgotPasswordFragment'
    @OnClick(R.id.forgotPwTV)
    public void navToForgotPw() {
        //To navigate to forgot Password Activity
        Toast.makeText(this, "Will navigate to forgot password!", Toast.LENGTH_SHORT).show();
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean isUserVerified() {
        if (mAuth.getCurrentUser().isEmailVerified()) {
            Log.d(TAG, mAuth.getCurrentUser().getEmail() + " has been verified");
            mAuth.getCurrentUser().reload();
            return true;
        } else {
            Log.d(TAG, mAuth.getCurrentUser().getEmail() + " has not been verified");
            Snackbar.make(findViewById(R.id.login_layout),
                    mAuth.getCurrentUser().getEmail() + " has not been verified yet, click the link in your email and then reload the app",
                    Snackbar.LENGTH_LONG).show();
            return false;
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * See how Butter Knife also lets us add an on click event by adding this annotation before the
     * declaration of the function, making our life way easier.
     */
    // Authenticates the email and password with Firebase
    @OnClick(R.id.loginBtn)
    public void logIn() {
        String email = loginEmailET.getText().toString();
        String password = loginPasswordET.getText().toString();
        hideKeyboard();

        if (!isValidEmail(email)) {
            Toast.makeText(this, getString(R.string.emailCheck_toast), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.passwordCheck_toast), Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage(getString(R.string.login_progressDialog));
        progressDialog.show();

        //Will need to work on logging in with username as well + credential validation
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail: sucess");
                            if (isUserVerified()) {
                                mAuth.removeAuthStateListener(mAuthStateListener);
                                setupCompletedCheck();
                            }
                        } else {
                            Log.d(TAG, "signInWithEmail: failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Having a tag, and the name of the function on the console message helps allot in
        // knowing where the message should appear.
        Log.d(TAG, "username: " + email + " password: " + password);
    }

    // The initial method called when clicking 'sign in with google' and verifies the google account
    @OnClick(R.id.googleBtn)
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
                        } else {
                            Log.d(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.login_layout), getString(R.string.google_authentication_message_failure), Snackbar.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void setupCompletedCheck() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        reference = database.getReference("Users").child(user.getUid()).child("setupComplete");
        reference.keepSynced(true);

        String[] children = {"fullName", "phoneNo", "birthDate",
                "allergies", "medication", "gender"};

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if ((boolean) dataSnapshot.getValue())
                        startMain();
                    else
                        startSetup();
                } catch (Exception e) {
                    startSetup();
                }
                progressDialog.dismiss();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void startMain() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    public void startSetup() {
        startActivity(new Intent(LoginActivity.this, SetupActivity.class));
    }
}

