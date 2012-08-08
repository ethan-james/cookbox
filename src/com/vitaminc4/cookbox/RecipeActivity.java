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

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;

public class RecipeActivity extends SherlockActivity {
  private Recipe recipe;
  
  @Override public void onStart() {
    super.onStart();
    this.setContentView(R.layout.recipe_view);

    Intent i = this.getIntent();
    recipe = (Recipe) i.getSerializableExtra("recipe");
    
    if (recipe != null) {
      WebView recipe_view = (WebView) findViewById(R.id.recipe);
      AndDown a = new AndDown();
      String html = "<html><head><style>body { background-color: black; color: white; } h1 { font-size: 20px; }</style></head><body>";
      html += a.markdownToHtml(recipe.toMarkdown()) + "</body></html>";
      recipe_view.loadData(html, "text/html", null);
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