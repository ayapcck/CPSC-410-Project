package ast;

import visitor.Visitor;

public class ExamDetailBlock extends Block {
    public float weight;
    public int count;

    public ExamDetailBlock(float weight, int count) {
        super();
        this.weight = weight;
        this.count = count;
    }

    @Override
    public <R> R accept(Visitor<R> v) {
        return null;
    }
}
