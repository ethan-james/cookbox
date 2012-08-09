package com.vitaminc4.cookbox;

import java.util.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import com.actionbarsherlock.app.SherlockListActivity;
import android.view.View;
import android.widget.ListView;
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

public class RecipeListActivity extends SherlockListActivity {
  /** Called when the activity is first created. */
  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.cookbox);
    
    Dropbox.initialize(this.getSharedPreferences("dropbox", 0));
    Dropbox.authenticate();
    LocalCache.initialize(getApplicationContext());
    
    ListView recipe_list = (ListView) findViewById(android.R.id.list);
    List<String> changed_files = Dropbox.delta();
    for (String path : changed_files) {
      String md = Dropbox.getFile(path);
      LocalCache.writeToFile(path, md);
    }
    
    recipe_list.setAdapter(new RecipeListAdapter(this));
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
    }
    return false;
  }
  
  public void openSearch() {
    SlidingDrawer search_drawer = (SlidingDrawer) findViewById(R.id.search_drawer);
    search_drawer.animateOpen();
  }

  public void doSearch(View view) {
    EditText e = (EditText) findViewById(R.id.search);
    Intent i = new Intent(this, RecipeSearchActivity.class);
    i.putExtra("search", e.getText().toString());
    startActivity(i);
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