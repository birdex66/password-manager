// Import statements
package password;

import java.io.*;
import java.util.*;

// passCreator class
class passCreator {
    // ArrayList to store passNew objects
    private ArrayList<passNew> accountList;
    // File name for storing passwords
    private String fileName = "passwords.dat";
    // File name for storing encrypted passwords
    private String protectedFileName = "encryptedPasswords.dat";

    // Constructor to initialize accountList
    public passCreator() {
        accountList = new ArrayList<passNew>();
    }
    
    // Constructor to initialize accountList from a file
    public passCreator(String fileName) throws IOException{
        this.fileName = fileName;
        accountList = new ArrayList<passNew>();
        FileInputStream myFile = new FileInputStream(fileName);
        Scanner myFileReader = new Scanner (myFile);
        
        // Reading usernames and passwords from the file
        while (myFileReader.hasNext()){
            String username = myFileReader.next();
            String password = myFileReader.next();
            addFileAccount(username, password);
         }
         myFileReader.close();
    }
    
    // Method to add a passNew object to accountList
    public void addFileAccount(String username, String password){
        passNew newAccount = new passNew(username, password);
        accountList.add(newAccount);
    }
    
    // Method to add a new account with username and password
    public void addAccount(String username, String password){
        passNew newAccount = new passNew(username, password);
        accountList.add(newAccount);
        addToFile(newAccount);
    }
    
    // Method to add account information to the file
    private void addToFile(passNew account){
        try {
            FileWriter filewriter = new FileWriter(this.fileName,true);
            PrintWriter printwriter = new PrintWriter(filewriter);
            printwriter.println(account.getUsername() + " " + account.getPassword());
            printwriter.close();
            filewriter.close();    
        } catch(IOException error){
            System.out.println("Error saving account to file: " + error.getMessage());
        }
    }
    
    // Getter method for fileName
    public  String getFileName() {
        return this.fileName;
    }
    
    // Getter method for protectedFileName
    public String getProtectedFileName() {
        return this.protectedFileName;
    }
    
    // Method to get the number of accounts
    public int getAccountCount() {
        return accountList.size();
    }
    
    // Method to remove an account from accountList
    public void remove(passNew reference) {
        accountList.remove(reference);
    }
    
    // Method to get passNew object by username and password
    public passNew getAccountByName(String username, String password) {
        for(int i=0; i<accountList.size(); ++i) {
            passNew reference = accountList.get(i);
            String userReference = reference.getUsername();
            String passReference = reference.getPassword();
            int settingReference = userReference.indexOf(username);
            int setReference = passReference.indexOf(password);
            if(settingReference != -1 && setReference != -1) {
                return reference;
            }
        }
        System.out.printf("\nAccount does not exist\n");
        return null;            
    }
    
    // Method to print all accounts
    public void printAllAccounts(){
        if(accountList.size() == 0) {
            System.out.printf("\nNo accounts have been added\n");
            return;
        }
        System.out.printf("\n User   Pass\n");
        for(int i=0; i<accountList.size(); ++i) {
            passNew reference = accountList.get(i);    
            reference.printInfo();
            System.out.println("---------------");
        }
    }
}