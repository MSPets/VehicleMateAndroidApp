package songer.michael.vehiclemate.database.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import songer.michael.vehiclemate.database.entity.VehicleGraphsEntity;

@Dao
public interface VehicleGraphsDAO
{
    @Transaction
    @Query("SELECT * FROM VehicleGraphsEntity WHERE (vehicle_uid=:veh_id)")
    //public List<VehicleGraphsRelations> getVehicleGraphs();
    List<VehicleGraphsEntity> getAll(long veh_id);
}
