package ast;

import visitor.Visitor;
import java.util.Map;

public class ExpensesBlock extends Block {
    public Map<String, ExpenseDetailBlock> expenseProperties;

    public ExpensesBlock(Map<String, ExpenseDetailBlock> expenseMap) {
        this.expenseProperties = expenseMap;
    }

    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}
