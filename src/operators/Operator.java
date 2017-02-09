package operators;

import fileformats.TupleWriter;
import models.Tuple;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

/**
 * Base Abstract class for all Operators
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */

public abstract class Operator {

    protected List<String> schema = null;

    /**
     * Prints out the hyphens for every level of the tree
     * @param printStream printstream
     * @param hyphen hyphen value currently
     */
    protected static void printHyphens(PrintStream printStream, int hyphen) {
        while (hyphen-- > 0)
            printStream.print('-');
    }

    /**
     * Return the gotoNext valid tuple.
     *
     * @return the gotoNext tuple
     */
    public abstract Tuple getNextTuple();

    /**
     * Return the schema of the current table.
     *
     * @return a list of strings - the schema
     */
    public abstract List<String> getSchema();

    /**
     * Reset the operator to the start.
     */
    public abstract void reset();


    /**
     * Print Every table row If we are calling the FileTupleWriter then we also
     * need to close the buffer For BinaryTupleWriter, it is taken care inside
     * the functionality
     *
     * @param writer The FileWriter or BinaryWriter object which will print the
     *               results
     * @throws IOException
     */

    public void dump(TupleWriter writer) {
        Tuple tp;
        while ((tp = getNextTuple()) != null)
            try {
                writer.dump(tp);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
    }

    public abstract String print();

    /**
     * Print out the query plan
     * @param ps The printstream to print to
     * @param val The value
     */
    public abstract void printQueryPlan(PrintStream ps, int val);
}
