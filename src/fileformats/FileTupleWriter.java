package fileformats;

import models.Tuple;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Concrete implementation for TupleWriter To write output tuples as text/csv
 * files
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */

public final class FileTupleWriter implements TupleWriter {
    private File fileName;
    private BufferedWriter bw = null;

    /**
     * Constructor for FileTupleWriter Takes the complete fileName for output
     * and then creates an Output stream on top of it
     *
     * @param file output filename
     */
    public FileTupleWriter(File file) throws IOException {
        this.fileName = file;
        bw = new BufferedWriter(new FileWriter(file));
    }

    public FileTupleWriter(String fileName) throws IOException {
        this(new File(fileName));
    }

    /**
     * Writes the tuple to the fileName.
     *
     * @param tuple the tuple to be written
     */
    @Override
    public void dump(Tuple tuple) throws IOException {
        bw.write(tuple.toString());
        bw.newLine();
    }

    /**
     * Closes the writer.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        bw.close();
    }

}
