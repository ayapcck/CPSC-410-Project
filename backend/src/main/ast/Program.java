package main.ast;

import main.visitor.Visitor;

public class Program extends AST {

    // TODO: potentially add a list of sheets
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}