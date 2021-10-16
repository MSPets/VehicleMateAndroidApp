package songer.michael.vehiclemate;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.button.MaterialButton;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.common.permission.AppPermissionHandler;
import com.tomtom.online.sdk.map.Icon;
import com.tomtom.online.sdk.map.MapFragment;
import com.tomtom.online.sdk.map.Marker;
import com.tomtom.online.sdk.map.MarkerBuilder;
import com.tomtom.online.sdk.map.OnMapReadyCallback;
import com.tomtom.online.sdk.map.Route;
import com.tomtom.online.sdk.map.RouteBuilder;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.map.TomtomMapCallback;
import com.tomtom.online.sdk.routing.OnlineRoutingApi;
import com.tomtom.online.sdk.routing.RoutingApi;
import com.tomtom.online.sdk.routing.RoutingException;

import com.tomtom.online.sdk.routing.route.RouteCalculationDescriptor;
import com.tomtom.online.sdk.routing.route.RouteCallback;
import com.tomtom.online.sdk.routing.route.RouteDescriptor;
import com.tomtom.online.sdk.routing.route.RoutePlan;
import com.tomtom.online.sdk.routing.route.RouteSpecification;
import com.tomtom.online.sdk.routing.route.description.RouteType;
import com.tomtom.online.sdk.routing.route.description.Summary;
import com.tomtom.online.sdk.routing.route.information.FullRoute;
import com.tomtom.online.sdk.search.OnlineSearchApi;
import com.tomtom.online.sdk.search.SearchApi;
import com.tomtom.online.sdk.search.data.reversegeocoder.ReverseGeocoderSearchQueryBuilder;
import com.tomtom.online.sdk.search.data.reversegeocoder.ReverseGeocoderSearchResponse;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import songer.michael.vehiclemate.database.entity.VehicleInformationEntity;
import songer.michael.vehiclemate.database.entity.VehicleTripsEntity;
import songer.michael.vehiclemate.database.interfaces.VehicleInformationInterface;
import songer.michael.vehiclemate.database.interfaces.VehicleTripsInterface;

import static java.util.Arrays.asList;

public class VehicleTripActivity extends AppCompatActivity implements OnMapReadyCallback, TomtomMapCallback.OnMapLongClickListener
{
    /*TODO:
     * Show previous trips
     */
    // String for finding logs
    private final String strLog = "VTA";
    // Vehicle id
    private long vehicleId;

    // Layouts
    private LinearLayout llMainContent;
    private RelativeLayout rlMapContent;
    private LinearLayout llPreviousTripsContent;
    // Map Variables
    private MapFragment mapFragment;
    private TomtomMap tomtomMap;
    private SearchApi searchApi;
    private RoutingApi routingApi;
    private Route route;
    private LatLng departurePosition;
    private LatLng destinationPosition;
    private LatLng wayPointPosition;
    private Icon departureIcon;
    private Icon destinationIcon;
    private @NotNull Summary routeInfo;
    public static final String SEARCH_API_KEY = "fVGbAkHdYa7v65xBDhcrNqw8T7JKUOk3";
    public static final String ROUTING_API_KEY = "fVGbAkHdYa7v65xBDhcrNqw8T7JKUOk3";
    // Vehicle Variables
    private Date dateOfTrip;
    private boolean isElectric = false;
    private boolean isImperial = true;
    private float distanceMeters = 0f;
    private float fuelEfficiency = 0f;
    private float fuelCost = 0f;
    private float totalCost = 0f;
    // Vehicle Edit Text
    private EditText etDay;
    private EditText etDistance;
    private EditText etFuelEfficiency;
    private EditText etFuelCost;
    private TextView tvTotalCost;
    // Format for output
    private DecimalFormat df;
    private SimpleDateFormat dateFormatter;
    // Text colour
    private int defHintColor = 0;
    private int defTextColor = 0;
    private int textStyle;

