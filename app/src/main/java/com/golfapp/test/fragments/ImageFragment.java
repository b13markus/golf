package com.golfapp.test.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.golfapp.test.R;
import com.squareup.picasso.Picasso;


public class ImageFragment extends BaseFragment {

    private int image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        image = getArguments().getInt("Image");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        Picasso.with(baseActivity).load(image).into(imageView);
        return view;
    }
}