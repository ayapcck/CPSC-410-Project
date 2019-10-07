package parser;

import java.util.*;

import ast.*;
import ast.Date;
import tokenizer.*;

public class Parser {
    private Parser theParser;
    private Tokenizer theTokenizer = Tokenizer.getTokenizer();

    public Program parse() {
        return Program();
    }

    private int countFrequencies(String s) {
        return Collections.frequency(theTokenizer.getAllTokens(), s);
    }

    public void createParser(List<String> tokens) {
        if (theParser == null) {
            theParser = new Parser();
        }
    }

    private Program Program() {
        List<Sheet> sheetList = new ArrayList<>();
        Sheet s;
        int numberOfSheets = countFrequencies("create");

        for(int i = 0; i< numberOfSheets; i++) {
            if (theTokenizer.checkTokenValue("create")) {
                s = Sheet();
                sheetList.add(s);
            }
        }
        return new Program(sheetList);
    }

    private Sheet Sheet() {
        Sheet sheet;
        SheetType type;

        switch(theTokenizer.nextToken()) {
            case "monthly_budget":
                type = MonthlyBudget();
                sheet = new Sheet(type);
                break;
            case "courses_tracker":
                type = CourseTracker();
                sheet = new Sheet(type);
                break;
            case "trends":
                type = Trends();
                sheet = new Sheet(type);
                break;
            case "projected":
                type = Projected();
                sheet = new Sheet(type);
                break;
            default:
                sheet = new Sheet(null);
                System.out.println("Invalid Sheet Type");
                System.exit(1);
                break;
        }
        return sheet;
    }

    private MonthlyBudget MonthlyBudget() {
        MonthlyBudgetBlock block;
        theTokenizer.getAndCheckTokenValue("{");
        block = MonthlyBudgetBlock();
        theTokenizer.getAndCheckTokenValue("}");
        return new MonthlyBudget(block);
    }

    private MonthlyBudgetBlock MonthlyBudgetBlock() {
        AST ast;
        Date date = null;
        ExpensesBlock expensesBlock = null;

        // We expect two keys in Monthly Budget
        for (int i = 0; i < 2; i++) {
            ast = checkBudgetKey(theTokenizer.nextToken());

            if (ast instanceof Date) {
                date = (Date) ast;
            } else if (ast instanceof ExpensesBlock) {
                expensesBlock = (ExpensesBlock) ast;
            }
        }
        return new MonthlyBudgetBlock(date, expensesBlock);
    }

    private Trends Trends() {
        return null;
    }

    private Projected Projected() {
        return null;
    }

    private CourseTracker CourseTracker() {
        return null;
    }

    private ExpensesBlock ExpensesBlock() {
        Map<String, ExpenseDetailBlock> expenseDetailBlockMap = new LinkedHashMap<>();
        String curr;
        String next = "";

        theTokenizer.getAndCheckTokenValue("{");

        while(!next.equals("}")) {
            curr = theTokenizer.nextToken();
            curr = curr.replace("\"", "");
            next = theTokenizer.nextToken();

            if (next.equals("{")) {
                expenseDetailBlockMap.put(curr, ExpenseDetailBlock());
            } else {
                expenseDetailBlockMap.put(curr, null);
            }
        }

        theTokenizer.getAndCheckTokenValue("}");
        return new ExpensesBlock(expenseDetailBlockMap);
    }

    private ExpenseDetailBlock ExpenseDetailBlock() {
        ExpenseDetailBlock block;
        int budget = -1;
        boolean track = false;
        String token;

        for (int i = 0; i< 2; i++) {
            token = theTokenizer.nextToken();

            if (token.equals("budget")) {
                theTokenizer.getAndCheckTokenValue(":");
                budget = parseToInt(theTokenizer.nextToken());
            } else if (token.equals("track")) {
                theTokenizer.getAndCheckTokenValue(":");
                track = parseToBoolean(theTokenizer.nextToken());
            }
        }
        theTokenizer.getAndCheckTokenValue("}");
        return new ExpenseDetailBlock(budget, track);
    }

    private ast.Date Date() {
        String month = theTokenizer.nextToken();
        int year = parseToInt(theTokenizer.nextToken());
        return new Date(month, year);
    }

    private AST checkBudgetKey(String token) {
        AST value = null;
        theTokenizer.getAndCheckTokenValue(":");
        switch(token) {
            case "date":
                value = Date();
                break;
            case "expenses":
                value = ExpensesBlock();
                break;
        }
        return value;
    }


    private int parseToInt(String s) {
        int value = 0;
        try {
            value = Integer.parseInt(s);
        } catch(Exception e) {
            System.out.println("Invalid value encountered");
            System.exit(1);
        }
        return value;
    }

    private boolean parseToBoolean(String s) {
        boolean value = false;
        try {
            value = Boolean.parseBoolean(s);
        } catch(Exception e) {
            System.out.println("Invalid value encountered");
           System.exit(1);
        }
        return value;
    }
}
