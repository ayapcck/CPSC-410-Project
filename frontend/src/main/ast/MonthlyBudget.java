package ast;
import visitor.Visitor;

public class MonthlyBudget extends SheetType {
    public MonthlyBudgetBlock budgetBlock;

    public MonthlyBudget(MonthlyBudgetBlock budgetBlock) {
        super();
        this.budgetBlock = budgetBlock;
    }

    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}