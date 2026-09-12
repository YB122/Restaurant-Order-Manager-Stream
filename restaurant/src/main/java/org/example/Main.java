package org.example;
import java.util.*;

public class Main {
  static public Scanner sc = new Scanner(System.in);
  static public Restaurant restaurant = new Restaurant();

  public static void main(String[] args) {
    boolean exit = true;
    while (exit) {
      menu();
      int choose = readInt("choose one from 1 to 14: ");
      switch (choose) {
        case 1:
          addMenuItem();
          break;
        case 2:
          removeMenuItem();
          break;
        case 3:
          displayAllMenuItems();
          break;
        case 4:
          searchMenuItem();
          break;
        case 5:
          createOrder();
          break;
        case 6:
          addItemToOrder();
          break;
        case 7:
          removeItemFromOrder();
          break;
        case 8:
          displayAllOrders();
          break;
        case 9:
          addOrderToKitchenQueue();
          break;
        case 10:
          processNextOrder();
          break;
        case 11:
          searchOrder();
          break;
        case 12:
          checkOrderStatus();
          break;
        case 13:
          displayCompletedOrders();
          break;
        case 14:
          exit = false;
          System.out.println("Exiting... Goodbye!");
          break;
        default:
          System.out.println("Invalid Choice, choose from 1 to 14 only");
          break;
      }
    }
  }

  static public void menu(){
    System.out.println("Menu");
    System.out.println("1. Add Item");
    System.out.println("2. Remove Item");
    System.out.println("3. Display All Items");
    System.out.println("4. Search Item");
    System.out.println("5. Create Order");
    System.out.println("6. Add Item to Order");
    System.out.println("7. Remove Item from Order");
    System.out.println("8. Display All Orders");
    System.out.println("9. Add Order to Kitchen Queue");
    System.out.println("10. Process Next Order");
    System.out.println("11. Display Order By ID");
    System.out.println("12. Check Order Status");
    System.out.println("13. Display Completed Orders");
    System.out.println("14. Exit");
    System.out.println("====================");
  }

  // ========== Safe Input Helpers ==========
  private static int readInt(String prompt) {
    while (true) {
      System.out.print(prompt);
      String line = sc.nextLine();
      if (line == null) {
        System.out.println("Input cannot be empty");
        continue;
      }
      line = line.trim();
      if (line.isEmpty()) {
        System.out.println("Input cannot be empty, try again");
        continue;
      }
      try {
        return Integer.parseInt(line);
      } catch (NumberFormatException e) {
        System.out.println("Invalid number, please enter a valid integer");
      }
    }
  }

  private static int readPositiveInt(String prompt) {
    while (true) {
      int val = readInt(prompt);
      if (val <= 0) {
        System.out.println("Value must be > 0, try again");
        continue;
      }
      return val;
    }
  }

  private static double readPositiveDouble(String prompt) {
    while (true) {
      System.out.print(prompt);
      String line = sc.nextLine().trim();
      if (line.isEmpty()) {
        System.out.println("Input cannot be empty, try again");
        continue;
      }
      try {
        double v = Double.parseDouble(line);
        if (v <= 0) {
          System.out.println("Price/Value must be > 0, try again");
          continue;
        }
        return v;
      } catch (NumberFormatException e) {
        System.out.println("Invalid number, please enter a valid number");
      }
    }
  }

  private static String readNonEmptyLine(String prompt) {
    while (true) {
      System.out.print(prompt);
      String s = sc.nextLine();
      if (s == null) s = "";
      s = s.trim();
      if (!s.isEmpty()) return s;
      System.out.println("Input cannot be empty, try again");
    }
  }

  // ========== Operations with Validation ==========
  static public void addMenuItem() {
    String itemName = readNonEmptyLine("Enter Item Name: ");
    double price = readPositiveDouble("Enter Item Price: ");
    String category = readNonEmptyLine("Enter Category: ");
    restaurant.addMenuItem(new MenuItem(itemName, price, category));
    System.out.println("Item added successfully");
  }

  static public void  removeMenuItem() {
    int itemID = readPositiveInt("Enter Item ID: ");
    restaurant.removeMenuItem(itemID);
  }

  static public void  displayAllMenuItems() {
    restaurant.displayAllMenuItems();
  }

  static public void  searchMenuItem() {
    int itemID = readPositiveInt("Enter Item ID: ");
    restaurant.searchMenuItem(itemID);
  }

  static public void  createOrder() {
    String customerName = readNonEmptyLine("Enter Customer Name: ");
    restaurant.createOrder(new Order(customerName));
  }

  static public void  addItemToOrder() {
    int orderID = readPositiveInt("Enter Order ID: ");
    int menuItemID = readPositiveInt("Enter Item ID in Menu: ");
    int itemQuantity = readPositiveInt("Enter Item Quantity: ");
    restaurant.addItemToOrder(itemQuantity, orderID, menuItemID);
  }

  static public void  removeItemFromOrder() {
    int orderID = readPositiveInt("Enter Order ID: ");
    int orderItemID = readPositiveInt("Enter Item ID in Order: ");
    restaurant.removeItemFromOrder(orderID, orderItemID);
  }

  static public void  displayAllOrders() {
    restaurant.displayAllOrders();
  }

  static public void  searchOrder() {
    int orderID = readPositiveInt("Enter Order ID: ");
    restaurant.searchOrder(orderID);
  }

  static public void addOrderToKitchenQueue() {
    int orderID = readPositiveInt("Enter Order ID: ");
    restaurant.addOrderToKitchenQueue(orderID);
  }

  static public void processNextOrder() {
    restaurant.processNextOrder();
  }

  static public void checkOrderStatus() {
    int orderID = readPositiveInt("Enter Order ID: ");
    restaurant.checkOrderStatus(orderID);
  }

  static public void displayCompletedOrders() {
    restaurant.displayCompletedOrders();
  }
}
