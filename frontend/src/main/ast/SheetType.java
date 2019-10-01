package ast;
import visitor.Visitor;

public class SSTitle extends AST {
    public final String name;

    public SSTitle(String name) {
        super();
        this.name = name;
    }

    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}