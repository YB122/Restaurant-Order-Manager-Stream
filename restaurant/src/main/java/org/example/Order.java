package org.example;

import java.util.*;

public class Order {
  private final int id;
  private static int orderCount;
  private String customerName;
  private List<OrderItem> orderItems = new ArrayList<>();
  private double totalPrice;
  private OrderStatus orderStatus;

  public Order(String customerName) {
    this.id = ++orderCount;
    this.customerName = customerName;
    this.totalPrice = 0;
    this.orderStatus = OrderStatus.PENDING;
  }

  public int getId() {
    return id;
  }

  public static int getOrderCount() {
    return orderCount;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public List<OrderItem> getOrderItems() {
    return this.orderItems;
  }

  public double getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(double totalPrice) {
    this.totalPrice += totalPrice;
  }

  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }

  public boolean addItem(OrderItem orderItem) {
    setTotalPrice(orderItem.calculateSubTotal());
    return this.orderItems.add(orderItem);
  }

  public boolean removeItem(OrderItem orderItem) {
    this.totalPrice -= orderItem.calculateSubTotal();
    return this.orderItems.remove(orderItem);
  }

  public double calculateTotal() {
    return orderItems.stream().mapToDouble(OrderItem::calculateSubTotal).sum();
  }

  public void displayOrder() {
    System.out.println("Order ID: " + id);
    System.out.println("Customer: " + customerName);
    System.out.println("Status: " + orderStatus);
    if (orderItems.isEmpty()) {
      System.out.println("No items in order");
    } else {
      for (OrderItem orderItem : this.orderItems) {
        System.out.println(orderItem.getMenuItem().getName() + " x " + orderItem.getQuantity() + " = " + orderItem.calculateSubTotal());
      }
    }
    System.out.println("Total = " + totalPrice);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Order{id=").append(id)
            .append(", customer='").append(customerName).append('\'')
            .append(", status=").append(orderStatus)
            .append(", total=").append(totalPrice)
            .append(", items=[");
    for (int i = 0; i < orderItems.size(); i++) {
      OrderItem oi = orderItems.get(i);
      sb.append(oi.getMenuItem().getName()).append(" x ").append(oi.getQuantity()).append("=").append(oi.calculateSubTotal());
      if (i < orderItems.size() - 1) sb.append(", ");
    }
    sb.append("]}");
    return sb.toString();
  }
}
