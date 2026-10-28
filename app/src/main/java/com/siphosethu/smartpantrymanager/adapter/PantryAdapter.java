package com.siphosethu.smartpantrymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.siphosethu.smartpantrymanager.R;
import com.siphosethu.smartpantrymanager.model.PantryItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class PantryAdapter extends BaseAdapter {

    private final List<PantryItem> items = new ArrayList<>();
    private final LayoutInflater inflater;
    private final NumberFormat quantityFormat;

    public PantryAdapter(Context context) {
        inflater = LayoutInflater.from(context);

        quantityFormat = NumberFormat.getNumberInstance();
        quantityFormat.setMaximumFractionDigits(6);
    }

    public void replaceItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public PantryItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent
    ) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.item_pantry,
                    parent,
                    false
            );

            holder = new ViewHolder();
            holder.name = convertView.findViewById(
                    R.id.textIngredientName
            );
            holder.quantity = convertView.findViewById(
                    R.id.textIngredientQuantity
            );

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PantryItem item = getItem(position);

        holder.name.setText(item.getIngredientName());

        String quantityText =
                quantityFormat.format(item.getQuantity()) +
                        " " + item.getUnit();

        holder.quantity.setText(quantityText);

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView quantity;
    }
}