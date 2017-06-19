package com.blogspot.shudiptotrafder.soilscience.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Shudipto on 6/17/2017.
 */

public class RandomFragment extends Fragment {


    void data() {
        Bundle bundle = getActivity().getIntent().getExtras();
        String s = bundle.getString("S");

        Uri uri = Uri.parse(s);

    }

}