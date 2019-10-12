package ui;

import ast.*;
import sheets_api.SheetsAPIHandler;
import tokenizer.*;
import utilities.DateUtils;
import visitor.EvaluateVisitor;
import visitor.Visitor;

import javax.sound.midi.SysexMessage;
import javax.swing.text.Document;
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
//        p.accept(ev);
        List<String> expenses = Arrays.asList("Groceries", "Eating out", "Testing something");
        SheetsAPIHandler.getSheetsAPIHandlerInstance().createExpensesColumns(expenses);
    }
}
