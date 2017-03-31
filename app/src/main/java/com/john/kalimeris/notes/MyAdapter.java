package com.john.kalimeris.notes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.john.kalimeris.notes.R.id.textView;

/**
 * Created by production_user13 on 27/9/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    static Activity _activity;
    static private List<noteObject> mDataset;
    static int count = 0;
    private int lastPosition = 0;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        //public TextView mTextView;
        protected TextView title;
        protected TextView description;
        protected TextView date;
        private ImageView reminderImage;

        public ViewHolder(View v) {
            super(v);

            v.setOnClickListener(this);

            title = (TextView) v.findViewById(textView);
            date = (TextView) v.findViewById(R.id.textView2);
            reminderImage = (ImageView) v.findViewById(R.id.imageViewReminder);
            description = (TextView) v.findViewById(R.id.txtv_description);

            checkDisplayViews();
            checkDescriptionFontSize();
        }

        private void checkDescriptionFontSize() {
            if (Utils.getValueFromListPreference(_activity, "font_size").equalsIgnoreCase("small"))
                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                    description.setTextAppearance(android.R.style.TextAppearance_Small);
                    description.setTypeface(null, Typeface.BOLD);
                }
                else {
                    description.setTextAppearance(_activity, android.R.style.TextAppearance_Small);
                    description.setTypeface(null, Typeface.BOLD);
                }
            else if (Utils.getValueFromListPreference(_activity, "font_size").equalsIgnoreCase("medium"))
                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                    description.setTextAppearance(android.R.style.TextAppearance_Medium);
                    description.setTypeface(null, Typeface.BOLD);
                }
                else {
                    description.setTextAppearance(_activity, android.R.style.TextAppearance_Medium);
                    description.setTypeface(null, Typeface.BOLD);
                }
            else if (Utils.getValueFromListPreference(_activity, "font_size").equalsIgnoreCase("large"))
                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                    description.setTextAppearance(android.R.style.TextAppearance_Large);
                    description.setTypeface(null, Typeface.BOLD);
                }
                else {
                    description.setTextAppearance(_activity, android.R.style.TextAppearance_Large);
                    description.setTypeface(null, Typeface.BOLD);
                }
        }

        private void checkDisplayViews() {

            if (Utils.getValueFromMultiselectCheckBox(_activity, "display_views", "title").equalsIgnoreCase("true"))
                title.setVisibility(View.VISIBLE);
            else
                title.setVisibility(View.INVISIBLE);

            if (Utils.getValueFromMultiselectCheckBox(_activity, "display_views", "description").equalsIgnoreCase("true"))
                description.setVisibility(View.VISIBLE);
            else
                description.setVisibility(View.INVISIBLE);

            if (Utils.getValueFromMultiselectCheckBox(_activity, "display_views", "date").equalsIgnoreCase("true"))
                date.setVisibility(View.VISIBLE);
            else
                date.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClick(View v) {
            int position = MainActivity.mRecyclerView.getChildAdapterPosition(v);
            noteObject note = mDataset.get(position);

            Intent intent = new Intent(_activity, NewEditNoteActivity.class);
            intent.putExtra("noteObject", note);
            intent.putExtra("newNote", false);
            _activity.startActivity(intent);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<noteObject> myDataset, Activity activity) {
        mDataset = myDataset;
        _activity = activity;
    }

    public void removeItem(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataset.size());
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mDataset, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mDataset, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_view, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.title.setText(mDataset.get(position).getTitle());
        holder.description.setText(mDataset.get(position).getDescription());
        holder.date.setText(mDataset.get(position).getDate());
        holder.reminderImage.setVisibility((mDataset.get(position).getEventId() != -1) ? View.VISIBLE : View.INVISIBLE);

        noteObject note = mDataset.get(position);

        holder.itemView.setBackgroundResource(R.drawable.note_item_style);
        GradientDrawable drawable = (GradientDrawable) holder.itemView.getBackground();

        drawable.setColor(Color.parseColor(note.getColor()));

        if (mDataset.get(position).getCheck()) {
            holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.description.setPaintFlags(holder.description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.title.setAlpha(0.5f);
            holder.description.setAlpha(0.5f);
            holder.date.setAlpha(0.5f);
        }
        else {
            holder.title.setPaintFlags(0);
            holder.description.setPaintFlags(0);
            holder.title.setAlpha(1f);
            holder.description.setAlpha(1f);
            holder.date.setAlpha(1f);
        }

        setAnimation(holder.itemView, position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setFilter(List<noteObject> newNotes) {

        mDataset = new ArrayList<noteObject>();
        mDataset.addAll(newNotes);
        notifyDataSetChanged();
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(_activity, android.R.anim.slide_in_left);
            animation.setDuration(500);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        ((View) holder.itemView).clearAnimation();
    }
}