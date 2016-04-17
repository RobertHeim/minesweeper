package de.robert_heim.minesweeper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * Stores data as properties.
 *
 * @author Robert Heim
 */
public class DB {
  
  private static final String DB_FILE = "minesweeper.db";
  
  /**
   * Stores the string representation of value.
   * 
   * @param key
   * @param value
   */
  public static void put(String key, Object value) {
    OutputStream output = null;
    try {
      Properties prop = load();
      output = new FileOutputStream(DB_FILE);
      prop.setProperty(key, value.toString());
      prop.store(output, null);
    }
    
    catch (IOException io) {
      io.printStackTrace();
    }
    finally {
      if (output != null) {
        try {
          output.close();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  private static Properties load() {
    InputStream input = null;
    Properties prop = new Properties();
    try {
      input = new FileInputStream(DB_FILE);
      prop.load(input);
      return prop;
    }
    catch (IOException ex) {
      // could not find file, which is fine.
    }
    finally {
      if (input != null) {
        try {
          input.close();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return prop;
  }
  
  public static Optional<String> get(String key) {
    Properties prop = load();
    return prop.containsKey(key) ? Optional.of(prop.getProperty(key)) : Optional.empty();
  }
  
  public static Optional<Long> getLong(String key) {
    Optional<String> val = get(key);
    if (val.isPresent()) {
      try {
        return Optional.of(Long.parseLong(val.get()));
      }
      catch (NumberFormatException e) {
        e.printStackTrace();
      }
    }
    return Optional.empty();
  }
  
  private DB() {
  }
}
