package com.id20140921.weather;

import android.content.Context;
import android.content.Intent;
import android.text.style.IconMarginSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by moses on 11/03/2017.
 */

public class ListAdapterTime extends BaseAdapter {

    private Context context;
    private final String[] time;
    private final String[] temp;
    private final Integer[] images;
    private final String[] humidity;

    public ListAdapterTime(Context c, String[] t, Integer[] im, String[] tp, String[] h) {
        context = c;
        this.time = t;
        this.images = im;
        this.temp = tp;
        this.humidity = h;
    }

    @Override
    public int getCount() {
        return time.length;
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
            grid = inflater.inflate(R.layout.forecase_time_item,null);
        }
        else {
            grid = (View)view;
        }

        TextView tx_f_time = (TextView)grid.findViewById(R.id.textView_f_time);
        ImageView im_f_time = (ImageView)grid.findViewById(R.id.imageView_f_time);
        TextView tx_f_temp = (TextView)grid.findViewById(R.id.textView_f_temp);
        TextView tx_f_hum = (TextView)grid.findViewById(R.id.textView_f_hum);

        tx_f_time.setText(time[i]);
        im_f_time.setImageResource(images[i]);
        tx_f_temp.setText(temp[i]+"°");
        tx_f_hum.setText(humidity[i]+"%");


        return grid;
    }
}
