package ast;

import visitor.Visitor;

public class AccountBalance extends AST {
    public int balance;

    public AccountBalance(int balance) {
        super();
        this.balance = balance;
    }

    @Override
    public <R> R accept(Visitor<R> v) {
        return null;
    }
}
