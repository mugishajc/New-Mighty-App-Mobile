package africa.delasoft.mighty;

import android.os.AsyncTask;

import africa.delasoft.mighty.data.model.PhoneNumber;

public class InsertPhoneNumberAsyncTask extends AsyncTask<PhoneNumber, Void, Void> {
    private PhoneNumberDao dao;

    public InsertPhoneNumberAsyncTask(PhoneNumberDao dao) {
        this.dao = dao;
    }

    @Override
    protected Void doInBackground(PhoneNumber... phoneNumbers) {
        // Insert the PhoneNumber into the Room database
        dao.insert(phoneNumbers[0]);
        return null;
    }
}

