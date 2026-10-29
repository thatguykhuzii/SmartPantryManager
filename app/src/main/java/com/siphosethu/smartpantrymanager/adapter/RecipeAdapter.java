package com.siphosethu.smartpantrymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.siphosethu.smartpantrymanager.R;
import com.siphosethu.smartpantrymanager.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends BaseAdapter {

    private final List<Recipe> recipes = new ArrayList<>();
    private final LayoutInflater inflater;

    public RecipeAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void replaceRecipes(List<Recipe> newRecipes) {
        recipes.clear();
        recipes.addAll(newRecipes);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return recipes.size();
    }

    @Override
    public Recipe getItem(int position) {
        return recipes.get(position);
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
                    R.layout.item_suggested_recipe,
                    parent,
                    false
            );

            holder = new ViewHolder();
            holder.name = convertView.findViewById(
                    R.id.textRecipeName
            );

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(getItem(position).getName());

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
    }
}