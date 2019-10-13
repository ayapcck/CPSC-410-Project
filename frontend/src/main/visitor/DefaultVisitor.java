package visitor;
import ast.*;

public class DefaultVisitor<R> implements Visitor<R> {

    @Override
    public R visit(Program n) {
        return null;
    }

    @Override
    public R visit(Sheet n) {
        return null;
    }

    @Override
    public R visit(SheetType n) {
        return null;
    }

    @Override
    public R visit(SSTitle n) {
        return null;
    }

    @Override
    public R visit(AccountBalance n) {
        return null;
    }

    @Override
    public R visit(CourseDetailBlock n) {
        return null;
    }

    @Override
    public R visit(CourseTracker n) {
        return null;
    }

    @Override
    public R visit(CourseTrackerBlock n) {
        return null;
    }

    @Override
    public R visit(Date n) {
        return null;
    }

    @Override
    public R visit(DateRange n) {
        return null;
    }

    @Override
    public R visit(ExamDetailBlock n) {
        return null;
    }

    @Override
    public R visit(ExpenseDetailBlock n) {
        return null;
    }

    @Override
    public R visit(ExpensesBlock n) {
        return null;
    }

    @Override
    public R visit(Income n) {
        return null;
    }

    @Override
    public R visit(MonthlyBudget n) {
        return null;
    }

    @Override
    public R visit(MonthlyBudgetBlock n) {
        return null;
    }

    @Override
    public R visit(Projected n) {
        return null;
    }

    @Override
    public R visit(ProjectedBlock n) {
        return null;
    }

    @Override
    public R visit(Trends n) {
        return null;
    }

    @Override
    public R visit(TrendsBlock n) {
        return null;
    }

    @Override
    public R visit(MonthType n) { return null; }

    @Override
    public R visit(CourseType n) { return null; }
}

