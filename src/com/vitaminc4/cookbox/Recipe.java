package com.vitaminc4.cookbox;

import java.lang.reflect.Field;
import java.util.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.util.Log;
import android.database.Cursor;
import java.io.InputStream;
import android.webkit.WebView;
import java.util.Scanner;
import android.content.Context;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Recipe implements Serializable {
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
  
  public Recipe(String md) {
    this.ingredients = new ArrayList<String>();
    this.directions = new ArrayList<String>();
    Log.w("Cookbox", md);
    this.name = Pattern.compile("# (.+) #").matcher(md).group(1);
    this.prep_time = new Integer(Pattern.compile("\\*\\*Prep time:\\*\\* (.+)  $").matcher(md).group(1));
    this.cook_time = new Integer(Pattern.compile("\\*\\*Cook time:\\*\\* (.+)  $").matcher(md).group(1));
    this.quantity = new Integer(Pattern.compile("\\*\\*Quantity:\\*\\* (.+)  $").matcher(md).group(1));
    this.url = Pattern.compile("(https?://\\S+)").matcher(md).group(1);
    this.comments = Pattern.compile("\\*\\*Comments:\\*\\* (.+)").matcher(md).group(1);
    
    Matcher ingredients = Pattern.compile("^\\* (.+)$").matcher(md);
    while (ingredients.find()) this.ingredients.add(ingredients.group(1));
    
    Matcher directions = Pattern.compile("^\\d+\\. (.+)$").matcher(md);
    while (directions.find()) this.directions.add(directions.group(1));
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
  
  public void set(String field, String[] values) {
    if (field == "ingredients") this.ingredients = Arrays.asList(values);
    else if (field == "directions") this.directions = Arrays.asList(values);
  }
  
  public void add(String field, String value) {
    if (field == "ingredient") this.ingredients.add(value);
    else if (field == "recipetext") this.directions.add(value);
  }
  
  public String toMarkdown() {
    String ingredients_list = "", directions_list = "";  
    String md = "# " + this.name + " #\n\n";
    
    md += "**Ingredients**  \n\n";
    
    for (String ingredient : this.ingredients) md += "* " + ingredient + "\n";
    
    md += "\n**Directions**  \n\n";
    for (String direction : this.directions) md += "1. " + direction + "\n";
    
    md += "\n";
    md += "**Prep time:** " + this.prep_time + "  \n";
    md += "**Cook time:** " + this.cook_time + "  \n";
    md += "**Quantity:** " + this.quantity + "  \n";
    md += this.url + "\n\n";
    md += "**Comments:** " + this.comments;
    
    return md;
  }
  
  public String toString() {
    return this.name + ": " + this.url;
  }
  
  public String slug() {
    return this.name.toLowerCase().replaceAll("[^a-z0-9]+", "-");
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