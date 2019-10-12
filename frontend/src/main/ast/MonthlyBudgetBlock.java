package ast;

import visitor.Visitor;

public class MonthlyBudgetBlock extends Block {
    public Date month;
    public ExpensesBlock expenses;

    public MonthlyBudgetBlock(Date date, ExpensesBlock expenses) {
        super();
        this.month = date;
        this.expenses = expenses;
    }
    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}
