package ast;
import visitor.Visitor;

public class Trends extends SheetType {
    TrendsBlock trendsBlock;

    public Trends(TrendsBlock trendsBlock) {
        super();
        this.trendsBlock = trendsBlock;
    }
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }
}