package operators;

import bplustree.BPlusTreeDeserializer;
import bplustreecomponents.RecordId;
import catalog.DBCatalog;
import models.Table;
import models.Tuple;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Index scan operator created when an attribute which has index on top of it is being used
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 *
 */
public class IndexScanOperator extends ScanOperator {
	private Integer lowKey;
	private Integer highKey;
	private int attrIdx;    // The index of the attribute column.
	private File indexFile; // the index file for deserializer to lacate
	private Boolean isIndexClustered = false;
	private RecordId currentRecordID;
	private BPlusTreeDeserializer dataEntryRetriever;  // tree serializer used for getValue the dataentry

	/**
	 * Constructor for IndexScanOperator
	 *
	 * @param table            Name of the table on which index is created
	 * @param lowkey           the lower bound of indexkeys
	 * @param highkey          the higher bound of indexkeys
	 * @param isIndexClustered flag to indicate if index is clustered or not
	 * @param indexFile        index file
	 */

	public IndexScanOperator(Table table, int attrIdx, Integer lowkey, Integer highkey, Boolean isIndexClustered, File indexFile) {
		super(table);
		this.attrIdx = attrIdx;
		this.lowKey = lowkey;
		this.highKey = highkey;
		this.isIndexClustered = isIndexClustered;
		this.indexFile = indexFile;
		try {
			dataEntryRetriever = new BPlusTreeDeserializer(indexFile, lowkey, highkey);
            currentRecordID = dataEntryRetriever.getNextRecordID();
        } catch (IOException e) {
            e.printStackTrace();
        }

	}

	@Override
	public Tuple getNextTuple() {
		if(this.currentRecordID == null){
			return null;
		}
		if(isIndexClustered){
			Tuple curr = super.currentTable.getNextTuple();
			if (curr != null && (highKey == null || curr.values[attrIdx] < highKey))
                return curr;
			return null;
		} else {
			RecordId temp = currentRecordID;
			try {
                currentRecordID = dataEntryRetriever.getNextRecordID();
            } catch (IOException e) {
                e.printStackTrace();
            }

			return super.currentTable.getNextTuple(temp);
		}
	}

	@Override
	public void reset() {
		currentTable.reset();
	}

	/**
	 * Returns the tableSchema of the table on which the index exists
	 */
	@Override
	public List<String> getSchema() {
		return schema;
	}

    /**
     * For printing the operator in plan
     * @return String form for printing in query plan
     */
	@Override
	public String print() {
		return "IndexScan[" + DBCatalog.getActualTableName(currentTable.tableName) + "]";
	}
}
