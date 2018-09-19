package team7.seshealthpatient.Activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

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
    private boolean animated = false;
    private int currentPage = 1;
    private boolean isPatient = false;
    private String doctorKey = "1234";
    private boolean isValidInput = false;

    @BindView(R.id.nameET)
    EditText nameET;

    @BindView(R.id.phoneET)
    EditText phoneET;

    @BindView(R.id.setupDOBDateET)
    EditText setupDOBDateET;

    @BindView(R.id.heightET)
    EditText heightET;

    @BindView(R.id.weightET)
    EditText weightET;

    @BindView(R.id.allergiesET)
    EditText allergiesET;

    @BindView(R.id.medicationET)
    EditText medicationET;

    @BindView(R.id.genderRGPatient)
    RadioGroup genderRGPatient;

    @BindView(R.id.introSetupTV)
    TextView introSetupTV;

    @BindView(R.id.nameETDoctor)
    EditText nameETDoctor;

    @BindView(R.id.occupationETDoctor)
    EditText occupationETDoctor;

    @BindView(R.id.locationETDoctor)
    EditText locationETDoctor;

    @BindView(R.id.genderRGDoctor)
    RadioGroup genderRGDoctor;

    @BindView(R.id.setupFirstPatientSV)
    ScrollView setupFirstPatientSV;

    @BindView(R.id.setupSecondPatientSV)
    ScrollView setupSecondPatientSV;

    @BindView(R.id.setupFirstDoctorSV)
    ScrollView setupFirstDoctorSV;

    @BindView(R.id.selectSetupLL)
    LinearLayout selectSetupLL;

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
        addListeners();

        animateWelcome();
    }

    public void doctorSetupInit() {
        isPatient = false;
        selectSetupLL.setVisibility(View.GONE);
        setupFirstPatientSV.setVisibility(View.GONE);
        setupSecondPatientSV.setVisibility(View.GONE);
        currentPage = 2;
    }

    @OnClick(R.id.selectPatientTV)
    public void patientSetupInit() {
        isPatient = true;
        selectSetupLL.setVisibility(View.GONE);
        setupFirstDoctorSV.setVisibility(View.GONE);
        currentPage = 2;
    }

    @OnClick({R.id.setupInitDoctor, R.id.setupInitPatient})
    public void returnToInit() {
        selectSetupLL.setVisibility(View.VISIBLE);
        setupFirstDoctorSV.setVisibility(View.VISIBLE);
        setupFirstPatientSV.setVisibility(View.VISIBLE);
        setupSecondPatientSV.setVisibility(View.VISIBLE);
        currentPage = 1;
    }

    @OnClick(R.id.setupNextPatient)
    public void toNextPagePatient() {
        if (checkPassedFirstPatient()) {
            setupFirstPatientSV.setVisibility(View.GONE);
            currentPage = 3;
        }
    }

    @OnClick(R.id.setupBackPatient)
    public void toPreviousPagePatient() {
        setupFirstPatientSV.setVisibility(View.VISIBLE);
        currentPage = 2;
    }

    @OnClick(R.id.setupFinishPatient)
    public void setPatientInfo() {
        if (checkPassedSecondPatient()) {
            String name = nameET.getText().toString().trim();
            RadioButton radioButton = findViewById(genderRGPatient.getCheckedRadioButtonId());
            String gender = radioButton.getText().toString().trim();
            String date = setupDOBDateET.getText().toString().trim();
            Double weight = Double.parseDouble(weightET.getText().toString().trim());
            Double height = Double.parseDouble(heightET.getText().toString().trim());
            String phoneNO = phoneET.getText().toString().trim();
            String allergies = allergiesET.getText().toString().trim();
            String medication = medicationET.getText().toString().trim();

            reference.child("Profile").child("name").setValue(name);
            reference.child("Profile").child("phoneNO").setValue(phoneNO);
            reference.child("Profile").child("DOB").setValue(date);
            reference.child("Profile").child("gender").setValue(gender);
            reference.child("Profile").child("weight").setValue(weight);
            reference.child("Profile").child("height").setValue(height);

            reference.child("allergies").setValue(allergies);
            reference.child("medication").setValue(medication);

            reference.child("setupComplete").setValue(true);
            reference.child("accountType").setValue("patient");

            Intent intent = new Intent(SetupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public boolean checkPassedFirstPatient() {
        Log.d(TAG, "Check all fields being called");
        if (nameET.getText().toString().trim().isEmpty() || !StringUtils.isAlphaSpace(nameET.getText().toString())) {
            Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phoneET.getText().toString().replaceAll(" ", "").length() != 10 || !StringUtils.isNumericSpace(phoneET.getText().toString())) {
            Toast.makeText(this, "Please enter a mobile number following the 04XX XXX XXX format", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (setupDOBDateET.getText().toString().trim().isEmpty()) {
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
        if (genderRGPatient.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean checkPassedSecondPatient() {
        Log.d(TAG, "Check all fields being called");
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
        String date = setupDOBDateET.getText().toString().trim();
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

    @OnClick(R.id.setupFinishDoctor)
    public void setDoctorInfo() {
        if (checkPassedDoctor()) {
            String name = nameETDoctor.getText().toString().trim();
            RadioButton radioButton = findViewById(genderRGDoctor.getCheckedRadioButtonId());
            String gender = radioButton.getText().toString().trim();
            String occupation = occupationETDoctor.getText().toString().trim();
            String location = locationETDoctor.getText().toString().trim();

            reference.child("Profile").child("name").setValue(name);
            reference.child("Profile").child("gender").setValue(gender);
            reference.child("Profile").child("occupation").setValue(occupation);
            reference.child("Profile").child("location").setValue(location);

            reference.child("setupComplete").setValue(true);
            reference.child("accountType").setValue("doctor");

            Intent intent = new Intent(SetupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public boolean checkPassedDoctor() {
        Log.d(TAG, "Check all fields being called");
        if (nameETDoctor.getText().toString().trim().isEmpty() || !StringUtils.isAlphaSpace(nameETDoctor.getText().toString())) {
            Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (occupationETDoctor.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a valid occupation", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (genderRGDoctor.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (locationETDoctor.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your clinic's address", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void addListeners() {
        // Listener for the Date Picker
        setupDOBDateET.setOnClickListener(new View.OnClickListener() {
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
                setupDOBDateET.setText(currentDateString);
            }
        };

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

    @Override
    public void onBackPressed() {
        if (currentPage == 1)
            logOut();
        else if (currentPage == 2)
            returnToInit();
        else if (currentPage == 3 && isPatient)
            toPreviousPagePatient();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            animated = true;
        }
    }

    public void animateWelcome() {
        if (!animated && currentPage == 1) {
            introSetupTV.setVisibility(View.INVISIBLE);

            introSetupTV.postDelayed(new Runnable() {
                @Override
                public void run() {
                    introSetupTV.setVisibility(View.VISIBLE);
                }
            }, 750);

            introSetupTV.postDelayed(new Runnable() {
                @Override
                public void run() {
                    introSetupTV.setVisibility(View.GONE);
                }
            }, 2000);
            animated = true;
        }
    }

    public void logOut() {
        mAuth.signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    @OnClick(R.id.selectDoctorTV)
    public void requestDoctorKey() {
        AlertDialog.Builder getKeyBuilder = new AlertDialog.Builder(this);
        getKeyBuilder.setTitle("Doctor Key");
        getKeyBuilder.setMessage("Please enter the key you have been provided to register as a doctor");
        View dialogView = (SetupActivity.this.getLayoutInflater()).inflate(R.layout.dialog_doctor_key, null);
        final EditText doctorKeyET = dialogView.findViewById(R.id.doctorKeyET);
        getKeyBuilder.setView(dialogView);

        getKeyBuilder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (doctorKeyET.getText().toString().trim().equals(doctorKey))
                            doctorSetupInit();
                        else
                            Toast.makeText(getApplicationContext(), "The entered key is incorrect", Toast.LENGTH_SHORT).show();
                    }
                });

        getKeyBuilder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        getKeyBuilder.show();
    }
}
