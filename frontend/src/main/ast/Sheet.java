package ast;
import visitor.Visitor;

public class Sheet extends AST {
    public final SSTitle title;
    public final SheetType type;

    public Sheet(String name) {
        super();
        this.name = name;
    }

    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}