package songer.michael.vehiclemate.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import songer.michael.vehiclemate.database.entity.NotificationEntity;

@Dao
public interface NotificationDAO
{
    // Insert
    @Insert
    Long addNotification(NotificationEntity notificationEntity);

    // Delete
    @Query("DELETE FROM NotificationEntity WHERE (uid=:id)")
    void deleteNotification(long id);

    // Delete all
    @Query("DELETE FROM NotificationEntity")
    void deleteAll();

    // List all
    @Query("SELECT * FROM NotificationEntity WHERE (vehicle_uid=:veh_id)")
    List<NotificationEntity> getAll(long veh_id);

    // Return specific
    @Query("SELECT * FROM NotificationEntity WHERE (uid=:uid)")
    List<NotificationEntity> getNotification(long uid);

    @Update
    void updateNotification(NotificationEntity notificationEntity);
}
