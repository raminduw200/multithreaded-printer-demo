public class PaperTechnician extends Thread {
    private final ServicePrinter printer;
    private final Logger logger;

    public PaperTechnician(ThreadGroup group, ServicePrinter printer, String technicianName) {
        super(group, technicianName);
        this.printer = printer;
        this.logger = Logger.getLogger();
    }


    /*
    • Attempt to refill the printer's paper trays three times, using the printer's refillPaper() method.
    • He/she should "sleep" for a random amount of time between each attempt to refill the paper.
    • When he/she has finished trying to refill the paper, print out a message,
    stating it has finished and how many packs of paper it refilled the printer with, e.g.
     */
    /*
     */
    @Override
    public void run() {
        int numberOfRefills = 0;
        // Paper technician attempts to refill papers three times.
        for(int i=0; i < 3; i++){
            logger.printRefillingStatus(printer, "PAPER", Thread.currentThread().getName());
            printer.refillPaper();
            if (((LaserPrinter) printer).isPaperRefilled()) {
                numberOfRefills ++;
                logger.printPrinterStatus(printer, "PAPER REFILLED");
            } else {
                logger.printPrinterStatus(printer, "ERROR: PAPER REFILLED FAILED");
            }
            try {
                // sleep thread for a random amount of time between 1 and 100 milliseconds (inclusive)
                sleep(Utility.randomThreadSleepTime());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Paper Technician Finished
        logger.printThreadFinishStatus(this, numberOfRefills, "PACKS OF PAPER USED");

    }

    @Override
    public String toString() {
        return "[ Paper" + this.getName() + " ] : ";
    }
}
