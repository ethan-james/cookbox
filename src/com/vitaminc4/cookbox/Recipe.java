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
import com.orm.androrm.*;
import org.apache.commons.lang3.StringUtils;

class ListField extends CharField {
  public String[] split() {
    return StringUtils.split("\n* ");
  }

  public void add(String line) {
    String current = get();
    if (current != null && current.length() == 0) set(line);
    else set(get() + "\n* " + line);
  }

  public void set(String[] list) {
    set(StringUtils.join(list, "\n* "));
  }
}

class Ingredient extends Model {
  public ForeignKeyField<Recipe> recipe;
  public ForeignKeyField<Serving> serving;
  public CharField text = new CharField();
  public CharField canonical = new CharField();

  public Ingredient() {
    super();
    recipe = new ForeignKeyField<Recipe>(Recipe.class);
    serving = new ForeignKeyField<Serving>(Serving.class);
  }

  public static Ingredient make(String text) {
    Ingredient i = new Ingredient();
    Serving s = Serving.search(text);

    i.text.set(text);
    if (s != null) i.serving.set(s);
    return i;
  }

  public static Ingredient create(String text) {
    Ingredient i = Ingredient.make(text);
    i.save(Bootstrap.context);
    return i;
  }
}

public class Recipe extends Model {
  public CharField name = new CharField();
  public CharField prep_time = new CharField();
  public CharField cook_time = new CharField();
  public OneToManyField<Recipe, Ingredient> ingredients;
  public ListField directions = new ListField();
  public CharField url = new CharField();
  public CharField quantity = new CharField();
  public CharField comments = new CharField();
  public CharField markdown = new CharField();
  
  public Recipe() {
    super();
    ingredients = new OneToManyField<Recipe, Ingredient>(Recipe.class, Ingredient.class);
  }
  
  public Recipe(String md) {
    this();

    this.markdown.set(md);

    Matcher m = Pattern.compile("# (.+) #").matcher(md);
    this.name.set(m.find() ? m.group(1) : "");
    
    m = Pattern.compile("^\\*\\*Prep time:\\*\\* (.+)  $", Pattern.MULTILINE).matcher(md);
    this.prep_time.set(m.find() ? m.group(1) : "");
    
    m = Pattern.compile("^\\*\\*Cook time:\\*\\* (.+)  $", Pattern.MULTILINE).matcher(md);
    this.cook_time.set(m.find() ? m.group(1) : "");
    
    m = Pattern.compile("^\\*\\*Quantity:\\*\\* (.+)  $", Pattern.MULTILINE).matcher(md);
    this.quantity.set(m.find() ? m.group(1) : "");
    
    m = Pattern.compile("(https?://\\S+)", Pattern.MULTILINE).matcher(md);
    this.url.set(m.find() ? m.group(1) : "");
    
    m = Pattern.compile("^\\*\\*Comments:\\*\\* (.+)", Pattern.MULTILINE).matcher(md);
    this.comments.set(m.find() ? m.group(1) : "");
    
    Matcher ingredients = Pattern.compile("^\\* (.+)$", Pattern.MULTILINE).matcher(md);
    while (ingredients.find()) this.ingredients.add(Ingredient.create(ingredients.group(1)));
    
    Matcher directions = Pattern.compile("^\\d+\\. (.+)$", Pattern.MULTILINE).matcher(md);
    while (directions.find()) this.directions.add(directions.group(1));
  }
  
  public void set(String field, String value) throws java.lang.NoSuchFieldException, IllegalAccessException {
    if (field == "title") this.name.set(value);
    else if (field == "preptime") this.prep_time.set(value);
    else if (field == "cooktime") this.cook_time.set(value);
    // else if (field == "imageurl") this.image_url.set(value);
    else if (field == "quantity") this.quantity.set(value);
    else {
        Class<?> c = this.getClass();
        Field f = c.getDeclaredField(field);
        f.set(this, value);
    }
  }
  
  public void set(String field, String[] values) {
    if (field == "ingredients") {
      for (String ingredient : values) ingredients.add(Ingredient.make(ingredient));
    } else if (field == "directions") this.directions.set(values);
  }
  
  public void add(String field, String value) {
    if (field == "ingredient") ingredients.add(Ingredient.make(value));
    else if (field == "recipetext" || field == "direction") this.directions.add(value);
  }
  
  public String toMarkdown() {
    String ingredients_list = "", directions_list = "";  
    String md = "# " + this.name + " #\n\n";
    
    md += "**Ingredients**  \n\n";
    for (Ingredient ingredient : this.ingredients.get(Bootstrap.context, this)) md += "* " + ingredient.text.get() + "\n";
    
    md += "\n**Directions**  \n\n";
    for (String direction : this.directions.split()) md += "1. " + direction + "\n";
    
    md += "\n";
    md += "**Prep time:** " + this.prep_time.get() + "  \n";
    md += "**Cook time:** " + this.cook_time.get() + "  \n";
    md += "**Quantity:** " + this.quantity.get() + "  \n";
    md += this.url + "\n\n";
    md += "**Comments:** " + this.comments.get();
    
    return md;
  }
  
  public String toString() {
    return this.name.get() + ": " + this.url.get();
  }
  
  public String slug() {
    return this.name.get().toLowerCase().replaceAll("[^a-z0-9]+", "-");
  }

  public static final QuerySet<Recipe> objects(Context context) {
    return objects(context, Recipe.class);
  }
}