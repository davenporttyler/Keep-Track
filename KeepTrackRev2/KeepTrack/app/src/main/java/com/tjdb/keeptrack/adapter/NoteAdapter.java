package com.tjdb.keeptrack.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.tjdb.keeptrack.utilities.DatabaseHelper;
import com.tjdb.keeptrack.R;
import com.tjdb.keeptrack.model.Note;

import java.util.ArrayList;

public class NoteAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Note> arrayList;

    public NoteAdapter(Context context, ArrayList<Note> arrayList) {
        super();
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return this.arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.note_item, null);
        TextView TextView = convertView.findViewById(R.id.text);

        //final ImageView delImageView = convertView.findViewById(R.id.delete);
        //delImageView.setTag(position);

        final View finalConvertView = convertView;
        //On delete icon click remove item from list and database
        /*delImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pos = (int) v.getTag();
                Animation animSlideRight = AnimationUtils.loadAnimation(context, R.anim.slide_out_right);
                animSlideRight.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // Fires when animation starts
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // ...
                        deleteItem(pos);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // ...
                    }
                });
                finalConvertView.startAnimation(animSlideRight);
            }
        });*/

        Note note = arrayList.get(position);

        convertView.setOnClickListener(view -> {
            //add dialog
        });

        TextView.setText(note.getText());

        return convertView;

    }

    //Remove item from list and database
    public void deleteItem(int position) {
        deleteItemFromDb(arrayList.get(position).getId());
        arrayList.remove(position);
        notifyDataSetChanged();
    }

    //Delete item from database
    public void deleteItemFromDb(long id) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        try {
            databaseHelper.deleteCourse(id);
            toastMsg("Deleted Successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            toastMsg("Something went wrong");
        }
    }

    //Create and call toast messages when necessary
    public void toastMsg(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}


