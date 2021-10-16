package songer.michael.vehiclemate.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class VehicleOutgoingsEntity
{
    @PrimaryKey(autoGenerate = true)
    public long uid;
    @ColumnInfo(name="vehicle_id")
    private long vehicleUid;
    @ColumnInfo(name = "info")
    private String info;
    @ColumnInfo(name = "cost")
    private float cost;
    @ColumnInfo(name = "date")
    private Date date;

    public VehicleOutgoingsEntity(long vehicleUid, String info, float cost, Date date)
    {
        this.vehicleUid = vehicleUid;
        this.info = info;
        this.cost = cost;
        this.date = date;
    }


    public long getUid()
    {
        return this.uid;
    }
    public long getVehicleUid()
    {
        return this.vehicleUid;
    }
    public String getInfo()
    {
        return this.info;
    }
    public float getCost()
    {
        return this.cost;
    }
    public Date getDate()
    {
        return this.date;
    }
}
