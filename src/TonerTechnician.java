public class TonerTechnician extends Thread {
    private final ServicePrinter printer;
    private final Logger logger;

    public TonerTechnician(ThreadGroup group, ServicePrinter printer, String technicianName) {
        super(group, technicianName);
        this.printer = printer;
        this.logger = Logger.getLogger();
    }

    /*
    * Toner technician tries to replace the toner 3 times.
    * Sleep random time after each attempt he tries to replace the toner.
    * Note that the toner technician is not aware of the printer's state,
    *       so he/she will attempt to replace the toner even if it is full,
    *       OR if it is not empty but not less than 10, it will skip the term
    * In these cases replacing toner cartridge will be unsuccessful and printing the "ERROR: TONER REPLACED FAILED"
    * status message.
     */
    @Override
    public void run() {
        int numberOfReplace = 0;
        for(int i=0; i < 3; i++){
            logger.printRefillingStatus(printer, "TONER", Thread.currentThread().getName());
            printer.replaceTonerCartridge();
            if (((LaserPrinter) printer).isTonerReplacedSuccess()) {
                numberOfReplace ++;
                logger.printPrinterStatus(printer, "TONER REPLACED");
            } else {
                logger.printPrinterStatus(printer, "ERROR: TONER REPLACED FAILED");
            }
            try {
                // sleep thread for a random amount of time between 1 and 1000 milliseconds (inclusive)
                sleep(Utility.randomThreadSleepTime());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Summarize the toner technician's work, e.g. Toner Technician Finished, toner replaced: 2
        logger.printThreadFinishStatus(this, numberOfReplace, "TONER CARTRIDGES REPLACED");
    }

    // ------------------ toString() ------------------
    @Override
    public String toString() {
        return "[ PAPER_TECHNICIAN : " + this.getName() + " ]";
    }
}
