package ui;

import tokenizer.*;
import ast.Program;
import visitor.Visitor;

import javax.swing.text.Document;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class main {
    public static List<String> literals = new ArrayList<String>();

    public static void main(String[] args) {
        System.out.println("Hello, here we go!");
        literals = Arrays.asList("create", "expenses", "date", "budget", "track", "date_range", "monthly_budget");
        Tokenizer.createTokenizer("input", literals);

    }
}
