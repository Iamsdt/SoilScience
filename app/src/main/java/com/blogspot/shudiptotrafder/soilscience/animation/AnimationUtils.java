package com.blogspot.shudiptotrafder.soilscience.animation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Shudipto on 6/7/2017.
 */

public class AnimationUtils {

    /**
     * This methods for animate recycler item
     * when recycler view scroll to top or down
     * @param viewHolder recycler view item
     * @param goToDown is going down or up
     */

    public static void animate(RecyclerView.ViewHolder viewHolder,
                               boolean goToDown){

        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
                viewHolder.itemView,
                "translationY",
                goToDown ? 200 : -200,0
        );

        objectAnimator.setDuration(1000);

        animatorSet.play(objectAnimator);
        animatorSet.start();

    }

}
