package com.tjdb.keeptrack;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tjdb.keeptrack.adapter.CourseAdapter;
import com.tjdb.keeptrack.detailsActivity.CourseDetailsActivity;
import com.tjdb.keeptrack.model.Course;
import com.tjdb.keeptrack.model.Term;
import com.tjdb.keeptrack.utilities.DatabaseHelper;
import com.tjdb.keeptrack.utilities.MyNotificationPublisher;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CourseListActivity extends AppCompatActivity {

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    private static final String TAG = "CourseListActivity";

    private static final Long COURSE_NOTIFICATION = 0L;
    private DatabaseHelper databaseHelper;
    private ArrayList<Course> courses;
    private CourseAdapter courseAdapter;
    private ListView itemsListView;
    private FloatingActionButton fab;

    private TextView title;

    private Term term;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        databaseHelper = new DatabaseHelper(this);
        long termId=getIntent().getExtras().getLong("termId");
        term=databaseHelper.getTermById(termId);

        //set custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(R.id.app_title);
        title.setText("Term: "+term.getTitle());

        fab = findViewById(R.id.fab);
        itemsListView = findViewById(R.id.itemsList);

        //initialise and set empty listView
        TextView empty = findViewById(R.id.emptyTextView);
        empty.setText("No course in this term..");
        FrameLayout emptyView = findViewById(R.id.emptyView);
        itemsListView.setEmptyView(emptyView);

        populateListView();
        onFabClick();
        //hideFab();
    }

    private void scheduleNotification(Notification notification, String notifyDate, Long id, long extraDelay) throws ParseException {
        Intent notificationIntent = new Intent(this, MyNotificationPublisher.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            notificationIntent.setIdentifier(String.valueOf(COURSE_NOTIFICATION+id));
        }
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, (COURSE_NOTIFICATION+id));
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;

        SimpleDateFormat dateSdf = new SimpleDateFormat("d MMMM yyyy");
        Date date=dateSdf.parse(notifyDate);
        long delay=date.toInstant().toEpochMilli() + extraDelay;

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
        Log.d(TAG, "scheduleNotification: Notification set successfully!");
    }

    private Notification getNotification(String content, Course course) {
        Intent intent = new Intent(this, CourseDetailsActivity.class);
        intent.putExtra("courseId", course.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, default_notification_channel_id);
        builder.setContentTitle("KeepTrack Reminder");
        builder.setContentText(content);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.ic_stat_name);
        builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        return builder.build();
    }

    private void insertDataToDb(String title, String startDate, String endDate,String status, String instructorName,String instructorPhone,String instructorEmail, long termId) {
        long insertData = databaseHelper.insertCourse(title, startDate, endDate, status, instructorName, instructorPhone, instructorEmail, termId);
        if (insertData!=-1) {
            try {
                Course course=databaseHelper.getCourseById(insertData);
                scheduleNotification(getNotification("Course: "+title+" started",course), startDate, course.getId(), 0);
                scheduleNotification(getNotification("Course: "+title+" ends today",course), endDate, -course.getId(), 120000);
                populateListView();
                toastMsg("Added successfully!");
                Log.d(TAG, "insertDataToDb: Inserted data into database");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            toastMsg("Something went wrong");
    }

    private void populateListView() {
        try {
            courses = databaseHelper.getAllCourses(term.getId());
            courseAdapter = new CourseAdapter(this, courses);
            itemsListView.setAdapter(courseAdapter);
            courseAdapter.notifyDataSetChanged();
            Log.d(TAG, "populateListView: Displaying data in list view");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void hideFab() {
        itemsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    fab.show();
                } else {
                    fab.hide();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }


    private void onFabClick() {
        try {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddDialog();
                    Log.d(TAG, "onFabClick: Opened edit dialog");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        final long date = System.currentTimeMillis();
        SimpleDateFormat dateSdf = new SimpleDateFormat("d MMMM YYYY");
        String dateString = dateSdf.format(date);
        startDateText.setText(dateString);
        endDateText.setText(dateString);

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        startDateText.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(CourseListActivity.this,
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
                final DatePickerDialog datePickerDialog = new DatePickerDialog(CourseListActivity.this,
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

        dialogBuilder.setTitle("Lets add new Course!");
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
                        insertDataToDb(title, startDate, endDate, status, instructorName, instructorPhone, instructorEmail, term.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    toastMsg("Oops, Cannot set an empty course!!!");
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onRestart() {
        super.onRestart();
        populateListView();
    }
}
