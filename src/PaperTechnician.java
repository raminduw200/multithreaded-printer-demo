public class PaperTechnician extends Thread {
    private final ServicePrinter printer;
    private final Logger logger;

    public PaperTechnician(ThreadGroup group, ServicePrinter printer, String technicianName) {
        super(group, technicianName);
        this.printer = printer;
        this.logger = Logger.getLogger();
    }


    /*
    * Paper technician tries to refill the printer paper tray 3 times.
    * Sleep random time after each attempt he tries to filter the paper tray.
    * Note that the paper technician is not aware of the printer's state,
    * so he/she will attempt to refill the paper tray even if it is full.
    * In this case filling paperRefill() will be unsuccessful and printing the "ERROR: PAPER REFILLED FAILED"
    * status message.
     */
    @Override
    public void run() {
        int numberOfRefills = 0;
        for(int i=0; i < 3; i++){
            logger.printRefillingStatus(printer, "PAPER", Thread.currentThread().getName());
            printer.refillPaper();
            if (((LaserPrinter) printer).isPaperRefilledSuccess()) {
                numberOfRefills ++;
                logger.printPrinterStatus(printer, "PAPER REFILLED");
            } else {
                logger.printPrinterStatus(printer, "ERROR: PAPER REFILLED FAILED");
            }
            try {
                // sleep thread for a random amount of time between 1 and 1000 milliseconds (inclusive)
                sleep(Utility.randomThreadSleepTime());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Summarize the paper technician's work, e.g. Paper Technician Finished, paper refilled: 2
        logger.printThreadFinishStatus(this, numberOfRefills, "PACKS OF PAPER USED");

    }

    @Override
    public String toString() {
        return "[ Paper" + this.getName() + " ] : ";
    }
}
