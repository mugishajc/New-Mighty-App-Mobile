package africa.delasoft.mighty;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import africa.delasoft.mighty.data.model.PhoneNumber;

public class PhoneNumberRepository {

    private PhoneNumberDao dao;
    private LiveData<List<PhoneNumber>> allCourses;

    public PhoneNumberRepository(Application application) {
        PhoneNumberDatabase database = PhoneNumberDatabase.getInstance(application);
        dao = database.Dao();
        allCourses = dao.getAllCourses();
    }

    public void insert(PhoneNumber model) {
        new InsertCourseAsyncTask(dao).execute(model);
    }

    public void delete(PhoneNumber model) {
        new DeleteCourseAsyncTask(dao).execute(model);
    }

    public void deleteAllCourses() {
        new DeleteAllCoursesAsyncTask(dao).execute();
    }

    public LiveData<List<PhoneNumber>> getAllCourses() {
        return allCourses;
    }

    private static class InsertCourseAsyncTask extends AsyncTask<PhoneNumber, Void, Void> {
        private PhoneNumberDao dao;

        private InsertCourseAsyncTask(PhoneNumberDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(PhoneNumber... model) {
            dao.insert(model[0]);
            return null;
        }
    }

    private static class DeleteCourseAsyncTask extends AsyncTask<PhoneNumber, Void, Void> {
        private PhoneNumberDao dao;

        private DeleteCourseAsyncTask(PhoneNumberDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(PhoneNumber... models) {
            dao.delete(models[0]);
            return null;
        }
    }

    private static class DeleteAllCoursesAsyncTask extends AsyncTask<Void, Void, Void> {
        private PhoneNumberDao dao;
        private DeleteAllCoursesAsyncTask(PhoneNumberDao dao) {
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            dao.deleteAllCourses();
            return null;
        }
    }
}

