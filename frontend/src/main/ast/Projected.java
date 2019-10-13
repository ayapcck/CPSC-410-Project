package ast;
import visitor.Visitor;

public class Projected extends SheetType {
    public ProjectedBlock projectedBlock;

    public Projected(ProjectedBlock projectedBlock) {
        super();
        this.projectedBlock = projectedBlock;
    }

    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}