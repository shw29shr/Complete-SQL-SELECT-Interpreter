package operators;

import helpers.Helpers;
import models.Tuple;
import tuplecomparators.CustomTupleComparator;
import net.sf.jsqlparser.statement.select.OrderByElement;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * Class for Sort Operator. The SortOperator is created when query has ORDER BY clause
 * It sorts the final output of the child and becomes the root operator in such case
 * (given query doesn't do a DISTINCT after that)
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */

public abstract class SortOperator extends UnaryOperator {

    List<Integer> orders = new ArrayList<>();
    List<?> orders_holder;
    CustomTupleComparator tupleComparator = null;
    private Logger logger = Logger.getLogger(SortOperator.class);

    /**
     * Constructor for Sort Operator
     * Iteratively reads next tuple of child and uses Collections.sort() for ordering
     *
     * @param orders The list of ORDER BY elements
     * @param child  Child operator
     */
    protected SortOperator(Operator child, List<?> orders) {
        super(child);
        orders_holder = orders;
        if (!orders.isEmpty()) {
            if (orders.get(0) instanceof OrderByElement) {
                for (Object obj : orders) {
                    OrderByElement obe = (OrderByElement) obj;
                    this.orders.add(Helpers.getAttributePosition(
                            obe.toString(), child.getSchema()));
                }
            } else if (orders.get(0) instanceof Integer) {
                this.orders = (List<Integer>) orders;
            } else
                throw new IllegalArgumentException();
        }

        tupleComparator = new CustomTupleComparator(this.orders);
    }


    public abstract void reset(long idx);

}
