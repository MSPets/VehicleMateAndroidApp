package songer.michael.vehiclemate.database;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import androidx.room.Room;

import com.google.common.base.Throwables;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import songer.michael.vehiclemate.database.dao.VehicleGraphsDAO;
import songer.michael.vehiclemate.database.dao.VehicleInformationDAO;
import songer.michael.vehiclemate.database.entity.VehicleInformationEntity;
import songer.michael.vehiclemate.database.entity.VehicleNotesEntity;
import songer.michael.vehiclemate.database.entity.VehicleOutgoingsEntity;
import songer.michael.vehiclemate.database.entity.VehicleTripsEntity;
import songer.michael.vehiclemate.database.interfaces.VehicleInformationInterface;
import songer.michael.vehiclemate.database.interfaces.VehicleNotesInterface;
import songer.michael.vehiclemate.database.interfaces.VehicleOutgoingsInterface;
import songer.michael.vehiclemate.database.interfaces.VehicleTripsInterface;

public class PopulateDatabase {
    String strLog = "PD";

    public PopulateDatabase(Context context)
    {
        long vehId = addVehicle(context);
        Log.d(strLog, "Vehicle id = " + vehId);
        addOutgoings(context, vehId);
        addTrips(context, vehId);
        addNotes(context, vehId);
    }
    private long addVehicle(Context context)
    {
        VehicleInformationInterface vehicleInformationInterface = new VehicleInformationInterface(context.getApplicationContext());

        long id = vehicleInformationInterface.insert(new VehicleInformationEntity("Vehicle Type", "Vehicle Model", "Vehicle Name", true));

        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File dir = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(dir, id + ".png");
        try
        {
            URL url = new URL("https://cdn4.iconfinder.com/data/icons/car-silhouettes/1000/sedan-512.png");
            InputStream is = url.openStream();
            FileOutputStream fos = new FileOutputStream(mypath);

            byte[] b = new byte[2048];
            int length;
            while ((length = is.read(b)) != -1)
            {
                fos.write(b, 0, length);
            }
            is.close();
            fos.close();
        }
        catch (Exception e)
        {
            Log.d(strLog, Throwables.getStackTraceAsString(e));
        }
        return id;
    }
    private void addOutgoings(Context context, long vehId)
    {
        VehicleOutgoingsInterface vehicleOutgoingsInterface = new VehicleOutgoingsInterface(context.getApplicationContext());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date date = calendar.getTime();
        vehicleOutgoingsInterface.insert(new VehicleOutgoingsEntity(vehId, "Insurance", 500, date));
        vehicleOutgoingsInterface.insert(new VehicleOutgoingsEntity(vehId, "Misc", 500, date));
        vehicleOutgoingsInterface.insert(new VehicleOutgoingsEntity(vehId, "Misc", 300, date));

        calendar.add(Calendar.MONTH, -1);
        date = calendar.getTime();
        vehicleOutgoingsInterface.insert(new VehicleOutgoingsEntity(vehId, "MOT", 130, date));

        calendar.add(Calendar.DATE, + 4);
        date = calendar.getTime();
        vehicleOutgoingsInterface.insert(new VehicleOutgoingsEntity(vehId, "Tyres", 340.22f, date));
        vehicleOutgoingsInterface.insert(new VehicleOutgoingsEntity(vehId, "Misc", 10.26f, date));
        vehicleOutgoingsInterface.insert(new VehicleOutgoingsEntity(vehId, "Misc", 33.12f, date));
        vehicleOutgoingsInterface.insert(new VehicleOutgoingsEntity(vehId, "Misc", 19.49f, date));
        vehicleOutgoingsInterface.insert(new VehicleOutgoingsEntity(vehId, "Misc", 604.86f, date));

        calendar.add(Calendar.MONTH, -2);
        date = calendar.getTime();
        vehicleOutgoingsInterface.insert(new VehicleOutgoingsEntity(vehId, "Maintenance", 86.59f, date));

        calendar.add(Calendar.DATE,  +7);
        date = calendar.getTime();
        vehicleOutgoingsInterface.insert(new VehicleOutgoingsEntity(vehId, "Chip repair", 40.74f, date));
        vehicleOutgoingsInterface.insert(new VehicleOutgoingsEntity(vehId, "Misc", 50.63f, date));
        vehicleOutgoingsInterface.insert(new VehicleOutgoingsEntity(vehId, "Misc", 60.99f, date));
        vehicleOutgoingsInterface.insert(new VehicleOutgoingsEntity(vehId, "Misc", 10.96f, date));
    }
    private void addTrips(Context context, long vehId)
    {
        VehicleTripsInterface vehicleTripsInterface = new VehicleTripsInterface(context.getApplicationContext());
        Calendar calendar = Calendar.getInstance();
        Date date;

        Random rng = new Random();
        float length;

        length = 2000 + rng.nextFloat() * (1609342 - 2000);
        calendar.add(Calendar.DATE, - 4);
        date = calendar.getTime();
        vehicleTripsInterface.insert(new VehicleTripsEntity(vehId, date, length, 20f,50f, 460f));

        length = 2000 + rng.nextFloat() * (1609342 - 2000);
        calendar.add(Calendar.DATE, - 4);
        date = calendar.getTime();
        vehicleTripsInterface.insert(new VehicleTripsEntity(vehId, date, length, 20f,50f, 460f));

        length = 2000 + rng.nextFloat() * (1609342 - 2000);
        calendar.add(Calendar.DATE, - 4);
        date = calendar.getTime();
        vehicleTripsInterface.insert(new VehicleTripsEntity(vehId, date, length, 20f,50f, 460f));

        length = 2000 + rng.nextFloat() * (1609342 - 2000);
        calendar.add(Calendar.DATE, - 4);
        date = calendar.getTime();
        vehicleTripsInterface.insert(new VehicleTripsEntity(vehId, date, length, 20f,50f, 460f));

        length = 2000 + rng.nextFloat() * (1609342 - 2000);
        calendar.add(Calendar.DATE, - 4);
        date = calendar.getTime();
        vehicleTripsInterface.insert(new VehicleTripsEntity(vehId, date, length, 20f,50f, 460f));

        length = 2000 + rng.nextFloat() * (1609342 - 2000);
        calendar.add(Calendar.DATE, - 4);
        date = calendar.getTime();
        vehicleTripsInterface.insert(new VehicleTripsEntity(vehId, date, length, 20f,50f, 460f));
    }
    private void addNotes(Context context, long vehId)
    {
        VehicleNotesInterface vehicleNotesInterface = new VehicleNotesInterface(context.getApplicationContext());
        vehicleNotesInterface.insert(new VehicleNotesEntity(vehId, "note"));
        vehicleNotesInterface.insert(new VehicleNotesEntity(vehId, "note2"));
        vehicleNotesInterface.insert(new VehicleNotesEntity(vehId, "note3"));
        vehicleNotesInterface.insert(new VehicleNotesEntity(vehId, "note4"));
    }
}