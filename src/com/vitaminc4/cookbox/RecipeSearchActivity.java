package com.vitaminc4.cookbox;

import com.actionbarsherlock.app.SherlockListActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import java.net.*;
import java.io.*;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

public class RecipeSearchActivity extends SherlockListActivity {
  @Override public void onStart() {
    super.onStart();
    this.setContentView(R.layout.cookbox);

    Intent i = this.getIntent();
    ListView recipe_list = (ListView) findViewById(android.R.id.list);
    String search_query = i.getStringExtra("search");
    Document doc = null;

    try {
      doc = Jsoup.connect("http://vegweb.com/search/vegweb/" + search_query).get();
    } catch (Exception e) { e.printStackTrace(); }

    recipe_list.setAdapter(new RecipeSearchAdapter(this, doc));
  }

  @Override public void onListItemClick(ListView l, View v, int position, long id) {
    String url = "http://vegweb.com" + (String) l.getItemAtPosition(position);
    Recipe r = null;
    try {
      Document d = Jsoup.connect(url).get();
      Log.w("Cookbox", Integer.parseInt(d.select(".field-name-field-recipe-servings .field-item").text()) + " servings");
      r = new Recipe();
      r.name = d.select(".field-name-title h1").text();
      r.set("ingredients", d.select(".field-name-field-recipe-ingredients p").html().split("<br />"));
      r.set("directions", d.select(".field-name-field-recipe-directions p").html().split("<br />"));
      r.url = url;
      r.prep_time = Integer.parseInt(d.select(".field-name-field-recipe-preptime .field-item").text().replace("^(\\d+).*$", "\\1"));
      r.cook_time = Integer.parseInt(d.select(".field-name-field-recipe-cooktime .field-item").text().replace("^(\\d+).*$", "\\1"));
      r.quantity = Integer.parseInt(d.select(".field-name-field-recipe-servings .field-item").text().replace("^(\\d+).*$", "\\1"));
      Log.w("Cookbox", r.prep_time + ", " + r.cook_time + ", " + r.quantity);
      r.comments = "";
    } catch (Exception e) { e.printStackTrace(); }
    
    Intent i = new Intent(this, RecipeActivity.class);
    i.putExtra("recipe", r);
    startActivity(i);
  }
}