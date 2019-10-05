package ast;
import visitor.Visitor;

public class Sheet extends AST {
    public SheetType type;

    public Sheet(SheetType type) {
        super();
        this.type = type;
    }

    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}