package fileformats;

import bplustreecomponents.RecordId;
import models.Tuple;

import java.io.IOException;

/**
 * Interface for TupleReader
 * This object is created to read the tuples from the underlying relations
 * Has 2 concrete implementations -
 * - FileTupleReader - To read tuples from csv files
 * - BinaryTupleReader - To read tuples from binary files
 * Mandates implementation of 2 methods -
 * - getNextTuple() - to read the nect tuple from the underlying relation
 * - reset() - to reset the read head when the first tuple needs to be read again
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */

public interface TupleReader {
    /**
     * getValue the index of the current tuple
     *
     * @return
     * @throws IOException
     */
    Long getIndex() throws IOException;

    /**
     * read the gotoNext tuple from the table at the given rid
     *
     * @param rid the record id specifying the record.
     * @return the current tuple at the rid.
     * @throws IOException If an I/O error occurs while calling the underlying
     *                     reader's read method
     */
    Tuple read(RecordId rid) throws IOException;

    /**
     * read the gotoNext tuple from the table.
     *
     * @return the current tuple the reader is at
     * @throws IOException If an I/O error occurs while calling the underlying
     *                     reader's read method
     */
    Tuple read() throws IOException;

    /**
     * resets the target to the specified position.
     *
     * @throws IOException If an I/O error occurs while calling the underlying
     *                     reader's reset method
     */
    void reset(long index) throws IOException;


    /**
     * resets the target to the start.
     *
     * @throws IOException If an I/O error occurs while calling the underlying
     *                     reader's reset method
     */
    void reset() throws IOException;

    /**
     * closes the target
     *
     * @throws IOException If an I/O error occurs while calling the underlying
     *                     reader's close method
     */
    void close() throws IOException;


}
