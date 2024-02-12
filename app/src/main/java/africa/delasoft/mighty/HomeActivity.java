package africa.delasoft.mighty;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import africa.delasoft.mighty.data.model.PhoneNumber;

public class HomeActivity extends AppCompatActivity {


    private static final long USSD_TIMEOUT_INTERVAL = 5 * 60 * 1000; // 5 minutes in milliseconds

    private Handler ussdTimeoutHandler = new Handler(Looper.getMainLooper());
    private Runnable ussdTimeoutRunnable;

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    private RecyclerView coursesRV;
    private ViewModal viewmodal;
    private FloatingActionButton floatingActionButton;

    private FirebaseAuth firebaseAuth;
    private String phoneNumbersFromDevice = "";
    private USSDApi ussdApi;
    private String savedPin = "";

    private static final long MIDNIGHT_HOUR = 23;
    private static final long MIDNIGHT_MINUTE = 59;
    private static final long MORNING_HOUR = 6;


    private static final String ACTION_RESUME_USSD = "africa.delasoft.mighty.ACTION_RESUME_USSD";

    private BroadcastReceiver resumeUSSDReceiver;

    // Define SharedPreferences file name
    private static final String SHARED_PREFS_FILE = "MySharedPrefs";

    // Define key for SharedPreferences
    private static final String KEY_PIN = "pin";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        setTitle("        Mighty Dashboard");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        firebaseAuth = FirebaseAuth.getInstance();


        ussdApi = USSDController.getInstance(getBaseContext());



        // Check and request WRITE_SETTINGS permission if needed
        AirplaneModeUtils.checkAndRequestWriteSettingsPermission(this);


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


        // Retrieve the PIN from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_FILE, MODE_PRIVATE);
        savedPin = sharedPreferences.getString(KEY_PIN, "");

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
                // Check if it's within the allowed time range for processing USSD
                if (shouldProcessUSSDNow()) {
                  //  Log.e("ClickedData", "Clicked on item with phone number: " + model.getPhoneNumber());

                    // Reset the last processed index to start processing from the first phone number
                    saveLastProcessedIndex(0);
                    callUssdInvoke();
                    Toast.makeText(HomeActivity.this, "Processing phone numbers...", Toast.LENGTH_SHORT).show();

                    // Schedule a timeout handler to check if 5 minutes have passed without processing
                    scheduleUSSDTimeout();

                } else {
                    // It's outside the allowed time range, show a message or handle it accordingly
                    Toast.makeText(HomeActivity.this, "USSD processing is paused. Try again during the allowed time range.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Register BroadcastReceiver to receive the alarm trigger for resuming USSD
        resumeUSSDReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Resume USSD processing
                callUssdInvoke();
                Toast.makeText(context, "Resuming USSD processing at " + getCurrentTime(), Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(resumeUSSDReceiver, new IntentFilter(ACTION_RESUME_USSD));

        // Schedule the alarm for resuming USSD at 6:00 AM
        scheduleResumeUSSDAlarm();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the BroadcastReceiver
        unregisterReceiver(resumeUSSDReceiver);
    }


    private void scheduleUSSDTimeout() {
        ussdTimeoutRunnable = new Runnable() {
            @Override
            public void run() {
                // Timeout occurred, call USSD processing
                saveLastProcessedIndex(0);
                callUssdInvoke();
                Toast.makeText(HomeActivity.this, "Resuming USSD processing due to timeout", Toast.LENGTH_SHORT).show();
            }
        };

        ussdTimeoutHandler.postDelayed(ussdTimeoutRunnable, USSD_TIMEOUT_INTERVAL);
    }


    private void cancelUSSDTimeout() {
        if (ussdTimeoutRunnable != null) {
            ussdTimeoutHandler.removeCallbacks(ussdTimeoutRunnable);
        }
    }

    private void scheduleResumeUSSDAlarm() {
        // Calculate the time until 6:00 AM from the current time
        long currentTimeMillis = System.currentTimeMillis();
        long midnightMillis = calculateMidnightMillis(currentTimeMillis);
        long morningMillis = midnightMillis + (MORNING_HOUR * DateUtils.HOUR_IN_MILLIS);

        // Set the alarm to trigger at 6:00 AM
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ACTION_RESUME_USSD);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, morningMillis, pendingIntent);

            // Enable airplane mode if it's outside the allowed time range
            if (currentTimeMillis >= morningMillis) {
                AirplaneModeUtils.enableAirplaneMode(this);
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, morningMillis, pendingIntent);

            // Enable airplane mode if it's outside the allowed time range
            if (currentTimeMillis >= morningMillis) {
                AirplaneModeUtils.enableAirplaneMode(this);
            }
        }

