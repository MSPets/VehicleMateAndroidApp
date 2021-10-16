package songer.michael.vehiclemate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.common.base.Throwables;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import songer.michael.vehiclemate.database.DBController;
import songer.michael.vehiclemate.database.entity.VehicleInformationEntity;
import songer.michael.vehiclemate.database.interfaces.VehicleInformationInterface;

public class NewVehicleActivity extends AppCompatActivity
{
    private final String strLog = "NVA";
    int defHintColour;
    int defTextColour;
    int errorTextColour;

    private static final int PICK_IMAGE = 1;
    Uri imageUri;
    private File sourceFile;
    private File destFile;
    private File tempFile;
    private SimpleDateFormat dateFormatter;

    private Bitmap imgBitmap;

    ImageView iVVehicleImage;
    Button btnVehicleImage;

    EditText eTVehicleType;
    EditText eTVehicleModel;
    EditText eTVehicleName;

    /*
    DatePickerDialog dPD;
    Date insDate;
    EditText eTInsDate;
    EditText eTInsCost;
    Date motDate;
    EditText eTMotDate;
    EditText eTMotCost;
     */
    EditText eTVTB;
    boolean isElectric = false;
    Button btnSubmit;

    /*
     * Type
     * Model
     * Name
     * Insurance Date
     * Insurance Cost
     * MOT Date
     * MOT Cost
     * Tank size
     */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vehicle);

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        errorTextColour = getResources().getColor(R.color.error);
        
        tempFile = new File(Environment.getExternalStorageDirectory() + "/" + "TESTDIR");
        if (!tempFile.exists()) {
            boolean s = tempFile.mkdirs();
            Log.d(strLog,"Making dir output " + s);
        }

        // Vehicle Image
        iVVehicleImage = (ImageView) findViewById(R.id.image_view_vehicle_image);
        btnVehicleImage = (Button) findViewById(R.id.button_vehicle_image);
        btnVehicleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });
        // Vehicle Type
        eTVehicleType = (EditText) findViewById(R.id.edit_text_vehicle_type);

        defHintColour = eTVehicleType.getCurrentHintTextColor();
        defTextColour = eTVehicleType.getCurrentTextColor();
        eTVehicleType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    closeKeyboard(view);
                }
            }
        });
        // Vehicle Model
        eTVehicleModel = (EditText) findViewById(R.id.edit_text_vehicle_model);
        eTVehicleModel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    closeKeyboard(view);
                }
            }
        });
        // Vehicle Name
        eTVehicleName = (EditText) findViewById(R.id.edit_text_vehicle_name);
        eTVehicleName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    closeKeyboard(view);
                }
            }
        });
        /*
        // Insurance Date
        eTInsDate = (EditText) findViewById(R.id.edit_text_vehicle_ins_date);
        eTInsDate.setInputType(InputType.TYPE_NULL);
        eTInsDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard(view);
                selectDate(eTInsDate);
            }
        });
        // Insurance Cost
        eTInsCost = (EditText) findViewById(R.id.edit_text_vehicle_ins_cost);
        eTInsCost.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    closeKeyboard(view);
                }
            }
        });
        // MOT Date
        eTMotDate = (EditText) findViewById(R.id.edit_text_vehicle_mot_date);
        eTMotDate.setInputType(InputType.TYPE_NULL);
        eTMotDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard(view);
                selectDate(eTMotDate);
            }
        });
        // MOT Cost
        eTMotCost = (EditText) findViewById(R.id.edit_text_vehicle_mot_cost);
        eTMotCost.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    closeKeyboard(view);
                }
            }
        });
        */
        // Tank/battery Size
        eTVTB = (EditText) findViewById(R.id.edit_text_vehicle_tank_Battery);
        eTVTB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    closeKeyboard(view);
                }
            }
        });
        // Submit Button
        btnSubmit = (Button) findViewById(R.id.button_submit_vehicle);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                submitVehicle();
            }
        });
    }
    public void radioButtonClicked(View view)
    {
        boolean ticked = ((RadioButton) view).isChecked();
        switch (view.getId()){
            case R.id.radio_button_electric:
                if (ticked)
                    isElectric = true;
                    break;
            case R.id.radio_button_fossil:
                if(ticked)
                    isElectric = false;
                    break;
        }
    }

    private void closeKeyboard(View view)
    {
        InputMethodManager iMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        iMM.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
/*
    private void selectDate(EditText et)
    {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        dPD = new DatePickerDialog(NewVehicleActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        et.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
        dPD.show();
    }
 */
    // https://www.mindbowser.com/image-compression-in-android/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();

            try
            {
                imgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            }
            catch (Exception e)
            {
                Log.e(strLog, Throwables.getStackTraceAsString(e));
            }

            iVVehicleImage.setImageURI(imageUri);
            iVVehicleImage.setVisibility(View.VISIBLE);
        }
    }
    //
    private void submitVehicle()
    {
        boolean complete = true;
        // Picture
        if(iVVehicleImage.getDrawable() == null)
        {
            btnVehicleImage.setTextColor(errorTextColour);
            btnVehicleImage.setHintTextColor(errorTextColour);
            complete = false;
        }
        else
        {
            btnVehicleImage.setTextColor(defTextColour);
            btnVehicleImage.setHintTextColor(defHintColour);
        }
        // Vehicle Type
        if(eTVehicleType.getText().toString().trim().isEmpty())
        {
            eTVehicleType.setTextColor(errorTextColour);
            eTVehicleType.setHintTextColor(errorTextColour);
            complete = false;
        }
        else
        {
            eTVehicleType.setTextColor(defTextColour);
            eTVehicleType.setHintTextColor(defHintColour);
        }
        // Vehicle Model
        if(eTVehicleModel.getText().toString().trim().isEmpty())
        {
            eTVehicleModel.setTextColor(errorTextColour);
            eTVehicleModel.setHintTextColor(errorTextColour);
            complete = false;
        }
        else
        {
            eTVehicleModel.setTextColor(defTextColour);
            eTVehicleModel.setHintTextColor(defHintColour);
        }
        // Vehicle Name
        if(eTVehicleName.getText().toString().trim().isEmpty())
        {
            eTVehicleName.setTextColor(errorTextColour);
            eTVehicleName.setHintTextColor(errorTextColour);
            complete = false;
        }
        else
        {
            eTVehicleName.setTextColor(defTextColour);
            eTVehicleName.setHintTextColor(defHintColour);
        }
        /*
        // Insurance date
        try
        {
            insDate = dateFormatter.parse(eTInsDate.getText().toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            complete = false;
            eTInsDate.setText("");
        }
        if(eTInsDate.getText().toString().trim().isEmpty())
        {
            eTInsDate.setTextColor(errorTextColour);
            eTInsDate.setHintTextColor(errorTextColour);
            complete = false;
        }
        else
        {
            eTInsDate.setTextColor(defTextColour);
            eTInsDate.setHintTextColor(defHintColour);
        }
        // Insurance cost
        if(eTInsCost.getText().toString().trim().isEmpty())
        {
            eTInsCost.setTextColor(errorTextColour);
            eTInsCost.setHintTextColor(errorTextColour);
            complete = false;
        }
        else
        {
            eTInsCost.setTextColor(defTextColour);
            eTInsCost.setHintTextColor(defHintColour);
        }
        // MOT date
        try
        {
            motDate = dateFormatter.parse(eTMotDate.getText().toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            complete = false;
            eTMotDate.setText("");
        }
        if(eTMotDate.getText().toString().trim().isEmpty())
        {
            eTMotDate.setTextColor(errorTextColour);
            eTMotDate.setHintTextColor(errorTextColour);
            complete = false;
        }
        else
        {
            eTMotDate.setTextColor(defTextColour);
            eTMotDate.setHintTextColor(defHintColour);
        }
        // MOT cost
        if(eTMotCost.getText().toString().trim().isEmpty())
        {
            eTMotCost.setTextColor(errorTextColour);
            eTMotCost.setHintTextColor(errorTextColour);
            complete = false;
        }
        else
        {
            eTMotCost.setTextColor(defTextColour);
            eTMotCost.setHintTextColor(defHintColour);
        }
        */
        // Vehicle tank/battery size
        if(eTVTB.getText().toString().trim().isEmpty())
        {
            eTVTB.setTextColor(errorTextColour);
            eTVTB.setHintTextColor(errorTextColour);
            complete = false;
        }
        else
        {
            eTVTB.setTextColor(defTextColour);
            eTVTB.setHintTextColor(defHintColour);
        }
        if (!complete)
        {
            AlertDialog.Builder alertDialogBuilderNeedInfo = new AlertDialog.Builder(this);
            alertDialogBuilderNeedInfo.setMessage("Please fill all fields");
            alertDialogBuilderNeedInfo.setPositiveButton("Okay",
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            return;
                        }
                    });
            AlertDialog alertDialogNeedInfo = alertDialogBuilderNeedInfo.create();
            alertDialogNeedInfo.show();
            TextView messageNeedInfo = alertDialogNeedInfo.findViewById(android.R.id.message);
            messageNeedInfo.setGravity(Gravity.CENTER);
        }
        else
        {
            // Show try
            AlertDialog.Builder alertDialogBuilderTrying = new AlertDialog.Builder(this);
            alertDialogBuilderTrying.setMessage("Trying to add vehicle\nWill return when done");
            alertDialogBuilderTrying.setPositiveButton("Okay",
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            return;
                        }
                    });
            AlertDialog alertDialogTrying = alertDialogBuilderTrying.create();
            alertDialogTrying.show();
            TextView messageTrying = alertDialogTrying.findViewById(android.R.id.message);
            messageTrying.setGravity(Gravity.CENTER);


            VehicleInformationInterface vehicleInformationInterface = new VehicleInformationInterface(this);
            // Mot
            long vehUid = -1;
            try
            {
                vehUid = vehicleInformationInterface.insert(new VehicleInformationEntity(eTVehicleType.getText().toString(), eTVehicleModel.getText().toString(), eTVehicleName.getText().toString(), isElectric));
            }
            catch (Exception e)
            {
                Log.e(strLog, Throwables.getStackTraceAsString(e));
            }
            if (vehUid == -1)
            {
                AlertDialog.Builder alertDialogBuilderError = new AlertDialog.Builder(this);
                alertDialogBuilderError.setMessage("Could not add vehicle");

                alertDialogBuilderError.setPositiveButton("Okay",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                return;
                            }
                        });

                AlertDialog alertDialogError = alertDialogBuilderError.create();
                alertDialogError.show();
                TextView messageError = alertDialogError.findViewById(android.R.id.message);
                messageError.setGravity(Gravity.CENTER);
            }
            else
            {
                Log.d(strLog, "ADD TO DB " + vehUid);
                // TODO: Make foolproof

                btnSubmit.setClickable(false);
                new NewVehicleActivity.addImageAsyncTask(this, vehUid, strLog, imgBitmap).execute();
            }
        }
    }
    private static class addImageAsyncTask extends android.os.AsyncTask<Void, Void, Void>
    {
        private Activity activity;
        private long vehUid;
        private String strLog;
        private Bitmap imgBitmap;

        addImageAsyncTask(Activity activity, long vehUid, String strLog, Bitmap imgBitmap)
        {
            this.activity = activity;
            this.vehUid = vehUid;
            this.strLog = strLog;
            this.imgBitmap = imgBitmap;
        }
        @Override
        protected Void doInBackground(Void... voids)
        {
            ContextWrapper cw = new ContextWrapper(this.activity.getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

            // Create imageDir
            File mypath = new File(directory,vehUid + ".png");

            Log.d(strLog, "Dir = " + directory + " path = " + mypath);

            FileOutputStream fos = null;
            try
            {
                fos = new FileOutputStream(mypath);
                // Use the compress method on the BitMap object to write image to the OutputStream
                imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }
            catch (Exception e)
            {
                Log.e(strLog, Throwables.getStackTraceAsString(e));
            }
            finally
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {
                    Log.e(strLog, Throwables.getStackTraceAsString(e));
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused)
        {
            super.onPostExecute(unused);
            this.activity.finish();
        }
    }
}
