package africa.delasoft.mighty;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddIncentiveActivity extends AppCompatActivity {
    private Button btnAddNewIncentives;
    private EditText multiLineEditText;

    public static final String EXTRA_COURSE_NAME = "africa.delasoft.mighty.EXTRA_COURSE_NAME";
    public static final String EXTRA_ID = "africa.delasoft.mighty.EXTRA_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_incentive);

        setTitle("Add new I#");

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
        // inside this method we are passing
        // all the data via an intent.
        Intent data = new Intent();

        // in below line we are passing all our course detail.
        data.putExtra(EXTRA_COURSE_NAME, phoneNumbers);
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            // in below line we are passing our id.
            data.putExtra(EXTRA_ID, id);
        }

        // at last we are setting result as data.
        setResult(RESULT_OK, data);

        // displaying a toast message after adding the data
        Toast.makeText(this, "PhoneNumbers has been saved to Room Database. Successfully!!", Toast.LENGTH_LONG).show();
        multiLineEditText.setText("");
    }

}