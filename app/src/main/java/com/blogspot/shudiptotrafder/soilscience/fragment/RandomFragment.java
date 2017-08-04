package com.blogspot.shudiptotrafder.soilscience.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Shudipto on 6/17/2017.
 */

public class RandomFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    void data() {
        Bundle bundle = getActivity().getIntent().getExtras();
        String s = bundle.getString("S");

        Uri uri = Uri.parse(s);

    }

}