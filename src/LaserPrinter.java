public class LaserPrinter implements ServicePrinter {
    /*
    printer's name/id, the current paper level, the current toner level and the number of documents printed.
     */
    private final String printerID;
    private int currentPaperLevel;
    private int currentTonerLevel;
    private int noOfDocsPrinted;

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
        while (document.getNumberOfPages() > currentPaperLevel ||
                document.getNumberOfPages() > currentTonerLevel) { // TODO : What if there is a document more than 250/500
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e); // TODO: catch these
            }
        }

        currentPaperLevel -= document.getNumberOfPages();
        currentTonerLevel -= document.getNumberOfPages();
        noOfDocsPrinted ++;

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
        while (currentTonerLevel > Minimum_Toner_Level) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e); // TODO: catch these
            }
        }

        currentTonerLevel = Full_Toner_Level;

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
        while (currentPaperLevel + SheetsPerPack > Full_Paper_Tray) {
            try {
                wait(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e); // TODO: catch these
            }
        }
        currentPaperLevel += SheetsPerPack;

        notifyAll();
    }

//    [ PrinterID: lp-CG.24, Paper Level: 35, Toner Level: 310, Documents Printed: 4 ]
    @Override
    public String toString() {
        return "[ " +
                "printerID: '" + printerID + '\'' +
                ", Paper Level: " + currentPaperLevel +
                ", Toner Level: " + currentTonerLevel +
                ", Documents Printed: " + noOfDocsPrinted +
                " ]";
    }
}
