package team7.seshealthpatient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.seshealthpatient.R;

public class SetupActivity extends AppCompatActivity{

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;

    @BindView(R.id.nameET)
    EditText nameET;

    @BindView(R.id.phoneET)
    EditText phoneET;

    @BindView(R.id.bDET)
    EditText bDET;

    @BindView(R.id.allergiesET)
    EditText allergiesET;

    @BindView(R.id.medicationET)
    EditText medicationET;

    @BindView(R.id.genderRG)
    RadioGroup genderRG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        user = FirebaseAuth.getInstance().getCurrentUser();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users").child(user.getUid());

        ButterKnife.bind(this);
        textChangedListeners();
    }

    @OnClick(R.id.setupMain)
    public void setUserInfo() {
        if (checkPassed()) {
            String[] children = {"fullName", "phoneNo", "birthDate",
                    "allergies", "medication", "gender"};

            EditText[] childrenET = {nameET,phoneET,bDET,allergiesET,medicationET};

            for (int i = 0; i < childrenET.length; i++)
                reference.child(children[i]).setValue(childrenET[i].getText().toString());

            RadioButton radioButton = findViewById(genderRG.getCheckedRadioButtonId());
            reference.child("gender").setValue(radioButton.getText().toString());

            reference.child("setupComplete").setValue(true);

            Intent intent = new Intent(SetupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }



    public boolean checkPassed() {
        EditText[] editTexts = {nameET,phoneET,bDET,allergiesET,medicationET};
        for (int i = 0; i < editTexts.length; i++)
            if (i < 3 && editTexts[i].getText().toString().length() == 0) {
                Toast.makeText(this, "Please complete the required fields", Toast.LENGTH_SHORT).show();
                return false;
            }
            else if (i >= 3 && editTexts[i].getText().toString().length() == 0)
                editTexts[i].setText("N/A");
            else if (genderRG.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please enter your gender", Toast.LENGTH_SHORT).show();
                return false;
            }

        return true;
    }

    public void textChangedListeners() {
        bDET.addTextChangedListener(new TextWatcher() {
            int previousLength = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousLength = bDET.getText().length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = bDET.getText().toString();
                int currentLength = input.length();

                if ((previousLength < currentLength) && (currentLength == 2 || currentLength == 5))
                    bDET.append("/");
            }
        });

        phoneET.addTextChangedListener(new TextWatcher() {
            int previousLength = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousLength = phoneET.getText().length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = phoneET.getText().toString();
                int currentLength = input.length();

                if ((previousLength < currentLength) && (currentLength == 4 || currentLength == 8))
                    phoneET.append(" ");
            }
        });
    }
}
