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
import android.content.SharedPreferences;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;

public class RecipeActivity extends SherlockActivity {
  final static private String APP_KEY = "qw5gcw6gry0qj39";
  final static private String APP_SECRET = "4vnujrisulr1fih";
  final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
  DropboxAPI<AndroidAuthSession> mDBApi;
  private Recipe recipe;
  
  @Override public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    AndroidAuthSession session = buildSession();
    mDBApi = new DropboxAPI<AndroidAuthSession>(session);
  }
  
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
    SharedPreferences settings = getPreferences(0);
    String key = settings.getString("key", null);
    String secret = settings.getString("secret", null);
    Log.w("Cookbox", key);
    Log.w("Cookbox", secret);
    
    if (key != null && secret != null) {
      pushToDropbox(new AccessTokenPair(key, secret));
    } else {
      mDBApi.getSession().startAuthentication(RecipeActivity.this);
    }
  }
  
  protected void onResume() {
    super.onResume();
    if (mDBApi != null && mDBApi.getSession().authenticationSuccessful()) {
      try {
          mDBApi.getSession().finishAuthentication();
          AccessTokenPair tokens = mDBApi.getSession().getAccessTokenPair();
          
          SharedPreferences settings = getPreferences(0);
          SharedPreferences.Editor editor = settings.edit();
          editor.putString("key", tokens.key);
          editor.putString("secret", tokens.secret);
          editor.commit();
          
          pushToDropbox(tokens);
      } catch (IllegalStateException e) {
          Log.i("DbAuthLog", "Error authenticating", e);
      }
    }
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
  
  private void pushToDropbox(AccessTokenPair tokens) {
    try {
      String md = recipe.toMarkdown();
      InputStream is = new ByteArrayInputStream(md.getBytes("UTF-8"));
      DropboxAPI.Entry e = mDBApi.putFile("/" + recipe.slug() + ".md", is, md.length(), null, null);
      Log.i("Cookbox", recipe.slug() + " rev " + e.rev + " uploaded to Dropbox");
    } catch (Exception e) { e.printStackTrace(); }
  }
  
  private AndroidAuthSession buildSession() {
    AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
    AndroidAuthSession session;

    String[] stored = getKeys();
    if (stored != null) {
        AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
        session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
    } else {
        session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
    }

    return session;
  }

  private String[] getKeys() {
      SharedPreferences prefs = getPreferences(0);
      String key = prefs.getString("key", null);
      String secret = prefs.getString("secret", null);
      if (key != null && secret != null) {
      	String[] ret = new String[2];
      	ret[0] = key;
      	ret[1] = secret;
      	return ret;
      } else {
      	return null;
      }
  }
}