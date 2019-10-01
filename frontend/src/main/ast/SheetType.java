package ast;
import visitor.Visitor;

public class SheetType extends AST {

    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}