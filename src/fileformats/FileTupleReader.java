package fileformats;

import bplustreecomponents.RecordId;
import models.Tuple;

import java.io.*;


/**
 * Concrete implementation for TupleReader
 * To read tuples from text/csv files
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */

public final class FileTupleReader implements TupleReader {
    private File fileName;
    private BufferedReader tableBuffer = null;


    /**
     * Constructor to create the FileTupleReader object that will return the
     * next tuple, takes the filename and creates a buffer on top of it
     *
     * @param fileName file with the table data
     */
    public FileTupleReader(File fileName) throws FileNotFoundException {
        this.fileName = fileName;
        tableBuffer = new BufferedReader(new FileReader(this.fileName));
    }

    /**
     * Constructor to create the FileTupleReader object that will return the
     * next tuple, takes the filename and creates a buffer on top of it
     *
     * @param fileName file with the table data
     */
    public FileTupleReader(String fileName) throws FileNotFoundException {
        this(new File(fileName));
    }

    /**
     * Reads a tuple from the file.
     *
     * @return Tuple that is at the current position of the fileName
     */
    @Override
    public Tuple read() throws IOException {
        String line = tableBuffer.readLine();
        if (line == null) return null;
        String[] p = line.split(",");
        int len = p.length;
        int[] columns = new int[len];
        for (int i = 0; i < len; i++) {
            columns[i] = Integer.valueOf(p[i]);
        }
        return new Tuple(columns);
    }

    /**
     * This operation is not supported.
     */
    @Override
    public void reset(long index) throws IOException {
        throw new UnsupportedOperationException("Unsupported !");
    }


    /**
     * Resets the fileName reader to the start of the file.
     */
    @Override
    public void reset() throws IOException {
        if (tableBuffer != null) {
            tableBuffer.close();
        }
        tableBuffer = new BufferedReader(new FileReader(fileName));
    }

    /**
     * Closes the tuple reader.
     */
    @Override
    public void close() throws IOException {
        tableBuffer.close();
    }

    @Override
    public Long getIndex() throws IOException {
        throw new UnsupportedOperationException("Unsupported!");
    }

    @Override
    public Tuple read(RecordId rid) throws IOException {
        throw new UnsupportedOperationException("Unsupported!");
    }

}
