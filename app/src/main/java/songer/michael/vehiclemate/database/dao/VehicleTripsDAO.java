package songer.michael.vehiclemate.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import songer.michael.vehiclemate.database.entity.VehicleTripsEntity;

@Dao
public interface VehicleTripsDAO
{
    @Insert
    Long addTrip(VehicleTripsEntity vehicleTripsEntity);

    // Delete
    @Query("DELETE FROM VehicleTripsEntity WHERE (uid=:id)")
    void deleteTrip(long id);

    // Delete all
    @Query("DELETE FROM VehicleTripsEntity")
    void deleteAll();

    // List all
    @Query("SELECT * FROM VehicleTripsEntity WHERE (vehicle_id=:veh_id)")
    List<VehicleTripsEntity> getAll(long veh_id);

    // Return specific
    @Query("SELECT * FROM VehicleTripsEntity WHERE (uid=:uid)")
    VehicleTripsEntity getTrip(long uid);

    // Update Note
    @Update
    void updateTrip(VehicleTripsEntity vehicleTripsEntity);
}
