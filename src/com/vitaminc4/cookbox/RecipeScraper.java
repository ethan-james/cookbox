package com.vitaminc4.cookbox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.apache.commons.lang3.*;
import java.util.*;
import android.util.Log;
import java.util.regex.*;

public class RecipeScraper {
  public Hashtable<String, String> attributes = new Hashtable<String, String>();

  public Recipe scrape(URL url) {
    try {
      attributes.put("url", url.toString());
      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
      String inputLine;
      StringBuilder html = new StringBuilder();
      StringBuffer finished = new StringBuffer();

      while ((inputLine = in.readLine()) != null) html.append(inputLine + "\n");
      in.close();

      String template = LocalCache.getAsset("scrapers/" + url.getHost().replaceFirst("^www\\.", "") + ".mdown");
      Pattern p = Pattern.compile("<%(.*?)%>");
      Matcher m = p.matcher(template);
      Document d = Jsoup.parse(html.toString());

      while (m.find()) {
        String match = m.group(1);
        if (match.charAt(0) == '=') {
          match = match.substring(1).trim();
          String[] match_parts = StringUtils.stripAll(match.split("\\+"));
          String selector_pattern = "^\\$\\([\'\"](.*)?[\'\"]\\)$";
          String quote_pattern = "^[\'\"](.*)?[\'\"]$";
          String selector = null;
          int selector_index = -1;

          for (int i = 0; i < match_parts.length; i++) {
            if (match_parts[i].matches(selector_pattern)) {
              selector = match_parts[i].replaceFirst(selector_pattern, "$1");
              selector_index = i;
            } else if (match_parts[i].matches(quote_pattern)) {
              match_parts[i] = match_parts[i].replaceFirst(quote_pattern, "$1");
            } else if (attributes.containsKey(match_parts[i])) {
              match_parts[i] = attributes.get(match_parts[i]);
            }
          }

          List<String> replacements = new ArrayList<String>();
          if (selector != null) {
            for (Element e : d.select(selector)) {
              for (String s : e.html().split("<br />")) {
                String[] replaced_match_parts = ArrayUtils.clone(match_parts);
                replaced_match_parts[selector_index] = Jsoup.parse(s).text();
                replacements.add(StringUtils.join(replaced_match_parts));
              }
            }
          } else replacements.add(StringUtils.join(match_parts));
          m.appendReplacement(finished, StringUtils.join(replacements, "\n"));
        }
      }
      m.appendTail(finished);

      return new Recipe(finished.toString());
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}