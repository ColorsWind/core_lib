package io.github.divios.core_lib.misc;


/**

     * Created by ricardodpsx@gmail.com on 4/01/15.
     * <p>
     * In {@code Fenwick Tree} structure We arrange the array in an smart way to perform efficient <em>range queries and updates</em>.
     * The key point is this: In a fenwick array, each position "responsible" for storing cumulative data of N previous positions (N could be 1)
     * For example:
     * array[40] stores: array[40] + array[39] ... + array[32] (8 positions)
     * array[32] stores: array[32] + array[31] ... + array[1]  (32 positions)
     * <p>
     * <strong>But, how do you know how much positions a given index is "responsible" for?</strong>
     * <p>
     * To know the number of items that a given array position 'ind' is responsible for
     * We should extract from 'ind' the portion up to the first significant one of the binary representation of 'ind'
     * for example, given ind == 40 (101000 in binary), according to Fenwick algorithm
     * what We want is to extract 1000(8 in decimal).
     * <p>
     * This means that array[40] has cumulative information of 8 array items.
     * But We still need to know the cumulative data bellow array[40 - 8 = 32]
     * 32 is  100000 in binnary, and the portion up to the least significant one is 32 itself!
     * So array[32] has information of 32 items, and We are done!
     * <p>
     * So cummulative data of array[1...40] = array[40] + array[32]
     * Because 40 has information of items from 40 to 32, and 32 has information of items from 32 to  1
     * <p>
     * Memory usage:  O(n)
     *
     * @author Ricardo Pacheco
     */
    public class FenwickTree {

    public double[] a;

    private FenwickTree() {
    }

    public static FenwickTree build(double[] in) {
        FenwickTree instance = new FenwickTree();

        instance.a = in.clone();
        for (int i = 1; i < instance.a.length - 1; i++) {
            instance.a[i] += instance.a[i - 1];
        }
        for (int i = instance.a.length - 1; i >= 0; i--) {
            int idx = (i & (i + 1)) - 1;
            if (idx >= 0) {
                instance.a[i] -= instance.a[idx];
            }
        }
        return instance;
    }

    public void update(int index, int val) {
        while (index < a.length) {
            a[index] += val;
            index = index | (index + 1);
        }
    }

    public long get(int r) {
        long res = 0;
        while (r >= 0) {
            res += a[r];
            r = (r & (r + 1)) - 1;
        }
        return res;
    }

    public int size() { return a.length; }

}
