public class Utility {
    // generate random document size(pages count) between 1 and 10 (inclusive)
    public static int randomDocumentSize() {
        return (int) (Math.random() * 10 + 1);
    }

    // sleep thread for a random amount of time between 1 and 1000 milliseconds;1 second (inclusive)
    public static int randomThreadSleepTime() {
        return (int) (Math.random() * 1000 + 1);
    }
}
