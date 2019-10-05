package ast;
import visitor.Visitor;

public class Sheet extends AST {
    public final SSTitle title;
    public final SheetType type;

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