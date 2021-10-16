package songer.michael.vehiclemate.database.interfaces;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.google.common.base.Throwables;

import java.util.List;
import java.util.concurrent.ExecutionException;

import songer.michael.vehiclemate.database.DBController;
import songer.michael.vehiclemate.database.entity.VehicleInformationEntity;
import static songer.michael.vehiclemate.MainActivity.DB_NAME;

public class VehicleInformationInterface
{
    String strLog = "VII";
    final private DBController userDB;
    // Load database
    public VehicleInformationInterface(Context context)
    {
        userDB = Room.databaseBuilder(context.getApplicationContext(), DBController.class, DB_NAME).build();
    }
    // Get all vehicles
    public List<VehicleInformationEntity> getAllVehicles()
    {
        try
        {
            return new getAllAsyncTask(userDB).execute().get();
        }
        catch (Exception e)
        {
            Log.e(strLog, Throwables.getStackTraceAsString(e));
        }
        return null;
    }
    // Async task for all vehicles
    private static class getAllAsyncTask extends android.os.AsyncTask<Void, Void, List<VehicleInformationEntity>>
    {
        private DBController userDB;
        getAllAsyncTask(DBController DBN)
        {
            userDB = DBN;
        }
        @Override
        protected List<VehicleInformationEntity> doInBackground(Void... voids)
        {
            return userDB.vehicleInformationDAO().getAll();
        }
    }

    public VehicleInformationEntity getVehicle(long id)
    {
        try
        {
            return new getSingleAsyncTask(userDB, id).execute().get();
        }
        catch (Exception e)
        {
            Log.e(strLog, Throwables.getStackTraceAsString(e));
        }
        return null;
    }
    // Async task for all vehicles
    private static class getSingleAsyncTask extends AsyncTask<Void, Void, VehicleInformationEntity>
    {
        private DBController userDB;
        private long id;
        getSingleAsyncTask(DBController DBN, long vehId)
        {
            userDB = DBN;
            id = vehId;
        }
        @Override
        protected VehicleInformationEntity doInBackground(Void... voids)
        {
            return userDB.vehicleInformationDAO().getVehicle(id);
        }
    }


    public long insert(VehicleInformationEntity vehicleInformationEntity)
    {
        try
        {
            return new insertAsyncTask(userDB, vehicleInformationEntity).execute().get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return -1;
    }
    private static class insertAsyncTask extends android.os.AsyncTask<Void, Void, Long>
    {
        private DBController userDB;
        private VehicleInformationEntity vehicleInformationEntity;

        insertAsyncTask(DBController DBN, VehicleInformationEntity vehicleInformationEntity)
        {
            userDB = DBN;
            this.vehicleInformationEntity = vehicleInformationEntity;
        }
        @Override
        protected Long doInBackground(Void... voids)
        {
            return userDB.vehicleInformationDAO().addVehicle(vehicleInformationEntity);
        }
    }

    public void delete(final long id)
    {
        final VehicleInformationEntity informationEntity = getVehicle(id);
        if (informationEntity != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    userDB.vehicleInformationDAO().deleteVehicle(id);
                    return null;
                }
            }.execute();
        }
    }

    public void update(final VehicleInformationEntity informationEntity) {
        final VehicleInformationEntity veh = getVehicle(informationEntity.getUid());
        if (veh != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    userDB.vehicleInformationDAO().updateVehicle(informationEntity);
                    return null;
                }
            }.execute();
        }
    }

    public void deleteAll() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                userDB.vehicleInformationDAO().deleteAll();
                return null;
            }
        }.execute();
    }

}
