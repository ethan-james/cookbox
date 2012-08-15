package com.vitaminc4.cookbox;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

public class VegwebScraper extends RecipeScraper {
  public Recipe scrape(String html) {
    Recipe r = new Recipe();
    Document d = Jsoup.parse(html);
    r.name = d.select(".field-name-title h1").text();
    r.set("ingredients", d.select(".field-name-field-recipe-ingredients p").html().split("<br />"));
    r.set("directions", d.select(".field-name-field-recipe-directions p").html().split("<br />"));
    // r.url = url;
    r.prep_time = Integer.parseInt(d.select(".field-name-field-recipe-preptime .field-item").text().replace("^(\\d+).*$", "\\1"));
    r.cook_time = Integer.parseInt(d.select(".field-name-field-recipe-cooktime .field-item").text().replace("^(\\d+).*$", "\\1"));
    r.quantity = Integer.parseInt(d.select(".field-name-field-recipe-servings .field-item").text().replace("^(\\d+).*$", "\\1"));
    r.comments = "";
    return r;
  }
}