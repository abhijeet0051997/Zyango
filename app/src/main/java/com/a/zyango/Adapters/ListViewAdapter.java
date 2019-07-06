package com.a.zyango.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.a.zyango.POJO.ListItem;
import com.a.zyango.R;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends ArrayAdapter<ListItem> {

    Context context;
    List<ListItem> list = new ArrayList<>();

    public ListViewAdapter(@NonNull Context context, List<ListItem> list) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);
        ImageView icon = v.findViewById(R.id.icon_view);
        ImageView edit = v.findViewById(R.id.edit_id);
        TextView hint = v.findViewById(R.id.hint);
        TextView value = v.findViewById(R.id.value);
        icon.setImageResource(list.get(position).getIcon_id());
        edit.setImageResource(list.get(position).getEditable_id());
        hint.setText(list.get(position).getTitle());
        value.setText(list.get(position).getValue());
        return v;
    }
}
