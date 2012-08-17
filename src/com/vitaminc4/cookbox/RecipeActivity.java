package com.vitaminc4.cookbox;

import android.os.Bundle;
import android.database.Cursor;
import android.app.Activity;
import android.webkit.WebView;
import android.content.Intent;
import android.util.Log;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnLongClickListener;
import com.commonsware.cwac.anddown.AndDown;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.net.URL;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;

public class RecipeActivity extends SherlockActivity {
  private Recipe recipe;
  
  @Override public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    this.setContentView(R.layout.recipe_view);
    Bootstrap.run(this.getApplicationContext());

    Intent i = this.getIntent();
    if ((recipe = (Recipe) i.getSerializableExtra("recipe")) == null) {
      String key = Intent.ACTION_SEND.equals(i.getAction()) ? Intent.EXTRA_TEXT : "recipe_url";
      try {
        URL url = new URL(i.getStringExtra(key));
        RecipeScraper scraper = Bootstrap.recipeParserManager.find(url);
        recipe = scraper.scrape(url);
        recipe.url = url.toString();
      } catch (Exception e) { e.printStackTrace(); }
    }

    if (recipe != null) {
      WebView recipe_view = (WebView) findViewById(R.id.recipe);
      AndDown a = new AndDown();
      String html = "<html><head><style>body { background-color: black; color: white; } h1 { font-size: 20px; }</style></head><body>";
      html += a.markdownToHtml(recipe.toMarkdown()) + "</body></html>";
      recipe_view.loadData(html, "text/html", null);
    } else {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage("This recipe isn't in a readable format.")
        .setCancelable(true).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
            finish();
          }
      });
      AlertDialog alert = builder.show();
    }
  }
  
  public void store() {
    String path = recipe.slug() + ".md";
    String contents = recipe.toMarkdown();
    LocalCache.writeToFile(path, contents);
    Dropbox.putFile(path, contents);
  }

  @Override public boolean onCreateOptionsMenu(Menu m) {
    MenuInflater i = getSupportMenuInflater();
    i.inflate(R.menu.recipe, m);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem m) {
    switch (m.getItemId()) {
      case R.id.keep_menu_item:
        store();
        return true;
    }
    return false;
  }
}