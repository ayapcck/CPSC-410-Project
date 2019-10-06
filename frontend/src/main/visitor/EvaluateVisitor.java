package visitor;

import ast.*;
import com.google.api.services.sheets.v4.Sheets;
import sheets_api.SheetsAPIHandler;

public class EvaluateVisitor implements Visitor {

    @Override
    public Object visit(Program n) {
        try {
            SheetsAPIHandler.createSheet(n.title.value);
        } catch (Exception e) {
            System.out.println("Error creating google sheets document: " + e);
        }
        return null;
    }

    @Override
    public Object visit(Sheet n) {
        return null;
    }

    @Override
    public Object visit(SheetType n) {
        return null;
    }

    @Override
    public Object visit(SSTitle n) {
        return null;
    }

    @Override
    public Object visit(AccountBalance n) {
        return null;
    }

    @Override
    public Object visit(CourseDetailBlock n) {
        return null;
    }

    @Override
    public Object visit(CourseTracker n) {
        return null;
    }

    @Override
    public Object visit(CourseTrackerBlock n) {
        return null;
    }

    @Override
    public Object visit(Date n) {
        return null;
    }

    @Override
    public Object visit(DateRange n) {
        return null;
    }

    @Override
    public Object visit(ExamDetailBlock n) {
        return null;
    }

    @Override
    public Object visit(ExpenseDetailBlock n) {
        return null;
    }

    @Override
    public Object visit(ExpensesBlock n) {
        return null;
    }

    @Override
    public Object visit(Income n) {
        return null;
    }

    @Override
    public Object visit(MonthlyBudget n) {
        return null;
    }

    @Override
    public Object visit(MonthlyBudgetBlock n) {
        return null;
    }

    @Override
    public Object visit(Projected n) {
        return null;
    }

    @Override
    public Object visit(Trends n) {
        return null;
    }
}
