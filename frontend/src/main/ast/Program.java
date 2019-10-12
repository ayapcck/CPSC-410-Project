package ast;
import visitor.Visitor;

import java.util.List;

public class Program extends AST {
    public List<Sheet> sheetList;

    public Program(List<Sheet> sheets) {
        sheetList = sheets;
    }

    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}