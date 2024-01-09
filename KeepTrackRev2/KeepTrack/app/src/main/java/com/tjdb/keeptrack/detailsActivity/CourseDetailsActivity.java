package com.tjdb.keeptrack.detailsActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tjdb.keeptrack.AssessmentListActivity;
import com.tjdb.keeptrack.utilities.DatabaseHelper;
import com.tjdb.keeptrack.NoteListActivity;
import com.tjdb.keeptrack.R;
import com.tjdb.keeptrack.model.Course;
import com.tjdb.keeptrack.model.Note;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CourseDetailsActivity extends AppCompatActivity {

    private static final String TAG = "TermDetailsActivity";

    long courseId;
    private Course course;
    private DatabaseHelper databaseHelper;
    private TextView title;

    private ImageView editView;

    private TextView titleTextView;
    private TextView startDateTextView;
    private TextView endDateTextView;
    private TextView statusTextView;
    private TextView instructorNameText;
    private TextView instructorPhoneText;
    private TextView instructorEmailText;
    private ImageView shareView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        databaseHelper = new DatabaseHelper(this);

        courseId = getIntent().getExtras().getLong("courseId");
        course = databaseHelper.getCourseById(courseId);

        //set custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(R.id.app_title);
        title.setText("Course Details");

        titleTextView = findViewById(R.id.title);
        startDateTextView = findViewById(R.id.startDate);
        endDateTextView = findViewById(R.id.endDate);
        statusTextView = findViewById(R.id.status);
        instructorNameText = findViewById(R.id.instructorName);
        instructorPhoneText = findViewById(R.id.instructorPhone);
        instructorEmailText = findViewById(R.id.instructorEmail);

        shareView = findViewById(R.id.note_share);

        updateDetails();

        CardView assessmentCard = findViewById(R.id.go_to_assessment);
        assessmentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AssessmentListActivity.class);
                i.putExtra("courseId", course.getId());
                startActivity(i);
            }
        });

        CardView noteCard = findViewById(R.id.go_to_note);
        noteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), NoteListActivity.class);
                i.putExtra("courseId", course.getId());
                startActivity(i);
            }
        });

        shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Note> notes=databaseHelper.getAllNotes(courseId);
                StringBuffer body=new StringBuffer("Notes: \n--------------------\n");

                for (Note note : notes) {
                    body.append(note.getText());
                    body.append("\n--------------------\n");
                }

                composeEmail("Notes of "+course.getTitle(), body.toString());
            }
        });

        editView = findViewById(R.id.edit);
        editView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
                Log.d(TAG, "onFabClick: Opened edit dialog");
            }
        });
    }

    private void updateDetails() {
        titleTextView.setText(course.getTitle());
        startDateTextView.setText(course.getStartDate());
        endDateTextView.setText(course.getEndDate());
        statusTextView.setText(course.getStatus());
        instructorNameText.setText(course.getInstructorName());
        instructorPhoneText.setText(course.getInstructorPhone());
        instructorEmailText.setText(course.getInstructorEmail());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private void showAddDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.course_dialog, null);
        dialogBuilder.setView(dialogView);


        final EditText editTitle = dialogView.findViewById(R.id.edit_title);
        final TextView startDateText = dialogView.findViewById(R.id.startDateInput);
        final TextView endDateText = dialogView.findViewById(R.id.endDateInput);
        final Spinner statusSpinner =  dialogView.findViewById(R.id.edit_status);
        final TextView instructorNameText = dialogView.findViewById(R.id.edit_instructorName);
        final TextView instructorPhoneText = dialogView.findViewById(R.id.edit_instructorPhone);
        final TextView instructorEmailText = dialogView.findViewById(R.id.edit_instructorEmail);


        //Set current date as default date
        final long date = System.currentTimeMillis();
        SimpleDateFormat dateSdf = new SimpleDateFormat("d MMMM YYYY");
        String dateString = dateSdf.format(date);

        editTitle.setText(course.getTitle());
        startDateText.setText(course.getStartDate());
        endDateText.setText(course.getEndDate());
        statusSpinner.setSelection(((ArrayAdapter<String>)statusSpinner.getAdapter()).getPosition(course.getStatus()));
        instructorNameText.setText(course.getInstructorName());
        instructorPhoneText.setText(course.getInstructorPhone());
        instructorEmailText.setText(course.getInstructorEmail());

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        startDateText.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(CourseDetailsActivity.this,
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
                final DatePickerDialog datePickerDialog = new DatePickerDialog(CourseDetailsActivity.this,
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

        dialogBuilder.setTitle("Update Course");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String title = editTitle.getText().toString();
                String startDate = startDateText.getText().toString();
                String endDate = endDateText.getText().toString();
                String status = statusSpinner.getSelectedItem().toString();
                String instructorName = instructorNameText.getText().toString();
                String instructorPhone = instructorPhoneText.getText().toString();
                String instructorEmail = instructorEmailText.getText().toString();
                if (title.length() != 0) {
                    try {
                        databaseHelper.updateCourse(courseId, title, startDate, endDate, status, instructorName, instructorPhone, instructorEmail, course.getTermId());
                        //scheduleNotification(getNotification(title), cal.getTimeInMillis());
                        course = databaseHelper.getCourseById(courseId);
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

    public void composeEmail(String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(intent);

    }
}