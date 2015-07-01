package com.trucks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by markcorrado on 3/23/15.
 */
public class DrawerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<FoodTruck> mFoodTruckArrayList;
    private LayoutInflater layoutInflater;

    public DrawerListAdapter(Context context, ArrayList<FoodTruck> foodTrucks) {
        super();
        this.context = context;
        this.mFoodTruckArrayList = foodTrucks;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mFoodTruckArrayList.size();
    }

    @Override
    public FoodTruck getItem(int position) {
        return mFoodTruckArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        FoodTruck truck = mFoodTruckArrayList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.drawer_item, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(truck.getName());
        return convertView;
    }

    class ViewHolder {
        TextView title;
    }
}
