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

    public void setCount(int count) {
        this.count = count;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public <R> R accept(Visitor<R> v) {
        return null;
    }
}