    // Start
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_trip);

        Intent intent = getIntent();
        String strId = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        try
        {
            vehicleId = Long.parseLong(strId);
        }
        catch (Exception e)
        {
            Log.e(strLog, Throwables.getStackTraceAsString(e));
            //MenuItem home = (MenuItem) findViewById(R.id.home);
            onOptionsItemSelected(null);
        }

        df = new DecimalFormat("#.##");
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);

        // Get default text colour
        etDistance = findViewById(R.id.edit_text_distance);
        defHintColor = etDistance.getCurrentHintTextColor();
        defTextColor = etDistance.getCurrentTextColor();
        textStyle = android.R.style.TextAppearance_Widget_EditText;

        // Get layout content
        llMainContent = findViewById(R.id.main_content);
        rlMapContent = findViewById(R.id.map_content);
        llPreviousTripsContent = findViewById(R.id.previous_trips);

        // Plot route button
        Button btnPlotRoute = findViewById(R.id.button_plot_route);
        btnPlotRoute.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                llMainContent.setVisibility(View.GONE);
                rlMapContent.setVisibility(View.VISIBLE);
            }
        });
        // Confirm Button
        Button btnConfirm = findViewById(R.id.button_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onOptionsItemSelected(null);
            }
        });
        // Calculate trip button
        Button btnCalc = findViewById(R.id.button_calculate_trip);
        btnCalc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                calculateCost();
            }
        });
        // Save Trip Button
        Button btnSave = findViewById(R.id.button_save_trip);
        btnSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveTrip();
            }
        });
        // Previous Trips
        // Plot route button
        Button btnPreviousTrips = findViewById(R.id.button_previous_trips);
        btnPreviousTrips.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                loadPreviousTrips();
                llMainContent.setVisibility(View.GONE);
                llPreviousTripsContent.setVisibility(View.VISIBLE);
            }
        });

        // Get GUI
        etDay = findViewById(R.id.edit_text_date);
        etDistance = findViewById(R.id.edit_text_distance);
        etFuelEfficiency = findViewById(R.id.edit_text_efficiency);
        etFuelCost = findViewById(R.id.edit_text_fuel_cost);
        tvTotalCost = findViewById(R.id.text_view_total_cost);

        tvTotalCost.setTextAppearance(this, textStyle);

        etDay.setInputType(InputType.TYPE_NULL);
        final Calendar cldr = Calendar.getInstance();
        etDay.setText(cldr.get(Calendar.DAY_OF_MONTH) + "/" + (cldr.get(Calendar.MONTH) + 1) + "/" + cldr.get(Calendar.YEAR));
        etDay.setOnClickListener(new View.OnClickListener()
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
                DatePickerDialog dPD = new DatePickerDialog(VehicleTripActivity.this, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        etDay.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
                dPD.show();
            }
        });


        // Setup map
        fillInfo();
        initLocationPermissions();
        setMapProperties();
        // Setup GUI
        setUIFields();
    }
    // Back button
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // If on map content
        if (rlMapContent.getVisibility() == View.VISIBLE)
        {
            try
            {
                updateGui(routeInfo.getLengthInMeters());
            }
            catch (Exception e)
            {
                Log.e(strLog, Throwables.getStackTraceAsString(e));
            }
            rlMapContent.setVisibility(View.GONE);
            llMainContent.setVisibility(View.VISIBLE);
        }
        // If on previous Trips content
        else if(llPreviousTripsContent.getVisibility() == View.VISIBLE)
        {
            llPreviousTripsContent.setVisibility(View.GONE);
            llMainContent.setVisibility(View.VISIBLE);
        }
        else
        {
            setResult(1, new Intent().putExtra(MainActivity.EXTRA_MESSAGE, vehicleId));
            finish();
        }
        return true;
    }
    // Permissions
    private void initLocationPermissions()
    {
        //Log.d(strLog, "initLocationPermissions");
        AppPermissionHandler permissionHandler = new AppPermissionHandler(this);
        permissionHandler.addLocationChecker();
        permissionHandler.askForNotGrantedPermissions();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //this.tomtomMap.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    /*
     *  MAP SETTINGS
     */
    private void setMapProperties()
    {
        //Log.d(strLog,"setMapProperties");

        mapFragment = MapFragment.newInstance();
        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getAsyncMap(this);

        //mapFragment.getView().setVisibility(View.GONE);
        searchApi = OnlineSearchApi.create(this, SEARCH_API_KEY);
        routingApi = OnlineRoutingApi.create(this, ROUTING_API_KEY);
    }
    private void setUIFields()
    {
        //Log.d(strLog, "setUIFields");
        departureIcon = Icon.Factory.fromResources(VehicleTripActivity.this, R.drawable.ic_map_route_departure);
        destinationIcon = Icon.Factory.fromResources(VehicleTripActivity.this, R.drawable.ic_map_route_destination);
    }
    @Override
    public void onMapReady(final TomtomMap tomtomMap)
    {
        {
            //Log.d(strLog, "onMapReady");
            //Map is ready here
            this.tomtomMap = tomtomMap;
            this.tomtomMap.setMyLocationEnabled(true);
            this.tomtomMap.setMyLocationEnabled(true);
            this.tomtomMap.addOnMapLongClickListener(this);
            this.tomtomMap.getMarkerSettings().setMarkersClustering(true);
            //this.tomtomMap.collectLogsToFile(SampleApp.LOG_FILE_PATH);
        }
    }
    @Override
    public void onMapLongClick(LatLng latLng)
    {
        //Log.d(strLog, "onMapLongClick");
        if (isDeparturePositionSet() && isDestinationPositionSet()) {
            clearMap();
        } else {
            handleLongClick(latLng);
        }
    }
    private void handleLongClick(LatLng latLng)
    {
        //Log.d(strLog, "handleLongClick");
        searchApi.reverseGeocoding(new ReverseGeocoderSearchQueryBuilder(latLng.getLatitude(), latLng.getLongitude()).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<ReverseGeocoderSearchResponse>() {
                    @Override
                    public void onSuccess(ReverseGeocoderSearchResponse response) {
                        processResponse(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        handleApiError(e);
                    }

                    private void processResponse(ReverseGeocoderSearchResponse response) {
                        if (response.hasResults()) {
                            processFirstResult(response.getAddresses().get(0).getPosition());
                        }
                        else {
                            Toast.makeText(VehicleTripActivity.this, getString(R.string.geocode_no_results), Toast.LENGTH_SHORT).show();
                        }
                    }

                    private void processFirstResult(LatLng geocodedPosition) {
                        if (!isDeparturePositionSet()) {
                            setAndDisplayDeparturePosition(geocodedPosition);
                        } else {
                            destinationPosition = geocodedPosition;
                            tomtomMap.removeMarkers();
                            drawRoute(departurePosition, destinationPosition);
                        }
                    }

                    private void setAndDisplayDeparturePosition(LatLng geocodedPosition) {
                        departurePosition = geocodedPosition;
                        createMarkerIfNotPresent(departurePosition, departureIcon);
                    }
                });
    }
    private void createMarkerIfNotPresent(LatLng position, Icon icon)
    {
        //Log.d(strLog, "createMarkerIfNotPresent");
        Optional<Marker> optionalMarker = tomtomMap.findMarkerByPosition(position);
        if (!optionalMarker.isPresent()) {
            tomtomMap.addMarker(new MarkerBuilder(position)
                    .icon(icon));
        }
    }
    private void drawRoute(LatLng start, LatLng stop)
    {
        //Log.d(strLog, "drawRoute");
        wayPointPosition = null;
        drawRouteWithWayPoints(start, stop, null);
    }
    private void drawRouteWithWayPoints(LatLng start, LatLng stop, LatLng[] wayPoints)
    {
        //Log.d(strLog, "drawRouteWithWayPoints");
        RouteSpecification routeSpecification = createRouteSpecification(start, stop, wayPoints);
        //showDialogInProgress();
        routingApi.planRoute(routeSpecification, new RouteCallback() {
            @Override
            public void onSuccess(@NotNull RoutePlan routePlan) {
                displayRoutes(routePlan.getRoutes());
                tomtomMap.displayRoutesOverview();
            }

            @Override
            public void onError(@NotNull RoutingException e) {
                handleApiError(e);
                clearMap();
            }

            private void displayRoutes(List routes) {
                FullRoute fullRoute;
                for (int i = 0; i < routes.size(); i++)
                {
                    fullRoute = (FullRoute) routes.get(i);
                    route = tomtomMap.addRoute(new RouteBuilder(fullRoute.getCoordinates()).startIcon(departureIcon).endIcon(destinationIcon));
                    // Save summary
                    Log.d(strLog, String.valueOf(fullRoute.getSummary()));
                    routeInfo = fullRoute.getSummary();
                }
            }
        });
    }
    private RouteSpecification createRouteSpecification(LatLng start, LatLng stop, LatLng[] wayPoints)
    {
        //Log.d(strLog, "createRouteSpecification");
        RouteDescriptor routeDescriptor = new RouteDescriptor.Builder()
                .routeType(RouteType.FASTEST)
                .build();
        RouteCalculationDescriptor routeCalculationDescriptor = createRouteCalculationDescriptor(routeDescriptor, wayPoints);
        return new RouteSpecification.Builder(start, stop)
                .routeCalculationDescriptor(routeCalculationDescriptor)
                .build();
    }
    private RouteCalculationDescriptor createRouteCalculationDescriptor(RouteDescriptor routeDescriptor, LatLng[] wayPoints)
    {
        //Log.d(strLog, "createRouteCalculationDescriptor");
        RouteCalculationDescriptor routeCalculationDescriptor;
        if(wayPoints != null)
        {
            routeCalculationDescriptor = new RouteCalculationDescriptor.Builder()
                    .routeDescription(routeDescriptor)
                    .waypoints(asList(wayPoints)).build();
        }
        else
        {
            routeCalculationDescriptor = new RouteCalculationDescriptor.Builder()
                    .routeDescription(routeDescriptor).build();
        }
        routeCalculationDescriptor.getReport();

        return routeCalculationDescriptor;
    }
    private void clearMap()
    {
        //Log.d(strLog, "clearMap");
        tomtomMap.clear();
        departurePosition = null;
        destinationPosition = null;
        route = null;
    }
    private boolean isDestinationPositionSet()
    {
        return destinationPosition != null;
    }
    private boolean isDeparturePositionSet()
    {
        return departurePosition != null;
    }
    private void handleApiError(Throwable e)
    {
        Toast.makeText(VehicleTripActivity.this, getString(R.string.api_response_error, e.getLocalizedMessage()), Toast.LENGTH_LONG).show();
    }
    /*
     * END MAP SETTINGS
     */
    /*
     * MAIN GUI
     */
    private void fillInfo()
    {
        // Get vehicle info
        VehicleInformationInterface vehicleInformationInterface = new VehicleInformationInterface(this);
        VehicleInformationEntity listVeh = vehicleInformationInterface.getVehicle(vehicleId);
        VehicleInformationEntity veh = listVeh;

        isElectric = veh.isElectric;
        fuelEfficiency = veh.vehicleEfficiency;
        fuelCost = veh.prevFuelCost;
        updateGui(0);
    }
    private void updateGui(int lengthInMeters)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String unit = sp.getString("unit_key", "imperial");

        // Get/set distance with unit
        TextView tv = findViewById(R.id.text_view_distance);
        tv.setTextAppearance(this, textStyle);
        if(unit.equals("imperial"))
        {tv.setText("Mile(s)");}
        else
        {tv.setText("km(s)");}
        String dis = "";
        // Convert distance
        if (lengthInMeters != 0)
        {
            // Meters to Miles
            if(unit.equals("imperial"))
            {isImperial=true; dis = String.valueOf((float)lengthInMeters/1609.34);}
            // Meters to km
            else
            {isImperial=false; dis = String.valueOf((float)lengthInMeters/1000);}
            etDistance.setText(df.format(Float.valueOf(dis)));
        }
        // Get/Set efficiency with unit
        if(fuelEfficiency != 0)
        { etFuelEfficiency.setText(String.valueOf(fuelEfficiency)); }
        tv = findViewById(R.id.text_view_efficiency);
        tv.setTextAppearance(this, textStyle);
        if(unit.equals("imperial"))
        {
            if(isElectric)
            { tv.setText("KWH"); }
            else
            { tv.setText("MPG"); }
        }
        else
        {
            if(isElectric)
            { tv.setText("KWH"); }
            else
            { tv.setText("KML"); }
        }
        // Get/Set fuel cost
        if (fuelCost != 0)
        { etFuelCost.setText(String.valueOf(fuelCost)); }

        tv = findViewById(R.id.text_view_fuel_cost);
        tv.setTextAppearance(this, textStyle);
        if(unit.equals("imperial"))
        {
            if(isElectric)
            { tv.setText("Per KW"); }
            else
            { tv.setText("Per Gallon");}
        }
        else
        {
            if(isElectric)
            { tv.setText("Per KW");}
            else
            { tv.setText("Per Liter"); }
        }
    }
    private boolean calculateCost()
    {
        boolean complete = true;
        float tempDistance = 0;
        float tempEfficiency = 0f;
        float tempFuelCost = 0f;

        try
        {
            if(etDay.getText().toString().trim().isEmpty()) throw new Exception();
            dateOfTrip = dateFormatter.parse(etDay.getText().toString());
            etDay.setTextColor(defTextColor);
            etDay.setHintTextColor(defHintColor);
        }
        catch (Exception e)
        {
            etDay.setTextColor(getResources().getColor(R.color.error));
            etDay.setHintTextColor(getResources().getColor(R.color.error));
            complete = false;
        }
        try
        {
            tempDistance = Float.parseFloat(etDistance.getText().toString());
            if (tempDistance == 0) throw new Exception();
            etDistance.setTextColor(defTextColor);
            etDistance.setHintTextColor(defHintColor);
        }
        catch (Exception e)
        {
            etDistance.setTextColor(getResources().getColor(R.color.error));
            etDistance.setHintTextColor(getResources().getColor(R.color.error));
            complete = false;
        }
        try
        {
            tempEfficiency = Float.parseFloat(etFuelEfficiency.getText().toString());
            if (tempEfficiency == 0) throw new Exception();
            etFuelEfficiency.setTextColor(defTextColor);
            etFuelEfficiency.setHintTextColor(defHintColor);
        }
        catch (Exception e)
        {
            etFuelEfficiency.setTextColor(getResources().getColor(R.color.error));
            etFuelEfficiency.setHintTextColor(getResources().getColor(R.color.error));
            complete = false;
        }
        try
        {
            tempFuelCost = Float.parseFloat(etFuelCost.getText().toString());
            if (tempFuelCost == 0) throw new Exception();
            etFuelCost.setTextColor(defTextColor);
            etFuelCost.setHintTextColor(defHintColor);
        }
        catch (Exception e)
        {
            etFuelCost.setTextColor(getResources().getColor(R.color.error));
            etFuelCost.setHintTextColor(getResources().getColor(R.color.error));
            complete = false;
        }

        if (!complete)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Problem calculating cost");
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

            return complete;
        }

        // Meters to Miles
        if(isImperial)
        {
            distanceMeters = (float) (tempDistance * 1609.34);
        }
        // Meters to km
        else
        {
            distanceMeters = tempDistance * 1000;
        }

        // Calc total cost
        totalCost = (tempDistance / tempEfficiency) * tempFuelCost;
        // Display total cost
        tvTotalCost.setText(String.valueOf(df.format(totalCost)));
        // Log info
        fuelEfficiency = tempEfficiency;
        fuelCost = tempFuelCost;

        Log.d(strLog, "date = " + dateFormatter.format(dateOfTrip) +
                " tempDistance = " + tempDistance +
                " total cost = " + totalCost +
                " distance meters = " + distanceMeters);

        // Needs return
        return complete;
    }
    // Save trip
    private void saveTrip()
    {
        // Check cost is valid
        if(calculateCost())
        {
            Log.d(strLog, "Trying to save");
            VehicleInformationInterface vehicleInformationInterface = new VehicleInformationInterface(this);
            VehicleInformationEntity listVeh = vehicleInformationInterface.getVehicle(vehicleId);
            // Update efficiency and fuel cost
            listVeh.setFuelEfficiency(fuelEfficiency);
            listVeh.setFuelCost(fuelCost);
            vehicleInformationInterface.update(listVeh);

            // Save Trip Information
            long id = -1;
            // Add to Database
            VehicleTripsInterface vehicleTripsInterface = new VehicleTripsInterface(this);
            try
            {
                id = vehicleTripsInterface.insert(new VehicleTripsEntity(vehicleId, dateOfTrip, distanceMeters, fuelCost, fuelEfficiency, totalCost));
            }
            catch (Exception e)
            {
                Log.e(strLog, Throwables.getStackTraceAsString(e));
            }
            // Error adding
            if (id == -1)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Error saving trip");
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
                onOptionsItemSelected(null);
            }
        }
    }

    // Previous trips
    private void loadPreviousTrips()
    {
        // Clear previous trips
        llPreviousTripsContent.removeAllViews();
        llPreviousTripsContent.invalidate();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if(sp.getString("unit_key", "imperial").equals("imperial"))
        {
            isImperial = true;
        }
        else
        {
            isImperial = false;
        }

        // Get previous trips
        VehicleTripsInterface vehicleTripsInterface = new VehicleTripsInterface(this);
        List<VehicleTripsEntity> vehicleTripsEntityList = vehicleTripsInterface.getAllTrips(vehicleId);
        Log.d(strLog, "Getting Previous Trips");
        for (VehicleTripsEntity vehicleTripsEntity : vehicleTripsEntityList)
        {
            addPreviousTrip(vehicleTripsEntity.getUid(),
                    vehicleTripsEntity.getDate(),
                    vehicleTripsEntity.getDistanceMeters(),
                    vehicleTripsEntity.getFuelCost(),
                    vehicleTripsEntity.getFuelEfficiency(),
                    vehicleTripsEntity.getTotalCost());
        }
    }
    private void addPreviousTrip(Long uid, Date date, float distanceMeters, float fuelCost, float fuelEfficiency, float totalCost)
    {
        // Date
        TextView tvDate = new TextView(this);
        tvDate.setText(dateFormatter.format(date));
        tvDate.setGravity(Gravity.CENTER_HORIZONTAL);
        tvDate.setTextAppearance(this, textStyle);
        // Distance
        TextView tvDistance = new TextView(this);
        if(isImperial)
        { tvDistance.setText((float)distanceMeters/1609.34 + " Mile(s)");}
        else
        { tvDistance.setText((float)distanceMeters/1000 + " KM(s)");}
        tvDistance.setGravity(Gravity.CENTER_HORIZONTAL);
        tvDistance.setTextAppearance(this, textStyle);
        // Fuel efficiency
        TextView tvFuelEfficiency = new TextView(this);
        if (isImperial)
        {
            if (isElectric)
            { tvFuelEfficiency.setText(fuelEfficiency + " KWH");}
            else
            { tvFuelEfficiency.setText(fuelEfficiency + " MPG");}
        }
        else
        {
            if (isElectric)
            { tvFuelEfficiency.setText(fuelEfficiency + " KWH");}
            else
            { tvFuelEfficiency.setText(fuelEfficiency + " KML");}
        }
        tvFuelEfficiency.setGravity(Gravity.CENTER_HORIZONTAL);
        tvFuelEfficiency.setTextAppearance(this, textStyle);
        // Fuel cost
        TextView tvFuelCost = new TextView(this);
        if(isImperial)
        {
            if(isElectric)
            { tvFuelCost.setText(fuelCost + " Per KW");}
            else
            { tvFuelCost.setText(fuelCost + " Per Gallon");}
        }
        else
        {
            if(isElectric)
            { tvFuelCost.setText(fuelCost + " Per KW");}
            else
            { tvFuelCost.setText(fuelCost + " Per Liter");}
        }
        tvFuelCost.setGravity(Gravity.CENTER_HORIZONTAL);
        tvFuelCost.setTextAppearance(this, textStyle);
        // Total Cost
        TextView tvTotalCost = new TextView(this);
        tvTotalCost.setText(String.valueOf(totalCost));
        tvTotalCost.setGravity(Gravity.CENTER_HORIZONTAL);
        tvTotalCost.setTextAppearance(this, textStyle);
        // Delete Button
        Button delButton = new MaterialButton(this);
        delButton.setText(getResources().getText(R.string.delete));
        delButton.setOnClickListener(new View.OnClickListener()
        { @Override
            public void onClick(View v)
            { deleteTrip(uid); }
        });

        // Setup Table
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        float mar = getResources().getDimension(R.dimen.def_margin);
        params.setMargins((int) mar, (int) mar/2, (int) mar, (int) mar/2);
        params.weight = 1;

        // New Table
        TableLayout newTable = new TableLayout(this);
        // Date
        TableRow newRow = new TableRow(this);
        newRow.addView(tvDate, params);
        newTable.addView(newRow);
        // Distance
        newRow = new TableRow(this);
        newRow.addView(tvDistance, params);
        newTable.addView(newRow);
        // Fuel Cost
        newRow = new TableRow(this);
        newRow.addView(tvFuelCost, params);
        newTable.addView(newRow);
        // Fuel efficiency
        newRow = new TableRow(this);
        newRow.addView(tvFuelEfficiency, params);
        newTable.addView(newRow);
        // Total Cost
        newRow = new TableRow(this);
        newRow.addView(tvTotalCost, params);
        newTable.addView(newRow);
        // Delete Button
        newRow = new TableRow(this);
        newRow.addView(delButton, params);
        newTable.addView(newRow);

        llPreviousTripsContent.addView(newTable);
    }
    private void deleteTrip(Long uid)
    {
        VehicleTripsInterface vehicleTripsInterface = new VehicleTripsInterface(this);
        vehicleTripsInterface.delete(uid);
        loadPreviousTrips();
    }
}
