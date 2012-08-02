package com.vitaminc4.cookbox;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.util.Log;
import android.database.Cursor;

public class DatabaseHandler extends SQLiteOpenHelper {
  private static final int DATABASE_VERSION = 1;

  private static final String DATABASE_NAME = "cookbox";
  private static final String TABLE_RECIPE = "recipe";
  private static final String TABLE_INGREDIENT = "ingredient";
  private static final String TABLE_DIRECTION = "direction";
  private static final String KEY_ID = "_id";

  public DatabaseHandler(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override public void onCreate(SQLiteDatabase db) {
    String CREATE_RECIPE_TABLE = "CREATE TABLE " + TABLE_RECIPE + "("
            + KEY_ID + " INTEGER PRIMARY KEY, name VARCHAR(64), prep_time INTEGER, cook_time INTEGER, url VARCHAR(256),"
            + "image_url VARCHAR(256), quantity INTEGER, comments TEXT)";
    String CREATE_INGREDIENT_TABLE = "CREATE TABLE " + TABLE_INGREDIENT + "("
            + KEY_ID + " INTEGER PRIMARY KEY, recipe_id INTEGER, step TEXT)";
    String CREATE_DIRECTION_TABLE = "CREATE TABLE " + TABLE_DIRECTION + "("
            + KEY_ID + " INTEGER PRIMARY KEY, recipe_id INTEGER, step TEXT)";
    db.execSQL(CREATE_RECIPE_TABLE);
    db.execSQL(CREATE_INGREDIENT_TABLE);
    db.execSQL(CREATE_DIRECTION_TABLE);
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENT);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIRECTION);
    onCreate(db);
  }
  
  public Recipe getRecipe(int id) {
    SQLiteDatabase db = this.getWritableDatabase();
    Cursor cr = db.query(TABLE_RECIPE, null, "_id = " + Integer.toString(id), null, null, null, null);
    Cursor ci = db.query(TABLE_INGREDIENT, null, "recipe_id = " + Integer.toString(id), null, null, null, null);
    Cursor cd = db.query(TABLE_DIRECTION, null, "recipe_id = " + Integer.toString(id), null, null, null, null);
    return new Recipe(cr, ci, cd);
  }
  
  public Cursor getRecipes() {
    SQLiteDatabase db = this.getWritableDatabase();
    return db.query(TABLE_RECIPE, null, null, null, null, null, null);
  }
  
  public void addRecipe(Recipe r) {
    SQLiteDatabase db = this.getWritableDatabase();
 
    ContentValues values = new ContentValues();
    values.put("name", r.name);
    values.put("prep_time", r.prep_time);
    values.put("cook_time", r.cook_time);
    values.put("url", r.url);
    values.put("image_url", r.image_url);
    values.put("quantity", r.quantity);
    values.put("comments", r.comments);
 
    int recipe_id = (int)db.insert(TABLE_RECIPE, null, values);
    db.close();
    
    for (String ingredient : r.ingredients) {
      if (ingredient != "") this.addIngredient(recipe_id, ingredient);
    }
    for (String direction : r.directions) {
      if (direction != "") this.addDirection(recipe_id, direction);
    }
  }
  
  public void addIngredient(int recipe_id, String step) {
    SQLiteDatabase db = this.getWritableDatabase();
 
    ContentValues values = new ContentValues();
    values.put("recipe_id", recipe_id);
    values.put("step", step);
 
    db.insert(TABLE_INGREDIENT, null, values);
    db.close();
  }
  
  public void addDirection(int recipe_id, String step) {
    SQLiteDatabase db = this.getWritableDatabase();
 
    ContentValues values = new ContentValues();
    values.put("recipe_id", recipe_id);
    values.put("step", step);
 
    db.insert(TABLE_DIRECTION, null, values);
    db.close();
  }
}