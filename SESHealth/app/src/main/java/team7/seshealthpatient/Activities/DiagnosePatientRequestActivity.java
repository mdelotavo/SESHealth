package team7.seshealthpatient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import team7.seshealthpatient.R;

public class DiagnosePatientRequestActivity extends AppCompatActivity {

    EditText description, symptoms;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_diagnose_patient_request);
        description = findViewById(R.id.description);
        symptoms = findViewById(R.id.symptoms);

        description.setInputType(0);
        symptoms.setInputType(0);

        Intent receivedIntent = getIntent();
        description.setText(receivedIntent.getStringExtra("description"));
        symptoms.setText(receivedIntent.getStringExtra("symptoms"));
    }
}
