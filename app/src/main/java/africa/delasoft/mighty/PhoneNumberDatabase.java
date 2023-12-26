package africa.delasoft.mighty;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import africa.delasoft.mighty.data.model.PhoneNumber;

@Database(entities = {PhoneNumber.class}, version = 3)
public abstract class PhoneNumberDatabase extends RoomDatabase {

    private static PhoneNumberDatabase instance;

    public abstract PhoneNumberDao Dao();

    public static synchronized PhoneNumberDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            PhoneNumberDatabase.class, "phone_numbers_table")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }

        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        PopulateDbAsyncTask(PhoneNumberDatabase instance) {
            PhoneNumberDao dao = instance.Dao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
