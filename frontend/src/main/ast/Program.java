package ast;
import visitor.Visitor;

import java.util.List;

public class Program extends AST {
    public SSTitle title;
    public List<Sheet> sheets;

    public Program(SSTitle ssTitle) {
        title = ssTitle;
    }
    public Program(SSTitle ssTitle, List<Sheet> sheets) {
        title = ssTitle;
        this.sheets = sheets;
    }

    // TODO: potentially add a list of sheets
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}