package logicaloperators;

import planbuilders.PhysicalPlanBuilder;

import java.io.PrintStream;

/**
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 */
public abstract class LogicalOperator {

    /**
     * Has abstract methods of a Logical Operator and implements a print method
     * */
    public abstract String print();

    public abstract void printQueryPlan(PrintStream ps, int val);

    public abstract void accept(PhysicalPlanBuilder ppb);

    protected static void prettyPrint(PrintStream ps, int value) {
        while (value-- > 0)
            ps.print('-');
    }


}
