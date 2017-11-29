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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blogspot.shudiptotrafder.soilscience.R;
import com.blogspot.shudiptotrafder.soilscience.utilities.Utility;

import java.util.ArrayList;

/**
 * Created by Shudipto Trafder on 11/3/2017.
 * at 9:36 PM
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    private final ItemClickListener clickListener;

    private final Context mContext;

    private ArrayList<String> arrayList;


    /**
     * Constructor for the CustomCursorAdapter that initializes the Context.
     *
     * @param context the current Context
     */
    public SearchAdapter(ItemClickListener clickListener, Context context,
                         ArrayList<String> arrayList) {
        this.clickListener = clickListener;
        mContext = context;
        this.arrayList = arrayList;
    }

    /**
     * set cursor from other activity
     *
     * @param arrayList new cursor for replace previous cursor
     */

    public void swapCursor(ArrayList<String> arrayList) {

        this.arrayList = arrayList;

        //check if this is a valid cursor, then update the cursor
        if (arrayList != null && arrayList.size() > 0) {
            this.notifyDataSetChanged();
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_view_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        String word = arrayList.get(position);
        holder.word.setText(word);
        holder.word.setTextSize(Utility.getTextSize(mContext));
    }


    @Override
    public int getItemCount() {

        if (arrayList == null) {
            return 0;
        }

        return arrayList.size();
    }

    // Inner class for creating ViewHolders
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        final TextView word;

        // --Commented out by Inspection (11/29/2017 9:42 PM):final ImageView imageView;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        MyViewHolder(View itemView) {
            super(itemView);

            word = itemView.findViewById(R.id.search_view_item_tv);
            //imageView = itemView.findViewById(R.id.search_view_image);
            word.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            String word = arrayList.get(getAdapterPosition());
            clickListener.onItemClickListener(word);
        }
    }
}
