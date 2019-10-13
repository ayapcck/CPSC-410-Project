package ast;

import visitor.Visitor;

public class TrendsBlock extends Block {
    DateRange range;
    ExpensesBlock expensesBlock;

    public TrendsBlock(DateRange range, ExpensesBlock expensesBlock) {
        this.range = range;
        this.expensesBlock = expensesBlock;
    }
    @Override
    public <R> R accept(Visitor<R> v) {
        return null;
    }
}
