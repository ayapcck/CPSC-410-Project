package typecheck;

import ast.*;
import visitor.Visitor;

import java.util.Map;

public class TypeCheckVisitor implements Visitor<Type> {

    boolean debug = true;

    private boolean checkMonth(String n) {
        return new MonthType().equals(n);
    }

    private boolean checkCourse(String n) {
        return new CourseType().equals(n);
    }

    private boolean checkSum(float n) {
        return n == 100;
    }

    @Override
    public Type visit(Program n) {
        for (Sheet s : n.sheets) {
            if (debug) System.out.print("Sheet: ");
            s.accept(this);
        }
        return null;
    }

    @Override
    public Type visit(Sheet n) {
        if (debug) System.out.println(n.title);
        n.type.accept(this);
        return null;
    }

    @Override
    public Type visit(SheetType n) {
        System.out.println("Not implemented");
        return null;
    }

    @Override
    public Type visit(SSTitle n) {
        System.out.println("Not implemented");
        return null;
    }

    @Override
    public Type visit(AccountBalance n) {
        System.out.println("Not implemented");
        return null;
    }

    @Override
    public Type visit(Block n) {
        return null;
    }

    @Override
    public Type visit(CourseDetailBlock n) {
        float sum = 0;
        for (Map.Entry<String, ExamDetailBlock> e : n.examDetails.entrySet()) {
            sum += e.getValue().weight;
            e.getValue().accept(this);
        }
        if (!checkSum(sum)) {
            System.out.println("Total weight does not add up to 100%");
        }
        return null;
    }

    @Override
    public Type visit(CourseTracker n) {
        n.courses.accept(this);
        return null;
    }

    @Override
    public Type visit(CourseTrackerBlock n) {
        for (Map.Entry<String, CourseDetailBlock> c : n.coursesInformation.entrySet()) {
            if (!checkCourse(c.getKey())) {
                System.out.println("A COURSE is a \"DEPARTMENT KEY\" followed by a \" \" and the \"COURSE ID\"");
                System.exit(1);
            }
            c.getValue().accept(this);
        }
        return null;
    }

    @Override
    public Type visit(Date n) {
        if (!checkMonth(n.month)) {
            System.out.println("A MONTH must be between January - December");
            System.exit(1);
        }
        // Capitalize only first letter
        n.month = n.month.substring(0, 1).toUpperCase() + n.month.substring(1).toLowerCase();
        if (debug) System.out.println(n.month + " " + n.year);
        return null;
    }

    @Override
    public Type visit(DateRange n) {
        n.start.accept(this);
        n.end.accept(this);
        return null;
    }

    @Override
    public Type visit(ExamDetailBlock n) {
        System.out.println("Not implemented");
        return null;
    }

    @Override
    public Type visit(ExpenseDetailBlock n) {
        // Already checked in parser
        return null;
    }

    @Override
    public Type visit(ExpensesBlock n) {
        if (debug) System.out.print("Expenses: ");
        for (Map.Entry<String, ExpenseDetailBlock> e : n.expenseProperties.entrySet()) {
            if (debug) System.out.print(e.getKey() + " ");
            e.getValue().accept(this);
        }
        if (debug) System.out.println();
        return null;
    }

    @Override
    public Type visit(Income n) {
        System.out.println("Not implemented");
        return null;
    }

    @Override
    public Type visit(MonthlyBudget n) {
        if (debug) System.out.println("Monthly Budget: ");
        n.budgetBlock.accept(this);
        return null;
    }

    @Override
    public Type visit(MonthlyBudgetBlock n) {
        n.month.accept(this);
        n.expenses.accept(this);
        return null;
    }

    @Override
    public Type visit(Projected n) {
        n.projectedBlock.accept(this);
        return null;
    }

    @Override
    public Type visit(ProjectedBlock n) {
        n.dateRange.accept(this);
        return null;
    }

    @Override
    public Type visit(Trends n) {
        n.trendsBlock.accept(this);
        return null;
    }

    @Override
    public Type visit(TrendsBlock n) {
        n.range.accept(this);
        n.expensesBlock.accept(this);
        return null;
    }

    @Override
    public Type visit(MonthType n) {
        System.out.println("Not implemented");
        return null;
    }

    @Override
    public Type visit(CourseType n) {
        System.out.println("Not implemented");
        return null; }
}
