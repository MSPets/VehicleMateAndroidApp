package songer.michael.vehiclemate.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import songer.michael.vehiclemate.database.dao.NotificationDAO;
import songer.michael.vehiclemate.database.dao.VehicleGraphsDAO;
import songer.michael.vehiclemate.database.dao.VehicleInformationDAO;
import songer.michael.vehiclemate.database.dao.VehicleNotesDAO;
import songer.michael.vehiclemate.database.dao.VehicleOutgoingsDAO;
import songer.michael.vehiclemate.database.dao.VehicleTripsDAO;
import songer.michael.vehiclemate.database.entity.NotificationEntity;
import songer.michael.vehiclemate.database.entity.VehicleGraphsEntity;
import songer.michael.vehiclemate.database.entity.VehicleInformationEntity;
import songer.michael.vehiclemate.database.entity.VehicleNotesEntity;
import songer.michael.vehiclemate.database.entity.VehicleOutgoingsEntity;
import songer.michael.vehiclemate.database.entity.VehicleTripsEntity;

@Database(entities = {
        VehicleInformationEntity.class,
        VehicleGraphsEntity.class,
        NotificationEntity.class,
        VehicleNotesEntity.class,
        VehicleOutgoingsEntity.class,
        VehicleTripsEntity.class
}, version = 1)

@TypeConverters({Converters.class})

public abstract class DBController extends RoomDatabase
{
    public abstract VehicleInformationDAO vehicleInformationDAO();
    public abstract VehicleGraphsDAO vehicleGraphsDAO();
    public abstract NotificationDAO notificationDAO();
    public abstract VehicleNotesDAO vehicleNotesDAO();
    public abstract VehicleOutgoingsDAO vehicleOutgoingsDAO();
    public abstract VehicleTripsDAO vehicleTripsDAO();
}
