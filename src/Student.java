public class Student extends Thread {
    private final LaserPrinter printer;
    private final Logger logger;
//    Have private data members that hold the information associated with him/her,
//    i.e. the thread group he/she is in; his/her printer; his/her name.

    public Student(ThreadGroup group, LaserPrinter printer, String studentName) {
        super(group, studentName);
        this.printer = printer;
        this.logger = Logger.getLogger();
    }

    /*
    • Create and print five documents with different lengths and names.
    • He/she should "sleep" for a random amount of time between each printing request.
    • When he/she has finished printing, print out a message, e.g.
    Joe Bloggs Finished Printing: 5 Documents, 95 pages
     */
    @Override
    public void run() {
        Document[] documents = new Document[5];
        int numberOfDocuments = 0;
        /*
        Document CWK2 = new Document( "JoeBloggs", "cwk2", 20 ); printer.printDocument( CWK2 ) ;
         */
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

        for(Document document: documents){
            logger.printDocumentStatus(printer, document);
            printer.printDocument(document);
            if (printer.isDocumentPrinted()) {
                numberOfDocuments++;
                logger.printPrinterStatus(printer, "DOCUMENT PRINTED");
            } else {
                logger.printPrinterStatus(printer, "ERROR: DOCUMENT PRINTED FAILED");
            }

            try {
                // sleep thread for a random amount of time between 1 and 100 milliseconds (inclusive)
                sleep(Utility.randomThreadSleepTime()/10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        logger.printThreadFinishStatus(this, numberOfDocuments, "DOCUMENTS");
    }

    @Override
    public String toString() {
        return "[ " + this.getName() + " ] : ";
    }
}
