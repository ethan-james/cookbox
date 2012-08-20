package com.vitaminc4.cookbox;

import java.util.Dictionary;
import java.util.Hashtable;
import java.net.URL;
import android.util.Log;

public class RecipeParserManager {
  static private Hashtable<String, RecipeScraper> siteScrapers;

  public RecipeParserManager() {
    siteScrapers = new Hashtable<String, RecipeScraper>();
    siteScrapers.put("vegweb.com", new VegwebScraper());
    siteScrapers.put("www.101cookbooks.com", new One01cookbooksScraper());
  }
  
  public RecipeScraper find(URL url) {
    String host = url.getHost();
    if (siteScrapers.containsKey(host)) {
      return siteScrapers.get(host);
    } else {
      return null;
    }
  }
}