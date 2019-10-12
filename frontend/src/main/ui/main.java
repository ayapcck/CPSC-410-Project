package ui;

import ast.Program;
import ast.SSTitle;
import sheets_api.SheetsAPIHandler;
import tokenizer.Tokenizer;
import visitor.EvaluateVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class main {
    public static List<String> literals = new ArrayList<String>();
    public static String spreadsheetId;

    public static void main(String[] args) {
        System.out.println("Hello, here we go!");
        literals = Arrays.asList(" create ", " sheet ", " for ", " add ", " track ", " projected expenses ",
                " monthly ", " trends ", " budget ", " expenses ", " starting savings ", " start ", " end ", " course ",
                " rows ", " columns ", " is ", "[", "]", "(", ")", ",", " sheet ", " account ", " balance ");
        Tokenizer.createTokenizer("input", literals);

        EvaluateVisitor ev = new EvaluateVisitor();
        Program p = new Program(new SSTitle("TestingName-2"));
        p.accept(ev);
        List<String> expenses = Arrays.asList("kaushdiakaisuhdiaushdi", "Eating out", "Testing something 2",  "something else");
        SheetsAPIHandler.getSheetsAPIHandlerInstance().createExpensesColumns(expenses);
    }
}
