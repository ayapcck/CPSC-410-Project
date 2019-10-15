package utilities;

public class ColumnUtils {

    public static char getColumnForNumber(int colNum) {
        char col = 'A';
        for (int i = 1; i <= colNum; i++) {
            col++;
        }
        return col;
    }

}
