package bplustreecomponents;

/**
 * We create a RecordID class which holds the pair <pageID, tupleID>
 * to identify every single unique tuple in the index tree data entry list
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 *
 */

public class RecordId {
    public int pageId;
    public int tupleId;

    /**
     * Constructor
     *
     * @param pageID  pageID which has the associated tuple
     * @param tupleID the tuplesID or the offset of the tuple in the given page
     */

    public RecordId(int pageID, int tupleID) {
        this.pageId = pageID;
        this.tupleId = tupleID;
    }

    /**
     * We override the toString method to return
     * RecordID as (pageID, tupleID). Ex. (3,4)
     */
    @Override
    public String toString() {
        return "(" + pageId + "," + tupleId + ")";
    }

    public int getPageId() {
        return pageId;
    }

    public int getTupleId() {
        return tupleId;
    }
}	
