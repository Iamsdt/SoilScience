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

package com.blogspot.shudiptotrafder.soilscience.animation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Shudipto Trafder.
 * on 6/7/2017
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
                goToDown ? 50 : -0,5
        );

        objectAnimator.setDuration(900);

        animatorSet.play(objectAnimator);
        animatorSet.start();

    }

}
