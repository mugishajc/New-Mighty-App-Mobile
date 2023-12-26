package africa.delasoft.mighty.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "phone_numbers_table")
public class PhoneNumber {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String phoneNumber;

    public PhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}


