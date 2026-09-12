# Restaurant Order Manager — Java Collections

> Console-based Restaurant Order Management System built to master `ArrayList`, `LinkedList`, `HashMap` and `LinkedHashMap` with distinct responsibilities. This is the complete implementation for **MONTH-9 / DAY-1 — Restaurant** assignment.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Tech Stack](#tech-stack)
3. [Project Structure](#project-structure)
4. [Collection Design — Why Each Collection?](#collection-design--why-each-collection)
5. [Core Domain Models](#core-domain-models)
   - [MenuItem.java](#1-menuitemjava)
   - [OrderItem.java](#2-orderitemjava)
   - [Order.java](#3-orderjava)
   - [OrderStatus.java](#4-orderstatusjava)
6. [Restaurant.java — The Manager](#restaurantjava--the-manager)
7. [Main.java — CLI & Input Handling](#mainjava--cli--input-handling)
8. [Order Lifecycle](#order-lifecycle)
9. [Features — 14 Menu Operations](#features--14-menu-operations)
10. [Input Validation & Error Handling](#input-validation--error-handling)
11. [How to Run](#how-to-run)
12. [Example Session (input.txt / output.txt)](#example-session-inputtxt--outputtxt)
13. [Assignment Requirements Coverage](#assignment-requirements-coverage)
14. [Known Issues / Code Review Notes](#known-issues--code-review-notes)
15. [Possible Improvements](#possible-improvements)
16. [Learning Outcomes](#learning-outcomes)

---

## Project Overview

**Goal:** Build a console Restaurant Order Manager where each Java collection type has a *single, justified purpose*.

*   Menu browsing & management ? **ArrayList**
*   Kitchen FIFO queue ? **LinkedList**
*   Permanent order registry with O(1) lookup by ID ? **HashMap**
*   Completed orders in completion order ? **LinkedHashMap**

Additional constraints: no arrays, no DB/GUI, order lifecycle controlled by an `OrderStatus` enum, IDs must be unique, completed/cancelled orders **stay** in the `HashMap`, invalid IDs / empty collections must not crash the program.

Assignment spec: `Restaurant_Order_Manager_Collections_Assignment(5).md` / `.pdf` at repo root.

---

## Tech Stack

| Layer | Detail |
|---|---|
| **Language** | Java (Maven `maven.compiler.source/target = 26`, UTF-8) |
| **Build** | Maven — `restaurant/pom.xml` (`groupId: org.example`, `artifactId: restaurant`, `version: 1.0-SNAPSHOT`) |
| **IDE** | IntelliJ IDEA (`.idea/` — `misc.xml`, `compiler.xml`, `workspace.xml`, `Restaurant.iml`) |
| **Collections** | `java.util.ArrayList`, `LinkedList`, `HashMap`, `LinkedHashMap`, `Optional`, Streams |
| **I/O** | `java.util.Scanner` on `System.in` |
| **OS** | Tested on Windows, path `D:\ROUTE\JAVA\COURSE-CODE\MONTH-9\DAY-1\Restaurant` |

---

## Project Structure

```text
Restaurant/
+-- Restaurant_Order_Manager_Collections_Assignment(5).md   # Full assignment spec
+-- Restaurant_Order_Manager_Collections_Assignment(5).pdf
+-- .idea/                                                 # IntelliJ config
¦   +-- compiler.xml
¦   +-- encodings.xml
¦   +-- jarRepositories.xml
¦   +-- misc.xml
¦   +-- workspace.xml
¦   +-- Restaurant.iml
+-- restaurant/                                            # Maven module
    +-- pom.xml                                            # Java 26, UTF-8
    +-- src/
    ¦   +-- main/
    ¦   ¦   +-- java/org/example/
    ¦   ¦   ¦   +-- Main.java         # 195 lines — CLI loop + safe input helpers
    ¦   ¦   ¦   +-- Restaurant.java   # ~195 lines — business logic & 4 collections
    ¦   ¦   ¦   +-- MenuItem.java     # Value object, auto-increment ID
    ¦   ¦   ¦   +-- Order.java        # Aggregate of OrderItems, status, total
    ¦   ¦   ¦   +-- OrderItem.java    # Join: MenuItem + quantity
    ¦   ¦   ¦   +-- OrderStatus.java  # Enum PENDING/IN_KITCHEN/COMPLETED/CANCELLED
    ¦   ¦   ¦   +-- example.zip       # Zipped snapshot of the 6 source files
    ¦   ¦   +-- resources/            # (empty)
    ¦   +-- test/java/                # (empty, placeholder)
    +-- target/
        +-- classes/org/example/*.class
        +-- input.txt                 # Sample simulated console input
        +-- output.txt                # Captured console output for that input
```

> **Package:** `org.example` for all domain classes.

---

## Collection Design — Why Each Collection?

| Field in `Restaurant.java` | Declared Type | Concrete Type | Purpose | Why This Collection? |
|---|---|---|---|---|
| `menuItems` | `List<MenuItem>` | `new ArrayList<>()` | All menu items | Random/index access, iteration, `removeIf`, stream filter. Menu is a list you display/search sequentially. `ArrayList` is optimal for indexed reads. |
| `ordersList` | `List<Order>` | `new LinkedList<>()` | Kitchen queue (waiting orders) | FIFO. `LinkedList` gives O(1) `addLast` / `removeFirst` / `getFirst`. Used as a queue (`ordersList:add`, `getFirst` + `removeFirst` in `processNextOrder()`). |
| `ordersMap` | `Map<Integer,Order>` | `new HashMap<>()` | **Permanent registry** of *every* order (PENDING / IN_KITCHEN / COMPLETED / CANCELLED) | O(1) lookup by `orderId` (`get`, `put`). Assignment rule: *never remove* from this map; status field tells the story. `searchOrder()`, `checkOrderStatus()`, `addItemToOrder()` all use it. |
| `orderItemsMap` | `Map<Integer,Order>` | `new LinkedHashMap<>()` | Completed orders in completion order | `LinkedHashMap` preserves **insertion order** ? `displayCompletedOrders()` shows orders exactly in the order they were processed. `HashMap` would lose that. |

> **Naming note:** In source `orderItemsMap` would be clearer as `completedOrders`. Likewise `ordersList` as `kitchenQueue`.

---

## Core Domain Models

### 1. `MenuItem.java`

**File:** `restaurant/src/main/java/org/example/MenuItem.java`

```java
private final int id;          // auto-increment via static itemCount
private static int itemCount;  // shared counter ? id = ++itemCount
private String name;
private double price;
private String category;
public MenuItem(String name, double price, String category)
```

*   `id` is **final** + auto-generated — caller cannot set a custom ID (IDs guaranteed unique, no duplicate check needed at construction).
*   Getters for all fields, setters for mutable fields (`name`, `price`, `category`) but not `id`.
*   `toString()` ? `MenuItem{id=1, name=''Burger'', price=150.0, category=''Main Course''}` — used by `displayAllMenuItems()` (`forEach(System.out::println)`).

### 2. `OrderItem.java`

**File:** `restaurant/src/main/java/org/example/OrderItem.java`

```java
private MenuItem menuItem;   // reference to the MenuItem
private int quantity;
private final int id;
private static int orderItemCount; // auto-increment id = ++orderItemCount
```

*   Join entity: wraps a `MenuItem` + quantity chosen by the customer.
*   `calculateSubTotal()` ? `menuItem.getPrice() * quantity`.
*   Own `id` (distinct from `MenuItem.id`) ? used by `removeItemFromOrder(orderId, orderItemId)`.

### 3. `Order.java`

**File:** `restaurant/src/main/java/org/example/Order.java`

```java
private final int id;                     // auto-increment via static orderCount
private static int orderCount;
private String customerName;
private List<OrderItem> orderItems = new ArrayList<>();
private double totalPrice;                // maintained incrementally
private OrderStatus orderStatus;          // PENDING initially
public Order(String customerName) {
  this.id = ++orderCount;
  this.customerName = customerName;
  this.totalPrice = 0;
  this.orderStatus = OrderStatus.PENDING;
}
```

Key methods:

| Method | What it does |
|---|---|
| `addItem(OrderItem)` | `totalPrice += subTotal; orderItems.add(...)` |
| `removeItem(OrderItem)` | `totalPrice -= subTotal; orderItems.remove(...)` |
| `calculateTotal()` | `orderItems.stream().mapToDouble(subTotal).sum()` — recomputed from scratch (authoritative). `totalPrice` is the cached incremental value; `calculateTotal()` is not used to sync them. |
| `displayOrder()` | Prints ID, customer, status, each `name x qty = subTotal`, and `Total`. |
| `toString()` | `Order{id=1, customer=''Ahmed'', status=PENDING, total=0.0, items=[Burger x2=300.0]}` — used by `displayAllOrders()` and `searchOrder()`. |

> **Quirk:** `setTotalPrice(double)` actually *adds* (`this.totalPrice += totalPrice`) — misnamed; it is an `addToTotal`.

### 4. `OrderStatus.java`

**File:** `restaurant/src/main/java/org/example/OrderStatus.java`

```java
public enum OrderStatus { PENDING, IN_KITCHEN, COMPLETED, CANCELLED }
```

No `String` status anywhere — all transitions go through `setOrderStatus(OrderStatus)`. This satisfies the assignment enum rule and makes illegal states unrepresentable.

---

## `Restaurant.java` — The Manager

**File:** `restaurant/src/main/java/org/example/Restaurant.java` (~195 lines, 4 collections)

#### Fields

```java
private List<MenuItem> menuItems   = new ArrayList<>();
private List<Order> ordersList     = new LinkedList<>();      // kitchenQueue
private Map<Integer, Order> ordersMap     = new HashMap<>();       // all orders
private Map<Integer, Order> orderItemsMap = new LinkedHashMap<>(); // completed
```

#### Methods (in source order)

**`addMenuItem(MenuItem)`**
*   Null-check + `price > 0` validation ? `menuItems.add(menuItem)`. Price-zero/negative rejected.

**`removeMenuItem(int itemID)`**
*   `removeIf(item -> item.getId() == itemID)` on the `ArrayList`. Prints `Item removed` or `Item with ID: X not found`. Validates `itemID > 0`.

**`displayAllMenuItems()`**
*   If empty ? `No menu items found`, else `menuItems.forEach(System.out::println)` (relies on `MenuItem.toString()`).

**`searchMenuItem(int itemID)`**
*   Streams + `Optional<MenuItem>` ? `filter(id==itemID).findFirst()`.  
*   ?? **Bug noted:** success branch prints `"Item with ID: " + result.get() + " not found"` — copy-paste error; should print the found item.

**`createOrder(Order)`**
*   Null / empty `customerName` check ? `ordersMap.put(order.getId(), order)` + `Order ID: X created`. Order stays in `HashMap` forever.

**`addItemToOrder(int qty, int orderID, int menuItemID)`**
*   Validates `qty>0`, `IDs>0`. Finds `MenuItem` via stream; if missing ? `Item with ID: X not found`.
*   Loops `ordersMap.entrySet()` to find order; if `COMPLETED`/`CANCELLED` ? `order already cancelled or completed`.
*   Creates `new OrderItem(item, qty)` and `order.addItem(...)` ? `order added successfully`.
*   ?? **Bug:** `item` is never assigned from `Optional` (`item` stays `null`) ? `new OrderItem(null, qty)` would NPE if the current `HashMap` loop path is taken. (The zipped snapshot may differ.)

**`removeItemFromOrder(int orderID, int orderItemID)`**
*   Direct `ordersMap.get(orderID)`; if null ? not found. Blocks if `COMPLETED`/`CANCELLED`.
*   Linear scan of `order.getOrderItems()` for matching `OrderItem.id` ? `order.removeItem(o)` + `order removed successfully`. Otherwise `Order Item with ID: X not found or not for you`.
*   Commented-out alternative loop kept in source.

**`displayAllOrders()`**
*   `ordersMap.values().forEach(System.out::println)` — dumps **all** orders regardless of status (registry view).

**`searchOrder(int orderID)`**
*   `ordersMap.get(orderID)` O(1) ? prints `toString()` or `not found`. Guards `orderID>0` and empty map.

**`addOrderToKitchenQueue(int orderID)`**
*   `ordersMap.get(orderID)` ? not found handling.
*   Duplicate check: linear scan of `ordersList` for same ID ? `already exists`.
*   Blocks if `CANCELLED`/`COMPLETED`.
*   `order.setOrderStatus(IN_KITCHEN)` + `ordersList.add(order)` ? `Order added successfully`. Status transition: `PENDING ? IN_KITCHEN`.

**`processNextOrder()`**
*   Empty queue guard ? `Kitchen queue is empty...`.
*   `Order next = ordersList.getFirst()` (Java 21+ `SequencedCollection` or `LinkedList.getFirst()`), null-guard, `next.setOrderStatus(COMPLETED)`, `orderItemsMap.put(next.getId(), next)` (preserves order), `ordersList.removeFirst()` ? `Order ID X processed successfully (COMPLETED)`.

**`checkOrderStatus(int orderID)`**
*   `ordersMap.get(orderID)` ? prints `order status is PENDING/IN_KITCHEN/...` or not-found.

**`displayCompletedOrders()`**
*   Iterates `orderItemsMap.entrySet()` in insertion order ? prints each `Order.toString()` or `No orders found`.

---

## `Main.java` — CLI & Input Handling

**File:** `restaurant/src/main/java/org/example/Main.java` (195 lines)

**Static state:**
```java
static public Scanner sc = new Scanner(System.in);
static public Restaurant restaurant = new Restaurant();
```

**Main loop (`main`):**
```java
boolean exit = true;
while (exit) {
  menu(); // prints 14 options
  int choose = readInt("choose one from 1 to 14: ");
  switch(choose) { case 1: addMenuItem(); ... case 14: exit=false; }
}
```

| Menu # | Label in `menu()` | Handler | Underlying `Restaurant` call |
|---|---|---|---|
| 1 | Add Item | `addMenuItem()` | `restaurant.addMenuItem(new MenuItem(name,price,cat))` |
| 2 | Remove Item | `removeMenuItem()` | `restaurant.removeMenuItem(itemID)` |
| 3 | Display All Items | `displayAllMenuItems()` | `restaurant.displayAllMenuItems()` |
| 4 | Search Item | `searchMenuItem()` | `restaurant.searchMenuItem(itemID)` |
| 5 | Create Order | `createOrder()` | `restaurant.createOrder(new Order(customerName))` — ID auto-generated |
| 6 | Add Item to Order | `addItemToOrder()` | `restaurant.addItemToOrder(qty, orderID, menuItemID)` |
| 7 | Remove Item from Order | `removeItemFromOrder()` | `restaurant.removeItemFromOrder(orderID, orderItemID)` |
| 8 | Display All Orders | `displayAllOrders()` | `restaurant.displayAllOrders()` (note menu says "All Orders", not single) |
| 9 | Add Order to Kitchen Queue | `addOrderToKitchenQueue()` | `restaurant.addOrderToKitchenQueue(orderID)` |
| 10 | Process Next Order | `processNextOrder()` | `restaurant.processNextOrder()` |
| 11 | Display Order By ID | `searchOrder()` | `restaurant.searchOrder(orderID)` (named "Search Order" in spec) |
| 12 | Check Order Status | `checkOrderStatus()` | `restaurant.checkOrderStatus(orderID)` |
| 13 | Display Completed Orders | `displayCompletedOrders()` | `restaurant.displayCompletedOrders()` |
| 14 | Exit | — | `Exiting... Goodbye!` |

**Safe input helpers (validation loop until valid):**

*   `readInt(prompt)` — `sc.nextLine()` + `Integer.parseInt`, catches `NumberFormatException`, rejects empty, prints `Invalid number...`.
*   `readPositiveInt(prompt)` — loops `readInt` until `>0`, else `Value must be > 0`.
*   `readPositiveDouble(prompt)` — `Double.parseDouble`, `>0`, else `Price/Value must be > 0`.
*   `readNonEmptyLine(prompt)` — trims, rejects empty ? `Input cannot be empty`.

**Operation wrappers** (e.g. `addMenuItem()` in `Main`) use those helpers so `Restaurant` never sees malformed input, but `Restaurant` still re-validates defensively.

---

## Order Lifecycle

```text
new Order(customer)          --?  PENDING        (createOrder ? HashMap)
      ¦
      ? addOrderToKitchenQueue
                                 IN_KITCHEN     (added to LinkedList queue)
      ¦
      ? processNextOrder
                                 COMPLETED      (poll queue ? LinkedHashMap, stays in HashMap)
      ¦
      ?-- (future) CANCELLED     (blocked from queue/addItem paths)
```

*   `HashMap` `ordersMap` contains the order in **every** state.
*   `LinkedList` `ordersList` contains it **only** while `IN_KITCHEN`.
*   `LinkedHashMap` `orderItemsMap` contains it **only** after `COMPLETED`, in processing order.

---

## Features — 14 Menu Operations

Detailed behavior (as enforced by `Restaurant.java`):

1. **Add Menu Item** — reads name/category (non-empty) + price (>0), `new MenuItem(...)` auto IDs, appends to `ArrayList`.
2. **Remove Menu Item** — `removeIf` by ID; unique-ID invariant maintained.
3. **Display Menu** — iterates `ArrayList`; handles empty.
4. **Search Menu Item** — stream `filter + findFirst` by ID.
5. **Create Order** — reads customer name, `new Order(name)` (PENDING), `put` into `HashMap`.
6. **Add Item to Order** — resolves order via `HashMap`, resolves menu item via `ArrayList` stream, validates not COMPLETED/CANCELLED, creates `OrderItem` and appends to order''s internal `ArrayList`.
7. **Remove Item from Order** — resolves order, blocks if terminal status, scans order''s `List<OrderItem>` by `OrderItem.id`.
8. **Display Order(s)** — `Main.displayAllOrders()` dumps *all* orders from `HashMap`; assignment spec''s "Display Order" (single) is implemented as #11.
9. **Add Order to Kitchen Queue** — `HashMap` ? status `IN_KITCHEN` ? `LinkedList.add` (duplicate prevention via scan).
10. **Process Next Order** — FIFO `getFirst` + `removeFirst`, status `COMPLETED`, `LinkedHashMap.put` to preserve completion order.
11. **Search Order** — `HashMap.get(orderID)` regardless of status.
12. **Check Order Status** — `HashMap.get` + `getOrderStatus()`.
13. **Display Completed Orders** — iterates `LinkedHashMap` in insertion order.
14. **Exit** — breaks loop.

---

## Input Validation & Error Handling

*   **Centralized in `Main`:** `readPositiveInt`, `readPositiveDouble`, `readNonEmptyLine` loop until valid — user never reaches `Restaurant` with empty strings, non-numbers, or `<=0`.
*   **Defensive in `Restaurant`:** every public method re-checks `<=0`, `null`, empty `HashMap`/`LinkedList`, missing IDs, and terminal statuses (`COMPLETED`/`CANCELLED` blocks mutations).
*   **Messages:** user-friendly — e.g. `Item with ID: 999 not found`, `Kitchen queue is empty...`, `order already cancelled or completed`, `Value must be > 0`.
*   **No crash:** `Optional`, `null` guards, `isEmpty()` checks, `try/catch` on parsing.

---

## How to Run

### Prerequisites
*   JDK 26 (as per `pom.xml:26`), Maven 3.9+, IntelliJ optional.

### Option A — IntelliJ IDEA
1. Open `restaurant/pom.xml` as project.
2. Run `org.example.Main` (Run ?).

### Option B — Maven (CLI)
```powershell
cd D:\ROUTE\JAVA\COURSE-CODE\MONTH-9\DAY-1\Restaurant\restaurant
mvn compile
mvn exec:java -Dexec.mainClass="org.example.Main"
# or after compile:
java -cp target/classes org.example.Main
```

### Option C — Manual javac
```powershell
javac -d out (Get-ChildItem -Recurse src/main/java/*.java)
java -cp out org.example.Main
```

### Replaying the sample session
```powershell
# PowerShell redirection
Get-Content target/input.txt | java -cp target/classes org.example.Main
# Output should match target/output.txt
```

---

## Example Session (`input.txt` ? `output.txt`)

`target/input.txt` feeds invalid inputs first to demonstrate validation:

```text
abc -5 0 15          # invalid menu choices ? loops until 1
1 Burger -10 0 150 Main Course   # add Burger: rejects -10, 0 ? accepts 150
3                    # display menu ? MenuItem{id=1, ...}
2 abc -1 999         # remove: rejects abc, -1 ? 999 not found
4 999                # search 999 ? not found
5 <empty> Ahmed      # create order: empty name rejected ? Order ID:1 created (PENDING)
6 999 1 2            # add item to order 999 ? Order not found
7 1 1                # remove item 1 from order 1 ? OrderItem not found (empty order)
8                    # display all orders ? Order{id=1, ... total=0.0}
9 999                # enqueue 999 ? not found
10                   # process ? Kitchen queue empty
11 999 12 999 13    # search/check/completed ? not found / No orders found
14                   # Exit
```

Successful *happy path* (manually) would be:

```text
1 ? Burger 150 Main Course, Cola 40 Drinks
5 ? Order for Ahmed (ID=1, PENDING)
6 ? Add Burger x2 to Order 1 ? 300
6 ? Add Cola   x1 to Order 1 ? 40 (Total 340)
8 ? Display Order 1: Burger x2=300, Cola x1=40, Total 340
9 ? Add Order 1 to Kitchen Queue ? IN_KITCHEN
10 ? Process Next Order ? COMPLETED, moved to LinkedHashMap
13 ? Display Completed Orders ? Order 1 in completion order
12 ? Check Order Status 1 ? COMPLETED
```

---

## Assignment Requirements Coverage

| Requirement | Implementation |
|---|---|
| `ArrayList<MenuItem> menu` | `Restaurant.menuItems` — `ArrayList` |
| `LinkedList<Order> kitchenQueue` | `Restaurant.ordersList` — `LinkedList` + `getFirst/removeFirst` |
| `HashMap<Integer,Order> orders` | `Restaurant.ordersMap` — permanent registry |
| `LinkedHashMap<Integer,Order> completedOrders` | `Restaurant.orderItemsMap` — `LinkedHashMap` |
| Enum `OrderStatus` | `OrderStatus.java` — PENDING/IN_KITCHEN/COMPLETED/CANCELLED |
| Unique IDs | Auto-increment static counters in `MenuItem`/`Order`/`OrderItem` |
| No arrays / correct collections | Satisfied — no arrays used |
| HashMap keeps all orders | `ordersMap` never removes; `processNextOrder` only moves to `LinkedHashMap` |
| Console-based, no DB/GUI | Pure `Scanner` + `System.out` |
| Handle invalid IDs / empty | Guards in `Restaurant` + validation loops in `Main` |

---

## Known Issues / Code Review Notes

> These do not prevent the program from running but are worth fixing before submission:

1. **`searchMenuItem` prints wrong message on success** — `Restaurant.java:54-56`:
   ```java
   if (result.isPresent()) {
     System.out.println("Item with ID: " + result.get() + " not found"); // ? bug
   }
   ```
   Should be `System.out.println(result.get())` or `Found: ...`.

2. **`addItemToOrder` NPE risk** — `Restaurant.java:78-94`: `Optional<MenuItem> result` is found but never assigned to `MenuItem item` (`item` stays `null`). `new OrderItem(null, qty)` will throw `NullPointerException` on `calculateSubTotal()`. Fix:
   ```java
   MenuItem item = result.get(); // or result.orElse(null) with check
   ```

3. **`setTotalPrice` is additive, not a setter** — `Order.java:33-35`:
   ```java
   public void setTotalPrice(double totalPrice) { this.totalPrice += totalPrice; }
   ```
   Misleading name; should be `addToTotal` or recalculated via `calculateTotal()`.

4. **`totalPrice` drift** — `addItem`/`removeItem` maintain `totalPrice` incrementally, but `calculateTotal()` recomputes from scratch and the two can diverge if items are mutated. Prefer `totalPrice = calculateTotal()` after mutations.

5. **Field naming** — `ordersList` ? `kitchenQueue`, `orderItemsMap` ? `completedOrders` would match spec and avoid confusion (`orderItemsMap` sounds like `Map<OrderItem>`).

6. **`List<Order>` vs `LinkedList<Order>`** — `ordersList` declared as `List` but uses `getFirst()/removeFirst()` (Java 21+ `SequencedCollection`). Safer to declare as `LinkedList<Order>` or `Queue<Order>` and use `poll()`/`peek()`.

7. **`displayAllOrders` vs spec** — Spec #8 is "Display Order" (single by ID); code #8 displays *all* orders. Single-order display is #11 `searchOrder`. Not a bug, just label drift between `Main.menu()` and spec table.

8. **Commented dead code** — `removeItemFromOrder` retains commented loops; should be cleaned.

---

## Possible Improvements

*   Fix bugs #1–#2 above.
*   Rename collections to spec names; declare queue as `Queue<Order> kitchenQueue = new LinkedList<>()` and use `offer`/`poll`.
*   Make `Order.totalPrice` derived only (`getTotalPrice() { return calculateTotal(); }`) to eliminate cache bugs.
*   Add `cancelOrder(int orderId)` (spec mentions CANCELLED) — currently `CANCELLED` is never set.
*   Add uniqueness check for custom menu IDs if the design ever allows manual IDs.
*   Add JUnit tests under `src/test/java` (currently empty) — e.g., test lifecycle PENDING?IN_KITCHEN?COMPLETED.
*   Extract `MenuItem` search into a private `findMenuItemById` helper to DRY `addItemToOrder`/`searchMenuItem`.
*   Use `Map.computeIfAbsent` / `getOrDefault` patterns for cleaner lookups.

---

## Learning Outcomes

After this project you can explain:

*   **Why `ArrayList` for menu?** Indexed storage, fast iteration, natural for a catalog you display/search linearly.
*   **Why `LinkedList` for kitchen?** FIFO queue — first order in is first out; `LinkedList` gives O(1) head insertion/removal.
*   **Why `HashMap` for all orders?** Permanent registry + O(1) ID lookup regardless of status; status enum separates state from storage.
*   **Why `LinkedHashMap` for completed?** Need both hash lookup *and* insertion/completion order preserved for display.
*   **Why `enum` for status?** Type-safe lifecycle, no magic strings, compiler-checked transitions.

---

## Files Reference

*   `restaurant/src/main/java/org/example/Main.java:1` — entry point, menu loop, input helpers
*   `restaurant/src/main/java/org/example/Restaurant.java:1` — 4 collections + 13 business methods
*   `restaurant/src/main/java/org/example/MenuItem.java:1` — `id`, `name`, `price`, `category`
*   `restaurant/src/main/java/org/example/Order.java:1` — `ArrayList<OrderItem>`, `OrderStatus`, total
*   `restaurant/src/main/java/org/example/OrderItem.java:1` — `MenuItem` + `quantity` ? subtotal
*   `restaurant/src/main/java/org/example/OrderStatus.java:1` — enum
*   `restaurant/pom.xml:1` — Maven, Java 26
*   `Restaurant_Order_Manager_Collections_Assignment(5).md:1` — full spec (source of truth)

---

*Generated for `D:\ROUTE\JAVA\COURSE-CODE\MONTH-9\DAY-1\Restaurant` — covers every source file, every method, every collection choice, and the full run lifecycle. Fix the two noted bugs for a clean submission.*
