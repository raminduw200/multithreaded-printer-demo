public class Logger {
    public final String RESET = "\u001B[0m";
    public final String BLACK = "\u001B[30m";
    public final String RED = "\u001B[31m";
    public final String GREEN = "\u001B[32m";
    public final String YELLOW = "\u001B[33m";
    public final String BLUE = "\u001B[34m";
    public final String PURPLE = "\u001B[35m";
    public final String CYAN = "\u001B[36m";
    public final String WHITE = "\u001B[37m";

    private static Logger logger;

    private Logger() {
    }

    public static Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }



    // success color change
    private String setSuccessColor(Boolean success) {
        if (success) {
            return GREEN;
        } else {
            return RED;
        }
    }

    private String setPaperLevelColor(int paperLevel) {
        if (paperLevel < 20) {
            return RED + paperLevel + RESET;
        } else if (paperLevel < 100) {
            return YELLOW + paperLevel + RESET;
        } else {
            return GREEN + paperLevel + RESET;
        }
    }

    private String setTonerLevelColor(int tonerLevel)  {
        if (tonerLevel < 20) {
            return RED + tonerLevel + RESET;
        } else if (tonerLevel < 100) {
            return YELLOW + tonerLevel + RESET;
        } else {
            return GREEN + tonerLevel + RESET;
        }
    }


    public void printWelcomeMessage() {
        System.out.println(GREEN);
        System.out.println("----------------------------------------------------------");
        System.out.println("------------ Welcome to the Printer Simulator! -----------");
        System.out.println("----------------------------------------------------------");
        System.out.println(RESET);
    }

    public void printEndMessage() {
        System.out.println(GREEN);
        System.out.println("----------------------------------------------------------");
        System.out.println("---------------- All Threads Terminated ! ----------------");
        System.out.println("----------------------------------------------------------");
        System.out.println(RESET);
    }

    public synchronized void printThreadCreateStatus(String type, String className, Boolean success) {
        System.out.print(setSuccessColor(success) + "---------");
        System.out.print(" CREATED " + BLUE + "[" + type + "] " + RESET + ": [" + className + "] ");
        System.out.println(RESET);
    }

    public synchronized void printThreadFinishStatus(Thread thread, int count, String units) {
        System.out.print(YELLOW + "---------");
        System.out.print(" [ " + BLUE + thread.getName() + YELLOW + " ] " +
                "FINISHED ALL " + RED + count + YELLOW + " " + units);
        System.out.println(RESET);
    }

    public synchronized void threadStatus(Thread thread) {
        System.out.print("[" + thread.getName() + "] : [ STATUS => ");
        Thread.State state = thread.getState();
        if (state == Thread.State.NEW || state == Thread.State.RUNNABLE) {
            System.out.print(GREEN);
        } else if (state == Thread.State.BLOCKED || state == Thread.State.TERMINATED) {
            System.out.print(RED);
        } else if (state == Thread.State.WAITING || state == Thread.State.TIMED_WAITING) {
            System.out.print(YELLOW);
        }
        System.out.print(state);
        System.out.println(RESET + " ]");
    }

    private String printerStatus(LaserPrinter printer, String status) {
        return "\n[ " +
                " PRINTER: '" + printer.getPrinterID() + '\'' +
                " | STATUS : '" + RED + status + RESET + '\'' +
                " | Paper Level: " + setPaperLevelColor(printer.getPaperLevel()) +
                " | Toner Level: " + setTonerLevelColor(printer.getTonerLevel()) +
                " | Documents Printed: " + printer.getNoOfDocsPrinted() +
                " ]\n";
    }

    private String documentStatus(Document document) {
        return "[ " +
                " PERSON: " + CYAN + document.getUserID() + RESET + " ] \n" +
                "\t [ DOCUMENT: '" + document.getDocumentName() +
                " | Pages: " + document.getNumberOfPages() +
                " ]";
    }

    public synchronized void printDocumentStatus(LaserPrinter printer, Document document) {
        System.out.println(printerStatus(printer, "PRINTING") + " { ");
        System.out.println("\t " + documentStatus(document));
        System.out.println("}");
    }

    public synchronized void printRefillingStatus(Printer printer, String type, String person) {
        System.out.println(printerStatus((LaserPrinter) printer, "REPLACING " + type) + " { ");
        System.out.println("\t [ PERSON : " + CYAN + person + RESET + " ]");
        System.out.println("}");
    }

    public synchronized void printPrinterStatus(Printer printer, String status) {
        System.out.println(printerStatus((LaserPrinter) printer, status));
    }
}
