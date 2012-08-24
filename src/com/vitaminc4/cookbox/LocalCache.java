package com.vitaminc4.cookbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Scanner;
import java.io.IOException;
import android.content.Context;
import java.util.List;
import java.util.ArrayList;
import android.util.Log;

public class LocalCache {
  private static Context context;
  
  public static void initialize(Context c) {
    if (context != null) return;
    context = c;
  }
  
  public static void writeToFile(String filename, String md) {
    try {
      File dir = context.getDir("recipes", Context.MODE_PRIVATE);
      String path = dir.getAbsolutePath() + "/" + filename;
      File outfile = new File(path);
      FileOutputStream out = new FileOutputStream(outfile);
      out.write(md.getBytes(), 0, md.length());
      out.close();
    } catch (IOException e) { e.printStackTrace(); }
  }
  
  public static List<String> getFileList() {
    File dir = context.getDir("recipes", Context.MODE_PRIVATE);
    List<String> filelist = new ArrayList<String>();
    for (File f : dir.listFiles()) filelist.add(f.getName());
    return filelist;
  }
  
  public static String getFile(String filename) {
    FileInputStream file;
    Scanner scanner = null;
    StringBuilder text = new StringBuilder();
    try {
      StringBuffer str = new StringBuffer();
      File dir = context.getDir("recipes", Context.MODE_PRIVATE);
      String NL = System.getProperty("line.separator");
      scanner = new Scanner(new FileInputStream(dir.getAbsolutePath() + "/" + filename), "utf-8");
      while (scanner.hasNextLine()){
        text.append(scanner.nextLine() + NL);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally{
      if (scanner != null) scanner.close();
    }
    
    return text.toString();
  }
  
  public static void deleteFile(String filename) {
    File dir = context.getDir("recipes", Context.MODE_PRIVATE);
    File file = new File(dir.getAbsolutePath() + "/" + filename);
    file.delete();
    Log.w("Cookbox", "Deleting " + filename);
  }
}