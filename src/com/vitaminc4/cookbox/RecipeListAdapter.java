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

public class RecipeListAdapter extends BaseAdapter {
	private final Context context;
	private List<Recipe> values;

	public RecipeListAdapter(Context context) {
		this.context = context;
		this.values = new ArrayList<Recipe>();

    for (String f : LocalCache.getFileList()) {
      String md = LocalCache.getFile(f);
      Log.i("Cookbox", new Integer(md.length()).toString());
      this.values.add(new Recipe(md));
    }
	}
	
	@Override public long getItemId(int position) {
	  return (long) position;
	}
	
	@Override public Object getItem(int position) {
	  return this.values.get(position);
	}
	
	@Override public int getCount() {
	  return this.values.size();
	}

	@Override public View getView(int position, View convertView, ViewGroup parent) {
		Recipe r = this.values.get(position);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.recipe_list_view, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		textView.setText(r.name);
		return rowView;
	}
}