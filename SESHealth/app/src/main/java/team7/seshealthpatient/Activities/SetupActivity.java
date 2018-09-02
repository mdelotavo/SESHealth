package team7.seshealthpatient.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team7.seshealthpatient.R;

public class SetupActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private String name;
    private RadioButton radioButton;
    private String date;
    private String gender;
    private String phoneNO;
    private String allergies;
    private String medication;

    @BindView(R.id.nameET)
    EditText nameET;

    @BindView(R.id.phoneET)
    EditText phoneET;

    @BindView(R.id.setupDOBDateTV)
    TextView setupDOBDateTV;

    @BindView(R.id.heightET)
    EditText heightET;

    @BindView(R.id.weightET)
    EditText weightET;

    @BindView(R.id.allergiesET)
    EditText allergiesET;

    @BindView(R.id.medicationET)
    EditText medicationET;

    @BindView(R.id.genderRG)
    RadioGroup genderRG;

    private static String TAG = "SetupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users").child(user.getUid());

        ButterKnife.bind(this);
        textChangedListeners();

        // Listener for the Date Picker
        setupDOBDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int day, month, year;
                day = 1;
                month = 0;
                year = cal.get(Calendar.YEAR);

                DatePickerDialog dialog = new DatePickerDialog(
                        SetupActivity.this,
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
                if (dayString.length() == 1) {
                    dayString = "0" + dayString;
                }
                if (monthString.length() == 1) {
                    monthString = "0" + monthString;
                }

                String currentDateString = dayString + "/" + monthString + "/" + year;
                Log.d(TAG, "Logged date as: " + currentDateString);
                setupDOBDateTV.setText(currentDateString);
            }
        };
    }

    @OnClick(R.id.setupMain)
    public void setUserInfo() {
        if (checkPassed()) {
            name = nameET.getText().toString().trim();
            radioButton = findViewById(genderRG.getCheckedRadioButtonId());
            gender = radioButton.getText().toString().trim();
            date = setupDOBDateTV.getText().toString().trim();
            phoneNO = phoneET.getText().toString().trim();
            allergies = allergiesET.getText().toString().trim();
            medication = medicationET.getText().toString().trim();

            reference.child("Profile").child("name").setValue(name);
            reference.child("Profile").child("phoneNO").setValue(phoneNO);
            reference.child("Profile").child("DOB").setValue(date);
            reference.child("Profile").child("gender").setValue(gender);

            reference.child("allergies").setValue(allergies);
            reference.child("medication").setValue(medication);

            reference.child("setupComplete").setValue(true);

            Intent intent = new Intent(SetupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    public boolean checkPassed() {
        Log.d(TAG, "Check all fields being called");
        if (nameET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phoneET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (setupDOBDateTV.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, R.string.dateOfBirthCheckLength_toast, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidDOB()) {
            Toast.makeText(SetupActivity.this, R.string.dateOfBirthCheckValidDate_toast, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (heightET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your height", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (weightET.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your weight", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (genderRG.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please enter your gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (allergiesET.getText().toString().trim().isEmpty()) {
            allergiesET.setText("N/A");
        }
        if (medicationET.getText().toString().trim().isEmpty()) {
            medicationET.setText("N/A");
        }
        return true;
    }

    private boolean isValidDOB() {
        Calendar cal = Calendar.getInstance();
        String date = setupDOBDateTV.getText().toString().trim();
        if (date.length() == 0) {
            return false;
        }
        int day = Integer.parseInt(date.substring(0, 2));
        int month = Integer.parseInt(date.substring(3, 5));
        int year = Integer.parseInt(date.substring(6, 10));
        Log.d(TAG, "IsValidDOB being called: " + day + "/" + month + "/" + year);

        if (year <= cal.get(Calendar.YEAR)) {
            if (month <= cal.get(Calendar.MONTH) + 1) {
                return (day <= cal.get(Calendar.DAY_OF_MONTH));
            }
        }
        return false;
    }

    public void textChangedListeners() {
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

    @OnClick(R.id.setupLogOutBtn)
    public void logOut() {
        mAuth.signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}
