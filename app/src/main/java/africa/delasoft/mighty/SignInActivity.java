package africa.delasoft.mighty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private EditText editTextEmail,editTextPassword;
    private Button btnLogin;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        FirebaseApp.initializeApp(this);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Busy Authenticating ...");
        progressDialog.setCanceledOnTouchOutside(false);

        if (currentUser != null) {
            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
            finish();
        }

        // Remove the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                                    finish();
                                } else {
                                    // If login fails, display a message to the user.
                                    Toast.makeText(SignInActivity.this, "Invalid Credentials \nAuthentication failed.",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });


            }
        });
    }



}