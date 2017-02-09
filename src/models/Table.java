package models;

import bplustreecomponents.RecordId;
import fileformats.TupleReader;
import utils.PropertyFileReader;

import java.io.IOException;
import java.util.List;

/**
 * Table object to handle all table level operations Every table will have a
 * name and schema associated with it We also create a buffer through which we
 * connect to the data file of the table and read the tuples
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class Table {

    PropertyFileReader reader = PropertyFileReader.getInstance();
    public String tableName = "";
    public List<String> tableSchema = null;

    private TupleReader ftr = null;

    /**
     * Constructor.
     *
     * @param tableName   the table tableName / alias
     * @param tableSchema the getSchema
     */
    public Table(String tableName, List<String> tableSchema, TupleReader ftr) {
        this.tableName = tableName;
        this.tableSchema = tableSchema;
        this.ftr = ftr;
    }

    /**
     * Get next tuple from the buffer
     * Calls the getNextTuple of the File/Binary Reader object
     *
     * @return Next tuple
     */
    public Tuple getNextTuple() {
        try {
            return ftr.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * take a rid to getValue a tuple
     *
     * @param rid
     * @return tuple
     */
    public Tuple getNextTuple(RecordId rid) {
        try {
            return ftr.read(rid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method to reset the read head in a given table In order to reset, close
     * the existing buffer and reset it again
     * Calls the reset of the File/Binary Reader object
     */
    public void reset() {
        try {
            ftr.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

