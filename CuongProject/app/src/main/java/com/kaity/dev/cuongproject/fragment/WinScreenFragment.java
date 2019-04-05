package com.kaity.dev.cuongproject.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kaity.dev.cuongproject.R;

public class WinScreenFragment extends Fragment {
    public static WinScreenFragment newInstance(Bitmap bitmap) {
        Bundle args = new Bundle();
        args.putParcelable("image", bitmap);
        WinScreenFragment fragment = new WinScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public WinScreenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_win, container, false);
        ImageView iv = (ImageView) view.findViewById(R.id.iv_image);
        iv.setImageBitmap((Bitmap) getArguments().getParcelable("image"));
        return view;
    }
}
