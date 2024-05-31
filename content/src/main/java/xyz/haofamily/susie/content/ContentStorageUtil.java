package xyz.haofamily.susie.content;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ContentStorageUtil {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yy/MM/dd/HH");

  public static String generateKey() {
    return String.format("%s/%s", LocalDateTime.now().format(DATE_TIME_FORMATTER), UUID.randomUUID().toString());
  }
}
