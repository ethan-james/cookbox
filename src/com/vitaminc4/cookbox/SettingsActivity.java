package com.vitaminc4.cookbox;

public class SettingsActivity extends android.preference.PreferenceActivity {
  
}

// mDBApi.getSession().startAuthentication(RecipeActivity.this);

// protected void onResume() {
//   super.onResume();
//   if (mDBApi != null && mDBApi.getSession().authenticationSuccessful()) {
//     try {
//         mDBApi.getSession().finishAuthentication();
//         AccessTokenPair tokens = mDBApi.getSession().getAccessTokenPair();
//         
//         SharedPreferences settings = getPreferences(0);
//         SharedPreferences.Editor editor = settings.edit();
//         editor.putString("key", tokens.key);
//         editor.putString("secret", tokens.secret);
//         editor.commit();
//         
//     } catch (IllegalStateException e) {
//         Log.i("DbAuthLog", "Error authenticating", e);
//     }
//   }
// }