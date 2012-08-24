package com.vitaminc4.cookbox;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;
import android.content.SharedPreferences;
import android.content.Context;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import android.util.Log;
import java.util.List;
import java.util.ArrayList;
import android.preference.PreferenceManager;

public class Dropbox {
  final static private String APP_KEY = "qw5gcw6gry0qj39";
  final static private String APP_SECRET = "4vnujrisulr1fih";
  final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
  final static private int delta_refresh_window = 600;
  private static Context context;
  private static SharedPreferences preferences;

  private static int last_delta = 0;
  private static DropboxAPI<AndroidAuthSession> mDBApi = null;
  
  public static boolean authenticate(Context c) {
    if (mDBApi != null) return true;
    context = c;
    preferences = PreferenceManager.getDefaultSharedPreferences(context);
    String[] tokens = preferences.getString("dropbox", "").split(":");
    String key = tokens.length == 2 ? tokens[0] : null;
    String secret = tokens.length == 2 ? tokens[1] : null;
    AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
    AccessTokenPair accessToken = (key != null && secret != null) ? new AccessTokenPair(key, secret) : null;
    AndroidAuthSession session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
    mDBApi = new DropboxAPI<AndroidAuthSession>(session);  
  
    return (accessToken != null);
  }
  
  public static boolean authenticated() {
    return preferences != null && preferences.getString("dropbox", null) != null;
  }
  
  public static void startAuthentication(Context c) {
    mDBApi.getSession().startAuthentication(c);
  }
  
  public static boolean authenticationSuccessful() {
    return mDBApi != null && mDBApi.getSession().authenticationSuccessful();
  }
  
  public static String finishAuthentication() {
    mDBApi.getSession().finishAuthentication();
    AccessTokenPair token = mDBApi.getSession().getAccessTokenPair();
    return token.key + ":" + token.secret;
  }
  
  public static boolean putFile(String filename, String contents) {
    try {
      InputStream is = new ByteArrayInputStream(contents.getBytes("UTF-8"));
      DropboxAPI.Entry e = mDBApi.putFileOverwrite(filename, is, contents.length(), null);
      Log.i("Cookbox", filename + " rev " + e.rev + " uploaded to Dropbox");
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  
  public static List<String> delta() {
    SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
    String cursor = p.getString("cursor", null);
    List<String> files = new ArrayList<String>();
    DropboxAPI.DeltaPage page;

    try {
      do {
        page = mDBApi.delta(cursor);
        cursor = page.cursor;
        for (DropboxAPI.DeltaEntry e : (List<DropboxAPI.DeltaEntry>) page.entries) {
          if (e.metadata == null) LocalCache.deleteFile(e.lcPath);
          else files.add(e.lcPath);
        }
      } while (page.hasMore);

      SharedPreferences.Editor edit = p.edit();
      edit.putString("cursor", cursor);
      edit.commit();
      
      return files;
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<String>();
    }
  }
  
  public static String getFile(String path) {    
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      DropboxAPI.DropboxFileInfo info = mDBApi.getFile(path, null, out, null);
      return out.toString("utf-8");
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}

