package com.vitaminc4.cookbox;

import java.util.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import com.actionbarsherlock.app.SherlockListActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.ArrayAdapter;
import android.os.Bundle;
import android.util.Log;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import android.content.Intent;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;
import android.widget.SlidingDrawer;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.content.Context;
import android.os.AsyncTask;

public class RecipeListActivity extends SherlockListActivity {
  /** Called when the activity is first created. */
  private RecipeListAdapter listAdapter;
  private SlidingDrawer progress_drawer;
  private ProgressBar download_progress;
  private TextView download_status;
  
  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.cookbox);
    
    Bootstrap.run(getApplicationContext());
    ListView recipe_list = (ListView) findViewById(android.R.id.list);
    progress_drawer = (SlidingDrawer) findViewById(R.id.progress_drawer);
    download_progress = (ProgressBar) findViewById(R.id.download_progress);
    download_status = (TextView) findViewById(R.id.download_status);
    
    if (Dropbox.authenticated()) new DownloaderTask().execute();    
    listAdapter = new RecipeListAdapter(this);
    recipe_list.setAdapter(listAdapter);
  }
  
  @Override public void onResume() {
    super.onResume();
    if (Dropbox.authenticated()) new DownloaderTask().execute();
    listAdapter.refresh();
  }
  
  @Override public void onListItemClick(ListView l, View v, int position, long id) {
    Intent i = new Intent(this, RecipeActivity.class);
    i.putExtra("recipe", (Recipe) l.getItemAtPosition(position));
    startActivity(i);
  }
  
  @Override public boolean onCreateOptionsMenu(Menu m) {
    MenuInflater i = getSupportMenuInflater();
    i.inflate(R.menu.main, m);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem m) {
    switch (m.getItemId()) {
      case R.id.search_menu_item:
        openSearch();
        return true;
      case R.id.settings_menu_item:
        openSettings();
        return true;
    }
    return false;
  }
  
  public void openSearch() {
    SlidingDrawer search_drawer = (SlidingDrawer) findViewById(R.id.search_drawer);
    search_drawer.animateOpen();
  }
  
  public void openSettings() {
    Intent i = new Intent(this, SettingsActivity.class);
    startActivity(i);
  }

  public void doSearch(View view) {
    EditText e = (EditText) findViewById(R.id.search);
    Intent i = new Intent(this, RecipeSearchActivity.class);
    i.putExtra("search", e.getText().toString());
    startActivity(i);
  }
  
  private class DownloaderTask extends AsyncTask<Object, Integer, Boolean> {
    @Override protected Boolean doInBackground(Object... params) {
      int progress = 0;
      publishProgress(-1, progress);
      List<String> changed_files = Dropbox.delta();
      progress++;
      for (String path : changed_files) {
        publishProgress(0, progress, changed_files.size());
        String md = Dropbox.getFile(path);
        LocalCache.writeToFile(path, md);
        progress++;
      }
      return true;
    }

    @Override protected void onProgressUpdate(Integer... progress) {
      switch (progress[0]) {
        case -1:
          download_status.setText("Checking for recipes...");
          break;
        case 0:
          download_status.setText("Downloading recipes...");
          break;
      }
      
      if (progress.length > 2) download_progress.setMax(progress[2]);
      download_progress.setProgress(progress[1]);
    }

    @Override protected void onPreExecute() {
      progress_drawer.open();
    }

    @Override protected void onPostExecute(Boolean o) {
      listAdapter.refresh();
      progress_drawer.close();
    }
  }
}

// List<Recipe> recipes = null;
// try {
//   SAXParserFactory spf = SAXParserFactory.newInstance();
//   SAXParser sp = spf.newSAXParser();
//   XMLReader xr = sp.getXMLReader();
//   ImportHandler h = new ImportHandler();
// 
//   xr.setContentHandler(h);
//   xr.parse(new InputSource(this.getResources().openRawResource(R.raw.my_cookbook)));
//   recipes = h.getParsedData();
// } catch(Exception e) { e.printStackTrace(); }
// DatabaseHandler db = new DatabaseHandler(this);
// Log.d("Cookbox", new Boolean(recipes == null).toString());
// for (Recipe r : recipes) db.addRecipe(r);
// 
// ArrayAdapter<Recipe> a = new RecipeListAdapter(this, recipes);
// this.setListAdapter(a);