        Toast.makeText(this, "USSD processing paused. Will resume at 6:00 AM", Toast.LENGTH_LONG).show();
    }


    private long calculateMidnightMillis(long currentTimeMillis) {
        long midnightMillis = currentTimeMillis - (currentTimeMillis % DateUtils.DAY_IN_MILLIS)
                + (MIDNIGHT_HOUR * DateUtils.HOUR_IN_MILLIS)
                + (MIDNIGHT_MINUTE * DateUtils.MINUTE_IN_MILLIS);

        if (midnightMillis <= currentTimeMillis) {
            midnightMillis += DateUtils.DAY_IN_MILLIS;
        }

        return midnightMillis;
    }

    private String getCurrentTime() {
        return android.text.format.DateFormat.getTimeFormat(this).format(System.currentTimeMillis());
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
            case R.id.action_setPin:
                startActivity(new Intent(HomeActivity.this, SetCodeActivity.class));
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

        // Log.e("tango size", String.valueOf(phoneNumbersArray.length));

        //Log.e("tango list", phoneNumbersFromDevice);

        // Start the process with the first phone number
        int lastProcessedIndex = getLastProcessedIndex();
        processUssdForPhoneNumber(hashMap, lastProcessedIndex, phoneNumbersArray);

    }

    private void processUssdForPhoneNumber(HashMap<String, HashSet<String>> hashMap, int index, String[] phoneNumbersArray) {
        if (index < phoneNumbersArray.length) {
            String phoneNumber = phoneNumbersArray[index].trim();

            checkAndLogoutOnFirstDataOfMonth();


            ussdApi.callUSSDInvoke("*348*"+savedPin+"*1#", hashMap, new USSDController.CallbackInvoke() {
                @Override
                public void responseInvoke(String message) {
                    // Handle the USSD response
                      Log.e("tango first combined with pin", message);

                    // After the first response, send "1"
                    ussdApi.send("1", new USSDController.CallbackMessage() {
                        @Override
                        public void responseMessage(String message) {
                            // Handle the message from USSD after sending "1"
                             Log.e("tango one", message);

                            // After the "1" response, send the phone number
                            ussdApi.send(phoneNumber, new USSDController.CallbackMessage() {

                                @Override
                                public void responseMessage(String message) {
                                    // Handle the message from USSD after sending the phone number
                                      Log.e("tango phone number", message);

                                }
                            });
                        }
                    });
                }

                @Override
                public void over(String message) {
                    // Handle the final message from USSD or error

                    // Save the index after processing
                    saveLastProcessedIndex(index);


                    // Process the USSD response
                    handleUssdResponse(message);


                    // Process the next phone number recursively
                    processUssdForPhoneNumber(hashMap, index + 1, phoneNumbersArray);

                }
            });

            // Reset the USSD timeout when processing a new phone number
            cancelUSSDTimeout();
            scheduleUSSDTimeout();

        } else {
            // All phone numbers processed, cancel the USSD timeout
            cancelUSSDTimeout();

            // The array is exhausted, and there are no more phone numbers to process.
            // You can handle this situation here, e.g., display a message or perform any other necessary action.
            //    Log.e("tango", "All phone numbers processed");
            Toast.makeText(this, "All phone numbers processed", Toast.LENGTH_LONG).show();
        }


    }

    // Method to handle the USSD response
    private void handleUssdResponse(String ussdResponse) {
        // Check for specific strings in the USSD response
        if (containsInvalidStrings(ussdResponse)) {
            // If invalid strings are found, trigger USSD code *131#
            triggerUSSDCode("*131#");
        }
    }

    // Method to check if the USSD response contains specific invalid strings
    private boolean containsInvalidStrings(String ussdResponse) {
        String[] invalidStrings = {"Connection problem", "invalid MMI code", "UNKNOWN APPLICATION", "Mobile network not available","Not registered neywork","network","connection","invalid","problem","invalid"};

        // Remove spaces from the USSD response for accurate matching
        ussdResponse = ussdResponse.replaceAll("\\s", "");

        for (String invalidString : invalidStrings) {
            // Remove spaces from the invalid string for accurate matching
            String trimmedInvalidString = invalidString.replaceAll("\\s", "");

            if (ussdResponse.contains(trimmedInvalidString)) {
                return true;
            }
        }
        return false;
    }


    // Method to trigger USSD code
    private void triggerUSSDCode(String ussdCode) {
        // String ussdPhoneNumber = Uri.encode(ussdCode); // Encode the USSD code
        // Uri uriPhone = Uri.parse("tel:" + ussdPhoneNumber);
        //  startActivity(new Intent(Intent.ACTION_CALL, uriPhone)); // Use startActivity since it's inside an Activity

        ussdApi.callUSSDInvoke("*131#", new HashMap<>(), new USSDController.CallbackInvoke() {
            @Override
            public void responseInvoke(String message) {

            }

            @Override
            public void over(String message) {
            }
        });


    }


    // Save the index in SharedPreferences
    private void saveLastProcessedIndex(int index) {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("last_processed_index", index);
        editor.apply();
    }

    // Retrieve the last processed index from SharedPreferences
    private int getLastProcessedIndex() {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        return preferences.getInt("last_processed_index", 0);
    }


    private void saveLastLogoutTimestamp() {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("last_logout_timestamp", System.currentTimeMillis());
        editor.apply();
    }


    private boolean shouldProcessUSSDNow() {
        // Get the current hour
        int currentHour = Integer.parseInt(android.text.format.DateFormat.format("H", System.currentTimeMillis()).toString());

        // Check if the current hour is within the allowed time range (6:00 AM to 11:59 PM)
        boolean isWithinAllowedTimeRange = currentHour >= MORNING_HOUR && currentHour < MIDNIGHT_HOUR;

        // Toggle airplane mode accordingly
        if (isWithinAllowedTimeRange) {
            // Enable airplane mode at 23:59 PM
            if (currentHour == MIDNIGHT_HOUR - 1) {
                AirplaneModeUtils.enableAirplaneMode(this);
            }
        } else {
            // Disable airplane mode at 6:00 AM
            if (currentHour == MORNING_HOUR) {
                AirplaneModeUtils.disableAirplaneMode(this);
            }
        }

        return isWithinAllowedTimeRange;
    }



    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;

        } else {
            Toast.makeText(getBaseContext(), "press back again to exit", Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }

    private void checkAndLogoutOnFirstDataOfMonth() {
        // Get the current month and date
        int currentMonth = Integer.parseInt(android.text.format.DateFormat.format("M", System.currentTimeMillis()).toString());
        int currentDay = Integer.parseInt(android.text.format.DateFormat.format("d", System.currentTimeMillis()).toString());

        // Check if the current month is March, June, or September and it's the first day of the month
        if ((currentMonth == Calendar.MARCH || currentMonth == Calendar.JUNE || currentMonth == Calendar.SEPTEMBER) && currentDay == 1) {
            // Log out the user
            firebaseAuth.signOut();
            startActivity(new Intent(HomeActivity.this, SignInActivity.class));
            finish();
        }
    }

}