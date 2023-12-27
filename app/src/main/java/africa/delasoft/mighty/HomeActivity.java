package africa.delasoft.mighty;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.List;

import africa.delasoft.mighty.data.model.PhoneNumber;
import africa.delasoft.mighty.ui.login.LoginActivity;

public class HomeActivity extends AppCompatActivity {

    // creating a variables for our recycler view.
    private RecyclerView coursesRV;
    private ViewModal viewmodal;
    private FloatingActionButton floatingActionButton;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
         setTitle("        Mighty Dashboard");



        firebaseAuth = FirebaseAuth.getInstance();

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
}
