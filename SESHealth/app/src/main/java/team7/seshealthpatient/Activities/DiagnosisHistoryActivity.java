package team7.seshealthpatient.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiagnosisHistoryActivity extends AppCompatActivity {


    private final static String TAG = "DiagnosisHistoryFragment";
    private ProgressDialog progressDialog;
    private FirebaseUser mUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private ListView diagnosisListView;
    private String uuid;


    @BindView(R.id.noDiagnosticsMadeTV)
    TextView noDiagnosticsMadeTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis_history);
        // You need this line on your activity so Butter Knife knows what Activity-View we are referencing
        ButterKnife.bind(this);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        database = FirebaseDatabase.getInstance();

        // Please try to use more String resources (values -> strings.xml) vs hardcoded Strings.
        setTitle("Diagnosis History");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        final List<String[]> diagnosisList = new ArrayList<>();

        final ListAdapter diagnosisListAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                diagnosisList
        );

        diagnosisListView.setAdapter(diagnosisListAdapter);

        Bundle extras = getIntent().getExtras();

        // TODO: Fix this logic
        if (extras != null)
            uuid = extras.getString("uid");
        else
            uuid = mUser.getUid();

        reference.child(uuid).child("Diagnosis")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        diagnosisList.clear();
                        noDiagnosticsMadeTV.setVisibility(View.VISIBLE);

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String key = child.getKey();
                            String timeStamp = (child.child("Timestamp").getValue() != null)
                                    ? child.child("Timestamp").getValue().toString() : null;
                            if (timeStamp != null) {
                                String[] diagnosisArr = new String[] {key, timeStamp};
                                diagnosisList.add(diagnosisArr);
                                diagnosisListView.invalidateViews();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    // Using this so the activity isn't recreated on orientation change
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
    }
}

