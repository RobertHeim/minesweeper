package de.robert_heim.minesweeper;

import java.util.Locale;
import java.util.ResourceBundle;

public class Lang {
  private ResourceBundle messages;
  
  private Locale currentLocale;
  
  private static Lang instance;
  
  private Lang() {
    this(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
  }
  
  private Lang(String language, String country) {
    currentLocale = new Locale(language, country);
    messages = ResourceBundle.getBundle("lang.Messages", currentLocale);
  }
  
  public static void setLocal(String language, String country) {
    instance = new Lang(language, country);
  }
  
  public static String t(String id) {
    if (!i().messages.containsKey(id)) {
      System.err.println("Could not find key " + id);
    }
    return i().messages.getString(id);
  }
  
  private static Lang i() {
    if (null == instance) {
      instance = new Lang();
    }
    return instance;
  }
  
}
