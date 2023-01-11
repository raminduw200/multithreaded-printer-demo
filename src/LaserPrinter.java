public class LaserPrinter implements ServicePrinter {
    /*
    printer's name/id, the current paper level, the current toner level and the number of documents printed.
     */
    private final String printerID;
    private int currentPaperLevel;
    private int currentTonerLevel;
    private int noOfDocsPrinted;

    // K
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
    Allow students to print "documents" using the printDocument( document) method, provided it has
    sufficient quantities of paper and toner to be able to print the document.
    Assume that to print one page of a document you need 1 sheet of paper and 1 unit of toner.
    Example: the printer can print a 10 page document provided both the paper and toner
    are greater than 10; and that printing this document reduces each by 10.
    If either the paper or toner (or both) are less than 10 then the document cannot be printed and
    printing must wait until there is enough of both to print it.
     */
    @Override
    public synchronized void printDocument(Document document) {
        paperRefilled = false;
        while (document.getNumberOfPages() > currentPaperLevel ||
                document.getNumberOfPages() > currentTonerLevel) { // TODO : What if there is a document more than 250/500
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
    Allow the toner technician to replace the toner cartridge using the replaceTonerCartridge( )
    method, provided it needs to be replaced, i.e. has a toner level of less than 10.
    Assume a toner cartridge can print 500 sheets of paper.
    Example: the printer has a toner level of 9, therefore, the toner cartridge can be replaced.
    But if it has a level of 10, then it cannot be replaced, as it would be a waste of toner,
    and the technician should wait.
    But he or she is only prepared to wait for 5 seconds before checking if it can be replaced it again.
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
        if (currentTonerLevel < Minimum_Toner_Level) {
            currentTonerLevel = Full_Toner_Level;
            tonerReplaced = true;
        }

        notifyAll();
    }

    /*
    Allow the paper technician to refill the paper tray using the refillPaper( ) method.
    Assume there are 50 sheets per pack of paper & the printer can hold up to 250 sheets of paper.
    Example: the printer has 150 sheets of paper, therefore, it can be refilled.
    But if it has 201 sheets of paper, then it cannot be refilled , as it would result in
    251 sheets, and the technician should wait. But he or she is only prepared to wait
    for 5 seconds before checking if it can be refilled it again.
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

        if (currentPaperLevel + SheetsPerPack <= Full_Paper_Tray) {
            currentPaperLevel += SheetsPerPack;
            paperRefilled = true;
        }

        notifyAll();
    }

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

    //    [ PrinterID: lp-CG.24, Paper Level: 35, Toner Level: 310, Documents Printed: 4 ]
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
