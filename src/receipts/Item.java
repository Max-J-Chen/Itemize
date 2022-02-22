package receipts;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;

public class Item {

    private String itemPrice;
    private String itemName;
    private String uniqueID;
    private List<Users> userObjects = new ArrayList<Users>();
    private List<String> userNames;
    private int numberOfUsers = 0;


    public Item(List<String> userNames) {
        this.createUsers(userNames);
    }

    public Item(String itemName, String uniqueID, String itemPrice, List<String> userNames) {
        this.itemPrice = itemPrice;
        this.itemName = itemName;
        this.uniqueID = uniqueID;
        this.userNames = userNames;
        this.createUsers(userNames);
    }

    public void createUsers(List<String> userNames) {
        for (String name:userNames) {
            userObjects.add(new receipts.Users(name));
        }
    }

    public List<Users> getUsers() {
        return userObjects;
    }

    public void assignOwnership() {
        Scanner scanner = new Scanner(System.in);   // Create a Scanner object
        String response = "";


        // Parse through each item's list of assignable users
            for (Users currentUser : userObjects) {


                    System.out.printf("%10s", currentUser.getUserName() + ": ");
                    response = scanner.nextLine();

                    if (response.equalsIgnoreCase("y")) {
                        currentUser.setIsOwner();
                        numberOfUsers++;
                    }

            }

        calculateUserTotals();
        System.out.println(" ");
        //clrscr();
    }

    public void calculateUserTotals() {
        Double price = Double.parseDouble(itemPrice);

        for (Users currentUser:userObjects) {
            if (currentUser.getIsOwner()) {
                currentUser.setAssignedPrice(price/numberOfUsers);
            } else {
                currentUser.setAssignedPrice(0.00);
            }
        }
    }

    public void setZeroTotal() {
        for (Users currentUser:userObjects) {
            currentUser.setAssignedPrice(0.00);
        }
    }

    public static void clrscr(){

        //Clears Screen in java

        try {

            if (System.getProperty("os.name").contains("Windows"))

                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

            else

                Runtime.getRuntime().exec("clear");

        } catch (IOException | InterruptedException ex) {}

    }



    public String getItemPrice() {
        return itemPrice;
    }

    public String getItemName() {
        return itemName;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    // Returns list of users in form of Strings instead of the User object
    public List<String> getUserNames() {
        return userNames;
    }



}
