package com.tjdb.keeptrack;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tjdb.keeptrack.adapter.NoteAdapter;
import com.tjdb.keeptrack.model.Course;
import com.tjdb.keeptrack.model.Note;
import com.tjdb.keeptrack.utilities.DatabaseHelper;

import java.util.ArrayList;

public class NoteListActivity extends AppCompatActivity {
    private static final String TAG = "TermActivity";
    private DatabaseHelper databaseHelper;
    private ArrayList<Note> notes;
    private NoteAdapter noteAdapter;
    private ListView itemsListView;
    private FloatingActionButton fab;

    private TextView title;

    private Course course;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        databaseHelper = new DatabaseHelper(this);
        long courseId = getIntent().getExtras().getLong("courseId");
        course = databaseHelper.getCourseById(courseId);

        //set custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(R.id.app_title);
        title.setText("Course: " + course.getTitle());

        fab = findViewById(R.id.fab);
        itemsListView = findViewById(R.id.itemsList);

        //initialise and set empty listView
        TextView empty = findViewById(R.id.emptyTextView);
        empty.setText("No note in this course..");
        FrameLayout emptyView = findViewById(R.id.emptyView);
        itemsListView.setEmptyView(emptyView);

        populateListView();
        onFabClick();
        //hideFab();
    }

    private void insertDataToDb(String text, long courseId) {
        boolean insertData = databaseHelper.insertNote(text, courseId);
        if (insertData) {
            try {
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
            notes = databaseHelper.getAllNotes(course.getId());
            noteAdapter = new NoteAdapter(this, notes);
            itemsListView.setAdapter(noteAdapter);
            noteAdapter.notifyDataSetChanged();
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
        final View dialogView = inflater.inflate(R.layout.note_dialog, null);
        dialogBuilder.setView(dialogView);


        final EditText editText = dialogView.findViewById(R.id.text);

        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String text = editText.getText().toString();
                if (text.length() != 0) {
                    try {
                        insertDataToDb(text, course.getId());
                        //scheduleNotification(getNotification(title), cal.getTimeInMillis());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    toastMsg("Oops, Cannot set an empty Note!!!");
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
