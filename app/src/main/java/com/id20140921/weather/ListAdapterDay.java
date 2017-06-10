package com.id20140921.weather;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by moses on 11/03/2017.
 */

public class ListAdapterDay extends BaseAdapter {

    private Context context;
    private final String[] day;
    private final Integer[] images;
    private final String[] temp_max;
    private final String[] temp_min;
    private final String[] pop;

    public ListAdapterDay(Context c, String[] d, Integer[] im, String[] tmax, String[] tmin,String[] p) {
        context = c;
        this.day = d;
        this.images = im;
        this.temp_max = tmax;
        this.temp_min = tmin;
        this.pop = p;
    }

    @Override
    public int getCount() {
        return day.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View grid;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.forecase_day_item,null);
        }
        else {
            grid = (View)view;
        }

        TextView tx_f_day = (TextView)grid.findViewById(R.id.textView_f_day);
        ImageView im_f_day = (ImageView)grid.findViewById(R.id.imageView_f_day);
        TextView tx_f_temp_max = (TextView)grid.findViewById(R.id.f_day_temp_max);
        TextView tx_f_temp_min = (TextView)grid.findViewById(R.id.f_day_temp_min);
        TextView tx_f_pop = (TextView)grid.findViewById(R.id.textView_f_day_pop);

        tx_f_day.setText(day[i]);
        im_f_day.setImageResource(images[i]);
        tx_f_temp_max.setText(temp_max[i]+"°");
        tx_f_temp_min.setText(temp_min[i]+"°");
        tx_f_pop.setText(pop[i]+"%");


        return grid;
    }
}
