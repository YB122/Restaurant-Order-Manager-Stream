package org.example;

import java.util.*;

public class Restaurant {
  private List<MenuItem> menuItems = new ArrayList<MenuItem>();
  private List<Order> ordersList = new LinkedList<>();
  private Map<Integer, Order> ordersMap = new HashMap<>();
  private Map<Integer, Order> orderItemsMap = new LinkedHashMap<>();

  public  void addMenuItem(MenuItem menuItem) {
    if (menuItem == null) {
      System.out.println("Cannot add null menu item");
      return;
    }
    if (menuItem.getPrice() <= 0) {
      System.out.println("Price must be > 0");
      return;
    }
    menuItems.add(menuItem);
  }

  public void removeMenuItem(int itemID) {
    if (itemID <= 0) {
      System.out.println("Invalid Item ID: must be > 0");
      return;
    }
    boolean removed = menuItems.removeIf(menuItem -> menuItem.getId() == itemID);
    if(removed) {
      System.out.println("Item removed");
    }else{
    System.out.println("Item with ID: " + itemID + " not found");
    }
  }

  public void displayAllMenuItems(){
    if (menuItems.isEmpty()) {
      System.out.println("No menu items found");
      return;
    }
    menuItems.forEach(System.out::println);
  }

  public void searchMenuItem(int itemID){
    if (itemID <= 0) {
      System.out.println("Invalid Item ID: must be > 0");
      return;
    }
    Optional<MenuItem> result = menuItems.stream().
            filter(item->item.getId() == itemID).
            findFirst();
    if (result.isPresent()) {
    System.out.println("Item with ID: " + result.get() + " not found");
    }else{
    System.out.println("Item with ID: " + itemID + " not found");
    }
  }

  public void createOrder(Order order){
    if (order == null) {
      System.out.println("Cannot create null order");
      return;
    }
    if (order.getCustomerName() == null || order.getCustomerName().trim().isEmpty()) {
      System.out.println("Customer name cannot be empty");
      return;
    }
    ordersMap.put(order.getId(), order);
    System.out.println("Order ID: " + order.getId() + " created");
  }

  public void addItemToOrder(int itemQuantity, int orderID, int menuItemID) {
    if (itemQuantity <= 0) {
      System.out.println("Quantity must be > 0");
      return;
    }
    if (orderID <= 0 || menuItemID <= 0) {
      System.out.println("IDs must be > 0");
      return;
    }
    boolean found = false;
    MenuItem item = null;
    Optional<MenuItem> result = menuItems.stream().filter(menuitem->menuitem.getId() == menuItemID).findFirst();
    if (result.isEmpty()) {
      System.out.println("Item with ID: " + menuItemID + " not found");
      return;
    }

    for (Map.Entry<Integer, Order> order : ordersMap.entrySet()) {
      if (order.getKey()  == orderID) {
        found = true;
        if(order.getValue().getOrderStatus().equals(OrderStatus.CANCELLED) || order.getValue().getOrderStatus().equals(OrderStatus.COMPLETED)) {
          System.out.println("order already cancelled or completed");
          return;
        }
        OrderItem orderItem = new OrderItem(item, itemQuantity);
        order.getValue().addItem(orderItem);
        System.out.println("order added successfully");
        break;
      }
    }

    if (!found) {
      System.out.println("Order with ID: " + orderID + " not found");
      return;
    }
  }

  public void removeItemFromOrder(int orderID, int orderItemID) {
    if (orderID <= 0 || orderItemID <= 0) {
      System.out.println("Invalid IDs: must be > 0");
      return;
    }
    boolean found = false;
    Order orderWanted = ordersMap.get(orderID);
    if(orderWanted == null) {
      System.out.println("Order with ID: " + orderID + " not found");
      return;
    }
    if(orderWanted.getOrderStatus().equals(OrderStatus.COMPLETED) || orderWanted.getOrderStatus().equals(OrderStatus.CANCELLED)) {
      System.out.println("order already completed or cancelled");
      return;
    }
//    for (Map.Entry<Integer, Order> order : ordersMap.entrySet()) {
//      if (order.getKey() == orderID) {
//
//        found = true;
//        orderWanted =  order.getValue();
//        break;
//      }
//    }


//    found = false;

//    assert orderWanted != null;
    for (OrderItem o : orderWanted.getOrderItems()) {
      if(o.getId() == orderItemID) {
        orderWanted.removeItem(o);
        System.out.println("order removed successfully");
        found = true;
        break;
      }
    }

    if (!found) {
      System.out.println("Order Item with ID: " + orderItemID + " not found or not for you");
    }
  }

  public void displayAllOrders() {
    if(ordersMap.isEmpty()) {
      System.out.println("No orders found");
      return;
    }
    ordersMap.values().forEach(System.out::println);
  }

  public void searchOrder(int orderID) {
    if (orderID <= 0) {
      System.out.println("Invalid Order ID: must be > 0");
      return;
    }
    if(ordersMap.isEmpty()) {
      System.out.println("No orders found");
      return;
    }
    Order order = ordersMap.get(orderID);
    if(order == null) {
      System.out.println("Order with ID: " + orderID + " not found");
      return;
    }
    System.out.println(order);
  }

  public void addOrderToKitchenQueue(int orderID) {
    if (orderID <= 0) {
      System.out.println("Invalid Order ID: must be > 0");
      return;
    }
    if(ordersMap.isEmpty()) {
      System.out.println("No orders found");
      return;
    }
    Order order = ordersMap.get(orderID);
    if(order == null) {System.out.println("Order with ID: " + orderID + " not found");}
    else {
      for (Order orders : ordersList) {
        if(orders.getId() == orderID) {
          System.out.println("Order ID: " + orders.getId() + " already exists");
          return;
        }
      }
      if(order.getOrderStatus().equals(OrderStatus.CANCELLED) ||  order.getOrderStatus().equals(OrderStatus.COMPLETED)) {
        System.out.println("order already cancelled or completed");
        return;
      }
      order.setOrderStatus(OrderStatus.IN_KITCHEN);
      ordersList.add(order);
      System.out.println("Order added successfully");
    }
  }

  public void processNextOrder() {
    if(ordersList.isEmpty()) {
      System.out.println("Kitchen queue is empty, no order to process");
      return;
    }
    Order next = ordersList.getFirst();
    if (next == null) {
      System.out.println("Next order is null, cannot process");
      ordersList.removeFirst();
      return;
    }
    next.setOrderStatus(OrderStatus.COMPLETED);
    orderItemsMap.put(next.getId(), next);
    ordersList.removeFirst();
    System.out.println("Order ID " + next.getId() + " processed successfully (COMPLETED)");
  }

  public void checkOrderStatus(int  orderID) {
      if (orderID <= 0) {
        System.out.println("Invalid Order ID: must be > 0");
        return;
      }
      if(ordersMap.isEmpty())  {
         System.out.println("No orders found");
         return;
      }
      Order order = ordersMap.get(orderID);
      if(order == null) {
        System.out.println("Order with ID: " + orderID + " not found");
        return;
      }
     System.out.println("order ID: " + orderID + " found");
     System.out.println("order status is "+ordersMap.get(orderID).getOrderStatus());
  }

  public void displayCompletedOrders() {
    if(orderItemsMap.isEmpty()) {
      System.out.println("No orders found");
      return;
    }
    for (Map.Entry<Integer, Order> order : orderItemsMap.entrySet()) {
      System.out.println(order.getValue().toString());
    }
  }
}
