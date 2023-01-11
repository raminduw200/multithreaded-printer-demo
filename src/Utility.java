public class Utility {
    // generate random document size(pages count) between 1 and 10 (inclusive)
    public static int randomDocumentSize() {
        int min = 1;
        int max = 10;
        int range = max - min + 1;
        return (int) (Math.random() * range) + min;
    }

    // sleep thread for a random amount of time between 1 and 1000 milliseconds;1 second (inclusive)
    public static int randomThreadSleepTime() {
        return (int) (Math.random() * 100 + 1);
    }
}
