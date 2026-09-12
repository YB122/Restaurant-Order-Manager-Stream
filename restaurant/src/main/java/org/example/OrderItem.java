package org.example;

public class OrderItem {
  private MenuItem menuItem;
  private int quantity;
  private final int id;
  private static int orderItemCount;
  public OrderItem(MenuItem menuItem, int quantity) {
    this.menuItem = menuItem;
    this.id = ++orderItemCount;
    this.quantity = quantity;
  }

  public MenuItem getMenuItem() {
    return menuItem;
  }

  public void setMenuItem(MenuItem menuItem) {
    this.menuItem = menuItem;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public double calculateSubTotal() {
    return menuItem.getPrice() * quantity;
  }

  public int getId() {
    return id;
  }

  @Override
  public String toString() {
    return "OrderItem{" +
            "menuItem=" + menuItem +
            ", quantity=" + quantity +
            ", id=" + id +
            '}';
  }
}
