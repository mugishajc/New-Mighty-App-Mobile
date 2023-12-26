package africa.delasoft.mighty;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import africa.delasoft.mighty.data.model.PhoneNumber;

@androidx.room.Dao
public interface PhoneNumberDao {

    @Insert
    void insert(PhoneNumber model);

    @Delete
    void delete(PhoneNumber model);

    @Query("DELETE FROM phone_numbers_table")
    void deleteAllCourses();


    @Query("SELECT * FROM phone_numbers_table")
    LiveData<List<PhoneNumber>> getAllCourses();
}
