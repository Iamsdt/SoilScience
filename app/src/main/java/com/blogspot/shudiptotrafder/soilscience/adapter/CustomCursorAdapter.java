package com.blogspot.shudiptotrafder.soilscience.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blogspot.shudiptotrafder.soilscience.R;
import com.blogspot.shudiptotrafder.soilscience.animation.AnimationUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.ConstantUtils;
import com.blogspot.shudiptotrafder.soilscience.utilities.Utility;

import java.util.Random;

/**
 * SoilScience
 * com.blogspot.shudiptotrafder.soilscience.adapter
 * Created by Shudipto Trafder on 4/1/2017.
 */

public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.MyViewHolder> {

    // Class variables for the Cursor that holds task data and the Context
    private Cursor mCursor;
    //private Context mContext;

    private final ClickListener clickListener;

    private final Context mContext;

    private int previousPosition = 0;

    /**
     * Constructor for the CustomCursorAdapter that initializes the Context.
     *
     * @param context the current Context
     */
    public CustomCursorAdapter(ClickListener clickListener,Context context) {
        this.clickListener = clickListener;
        mContext = context;
    }

    /**
     * set cursor from other activity
     * @param cursor new cursor for replace previous cursor
     */

    public void swapCursor(Cursor cursor) {

        mCursor = cursor;

        //check if this is a valid cursor, then update the cursor
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
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

        String word = mCursor.getString(ConstantUtils.INDEX_ONLY_WORD);
        holder.word.setText(word);
        holder.word.setTextSize(Utility.getTextSize(mContext));

        holder.itemView.setTag(word);

        if(position > previousPosition){ // We are scrolling DOWN
            AnimationUtils.animate(holder, true);

        }else{ // We are scrolling UP
            AnimationUtils.animate(holder, false);
        }

        previousPosition = position;

    }

    @Override
    public int getItemCount() {

        if (mCursor == null){
            return 0;
        }

        return mCursor.getCount();
    }

    /**
     * This method for select word from database randomly
     * @return random word form database
     */
    public String getRandomWord(){

        String word = null;

        if (mCursor != null){

            int size = mCursor.getCount();

            Random random = new Random();
            int position = random.nextInt(size);
            if (position == 0){
                mCursor.moveToPosition(1);
            } else {
                mCursor.moveToPosition(position);
            }

            word = mCursor.getString(ConstantUtils.INDEX_ONLY_WORD);
        }

        Utility.setAnalyticsData(mContext,"Random Word",word);

        return word;
    }


    public interface ClickListener{
        void onItemClickListener(String s);
    }

    // Inner class for creating ViewHolders
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        final TextView word;
        CardView cardView;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        MyViewHolder(View itemView) {
            super(itemView);

            word = (TextView) itemView.findViewById(R.id.mainRecycleView_TV);

            if (Build.VERSION.SDK_INT >= 21){
                cardView = (CardView) itemView.findViewById(R.id.mainRecycleView_Card);
                cardView.setOnClickListener(this);

            } else {
                word.setOnClickListener(this);
            }

        }

        @Override
        public void onClick(View v) {
            mCursor.moveToPosition(getAdapterPosition());
            String word = mCursor.getString(ConstantUtils.INDEX_ONLY_WORD);
            clickListener.onItemClickListener(word);
        }
    }
}
