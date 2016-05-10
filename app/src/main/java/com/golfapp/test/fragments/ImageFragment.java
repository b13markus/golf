package com.golfapp.test.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
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
    private Uri uri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        image = getArguments().getInt("Image");
        uri = resourceToUri(baseActivity, image);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        Picasso.with(baseActivity)
                .load(uri)
                .config(Bitmap.Config.RGB_565)
                .into(imageView);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Picasso.with(baseActivity).invalidate(resourceToUri(baseActivity, image));
    }

    public static Uri resourceToUri (Context context, int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID) );
    }
}