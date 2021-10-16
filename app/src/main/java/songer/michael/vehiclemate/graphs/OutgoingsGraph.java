package songer.michael.vehiclemate.graphs;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.room.Ignore;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.Description;
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

import songer.michael.vehiclemate.database.entity.VehicleOutgoingsEntity;
import songer.michael.vehiclemate.database.interfaces.VehicleOutgoingsInterface;

public class OutgoingsGraph
{
    public OutgoingsGraph(Context context, long vehicleIdLong, SimpleDateFormat dateFormatter, BarChart barChart)
    {
        String strLog = "OutgoingGraphs";

        VehicleOutgoingsInterface vehicleOutgoingsInterface = new VehicleOutgoingsInterface(context);
        List<VehicleOutgoingsEntity> vehicleOutgoingsEntityList = vehicleOutgoingsInterface.getAllOutgoings(vehicleIdLong);

        ArrayList<Date> daysOfCosts = new ArrayList<>();
        ArrayList<Float> cost = new ArrayList<>();

        boolean added = false;
        for (VehicleOutgoingsEntity entity : vehicleOutgoingsEntityList)
        {
            // Check if date exists
            added = false;
            for (int i=0; i<daysOfCosts.size(); i++)
            {
                // Date exists
                if(dateFormatter.format(daysOfCosts.get(i)).equals(dateFormatter.format(entity.getDate())))
                {
                    cost.set(i, cost.get(i) + entity.getCost());
                    added = true;
                }
            }
            // New date
            if (!added)
            {
                daysOfCosts.add(entity.getDate());
                cost.add(entity.getCost());
            }
        }

        // https://stackoverflow.com/questions/39945375/how-to-set-the-x-axis-label-with-mpandroidchart
        ArrayList<BarEntry> outgoingsBarEntry = new ArrayList<>();
        // Add cost to barchart entry
        for (int i=0; i<cost.size(); i++)
        {
            outgoingsBarEntry.add(new BarEntry(i, cost.get(i)));
        }

        // Add Entries to dataset
        BarDataSet dataset = new BarDataSet(outgoingsBarEntry, "");

        // Convert dataset to bar data
        BarData data = new BarData(dataset);

        // X axis formatter
        ValueFormatter axisValueFormatter = new OutgoingsAxisValueFormatter(barChart, daysOfCosts, dateFormatter);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(axisValueFormatter);

        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.setFitBars(true);
        //barChart.animateY(5000);

        barChart.setData(data);
        barChart.getDescription().setText("Outgoings");
        barChart.invalidate();

        // Make visible
        barChart.setVisibility(View.VISIBLE);
    }
    private class OutgoingsAxisValueFormatter extends ValueFormatter
    {
        private final BarLineChartBase<?> chart;
        private ArrayList<Date> daysOfCosts;
        private SimpleDateFormat dateFormatter;

        public OutgoingsAxisValueFormatter (BarLineChartBase<?> chart, ArrayList<Date> daysOfCosts, SimpleDateFormat dateFormatter)
        {
            this.chart = chart;
            this.daysOfCosts = daysOfCosts;
            this.dateFormatter = dateFormatter;
        }

        @Override
        public String getFormattedValue(float value)
        {
            try
            {
                return dateFormatter.format(this.daysOfCosts.get((int) value));
            }
            catch (Exception e)
            {
            }
            return "";
        }
    }
}
