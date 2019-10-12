package ui;

import ast.*;
import ast.Date;
import sheets_api.SheetsAPIHandler;
import tokenizer.*;
import utilities.DateUtils;
import visitor.EvaluateVisitor;
import visitor.Visitor;

import javax.sound.midi.SysexMessage;
import javax.swing.text.Document;
import java.util.*;

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
        List<String> expenseNames = Arrays.asList("Groceries", "Eating out", "Coffee", "Testing something 2",  "Something else");
        Map<String,ExpenseDetailBlock> expenses = new HashMap<>();
        expenseNames.forEach((String name) -> {
            expenses.put(name, new ExpenseDetailBlock(100, true));
        });
        List<Sheet> sheets = new ArrayList<>();
        Sheet test = new Sheet(new MonthlyBudget(
                new MonthlyBudgetBlock(
                        new Date("October", 2019),
                        new ExpensesBlock(expenses))));
        sheets.add(test);
        Program p = new Program(new SSTitle("Test everything"), sheets);
        p.accept(ev);
    }
}
