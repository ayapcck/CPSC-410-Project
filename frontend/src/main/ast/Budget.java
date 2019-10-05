package ast;
import visitor.Visitor;

public class Budget extends SheetType {

    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}