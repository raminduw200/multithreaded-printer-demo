public class PrintingSystem {
    public static void main(String[] args) {
        // Initialise the logger
        Logger logger = Logger.getLogger();
        logger.printWelcomeMessage();


        // Create Student thread group, to hold all the student threads
        ThreadGroup studentGroup = new ThreadGroup("Student");
        logger.printThreadCreateStatus("THREAD GROUP", studentGroup.getName(), true);

        // Create Technician thread group to hold all the technician threads; Paper Technician and Toner Technician
        ThreadGroup technicianGroup = new ThreadGroup("Technician");
        logger.printThreadCreateStatus("THREAD GROUP", technicianGroup.getName(), true);


        // Instantiate laser printer that will be used by the technician and the student
        LaserPrinter laserPrinter = new LaserPrinter("Laser Printer", 100, 100,
                0, studentGroup, technicianGroup);
        logger.printThreadCreateStatus("Monitor", laserPrinter.getPrinterID(), true);


        // Create 4 students who are using the laser printer, who belongs to Student thread group
        Student student1 = new Student(studentGroup, laserPrinter, "Student 1");
        logger.printThreadCreateStatus("THREAD", student1.getName(), true);

        Student student2 = new Student(studentGroup, laserPrinter, "Student 2");
        logger.printThreadCreateStatus("THREAD", student2.getName(), true);

        Student student3 = new Student(studentGroup, laserPrinter, "Student 3");
        logger.printThreadCreateStatus("THREAD", student3.getName(), true);

        Student student4 = new Student(studentGroup, laserPrinter, "Student 4");
        logger.printThreadCreateStatus("THREAD", student4.getName(), true);



        // Create paper technician; supply papers for the printer, who belongs to Technician group.
        PaperTechnician paperTechnician = new PaperTechnician(technicianGroup, laserPrinter, "Paper Technician");
        logger.printThreadCreateStatus("THREAD", paperTechnician.getName(), true);

        // Create toner technician; supply toner for the printer, who belongs to Technician group.
        TonerTechnician tonerTechnician = new TonerTechnician(technicianGroup, laserPrinter, "Toner Technician");
        logger.printThreadCreateStatus("THREAD", tonerTechnician.getName(), true);



        // start the thread
        student1.start();
        logger.threadStatus(student1);

        student2.start();
        logger.threadStatus(student2);

        student3.start();
        logger.threadStatus(student3);

        student4.start();
        logger.threadStatus(student4);

        paperTechnician.start();
        logger.threadStatus(paperTechnician);

        tonerTechnician.start();
        logger.threadStatus(tonerTechnician);

        // Wait for all the threads to finish
        try {
            student1.join();
            student2.join();
            student3.join();
            student4.join();
            paperTechnician.join();
            tonerTechnician.join();
            logger.threadStatus(student1);
            logger.threadStatus(student2);
            logger.threadStatus(student3);
            logger.threadStatus(student4);
            logger.threadStatus(paperTechnician);
            logger.threadStatus(tonerTechnician);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // In addition, it must print out reports of what it is doing and when it has finished creating the threads and
        // other objects, etc
        logger.printEndMessage();
    }
}