package songer.michael.vehiclemate.database.entity;

import android.app.DatePickerDialog;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class VehicleTripsEntity
{
    @PrimaryKey(autoGenerate = true)
    public long uid;
    @ColumnInfo(name="vehicle_id")
    private long vehicleUid;
    @ColumnInfo(name="date")
    private Date date;
    @ColumnInfo(name="distance_meters")
    private float distanceMeters;
    @ColumnInfo(name="fuel_cost")
    private float fuelCost;
    @ColumnInfo(name="fuel_efficiency")
    private float fuelEfficiency;
    @ColumnInfo(name="total_cost")
    private float totalCost;

    public VehicleTripsEntity(long vehicleUid, Date date, float distanceMeters, float fuelCost, float fuelEfficiency, float totalCost)
    {
        this.vehicleUid = vehicleUid;
        this.date = date;
        this.distanceMeters = distanceMeters;
        this.fuelCost = fuelCost;
        this.fuelEfficiency = fuelEfficiency;
        this.totalCost = totalCost;
    }

    public long getUid() { return this.uid; }
    public long getVehicleUid() {return this.vehicleUid; }
    public Date getDate() {return this.date; }
    public float getDistanceMeters() {return this.distanceMeters; }
    public float getFuelCost() {return this.fuelCost; }
    public float getFuelEfficiency() {return this.fuelEfficiency; }
    public float getTotalCost() {return this.totalCost; }
}
