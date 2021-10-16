package songer.michael.vehiclemate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.common.base.Throwables;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import songer.michael.vehiclemate.database.PopulateDatabase;
import songer.michael.vehiclemate.database.entity.VehicleInformationEntity;
import songer.michael.vehiclemate.database.interfaces.VehicleInformationInterface;

public class MainActivity extends AppCompatActivity
{
    private Button btnNewVehicle;
    private ArrayList<Button> btnArr = new ArrayList<>();

    private String strLog = "MA";

    public static final String EXTRA_MESSAGE = "songer.michael.vehiclemate.EXTRA.MESSAGE";
    public static final String DB_NAME = "songer.michael.vehiclemate.dbname";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO DELETE INFO WITH VEHICLE
        // FOR DEBUGGING
        /*
        VehicleInformationInterface vehicleInformationInterface = new VehicleInformationInterface(this);
        List<VehicleInformationEntity> tempList = vehicleInformationInterface.getAllVehicles();

        if(tempList.size()==0)
        {
            Log.d(strLog, "Populating database");
            new PopulateDatabase(this);
        }
        */
        // load GUI runs only in resume to stop dupe
        loadGUI();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intentSettings = new Intent(this, SettingsActivity.class);
            startActivity(intentSettings);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected  void onResume()
    {
        super.onResume();
        loadGUI();
    }
    private void loadGUI()
    {
        LinearLayout llCVehicles = findViewById(R.id.ll_current_vehicles);
        llCVehicles.removeAllViews();
        llCVehicles.invalidate();

        btnNewVehicle = (Button) findViewById(R.id.btn_new_vehicle);

        btnNewVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openAddVehicle();
            }
        });

        VehicleInformationInterface vehicleInformationInterface = new VehicleInformationInterface(this);
        try
        {
            List<VehicleInformationEntity> vehicleInformationEntities = vehicleInformationInterface.getAllVehicles();

            for(VehicleInformationEntity veh: vehicleInformationEntities)
            {
                addVehBtn(veh.getInfoString());
            }
        }
        catch (Exception e)
        {
            Log.d(strLog, Throwables.getStackTraceAsString(e));
        }
        //new PopulateDatabase(getApplicationContext());
    }

    public void openAddVehicle()
    {
        Intent intentNewVehicle = new Intent(this, NewVehicleActivity.class);
        startActivity(intentNewVehicle);
    }

    private void addVehBtn(String info)
    {
        LinearLayout btnCon = (LinearLayout) findViewById(R.id.ll_current_vehicles);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        float mar = getResources().getDimension(R.dimen.def_margin);
        params.setMargins((int) mar, (int) mar, (int) mar, (int) mar);
        params.gravity = Gravity.CENTER_HORIZONTAL;

        Button btnTmp = new MaterialButton(this);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);

        List<String> listInfo = Arrays.asList(info.split(","));

        // Name
        String sortInfo = "";

        if(!listInfo.get(3).equals("null") && !listInfo.get(3).trim().isEmpty())
        {
            sortInfo += listInfo.get(3);
        }
        // Type
        if(!listInfo.get(1).equals("null") && !listInfo.get(1).trim().isEmpty())
        {
            sortInfo += "\n" + listInfo.get(1);
        }
        // Model
        if(!listInfo.get(2).equals("null") && !listInfo.get(2).trim().isEmpty())
        {
            sortInfo += "\n" + listInfo.get(2);
        }

        btnTmp.setText(sortInfo);

        btnTmp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, VehicleOverviewActivity.class);
                intent.putExtra(EXTRA_MESSAGE, listInfo.get(0));
                startActivity(intent);
            }
        });

        btnCon.addView(btnTmp, params);
        btnArr.add(btnTmp);
    }
}