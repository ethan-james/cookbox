package com.vitaminc4.cookbox;

import android.content.Context;
import android.content.Intent;
import com.orm.androrm.*;
import java.util.List;
import java.util.ArrayList;

public class Bootstrap {
  public static RecipeParserManager recipeParserManager = new RecipeParserManager();
  public static Context context;
  
  public static void run(Context c) {
    context = c;
    Dropbox.authenticate(c);

    List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
    models.add(Recipe.class);
    models.add(Ingredient.class);
    models.add(Food.class);
    models.add(Serving.class);
        
    DatabaseAdapter.setDatabaseName("cookbox");
    DatabaseAdapter adapter = new DatabaseAdapter(context);
    adapter.setModels(models);
  }
}