package com.blogspot.shudiptotrafder.soilscience.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blogspot.shudiptotrafder.soilscience.R;
import com.blogspot.shudiptotrafder.soilscience.theme.ThemesContract;

import java.util.ArrayList;

/**
 * Created by Shudipto on 6/19/2017.
 */

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorHolder> {

    private final Context context;
    private final ArrayList<ThemesContract> themeIds;
    private final ColorClickListener clickListener;

    public ColorAdapter(Context context,
                        ArrayList<ThemesContract> themeIds,
                        ColorClickListener clickListener) {

        this.context = context;
        this.themeIds = themeIds;
        this.clickListener = clickListener;
    }

    @Override
    public ColorHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.color_list_item, parent, false);

        return new ColorHolder(view);
    }

    @Override
    public void onBindViewHolder(ColorHolder holder, int position) {

        ThemesContract themesContract = themeIds.get(position);

        int primary = getThemeAttr(themesContract.getId(), R.attr.colorPrimary);
        int primaryDark = getThemeAttr(themesContract.getId(), R.attr.colorPrimaryDark);
        int accent = getThemeAttr(themesContract.getId(), R.attr.colorAccent);


        //textView color
        holder.name.setText(themesContract.getName());
        holder.name.setTextColor(primary);

        //circle color
        holder.primaryColor.setBackground(getDrawAbles(primary));
        holder.primaryColorDark.setBackground(getDrawAbles(primaryDark));
        holder.accentColor.setBackground(getDrawAbles(accent));

    }

    /**
     * This methods for create a new drawable
     * we take a color id and show color through drawable
     *
     * @param id color id for selected color
     */

    private Drawable getDrawAbles(@ColorInt int id) {
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());

        shapeDrawable.setIntrinsicWidth(floatToInt());

        shapeDrawable.setIntrinsicHeight(floatToInt());
        shapeDrawable.setColorFilter(id, PorterDuff.Mode.SRC_ATOP);

        return shapeDrawable;

    }

    /**
     * This methods convert dp to px
     *
     * @param dp float to convert
     * @return converted value in px
     */
    private float dpToPxConverter(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * this methods will convert float to int
     *
     * @return float value to int value
     */
    private int floatToInt() {
        return (int) dpToPxConverter((float) 32);
    }

    /**
     * This methods for find color id through theme
     * @param stylesID theme id of any theme
     * @param attrId color type id like color primary, accent color
     * @return given color id from theme in the basis of colorAttr
     */

    private int getThemeAttr(int stylesID, @AttrRes int attrId) {

        TypedArray typedValue = context.obtainStyledAttributes(stylesID, new int[]{attrId});
        int colorFromTheme = typedValue.getColor(0, 0);
        typedValue.recycle();
        return colorFromTheme;
    }

    @Override
    public int getItemCount() {
        return themeIds.size();
    }


    public interface ColorClickListener {
        void onColorItemClick(int id);
    }

    class ColorHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        final TextView name;
        final TextView primaryColor;
        final TextView primaryColorDark;
        final TextView accentColor;

        final LinearLayout linearLayout;

        ColorHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.color_list_name);
            primaryColor = (TextView) itemView.findViewById(R.id.color_list_primary);
            primaryColorDark = (TextView) itemView.findViewById(R.id.color_list_primaryDark);
            accentColor = (TextView) itemView.findViewById(R.id.color_list_accent);

            linearLayout = (LinearLayout) itemView
                    .findViewById(R.id.color_list_linearLayout);

            linearLayout.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            clickListener.onColorItemClick(getAdapterPosition());
        }
    }
}
