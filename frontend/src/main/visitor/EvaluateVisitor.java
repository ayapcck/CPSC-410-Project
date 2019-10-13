package visitor;

import ast.*;
import ast.Date;
import com.google.api.services.sheets.v4.Sheets;
import sheets_api.SheetsAPIHandler;

import java.util.*;

public class EvaluateVisitor implements Visitor {

    String currentSheetTitle = "";

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
        SheetsAPIHandler
                .getSheetsAPIHandlerInstance()
                .createExpensesColumns(this.currentSheetTitle, expenses);
        List<String> trackedExpenses = new ArrayList<>();
        for (String expense : expenseColumns) {
            ExpenseDetailBlock details = expensesBlock.expenseProperties.get(expense);
            boolean track = (boolean) details.accept(this);
            if (track) trackedExpenses.add(expense);
        }
        SheetsAPIHandler
                .getSheetsAPIHandlerInstance()
                .createTrackingColumns(this.currentSheetTitle, trackedExpenses);
        return null;
    }

    @Override
    public Object visit(Income income) {
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

        projected.projectedBlock.accept(this);
        return null;
    }

    @Override
    public Object visit(ProjectedBlock projectedBlock) {
        projectedBlock.accountBalance.accept(this);
        projectedBlock.dateRange.accept(this);
        projectedBlock.expensesBlock.accept(this);
        projectedBlock.income.accept(this);
        return null;
    }

    @Override
    public Object visit(Trends trends) {
        return null;
    }
}
