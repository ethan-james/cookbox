package com.vitaminc4.cookbox;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import android.util.Log;

public class One01cookbooksScraper extends RecipeScraper {
  public Recipe scrape(String html) {
    Recipe r = new Recipe();
    Document d = Jsoup.parse(html);
    Element recipe = d.select("#recipe").first();
    Element ingredients = recipe.select("blockquote").first();

    for (Element e : ingredients.select("p")) {
      for (String line : e.html().split("<br />")) r.add("ingredient", line);
    }
    Element e = ingredients.nextElementSibling();
    while (e != null && e.tagName() == "p") {
      if (e.text().equals(e.select("i").text())) r.quantity = e.text();
      else for (String line : e.html().split("<br />")) r.add("direction", line);
      e = e.nextElementSibling();
    }
    r.name = d.select("#recipe h1").text();
    r.prep_time = d.select("#recipe .recipetimes .preptime").text();
    r.cook_time = d.select("#recipe .recipetimes .cooktime").text();
    r.comments = "";
    return r;
  }
}