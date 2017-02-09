package logicaloperators;

import java.io.PrintStream;

/**
 * Logical Unary Operator abstract class for operators which will have one child
 * Currently it is implemented by the Logical Select, Logical Sort, Logical Project, Logical DuplicateElimination and Logical HashDuplicateElimination Operators
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */

public abstract class LogicalUnaryOperator extends LogicalOperator {

    public LogicalOperator onlyChild = null;

    protected LogicalUnaryOperator(LogicalOperator child) {

        this.onlyChild = child;
    }

    @Override
    public void printQueryPlan(PrintStream ps, int va) {
        prettyPrint(ps, va);
        ps.println(print());
        onlyChild.printQueryPlan(ps, va + 1);
    }

}
