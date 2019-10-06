package ast;
import visitor.Visitor;

public class Program extends AST {
    public SSTitle title;

    public Program(SSTitle ssTitle) {
        title = ssTitle;
    }

    // TODO: potentially add a list of sheets
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}