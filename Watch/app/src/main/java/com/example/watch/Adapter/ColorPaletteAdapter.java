package com.example.watch.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;

import java.util.ArrayList;

public class ColorPaletteAdapter extends BaseAdapter {
    private static final String TAG = "ColorPaletteAdapter";

    private Context context;
    private int[][] colorRGB;
    private int width, height;

    public ColorPaletteAdapter(Context context, int[][] colorRGB, int width, int height) {
        this.context = context;
        this.colorRGB = colorRGB;
        this.width = width;
        this.height = height;
    }

    @Override
    public int getCount() {
        return colorRGB.length;
    }

    @Override
    public Object getItem(int position) {
        return colorRGB[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = new View(context);
            view.setLayoutParams(new GridView.LayoutParams(width, height));
        } else {
            view = convertView;
        }

        view.setBackgroundColor(Color.rgb(colorRGB[position][0], colorRGB[position][1], colorRGB[position][2]));

        return view;
    }
}
