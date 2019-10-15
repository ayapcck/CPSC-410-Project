package utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static int parseMonth(String month) {
        try {
            Date date = new SimpleDateFormat("MMM", Locale.CANADA).parse(month);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.MONTH) + 1;
        } catch(ParseException e) {
            System.out.println("Error parsing month: " + e);
        }
        return -1;
    }

    public static List<String> getMonthDates(String monthYear) {
        String monthStr = monthYear.split(" ")[0];
        int monthInt = parseMonth(monthStr);
        int year = Integer.parseInt(monthYear.split(" ")[1]);
        YearMonth yearMonth = YearMonth.of(year, monthInt);
        int lengthOfMonth = yearMonth.lengthOfMonth();
        List<String> monthDates = new ArrayList<>();
        for (int i = 1; i <= lengthOfMonth; i++) {
            monthDates.add(monthInt + "/" + i + "/" + year);
        }
        return monthDates;
    }

    public static List<String> generateMonthsFrom(String monthYear, int numMonths) {
        Calendar start = Calendar.getInstance();
        int month = parseMonth(monthYear.split(" ")[0]) - 1;
        int year = Integer.parseInt(monthYear.split(" ")[1]);
        List<String> months = new ArrayList<>();
        start.set(Calendar.MONTH, month);
        for (int i = 0; i < numMonths; i++) {
            String val = start.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.CANADA);
            months.add(val + "  " + year);
            start.set(Calendar.MONTH, month + i + 1);
            if (i + 1 == 12) year++;
        }
        return months;
    }

    public static int getDifference(String startDate, String endDate) {
        String startMonth = startDate.split(" ")[0];
        int startYear = Integer.parseInt(startDate.split(" ")[1]);
        String endMonth = endDate.split(" ")[0];
        int endYear = Integer.parseInt(endDate.split(" ")[1]);

        return (endYear - startYear) * 12
                + (parseMonth(endMonth) - parseMonth(startMonth)) + 1;
    }

}
