import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

class CSV_run {
    // Method to execute the CSV processing
    public void run() {
        long start = System.nanoTime(); // Record start time

        // Define file paths and names
        String path = "C:/Users/Demch/OneDrive/Рабочий стол/English learning/";
        String curDateFormat = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String nameFile = "Favorites_" + curDateFormat + ".csv";
        String newFileName = "reWords_Transfer " + LocalDate.now() + ".csv";

        CSV_reWord csv = new CSV_reWord(); // Create CSV_reWord object

        try {
            // Perform multithreading CSV processing with predefined column indices
            csv.multithreading(
                    path + nameFile, ';', null, path + newFileName, new int[]{2, 9}, 1, 2, 9, 3, 4);
        } catch (RuntimeException run) { // Handle RuntimeException
            run.printStackTrace(); // Print stack trace if an exception occurs
        } catch (Exception e) { // Handle other exceptions
            System.out.println(e.getMessage()); // Print exception message
            System.out.println("Please write down below a list of columns for connect");
            // Read user input for list of columns to connect
            String[] stringPackInt = new Scanner(System.in).nextLine().split(",");
            // Perform multithreading CSV processing with user-provided column indices
            csv.multithreading(
                    path + nameFile, ';', null, path + newFileName, stringPackInt, 1, 2, 9, 3, 4);
        }

        // Calculate and print the time taken for the processing
        System.out.println("Spent time " + (System.nanoTime() - start) / 1000000 + " milisec");
    }
}
