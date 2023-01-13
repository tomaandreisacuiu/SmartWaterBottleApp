package com.example.smartwaterbottle;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CardsAdapter extends ArrayAdapter<AlarmModel> {

    private int layoutResourceId;
    private Context context;
    private List<AlarmModel> data;
    private DataBaseHelper dataBaseHelper;

    public CardsAdapter(Context context, int layoutResourceId, List<AlarmModel> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        dataBaseHelper = new DataBaseHelper(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) row.findViewById(R.id.textView);
            holder.deleteButton = (Button) row.findViewById(R.id.deleteButton);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final AlarmModel item = data.get(position);
        holder.textView.setText(item.toString());
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataBaseHelper.deleteOne(item);
                data.remove(position);
                notifyDataSetChanged();
            }
        });

        return row;
    }

    static class ViewHolder {
        TextView textView;
        Button deleteButton;
    }
}

