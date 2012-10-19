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
import java.io.*;
import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.net.URL;
import android.text.Html;
import android.text.Html.TagHandler;
import android.text.Editable;
import org.xml.sax.XMLReader;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;
import org.apache.commons.lang3.StringUtils;

public class RecipeActivity extends SherlockActivity {
  private Recipe recipe;
  private int recipe_id;
  
  @Override public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    this.setContentView(R.layout.recipe_view);
    Bootstrap.run(this.getApplicationContext());

    Intent i = this.getIntent();
    if ((recipe_id = i.getIntExtra("recipe_id", -1)) == -1) {
      String key = Intent.ACTION_SEND.equals(i.getAction()) ? Intent.EXTRA_TEXT : "recipe_url";
      try {
        URL url = new URL(i.getStringExtra(key));
        RecipeScraper scraper = Bootstrap.recipeParserManager.find(url);
        recipe = scraper.scrape(url);
        recipe.url.set(url.toString());
      } catch (Exception e) { e.printStackTrace(); }
    } else {
      recipe = Recipe.objects(Bootstrap.context).get(recipe_id);
    }

    if (recipe != null) {
      WebView recipe_view = (WebView) findViewById(R.id.recipe);
      AndDown a = new AndDown();
      String html = "<html><head><style>body { background-color: black; color: white; } h1 { font-size: 20px; }</style></head><body>";
      html += a.markdownToHtml(recipe.markdown.get()) + "</body></html>";
      recipe_view.setBackgroundColor(0);
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
    recipe.save(Bootstrap.context);
    Dropbox.putFile(recipe.slug() + ".md", recipe.markdown.get());
  }
  
  public void share() {
    Intent i = new Intent(android.content.Intent.ACTION_SEND);
    AndDown a = new AndDown();
    
    i.setType("text/html");
    i.putExtra(android.content.Intent.EXTRA_SUBJECT, recipe.name.get());
    i.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(a.markdownToHtml(recipe.markdown.get()), null, new ListTagHandler()));
    startActivity(Intent.createChooser(i, "Share via"));
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
      case R.id.share_menu_item:
        share();
        return true;
    }
    return false;
  }
  
  public class ListTagHandler implements TagHandler {
    public void handleTag (boolean opening, String tag, Editable output, XMLReader xmlReader) {
      if (tag.equalsIgnoreCase("li")) {
         if (opening) output.append("\u2022 ");
         else output.append("\n");
      }
    }
  }
}