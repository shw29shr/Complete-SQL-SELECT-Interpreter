package models;

import fileformats.TupleReader;

import java.io.IOException;
import java.io.PrintStream;

/**
 * The abstraction of a tuple.
 *
 * @author Saarthak Chandra     sc2776
 *         Shweta Shrivastava   ss3646
 *         Vikas P Nelamangala	vpn6
 *
 */
public class Tuple {

    public int[] values = null;

    public TupleReader tupleReader = null;

    /**
     * Constructor for tuple
     *
     * @param tupleValues Integer array read from file which represents tuple
     */
    public Tuple(int[] tupleValues) {
        this.values = tupleValues;
    }

    /**
     * Get the size of the tuple
     *
     * @return Size of the tuple
     */
    public int getSize() {
        return values.length;
    }



    /**
     * Get the value of the i'th column.
     *
     * @param i index
     * @return the value
     */
    public int getValue(int i) {
        return values[i];
    }


    /**
     * Get the length of the tuple.
     *
     * @return the length
     */
    public int length() {
        return values.length;
    }

    /**
     * Hashcode of the tuple.
     *
     * @return the hash code of the string form
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * We override the equals method
     *
     */
    @Override
    public boolean equals(Object obj) {
        Tuple t = (Tuple) obj;

        int length1 = this.values.length;
        int length2 = t.values.length;
        if (length1 != length2)
            return false;

        for (int i = 0; i < length1; i++)
            if (this.values[i] != t.values[i])
                return false;

        return true;
    }

    /**
     * Overriding the toString method by
     * separating each column with commas.
     */
    @Override
    public String toString() {
        if (values.length < 1) return "";
        StringBuilder sb = new StringBuilder(String.valueOf(values[0]));
        int i = 1;
        while (i < values.length) {
            sb.append(',');
            sb.append(String.valueOf(values[i++]));
        }
        return sb.toString();
    }

    /**
     * Dump the tuple's value into a new line
     * in the print stream.
     *
     * @param ps the print stream
     */
    public void dump(PrintStream ps) {
        try {
            String str = toString() + '\n';
            ps.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
