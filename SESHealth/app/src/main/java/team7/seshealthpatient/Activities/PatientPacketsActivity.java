package team7.seshealthpatient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class PatientPacketsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String uid;

        Intent receivedIntent = getIntent();
        uid = receivedIntent.getStringExtra("uid");
    }
}
