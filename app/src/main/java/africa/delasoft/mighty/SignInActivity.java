package africa.delasoft.mighty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import africa.delasoft.mighty.ui.login.LoginActivity;

public class SignInActivity extends AppCompatActivity {

    private EditText editTextCouponCode;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Remove the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        editTextCouponCode = findViewById(R.id.editTextCouponCode);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextCouponCode.getText().toString().isEmpty()) {
                    return;
                }
                checkValidLoginCode();
            }
        });
    }

    private void checkValidLoginCode(){
        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
        startActivity(intent);
        this.finish();
    }

}