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
    r.name.set(d.select(".field-name-title h1").text());
    r.set("ingredients", d.select(".field-name-field-recipe-ingredients p").html().split("<br />"));
    r.set("directions", d.select(".field-name-field-recipe-directions p").html().split("<br />"));
    // r.url = url;
    
    r.prep_time.set(d.select(".field-name-field-recipe-preptime .field-item").text());
    r.cook_time.set(d.select(".field-name-field-recipe-cooktime .field-item").text());
    r.quantity.set(d.select(".field-name-field-recipe-servings .field-item").text());
    r.comments.set("");
    return r;
  }
}