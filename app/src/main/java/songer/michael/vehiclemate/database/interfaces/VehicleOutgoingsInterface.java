package songer.michael.vehiclemate.database.interfaces;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.room.Room;

import com.google.common.base.Throwables;

import java.util.List;

import songer.michael.vehiclemate.database.DBController;
import songer.michael.vehiclemate.database.entity.VehicleOutgoingsEntity;

import static songer.michael.vehiclemate.MainActivity.DB_NAME;

public class VehicleOutgoingsInterface
{
    private String strLog = "VOI";
    
    final private DBController userDB;
    
    public VehicleOutgoingsInterface(Context context)
    {
        userDB = Room.databaseBuilder(context.getApplicationContext(), DBController.class, DB_NAME).build();
    }

    // Get all outgoing
    public List<VehicleOutgoingsEntity> getAllOutgoings(long vehId)
    {
        try
        {
            return new VehicleOutgoingsInterface.getAllAsyncTask(userDB, vehId).execute().get();
        }
        catch (Exception e)
        {
            Log.e(strLog, Throwables.getStackTraceAsString(e));
        }
        return null;
    }
    private static class getAllAsyncTask extends AsyncTask<Void, Void, List<VehicleOutgoingsEntity>>
    {
        private DBController userDB;
        private long vehId;

        getAllAsyncTask(DBController DBN, long vehicleId)
        {
            userDB = DBN;
            vehId = vehicleId;
        }
        @Override
        protected List<VehicleOutgoingsEntity> doInBackground(Void... voids)
        {
            List<VehicleOutgoingsEntity> vehicleOutgoingsEntityList = userDB.vehicleOutgoingsDAO().getAll(vehId);

            boolean changed = true;
            while(changed)
            {
                changed = false;
                for(int i = 0; i<vehicleOutgoingsEntityList.size(); i++)
                {
                    if (i+1 < vehicleOutgoingsEntityList.size())
                    {
                        // If next date is before current
                        if (vehicleOutgoingsEntityList.get(i + 1).getDate().after(vehicleOutgoingsEntityList.get(i).getDate())) {
                            // save current
                            VehicleOutgoingsEntity toMove = vehicleOutgoingsEntityList.get(i);
                            // Move next to current
                            vehicleOutgoingsEntityList.set(i, vehicleOutgoingsEntityList.get(i + 1));
                            // Save current as next
                            vehicleOutgoingsEntityList.set(i + 1, toMove);
                            changed = true;
                        }
                    }
                }
            }
            return vehicleOutgoingsEntityList;
        }
    }
    // Get Outgoing
    public VehicleOutgoingsEntity getNote(long id)
    {
        try
        {
            return new VehicleOutgoingsInterface.getSingleAsyncTask(userDB, id).execute().get();
        }
        catch (Exception e)
        {
            Log.e(strLog, Throwables.getStackTraceAsString(e));
        }
        return null;
    }
    // Async task for single outgoing
    private static class getSingleAsyncTask extends AsyncTask<Void, Void, VehicleOutgoingsEntity>
    {
        private DBController userDB;
        private long id;
        getSingleAsyncTask(DBController DBN, long nId)
        {
            userDB = DBN;
            id = nId;
        }
        @Override
        protected VehicleOutgoingsEntity doInBackground(Void... voids)
        {
            return userDB.vehicleOutgoingsDAO().getOutgoing(id);
        }
    }
    // Insert outgoing
    public long insert(VehicleOutgoingsEntity veh)
    {
        try
        {
            return new VehicleOutgoingsInterface.insertAsyncTask(userDB, veh).execute().get();
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
        private VehicleOutgoingsEntity outgoingsEntity;

        insertAsyncTask(DBController DBN, VehicleOutgoingsEntity outgoingsEntity)
        {
            userDB = DBN;
            this.outgoingsEntity=outgoingsEntity;
        }
        @Override
        protected Long doInBackground(Void... voids)
        {
            return userDB.vehicleOutgoingsDAO().addOutgoing(outgoingsEntity);
        }
    }
    // Delete Outgoing
    public void delete(final long id)
    {
        final VehicleOutgoingsEntity veh = getNote(id);
        if (veh != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    userDB.vehicleOutgoingsDAO().deleteOutgoing(id);
                    return null;
                }
            }.execute();
        }
    }
    // Update Outgoing
    public void update(final VehicleOutgoingsEntity noteData) {
        final VehicleOutgoingsEntity veh = getNote(noteData.getUid());
        if (veh != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    userDB.vehicleOutgoingsDAO().updateOutgoing(noteData);
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
                userDB.vehicleOutgoingsDAO().deleteAll();
                return null;
            }
        }.execute();
    }
}
