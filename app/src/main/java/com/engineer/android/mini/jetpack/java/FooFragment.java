package com.engineer.android.mini.jetpack.java;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.engineer.android.mini.R;
import com.engineer.android.mini.jetpack.base.SimpleBaseFragment;

import static com.engineer.android.mini.jetpack.base.SimpleBaseFragmentKt.ARG_PARAM1;
import static com.engineer.android.mini.jetpack.base.SimpleBaseFragmentKt.ARG_PARAM2;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FooFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FooFragment extends SimpleBaseFragment {
    public static FooFragment newInstance(String param1, String param2) {
        FooFragment fragment = new FooFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fooViewModel.getFoo().observe(this, integer -> {
                    TextView tv = view.findViewById(R.id.text);
                    Log.e(getTAG(), "onViewCreated: " + integer);
                    tv.setText("result is " + integer);
                }
        );
        fooViewModel.doFoo();
        fooViewModel.doFoo2();
    }

    @Override
    public int provideLayoutId() {
        return R.layout.fragment_foo;
    }
}