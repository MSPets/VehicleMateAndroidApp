package songer.michael.vehiclemate.graphs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import androidx.preference.PreferenceManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import songer.michael.vehiclemate.database.entity.VehicleTripsEntity;
import songer.michael.vehiclemate.database.interfaces.VehicleTripsInterface;

public class TripsGraph
{
    public TripsGraph(Context context, long vehicleIdLong, boolean isImperial, SimpleDateFormat dateFormatter,
                      BarChart barChart, BarChart barChart2)
    {
        String strLog = "TripGraphs";

        VehicleTripsInterface vehicleTripsInterface = new VehicleTripsInterface(context);
        List<VehicleTripsEntity> vehicleTripsEntityList = vehicleTripsInterface.getAllTrips(vehicleIdLong);

        ArrayList<Date> daysOfTrips = new ArrayList<>();
        ArrayList<Float> distance = new ArrayList<>();
        ArrayList<Float> totalCost = new ArrayList<>();

        // Get dates with distance and total
        boolean added = false;
        for (VehicleTripsEntity entity : vehicleTripsEntityList)
        {
            // Check if date exists
            added = false;
            Log.d(strLog, "date of trip - " + dateFormatter.format(entity.getDate()));

            double distanceConverted;
            if(isImperial)
            {
                distanceConverted = entity.getDistanceMeters()/1609.34;
            }
            else
            {
                distanceConverted = entity.getDistanceMeters()/1000;
            }

            for (int i=0; i<daysOfTrips.size(); i++)
            {
                // Date exists
                if(dateFormatter.format(daysOfTrips.get(i)).equals(dateFormatter.format(entity.getDate())))
                {
                    distance.set(i, distance.get(i) + (float) distanceConverted);
                    totalCost.set(i, totalCost.get(i) + entity.getTotalCost());
                    added = true;
                }
            }
            // New date
            if (!added)
            {
                daysOfTrips.add(entity.getDate());
                distance.add((float) distanceConverted);
                totalCost.add(entity.getTotalCost());
            }
        }

        // Log
        for(int i=0; i<daysOfTrips.size(); i++)
        {
            Log.d(strLog, " Trip on " + dateFormatter.format(daysOfTrips.get(i)).toString() + " distance " + distance.get(i) + " total cost " + totalCost.get(i));
        }

        // Chart 1 (Distance)
        ArrayList<BarEntry> distanceBarEntry = new ArrayList<>();
        // Add cost to barchart entry
        for (int i=0; i<distance.size(); i++)
        {
            distanceBarEntry.add(new BarEntry(i, distance.get(i)));
        }

        // Add Entries to dataset
        String label = "Distance ";
        if (isImperial)
        {
            label += "Miles";
        }
        else
        {
            label += "KM";
        }
        BarDataSet distanceDataset = new BarDataSet(distanceBarEntry, label);

        // Convert dataset to bar data
        BarData distanceData = new BarData(distanceDataset);

        // X axis formatter
        ValueFormatter axisValueFormatter = new TripsGraph.TripsAxisValueFormatter(barChart, daysOfTrips, dateFormatter);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(axisValueFormatter);

        distanceDataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.setFitBars(true);
        //barChart.animateY(5000);

        barChart.setData(distanceData);
        barChart.getDescription().setText("Distance");
        barChart.invalidate();

        // Make visible
        barChart.setVisibility(View.VISIBLE);

        // Chart 2
        ArrayList<BarEntry> totalCostBarEntry = new ArrayList<>();
        // Add cost to barchart entry
        for (int i=0; i<distance.size(); i++)
        {
            totalCostBarEntry.add(new BarEntry(i, totalCost.get(i)));
        }

        // Add Entries to dataset
        BarDataSet totalCostDataset = new BarDataSet(totalCostBarEntry, "Total Cost");

        // Convert dataset to bar data
        BarData totalCostData = new BarData(totalCostDataset);

        // X axis formatter
        ValueFormatter axisValueFormatter2 = new TripsGraph.TripsAxisValueFormatter(barChart, daysOfTrips, dateFormatter);
        XAxis xAxis2 = barChart.getXAxis();
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setDrawGridLines(false);
        xAxis2.setGranularity(1f);
        xAxis2.setLabelCount(7);
        xAxis2.setValueFormatter(axisValueFormatter2);

        totalCostDataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart2.setFitBars(true);
        //barChart2.animateY(5000);

        barChart2.setData(totalCostData);
        barChart2.getDescription().setText("Total Cost");
        barChart2.invalidate();

        // Make visible
        barChart2.setVisibility(View.VISIBLE);
    }
    private class TripsAxisValueFormatter extends ValueFormatter
    {
        private final BarLineChartBase<?> chart;
        private ArrayList<Date> daysOfTrips;
        private SimpleDateFormat dateFormatter;

        public TripsAxisValueFormatter (BarLineChartBase<?> chart, ArrayList<Date> daysOfTrips, SimpleDateFormat dateFormatter)
        {
            this.chart = chart;
            this.daysOfTrips = daysOfTrips;
            this.dateFormatter = dateFormatter;
        }

        @Override
        public String getFormattedValue(float value)
        {
            try
            {
                return dateFormatter.format(this.daysOfTrips.get((int) value));
            }
            catch (Exception e){}
            return "";
        }
    }
}
