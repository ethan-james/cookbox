package com.vitaminc4.cookbox;

import android.database.Cursor;
import android.app.Activity;
import android.webkit.WebView;
import android.content.Intent;
import android.util.Log;
import android.content.res.Resources;
import java.io.InputStream;
import java.util.Scanner;
import android.view.View;
import android.view.View.OnLongClickListener;

public class RecipeActivity extends Activity {
  @Override public void onStart() {
    super.onStart();
    this.setContentView(R.layout.recipe_view);

    Intent i = this.getIntent();
    int recipe_id = i.getIntExtra("recipe_id", -1);
    if (recipe_id > 0) {
      try {
        DatabaseHandler dbh = new DatabaseHandler(this);
        Recipe r = dbh.getRecipe(recipe_id);
        InputStream is = getResources().getAssets().open("html/recipe_view.html");
        String html = convertStreamToString(is);
        String ingredients_list = "", directions_list = "";
        WebView recipe = (WebView) findViewById(R.id.recipe);
      
        for (String ingredient : r.ingredients) ingredients_list += "<li>" + ingredient + "</li>";
        for (String direction : r.directions) directions_list += "<li>" + direction + "</li>";
      
        html = html.replace("<% name %>", r.name);
        html = html.replace("<% prep_time %>", Integer.toString(r.prep_time));
        html = html.replace("<% cook_time %>", Integer.toString(r.cook_time));
        html = html.replace("<% quantity %>", Integer.toString(r.quantity));
        html = html.replace("<% url %>", r.url);
        html = html.replace("<% comments %>", r.comments);
        html = html.replace("<% ingredients %>", ingredients_list);
        html = html.replace("<% directions %>", directions_list);

        recipe.loadData(html, "text/html", null);
        recipe.setOnLongClickListener(new OnLongClickListener() {
          @Override public boolean onLongClick(View v) {
            WebView wv = (WebView)v;
            WebView.HitTestResult hr = wv.getHitTestResult();
            Log.w("Cookbox", "getExtra = "+ hr.getExtra() + "\t\t Type=" + hr.getType());
            return true;
          }
        });
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  protected String convertStreamToString(InputStream is) {
    try {
      Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
      if (scanner.hasNext()) return scanner.next();
      return "";
    } catch (java.util.NoSuchElementException e) {
      return "";
    }
  }
}