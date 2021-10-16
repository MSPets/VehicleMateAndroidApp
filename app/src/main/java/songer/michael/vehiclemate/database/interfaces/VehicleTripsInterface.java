package songer.michael.vehiclemate.database.interfaces;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.room.Room;

import com.google.common.base.Throwables;

import java.util.List;
import java.util.concurrent.ExecutionException;

import songer.michael.vehiclemate.database.DBController;
import songer.michael.vehiclemate.database.entity.VehicleTripsEntity;

import static songer.michael.vehiclemate.MainActivity.DB_NAME;

public class VehicleTripsInterface
{
    private String strLog = "VTI";

    final private DBController userDB;

    public VehicleTripsInterface(Context context)
    {
        userDB = Room.databaseBuilder(context.getApplicationContext(), DBController.class, DB_NAME).build();
    }

    // Get all trips
    public List<VehicleTripsEntity> getAllTrips(long vehId)
    {
        try
        {
            return new VehicleTripsInterface.getAllAsyncTask(userDB, vehId).execute().get();
        }
        catch (Exception e)
        {
            Log.e(strLog, Throwables.getStackTraceAsString(e));
        }
        return null;
    }
    private static class getAllAsyncTask extends AsyncTask<Void, Void, List<VehicleTripsEntity>>
    {
        private DBController userDB;
        private long vehId;

        getAllAsyncTask(DBController DBN, long vehicleId)
        {
            userDB = DBN;
            vehId = vehicleId;
        }
        @Override
        protected List<VehicleTripsEntity> doInBackground(Void... voids)
        {
            return userDB.vehicleTripsDAO().getAll(vehId);
        }
    }
    // Get trip
    public VehicleTripsEntity getNote(long id)
    {
        try
        {
            return new VehicleTripsInterface.getSingleAsyncTask(userDB, id).execute().get();
        }
        catch (Exception e)
        {
            Log.e(strLog, Throwables.getStackTraceAsString(e));
        }
        return null;
    }
    // Async task for single trip
    private static class getSingleAsyncTask extends AsyncTask<Void, Void, VehicleTripsEntity>
    {
        private DBController userDB;
        private long id;
        getSingleAsyncTask(DBController DBN, long nId)
        {
            userDB = DBN;
            id = nId;
        }
        @Override
        protected VehicleTripsEntity doInBackground(Void... voids)
        {
            return userDB.vehicleTripsDAO().getTrip(id);
        }
    }
    // Insert trip
    public long insert(VehicleTripsEntity veh)
    {
        try
        {
            return new VehicleTripsInterface.insertAsyncTask(userDB, veh).execute().get();
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
        private VehicleTripsEntity tripsEntity;

        insertAsyncTask(DBController DBN, VehicleTripsEntity tripsEntity)
        {
            userDB = DBN;
            this.tripsEntity=tripsEntity;
        }
        @Override
        protected Long doInBackground(Void... voids)
        {
            return userDB.vehicleTripsDAO().addTrip(tripsEntity);
        }
    }
    // Delete trip
    public void delete(final long id)
    {
        final VehicleTripsEntity veh = getNote(id);
        if (veh != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    userDB.vehicleTripsDAO().deleteTrip(id);
                    return null;
                }
            }.execute();
        }
    }
    // Update Trip
    public void update(final VehicleTripsEntity tripsEntity) {
        final VehicleTripsEntity veh = getNote(tripsEntity.getUid());
        if (veh != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    userDB.vehicleTripsDAO().updateTrip(tripsEntity);
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
                userDB.vehicleTripsDAO().deleteAll();
                return null;
            }
        }.execute();
    }
}
