
package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Printer class used to print results, settings, and data both into a file and command line.
 * @author Juan David Mendez
 */
public final class Printer {
    // Singleton variables. Only one is active at a time!
    private static Printer FileAndConsolePrinterInstance = null; 
    private static Printer FilePrinterInstance = null; 
    private static Printer ConsolePrinterInstance = null;
    
    double printLevel = Settings.getPrintLevel();
    
    String fileName;
    final String dirName = "Result Logs";
    
    File dir = null;

    private enum PrinterType {
        FILE_AND_CONSOLE, FILE_ONLY, CONSOLE_ONLY  
    }
    
    private Printer(PrinterType aType) {
        if(aType == PrinterType.FILE_AND_CONSOLE || aType == PrinterType.FILE_ONLY) {
            fileName = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss'.txt'").format(new Date());

            dir = new File(dirName);
            dir.mkdirs();

            print(5, "Starting: " + this.getClass());
        }
    }
    
    /**
     * Returns the current non-null version of the Printer class. 
     * 
     * @return 
     */
    public static Printer getCurrentPrinter() {
        if(FileAndConsolePrinterInstance != null) {
            return FileAndConsolePrinterInstance;
        }
        else if(FilePrinterInstance != null) {
            return FilePrinterInstance;
        }
        else if(ConsolePrinterInstance != null) {
            return ConsolePrinterInstance;
        }
        else {
            throw new RuntimeException("No valid Printer currently exists. Please call one of the getInstance methods.");
        }
    }
    
    /**
     * Singleton Pattern x 3. There are 3 versions of the Printer: 
     * one to print just to the console, one to print just to a file, and 
     * one to print to both. 
     * 
     * @return an instance of this class or creates one if none exist
     */
        
    public static Printer getFileAndConsoleInstance() {
      if(FileAndConsolePrinterInstance == null) {
         FileAndConsolePrinterInstance = new Printer(Printer.PrinterType.FILE_AND_CONSOLE);
         ConsolePrinterInstance = null;
         FilePrinterInstance = null;
      }
      return FileAndConsolePrinterInstance;
   }
    
    public static Printer getConsolePrinterInstance() {
        if(ConsolePrinterInstance == null) {
            ConsolePrinterInstance = new Printer(Printer.PrinterType.CONSOLE_ONLY);
            FileAndConsolePrinterInstance = null;
            FilePrinterInstance = null;
        }
        
        return ConsolePrinterInstance;
    }
    
    public static Printer getFilePrinterInstance() {
        if(FilePrinterInstance == null ) {
            FilePrinterInstance = new Printer(Printer.PrinterType.FILE_ONLY);
            FileAndConsolePrinterInstance = null;
            ConsolePrinterInstance = null;
        }
        
        return FilePrinterInstance;
    }
    
    // GETTERS
    public String getFileName() {
        return fileName;
    }
    
    public File getDirFile() {
        return dir;
    }
    /**
     *
     * @param level The level of message, 
     * 1 Required
     * 2 Settings
     * 3 Results
     * 4 Data
     * 5 System
     * @param message To be printed.
     */
    public void print(int level, String message) {
                 
        if(3 >= level)  {
            
            if(FileAndConsolePrinterInstance != null) {
                try(FileWriter fw = new FileWriter(new File(dir,fileName), true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        PrintWriter out = new PrintWriter(bw,true))
                    {
                     out.println(message);
                     System.out.println(message);
                     //out.close();

                } catch (IOException e) {

                }
            }
            else if(FilePrinterInstance != null) {
                try(FileWriter fw = new FileWriter(new File(dir,fileName), true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        PrintWriter out = new PrintWriter(bw,true))
                    {
                     out.println(message);
                    }
                catch(IOException e) {
                    
                }
            }
            else if(ConsolePrinterInstance != null) {
                System.out.println(message);
            }
            else {
                throw new RuntimeException("Invalid call to Printer.print.");
            }
        }
    }
    
    /**
     * This method prints the passed message but prepends it with a Timestamp. 
     * This is mostly useful for debugging but also to see how long various actions
     * are taking. 
     * 
     * @param level The print level
     * @param message The message to be printed
     */
    public void printTS(int level, String message ) {
        String msg = "[" + new Timestamp(System.currentTimeMillis()) + "] - ";
        msg += message;
        print(level, msg);
    }
    
    /**
     * This method is necessary to "reset" the Printer class when running different
     * test cases. This encourages the Java garbage collector to clean it up and give
     * us a new instance.
     */
    public static void destroy() {
        Printer.FileAndConsolePrinterInstance = null;
        Printer.ConsolePrinterInstance = null;
        Printer.FilePrinterInstance = null;
    }
    
    public static boolean isConsolePrinter() {
        return Printer.ConsolePrinterInstance != null;
    }
    
    public static boolean isFileAndConsolePrinter() {
        return Printer.FileAndConsolePrinterInstance != null;
    }
    
    public static boolean isFilePrinter() {
        return Printer.FilePrinterInstance != null;
    }
}
