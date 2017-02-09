package logicaloperators;

import net.sf.jsqlparser.statement.select.OrderByElement;
import planbuilders.PhysicalPlanBuilder;

import java.util.List;

/**
 * Class for Logical Sort Operator.
 * It extends Logical Unary Operator
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */

public class LogicalSortOperator extends LogicalUnaryOperator {

    public List<OrderByElement> orderElements = null;
    /**
     * Constructor for Logical Sort Operator
     * */

    public LogicalSortOperator(LogicalOperator onlyChild, List<OrderByElement> oe) {
        super(onlyChild);
        this.orderElements = oe;
    }

    @Override
    public void accept(PhysicalPlanBuilder ppb) {
        ppb.visit(this);
    }

    @Override
    public String print() {
        return String.format("Sort%s", ((orderElements == null) ? "[null]" : orderElements.toString()));
    }

}
