package songer.michael.vehiclemate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.room.ColumnInfo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.common.base.Throwables;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import songer.michael.vehiclemate.database.entity.VehicleNotesEntity;
import songer.michael.vehiclemate.database.entity.VehicleOutgoingsEntity;
import songer.michael.vehiclemate.database.interfaces.VehicleNotesInterface;
import songer.michael.vehiclemate.database.interfaces.VehicleOutgoingsInterface;

public class VehicleOutgoingsActivity extends AppCompatActivity
{
    private String strLog = "VOA";
    int defHintColour;
    int defTextColour;
    int errorTextColour;
    int textStyle;

    private String vehicleId;

    ConstraintLayout clOutgoing;
    LinearLayout llCurCosts;
    LinearLayout llNewCost;

    Date dateOfCost;
    SimpleDateFormat dateFormatter;
    EditText etCostDate;

    List<VehicleOutgoingsEntity> outGoingsList;

    // https://www.mysamplecode.com/2012/07/android-listview-custom-layout-filter.html

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_outgoings);

        // Extra message
        Intent intent = getIntent();
        vehicleId = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);

        // Layouts for switching
        clOutgoing = findViewById(R.id.linear_layout_outgoings);
        llCurCosts = findViewById(R.id.linear_layout_outgoing_info);
        llNewCost = findViewById(R.id.linear_layout_new_outgoing);
        // Default text style
        textStyle = android.R.style.TextAppearance_Widget_EditText;

        // New Outgoing
        Button newOutgoing = findViewById(R.id.button_new_outgoing);
        newOutgoing.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // load other page
                newOutgoing();
            }
        });

        Button search = findViewById(R.id.button_search);
        search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                searchEntities();
            }
        });

        loadPreviousOutgoings();
    }
    // Back button
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Return
        if (llNewCost.getVisibility() == View.VISIBLE)
        {
            llNewCost.setVisibility(View.GONE);
            loadPreviousOutgoings();
            clOutgoing.setVisibility(View.VISIBLE);
        }
        else
        {
            setResult(1, new Intent().putExtra(MainActivity.EXTRA_MESSAGE, vehicleId));
            finish();
        }
        return true;
    }

    // Get previous outgoings
    private void loadPreviousOutgoings()
    {
        llCurCosts.removeAllViews();
        llCurCosts.invalidate();
        try
        {
            VehicleOutgoingsInterface vehicleOutgoingsInterface = new VehicleOutgoingsInterface(this);
            outGoingsList = vehicleOutgoingsInterface.getAllOutgoings(Long.parseLong(vehicleId));
            Log.d(strLog, "Got outgoings for " + vehicleId);
            for(VehicleOutgoingsEntity vehicleOutgoingsEntity: outGoingsList)
            {
                makeTableGui(vehicleOutgoingsEntity.getUid(),
                        vehicleOutgoingsEntity.getInfo(),
                        vehicleOutgoingsEntity.getCost(),
                        vehicleOutgoingsEntity.getDate());
            }
        }
        catch (Exception e)
        {
            Log.e(strLog, Throwables.getStackTraceAsString(e));
        }
        Log.d(strLog, "Done Loading Outgoings");
    }
    // Holder for one outgoing
    private void makeTableGui(long id, String info, float cost, Date date)
    {
        TextView tvInfo = new TextView(this);
        tvInfo.setText(info);
        tvInfo.setGravity(Gravity.CENTER_HORIZONTAL);
        tvInfo.setTextAppearance(this, textStyle);

        TextView tvCost = new TextView(this);
        tvCost.setText(String.valueOf(cost));
        tvCost.setGravity(Gravity.CENTER_HORIZONTAL);
        tvCost.setTextAppearance(this, textStyle);

        TextView tvDate = new TextView(this);
        tvDate.setText(dateFormatter.format(date));
        tvDate.setGravity(Gravity.CENTER_HORIZONTAL);
        tvDate.setTextAppearance(this, textStyle);

        Button del = new MaterialButton(this);
        del.setText(getResources().getText(R.string.delete));
        del.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                deleteOutgoing(id);
            }
        });

        // Setup Table
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        float mar = getResources().getDimension(R.dimen.def_margin);
        params.setMargins((int) mar, (int) mar/2, (int) mar, (int) mar/2);
        params.weight = 1;

        // New Table
        TableLayout newTable = new TableLayout(this);
        // First Row
        TableRow newRow = new TableRow(this);
        newRow.addView(tvInfo, params);
        newTable.addView(newRow);
        // Second Row
        newRow = new TableRow(this);
        newRow.addView(tvCost, params);
        newRow.addView(tvDate, params);
        newTable.addView(newRow);
        newRow = new TableRow(this);
        newRow.addView(del, params);
        newTable.addView(newRow);
        llCurCosts.addView(newTable);
    }
    private void deleteOutgoing(long id)
    {
        VehicleOutgoingsInterface vehicleOutgoingsInterface = new VehicleOutgoingsInterface(this);
        vehicleOutgoingsInterface.delete(id);
        loadPreviousOutgoings();
    }

    private void searchEntities()
    {
        llCurCosts.removeAllViews();
        llCurCosts.invalidate();

        EditText etSearch = findViewById(R.id.edit_text_search);
        if (!etSearch.getText().toString().trim().isEmpty())
        {
            String searchFor = etSearch.getText().toString();
            if (outGoingsList.size() > 0)
            {
                Log.d(strLog, "Searching for " + searchFor);
                for (VehicleOutgoingsEntity entity : outGoingsList)
                {
                    if(entity.getInfo().toLowerCase().contains(searchFor))
                    {
                        Log.d(strLog, "found " + entity.getInfo());
                        makeTableGui(entity.getUid(), entity.getInfo(), entity.getCost(), entity.getDate());
                    }
                }
            }
        }
        else
        {
            loadPreviousOutgoings();
        }
    }

    // New outgoing
    private void newOutgoing()
    {
        clOutgoing.setVisibility(View.GONE);
        llNewCost.setVisibility(View.VISIBLE);

        // Get Cost Date
        etCostDate = findViewById(R.id.edit_text_date);
        etCostDate.setInputType(InputType.TYPE_NULL);
        // Text colours
        errorTextColour = getResources().getColor(R.color.error);
        defTextColour = etCostDate.getCurrentTextColor();
        defHintColour = etCostDate.getCurrentHintTextColor();
        etCostDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Close Keyboard
                InputMethodManager iMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                iMM.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // Select Date
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                DatePickerDialog dPD = new DatePickerDialog(VehicleOutgoingsActivity.this, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        etCostDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
                dPD.show();
            }
        });
        Button submit = findViewById(R.id.button_outgoing_submit);
        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                submitOutgoing();
            }
        });

    }
    // Submit new outgoing
    private void submitOutgoing()
    {
        Boolean complete = true;

        // Check info
        EditText info = findViewById(R.id.edit_text_information);
        if (info.getText().toString().trim().isEmpty())
        {
            info.setTextColor(errorTextColour);
            info.setHintTextColor(errorTextColour);
            complete = false;
        }
        else
        {
            info.setTextColor(defTextColour);
            info.setHintTextColor(defHintColour);
        }
        // Check cost
        EditText cost = findViewById(R.id.edit_text_outgoing_cost);
        if(cost.getText().toString().trim().isEmpty())
        {
            cost.setTextColor(errorTextColour);
            cost.setHintTextColor(errorTextColour);
            complete = false;
        }
        else
        {
            cost.setTextColor(defTextColour);
            cost.setHintTextColor(defHintColour);
        }
        // Check Date
        try
        {
            dateOfCost = dateFormatter.parse(etCostDate.getText().toString());
        }
        catch (Exception e)
        {
            Log.e(strLog, Throwables.getStackTraceAsString(e));
            complete = false;
            etCostDate.setText("");
        }

        if(!complete)
        {
            return;
        }
        else
        {
            // Add to database
            long outgoingID = -1;
            VehicleOutgoingsInterface vehicleOutgoingsInterface = new VehicleOutgoingsInterface(this);
            try
            {
                outgoingID = vehicleOutgoingsInterface.insert(new VehicleOutgoingsEntity(Long.parseLong(vehicleId), info.getText().toString(), Float.parseFloat(cost.getText().toString()), dateOfCost));
            }
            catch (Exception e)
            {
                Log.e(strLog, Throwables.getStackTraceAsString(e));
            }
            // Error adding
            if (outgoingID == -1)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Error adding Outgoing");
                alertDialogBuilder.setPositiveButton("Okay",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1)
                            {
                                return;
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                TextView message = alertDialog.findViewById(android.R.id.message);
                message.setGravity(Gravity.CENTER);
            }
            else
            {
                info.setText("");
                cost.setText("");
                etCostDate.setText("");
                onOptionsItemSelected(null);
            }
        }
    }
}