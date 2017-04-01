package com.blogspot.shudiptotrafder.soilscience.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blogspot.shudiptotrafder.soilscience.R;
import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;

/**
 * SoilScience
 * com.blogspot.shudiptotrafder.soilscience.adapter
 * Created by Shudipto Trafder on 4/1/2017.
 */

public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.MyViewHolder> {

    // Class variables for the Cursor that holds task data and the Context
    private Cursor mCursor;
    //private Context mContext;

    private ClickListener clickListener;

    /**
     * Constructor for the CustomCursorAdapter that initializes the Context.
     *
     * @param mContext the current Context
     */
    public CustomCursorAdapter(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null;
            // bc nothing has changed
        }
        Cursor tempCursor = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return tempCursor;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_recycler_view_list,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        int descriptionIndex = mCursor.getColumnIndex(MainWordDBContract.MainWordDBEntry.COLUMN_WORD);
        String word = mCursor.getString(descriptionIndex);
        holder.word.setText(word);
    }

    @Override
    public int getItemCount() {

        if (mCursor==null){
            return 0;
        }

        return mCursor.getCount();
    }

    public interface ClickListener{
        void onItemClickListener(String s);
    }

    // Inner class for creating ViewHolders
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        TextView word;
        CardView cardView;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public MyViewHolder(View itemView) {
            super(itemView);

            word = (TextView) itemView.findViewById(R.id.mainRecycleView_TV);
            cardView = (CardView) itemView.findViewById(R.id.mainRecycleView_Card);

            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCursor.moveToPosition(getAdapterPosition());
            int descriptionIndex = mCursor.getColumnIndex(MainWordDBContract.MainWordDBEntry.COLUMN_WORD);
            String word = mCursor.getString(descriptionIndex);
            clickListener.onItemClickListener(word);
        }
    }
}
