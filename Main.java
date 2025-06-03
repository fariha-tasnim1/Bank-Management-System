import java.io.*;
import java.util.*;

abstract class User {
    protected String userID, name;

    public User(String userID, String name) {
        this.userID = userID;
        this.name = name;
    }

    public abstract void displayMenu();
}

class Account {
    private String accountNumber;
    private double balance;

    public Account(String accNum, double initialBalance) {
        this.accountNumber = accNum;
        this.balance = initialBalance;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public void withdraw(double amount) throws Exception {
        if (amount > balance) throw new Exception("Insufficient balance");
        balance -= amount;
    }

    public double getBalance() {
        return balance;
    }
}

class FileManager {
    public static void exportTransaction(String details) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.txt", true))) {
            writer.write(details + "\n");
        } catch (IOException e) {
            System.out.println("File write error: " + e.getMessage());
        }
    }
}

class Customer extends User {
    private Account account;

    public Customer(String userID, String name, Account account) {
        super(userID, name);
        this.account = account;
    }

    public void deposit(double amount) {
        account.deposit(amount);
        FileManager.exportTransaction(name + " deposited: " + amount);
    }

    public void withdraw(double amount) {
        try {
            account.withdraw(amount);
            FileManager.exportTransaction(name + " withdrew: " + amount);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void viewBalance() {
        System.out.println("Current Balance: " + account.getBalance());
    }

    @Override
    public void displayMenu() {
        System.out.println("\n--- Customer Menu ---");
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw");
        System.out.println("3. View Balance");
        System.out.println("4. Logout");
    }

    public String getUserID() {
        return userID;
    }
}

public class Main {
    private static List<Customer> customers = new ArrayList<>();

    // 🔍 Find customer by ID
    public static Customer findCustomerByID(String userID) {
        for (Customer c : customers) {
            if (c.getUserID().equals(userID)) {
                return c;
            }
        }
        return null;
    }

    // ➕ Register new customer
    public static void openNewAccount(Scanner sc) {
        System.out.println("\n=== Open a New Account ===");
        System.out.print("Enter your Name: ");
        String name = sc.nextLine();
        System.out.print("Enter User ID: ");
        String userID = sc.nextLine();

        // Check for duplicate ID
        if (findCustomerByID(userID) != null) {
            System.out.println("User ID already exists! Try again.");
            return;
        }

        System.out.print("Enter Account Number: ");
        String accNumber = sc.nextLine();
        System.out.print("Enter Initial Deposit Amount: ");
        double initialBalance = sc.nextDouble();
        sc.nextLine(); // consume newline

        Account newAcc = new Account(accNumber, initialBalance);
        Customer newCustomer = new Customer(userID, name, newAcc);
        customers.add(newCustomer);

        FileManager.exportTransaction(name + " opened an account with balance: " + initialBalance);
        System.out.println("Account created successfully!");
    }

    // 🔑 Log in
    public static Customer login(Scanner sc) {
        System.out.print("\nEnter your User ID to login: ");
        String userID = sc.nextLine();
        Customer customer = findCustomerByID(userID);
        if (customer == null) {
            System.out.println("No customer found with that ID.");
            return null;
        } else {
            System.out.println("Login successful. Welcome " + customer.name + "!");
            return customer;
        }
    }

    // 🧾 Transaction Menu
    public static void transactionMenu(Customer customer, Scanner sc) {
        int choice;
        do {
            customer.displayMenu();
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter amount to deposit: ");
                    double depositAmount = sc.nextDouble();
                    customer.deposit(depositAmount);
                    break;
                case 2:
                    System.out.print("Enter amount to withdraw: ");
                    double withdrawAmount = sc.nextDouble();
                    customer.withdraw(withdrawAmount);
                    break;
                case 3:
                    customer.viewBalance();
                    break;
                case 4:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 4);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Add 1 existing customer for demo
        Customer afia = new Customer("U001", "Afia", new Account("ACC001", 2000.0));
        customers.add(afia);

        int mainChoice;
        do {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Open New Account");
            System.out.println("2. Login to Existing Account");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            mainChoice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (mainChoice) {
                case 1:
                    openNewAccount(sc);
                    break;
                case 2:
                    Customer loggedInCustomer = login(sc);
                    if (loggedInCustomer != null) {
                        transactionMenu(loggedInCustomer, sc);
                    }
                    break;
                case 3:
                    System.out.println("Thank you for using the system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }

        } while (mainChoice != 3);

        sc.close();
    }
}