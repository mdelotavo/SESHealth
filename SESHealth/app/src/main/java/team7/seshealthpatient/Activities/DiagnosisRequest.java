package team7.seshealthpatient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import team7.seshealthpatient.R;

public class DiagnosisRequest extends AppCompatActivity {

    EditText description, symptoms;
    Button btnSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_diagnose_request);
        description = findViewById(R.id.description);
        symptoms = findViewById(R.id.symptoms);
        btnSubmit = findViewById(R.id.button_submit);

        Intent receivedIntent = getIntent();
        final String uid = receivedIntent.getStringExtra("uid");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (description.getText().toString().trim().isEmpty()
                        || symptoms.getText().toString().trim().isEmpty()) {
                    toastMessage("Empty fields...");
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Diagnose")
                            .push().setValue(new Diagnose(description.getText().toString(), symptoms.getText().toString()));
                    toastMessage("Sent");
                    finish();
                }
            }
        });
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
