package tuplecomparators;

import models.Tuple;

import java.util.Comparator;
import java.util.List;

/**
 * Class implementing Custom Tuple Comparator for Sort Merge Join to work.
 * Compares a tuple from left table with a tuple from right.
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class SortMergeTupleComparator implements Comparator<Tuple> {

    private List<Integer> leftSchemaAttributeOrders = null;
    private List<Integer> rightSchemaAttributeOrders = null;

    public SortMergeTupleComparator(List<Integer> leftSchemaAttributeOrders, List<Integer> rightSchemaAttributeOrders) {
        this.leftSchemaAttributeOrders = leftSchemaAttributeOrders;
        this.rightSchemaAttributeOrders = rightSchemaAttributeOrders;
    }

    @Override
    public int compare(Tuple left, Tuple right) {
        for (int i = 0; i < leftSchemaAttributeOrders.size(); i++) {
            int leftValue = left.values[leftSchemaAttributeOrders.get(i)];
            int rightValue = right.values[rightSchemaAttributeOrders.get(i)];

            int result = Integer.compare(leftValue, rightValue);
            if (result != 0) return result;
        }

        return 0;
    }

}
