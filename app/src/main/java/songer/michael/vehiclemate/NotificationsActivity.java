package songer.michael.vehiclemate;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.material.button.MaterialButton;
import com.google.common.base.Throwables;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import songer.michael.vehiclemate.database.entity.NotificationEntity;
import songer.michael.vehiclemate.database.interfaces.NotificationInterface;

// TODO: Reorganise code (delete function)
public class NotificationsActivity extends AppCompatActivity
{
    // For android 26+
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;

    LinearLayout llNotifications;
    LinearLayout llNotificationInfo;
    LinearLayout llNew;

    String strLog = "NA";
    private String vehicleId;

    DatePickerDialog dPD;
    TimePickerDialog tPD;
    SimpleDateFormat sdf;

    Integer cMin;
    Integer cHour;
    Integer cDay;
    Integer cMonth;
    Integer cYear;

    EditText editTextDate;
    EditText editTextTime;

    int defHintColour;
    int defTextColour;
    int errorTextColour;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Intent intent = getIntent();
        vehicleId = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        llNotifications = findViewById(R.id.linear_layout_notifications);
        llNotificationInfo = findViewById(R.id.linear_layout_notifications_info);
        llNew = findViewById(R.id.linear_layout_new_notification);

        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.UK);
        errorTextColour = getResources().getColor(R.color.error);

        Button btnNew = findViewById(R.id.button_new_notification);
        btnNew.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                llNotifications.setVisibility(View.GONE);
                llNew.setVisibility(View.VISIBLE);
            }
        });

        setupAddNotification();
        loadNotifications();
    }
    // Setup Add notification
    public void setupAddNotification()
    {
        // Add new notification
        editTextDate = findViewById(R.id.edit_text_date);
        // Get default colours
        defTextColour = editTextDate.getCurrentTextColor();
        defHintColour = editTextDate.getCurrentHintTextColor();
        editTextDate.setInputType(InputType.TYPE_NULL);
        editTextDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                closeKeyboard(v);
                selectDate(editTextDate, null);
            }
        });
        editTextDate.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                closeKeyboard(v);
            }
        });
        editTextTime = findViewById(R.id.edit_text_time);
        editTextTime.setInputType(InputType.TYPE_NULL);
        editTextTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                closeKeyboard(v);
                selectTime(editTextTime, null);
            }
        });
        editTextTime.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                closeKeyboard(v);
            }
        });

        Button submit = findViewById(R.id.button_submit);
        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addNotification();
            }
        });
    }
    private void makeRed(EditText et)
    {
        et.setHintTextColor(errorTextColour);
        et.setTextColor(errorTextColour);
    }
    private void makeDefault(EditText et)
    {
        et.setHintTextColor(defHintColour);
        et.setTextColor(defTextColour);
    }
    // Add Notification
    public void addNotification()
    {
        EditText editTextTitle = findViewById(R.id.edit_text_label);

        boolean complete = true;
        // Check input
        if(editTextTitle.getText().toString().trim().isEmpty())
        {
            makeRed(editTextTitle);
            complete = false;
        }
        else
        {
            makeDefault(editTextTitle);
        }
        if (editTextDate.getText().toString().trim().isEmpty())
        {
            makeRed(editTextDate);
            complete = false;
        }
        else
        {
            makeDefault(editTextDate);
        }
        if(editTextTime.getText().toString().trim().isEmpty())
        {
            makeRed(editTextTime);
            complete = false;
        }
        else
        {
            makeDefault(editTextTime);
        }
        // Check date/time
        if(cMin==null||cHour==null||cDay==null||cMonth==null||cYear==null)
        {
            makeRed(editTextDate);
            makeRed(editTextTime);
            return;
        }
        else
        {
            makeDefault(editTextDate);
            makeDefault(editTextTime);
        }
        if (!complete)
            return;

        // Set delay
        final Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, cMin);
        calendar.set(Calendar.HOUR_OF_DAY, cHour);
        calendar.set(Calendar.DAY_OF_MONTH, cDay);
        calendar.set(Calendar.MONTH, cMonth);
        calendar.set(Calendar.YEAR, cYear);

        Date notificationDate = calendar.getTime();

        Log.d(strLog, sdf.format(notificationDate) + " " + notificationDate.getTime());
        long delay = notificationDate.getTime();

        // Check delay
        if (delay < 0)
        {
            makeRed(editTextDate);
            makeRed(editTextTime);
            return;
        }
        NotificationInterface notificationInterface = new NotificationInterface(this);
        long notUid = -1;
        // Add to DB
        try
        {
            notUid = notificationInterface.insert(new NotificationEntity(Long.parseLong(vehicleId), editTextTitle.getText().toString(), notificationDate));
        }
        catch (Exception e)
        {
            Log.e(strLog, Throwables.getStackTraceAsString(e));
        }
        // If could not add
        if(notUid == -1)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Could not add notification");
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
        else
        {
            scheduleNotification(buildNotification(editTextTitle.getText().toString()), delay, (int)notUid);
            editTextTitle.setText("");
            editTextDate.setText("");
            editTextTime.setText("");
            onOptionsItemSelected(null);
        }
    }
    // Back Button
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Return
        if (llNew.getVisibility() == View.VISIBLE)
        {
            llNew.setVisibility(View.GONE);
            loadNotifications();
            llNotifications.setVisibility(View.VISIBLE);
        }
        else
        {
            setResult(1, new Intent().putExtra(MainActivity.EXTRA_MESSAGE, vehicleId));
            finish();
        }
        return true;
    }

    private void loadNotifications()
    {
        // Reload notifications
        llNotificationInfo.removeAllViews();
        llNotificationInfo.invalidate();
        // Get Notifications
        NotificationInterface notificationInterface = new NotificationInterface(this);
        try
        {
            List<NotificationEntity> notificationEntities = notificationInterface.getAllNotifications(Long.parseLong(vehicleId));
            for(NotificationEntity not: notificationEntities)
            {
                Date now = new Date();
                if(not.getDate().getTime() < now.getTime())
                {
                    notificationInterface.delete(not.getUid());
                }
                else
                {
                    addToGUI(not.getUid(), not.getLabel(), not.getDate());
                }
            }
        }
        catch (Exception e)
        {
            Log.e(strLog, Throwables.getStackTraceAsString(e));
        }

    }
    private void deleteNotification(long id)
    {
        // Delete from alarm
        Intent intent = new Intent(this, NotificationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), (int)id, intent, 0);
        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();

        // Delete from GUI
        NotificationInterface notificationInterface = new NotificationInterface(this);
        notificationInterface.delete(id);
        loadNotifications();
    }
    // TODO: update notification
    private void updateNotification(long id, String label, Date date)
    {
        NotificationInterface notificationInterface = new NotificationInterface(this);
        notificationInterface.update(new NotificationEntity(id, date));
    }
    private void addToGUI(long id, String name, Date date)
    {
        final Calendar calendar = Calendar.getInstance();
        Log.d(strLog, "Date gotten = " + sdf.format(date));
        calendar.setTime(date);
        // Add Title
        TextView title = new TextView(this);
        title.setText(name);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        // Date
        EditText etDate = new EditText(this);
        etDate.setText(
                String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)) + "/" +
                String.format("%02d", calendar.get(Calendar.MONTH) + 1) + "/" +
                calendar.get(Calendar.YEAR));
        etDate.setInputType(InputType.TYPE_NULL);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard(v);
                selectDate(etDate, calendar);
            }
        });
        etDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                closeKeyboard(v);
            }
        });
        // Time
        EditText etTime = new EditText(this);
        etTime.setText(
                String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                        String.format("%02d", calendar.get(Calendar.MINUTE)));
        etTime.setInputType(InputType.TYPE_NULL);
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard(v);
                selectTime(etTime, calendar);
            }
        });
        etTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                closeKeyboard(v);
            }
        });
        // Update
        /*
        Button update = new MaterialButton(this);
        update.setText("Update");
        update.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO: ADD UPDATE
                Date newDate = new Date();
                updateNotification(id, newDate);
            }
        });
         */
        // Delete button
        Button del = new MaterialButton(this);
        del.setText("Delete");
        del.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                deleteNotification(id);
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
        newRow.addView(title, params);
        newTable.addView(newRow);
        // Second Row
        newRow = new TableRow(this);
        newRow.addView(etDate,params);
        newRow.addView(etTime,params);
        //newRow.addView(update, params);
        newTable.addView(newRow);
        // Third Row
        newRow = new TableRow(this);
        newRow.addView(del, params);
        newTable.addView(newRow);

        llNotificationInfo.addView(newTable);
    }

    private void closeKeyboard(View view)
    {
        InputMethodManager iMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        iMM.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void selectDate(EditText et, Calendar calendar)
    {
        if(calendar == null)
        {
            calendar = Calendar.getInstance();
        }
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        // date picker dialog
        dPD = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        cDay = dayOfMonth;
                        cMonth = monthOfYear;
                        cYear = year;
                        et.setText(String.format("%02d", dayOfMonth) + "/" + String.format("%02d",(monthOfYear + 1)) + "/" + year);
                    }
                }, year, month, day);
        dPD.show();
    }

    private void selectTime(EditText et, Calendar calendar)
    {
        if(calendar == null)
        {
            calendar = Calendar.getInstance();
        }
        int min = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        // date picker dialog
        tPD = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
            {
                cHour = hourOfDay;
                cMin = minute;
                et.setText(String.format("%02d",hourOfDay) + ":" + String.format("%02d",minute));
            }
        }, hour, min, true);
        tPD.show();
    }

    /*
     *  NOTIFICATIONS
     */
    private void scheduleNotification (Notification notification, long delay, int id)
    {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, delay, pendingIntent);

        Log.d(strLog, "Scheduled notification for " + sdf.format(delay));
    }
    private Notification buildNotification (String content)
    {
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Default");
        builder.setContentIntent(resultPendingIntent);
        builder.setContentTitle("Scheduled Reminder");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setAutoCancel(true);
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);

        return builder.build();
    }
}
