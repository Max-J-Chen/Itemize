package receipts;
import net.sourceforge.tess4j.TesseractException;
import tess4j.OCR;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Receipt {

    private int numberOfUsers;

    /**
     *
     * @param rawText Raw text from the OCR'd receipts
     * @param userNames String of usernames that the receipt is to be divided among
     *
     * @return A list of item objects, each with their own assigned price, item name, unique id, and list of user objects
     */

    public List<Item> parseReceipt(String rawText, List<String> userNames) {

        // Instantiates regex
        String regexDollar = "((([1-9]\\d{0,2}(,\\d{3})*)|(([1-9]\\d*)?\\d))(\\.\\d\\d))";
        String regexItemID = "\\d{12}";

        // Puts each line into a list
        String[] linesList = rawText.split("\\n");

        // Creates a pattern from regex
        Pattern dollarPattern = Pattern.compile(regexDollar);
        Pattern itemIDPattern = Pattern.compile(regexItemID);

        List<Item> itemList = new ArrayList<Item>();

        // Parses through each line in linesList for dollar pattern
        for (int i = 0; i < linesList.length; i++) {

            // Create a matcher for each line of the array
            Matcher dollarMatcher = dollarPattern.matcher(linesList[i]);
            Matcher itemIDMatcher = itemIDPattern.matcher(linesList[i]);

            String itemPrice = "";
            String itemID = "";
            String itemName = "";

            // Create item object for each line that is a valid item
            if (dollarMatcher.find() && itemIDMatcher.find()) //  Valid items are denoted by the presence of both dollars and unique ID
            {
                //System.out.println("TEST: " + linesList[i]); // delete this <- just a debugger tool
                itemPrice = dollarMatcher.group(1);
                itemID = itemIDMatcher.group(0);
                itemName = linesList[i].substring(0, itemIDMatcher.start()-1);

                itemList.add(new Item(itemName, itemID, itemPrice, userNames));
            }
        }


        // TEST: print each item's name, ID, and price
        /*
        for (Item currentItem : itemList) {
            System.out.println(currentItem.getItemName() + " " +
                    currentItem.getUniqueID() + " " +
                    currentItem.getItemPrice());

        }


         */

        return itemList;
    }

    /**
     * This method prompts the user to enter the number and names of the users that they want to split the receipt among.
     *
     * @return a list of strings that are the names of the users.
     */

    public List<String> nameUsers() { // Need to make it so that a new user object is generated with each item, because each item now just references the same three users.

        // Prompt user to list number of users and names
        System.out.print("How many users do need to split this receipt with? ");

        Scanner scanner = new Scanner(System.in);   // Create a Scanner object
        numberOfUsers = scanner.nextInt();
        System.out.println("Enter the names of the users");
        scanner.nextLine();                         // Consume the remaining newline

        List<String> userList = new ArrayList<String>();
        String userName = "";

        // Ask for names and create users objects
        for (int i = 1; i <= numberOfUsers; i++) {
            System.out.print("User " + i + ": ");
            userName = scanner.nextLine();
            userList.add(userName);
        }
        return userList;
    }

    public void assignPercentages(List<Item> itemList) {

        Scanner scanner = new Scanner(System.in);   // Create a Scanner object
        String response = "";

        System.out.println("Who should this item be split amongst? Type 'y' or 'n' \n");

        // Each item in this list has a list of user objects. Parse through each item, and their user objects, and extract strings for printing
        for (Item currentItem : itemList) {
            // Prints and centers item name and price
            printColumnHeaders();
            System.out.printf("%15s%15s\n",StringUtils.center(currentItem.getItemName(),15), StringUtils.center(currentItem.getItemPrice(),15));

            currentItem.assignOwnership();

        }
    }

    public List<Users> determineTotals(List<Item> itemList, List<String> userNames) {

        // Create objects of users to hold totals
        Item totalItems = new Item(userNames);
        totalItems.setZeroTotal();      // Initialize the hold totals to 0
        List<Users> userTotals = totalItems.getUsers();


        // Parse through each item's user objects and add to item total object
        for (Item currentItem : itemList) {

            List<Users> currentUsers = currentItem.getUsers();
            for(int i = 0; i<numberOfUsers; i++) {

                // Assign the cumulative sum of the returned assigned price to the respective user in totalItems
                userTotals.get(i).setAssignedPrice(currentUsers.get(i).getAssignedPrice()
                                                    + userTotals.get(i).getAssignedPrice());

            }

        }
        return userTotals;
    }

    public void printUserTotals(List<Users> userTotals) {
        System.out.println("Division of Receipt:");
        System.out.println("______________________________________________________");

        for(Users currentUser: userTotals) {
            System.out.println(currentUser.getUserName() + ": " + currentUser.getAssignedPrice());
        }
    }

    public void printColumnHeaders() {

        // Print column headers
        System.out.printf("%15s%15s\n", StringUtils.center("Name",15), StringUtils.center("Price",15));
        System.out.println("______________________________");

    }


    public static void main (String[] args) {

        // Prompt user to pick file
        File imageFile = FileChooser.getFile();
        //File imageFile = new File("D:\\Downloads\\Tess4J\\test\\resources\\test-data\\receipt-ocr-original2.jpeg");

        //System.out.println(imageFile);

        // Pass file to OCR to return a string containing the text line by line
        OCR instance = new OCR();
        try {
            String result = instance.scanFile(imageFile);
            //System.out.println(result);
            Receipt receipt = new Receipt();

            // Ask for number and name of users, create user objects, and return list of users
            List<String> userList = new ArrayList<String>();
            userList = receipt.nameUsers();

            // Parse through receipt and return list of items
            List<Item> itemList = new ArrayList<Item>();
            itemList = receipt.parseReceipt(result, userList);

            // Assign percentages by
            receipt.assignPercentages(itemList);
            receipt.printUserTotals(receipt.determineTotals(itemList,userList));


        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }



    }

}
