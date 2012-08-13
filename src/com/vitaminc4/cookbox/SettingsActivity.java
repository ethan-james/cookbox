package com.vitaminc4.cookbox;

import android.os.Bundle;
import android.util.Log;
import android.preference.*;
import android.content.SharedPreferences;

public class SettingsActivity extends android.preference.PreferenceActivity {
  @Override protected void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    addPreferencesFromResource(R.menu.preferences);
  }
  
  @Override public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
    if (preference.getKey().equals("dropbox")) linkDropbox();
    return false;
  }
  
  public void linkDropbox() {
    Dropbox.startAuthentication(SettingsActivity.this);
  }

  protected void onResume() {
    super.onResume();
    if (Dropbox.authenticationSuccessful()) {
      try {
        String tokens = Dropbox.finishAuthentication();
        SharedPreferences.Editor editor = getPreferenceManager().findPreference("dropbox").getEditor();
        editor.putString("dropbox", tokens);
        editor.commit();
      } catch (IllegalStateException e) {
        Log.i("DbAuthLog", "Error authenticating", e);
      }
    }
  }
}