package ast;

import visitor.Visitor;

public class Income extends AST {
    public int incomeValue;

    public Income(int income) {
        incomeValue = income;
    }

    @Override
    public <R> R accept(Visitor<R> v) {
        return null;
    }
}
