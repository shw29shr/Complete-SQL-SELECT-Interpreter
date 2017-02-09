package logicaloperators;

import net.sf.jsqlparser.expression.Expression;
import planbuilders.PhysicalPlanBuilder;

import java.io.PrintStream;

/**
 * Class for Logical Join Operator
 * Join Operator is created when query has more than one table
 * which are being joined by a given join condition
 * Logical Join Operator extends the Logical Binary operator class
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class LogicalJoinOperator extends LogicalBinaryOperator {

    public Expression exp = null;
    /**
     * Constructor for Logical Duplicate Elimination Operator
     * @param left the left child.
     * @param right the right child
     * @param e the expression
     *
     * */
    public LogicalJoinOperator(LogicalOperator left, LogicalOperator right, Expression e) {
        super(left, right);
        this.exp = e;
    }

    @Override
    public void accept(PhysicalPlanBuilder pb) {
        pb.visit(this);
    }

    @Override
    public String print() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void printQueryPlan(PrintStream ps, int v) {
        throw new UnsupportedOperationException();
    }

}
