package com.vitaminc4.cookbox;

import com.actionbarsherlock.app.SherlockListActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.ListView;
import java.net.*;
import java.io.*;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
}