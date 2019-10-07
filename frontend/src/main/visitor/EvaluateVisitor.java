package visitor;

import ast.*;
import sheets_api.SheetsAPIHandler;

import java.io.IOException;

public class EvaluateVisitor implements Visitor {

    @Override
    public Object visit(Program n) {
        String name = (String) n.title.accept(this);
        SheetsAPIHandler
                .getSheetsAPIHandlerInstance()
                .createSpreadsheet(name);
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
    public String visit(SSTitle n) {
        return n.value;
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
