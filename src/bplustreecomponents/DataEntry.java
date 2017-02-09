package bplustreecomponents;

import java.util.ArrayList;
import java.util.List;

/**
 * DataEntry class represents the actual data entrie present in the leaf node.
 * It consists of a indexKey and a list of record IDs which have that indexKey value
 *
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 */

public class DataEntry implements Comparable<DataEntry> {
    public int indexKey;
    public ArrayList<RecordId> recordIDList;

    /**
     * Constructor which takes the key field and a list of RecordIDs
     *
     * @param indexKey     the value of the index key
     * @param recordIDList List of Record ids
     */
    public DataEntry(int indexKey, List<RecordId> recordIDList) {
        this.indexKey = indexKey;
        this.recordIDList = new ArrayList<RecordId>(recordIDList);
    }

    /**
     * Compares two DataEntry fields.
     */
    @Override
    public int compareTo(DataEntry d) {
        if(this.indexKey > d.indexKey) return 1;
        else if(this.indexKey < d.indexKey) return -1;
        else return 0;
    }


    /**
     * We override the toString function so that
     * it returns the string representation of each data entry
     * to match with the input index text files as ..
     * <[127:(12,93)]>
     * <[128:(12,4)]>
     * <[129:(16,45)]>
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<[" + indexKey + ":");
        for (RecordId rid : recordIDList) {
            sb.append(rid.toString());
        }
        sb.append("]>");
        return sb.toString();
    }


}
