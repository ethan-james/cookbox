package com.vitaminc4.cookbox;

import android.content.Context;
import android.content.Intent;

public class Bootstrap {
  public static RecipeParserManager recipeParserManager = new RecipeParserManager();
  
  public static void run(Context c) {
    Dropbox.authenticate(c);
    LocalCache.initialize(c);
  }
}