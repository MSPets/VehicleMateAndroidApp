package songer.michael.vehiclemate.database.interfaces;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.room.Room;

import com.google.common.base.Throwables;

import java.util.List;
import java.util.concurrent.ExecutionException;

import songer.michael.vehiclemate.database.DBController;
import songer.michael.vehiclemate.database.entity.VehicleNotesEntity;

import static songer.michael.vehiclemate.MainActivity.DB_NAME;

public class VehicleNotesInterface
{
    private String strLog = "VNI";

    final private DBController userDB;
    public VehicleNotesInterface(Context context)
    {
        userDB = Room.databaseBuilder(context.getApplicationContext(), DBController.class, DB_NAME).build();
    }
    // Get all notes
    public List<VehicleNotesEntity> getAllNotes(long vehId)
    {
        try
        {
            return new VehicleNotesInterface.getAllAsyncTask(userDB, vehId).execute().get();
        }
        catch (Exception e)
        {
            Log.e(strLog, Throwables.getStackTraceAsString(e));
        }
        return null;
    }
    private static class getAllAsyncTask extends AsyncTask<Void, Void, List<VehicleNotesEntity>>
    {
        private DBController userDB;
        private long vehId;
        getAllAsyncTask(DBController DBN, long vehicleId)
        {
            userDB = DBN;
            vehId = vehicleId;
        }
        @Override
        protected List<VehicleNotesEntity> doInBackground(Void... voids)
        {
            return userDB.vehicleNotesDAO().getAll(vehId);
        }
    }
    // Get Note
    public VehicleNotesEntity getNote(long id)
    {
        try
        {
            return new VehicleNotesInterface.getSingleAsyncTask(userDB, id).execute().get();
        }
        catch (Exception e)
        {
            Log.e(strLog, Throwables.getStackTraceAsString(e));
        }
        return null;
    }
    // Async task for single note
    private static class getSingleAsyncTask extends AsyncTask<Void, Void, VehicleNotesEntity>
    {
        private DBController userDB;
        private long id;
        getSingleAsyncTask(DBController DBN, long nId)
        {
            userDB = DBN;
            id = nId;
        }
        @Override
        protected VehicleNotesEntity doInBackground(Void... voids)
        {
            return userDB.vehicleNotesDAO().getNote(id);
        }
    }
    // Insert note
    public long insert(VehicleNotesEntity veh)
    {
        try
        {
            return new VehicleNotesInterface.insertAsyncTask(userDB, veh).execute().get();
        }
        catch (Exception e)
        {
            Log.e(strLog, Throwables.getStackTraceAsString(e));
        }
        return -1;
    }
    private static class insertAsyncTask extends android.os.AsyncTask<Void, Void, Long>
    {
        private DBController userDB;
        private VehicleNotesEntity note;

        insertAsyncTask(DBController DBN, VehicleNotesEntity note)
        {
            userDB = DBN;
            this.note=note;
        }
        @Override
        protected Long doInBackground(Void... voids)
        {
            return userDB.vehicleNotesDAO().addNote(note);
        }
    }
    // Delete Note
    public void delete(final long id)
    {
        final VehicleNotesEntity veh = getNote(id);
        if (veh != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    userDB.vehicleNotesDAO().deleteNote(id);
                    return null;
                }
            }.execute();
        }
    }
    // Update Note
    public void update(final VehicleNotesEntity noteData) {
        final VehicleNotesEntity veh = getNote(noteData.getUid());
        if (veh != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    userDB.vehicleNotesDAO().updateNote(noteData);
                    return null;
                }
            }.execute();
        }
    }
    // Delete all
    public void deleteAll() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                userDB.vehicleNotesDAO().deleteAll();
                return null;
            }
        }.execute();
    }
}
