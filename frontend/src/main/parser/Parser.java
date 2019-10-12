package parser;

import java.util.*;

import ast.*;
import ast.Date;
import tokenizer.*;

public class Parser {
    private Tokenizer theTokenizer = Tokenizer.getTokenizer();
    private Map<String, ExpenseDetailBlock> expenseDetailBlockMap = new LinkedHashMap<>();

    public Program parse() {
        return Program();
    }

    private Program Program() {
        List<Sheet> sheetList = new ArrayList<>();
        Sheet s;

        while(theTokenizer.hasMoreTokens()) {
            s = Sheet();
            sheetList.add(s);
        }
        return new Program(sheetList);
    }

    private Sheet Sheet() {
        Sheet sheet;
        SheetType type;

        theTokenizer.getAndCheckTokenValue("create");
        theTokenizer.getAndCheckTokenValue("sheet");

        switch(theTokenizer.nextToken()) {
            case "monthly_budget":
                type = MonthlyBudget();
                System.out.println("DONE");
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

        theTokenizer.getAndCheckTokenValue("end");
        theTokenizer.getAndCheckTokenValue("sheet");
        return sheet;
    }

    private MonthlyBudget MonthlyBudget() {
        MonthlyBudgetBlock block;
        block = MonthlyBudgetBlock();
        return new MonthlyBudget(block);
    }

    private MonthlyBudgetBlock MonthlyBudgetBlock() {
        Date date = null;
        ExpensesBlock expensesBlock = null;
        String curr = "";
        String next = "";

        // Getting the next token
        next = theTokenizer.viewNextToken();

        while (!next.equals("end")) {
        next = theTokenizer.nextToken();

            while(next.equals("add")) {
                curr = theTokenizer.nextToken();

                if (curr.equals("date"))
                    date = Date();
                else if (curr.equals("expenses"))
                    initializeExpenseDetailMap();
                else {
                    System.out.println("Invalid token: " + curr);
                    System.exit(1);
                }
                theTokenizer.getAndCheckTokenValue(",");
                next = theTokenizer.nextToken();
            }

            while (next.equals("track") || next.equals("budget")) {
                if (next.equals("track")) {
                    parseTrack();
                } else {
                    parseBudget();
                }
                theTokenizer.getAndCheckTokenValue(",");
                next = theTokenizer.nextToken();
            }

            // We don't want to pop off the last token
            next = theTokenizer.viewNextToken();

        }

        expensesBlock = new ExpensesBlock(expenseDetailBlockMap);
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

    private void initializeExpenseDetailMap() {
        ExpenseDetailBlock detailBlock = new ExpenseDetailBlock(0, false);
        String next = "";
        String curr = "";

        theTokenizer.getAndCheckTokenValue("\\[");
        next = ",";

        while(next.equals(",")) {
            curr = theTokenizer.nextToken();
            expenseDetailBlockMap.put(curr, detailBlock);

            next = theTokenizer.nextToken();
        }
        checkToken(next, "]");
    }

    private ast.Date Date() {
        String month = theTokenizer.nextToken();
        int year = parseToInt(theTokenizer.nextToken());
        return new Date(month, year);
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

    private void parseTrack() {
        String key;
        ExpenseDetailBlock block;
        theTokenizer.getAndCheckTokenValue("expense");
        theTokenizer.getAndCheckTokenValue("for");
        key = theTokenizer.nextToken();

        if (expenseDetailBlockMap.containsKey(key)) {
            block = expenseDetailBlockMap.get(key);
            block.setTrack(true);

            expenseDetailBlockMap.put(key, block);
        }
    }

    private void parseBudget() {
        String key;
        ExpenseDetailBlock block;
        theTokenizer.getAndCheckTokenValue("for");
        key = theTokenizer.nextToken();

        if (expenseDetailBlockMap.containsKey(key)) {
            block = expenseDetailBlockMap.get(key);
            block.setBudget(parseToInt(theTokenizer.nextToken()));

            expenseDetailBlockMap.put(key, block);
        }
    }

    private void checkToken(String s, String regex) {
        if (!s.matches(regex)) System.exit(1);
    }
}
