package visitor;
import ast.*;

public interface Visitor<R> {

    R visit(Program n);

    R visit(Sheet n);

    R visit(SheetType n);

    R visit(SSTitle n);
}