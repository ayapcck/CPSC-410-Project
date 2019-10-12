package utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.*;

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

}
