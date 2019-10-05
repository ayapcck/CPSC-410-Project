package ui;
import tokenizer.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class main {
    List<String> literals = new ArrayList<String>();

    public void main(String[] args) {
        System.out.println("Hello, here we go!");
        literals = Arrays.asList("create", "expenses", "date", "budget", "track", "date_range", "monthly_budget");
        Tokenizer.createTokenizer("blah", literals);
    }
}
