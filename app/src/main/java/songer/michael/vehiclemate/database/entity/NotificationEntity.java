package songer.michael.vehiclemate.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.DoubleAccumulator;

@Entity
public class NotificationEntity
{
    @PrimaryKey(autoGenerate = true)
    public long uid;
    @ColumnInfo(name = "vehicle_uid")
    private long vehicleUid;
    @ColumnInfo(name = "label")
    private String label;
    @ColumnInfo(name = "date")
    private Date date;

    public NotificationEntity(long vehicleUid, String label, Date date)
    {
        this.vehicleUid = vehicleUid;
        this.label = label;
        this.date = date;
    }
    @Ignore
    public NotificationEntity(long id, Date date)
    {
        this.uid = id;
        this.date = date;
    }
    public long getUid()
    {
        return this.uid;
    }
    public long getVehicleUid(){return this.vehicleUid;}
    public String getLabel()
    {
        return this.label;
    }
    public Date getDate()
    {
        return this.date;
    }
}
