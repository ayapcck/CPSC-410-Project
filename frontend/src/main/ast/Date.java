package ast;

import visitor.Visitor;

public class Date extends AST {
    public String month;
    public int year;

    public Date(String m, int y) {
        month = m;
        year = y;
    }
    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}
