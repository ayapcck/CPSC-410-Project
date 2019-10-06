package ast;

import visitor.Visitor;

public class DateRange extends AST {
    public Date start;
    public Date end;

    public DateRange(Date s, Date e) {
        start = s;
        end = e;
    }
    @Override
    public <R> R accept(Visitor<R> v) {
        return null;
    }
}
