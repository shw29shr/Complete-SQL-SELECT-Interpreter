package catalog;

import fileformats.BinaryTupleReader;
import models.Tuple;

import java.io.*;
import java.util.HashMap;

/**
 *  This is the class which generates the stats.txt file
 *  storing all information about a relation
 *  such as no. of tuples and min and max value in each attribute
 */

/**
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 */
public class GenerateStats {

    BufferedReader bufferedReader;
    BufferedWriter bufferedWriter;
    HashMap<String, TableStatsStructure> stats;
    File statsInfoFile;
	String outputDir;
	String inputDir;
	String tableSchemaDir;

    /**
     * Constructor for GenerateStats class
     */
	public GenerateStats(){
		this.outputDir = DBCatalog.dbDirectory;
		try {
			if(outputDir == null) {
				throw new FileNotFoundException();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		statsInfoFile = new File(outputDir + File.separator + "stats.txt");
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(statsInfoFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.inputDir = DBCatalog.dataDirectory;
        this.tableSchemaDir = DBCatalog.schemaPath;

        try {
            if(inputDir == null || tableSchemaDir == null) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

	/**
	 * Now for every relation, refer the table information class
     * and gather the statistics on each
	 *
	 * @throws IOException
	 */
	public void collectTableStatistics() throws IOException {
        File tableSchema = new File(tableSchemaDir);
        bufferedReader = new BufferedReader(new FileReader(tableSchema));
		stats = new HashMap<>();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			String[] tableInfoLine = line.split("\\s+");
			String tableName = tableInfoLine[0];
			TableStatsStructure tableStatStructure = new TableStatsStructure(tableName, new String[tableInfoLine.length - 1], new int[tableInfoLine.length - 1], new int[tableInfoLine.length - 1]);
			int rowCount = 0;
			int min[] = new int[tableInfoLine.length - 1];
			int max[] = new int[tableInfoLine.length - 1];
			for(int i = 0; i < min.length; i++) {
				min[i] = Integer.MAX_VALUE;
			}
			for(int i = 0; i < max.length; i++) {
				max[i] = Integer.MIN_VALUE;
			}
			File tableData = new File(inputDir + File.separator + tableName);
			BinaryTupleReader binaryTupleReader = new BinaryTupleReader(tableData);
			Tuple tuple = null;
			while((tuple = binaryTupleReader.read()) != null) {
				rowCount++;
				for(int i = 0; i < min.length; i++) {
                    min[i] = Math.min(min[i], tuple.getValue(i));
                    max[i] = Math.max(max[i], tuple.getValue(i));
                }
            }
			bufferedWriter.write(tableName + " ");
			bufferedWriter.write(rowCount + " ");
            for(int i = 1; i < tableInfoLine.length; i++) {
                bufferedWriter.write(tableInfoLine[i] + ",");
                bufferedWriter.write(min[i - 1] + ",");
				bufferedWriter.write(max[i - 1] + " ");
				tableStatStructure.addNewColumnToColumnInfo(tableInfoLine[i], min[i - 1], max[i - 1]);
				tableStatStructure.setRowCount(rowCount);
			}
			stats.put(tableName, tableStatStructure);
			bufferedWriter.newLine();
		}
		bufferedWriter.close();
	}

	/**
	 * returns the statistics collected over a table
     * in the form of a HashMap
     *
     * @return
	 */
	public HashMap<String, TableStatsStructure> getTableStatistics() {
		return stats;
	}


}
