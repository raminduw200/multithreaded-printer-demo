public class Student extends Thread {
    private final LaserPrinter printer;
    private final Logger logger;

    public Student(ThreadGroup group, LaserPrinter printer, String studentName) {
        super(group, studentName);
        this.printer = printer;
        this.logger = Logger.getLogger();
    }

    /*
    * Student tries to print 5 documents.
    * Sleep random time after each attempt he tries to print the document.
    * Note that the student is not aware of the printer's state,
    * so he/she will attempt to print the document even if it is out of paper or toner.
    * In these cases printing the document will be unsuccessful and printing the "ERROR: PRINTING FAILED"
    * status message.
     */
    @Override
    public void run() {
        Document[] documents = new Document[5];
        int numberOfDocuments = 0; // to keep track of printed documents

        // initialize the documents
        documents[0] = new Document(
                this.getName(), "Concurrent Programming - Coursework I", Utility.randomDocumentSize());
        documents[1] = new Document(
                this.getName(), "Concurrent Programming - Coursework II", Utility.randomDocumentSize());
        documents[2] = new Document(
                this.getName(), "Finite Automata - Assignment II", Utility.randomDocumentSize());
        documents[3] = new Document(
                this.getName(), "Operating System - Semaphore is OS", Utility.randomDocumentSize());
        documents[4] = new Document(
                this.getName(),
                "Data Structures and Algorithms - Booyer More Algorithm",
                Utility.randomDocumentSize()
        );

        // print the documents
        for(Document document: documents){
            logger.printDocumentStatus(printer, document);
            printer.printDocument(document);
            if (printer.isDocumentPrintedSuccess()) {
                numberOfDocuments++;
                logger.printPrinterStatus(printer, "DOCUMENT PRINTED");
            } else {
                logger.printPrinterStatus(printer, "ERROR: DOCUMENT PRINTED FAILED");
            }

            try {
                /*
                * sleep thread for a random amount of time between 1 and 100 milliseconds (inclusive)
                * student sleep 5 more less times than the technicians since to avoid technicians tries to replace
                * resources at first before the student print the documents. Students will continue without printing the
                * document since toner or paper is not available if random time / 5.
                 */
                sleep(Utility.randomThreadSleepTime()/5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        logger.printThreadFinishStatus(this, numberOfDocuments, "DOCUMENTS");
    }

    // ------------------ toString() ------------------
    @Override
    public String toString() {
        return "[ " + this.getName() + " ] : ";
    }
}
