package logicaloperators;

import planbuilders.PhysicalPlanBuilder;

/**
 * Logical Duplicate Elimination Operator
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class LogicalDuplicateEliminationOperator extends LogicalUnaryOperator {

    /**
     * Constructor for Logical Duplicate Elimination Operator
     * @param child the  child.
     *
     * */

    public LogicalDuplicateEliminationOperator(LogicalOperator child) {
        super(child);
    }

    @Override
    public void accept(PhysicalPlanBuilder pb) {
        pb.visit(this);
    }

    @Override
    public String print() {
        return "Duplicate Elimination";
    }

}
