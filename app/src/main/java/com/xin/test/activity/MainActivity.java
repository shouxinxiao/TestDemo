package com.xin.test.activity;

import android.support.v4.app.Fragment;

import com.xin.test.fragment.TestFragment;


public class MainActivity extends SingleFragmenyActivity {

    @Override
    protected Fragment createFragment() {
        return TestFragment.newIntener();
    }
}
