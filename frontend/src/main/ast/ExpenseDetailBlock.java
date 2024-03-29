package ast;

import visitor.Visitor;

public class ExpenseDetailBlock extends Block {
    public int budget;
    public boolean track;

    public ExpenseDetailBlock(int budget, boolean track) {
        this.budget = budget;
        this.track = track;
    }
    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public void setTrack( boolean track) {
        this.track = track;
    }
}
