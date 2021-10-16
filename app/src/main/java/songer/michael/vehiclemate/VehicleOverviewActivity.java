package songer.michael.vehiclemate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Throwables;

import java.io.File;
import java.io.FileInputStream;

import songer.michael.vehiclemate.database.interfaces.VehicleInformationInterface;

public class VehicleOverviewActivity extends AppCompatActivity
{
    /*TODO:
     * Picture
     * Overview of graphs
     * MOT - check for current and previous
     * Insurance - check for current and previous
     * Maintenance page
     * Trip page
     * Notes page
     * Add delete
     */
    String strLog = "VAO";
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_overview);

        Intent intent = getIntent();
        id = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Log.d("VIA", "id = " + id);

        Button btnGraphs;
        Button btnMaintenance;
        Button btnTrips;
        Button btnNotes;
        Button btnDelete;
        Button btnNotifications;

        try
        {
            ContextWrapper cw = new ContextWrapper(this);

            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File imgPath = new File(directory,id + ".png");
            Bitmap vehicleBitmap = BitmapFactory.decodeStream(new FileInputStream(imgPath));

            ImageView ivCar = (ImageView) this.findViewById(R.id.image_view_vehicle_image);
            ivCar.setImageBitmap(vehicleBitmap);
        }
        catch (Exception e)
        {
            Log.e(strLog, "Error getting image for " + id + "\n" + Throwables.getStackTraceAsString(e));
        }

        btnGraphs = findViewById(R.id.button_graphs);
        btnMaintenance = findViewById(R.id.button_outgoings);
        btnTrips = findViewById(R.id.button_trips);
        btnNotes = findViewById(R.id.button_notes);
        btnDelete = findViewById(R.id.button_delete);
        btnNotifications = findViewById(R.id.button_notifications);

        btnGraphs.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(VehicleOverviewActivity.this, VehicleGraphsActivity.class);
                intent.putExtra(MainActivity.EXTRA_MESSAGE, id);
                startActivityForResult(intent, 1);
            }
        });

        btnMaintenance.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(VehicleOverviewActivity.this, VehicleOutgoingsActivity.class);
                intent.putExtra(MainActivity.EXTRA_MESSAGE, id);
                startActivity(intent);
            }
        });

        btnTrips.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(VehicleOverviewActivity.this, VehicleTripActivity.class);
                intent.putExtra(MainActivity.EXTRA_MESSAGE, id);
                startActivity(intent);
            }
        });

        btnNotes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(VehicleOverviewActivity.this, VehicleNotesActivity.class);
                intent.putExtra(MainActivity.EXTRA_MESSAGE, id);
                startActivity(intent);
            }
        });

        btnNotifications.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(VehicleOverviewActivity.this, NotificationsActivity.class);
                intent.putExtra(MainActivity.EXTRA_MESSAGE, id);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO: Setup delete
                deleteVehicle(id);
            }
        });
    }
    public void deleteVehicle(String id)
    {
        VehicleInformationInterface vehicleInformationInterface = new VehicleInformationInterface(this);
        try
        {
            vehicleInformationInterface.delete(Long.parseLong(id));
        }
        catch (Exception e)
        {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Error Deleting Vehicle");
            alertDialogBuilder.setPositiveButton("Okay",
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            return;
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            TextView message = alertDialog.findViewById(android.R.id.message);
            message.setGravity(Gravity.CENTER);
        }
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        id = data.getStringExtra(MainActivity.EXTRA_MESSAGE);
    }
}