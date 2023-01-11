public class LaserPrinter implements ServicePrinter {
    private final String printerID;
    private int currentPaperLevel;
    private int currentTonerLevel;
    private int noOfDocsPrinted;

    // Below attributes used to check whether the action performed was successful or not.
    private boolean paperRefilled = false;
    private boolean tonerReplaced = false;
    private boolean documentPrinted = false;


    public LaserPrinter(String printerID, int currentPaperLevel, int currentTonerLevel, int noOfDocsPrinted) {
        this.printerID = printerID;
        this.currentPaperLevel = currentPaperLevel;
        this.currentTonerLevel = currentTonerLevel;
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
        paperRefilled = false;
        while (document.getNumberOfPages() > currentPaperLevel ||
                document.getNumberOfPages() > currentTonerLevel) {
            if (
                    // if document is larger than the print tray or
                    (document.getNumberOfPages() > Full_Paper_Tray) ||
                    // if document is larger than the toner capacity or
                    (document.getNumberOfPages() > Full_Toner_Level) ||
                    // if document is larger than the minimum toner level and the current toner level is larger than
                    // the minimum toner level
                    (document.getNumberOfPages() > Minimum_Toner_Level && currentTonerLevel > Minimum_Toner_Level)
            ) {
                break;
            }
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        currentPaperLevel -= document.getNumberOfPages();
        currentTonerLevel -= document.getNumberOfPages();
        noOfDocsPrinted ++;
        documentPrinted = true;

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
        tonerReplaced = false;
        while (currentTonerLevel >= Minimum_Toner_Level) {
            if (tried_count > 1) {
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
        if (currentTonerLevel < Minimum_Toner_Level) {
            currentTonerLevel = Full_Toner_Level;
            tonerReplaced = true;
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
        paperRefilled = false;
        while (currentPaperLevel + SheetsPerPack > Full_Paper_Tray) {
            if (tried_count > 1) {
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
        if (currentPaperLevel + SheetsPerPack <= Full_Paper_Tray) {
            currentPaperLevel += SheetsPerPack;
            paperRefilled = true;
        }

        notifyAll();
    }

    // ------------------ Getters ------------------
    public String getPrinterID() {
        return printerID;
    }

    public int getCurrentPaperLevel() {
        return currentPaperLevel;
    }

    public int getCurrentTonerLevel() {
        return currentTonerLevel;
    }

    public int getNoOfDocsPrinted() {
        return noOfDocsPrinted;
    }

    public boolean isPaperRefilled() {
        return paperRefilled;
    }

    public boolean isTonerReplaced() {
        return tonerReplaced;
    }

    public boolean isDocumentPrinted() {
        return documentPrinted;
    }

    // ------------------ toString() ------------------
    @Override
    public synchronized String toString() {
        return "[ " +
                " printerID: '" + printerID + '\'' +
                ", Paper Level: " + currentPaperLevel +
                ", Toner Level: " + currentTonerLevel +
                ", Documents Printed: " + noOfDocsPrinted +
                " ]";
    }
}
