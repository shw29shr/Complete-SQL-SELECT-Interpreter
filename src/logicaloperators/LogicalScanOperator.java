package logicaloperators;

import catalog.DBCatalog;
import models.Table;
import planbuilders.PhysicalPlanBuilder;

import java.io.PrintStream;

/**
 * Logical Scan Operator class
 * It does not have any children and directly extends the Logical Operator class
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class LogicalScanOperator extends LogicalOperator {

	public Table currentTable = null;

	/**
	 * Constructor for Logical Select Operator.
	 * @param tab the table name.
	 * */
	public LogicalScanOperator(Table tab) {
		this.currentTable = tab;
	}

	@Override
	public void accept(PhysicalPlanBuilder pb) {
		pb.visit(this);
	}

	@Override
	public String print() {
        return String.format("Leaf[%s]", DBCatalog.getActualTableName(currentTable.tableName));
    }

	@Override
	public void printQueryPlan(PrintStream ps, int v) {
		prettyPrint(ps, v);
		ps.println(print());
	}

}
