package songer.michael.vehiclemate.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import songer.michael.vehiclemate.database.entity.VehicleNotesEntity;
import songer.michael.vehiclemate.database.entity.VehicleOutgoingsEntity;

@Dao
public interface VehicleOutgoingsDAO
{
    @Insert
    Long addOutgoing(VehicleOutgoingsEntity vehicleOutgoingsEntity);

    // Delete
    @Query("DELETE FROM VehicleOutgoingsEntity WHERE (uid=:id)")
    void deleteOutgoing(long id);

    // Delete all
    @Query("DELETE FROM VehicleOutgoingsEntity")
    void deleteAll();

    // List all
    @Query("SELECT * FROM VehicleOutgoingsEntity WHERE (vehicle_id=:veh_id)")
    List<VehicleOutgoingsEntity> getAll(long veh_id);

    // Return specific
    @Query("SELECT * FROM VehicleOutgoingsEntity WHERE (uid=:uid)")
    VehicleOutgoingsEntity getOutgoing(long uid);

    // Update Note
    @Update
    void updateOutgoing(VehicleOutgoingsEntity vehicleOutgoingsEntity);
}
