package main.ast;
import main.visitor.Visitor;

public class SheetType extends AST {

    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}