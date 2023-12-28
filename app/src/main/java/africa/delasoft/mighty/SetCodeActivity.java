package africa.delasoft.mighty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetCodeActivity extends AppCompatActivity {

    // Define SharedPreferences file name
    private static final String SHARED_PREFS_FILE = "MySharedPrefs";

    // Define keys for SharedPreferences
    private static final String KEY_PIN = "pin";

    private EditText editTextPin;
    private Button savePinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_code);


        setTitle("    PIN Settings");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        editTextPin = findViewById(R.id.editTextPin);
        savePinButton = findViewById(R.id.savePin);

        savePinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextPin.getText().toString().isEmpty()){
                    Toast.makeText(SetCodeActivity.this, "Required Field\nPlease Enter PIN to save\nremember that if you save it will overwrite the existing PIN", Toast.LENGTH_LONG).show();
                return;
                }
                savePinToSharedPreferences();
            }
        });

    }

    private void savePinToSharedPreferences() {
        // Get the PIN from the EditText
        String pin = editTextPin.getText().toString();

        // Save the PIN to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PIN, pin);
        editor.apply();

        Toast.makeText(this, editTextPin.getText().toString()+" PIN is saved succesfully", Toast.LENGTH_LONG).show();
    }


}