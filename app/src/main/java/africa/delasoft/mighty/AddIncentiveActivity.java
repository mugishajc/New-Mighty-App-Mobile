package africa.delasoft.mighty;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import africa.delasoft.mighty.data.model.PhoneNumber;

public class AddIncentiveActivity extends AppCompatActivity {
    private Button btnAddNewIncentives;
    private EditText multiLineEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_incentive);

        setTitle("Add new I#");


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnAddNewIncentives = findViewById(R.id.btnAddNewIncentives);
        multiLineEditText = findViewById(R.id.multiLineEditText);

        btnAddNewIncentives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String incentiveName = multiLineEditText.getText().toString();
                if (incentiveName.isEmpty()) {
                    Toast.makeText(AddIncentiveActivity.this, "Please enter the valid Phone numbers details.", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveCourse(incentiveName);


            }
        });

    }
    private void saveCourse(String phoneNumbers) {
        if (phoneNumbers == null || phoneNumbers.trim().isEmpty()) {
            // If the phoneNumbers is null or empty, show an error message
            Toast.makeText(this, "Please enter valid phone numbers details.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Trim the phone number to remove leading and trailing whitespaces
        String trimmedPhoneNumber = phoneNumbers.trim();

        // Create a PhoneNumber object with the given phone number
        PhoneNumber phoneNumberObject = new PhoneNumber(trimmedPhoneNumber);

        if (phoneNumberObject == null) {
            // If the PhoneNumber object is null, show an error message
            Toast.makeText(this, "Failed to create PhoneNumber object.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert the PhoneNumber object into the Room database
        PhoneNumberDatabase database = PhoneNumberDatabase.getInstance(getApplicationContext());
        if (database == null) {
            // If the Room database is null, show an error message
            Toast.makeText(this, "Failed to get Room database instance.", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneNumberDao dao = database.Dao();
        if (dao == null) {
            // If the PhoneNumberDao is null, show an error message
            Toast.makeText(this, "Failed to get PhoneNumberDao.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert the PhoneNumber object using AsyncTask to avoid running on the main thread
        new InsertPhoneNumberAsyncTask(dao).execute(phoneNumberObject);

        // Display a toast message after adding the data
        Toast.makeText(this, "PhoneNumbers has been saved to the Local Database. Successfully!!", Toast.LENGTH_LONG).show();

        // Reset the EditText field
        multiLineEditText.setText("");
    }


}