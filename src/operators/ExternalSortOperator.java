package operators;

import catalog.ConfigHandler;
import catalog.DBCatalog;
import fileformats.BinaryTupleReader;
import fileformats.BinaryTupleWriter;
import fileformats.TupleReader;
import fileformats.TupleWriter;
import models.Tuple;
import utils.PropertyFileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 */
public class ExternalSortOperator extends SortOperator {

	private final String randomId = UUID.randomUUID().toString().substring(0, 8);
	private final String localDirectory = DBCatalog.tempDirectory + randomId + File.separator;
	private PropertyFileReader reader = PropertyFileReader.getInstance();
	private TupleReader tr = null;
	private List<TupleReader> buffers = new ArrayList<>(ConfigHandler.sortBufferPagesNumber - 1);
	private int tuplesPerPage = 0;

    /**
     * Constructor for external sort
     * @param child child operator
     * @param orders list of ORDER BY elements
     */
	public ExternalSortOperator(Operator child, List<?> orders) {
		super(child, orders);
		new File(localDirectory).mkdirs();
		tuplesPerPage = Integer.parseInt(reader.getProperty("pageSize")) / (Integer.parseInt(reader.getProperty("attributeSize")) * getSchema().size());
		int tpsPerRun = tuplesPerPage * ConfigHandler.sortBufferPagesNumber;
		List<Tuple> tupleList = new ArrayList<>(tpsPerRun);
		int i = 0;
		while (true) {
			try {
				tupleList.clear();
				int cnt = tpsPerRun;
				Tuple tp = null;
				while (cnt-- > 0 && (tp = child.getNextTuple()) != null)
					tupleList.add(tp);
				if (tupleList.isEmpty()) break;
				Collections.sort(tupleList, tupleComparator);
				TupleWriter tw = new BinaryTupleWriter(fileName(0, i++));
				for (Tuple tuple : tupleList)
					tw.dump(tuple);
				tw.close();
				if (tupleList.size() < tpsPerRun)
                    break;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

		if (i == 0) return;
		int curPass = 1;
		int lastRuns = i;
		while (lastRuns > 1)
			lastRuns = merge(curPass++, lastRuns);
		File oldFile = new File(fileName(curPass - 1, 0));
		File newFile = new File(localDirectory + "final");
		oldFile.renameTo(newFile);
		try {
			tr = new BinaryTupleReader(localDirectory + "final");
		} catch (FileNotFoundException e) {
			tr = null;
		}
	}

	@Override
	public Tuple getNextTuple() {
		if (tr == null)
            return null;
		try {
			return tr.read();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void reset() {
		if (tr == null) return;
		try {
			tr.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reset(long idx) {
		if (tr == null) return;
		try {
			tr.reset(idx);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String fileName(int pass, int run) {
		return localDirectory + String.valueOf(pass) + "_" + String.valueOf(run);
	}

	private int merge(int curPass, int lastRuns) {
		int currentRuns = 0;
		int i = 0;
		while (i < lastRuns) {
			buffers.clear();

			int maxJ = Math.min(i + ConfigHandler.sortBufferPagesNumber - 1, lastRuns);
			for (int j = i; j < maxJ; j++) {
				try {
					TupleReader tr = new BinaryTupleReader(fileName(curPass - 1, j));
					buffers.add(tr);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					break;
				}
			}
			try {
				TupleWriter tw = new BinaryTupleWriter(fileName(curPass, currentRuns++));
				PriorityQueue<Tuple> pq = new PriorityQueue<>(ConfigHandler.sortBufferPagesNumber - 1, tupleComparator);
				for (TupleReader tr : buffers) {
					Tuple tp = tr.read();
					if (tp != null) {
						tp.tupleReader = tr;
						pq.add(tp);
					}
				}
				while (!pq.isEmpty()) {
					Tuple tp = pq.poll();
					tw.dump(tp);
					TupleReader tr = tp.tupleReader;
					tp = tr.read();
					if (tp != null) {
						tp.tupleReader = tr;
						pq.add(tp);
					}
				}
				tw.close();
				for (TupleReader tr : buffers)
					tr.close();
				for (int j = i; j < maxJ; j++) {
					File file = new File(fileName(curPass - 1, j));
					file.delete();
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
			i += ConfigHandler.sortBufferPagesNumber - 1;
		}
		return currentRuns;
	}

    /**
     * For printing the operator in plan
     * @return String form for printing in query plan
     */
	@Override
	public String print() {
		if (orders_holder != null) {
			return String.format("ExternalSort%s", orders_holder.toString());
		}
        else if (orders != null) {
			return String.format("ExternalSort%s", orders.toString());
		}
        else {
			return ("ExternalSort[]");
		}
	}

}
