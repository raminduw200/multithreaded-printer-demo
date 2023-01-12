public class LaserPrinter implements ServicePrinter {
    private final String printerID;
    private int paperLevel;
    private int tonerLevel;
    private int noOfDocsPrinted;

    private final ThreadGroup studentThreadGroup;
    private final ThreadGroup technicianThreadGroup;


    // Below attributes used to check whether the action performed was successful or not.
    private boolean paperRefilledSuccess = false;
    private boolean tonerReplacedSuccess = false;
    private boolean documentPrintedSuccess = false;


    public LaserPrinter(String printerID, int paperLevel, int tonerLevel, int noOfDocsPrinted,
                        ThreadGroup students, ThreadGroup technicians) {
        this.printerID = printerID;
        this.paperLevel = paperLevel;
        this.tonerLevel = tonerLevel;
        this.studentThreadGroup = students;
        this.technicianThreadGroup = technicians;
        this.noOfDocsPrinted = noOfDocsPrinted;
    }

    /*
    * Print the document if the printer has sufficient quantities of paper and toner to be able to print the document.
    * If either the paper or toner (or both) are less than document page count then the document cannot be printed and
    * printing must wait until there is enough of both to print it.
    * Note that there are three important facts to consider when printing the document which is mentioned in the below
    * if statement.
    *   - if document is larger than the print tray or
    *   - if document is larger than the toner capacity or
    *   - if document is larger than the minimum toner level and the current toner level is larger than the minimum
    *     toner level
    * In those cases it will skip the printing document and continue because those actions can not be performed.
     */
    @Override
    public synchronized void printDocument(Document document) {
        documentPrintedSuccess = false;
        while (document.getNumberOfPages() > paperLevel ||
                document.getNumberOfPages() > tonerLevel) {
            if (
                    // if document is larger than the print tray or
                    (document.getNumberOfPages() > Full_Paper_Tray) ||
                    // if document is larger than the toner capacity or
                    (document.getNumberOfPages() > Full_Toner_Level) ||
                    // if document is larger than the minimum toner level and the current toner level is larger than
                    // the minimum toner level, Example:
                    //        Minimum_Toner_Level = 10
                    //        tonerLevel = 11
                    //        getNumberOfPages = 12

                    (document.getNumberOfPages() > Minimum_Toner_Level && tonerLevel > Minimum_Toner_Level) ||
                    // check if technicians are there to refill
                    !isTechniciansActive()
            ) {
                break;
            }
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (document.getNumberOfPages() <= paperLevel && document.getNumberOfPages() <= tonerLevel) {
            paperLevel -= document.getNumberOfPages();
            tonerLevel -= document.getNumberOfPages();
            noOfDocsPrinted ++;
            documentPrintedSuccess = true;
        }

        notifyAll();
    }

    /*
    * Replace the toner cartridge if the toner level is less than Minimum_Toner_Level which is 10.
    * If the toner level is greater than or equal to Minimum_Toner_Level then the toner cartridge cannot be replaced and
    * replacing must wait until the toner level is less than Minimum_Toner_Level.
    * Note that there are two important facts to consider,
    *       - There are 4 students tries to print 5 documents but 1 toner technician tries to refill the toner only 3
    *         times. As mentioned in the above printDocument() method(if statement conditions),
    *         this might lead to deadlocks. Because students and technicians work concurrently and students might have
    *         finished printing while technicians are waiting to replace till the toner/cartridge is below the given
    *         requirement. To avoid that situation, have added a condition to check whether the technician tried to
    *         replace the cartridge. If the technician tried to replace the cartridge more than once then, skip
    *         this turn and continue.
     */
    @Override
    public synchronized void replaceTonerCartridge() {
        int tried_count = 0;
        tonerReplacedSuccess = false;
        while (tonerLevel >= Minimum_Toner_Level) {
            if (tried_count > 1 || !isStudentsActive()) {
                break;
            }
            try {
                wait(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            tried_count++;
        }
        /*
        This is to check whether technician tried more than once, but those tries are not because of the current toner
        level less than minimum toner level of the printer. That is because students have finished printing but
        technicians are waiting to refill but cannot refill since toner/cartridge level is not decreasing.
         */
        if (tonerLevel < Minimum_Toner_Level) {
            tonerLevel = Full_Toner_Level;
            tonerReplacedSuccess = true;
        }

        notifyAll();
    }

    /*
    * Same as replaceTonerCartridge() method.
    * Additional condition is to check whether the refilling paper will not overflow the printers' tray. if not can
    * refill the sheets of papers(50 papers).
     */
    @Override
    public synchronized void refillPaper() {
        int tried_count = 0;
        paperRefilledSuccess = false;
        while (paperLevel + SheetsPerPack > Full_Paper_Tray) {
            if (tried_count > 1 || !isStudentsActive()) {
                break;
            }
            try {
                wait(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            tried_count ++;
        }
        /*
        This is to check whether technician tried more than once, but those tries are not because of the current toner
        level less than minimum toner level of the printer. That is because students have finished printing but
        technicians are waiting to refill but cannot refill since toner/cartridge level is not decreasing.
         */
        if (paperLevel + SheetsPerPack <= Full_Paper_Tray) {
            paperLevel += SheetsPerPack;
            paperRefilledSuccess = true;
        }

        notifyAll();
    }

    public synchronized boolean isTechniciansActive() {
        return this.technicianThreadGroup.activeCount() != 0;
    }

    public synchronized boolean isStudentsActive() {
        return this.studentThreadGroup.activeCount() != 0;
    }

    // ------------------ Getters ------------------
    public synchronized String getPrinterID() {
        return printerID;
    }

    public synchronized int getPaperLevel() {
        return paperLevel;
    }

    public synchronized int getTonerLevel() {
        return tonerLevel;
    }

    public synchronized int getNoOfDocsPrinted() {
        return noOfDocsPrinted;
    }

    public synchronized boolean isPaperRefilledSuccess() {
        return paperRefilledSuccess;
    }

    public synchronized boolean isTonerReplacedSuccess() {
        return tonerReplacedSuccess;
    }

    public synchronized boolean isDocumentPrintedSuccess() {
        return documentPrintedSuccess;
    }

    // ------------------ toString() ------------------
    @Override
    public synchronized String toString() {
        return "[ " +
                " printerID: '" + printerID + '\'' +
                ", Paper Level: " + paperLevel +
                ", Toner Level: " + tonerLevel +
                ", Documents Printed: " + noOfDocsPrinted +
                " ]";
    }
}
