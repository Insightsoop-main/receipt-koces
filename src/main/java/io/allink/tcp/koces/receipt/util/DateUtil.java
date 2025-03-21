package io.allink.tcp.koces.receipt.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {
    // ✅ YYMMDDhhmmss 형식 변환을 위한 Formatter
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmss");
    private static final DateTimeFormatter YMD_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter OUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ✅ 현재 시간을 YYMMDDhhmmss 형식으로 반환
    public static String getCurrentTrdDate() {
        return LocalDateTime.now().format(DATE_FORMATTER);
    }

    public static String getYMD(String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date, YMD_FORMATTER);
        return dateTime.format(OUT_FORMATTER);

    }
}
