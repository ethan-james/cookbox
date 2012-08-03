package com.vitaminc4.cookbox;

import android.database.Cursor;
import android.app.Activity;
import android.webkit.WebView;
import android.content.Intent;
import android.util.Log;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnLongClickListener;
import com.commonsware.cwac.anddown.AndDown;

public class RecipeActivity extends Activity {
  @Override public void onStart() {
    super.onStart();
    this.setContentView(R.layout.recipe_view);

    Intent i = this.getIntent();
    Recipe r = (Recipe) i.getSerializableExtra("recipe");
    if (r != null) {
      WebView recipe = (WebView) findViewById(R.id.recipe);
      AndDown a = new AndDown();
      String html = "<html><head><style>body { background-color: black; color: white; } h1 { font-size: 20px; }</style></head><body>";
      html += a.markdownToHtml(r.toMarkdown()) + "</body></html>";
      recipe.loadData(html, "text/html", null);
    }
  }
}