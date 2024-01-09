package com.tjdb.keeptrack.detailsActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tjdb.keeptrack.CourseListActivity;
import com.tjdb.keeptrack.utilities.DatabaseHelper;
import com.tjdb.keeptrack.R;
import com.tjdb.keeptrack.model.Term;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TermDetailsActivity extends AppCompatActivity {

    private static final String TAG = "TermDetailsActivity";

    long termId;
    private Term term;
    private DatabaseHelper databaseHelper;
    private TextView title;

    private ImageView editView;

    private TextView titleTextView;
    private TextView startDateTextView;
    private TextView endDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_details);

        databaseHelper = new DatabaseHelper(this);

        termId=getIntent().getExtras().getLong("termId");
        term=databaseHelper.getTermById(termId);

        //set custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(R.id.app_title);
        title.setText("Term Details");

        titleTextView = findViewById(R.id.title);
        startDateTextView = findViewById(R.id.startDate);
        endDateTextView = findViewById(R.id.endDate);

        updateDetails();

        CardView courseCard=findViewById(R.id.go_to_courses);
        courseCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CourseListActivity.class);
                i.putExtra("termId", term.getId());
                startActivity(i);
            }
        });

        editView=findViewById(R.id.edit);
        editView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
                Log.d(TAG, "onFabClick: Opened edit dialog");
            }
        });
    }

    private void updateDetails() {
        titleTextView.setText(term.getTitle());
        startDateTextView.setText(term.getStartDate());
        endDateTextView.setText(term.getEndDate());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private void showAddDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.term_dialog, null);
        dialogBuilder.setView(dialogView);


        final EditText editTitle = dialogView.findViewById(R.id.edit_title);
        final TextView startDateText = dialogView.findViewById(R.id.startDateInput);
        final TextView endDateText = dialogView.findViewById(R.id.endDateInput);

        //Set current date as default date
        final long date = System.currentTimeMillis();
        SimpleDateFormat dateSdf = new SimpleDateFormat("d MMMM YYYY");
        String dateString = dateSdf.format(date);

        editTitle.setText(term.getTitle());
        startDateText.setText(term.getStartDate());
        endDateText.setText(term.getEndDate());

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        startDateText.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(TermDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String newMonth = getMonth(monthOfYear + 1);
                                startDateText.setText(dayOfMonth + " " + newMonth + " " + year);
                                cal.set(Calendar.YEAR, year);
                                cal.set(Calendar.MONTH, monthOfYear);
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                Log.d(TAG, "onDateSet: Date has been set successfully");
                            }
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMinDate(date);
            }
        });

        endDateText.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(TermDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String newMonth = getMonth(monthOfYear + 1);
                                endDateText.setText(dayOfMonth + " " + newMonth + " " + year);
                                cal.set(Calendar.YEAR, year);
                                cal.set(Calendar.MONTH, monthOfYear);
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                Log.d(TAG, "onDateSet: Date has been set successfully");
                            }
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMinDate(date);
            }
        });

        dialogBuilder.setTitle("Update Term");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String title = editTitle.getText().toString();
                String startDate = startDateText.getText().toString();
                String endDate = endDateText.getText().toString();
                if (title.length() != 0) {
                    try {
                        databaseHelper.updateTerm(term.getId(),title, startDate, endDate);
                        //scheduleNotification(getNotification(title), cal.getTimeInMillis());
                        term=databaseHelper.getTermById(termId);
                        updateDetails();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    toastMsg("Oops, Cannot set an empty ToDo!!!");
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                dialog.cancel();
            }
        });


        AlertDialog b = dialogBuilder.create();
        Animation animSlideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        animSlideUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Fires when animation starts
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // ...
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // ...
            }
        });
        dialogView.startAnimation(animSlideUp);
        b.show();
    }

    private void toastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }
}