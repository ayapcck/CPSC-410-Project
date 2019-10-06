package ast;
import visitor.Visitor;

public class Sheet extends AST {
    public SSTitle title;
    public SheetType type;

    public Sheet(SSTitle title, SheetType type) {
        super();
        this.title = title;
        this.type = type;
    }

    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}