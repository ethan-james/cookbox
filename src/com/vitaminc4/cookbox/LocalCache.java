package com.vitaminc4.cookbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import android.content.Context;
import java.util.List;
import java.util.ArrayList;

public class LocalCache {
  private static Context context;
  
  public static void initialize(Context c) {
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
  
  public static String getFile(String path) {
    FileInputStream file;
    StringBuffer str = new StringBuffer();
    
    try {
      file = context.openFileInput(path);
      DataInputStream data = new DataInputStream(file);
      String line = null;
      if ((line = data.readLine()) != null) str.append(line);

      data.close();
      file.close();
    } catch (Exception e) { e.printStackTrace(); }
    
    return str.toString();
  }
}