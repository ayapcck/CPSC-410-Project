package parser;

import ast.*;
import tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private Tokenizer theTokenizer = Tokenizer.getTokenizer();
    private Map<String, ExpenseDetailBlock> expenseDetailBlockMap = new LinkedHashMap<>();
    private Map<String, CourseDetailBlock> coursesInformation = new LinkedHashMap<>();
    private String courseName = "";

    public Program parse() {
        return Program();
    }

    private Program Program() {
        theTokenizer.getAndCheckTokenValue("spreadsheet");
        SSTitle title = SSTitle();
        List<Sheet> sheetList = new ArrayList<>();
        Sheet s;

        while(theTokenizer.hasMoreTokens() && !theTokenizer.checkTokenValue("spreadsheet")) {
            s = Sheet();
            sheetList.add(s);
        }
        theTokenizer.getAndCheckTokenValue("spreadsheet");
        return new Program(title, sheetList);
    }

    private Sheet Sheet() {
        Sheet sheet;
        SheetType type;
        theTokenizer.getAndCheckTokenValue("create");
        theTokenizer.getAndCheckTokenValue("sheet");

        switch(theTokenizer.nextToken()) {
            case "monthly_budget":
                type = MonthlyBudget();
                sheet = new Sheet(type);
                break;
            case "course_tracker":
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
        theTokenizer.getAndCheckTokenValue("sheet");
        System.out.println("DONE");
        return sheet;
    }

    private SSTitle SSTitle() {
        return new SSTitle(theTokenizer.nextToken());
    }

    private MonthlyBudget MonthlyBudget() {
        MonthlyBudgetBlock block;
        block = MonthlyBudgetBlock();
        return new MonthlyBudget(block);
    }

    private MonthlyBudgetBlock MonthlyBudgetBlock() {
        ExpensesBlock expensesBlock;
        String curr = "";
        String next = "";

        next = ",";

        Date date = null;
        while(next.equals(",")) {
            curr = theTokenizer.nextToken();

            if(curr.equals("add")) {
                curr = theTokenizer.nextToken();

                if (curr.equals("date")) {
                    date = Date();
                }
                else if (curr.equals("expenses"))
                    initializeExpenseDetailMap();
                else {
                    System.out.println("Invalid token: " + curr);
                    System.exit(1);
                }
            } else if (curr.equals("track")) {
                parseTrack();
            } else if (curr.equals("budget")){
                parseBudget();
            } else {
                System.out.print("Invalid token: "+ curr);
                System.exit(1);
            }

            next = theTokenizer.nextToken();
        }

        if (!next.equals("end")) {
            System.out.println("Invalid token: " + next);
            System.exit(1);
        }

        expensesBlock = new ExpensesBlock(expenseDetailBlockMap);
        return new MonthlyBudgetBlock(date, expensesBlock);
    }

    private Trends Trends() {
        TrendsBlock trendsBlock;
        trendsBlock = TrendsBlock();
        return new Trends(trendsBlock);
    }

    private TrendsBlock TrendsBlock() {
        DateRange range = null;
        ExpensesBlock expensesBlock = null;
        String next = ",";
        String curr = "";

        while(next.equals(",")) {
            theTokenizer.getAndCheckTokenValue("add");
            curr = theTokenizer.nextToken();
            if (curr.equals("date")) {
                range = DateRange();
            } else if (curr.equals("expenses")) {
                expensesBlock = ExpensesBlock();
            }
            next = theTokenizer.nextToken();
        }

        if (!next.equals("end")) {
            System.out.println("Invalid token: " + next);
            System.exit(1);
        }

        return new TrendsBlock(range, expensesBlock);
    }

    private ProjectedBlock ProjectedBlock() {
        DateRange range = null;
        Income income  = null;
        ExpensesBlock expensesBlock = null;
        AccountBalance accountBalance = null;
        String next = ",";
        String curr = "";

        while(next.equals(",")) {
            theTokenizer.getAndCheckTokenValue("add");
            curr = theTokenizer.nextToken();

            if (curr.equals("date")) {
                range = DateRange();
            } else if (curr.equals("expenses")) {
                expensesBlock = ProjectedExpensesBlock();
            } else if (curr.equals("income")) {
                income = Income();
            } else if (curr.equals("account")) {
                accountBalance = AccountBalance();
            }
            next = theTokenizer.nextToken();
        }

        if (!next.equals("end")) {
            System.out.println("Invalid token: " + next);
            System.exit(1);
        }

        return new ProjectedBlock(range, income, expensesBlock, accountBalance);
    }

    private CourseTrackerBlock CourseTrackerBlock() {
        CourseDetailBlock detailBlock = null;
        courseName = theTokenizer.nextToken();
        detailBlock = CourseDetailBlock();

        coursesInformation.put(courseName, detailBlock);
        return new CourseTrackerBlock(coursesInformation);
    }

    private CourseDetailBlock CourseDetailBlock() {
        Map<String, ExamDetailBlock> examDetailBlockMap = new LinkedHashMap<>();
        ExamDetailBlock examDetailBlock = null;
        String next = ",";
        String key = "";
        int goalGrade = 0;

        while (next.equals(",")) {
            theTokenizer.getAndCheckTokenValue("add");
            key = theTokenizer.nextToken();
            if (key.equals("goal")) {
                goalGrade = parseToInt(theTokenizer.nextToken());
            } else {
                examDetailBlock = ExamDetailBlock();
                examDetailBlockMap.put(key, examDetailBlock);
            }
            next = theTokenizer.nextToken();
        }

        if (!next.equals("end")) {
            System.out.println("Invalid token: " + next);
            System.exit(1);
        }

        return new CourseDetailBlock(examDetailBlockMap, goalGrade);
    }

    private ExamDetailBlock ExamDetailBlock() {
        int count = 0;
        float weight = 0;

        theTokenizer.getAndCheckTokenValue("count");
        count = parseToInt(theTokenizer.nextToken());

        theTokenizer.getAndCheckTokenValue("and");
        theTokenizer.getAndCheckTokenValue("weight");
        weight = parseToInt(theTokenizer.nextToken());
        return new ExamDetailBlock(weight, count);
    }

    private ExpensesBlock ExpensesBlock() {
        Map<String, ExpenseDetailBlock> trendsExpenseMap = new LinkedHashMap<>();
        String next = "";

        theTokenizer.getAndCheckTokenValue("\\[");

        while(!next.equals("]")) {
            trendsExpenseMap.put(theTokenizer.nextToken(), new ExpenseDetailBlock(0, false));
            next = theTokenizer.nextToken(); // , or ]
        }
        return new ExpensesBlock(trendsExpenseMap);
    }

    private ExpensesBlock ProjectedExpensesBlock() {
        Map<String, ExpenseDetailBlock> trendsExpenseMap = new LinkedHashMap<>();
        ExpenseDetailBlock detailBlock;
        String next = "";
        String key = "";
        int budget;

        theTokenizer.getAndCheckTokenValue("\\[");

        while(!next.equals("]")) {
            key = theTokenizer.nextToken();

            next = theTokenizer.nextToken();

            if (next.equals(":")) {
                budget = parseToInt(theTokenizer.nextToken());
                trendsExpenseMap.put(key, new ExpenseDetailBlock(budget, false));

                next = theTokenizer.nextToken(); // , or ]
            } else {
                trendsExpenseMap.put(key, null);
            }
        }
        return new ExpensesBlock(trendsExpenseMap);
    }

    private Projected Projected() {
        ProjectedBlock projectedBlock;
        projectedBlock = ProjectedBlock();
        return new Projected(projectedBlock);
    }

    private CourseTracker CourseTracker() {
        CourseTrackerBlock  trackerBlock = null;
        String next = theTokenizer.viewNextToken();

        while (!next.equals("end")) {
            theTokenizer.getAndCheckTokenValue("start");
            theTokenizer.getAndCheckTokenValue("course");
            trackerBlock = CourseTrackerBlock();
            theTokenizer.getAndCheckTokenValue("course");
            theTokenizer.getAndCheckTokenValue(courseName); //
            next = theTokenizer.viewNextToken();
        }
        theTokenizer.getAndCheckTokenValue("end");
        return new CourseTracker(trackerBlock);
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

    private DateRange DateRange() {
        ast.Date start;
        ast.Date end;
        start = Date();
        theTokenizer.getAndCheckTokenValue("to");
        end = Date();

        return new DateRange(start, end);
    }

    private Income Income() {
        int income = parseToInt(theTokenizer.nextToken());
        return new Income(income);
    }

    private AccountBalance AccountBalance() {
        int accountBalance = 0;
        theTokenizer.getAndCheckTokenValue("balance");
        accountBalance = parseToInt(theTokenizer.nextToken());
        return new AccountBalance(accountBalance);
    }

    private int parseToInt(String s) {
        int value = 0;
        try {
            value = Integer.parseInt(s);
        } catch(Exception e) {
            System.out.println("Invalid value encountered" + e);
            System.exit(1);
        }
        return value;
    }

    private float parseToFloat(String s) {
        float value = 0;
        try {
            value = Float.parseFloat(s);
        } catch(Exception e) {
            System.out.println("Invalid value encountered" + e);
            System.exit(1);
        }
        return value;
    }

    private boolean parseToBoolean(String s) {
        boolean value = false;
        try {
            value = Boolean.parseBoolean(s);
        } catch(Exception e) {
            System.out.println("Invalid value encountered" + e);
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
        } else {
            System.out.println("This column does not exist");
            System.exit(1);
        }
    }

    private void parseBudget() {
        String key;
        ExpenseDetailBlock block;
        theTokenizer.getAndCheckTokenValue("for");
        key = theTokenizer.nextToken();

        if (expenseDetailBlockMap.containsKey(key)) {
            block = expenseDetailBlockMap.get(key);
            theTokenizer.getAndCheckTokenValue("is");
            block.setBudget(parseToInt(theTokenizer.nextToken()));

            expenseDetailBlockMap.put(key, block);
        } else {
            System.out.println("This column does not exist");
            System.exit(1);
        }
    }

    private void checkToken(String s, String regex) {
        if (!s.matches(regex)) System.exit(1);
    }
}
