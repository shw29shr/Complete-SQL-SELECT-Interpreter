package operators;

import models.Tuple;

/**
 * Operator created when query has a DISTINCT It assumes that the output of
 * child is SORTED therefore query must also have ORDER BY
 * DuplicateEliminationOperator extends the UnaryOperator and has one child the
 * output of which needs to be unique
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class DuplicateEliminationOperator extends UnaryOperator {

    private Tuple latestTuple = null;
    int tuple_counter = 0;
    /**
     * Constructor initialization
     *
     * @param child Child operator
     */
    public DuplicateEliminationOperator(Operator child) {
        super(child);
    }

    /**
     * Since the input is assumed to be sorted, every tuple is checked against
     * the previous tuple If they match, its not returned. If they don't return
     * it.
     *
     * @return Next non-duplicate tuple
     */
    @Override
    public Tuple getNextTuple() {
        tuple_counter++;
        if(latestTuple == null){
            latestTuple = child.getNextTuple();
            return latestTuple;
        }
        else {
            Tuple nonDuplicateTuple = null;
            while((nonDuplicateTuple = child.getNextTuple()) != null)
                if(!checkTuple(nonDuplicateTuple, latestTuple)) break;
            latestTuple = nonDuplicateTuple;
            return nonDuplicateTuple;
        }
    }

    /**
     * Util function to compare if two tuples are equal.
     *
     * @return True if tuples are same , false otherwise
     */
    private boolean checkTuple(Tuple left, Tuple right) {
        for(int i = 0; i < left.getSize(); i++){
            if(left.getValue(i) != right.getValue(i))
                return false;
        }
        return true;
    }

    /**
     * For printing the operator in plan
     * @return String form for printing in query plan
     */
    @Override
    public String print() {
        return "DuplicateElimination";
    }


}

