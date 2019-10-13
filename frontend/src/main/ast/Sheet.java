package ast;
import visitor.Visitor;

public class Sheet extends AST {
    public SheetType type;
    public SSTitle title;

    public Sheet(SheetType type, SSTitle title) {
        super();
        this.type = type;
        this.title = title;
    }

    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}