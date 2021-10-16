package songer.michael.vehiclemate.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity
public class VehicleInformationEntity
{
    @PrimaryKey(autoGenerate = true)
    public long uid;

    // Basic info
    @ColumnInfo(name = "vehicle_type")
    public String vehicleType;
    @ColumnInfo(name = "vehicle_model")
    public String vehicleModel;
    @ColumnInfo(name = "vehicle_name")
    public String vehicleName;
    @ColumnInfo(name="vehicle_fuel_size")
    public float fuelSize;

    // Misc
    @ColumnInfo(name= "is_electric" )
    public boolean isElectric;
    @ColumnInfo(name = "vehicle_efficiency")
    public float vehicleEfficiency = 0f;
    @ColumnInfo(name = "previous_fuel_cost")
    public float prevFuelCost = 0f;

    public VehicleInformationEntity(String type, String model, String name, Boolean isElectric)
    {
        this.vehicleType = type;
        this.vehicleModel = model;
        this.vehicleName = name;
        this.isElectric = isElectric;
    }
    public VehicleInformationEntity(){}

    public long getUid()
    {
        return uid;
    }

    public List<String> getInfoList()
    {
        List<String> vehInfo = new ArrayList<String>();
        vehInfo.add(String.valueOf(uid));
        vehInfo.add(vehicleType);
        vehInfo.add(vehicleModel);
        vehInfo.add(vehicleName);

        return vehInfo;
    }
    public String getInfoString()
    {
        String infoStr = uid + ",";
        infoStr += vehicleType + ",";
        infoStr += vehicleModel + ",";
        infoStr += vehicleName + ",";

        return infoStr;
    }

    public void setFuelEfficiency(float fuelEfficiency)
    {
        this.vehicleEfficiency = fuelEfficiency;
    }
    public void setFuelCost(float fuelCost)
    {
        this.prevFuelCost = fuelCost;
    }
}
