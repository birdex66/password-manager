// Import statements
package password;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

// Main class
public class Main {
    // Scanner object for user input
    private static Scanner scan = new Scanner(System.in);
    // Object for setup credentials
    private static setup naurnaur = new setup();
    // Constants for file names
    private static final String DATA_FILE = ".dat";
    private static final String ENCRYPTED_FILE = "encrypted";
    // Flag to indicate if it's a master login
    private static boolean master;
    // String to check username and password
    private static String check = "";
    // Default input username and password
    private static String inputName = "username";
    private static String inputPassword = "password";

    // Main method
    public static void main(String[] args) throws Exception{
        // Object for handling user account operations
        passCreator account = new passCreator();
        boolean access = false;
        
        // Loop for account creation or login
        while(!access) {
            System.out.printf("Press Y for a new account\nPress N to log in\n");
            String choice = scan.next();
            if(choice.equals("Y")) {
                createAccount();
            }else if(choice.equals("N")) {
                access = login();
            }else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
        
        // Main menu
        if(access) {
            int choice= -1;
            System.out.printf("\nopening user files..\n");
            String inName = "";
            String outName = "";
            if(master) {
                inName = account.getProtectedFileName();
                outName = account.getFileName();
            }else {
                inName = ENCRYPTED_FILE + check + DATA_FILE;
                outName = check + DATA_FILE;
            }
            decryptFile(inName, outName, inputPassword);
            clearFile(inName);
            account = openDataFile(outName);
            System.out.print("files opened successfully.\n");
            
            // Loop for user operations
            while(choice != 5) {
                displayMenu();
                choice = scan.nextInt();
                
                // View passwords
                if(choice == 1){
                    account.printAllAccounts();
                    System.out.printf("\nAccount count: %d\n\n",account.getAccountCount() );
                // Add new password
                }else if(choice == 2) {
                    System.out.print("Enter your new username: ");
                    String name = scan.next();
                    System.out.print("Enter your new password: ");
                    String password = scan.next();
                    account.addAccount(name, password);
                // Edit password
                }else if(choice == 3) {
                    System.out.print("Enter the username to edit: ");
                    String user = scan.next();
                    System.out.print("Enter the password to edit: ");
                    String password = scan.next();
                    passNew reference = account.getAccountByName(user, password);
                    if(reference != null) {
                        editPassword(outName, "temp.dat" , password, user, reference);
                    }
                // Remove password
                }else if(choice == 4) {
                    System.out.print("Enter the username to remove: ");
                    String user = scan.next();
                    System.out.print("Enter the password to remove: ");
                    String password = scan.next();
                    passNew reference = account.getAccountByName(user, password);
                    if(reference != null) {
                        account.remove(reference);
                        removePassword(outName, "temp.dat" , password, user);
                    }
                // Exit
                }else if(choice == 5) {
                    encryptFile(outName, inName, inputPassword);
                    clearFile(outName);
                    System.out.printf("\nExiting...\n");
                }else {
                    System.exit(0);
                }
            }
        }
    }

    // Method to edit password
    private static void editPassword(String outFile, String inFile, String pass, String user, passNew reference) throws FileNotFoundException {
        try (Scanner in = new Scanner(new File(outFile));
             PrintWriter out = new PrintWriter(inFile)) {
            while(in.hasNextLine()) {
                String line = in.nextLine();
                if(line.equals(user + " " +  pass)) {
                    System.out.printf("\nEnter your new username: ");
                    String username = scan.next();
                    System.out.print("Enter your new password: ");
                    String password = scan.next();
                    line = username + " " + password;
                    
                    reference.setUsername(username);
                    reference.setPassword(password);
                }
                out.println(line);
            }
            
            in.close();
            out.close();
            
            
            clearFile(outFile);
            copyFile(inFile,outFile);
            clearFile(inFile);
        }
        catch(Exception error) {
            System.out.println("Error while editing password." + error.getMessage());
        }
    }
    
    // Method to remove password
    private static void removePassword(String outFile, String inFile, String pass, String user) throws FileNotFoundException {
        try (Scanner in = new Scanner(new File(outFile));
             PrintWriter out = new PrintWriter(inFile)) {
            while(in.hasNextLine()) {
                String line = in.nextLine();
                if(line.equals(user + " " +  pass)) {
                    out.println();
                }else {
                    out.println(line);
                }
            }
            
            in.close();
            out.close();
            
            
            clearFile(outFile);
            copyFile(inFile,outFile);
            clearFile(inFile);
        }
        catch(Exception error) {
            System.out.println("Error while removing password." + error.getMessage());
        }
    }
    
    // Method to copy file
    private static void copyFile(String inFile, String outFile)throws IOException{
        try (Scanner in = new Scanner(new File(inFile));
             PrintWriter out = new PrintWriter(outFile)) {
                while (in.hasNextLine()) {
                    out.println(in.nextLine());
                }
            }catch (IOException e) {
                System.out.println("Error while copying file: " + e.getMessage());
            }
    }
    
    // Method to create new account
    private static void createAccount() throws Exception {
        System.out.print("Enter your username: ");
        String name = scan.next();
        
        System.out.print("Enter your password\n(must be 16 or less characters):\n ");
        String password = scan.next();
        
        String encryptedFileName = encryptFileName(password,name);
        
        if(fileExists(encryptedFileName)) {
            System.out.println("Username taken, please use a new name");
        }else{
            naurnaur.setUsername(name);
            naurnaur.setPassword(password);
            createUserFile(encryptFileName(password,name));
            createUserFile(ENCRYPTED_FILE + encryptFileName(password,name));
        }
    }
    
    // Method for user login
    private static boolean login() throws Exception{
        System.out.printf("\nEnter your username: ");
        inputName = scan.next();
        
        System.out.print("Enter your password: ");
        inputPassword = scan.next();
        check = encryptFileName(inputPassword,inputName);
        master = inputName.equals("0") && inputPassword.equals("0");

        if(fileExists(check + DATA_FILE) || master) {
            naurnaur.setUsername(inputName);
            naurnaur.setPassword(inputPassword);
            return true;
        }else {
            System.out.printf("\nUsername or password is incorrect.\n\n");
            return false;
        }
    }
        
    // Method to display main menu
    private static void displayMenu() {
        System.out.println();
        System.out.println("Welcome, " + naurnaur.getUsername() + ".");
        System.out.printf ("\n=======Options=======\n");
        System.out.println("1:  View Passwords  ");
        System.out.println("2:   Add Password   ");
        System.out.println("3:  Edit Password  ");
        System.out.println("4:  Remove Password  ");
        System.out.println("5:   Exit Program   ");
    }
    
    // Method to open data file
    private static passCreator openDataFile(String fileName) throws IOException {
        return new passCreator(fileName);
      }
    
    // Method to clear file
    private static void clearFile(String fileName){
        try {
        FileWriter out = new FileWriter(fileName, false);
        out.close();
        }catch(IOException error){
            System.out.println("Error while clearing the file: " + error.getMessage());
        }
    }
    
    // Method to encrypt file
    private static void encryptFile(String inFile, String outFile, String key)throws FileNotFoundException {
        String add = "0000000000000000";
        if(key.length() < 16) {
            key += add.substring(0,16 - key.length());
        }
         try (Scanner in = new Scanner(new File(inFile));
                 PrintWriter out = new PrintWriter(outFile)) {
             
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"),new IvParameterSpec("aaaabbbbccccdddd".getBytes("ASCII")));
            
            while(in.hasNextLine()) {
                String to = in.nextLine();
                byte[] gibb = cipher.doFinal(to.getBytes(StandardCharsets.UTF_8));
                String encryptedBase64 = Base64.getEncoder().encodeToString(gibb);
                out.println(encryptedBase64);
                }
            in.close();
            out.close();
         }catch(Exception error) {
            System.out.println("Error while encrypting file." + error.getMessage());
        }
    }
    
    // Method to decrypt file
    private static void decryptFile(String inFile, String outFile, String key)throws FileNotFoundException {
        String add = "0000000000000000";
        if(key.length() < 16) {
            key += add.substring(0,16 - key.length());
        }
        
         try (Scanner in = new Scanner(new File(inFile));
                 PrintWriter out = new PrintWriter(outFile)) {
             
             
             Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
             cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec("aaaabbbbccccdddd".getBytes("ASCII")));
                 
        while(in.hasNextLine()) {    
            String to = in.nextLine();
            byte[] bbig = Base64.getDecoder().decode(to);
            byte[] decrypted = cipher.doFinal(bbig);
            String decryptedBase64 = new String(decrypted, StandardCharsets.UTF_8);
            out.println(decryptedBase64);
            
        }
        //System.out.println("decrypted");
        in.close();
        out.close();
        }catch(Exception error) {
            System.out.println("Error while decrypting file.Exiting...\n" + error.getMessage());
            error.printStackTrace();
            System.exit(1);
        }
    }
     
