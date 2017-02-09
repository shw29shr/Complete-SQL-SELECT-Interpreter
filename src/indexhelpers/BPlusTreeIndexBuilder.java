package indexhelpers;

import bplustree.BPlusTree;
import catalog.DBCatalog;
import fileformats.BinaryTupleWriter;
import logicaloperators.LogicalOperator;
import logicaloperators.LogicalScanOperator;
import logicaloperators.LogicalSortOperator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.OrderByElement;
import operators.Operator;
import planbuilders.PhysicalPlanBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This class is used for building the actual
 * B+ tree index file for the specified attributes.
 *
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 *
 */

public class BPlusTreeIndexBuilder {
	private Map<String, List<BPlusTreeIndexData>> tableInformation;

	/**
	 * Initializes the IndexManager.
	 */
	public BPlusTreeIndexBuilder() {
		tableInformation = new HashMap<>();
	}

	/**
     * Get the index information from relation and attribute.
     *
     * @param relation
	 * @param attribute
	 * @return the index information.
	 */
	public BPlusTreeIndexData getIndexInformation(String relation, String attribute) {
		List<BPlusTreeIndexData> iis = getIndexInformationList(relation);
		if (iis == null) return null;
        for (BPlusTreeIndexData btid : iis) {
            if (btid.relationIndexColumn.equals(attribute)) return btid;
		}
		return null;
	}


	/**
	 * Get a list of the index information of this relation.
	 *
	 * @param relation
	 * @return list of index information
	 */
	private List<BPlusTreeIndexData> getIndexInformationList(String relation) {
		String originalTable = DBCatalog.getActualTableName(relation);
		if (!tableInformation.containsKey(originalTable)) {
			return null;
		}
		return tableInformation.get(originalTable);
	}

	/**
	 * Add the index information.
     */
	public void addIndexInformation(String relation, String attr, boolean clust, int ord) {

		if (!tableInformation.containsKey(relation)) {
			tableInformation.put(relation, new LinkedList<>());
		}
		List<BPlusTreeIndexData> tableIndexes = tableInformation.get(relation);
		BPlusTreeIndexData info = new BPlusTreeIndexData(clust,ord,relation, attr);
		if (clust) {
			tableIndexes.add(0, info);
		} else {
			tableIndexes.add(info);
		}
	}


	/**
	 * This function creates the B Plus tree
	 */
	public void createBPlusIndex() {
		PhysicalPlanBuilder pb = new PhysicalPlanBuilder();
		File indexFileDirectory = new File(DBCatalog.indexDirectory);
		if (!indexFileDirectory.exists()) {
			indexFileDirectory.mkdir();
		}

		for (String relationName : tableInformation.keySet()) {
			String tabPath = DBCatalog.dataDirectory + relationName;
			List<BPlusTreeIndexData> informationList = tableInformation.get(relationName);
			for (BPlusTreeIndexData bTreeIndexData : informationList) {
				int attributeIndex = DBCatalog.schemas.get(relationName).indexOf(bTreeIndexData.relationIndexColumn);
				String indexPath = DBCatalog.indexDirectory + relationName + '.' + bTreeIndexData.relationIndexColumn;
				if (bTreeIndexData.isIndexClustered) {
					Table tbl = new Table(null, relationName);
					Expression exp = new Column(tbl, bTreeIndexData.relationIndexColumn);
					OrderByElement obe = new OrderByElement();
					obe.setExpression(exp);
					LogicalOperator lop = new LogicalScanOperator(DBCatalog.getTableObject(relationName));
					lop = new LogicalSortOperator(lop, Arrays.asList(obe));
					lop.accept(pb);
					Operator op = pb.getPhysicalOperator();

					try {
						BinaryTupleWriter bw = new BinaryTupleWriter(tabPath);
						op.dump(bw);
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					File indexFile = new File(indexPath);
					BPlusTree blt = new BPlusTree(new File(tabPath),
							attributeIndex, bTreeIndexData.bTreeOrder, indexFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


}
