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

/**
 * Created by Shudipto Trafder on 11/29/2017.
 * at 9:46 PM
 */


public interface ItemClickListener {
    //This class for item click listener for recycler view


    /**
     * This methods for RecyclerView click listener
     * @param s is a string to pass any text for further use
     */
    void onItemClickListener(String s);

    /**
     * This methods for RecyclerView click listener
     * @param i is a int to pass any int number for further use
     */
    void onItemClickListener(int i);
}
