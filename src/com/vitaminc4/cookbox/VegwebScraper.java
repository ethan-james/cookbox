package com.vitaminc4.cookbox;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import android.util.Log;

public class VegwebScraper extends RecipeScraper {
  public Recipe scrape(String html) {
    Recipe r = new Recipe();
    Document d = Jsoup.parse(html);
    r.name = d.select(".field-name-title h1").text();
    Log.w("Cookbox", r.name);
    r.set("ingredients", d.select(".field-name-field-recipe-ingredients p").html().split("<br />"));
    Log.w("Cookbox", r.ingredients.toString());
    r.set("directions", d.select(".field-name-field-recipe-directions p").html().split("<br />"));
    // r.url = url;
    Log.w("Cookbox", r.directions.toString());
    
    r.prep_time = d.select(".field-name-field-recipe-preptime .field-item").text();
    Log.w("Cookbox", r.prep_time);
    r.cook_time = d.select(".field-name-field-recipe-cooktime .field-item").text();
    Log.w("Cookbox", r.cook_time);
    r.quantity = d.select(".field-name-field-recipe-servings .field-item").text();
    Log.w("Cookbox", r.quantity);
    r.comments = "";
    return r;
  }
}