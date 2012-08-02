package com.vitaminc4.cookbox;

import java.lang.reflect.Field;
import java.util.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.util.Log;
import android.database.Cursor;
 
public class Recipe {
  
  public String name;
  public int prep_time;
  public int cook_time;
  public List<String> ingredients;
  public List<String> directions;
  public String url;
  public String image_url;
  public int quantity;
  public String comments;
  
  public Recipe() {
    this.ingredients = new ArrayList<String>();
    this.directions = new ArrayList<String>();
  }
  
  public Recipe(Cursor cr, Cursor ci, Cursor cd) {
    int name_idx = cr.getColumnIndex("name");
    int prep_idx = cr.getColumnIndex("prep_time");
    int cook_idx = cr.getColumnIndex("cook_time");
    int url_idx = cr.getColumnIndex("url");
    int img_url_idx = cr.getColumnIndex("image_url");
    int qty_idx = cr.getColumnIndex("quantity");
    int comments_idx = cr.getColumnIndex("comments");
    int ingredient_step_idx = ci.getColumnIndex("step");
    int direction_step_idx = cd.getColumnIndex("step");
    
    cr.moveToFirst();
    this.name = cr.getString(name_idx);
    this.prep_time = cr.getInt(prep_idx);
    this.cook_time = cr.getInt(cook_idx);
    this.url = cr.getString(url_idx);
    this.image_url = cr.getString(img_url_idx);
    this.quantity = cr.getInt(qty_idx);
    this.comments = cr.getString(comments_idx);
    
    this.ingredients = new ArrayList<String>();
    while(ci.moveToNext()) this.add("ingredient", ci.getString(ingredient_step_idx));
    
    this.directions = new ArrayList<String>();
    while(cd.moveToNext()) this.add("recipetext", cd.getString(direction_step_idx));
  }
  
  public void set(String field, String value) throws NoSuchFieldException, IllegalAccessException {
    if (field == "title") this.name = value;
    else if (field == "preptime") this.prep_time = Integer.parseInt(value);
    else if (field == "cooktime") this.cook_time = Integer.parseInt(value);
    else if (field == "imageurl") this.image_url = value;
    else if (field == "quantity") this.quantity = Integer.parseInt(value);
    else {
        Class<?> c = this.getClass();
        Field f = c.getDeclaredField(field);
        f.set(this, value);
    }
  }
  
  public void add(String field, String value) {
    if (field == "ingredient") this.ingredients.add(value);
    else if (field == "recipetext") this.directions.add(value);
  }
  
  public String toString() {
    return this.name + ": " + this.url;
  }
}