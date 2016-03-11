package com.persistent.beacondetector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Beacon> {
    Context mContext;
    List<Beacon> dataList;
    int resource;

    public CustomAdapter(Context context, int resource, List<Beacon> objects) {
        super(context, resource, objects);
        mContext = context;
        dataList = objects;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater;
        final ViewHolder viewHolder;
        if (convertView == null) {
            inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.resource, null);
            viewHolder = new ViewHolder();
            viewHolder.UUID = (TextView) convertView.findViewById(R.id.uuid);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);
            viewHolder.majorMinor = (TextView) convertView.findViewById(R.id.major_minor);
            viewHolder.txPower = (TextView) convertView.findViewById(R.id.tx_power);
            viewHolder.RSSIPower = (TextView) convertView.findViewById(R.id.rssi_power);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Beacon beacon = this.dataList.get(position);
        viewHolder.UUID.setText("UUID :" + beacon.getId1());
        viewHolder.distance.setText("Distance :"+beacon.getDistance());
        viewHolder.majorMinor.setText("Major :"+beacon.getId2()+" Minor :"+beacon.getId3());
        viewHolder.txPower.setText("Tx Power :"+beacon.getTxPower());
        viewHolder.RSSIPower.setText("RSSI : "+beacon.getRssi());
        return convertView;
    }

    static class ViewHolder {
        TextView UUID;
        TextView distance;
        TextView majorMinor;
        TextView txPower;
        TextView RSSIPower;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }
}
