package africa.delasoft.mighty;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

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
                String phoneNumbers = multiLineEditText.getText().toString().trim();
                if (phoneNumbers.isEmpty()) {
                    Toast.makeText(AddIncentiveActivity.this, "Please enter valid phone numbers.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Split the input into an array of phone numbers
                String[] phoneNumberArray = phoneNumbers.split(",");

                // List to store valid phone numbers
                List<String> validPhoneNumbers = new ArrayList<>();

                // Iterate through each phone number and validate
                for (String phoneNumber : phoneNumberArray) {
                    String trimmedPhoneNumber = phoneNumber.trim();

                    if (isValidPhoneNumber(trimmedPhoneNumber)) {
                        validPhoneNumbers.add(trimmedPhoneNumber);
                    } else {
                        // Show a toast message for each invalid phone number
                        Toast.makeText(AddIncentiveActivity.this, "Invalid phone number: " + trimmedPhoneNumber, Toast.LENGTH_LONG).show();
                    }
                }

                // Proceed to save valid phone numbers
                if (!validPhoneNumbers.isEmpty()) {
                    saveCourse(TextUtils.join(",", validPhoneNumbers));
                }
            }
        });

    }

    // Function to validate a phone number
    private boolean isValidPhoneNumber(String phoneNumber) {
        // Add your validation logic here
        return phoneNumber.matches("\\d{10}"); // Matches exactly 10 digits
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