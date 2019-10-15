package visitor;
import ast.*;

public interface Visitor<R> {

    R visit(Program n);

    R visit(Sheet n);

    R visit(SheetType n);

    R visit(SSTitle n);

    R visit(AccountBalance n);

    R visit(Block n);

    R visit(CourseDetailBlock n);

    R visit(CourseTracker n);

    R visit(CourseTrackerBlock n);

    R visit(Date n);

    R visit(DateRange n);

    R visit(ExamDetailBlock n);

    R visit(ExpenseDetailBlock n);

    R visit(ExpensesBlock n);

    R visit(Income n);

    R visit(MonthlyBudget n);

    R visit(MonthlyBudgetBlock n);

    R visit(Projected n);

    R visit(ProjectedBlock n);

    R visit(Trends n);

    R visit(TrendsBlock n);

    R visit(MonthType n);

    R visit(CourseType n);
}

