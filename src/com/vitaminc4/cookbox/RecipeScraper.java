package com.vitaminc4.cookbox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import android.util.Log;

public abstract class RecipeScraper {
  public abstract Recipe scrape(String html);
  
  public Recipe scrape(URL url) {
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
      String inputLine, html = "";

      while ((inputLine = in.readLine()) != null) html += inputLine;
      in.close();
    
      return this.scrape(html);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}