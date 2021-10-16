package songer.michael.vehiclemate.database.interfaces;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.room.Room;

import com.google.common.base.Throwables;

import java.util.List;
import java.util.concurrent.ExecutionException;

import songer.michael.vehiclemate.database.DBController;
import songer.michael.vehiclemate.database.entity.NotificationEntity;
import songer.michael.vehiclemate.database.entity.NotificationEntity;

import static songer.michael.vehiclemate.MainActivity.DB_NAME;

public class NotificationInterface
{
    String strLog = "NI";
    final private DBController userDB;
    public NotificationInterface(Context context)
    {
        userDB = Room.databaseBuilder(context.getApplicationContext(), DBController.class, DB_NAME).build();
    }
    // Get all notifications
    public List<NotificationEntity> getAllNotifications(long vehId)
    {
        try
        {
            return new NotificationInterface.getAllAsyncTask(userDB, vehId).execute().get();
        }
        catch (Exception e)
        {
            Log.e(strLog, Throwables.getStackTraceAsString(e));
        }
        return null;
    }
    private static class getAllAsyncTask extends AsyncTask<Void, Void, List<NotificationEntity>>
    {
        private DBController userDB;
        private long vehId;
        getAllAsyncTask(DBController DBN, long vehId)
        {
            userDB = DBN;
            this.vehId = vehId;
        }
        @Override
        protected List<NotificationEntity> doInBackground(Void... voids)
        {
            return userDB.notificationDAO().getAll(vehId);
        }
    }
    // Get Notification
    public List<NotificationEntity> getNotification(long id)
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
    private static class getSingleAsyncTask extends AsyncTask<Void, Void, List<NotificationEntity>>
    {
        private DBController userDB;
        private long id;
        getSingleAsyncTask(DBController DBN, long nId)
        {
            userDB = DBN;
            id = nId;
        }
        @Override
        protected List<NotificationEntity> doInBackground(Void... voids)
        {
            return userDB.notificationDAO().getNotification(id);
        }
    }
    // Insert notification
    public long insert(NotificationEntity veh) throws ExecutionException, InterruptedException
    {
        return new insertAsyncTask(userDB, veh).execute().get();
    }
    private static class insertAsyncTask extends android.os.AsyncTask<Void, Void, Long>
    {
        private DBController userDB;
        private NotificationEntity veh;

        insertAsyncTask(DBController DBN, NotificationEntity vehh){userDB = DBN; veh=vehh;}
        @Override
        protected Long doInBackground(Void... voids)
        {
            return userDB.notificationDAO().addNotification(veh);
        }
    }
    // Delete Notification
    public void delete(final long id)
    {
        final List<NotificationEntity> veh = getNotification(id);
        if (veh != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    userDB.notificationDAO().deleteNotification(id);
                    return null;
                }
            }.execute();
        }
    }
    // Update Notification
    public void update(final NotificationEntity notificationEntity) {
        final List<NotificationEntity> veh = getNotification(notificationEntity.getUid());
        if (veh != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    userDB.notificationDAO().updateNotification(notificationEntity);
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
                userDB.notificationDAO().deleteAll();
                return null;
            }
        }.execute();
    }
}
