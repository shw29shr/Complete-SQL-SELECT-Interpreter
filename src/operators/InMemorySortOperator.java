package operators;

import models.Tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * This class implements sorting within memory.
 *
 * @author Saarthak Chandra - sc2776
 * Shweta Shrivastava - ss3646
 * Vikas P Nelamangala - vpn6
 */

public class InMemorySortOperator extends SortOperator {

    private List<Tuple> tuplesList = new ArrayList<>();
    private long index = 0;

    /**
     * Construct a sort operator.
     *
     * @param child  its child
     * @param orders the list of attributes to be ordered
     */
    public InMemorySortOperator(Operator child, List<?> orders) {
        super(child, orders);
        Tuple tp;
        while((tp = child.getNextTuple()) != null)
            tuplesList.add(tp);
        Collections.sort(tuplesList, tupleComparator);
    }

    /**
     * Since the whole table is buffered in memory,
     * we can keep track of the gotoNext index to be read.
     */
    @Override
    public Tuple getNextTuple() {
        if(index >= tuplesList.size())
            return null;
        return tuplesList.get((int) index++);
    }

    /**
     * Zero the current index.
     */
    @Override
    public void reset() {
        index = 0;
    }

    @Override
    public void reset(long idx) {
        if(idx < 0 || idx >= tuplesList.size())
            return;
        index = idx;
    }

    /**
     * For printing the operator in plan
     * @return String form for printing in query plan
     */
    @Override
    public String print() {
        if(orders_holder != null){
            return String.format("InMemSort%s", orders_holder.toString());
        }
        else if(orders != null){
            return String.format("InMemSort%s", orders.toString());
        }
        else {
            return ("InMemSort[]");
        }
    }
}

