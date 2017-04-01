package com.blogspot.shudiptotrafder.soilscience.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blogspot.shudiptotrafder.soilscience.R;

/**
 * SoilScience
 * com.blogspot.shudiptotrafder.soilscience.adapter
 * Created by Shudipto Trafder on 4/1/2017.
 */

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.MyViewHolder>{

    private String[] words;
    private onItemClickListener clickListener;

    public MainRecyclerViewAdapter(onItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_recycler_view_list,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String text = words[position];
        holder.textView.setText(text);
    }

    @Override
    public int getItemCount() {
        return words.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textView;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.mainRecycleView_TV);
            cardView = (CardView) itemView.findViewById(R.id.mainRecycleView_Card);
            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            String word = words[getAdapterPosition()];
            clickListener.clickListener(word);
        }
    }

    //setters for words list
    public void setWords(String[] words) {
        this.words = words;
    }

    //interface for click handler
    public interface onItemClickListener{
        void clickListener(String s);
    }

}
