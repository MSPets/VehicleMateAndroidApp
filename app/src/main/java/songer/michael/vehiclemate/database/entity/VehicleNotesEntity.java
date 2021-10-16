package songer.michael.vehiclemate.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class VehicleNotesEntity
{
    @PrimaryKey(autoGenerate = true)
    public long uid;
    @ColumnInfo(name = "vehicle_id")
    private long vehicleUid;
    @ColumnInfo(name = "note")
    private String note;

    public VehicleNotesEntity(long vehicleUid, String note)
    {
        this.vehicleUid = vehicleUid;
        this.note = note;
    }
    public String getNote(){return this.note;}
    public long getUid(){return this.uid;}
    public long getVehicleUid(){return this.vehicleUid;}
    public void setNote(String note){this.note = note;}
}
