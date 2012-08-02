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
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

public class RecipeSearchAdapter extends BaseAdapter {
	private final Context context;
	private List<RecipeListing> values;

	public RecipeSearchAdapter(Context context, Document d) {
		this.context = context;
		this.values = new ArrayList<RecipeListing>();
		
		Elements values = d.select(".group-middle");
		for (Element e : values) {
		  Element name_link = e.select(".field-name-title a").first();

		  String name = name_link.text();
		  String likes = e.select(".field-name-recipe-likes-comments .field-item").first().text();
		  String url = name_link.attr("href");

		  this.values.add(new RecipeListing(name, likes, url));
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
		RecipeListing r = this.values.get(position);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.recipe_list_view, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		textView.setText(r.name + "\n(" + r.likes + ")");
		return rowView;
	}
	
	class RecipeListing {
    public String name;
    public String likes;
    public String url;
    
    public RecipeListing(String n, String l, String u) {
      this.name = n;
      this.likes = l;
      this.url = u;
    }
	}
}