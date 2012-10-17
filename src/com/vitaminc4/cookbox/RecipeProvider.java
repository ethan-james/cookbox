package com.vitaminc4.cookbox;

import android.content.UriMatcher;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.net.Uri;
import android.database.Cursor;

public class RecipeProvider extends ContentProvider {
  private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
  
  static {
    sUriMatcher.addURI("com.vitaminc4.cookbox.provider", "recipe/*", 1);
  }
  
  @Override public boolean onCreate() {
    return true;
  }
  
  @Override public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    return null;
  }

  @Override public Uri insert(Uri uri, ContentValues values) {
    return null;
  }
  
  @Override public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    return 0;
  }
  
  @Override public int delete(Uri uri, String selection, String[] selectionArgs) {
    return 0;
  }
  
  @Override public String getType(Uri uri) {
    return null;
  }
}