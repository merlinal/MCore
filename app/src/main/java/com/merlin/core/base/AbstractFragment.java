package com.merlin.core.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ncm on 16/11/30.
 */

public class AbstractFragment extends Fragment implements ViewInterface {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void initTool() {
    }

    @Override
    public void handleParam() {
    }

    @Override
    public void initData() {
    }

    @Override
    public void initView() {
    }

    public void onFragmentBackPressed() {
        getActivity().onBackPressed();
    }

}
