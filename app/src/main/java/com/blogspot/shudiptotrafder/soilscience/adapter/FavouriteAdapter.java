/*
 * Copyright {2017} {Shudipto Trafder}
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blogspot.shudiptotrafder.soilscience.adapter;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blogspot.shudiptotrafder.soilscience.R;
import com.blogspot.shudiptotrafder.soilscience.data.MainWordDBContract;
import com.blogspot.shudiptotrafder.soilscience.utilities.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Shudipto Trafder on 11/8/2017.
 * at 9:35 PM
 */

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.MyViewHolder> {

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    private final Handler handler = new Handler(); // hanlder for running delayed runnables
    private final HashMap<String, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be


    private final List<String> dataList;
    private final List<String> itemsPendingRemoval;

    private final FavouriteAdapter.ClickListener clickListener;

    private final Context mContext;

    //private int previousPosition = -1;


    //private boolean isDeleteRequest = false;


    public FavouriteAdapter(FavouriteAdapter.ClickListener clickListener,
                            List<String> dataList, Context context) {
        this.clickListener = clickListener;
        mContext = context;

        this.dataList = dataList;
        itemsPendingRemoval = new ArrayList<>();
    }


    @Override
    public FavouriteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favourite_row_item, parent, false);

        return new FavouriteAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavouriteAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        final String data = dataList.get(position);

        if (itemsPendingRemoval.contains(data)) {
            holder.regular.setVisibility(View.GONE);
            holder.swipe.setVisibility(View.VISIBLE);

            holder.undo.setOnClickListener(v -> undoOpt(data));

        } else {
            holder.swipe.setVisibility(View.GONE);
            holder.regular.setVisibility(View.VISIBLE);

            holder.word.setText(data);
            holder.word.setTextSize(Utility.getTextSize(mContext));

            holder.itemView.setTag(data);
        }


//        if(position > previousPosition){ // We are scrolling DOWN
//            AnimationUtils.animate(holder, true);
//
//        }else{ // We are scrolling UP
//            AnimationUtils.animate(holder, false);
//        }
//
//        previousPosition = position;


    }

    private void undoOpt(String customer) {
        Runnable pendingRemovalRunnable = pendingRunnables.get(customer);
        pendingRunnables.remove(customer);
        if (pendingRemovalRunnable != null)
            handler.removeCallbacks(pendingRemovalRunnable);
        itemsPendingRemoval.remove(customer);
        // this will rebind the row in "normal" state
        notifyItemChanged(dataList.indexOf(customer));
    }

    public void pendingRemoval(int position, View view) {

        final String data = dataList.get(position);
        if (!itemsPendingRemoval.contains(data)) {
            itemsPendingRemoval.add(data);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the data
            Runnable pendingRemovalRunnable = () -> remove(dataList.indexOf(data), view);
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(data, pendingRemovalRunnable);
        }
    }

    private void remove(int position,View view) {
        String data = dataList.get(position);
        if (itemsPendingRemoval.contains(data)) {
            itemsPendingRemoval.remove(data);
        }
        if (dataList.contains(data)) {
            dataList.remove(position);

            Uri uri = MainWordDBContract.Entry.buildUriWithWord(data);

            ContentValues values = new ContentValues();
            values.put(MainWordDBContract.Entry.COLUMN_FAVOURITE, false);
            int update = mContext.getContentResolver().update(uri, values, null, null);

            if (update != -1){
                notifyItemRemoved(position);
                Snackbar.make(view,"One favourite word removed",Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isPendingRemoval(int position) {
        String data = dataList.get(position);
        return itemsPendingRemoval.contains(data);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface ClickListener {
        void onItemClickListener(String s);
    }

    // Inner class for creating ViewHolders
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        final TextView word;
        CardView cardView;

        final LinearLayout regular;
        final LinearLayout swipe;
        final TextView delete;
        final TextView undo;


        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        MyViewHolder(View itemView) {
            super(itemView);

            word = itemView.findViewById(R.id.mainRecycleView_TV);

            swipe = itemView.findViewById(R.id.swipeLayout);
            regular = itemView.findViewById(R.id.regular_layout);

            delete = itemView.findViewById(R.id.undo_deleteTxt);
            undo = itemView.findViewById(R.id.undoBtn);


            if (Build.VERSION.SDK_INT >= 21) {
                cardView = itemView.findViewById(R.id.mainRecycleView_Card);
                cardView.setOnClickListener(this);

            } else {
                word.setOnClickListener(this);
            }

        }

        @Override
        public void onClick(View v) {
            String word = dataList.get(getAdapterPosition());
            clickListener.onItemClickListener(word);
        }
    }
}

