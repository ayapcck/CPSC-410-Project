package ast;

public abstract class AST {
    public abstract <R> R accept(Visitor<R> v);
}