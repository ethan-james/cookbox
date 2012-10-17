package com.vitaminc4.cookbox;

import java.util.*;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import android.database.Cursor;
import java.util.Collections;
import java.util.Comparator;
import com.orm.androrm.*;

public class RecipeListAdapter extends BaseAdapter {
	private final Context context;
	private QuerySet<Recipe> values;

	public RecipeListAdapter(Context context) {
		this.context = context;
		loadRecipes();
	}
	
	private void loadRecipes() {
    this.values = Recipe.objects(this.context).orderBy("name");
	}
	
	@Override public long getItemId(int position) {
	  return (long) this.values.toList().get(position).getId();
	}
	
	@Override public Object getItem(int position) {
	  return this.values.toList().get(position);
	}
	
	@Override public int getCount() {
	  return this.values.count();
	}

	@Override public View getView(int position, View convertView, ViewGroup parent) {
		Recipe r = this.values.toList().get(position);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.recipe_list_view, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		textView.setText(r.name.get());
		return rowView;
	}
	
	public void refresh() {
	  loadRecipes();
	  notifyDataSetChanged();
	}
}