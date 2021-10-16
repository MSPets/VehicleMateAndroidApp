package songer.michael.vehiclemate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.common.base.Throwables;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import songer.michael.vehiclemate.database.entity.VehicleOutgoingsEntity;
import songer.michael.vehiclemate.database.interfaces.VehicleOutgoingsInterface;
import songer.michael.vehiclemate.graphs.OutgoingsGraph;
import songer.michael.vehiclemate.graphs.TripsGraph;

public class VehicleGraphsActivity extends AppCompatActivity
{
    /*TODO:
     * Fuel cost / efficiency overtime
     * Outgoings
     * Trips cost overtime
     */
    // PhilJay:MPAndroidChart:v3.1.0
    // quickchart.io/documentation/
    // https://github.com/PhilJay/MPAndroidChart

    private String strLog = "VGA";
    private String vehicleId;
    private long vehicleIdLong;

    private boolean isImperial;

    private LinearLayout llMainContent;
    private BarChart barChart;
    private BarChart barChart2;

    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_graphs);

        Intent intent = getIntent();
        vehicleId = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        try
        {
            vehicleIdLong = Long.parseLong(vehicleId);
        }
        catch (Exception e)
        {
            Throwables.getStackTraceAsString(e);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Error Loading graphs");
            alertDialogBuilder.setPositiveButton("Okay",
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1)
                        {
                            onOptionsItemSelected(null);
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            TextView message = alertDialog.findViewById(android.R.id.message);
            message.setGravity(Gravity.CENTER);
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if(sp.getString("unit_key", "imperial").equals("imperial"))
        {
            isImperial = true;
        }
        else
        {
            isImperial = false;
        }

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);

        llMainContent = findViewById(R.id.main_content);
        barChart = findViewById(R.id.barchart);
        barChart2 = findViewById(R.id.barchart2);

        Button outgoingButton = findViewById(R.id.button_outgoings_chart);
        outgoingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                llMainContent.setVisibility(View.GONE);
                getOutgoingsGraph();
            }
        });

        Button tripsButton = findViewById(R.id.button_trips_chart);
        tripsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                llMainContent.setVisibility(View.GONE);
                getTripsGraph();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        //setResult(1, new Intent().putExtra(MainActivity.EXTRA_MESSAGE, vehicleId));
        //finish();
        //return true;
        if(llMainContent.getVisibility() == View.GONE)
        {
            barChart.setVisibility(View.GONE);
            barChart2.setVisibility(View.GONE);
            llMainContent.setVisibility(View.VISIBLE);
        }
        else
        {
            setResult(1, new Intent().putExtra(MainActivity.EXTRA_MESSAGE, vehicleId));
            finish();
        }
        return true;
    }

    private void getOutgoingsGraph()
    {
        new OutgoingsGraph(this, vehicleIdLong, dateFormatter, barChart);
    }

    private void getTripsGraph()
    {
        new TripsGraph(this, vehicleIdLong, isImperial, dateFormatter, barChart, barChart2);
    }
}
