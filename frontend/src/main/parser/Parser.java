package parser;

import java.util.*;

import ast.*;
import tokenizer.*;

public class Parser {
    private Parser theParser;
    private Tokenizer theTokenizer = Tokenizer.getTokenizer();

    private Parser(List<String> tokenList) {

    }

    public Program parse() {
        return Program();
    }

    private int countFrequencies(String s) {
        return Collections.frequency(theTokenizer.getAllTokens(), s);
    }

    public void createParser(List<String> tokens) {
        if (theParser == null) {
            theParser = new Parser(tokens);
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
        return new Program(null);
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
        return null;
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
}
