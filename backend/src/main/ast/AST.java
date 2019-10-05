package main.ast;

import main.visitor.Visitor;

public abstract class AST {
    public abstract <R> R accept(Visitor<R> v);
}