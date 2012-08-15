package com.vitaminc4.cookbox;

import java.lang.reflect.Field;
import java.util.*;
import android.util.Log;
import android.database.Cursor;
import java.io.InputStream;
import android.webkit.WebView;
import java.util.Scanner;
import android.content.Context;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URL;

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

    Matcher m = Pattern.compile("# (.+) #").matcher(md);
    this.name = m.find() ? m.group(1) : "";
    
    m = Pattern.compile("^\\*\\*Prep time:\\*\\* (.+)  $", Pattern.MULTILINE).matcher(md);
    this.prep_time = m.find() ? new Integer(m.group(1)) : 0;
    
    m = Pattern.compile("^\\*\\*Cook time:\\*\\* (.+)  $", Pattern.MULTILINE).matcher(md);
    this.cook_time = m.find() ? new Integer(m.group(1)) : 0;
    
    m = Pattern.compile("^\\*\\*Quantity:\\*\\* (.+)  $", Pattern.MULTILINE).matcher(md);
    this.quantity = m.find() ? new Integer(m.group(1)) : 0;
    
    m = Pattern.compile("(https?://\\S+)", Pattern.MULTILINE).matcher(md);
    this.url = m.find() ? m.group(1) : "";
    
    m = Pattern.compile("^\\*\\*Comments:\\*\\* (.+)", Pattern.MULTILINE).matcher(md);
    this.comments = m.find() ? m.group(1) : "";
    
    Matcher ingredients = Pattern.compile("^\\* (.+)$", Pattern.MULTILINE).matcher(md);
    while (ingredients.find()) this.ingredients.add(ingredients.group(1));
    
    Matcher directions = Pattern.compile("^\\d+\\. (.+)$", Pattern.MULTILINE).matcher(md);
    while (directions.find()) this.directions.add(directions.group(1));
  }
  
  public Recipe(URL url) {

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