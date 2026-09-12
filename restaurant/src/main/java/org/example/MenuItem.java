package org.example;

public class MenuItem {
  private final int id;
  private static int itemCount;
  private String name;
  private double price;
  private String category;
  public MenuItem(String name, double price, String category) {
    this.id = ++itemCount;
    this.name = name;
    this.price = price;
    this.category = category;
  }

  public int getId() {
    return id;
  }
  public String getName() {
    return name;
  }

  public static int getItemCount() {
    return itemCount;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  @Override
  public String toString() {
    return "MenuItem{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", price=" + price +
            ", category='" + category + '\'' +
            '}';
  }
}
