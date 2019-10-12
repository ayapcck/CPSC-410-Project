package ast;

import visitor.Visitor;

public class SSTitle extends AST {
    public final String value;

    public SSTitle(String name) {
        super();
        this.value = name;
    }

    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}