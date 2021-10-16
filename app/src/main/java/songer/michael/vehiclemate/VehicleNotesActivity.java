package songer.michael.vehiclemate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.common.base.Throwables;

import java.util.List;

import songer.michael.vehiclemate.database.entity.VehicleNotesEntity;
import songer.michael.vehiclemate.database.interfaces.VehicleNotesInterface;

public class VehicleNotesActivity extends AppCompatActivity
{
    private String strLog = "VNA";
    private String vehicleId;

    LinearLayout llNotes;
    LinearLayout llCurNotes;
    LinearLayout llNewNote;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_notes);

        Intent intent = getIntent();
        vehicleId = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        llNotes = findViewById(R.id.linear_layout_notes);
        llCurNotes = findViewById(R.id.linear_layout_note_info);
        llNewNote = findViewById(R.id.linear_layout_new_note);

        Button newNote = findViewById(R.id.button_new_note);
        newNote.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                llNotes.setVisibility(View.GONE);
                llNewNote.setVisibility(View.VISIBLE);
            }
        });

        Button submit = findViewById(R.id.button_note_submit);
        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                newNote();
            }
        });

        loadNotes();
    }
    // Back Button
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Return
        if (llNewNote.getVisibility() == View.VISIBLE)
        {
            llNewNote.setVisibility(View.GONE);
            loadNotes();
            llNotes.setVisibility(View.VISIBLE);
        }
        else
        {
            setResult(1, new Intent().putExtra(MainActivity.EXTRA_MESSAGE, vehicleId));
            finish();
        }
        return true;
    }
    private void newNote()
    {
        EditText etNote = findViewById(R.id.edit_text_note_info);
        if (etNote.getText().toString().trim().isEmpty())
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Note cannot be empty");
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
            long id = -1;
            // Add to Database
            VehicleNotesInterface vehicleNotesInterface = new VehicleNotesInterface(this);
            try
            {
                id = vehicleNotesInterface.insert(new VehicleNotesEntity(Long.parseLong(vehicleId), etNote.getText().toString()));
            }
            catch (Exception e)
            {
                Log.e(strLog, Throwables.getStackTraceAsString(e));
            }
            // Error adding
            if (id == -1)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Error adding note");
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
                etNote.setText("");
                onOptionsItemSelected(null);
            }
        }
    }
    private void loadNotes()
    {
        // Reload notes
        llCurNotes.removeAllViews();
        llCurNotes.invalidate();
        // Get Notes
        try
        {
            VehicleNotesInterface vehicleNotesInterface = new VehicleNotesInterface(this);
            List<VehicleNotesEntity> vehicleNotesEntities = vehicleNotesInterface.getAllNotes(Long.parseLong(vehicleId));
            Log.d(strLog, "Got notes for " + vehicleId);

            for(VehicleNotesEntity vehicleNotesEntity: vehicleNotesEntities)
            {
                addToGUI(vehicleNotesEntity.getUid(), vehicleNotesEntity.getNote());
            }
        }
        catch (Exception e)
        {
            Log.e(strLog, Throwables.getStackTraceAsString(e));
        }
        Log.d(strLog, "Done Loading Notes");
    }
    private void deleteNote(long id)
    {
        VehicleNotesInterface notesInterface = new VehicleNotesInterface(this);
        notesInterface.delete(id);
        loadNotes();
    }
    private void updateNote(Long id, String text)
    {
        VehicleNotesInterface notesInterface = new VehicleNotesInterface(this);
        VehicleNotesEntity vehicleNotesEntity = notesInterface.getNote(id);
        vehicleNotesEntity.setNote(text);
        notesInterface.update(vehicleNotesEntity);
        loadNotes();
    }
    private void addToGUI(Long id, String note)
    {
        EditText etNote = new EditText(this);
        etNote.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                InputType.TYPE_CLASS_TEXT);
        etNote.setText(note);
        Button updateBtn = new MaterialButton(this);
        updateBtn.setText(getResources().getText(R.string.update));
        updateBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Update Note
                updateNote(id, etNote.getText().toString());
            }
        });
        Button delBtn = new MaterialButton(this);
        delBtn.setText(getResources().getText(R.string.delete));
        delBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Delete note
                deleteNote(id);
            }
        });
        // Setup Table
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        float mar = getResources().getDimension(R.dimen.def_margin);
        params.setMargins((int) mar, (int) mar/2, (int) mar, (int) mar/2);
        params.weight = 1;
        TableRow.LayoutParams params2 = params;
        params2.width = TableRow.LayoutParams.WRAP_CONTENT;
        params2.height = TableRow.LayoutParams.WRAP_CONTENT;
        // New Table
        TableLayout newTable = new TableLayout(this);
        // First Row
        TableRow newRow = new TableRow(this);
        newRow.addView(etNote, params);
        newTable.addView(newRow);
        // Second Row
        newRow = new TableRow(this);
        newRow.addView(updateBtn, params2);
        newRow.addView(delBtn, params2);
        newTable.addView(newRow);

        llCurNotes.addView(newTable);
    }
}