package logicaloperators;

import net.sf.jsqlparser.expression.Expression;
import planbuilders.PhysicalPlanBuilder;

/**
 * Class for Logical Select Operator
 * The SelectOperator will only be created when query has a WHERE condition in it
 * Logical SelectOperator extends the Logical Unary Operator and has one child which is the ScanOperator
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class LogicalSelectOperator extends LogicalUnaryOperator {

    public Expression expression = null;

    /**
     * Constructor for Logical Select Operator
     * @param e Expression
     * @param lo Operator reference
     * */
    public LogicalSelectOperator(LogicalOperator lo, Expression e) {
        super(lo);
        this.expression = e;
    }

    @Override
    public void accept(PhysicalPlanBuilder pb) {
        pb.visit(this);
    }

    @Override
    public String print() {
        return String.format("Select[%s]", ((expression == null) ? "null" : expression.toString()));
    }

}
