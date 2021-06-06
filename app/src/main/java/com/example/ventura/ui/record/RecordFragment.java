package com.example.ventura.ui.record;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ventura.R;
import com.example.ventura.StartSessionActivity;

public class RecordFragment extends Fragment {

    private RecordViewModel mViewModel;

    public static RecordFragment newInstance() {
        return new RecordFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.record_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RecordViewModel.class);
        Intent i = new Intent(this.getActivity(), StartSessionActivity.class);
        startActivity(i);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}