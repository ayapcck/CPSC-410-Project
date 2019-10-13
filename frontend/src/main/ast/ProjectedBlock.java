package ast;

import visitor.Visitor;

public class ProjectedBlock extends Block {
    DateRange dateRange;
    Income income;
    ExpensesBlock expensesBlock;
    AccountBalance accountBalance;

    public ProjectedBlock(DateRange dateRange, Income income, ExpensesBlock expensesBlock, AccountBalance accountBalance) {
        super();
        this.dateRange = dateRange;
        this.income = income;
        this.expensesBlock = expensesBlock;
        this.accountBalance = accountBalance;
    }

    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}