    // Method to create user file
    static void createUserFile(String name)throws IOException{
         boolean works;
        File f = new File("C:\\Users\\Sameer\\eclipse-workspace\\password\\"+ name + DATA_FILE);
            works = f.createNewFile();
        if (works) {
                System.out.println("File created successfully.");
            } else {
                System.out.println("File creation failed.");
            }
     }
     
    // Method to check if file exists
    public static boolean fileExists(String fileName){
         File f = new File(fileName);
         return f.exists();
     }
     
    // Method to check if file is empty
    public static boolean fileEmpty(String fileName) {
         File f = new File(fileName + DATA_FILE);
         return f.length() == 0;
     }
     
    // Method to encrypt file name
     public static String encryptFileName(String key, String text) throws Exception {
         String add = "0000000000000000";
         if(key.length() < 16) {
             key += add.substring(0,16 - key.length());
         }
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec("aaaabbbbccccdddd".getBytes("ASCII")));
            byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            String encryptedBase64 = Base64.getEncoder().encodeToString(encrypted);
            return encryptedBase64.replace("/","_");
        }

      /*  public static byte[] decrypt(String key, byte[] encryptedBytes) throws Exception {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec("aaaabbbbccccdddd".getBytes("ASCII")));
            byte[] decrypted = cipher.doFinal(encryptedBytes);
            return decrypted;
        }*/
}