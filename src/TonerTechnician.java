public class TonerTechnician extends Thread {
    private final ServicePrinter printer;
    private final Logger logger;

    public TonerTechnician(ThreadGroup group, ServicePrinter printer, String technicianName) {
        super(group, technicianName);
        this.printer = printer;
        this.logger = Logger.getLogger();
    }

    /*
    This class is very similar to the paper technician class, except it attempts to replace
     the toner three times, using the printer's replaceTonerCartridge( ) method.
     When he/she has finished trying to replace the toner cartridge, print out a message,
     stating it has finished and how many toner cartridges it replaced, e.g.
    Toner Technician Finished, cartridges replaced: 2
     */
    @Override
    public void run() {
        int numberOfReplace = 0;
        for(int i=0; i < 3; i++){
            logger.printRefillingStatus(printer, "TONER", Thread.currentThread().getName());
            printer.replaceTonerCartridge();
            if (((LaserPrinter) printer).isTonerReplaced()) {
                numberOfReplace ++;
                logger.printPrinterStatus(printer, "TONER REFILLED");
            } else {
                logger.printPrinterStatus(printer, "ERROR: TONER REFILLED FAILED");
            }
            try {
                // sleep thread for a random amount of time between 1 and 100 milliseconds (inclusive)
                sleep(Utility.randomThreadSleepTime());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Paper Technician Finished
        logger.printThreadFinishStatus(this, numberOfReplace, "TONER CARTRIDGES REPLACED");
    }

    @Override
    public String toString() {
        return "[ PAPER_TECHNICIAN : " + this.getName() + " ]";
    }
}
