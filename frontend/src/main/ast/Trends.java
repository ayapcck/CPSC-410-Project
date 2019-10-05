package ast;
import visitor.Visitor;

public class Trends extends SheetType {

    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}