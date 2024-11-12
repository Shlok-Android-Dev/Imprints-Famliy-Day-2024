package com.runner.bluetooth;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;


import com.runner.R;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Object> deviceList;

    public DeviceListAdapter(Context context, List<Object> deviceList) {
        this.context = context;
        this.deviceList = deviceList;

    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_info_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ViewHolder itemHolder = (ViewHolder) holder;
        final DeviceInfoModel deviceInfoModel = (DeviceInfoModel) deviceList.get(position);
        itemHolder.textName.setText(deviceInfoModel.getDeviceName());
        itemHolder.textAddress.setText(deviceInfoModel.getDeviceHardwareAddress());

        // When a device is selected
        itemHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                SharedPreferences prefeMain = context.getSharedPreferences("FRIENDS", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefeMain.edit();

                if(deviceInfoModel.getDeviceHardwareAddress().equalsIgnoreCase("VIA-USB"))
                {
                    editor.putString("deviceAddress", "");
                    editor.putString("deviceName", "");
                    editor.putString("PRINTER_TYPE", "TSC");
                    editor.putString("PRINTER_TYPE_TSC", "USB");
                    editor.commit();
                    Toast.makeText(context,"TSC Printer is selected for Print",Toast.LENGTH_LONG).show();
                }
                else
                {
                    editor.putString("deviceAddress", deviceInfoModel.getDeviceHardwareAddress());
                    editor.putString("deviceName", deviceInfoModel.getDeviceName());
                    editor.putString("PRINTER_TYPE", "TSC");
                    editor.putString("PRINTER_TYPE_TSC", "Bluetooth");
                    editor.commit();
                    Toast.makeText(context,"Bluetooth device is selected for Print",Toast.LENGTH_LONG).show();
                }


                Activity activity= (Activity) context;
                activity.finish();

            }
        });
    }

    @Override
    public int getItemCount() {
        int dataCount = deviceList.size();
        return dataCount;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textAddress;
        LinearLayout linearLayout;

        public ViewHolder(View v) {
            super(v);
            textName = v.findViewById(R.id.textViewDeviceName);
            textAddress = v.findViewById(R.id.textViewDeviceAddress);
            linearLayout = v.findViewById(R.id.linearLayoutDeviceInfo);
        }
    }
}
