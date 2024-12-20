package com.elliot.footballmanager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class to provide helper methods simplifying dealing with the date object.
 *
 * @author Elliot
 */
public class DateUtils {

  public static SimpleDateFormat FIXTURE_DATE_FORMAT = new SimpleDateFormat(
      "EEEE MMM dd HH:mm:ss zzzz yyyy");
  public static SimpleDateFormat FIXTURE_DATE_DISPLAY_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
  private static final Calendar calendar = Calendar.getInstance(Locale.ENGLISH);

  public DateUtils() {

  }

  public static Date addDays(Date date, int days) {
    calendar.setTime(date);
    calendar.add(Calendar.DATE, days);
    return calendar.getTime();
  }

}
