package operators;

import models.Tuple;

import java.util.HashSet;

/**
 * Operator created when query has a DISTINCT but no ORDER BY clause
 * i.e. the output of the child is NOT sorted
 * HashDuplicateEliminationOperator extends the UnaryOperator and has one child
 * the output of which needs to be unique
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */

public class HashDuplicateEliminationOperator extends UnaryOperator {

    private HashSet<Tuple> set = new HashSet<>();

    /**
     * Constructor Initialization
     *
     * @param child Child operator
     */
    public HashDuplicateEliminationOperator(Operator child) {
        super(child);
    }

    /**
     * Since output is not sorted, we use a HashSet to check if the
     * current tuple is already present or not.
     * If it is not present in the HashSet, add it and return, else continue
     *
     * @return Next non-duplicate tuple
     */
    @Override
    public Tuple getNextTuple() {
        Tuple tp = null;
        while((tp = child.getNextTuple()) != null){
            if (set.contains(tp))
                continue;
            set.add(tp);
            return tp;
        }

        return null;
    }

    /**
     * For printing the operator in plan
     * @return String form for printing in query plan
     */
    @Override
    public String print() {
        return "Hash Duplicate Elimination";
    }

}
