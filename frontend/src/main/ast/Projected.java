package ast;
import visitor.Visitor;

public class Projected extends SheetType {

    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}