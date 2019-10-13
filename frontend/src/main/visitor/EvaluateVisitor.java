package visitor;

import ast.*;
import ast.Date;
import com.google.api.services.sheets.v4.Sheets;
import sheets_api.SheetsAPIHandler;
import utilities.DateUtils;

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
        values.add(balance);
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
        return null;
    }

    @Override
    public Object visit(CourseTracker courseTracker) {
        return null;
    }

    @Override
    public Object visit(CourseTrackerBlock courseTrackerBlock) {
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
        return null;
    }

    @Override
    public Object visit(ExpenseDetailBlock expenseDetailBlock) {
        // TODO: Do something with the expenseDetail budget
        return expenseDetailBlock.track;
    }

    @Override
    public Object visit(ExpensesBlock expensesBlock) {
        Set<String> expenseColumns = expensesBlock.expenseProperties.keySet();
        List<String> expenses = new ArrayList<>(expenseColumns);
        Collections.sort(expenses);
        if (this.currentSheetTitle.equals("Projected")) {
            projectedExpenses(expenses, expensesBlock.expenseProperties);
        } else {
            monthlyExpenses(expenses, expensesBlock.expenseProperties);
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
        return null;
    }

    @Override
    public Object visit(TrendsBlock n) {
        return null;
    }
}
