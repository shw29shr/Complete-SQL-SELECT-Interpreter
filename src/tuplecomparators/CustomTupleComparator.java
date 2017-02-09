package tuplecomparators;

import models.Tuple;
import org.apache.log4j.Logger;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class CustomTupleComparator implements Comparator<Tuple> {
    List<Integer> orders = null;
    HashSet<Integer> set = null;

    public CustomTupleComparator(List<Integer> orders) {
        this.orders = orders;
        this.set = new HashSet<>(orders);
    }

    /**
     * Compare method that has to be overriden.
     * Contains custom logic to compare two tuples t1 and t2 and decide which is smaller than the other.
     *
     * @param tuple1 The first tuple
     * @param tuple2 The second tuple
     * @return 1 if t1>t2, -1 if t1<t2, 0 otherwise
     */
    @Override
    public int compare(Tuple tuple1, Tuple tuple2) {
        if(tuple1.length() != tuple2.length())
            try {
                throw new Exception("Comparing tuples of different lengths");
            } catch (Exception e) {
                return 0;
            }

        for(int idx : orders) {
            int tuple1values = tuple1.values[idx];
            int tuple2values = tuple2.values[idx];
            int checkresult = Integer.compare(tuple1values, tuple2values);
            if (checkresult != 0)
                return checkresult;
        }

        for(int i = 0; i < tuple1.getSize(); i++) {
            if (set.contains(i)) continue;
            int tuple1values = tuple1.values[i];
            int tuple2values = tuple2.values[i];
            int checkresult = Integer.compare(tuple1values, tuple2values);
            if (checkresult != 0)
                return checkresult;
        }

        return 0;
    }
}