package africa.delasoft.mighty;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.romellfudi.ussdlibrary.USSDApi;
import com.romellfudi.ussdlibrary.USSDController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import africa.delasoft.mighty.data.model.PhoneNumber;
import africa.delasoft.mighty.ui.login.LoginActivity;

public class HomeActivity extends AppCompatActivity {

    // creating a variables for our recycler view.
    private RecyclerView coursesRV;
    private ViewModal viewmodal;
    private FloatingActionButton floatingActionButton;

    private FirebaseAuth firebaseAuth;
    private String phoneNumbersFromDevice ="";
    private USSDApi ussdApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
         setTitle("        Mighty Dashboard");

         setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        firebaseAuth = FirebaseAuth.getInstance();


        ussdApi = USSDController.getInstance(getBaseContext());

       // callUssdInvoke();

        // Inside your Application class or an appropriate initialization place
        PhoneNumberDatabase database = PhoneNumberDatabase.getInstance(getApplicationContext());

        // initializing our variable for our recycler view and fab.
        coursesRV = findViewById(R.id.idRVCourses);
        floatingActionButton = findViewById(R.id.idFABAdd);

        // adding on click listener for floating action button.
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddIncentiveActivity.class);
                startActivity(intent);
            }
        });

        // setting layout manager to our adapter class.
        coursesRV.setLayoutManager(new LinearLayoutManager(this));
        coursesRV.setHasFixedSize(true);

        // initializing adapter for recycler view.
        final CourseRVAdapter adapter = new CourseRVAdapter();

        // setting adapter class for recycler view.
        coursesRV.setAdapter(adapter);

        // passing a data from view modal.
        viewmodal = new ViewModelProvider(this).get(ViewModal.class);

        // below line is use to get all the courses from view modal.
        viewmodal.getAllCourses().observe(this, new Observer<List<PhoneNumber>>() {
            @Override
            public void onChanged(List<PhoneNumber> phoneNumbers) {
                // when the data is changed in our models we are
                // adding that list to our adapter class.
                adapter.submitList(phoneNumbers);

                // Log all phone numbers in a single line with commas
                // Log all phone numbers in a single line with commas
                StringBuilder phoneNumbersStringBuilder = new StringBuilder();
                for (PhoneNumber phoneNumber : phoneNumbers) {
                    phoneNumbersStringBuilder.append(phoneNumber.getPhoneNumber()).append(",");
                }

                // Remove the trailing comma
                String allPhoneNumbers = phoneNumbersStringBuilder.toString();
                if (allPhoneNumbers.endsWith(",")) {
                    allPhoneNumbers = allPhoneNumbers.substring(0, allPhoneNumbers.length() - 1);
                }

                phoneNumbersFromDevice = allPhoneNumbers.toString();

            }
        });
        // below method is use to add swipe to delete method for item of recycler view.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // on recycler view item swiped then we are deleting the item of our recycler view.
                viewmodal.delete(adapter.getCourseAt(viewHolder.getAdapterPosition()));
                Toast.makeText(HomeActivity.this, "Incentive is deleted,Successfully!!!", Toast.LENGTH_SHORT).show();
            }
        })
                .
                // below line is use to attach this to recycler view.
                        attachToRecyclerView(coursesRV);
        // below line is use to set item click listener for our item of recycler view.
        adapter.setOnItemClickListener(new CourseRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PhoneNumber model) {
                callUssdInvoke();
                Toast.makeText(HomeActivity.this, "clicked: "+model.getPhoneNumber()+"\n"+model.getId(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(HomeActivity.this, AddIncentiveActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_help:
                Toast.makeText(this, "Please send support email at \nrwandadevelopmentteam@gmail.com", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_logout:
                showLogoutConfirmationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention !!!");
        builder.setMessage("Logout Confirmation \n Are you sure you want to log out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                firebaseAuth.signOut();
                startActivity(new Intent(HomeActivity.this, SignInActivity.class));
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void callUssdInvoke() {
        HashMap<String, HashSet<String>> hashMap = new HashMap<>();
        hashMap.put("KEY_LOGIN", new HashSet<>(Arrays.asList("espere", "waiting", "loading", "esperando")));
        hashMap.put("KEY_ERROR", new HashSet<>(Arrays.asList("problema", "problem", "error", "null")));



        // Split the phone numbers by comma
        String[] phoneNumbersArray = phoneNumbersFromDevice.split(",");

        Log.e("tango size", String.valueOf(phoneNumbersArray.length));

        Log.e("tango list",phoneNumbersFromDevice);

        // Start the process with the first phone number
        processUssdForPhoneNumber(hashMap, 0, phoneNumbersArray);
    }

    private void processUssdForPhoneNumber(HashMap<String, HashSet<String>> hashMap, int index, String[] phoneNumbersArray) {
        if (index < phoneNumbersArray.length) {
            String phoneNumber = phoneNumbersArray[index].trim();


            ussdApi.callUSSDInvoke("*348*6613#", hashMap, new USSDController.CallbackInvoke() {
                @Override
                public void responseInvoke(String message) {
                    // Handle the USSD response
                    Log.e("tango", message);

                    // After the first response, send "1"
                    ussdApi.send("1", new USSDController.CallbackMessage() {
                        @Override
                        public void responseMessage(String message) {
                            // Handle the message from USSD after sending "1"
                            Log.e("tango", phoneNumber);

                            // After the "1" response, send the phone number
                            ussdApi.send(phoneNumber, new USSDController.CallbackMessage() {
                                @Override
                                public void responseMessage(String message) {
                                    // Handle the message from USSD after sending the phone number
                                    Log.e("tango", message);

                                    // Process the next phone number recursively
                                    processUssdForPhoneNumber(hashMap, index + 1, phoneNumbersArray);
                                }
                            });
                        }
                    });
                }

                @Override
                public void over(String message) {
                    // Handle the final message from USSD or error
                    Log.e("tango fn", message);
                    processUssdForPhoneNumber(hashMap, index + 1, phoneNumbersArray);
                }
            });
        }
    }







}
