package songer.michael.vehiclemate.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class VehicleGraphsEntity
{
    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "vehicle_uid")
    public int vehicleUid;
    public int motUid;
    public int insuranceUid;
    public int maintenanceUid;
}
