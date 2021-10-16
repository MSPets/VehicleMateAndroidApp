package songer.michael.vehiclemate.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import songer.michael.vehiclemate.database.entity.VehicleInformationEntity;

@Dao
public interface VehicleInformationDAO
{
    // Insert
    @Insert
    Long addVehicle(VehicleInformationEntity vehicleInfo);

    // Delete
    @Query("DELETE FROM vehicleInformationEntity WHERE (uid=:id)")
    void deleteVehicle(long id);

    // Delete all
    @Query("DELETE FROM vehicleInformationEntity")
    void deleteAll();

    // List all
    @Query("SELECT * FROM vehicleInformationEntity")
    List<VehicleInformationEntity> getAll();

    // Return specific
    @Query("SELECT * FROM vehicleInformationEntity WHERE (uid=:vehicleId)")
    VehicleInformationEntity getVehicle(long vehicleId);

    @Update
    void updateVehicle(VehicleInformationEntity vehicleInformationEntity);
}
