package visitor;

import ast.*;
import ast.Date;
import sheets_api.SheetsAPIHandler;
import utilities.DateUtils;
import utilities.StringUtils;

import java.util.*;

public class EvaluateVisitor implements Visitor {

    private String currentSheetTitle = "";

    @Override
    public Object visit(Program program) {
        String name = (String) program.title.accept(this);
        SheetsAPIHandler
                .getSheetsAPIHandlerInstance()
                .createSpreadsheet(name);
        for (Sheet sheet : program.sheets) {
            sheet.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(Sheet sheet) {
        sheet.type.accept(this);
        return null;
    }

    @Override
    public Object visit(SheetType sheetType) {
        return null;
    }

    @Override
    public String visit(SSTitle ssTitle) {
        return ssTitle.value;
    }

    @Override
    public Object visit(AccountBalance accountBalance) {
        int balance = accountBalance.balance;
        List<Object> values = new ArrayList<>();
        values.add("Estimated Savings:");
        values.add("=" + balance + "+INDIRECT(ADDRESS(ROW()-2,COLUMN()))");
        SheetsAPIHandler
                .getSheetsAPIHandlerInstance()
                .createEstimatedSavingsRow(this.currentSheetTitle, values);
        return null;
    }

    @Override
    public Object visit(Block n) {
        return null;
    }

    @Override
    public Object visit(CourseDetailBlock courseDetailBlock) {
        Float goalGrade = courseDetailBlock.goalGrade;
        Map<String, ExamDetailBlock> examDetails = courseDetailBlock.examDetails;
        Set<String> exams = examDetails.keySet();
        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList("", "Weight (%)", "Maximum Marks", "Marks Obtained", "Weighted Marks"));
        for (String exam : exams) {
            ExamDetailBlock detailBlock = examDetails.get(exam);
            detailBlock.accept(this);
            int itemCount = detailBlock.count;
            float totalWeight = detailBlock.weight;
            float individualWeight = totalWeight / itemCount;
            for (int i = 1; i <= itemCount; i++) {
                String name = StringUtils.capitalizeSentence(exam);
                if (totalWeight != individualWeight) {
                    name = name + " " + i;
                }
                rows.add(Arrays.asList(name, totalWeight, 100, "", "=MULTIPLY(DIVIDE(INDIRECT(ADDRESS(ROW(), COLUMN()-1)),INDIRECT(ADDRESS(ROW(), COLUMN()-2))),INDIRECT(ADDRESS(ROW(), COLUMN()-3)))"));
            }
        }
        int numToSum = rows.size() - 1;
        rows.add(Arrays.asList("Total", 100, "", "", "=SUM(INDIRECT(ADDRESS(ROW()-"
                + numToSum + ", COLUMN())):INDIRECT(ADDRESS(ROW()-1, COLUMN())))"));
        Map<Float, List<List<Object>>> retMap = new HashMap<>();
        retMap.put(goalGrade, rows);
        return retMap;
    }

    @Override
    public Object visit(CourseTracker courseTracker) {
        this.currentSheetTitle = "Course Tracker";
        SheetsAPIHandler
                .getSheetsAPIHandlerInstance()
                .createSheet(currentSheetTitle);
        courseTracker.courses.accept(this);
        return null;
    }

    @Override
    public Object visit(CourseTrackerBlock courseTrackerBlock) {
        Map<String, CourseDetailBlock> courseDetails = courseTrackerBlock.coursesInformation;
        Set<String> courses = courseDetails.keySet();
        for (String course : courses) {
            CourseDetailBlock courseDetailBlock = courseDetails.get(course);
            Map<Float, List<List<Object>>> courseMap = (Map<Float, List<List<Object>>>)
                    courseDetailBlock.accept(this);
            Set<Float> courseKey = courseMap.keySet();
            for (Float key : courseKey) {
                List<List<Object>> values = courseMap.get(key);
                List<List<Object>> newValues = new ArrayList<>();
                List<Object> headerRow = new ArrayList<>();
                headerRow.add(course);
                headerRow.add("Goal:");
                headerRow.add(key);
                newValues.add(headerRow);
                newValues.addAll(values);
                SheetsAPIHandler
                        .getSheetsAPIHandlerInstance()
                        .addCourseRows(this.currentSheetTitle, newValues);
            }
        }
        return null;
    }

    @Override
    public String visit(Date date) {
        return date.month + " " + date.year;
    }

    @Override
    public Object visit(DateRange dateRange) {
        String startDate = (String) dateRange.start.accept(this);
        String endDate = (String) dateRange.end.accept(this);
        int diffInMonths = DateUtils.getDifference(startDate, endDate);
        List<String> months = DateUtils.generateMonthsFrom(startDate, diffInMonths);
        SheetsAPIHandler
                .getSheetsAPIHandlerInstance()
                .createHeaderColumns(this.currentSheetTitle, months);
        return null;
    }

    @Override
    public Object visit(ExamDetailBlock examDetailBlock) {
        return examDetailBlock;
    }

    @Override
    public Object visit(ExpenseDetailBlock expenseDetailBlock) {
        return expenseDetailBlock.track;
    }

    @Override
    public Object visit(ExpensesBlock expensesBlock) {
        Set<String> expenseColumns = expensesBlock.expenseProperties.keySet();
        List<String> expenses = new ArrayList<>(expenseColumns);
        Collections.sort(expenses);
        switch (this.currentSheetTitle) {
            case "Projected":
                projectedExpenses(expenses, expensesBlock.expenseProperties);
                break;
            case "Trends":
                trendsExpenses(expenses);
                break;
            default:
                monthlyExpenses(expenses, expensesBlock.expenseProperties);
                break;
        }
        return null;
    }

    private void monthlyExpenses(List<String> expenses,
                                 Map<String, ExpenseDetailBlock> expensesBlock) {
        SheetsAPIHandler
                .getSheetsAPIHandlerInstance()
                .createHeaderColumns(this.currentSheetTitle, expenses);
        List<String> trackedExpenses = new ArrayList<>();
        for (String expense : expenses) {
            ExpenseDetailBlock details = expensesBlock.get(expense);
            boolean track = (boolean) details.accept(this);
            if (track) trackedExpenses.add(expense);
        }
        SheetsAPIHandler
                .getSheetsAPIHandlerInstance()
                .createTrackingColumns(this.currentSheetTitle, trackedExpenses);
    }

    private void projectedExpenses(List<String> expenses,
                                   Map<String, ExpenseDetailBlock> expensesBlock) {
        Map<String, Integer> expenseRows = new HashMap<>();
        for (String expense : expenses) {
            ExpenseDetailBlock expenseDetailBlock = expensesBlock.get(expense);
            int budget = expenseDetailBlock.budget;
            expenseRows.put(expense, budget);
        }
        SheetsAPIHandler
                .getSheetsAPIHandlerInstance()
                .createProjectedExpensesRows(this.currentSheetTitle, expenses, expenseRows);
    }

    private void trendsExpenses(List<String> expenses) {
            SheetsAPIHandler
                    .getSheetsAPIHandlerInstance()
                    .createTrendsExpenses(this.currentSheetTitle, expenses);
    }

    @Override
    public Object visit(Income income) {
        List<List<Object>> values = new ArrayList<>();
        values.add(Arrays.asList("INCOME:", income.incomeValue));
        SheetsAPIHandler
                .getSheetsAPIHandlerInstance()
                .createBudgetRows(this.currentSheetTitle, values, 2);
        return null;
    }

    @Override
    public Object visit(MonthlyBudget monthlyBudget) {
        monthlyBudget.budgetBlock.accept(this);
        return null;
    }

    @Override
    public Object visit(MonthlyBudgetBlock monthlyBudgetBlock) {
        String title = (String) monthlyBudgetBlock.month.accept(this);
        this.currentSheetTitle = title;
        SheetsAPIHandler
                .getSheetsAPIHandlerInstance()
                .createSheet(title);
        List<List<Object>> values = new ArrayList<>();
        values.add(Arrays.asList("Expenses -> \n Date V"));
        SheetsAPIHandler
                .getSheetsAPIHandlerInstance()
                .updateSpreadsheetValues("'" + title + "'!A1:A1", values);
        SheetsAPIHandler
                .getSheetsAPIHandlerInstance()
                .createMonthRows(title);
        monthlyBudgetBlock.expenses.accept(this);
        return null;
    }

    @Override
    public Object visit(Projected projected) {
        this.currentSheetTitle = "Projected";
        SheetsAPIHandler
                .getSheetsAPIHandlerInstance()
                .createSheet(this.currentSheetTitle);
        projected.projectedBlock.accept(this);
        return null;
    }

    @Override
    public Object visit(ProjectedBlock projectedBlock) {
        projectedBlock.dateRange.accept(this);
        projectedBlock.income.accept(this);
        projectedBlock.expensesBlock.accept(this);
        projectedBlock.accountBalance.accept(this);
        return null;
    }

    @Override
    public Object visit(Trends trends) {
        this.currentSheetTitle = "Trends";
        SheetsAPIHandler
                .getSheetsAPIHandlerInstance()
                .createSheet(this.currentSheetTitle);
        trends.trendsBlock.accept(this);
        return null;
    }

    @Override
    public Object visit(TrendsBlock trendsBlock) {
        trendsBlock.range.accept(this);
        trendsBlock.expensesBlock.accept(this);
        return null;
    }
}
