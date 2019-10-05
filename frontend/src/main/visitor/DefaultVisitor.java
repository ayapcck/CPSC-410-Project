package visitor;
import ast.*;

public class DefaultVisitor<R> implements Visitor<R> {

    @Override
    public R visit(Program n) {
        return null;
    }

    @Override
    public R visit(Sheet n) {
        return null;
    }

    @Override
    public R visit(SheetType n) {
        return null;
    }

    @Override
    public R visit(SSTitle n) {
        return null;
    }
}