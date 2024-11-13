import java.util.Calendar;

public class EstonianIdCodeSortingAlgorithm {

    // This generateIdCode() method is from the University of Tartu's DSA course.
    static long generateIdCode() {
        java.util.concurrent.ThreadLocalRandom chance = java.util.concurrent.ThreadLocalRandom.current();
        Calendar calendar = new java.util.GregorianCalendar();
        calendar.setTime(new java.util.Date(chance.nextLong(-5364669600000L, 7258024800000L)));
        long code = ((calendar.get(Calendar.YEAR) - 1700) / 100 * 2 - chance.nextInt(2)) * (long) Math.pow(10, 9) +
                calendar.get(Calendar.YEAR) % 100 * (long) Math.pow(10, 7) +
                (calendar.get(Calendar.MONTH) + 1) * (long) Math.pow(10, 5) +
                calendar.get(Calendar.DAY_OF_MONTH) * (long) Math.pow(10, 3) +
                chance.nextLong(1000);
        int sumOfProducts = 0;
        int[] firstPowerWeights = {1, 2, 3, 4, 5, 6, 7, 8, 9, 1};
        for (int i = 0; i < 10; i++) sumOfProducts += code / (long) Math.pow(10, i) % 10 * firstPowerWeights[9 - i];
        int control = sumOfProducts % 11;
        if (control == 10) {
            sumOfProducts = 0;
            int[] secondPowerWeights = {3, 4, 5, 6, 7, 8, 9, 1, 2, 3};
            for (int i = 0; i < 10; i++) sumOfProducts += code / (long) Math.pow(10, i) % 10 * secondPowerWeights[9 - i];
            control = sumOfProducts % 11;
            control = control < 10 ? control : 0;
        }
        return code * 10 + control;
    }

    /** Sorts idCodes by the following, ordered by priority:
     * <ul style="list-style-type:none">
     *     <li>a) older people first;</li>
     *     <li>b) idCode's sequence number (indices 8-10);</li>
     *     <li>c) sex of the person.</li>
     * </ul>
     * @param idCodes the array of idCodes that is needed to be sorted.
     */
    public static void sort(long[] idCodes){
        int max1 = 9998; // from == 3
        int min1 = 1;
        int range1 = max1 - min1 + 1;
        int max2 = 1231; // Method will group centuries/sexes 1-2 -> 0, 3-4 -> 1, 5-6 -> 2, 7-8 -> 3
        int min2 = 101;
        int range2 = max2 - min2 + 1;
        int max3 = 399;
        int min3 = 0;
        int range3 = max3 - min3 + 1;
        int[] count1 = new int[range1];
        int[] count2 = new int[range2];
        int[] count3 = new int[range3];
        long[] output = new long[idCodes.length];
        long[] output2 = new long[idCodes.length];

        for (long l : idCodes) {
            int firstDigit =  (int)(l/10_000_000_000L);
            int group = (firstDigit + (firstDigit << 31 >>> 31) >> 1) -1;
            count1[(int) (l % 10_000L / 10L)*10 + firstDigit - min1]++;
            count2[(int)(l % 100_000_000L / 10_000L)-min2]++;
            count3[100*group+(int)(l%10_000_000_000L / 100_000_000L)]++;
        }

        for (int i = 1; i < count1.length; i++) {
            count1[i] += count1[i - 1];
        }
        for (int i = 1; i < count3.length; i++) {
            count3[i] += count3[i - 1];
        }
        // Max values by each group: 8 99 12 31 999 9
        int dayIndex = 2;
        int monthIndex = 1;
        int index = 1;
        int prevIndex = 0;
        while(index < count2.length) {
            count2[index] += count2[prevIndex];
            prevIndex = index;
            dayIndex++;
            if(dayIndex == 32) {
                dayIndex = 1;
                monthIndex++;
            }
            index = 100*monthIndex+dayIndex-min2;
        }

        for (int i = idCodes.length - 1; i >= 0; i--) {
            output[--count1[(int)(idCodes[i] % 10_000L / 10L)*10 + (int)(idCodes[i]/10_000_000_000L) - min1]] = idCodes[i];
        }
        for (int i = output.length - 1; i >= 0; i--) {
            output2[--count2[(int)(output[i] % 100_000_000L / 10_000L)-min2]] = output[i];
        }
        for (int i = output2.length - 1; i >= 0; i--) {
            int firstDigit =  (int)(output2[i]/10_000_000_000L);
            int group = (firstDigit + (firstDigit << 31 >>> 31) >> 1) -1;
            idCodes[--count3[100*group+(int)(output2[i] % 10_000_000_000L / 100_000_000L)]] = output2[i];
        }
    }

    public static void main(String[] args) {
        System.out.println("---Speed---");
        long[] idCodes = new long[10000000];
        for (int j = 0; j < 20; j++) {
            for (int i = 0; i < idCodes.length; i++) {
                idCodes[i] = generateIdCode();
            }
            long a = System.currentTimeMillis();
            sort(idCodes);
            long b = System.currentTimeMillis();
            System.out.println(b-a + "ms");
        }
        System.out.println("--Control--");
        idCodes = new long[10];
        for (int i = 0; i < idCodes.length; i++) {
            idCodes[i] = generateIdCode();
            System.out.print(idCodes[i]+" ");
        }
        System.out.println("\n---Sorted---");
        sort(idCodes);
        for (long l : idCodes) {
            System.out.print(l+" ");
        }
    }
}
