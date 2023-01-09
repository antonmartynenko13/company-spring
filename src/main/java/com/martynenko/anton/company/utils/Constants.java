package com.martynenko.anton.company.utils;

public abstract class Constants {

  private Constants() { }

  public static final String XLSX_CONTENT_TYPE
      = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  public static final String DATE_FORMAT = "yyyy-MM-dd";
  public static final String EMAIL_PATTERN = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
      + "[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
      + "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

  public static final int DATABASE_STRINGS_MAX_SIZE = 30;

}
