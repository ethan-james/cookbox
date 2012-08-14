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
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class RecipeSearchActivity extends SherlockListActivity {
  @Override public void onStart() {
    super.onStart();
    this.setContentView(R.layout.cookbox);

    Intent i = this.getIntent();
    ListView recipe_list = (ListView) findViewById(android.R.id.list);
    Document doc = null;

    try {
      Connection c = Jsoup.connect("http://vegweb.com/search/vegweb/" + i.getStringExtra("search")).timeout(30000);
      doc = c.get();
    } catch (Exception e) { e.printStackTrace(); }

    if (doc != null) recipe_list.setAdapter(new RecipeSearchAdapter(this, doc));
    else {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage("Your search has timed out.")
        .setCancelable(true).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
            finish();
          }
      });
      AlertDialog alert = builder.show();
    }
  }

  @Override public void onListItemClick(ListView l, View v, int position, long id) {
    Recipe r = null;
    try {
      r = new Recipe(new URL("http://vegweb.com" + (String) l.getItemAtPosition(position)));
    } catch (Exception e) { e.printStackTrace(); }
    
    Intent i = new Intent(this, RecipeActivity.class);
    i.putExtra("recipe", r);
    startActivity(i);
  }
}