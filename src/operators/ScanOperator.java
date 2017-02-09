package operators;

import catalog.DBCatalog;
import models.Table;
import models.Tuple;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Scan Operator class which calls the Table object methods to read from the data files
 * The Scan Operator is ALWAYS created whenever a query is to be executed
 * It does not have any children and directly extends the Operator class
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class ScanOperator extends Operator {

	Table currentTable = null;

	/**
	 * Scan operator constructor
	 * Takes in a table object and then constructs the schema for it by appending table name to every column name
	 *
	 * @param table Table object for which Scan operator has to be created for
	 */
	public ScanOperator(Table table) {
		this.currentTable = table;
		schema = new ArrayList<String>();
		if (currentTable == null || currentTable.tableSchema == null) {
			System.out.println("Table or tableSchema is missing!");
		}
		generateSchema();
	}

	/**
	 * Generate schema for the current table object by appending table name to each column
	 * Add it to the currentSchema member
	 */
	public void generateSchema() {
		for (String column : currentTable.tableSchema) {
			schema.add(currentTable.tableName + '.' + column);
		}
	}

	/**
	 * Method which reads the next tuple of the current table object
	 * @return The next tuple
	 */
	@Override
	public Tuple getNextTuple() {
		return currentTable.getNextTuple();
	}

	@Override
	public void reset() {
		currentTable.reset();

	}

	/**
	 * Get the schema for the current table
	 *
	 * @return The schema for the current table
	 */
	public List<String> getSchema() {
		return schema;
	}

    /**
     * For printing the operator in plan
     * @return String form for printing in query plan
     */
	@Override
	public String print() {
		return "TableScan[" + DBCatalog.getActualTableName(currentTable.tableName) + "]";
	}

    /**
     * For printing the operator in plan
     * @param printStream the print stream
     * @param hyphen the value of num of hyphens
     */
	@Override
	public void printQueryPlan(PrintStream printStream, int hyphen) {
		printHyphens(printStream, hyphen);
		printStream.println(print());
	}

}
