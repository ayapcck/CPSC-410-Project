package ui;

import ast.SSTitle;
import ast.Sheet;
import ast.SheetType;
import sheets_api.SheetsAPIHandler;
import tokenizer.*;
import ast.Program;
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
        literals = Arrays.asList("create", "expenses", "date", "budget", "track", "date_range", "monthly_budget");
        Tokenizer.createTokenizer("input", literals);

        EvaluateVisitor ev = new EvaluateVisitor();
        Program p = new Program(new SSTitle("TestingName-2"));
        p.accept(ev);
        SheetsAPIHandler.getSheetsAPIHandlerInstance().createSheet("Monthly");
    }
}
