package africa.delasoft.mighty;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import africa.delasoft.mighty.data.model.PhoneNumber;

public class ViewModal extends AndroidViewModel {

    private PhoneNumberRepository repository;

    // below line is to create a variable for live
    // data where all the courses are present.
    private LiveData<List<PhoneNumber>> allCourses;

    // constructor for our view modal.
    public ViewModal(@NonNull Application application) {
        super(application);
        repository = new PhoneNumberRepository(application);
        allCourses = repository.getAllCourses();
    }

    // below method is use to insert the data to our repository.
    public void insert(PhoneNumber model) {
        repository.insert(model);
    }

    // below line is to delete the data in our repository.
    public void delete(PhoneNumber model) {
        repository.delete(model);
    }

    // below method is to delete all the courses in our list.
    public void deleteAllCourses() {
        repository.deleteAllCourses();
    }

    // below method is to get all the courses in our list.
    public LiveData<List<PhoneNumber>> getAllCourses() {
        return allCourses;
    }
}

