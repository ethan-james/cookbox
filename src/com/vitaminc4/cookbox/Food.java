package com.vitaminc4.cookbox;

import android.content.Context;
import android.util.Log;
import com.orm.androrm.*;
import org.json.*;
import org.apache.commons.lang3.StringUtils;

public class Food extends Model {
  public IntegerField food_id = new IntegerField(); // FatSecret food id
  public CharField food_name = new CharField();
  public CharField food_type = new CharField();
  public CharField brand_name = new CharField();
  public CharField food_url = new CharField();
  public OneToManyField<Food, Serving> servings;

  public static String[] serving_words = {
    "stalks",
    "stalk",
    "canisters",
    "canister",
    "cans",
    "can",
    "cubed",
    "cube",
    "splash",
    "pinch",
    "to",
    "lbs",
    "lb",
    "^a ",
    " a ",
    "dash",
    " or ",
    " of ",
    "heads",
    "head",
    "quarts",
    "quart",
    "handfuls",
    "handful",
    "packages",
    "package",
    "boxes",
    "box",
    "pounds",
    "pound",
    "cups",
    "cup",
    "tablespoons",
    "tablespoon",
    "teaspoons",
    "teaspoon",
    "ounces",
    "ounce",
    "oz",
    "ml",
    "bunch",
    "bunches",
    "small",
    "medium",
    "large",
    "big"
  };

  public Food() {
    super();
    servings = new OneToManyField<Food, Serving>(Food.class, Serving.class);
  }

  public Food(JSONObject food) {
    this();

    try {
      food_name.set(food.getString("food_name"));
      food_type.set(food.optString("food_type"));
      brand_name.set(food.optString("brand_name"));
      food_url.set(food.optString("food_url"));

      JSONObject serving_obj = food.getJSONObject("servings").optJSONObject("serving");
      JSONArray serving_array = food.getJSONObject("servings").optJSONArray("serving");
      JSONObject serving = null;

      if (serving_obj != null) serving = serving_obj;
      else if (serving_array != null && serving_array.length() > 0) serving = serving_array.optJSONObject(0);

      Serving s = new Serving(serving, this);
      s.save(Bootstrap.context);
      servings.add(s);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public static Food search(String text) {
    try {
      JSONObject json = FatSecret.getFood(text);

      if (json == null) {
        return null;
      } else {
        JSONObject food_obj = json.optJSONObject("food");
        int food_id = (food_obj == null) ? json.optInt("food_id") : food_obj.optInt("food_id");
        Food food = (food_id == 0) ? null : Food.objects(Bootstrap.context).get(food_id);

        if (food == null) {
          food = new Food(json.getJSONObject("food"));
          food.save(Bootstrap.context);
        }

        return food;
      }
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static final QuerySet<Food> objects(Context context) {
    return objects(context, Food.class);
  }

  public static String canonize(String text) {
    String canonical = text.trim().replaceFirst("^\\d+[-/0-9\\u00BC\\u00BD\\u00BE ]*", "").trim();
    canonical = canonical.replaceAll("\\([^()]+\\)", "").trim();
    canonical = canonical.replaceAll(",[-a-zA-Z0-9 /.,"]+$", "").trim();
    canonical = canonical.replaceFirst("^(" + StringUtils.join(Food.serving_words, "|") + ")", "").trim();
    Log.w("Cookbox", canonical);
    return canonical;
  }
}

class Serving extends Model {
  public ForeignKeyField<Food> food;
  public IntegerField serving_id = new IntegerField();
  public CharField serving_description = new CharField();
  public CharField serving_url = new CharField();
  public DoubleField metric_serving_amount = new DoubleField();
  public CharField metric_serving_unit = new CharField();
  public DoubleField number_of_units = new DoubleField();
  public CharField measurement_description = new CharField();
  public DoubleField calories = new DoubleField();
  public DoubleField carbohydrate = new DoubleField();
  public DoubleField protein = new DoubleField();
  public DoubleField fat = new DoubleField();
  public DoubleField saturated_fat = new DoubleField();
  public DoubleField polyunsaturated_fat = new DoubleField();
  public DoubleField monounsaturated_fat = new DoubleField();
  public DoubleField trans_fat = new DoubleField();
  public DoubleField cholesterol = new DoubleField();
  public DoubleField sodium = new DoubleField();
  public DoubleField potassium = new DoubleField();
  public DoubleField fiber = new DoubleField();
  public DoubleField sugar = new DoubleField();
  public IntegerField vitamin_a = new IntegerField();
  public IntegerField vitamin_c = new IntegerField();
  public IntegerField calcium = new IntegerField();
  public IntegerField iron = new IntegerField();

  public Serving() {
    super();
    food = new ForeignKeyField<Food>(Food.class);
  }

  public Serving(JSONObject s, Food f) {
    this();
    serving_id.set(s.optInt("serving_id"));
    serving_description.set(s.optString("serving_description"));
    serving_url.set(s.optString("serving_url"));
    metric_serving_amount.set(s.optDouble("metric_serving_amount"));
    metric_serving_unit.set(s.optString("metric_serving_unit"));
    number_of_units.set(s.optDouble("number_of_units"));
    measurement_description.set(s.optString("measurement_description"));
    calories.set(s.optDouble("calories"));
    carbohydrate.set(s.optDouble("carbohydrate"));
    protein.set(s.optDouble("protein"));
    fat.set(s.optDouble("fat"));
    saturated_fat.set(s.optDouble("saturated_fat"));
    polyunsaturated_fat.set(s.optDouble("polyunsaturated_fat"));
    monounsaturated_fat.set(s.optDouble("monounsaturated_fat"));
    trans_fat.set(s.optDouble("trans_fat"));
    cholesterol.set(s.optDouble("cholesterol"));
    sodium.set(s.optDouble("sodium"));
    potassium.set(s.optDouble("potassium"));
    fiber.set(s.optDouble("fiber"));
    sugar.set(s.optDouble("sugar"));
    vitamin_a.set(s.optInt("vitamin_a"));
    vitamin_c.set(s.optInt("vitamin_c"));
    calcium.set(s.optInt("calcium"));
    iron.set(s.optInt("iron"));
    food.set(f);
  }

  public static Serving search(String text) {
    String canonical = Food.canonize(text);
    Food food = Food.search(canonical);
    QuerySet<Serving> servings = (food == null) ? null : food.servings.get(Bootstrap.context, food);
    return (food == null || servings.count() == 0) ? null : servings.toList().get(0);
  }

  public static final QuerySet<Serving> objects(Context context) {
    return objects(context, Serving.class);
  }
}