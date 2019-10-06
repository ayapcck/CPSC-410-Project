package ast;
import visitor.Visitor;

public class Program extends AST {

    // TODO: potentially add a list of sheets
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}

