package songer.michael.vehiclemate.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import songer.michael.vehiclemate.database.entity.VehicleNotesEntity;

@Dao
public interface VehicleNotesDAO
{
    // Insert
    @Insert
    Long addNote(VehicleNotesEntity vehicleNotesEntity);

    // Delete
    @Query("DELETE FROM VehicleNotesEntity WHERE (uid=:id)")
    void deleteNote(long id);

    // Delete all
    @Query("DELETE FROM VehicleNotesEntity")
    void deleteAll();

    // List all
    @Query("SELECT * FROM VehicleNotesEntity WHERE (vehicle_id=:veh_id)")
    List<VehicleNotesEntity> getAll(long veh_id);

    // Return specific
    @Query("SELECT * FROM VehicleNotesEntity WHERE (uid=:uid)")
    VehicleNotesEntity getNote(long uid);

    // Update Note
    @Update
    void updateNote(VehicleNotesEntity VehicleNotesEntity);

}
