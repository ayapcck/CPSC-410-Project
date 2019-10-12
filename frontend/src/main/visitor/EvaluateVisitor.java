package visitor;

import ast.*;
import ast.Date;
import com.google.api.services.sheets.v4.Sheets;
import sheets_api.SheetsAPIHandler;

import java.util.*;

public class EvaluateVisitor implements Visitor {

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
                .createExpensesColumns(expenses);
        List<String> trackedExpenses = new ArrayList<>();
        for (String expense : expenseColumns) {
            ExpenseDetailBlock details = expensesBlock.expenseProperties.get(expense);
            boolean track = (boolean) details.accept(this);
            if (track) trackedExpenses.add(expense);
        }
        SheetsAPIHandler
                .getSheetsAPIHandlerInstance()
                .createTrackingColumns(trackedExpenses);
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
        SheetsAPIHandler
                .getSheetsAPIHandlerInstance()
                .createSheet(title);
        SheetsAPIHandler
                .getSheetsAPIHandlerInstance()
                .createMonthRows(title);
        monthlyBudgetBlock.expenses.accept(this);
        return null;
    }

    @Override
    public Object visit(Projected projected) {
        return null;
    }

    @Override
    public Object visit(Trends trends) {
        return null;
    }
}
