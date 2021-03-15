import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.*;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/*
CSCE 315
3-15-2021
Project 2: Database GUI Project
Team 13:
  Aldo Galvan
  Ali Imran
  Brendon Banasik
  Christian Kim
 */


public class jdbcpostgreSQLGUI {

  public static class GUI extends JFrame {
    //frames
    JFrame frame = new JFrame("GUI");
    JFrame CustomizeFrame = new JFrame("Customize Entree");

    //panels
    JPanel Container = new JPanel();
    JPanel Home = new JPanel();
    JPanel EmployeeCheckout = new JPanel();
    JPanel EmployeeName = new JPanel();
    JPanel CustomerCheckout = new JPanel();
    JPanel CustomerName = new JPanel();
    JPanel ManagerMode = new JPanel();
    JPanel AddItem = new JPanel();
    JPanel DropItem = new JPanel();
    JPanel CustomizePanel = new JPanel();

    //general exit button back to home
    JButton exitToHome = new JButton("Exit");
   
    //buttons for home
    JButton customerButton = new JButton("Customer");
    JButton employeeButton = new JButton("Employee");

    //buttons and other swing functions for employee mode and self-checkout mode
    //self-checkout does not have managerButton
    JButton managerButton = new JButton("Manager Mode");
    JButton submitOrderButton = new JButton("Submit Order");
    JButton submitCustNameButton = new JButton("Submit Name");
    JButton submitEmpNameButton = new JButton("Submit Name");
    JComboBox<String> entreeList = null, sideList = null, drinkList = null, dessertList = null, menuList = null, removeList = null;
    JCheckBox[] toppingList;
    String[] entreeNames, sideNames, drinkNames, dessertsNames, menu;
    String selectedTopping = "";
    JButton addEntree = new JButton("Add Entree");
    JButton addSide = new JButton("Add Side");
    JButton addDrink = new JButton("Add Drink");
    JButton addDessert = new JButton("Add Dessert");
    JTextArea custOrder = new JTextArea("Your Order:\n\n", 10, 50);
    JScrollPane sp = new JScrollPane(custOrder);
    JButton submitToppings = new JButton("Submit");
    double totalPrice = 0.00;
    JButton tryFood = new JButton("We Recommend...");
    JTextArea tryFoodOut = new JTextArea();
    JLabel stockReccom = new JLabel("");
    JLabel priceReccom = new JLabel("Recommended Menu Item Price Change: ");
    JLabel total = new JLabel("Total: $0.00");
    JLabel trends = new JLabel("");
    JLabel trends2 = new JLabel("");
    String lastName = "";
    String firstName = "";
    String selectedDessert = "";
    String selectedDrink = "";
    String selectedEntree = "";
    String selectedSide = "";
    String totalDessert = "";
    String totalDrink = "";
    String totalEntree = "";
    String totalSide = "";

    //components for manager mode
    JButton priceButton = new JButton("Price-Override($)");
    JButton stockButton = new JButton("Inventory Update");
    JButton addButton = new JButton("Add Item");
    JButton dropButton = new JButton("Remove Item");
    JTextField addNewItem = new JTextField(15);
    JLabel addNewItemLabel = new JLabel("Enter New Item");
    JLabel removeItemLabel = new JLabel("Select Item to Remove");
    JButton submitAddButton = new JButton("Add Item");
    JButton submitDropButton = new JButton("Remove Item");
    String[] categoryArray = {"entrees", "sides", "drinks", "desserts"};
    JComboBox<String> categoryList = new JComboBox<String>(categoryArray);
    JLabel addNewCategoryLabel = new JLabel("Select Category");
    JTextField addNewPrice = new JTextField(5);
    JLabel addNewPriceLabel = new JLabel("Enter Price");
    JTextField addNewStock = new JTextField(5);
    JLabel addNewStockLabel = new JLabel("Enter Stock");
    JButton backButton = new JButton("Cancel");
    JLabel changeStockLabel = new JLabel("Enter New Stock");
    JTextField changeStock = new JTextField(10);
    JLabel changePriceLabel = new JLabel("Enter New Price");
    JTextField changePrice = new JTextField(10);

    //highest and lowest trending entrees
    String high1s = "";
    String high2s = "";
    String low1s = "";
    String low2s = "";

    //text entries for employee mode and self-checkout
    JLabel custFirstNameLabel = new JLabel("Enter First Name");
    JTextField custFirstNameText = new JTextField(10);
    JLabel custLastNameLabel = new JLabel("Enter Last Name");
    JTextField custLastNameText = new JTextField(10);

    //sets up card layout along with connected to database
    CardLayout cLayout = new CardLayout();
    Connection conn;
    int index=0;
    

    //Customer Checkout Panels
    JPanel menuPanel = new JPanel();
    JPanel recommPanel = new JPanel();
    JPanel orderPanel = new JPanel();

    //Manager Mode Panels
    JPanel overridePanel = new JPanel();
    JPanel addRemovePanel = new JPanel();
    JPanel managerRecommPanel = new JPanel();

    public GUI(Connection con) {
      this.conn = con;
      //sets up card layout structure
      Container.setLayout(cLayout);
      Container.add(Home, "1");
      Container.add(EmployeeCheckout, "2");
      Container.add(CustomerCheckout, "3");
      Container.add(ManagerMode, "4");
      Container.add(CustomerName, "5");
      Container.add(EmployeeName, "6");
      Container.add(AddItem, "7");
      Container.add(DropItem, "8");

      cLayout.show(Container, "1");
      
      /* Home Page */
      Home.add(customerButton);
      Home.add(employeeButton);
      
      //updates menu structure
      updateMenu();

      /* Buttons */
      //returns back to customer/employee selection page
      exitToHome.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          //resets all variables for new order
          setDefault();
          cLayout.show(Container, "1");
        }
      });
      //Position of Button "Exit" 
      exitToHome.setBounds(400, 400, 100, 50);

      //functions contain button attributes
      homeScreenButtons();
      employeeScreenButtons();
      customerScreenButtons();
      checkoutButtons();
      managerModeButtons();
      
      //sets frame sizing and atributes
      frame.add(Container);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      frame.setSize(800, 400);
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);

      CustomizeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      CustomizeFrame.pack();
      CustomizeFrame.setSize(400, 150);
      CustomizeFrame.setLocationRelativeTo(null);
    }

    void homeScreenButtons() {
      //allows employees to enter a customer name
      employeeButton.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          
          /* Employee Checkout Page */
          EmployeeName.add(exitToHome);
          EmployeeName.add(custFirstNameLabel);
          EmployeeName.add(custFirstNameText);
          EmployeeName.add(custLastNameLabel);
          EmployeeName.add(custLastNameText);
          //option to either set name or go to manager screen
          EmployeeName.add(submitEmpNameButton);
          EmployeeName.add(managerButton);
          cLayout.show(Container, "6");
        }
      });

      //customer can enter first and last name
      customerButton.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          /* Customer Checkout Page */
          CustomerName.add(exitToHome);
          CustomerName.add(custFirstNameLabel);
          CustomerName.add(custFirstNameText);
          CustomerName.add(custLastNameLabel);
          CustomerName.add(custLastNameText);
          CustomerName.add(submitCustNameButton);

          cLayout.show(Container, "5");          
        }
      });
    }

    //function containing employee button functionality
    void employeeScreenButtons() {
      //allows employees to submit an order for a customer
      submitEmpNameButton.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          //gets customer name information from drop down menus
          lastName = custLastNameText.getText();
          firstName = custFirstNameText.getText();

          setCheckoutLayout(EmployeeCheckout);

          cLayout.show(Container, "2");
        }
      });
      
      //manager mode allows managers to change items
      managerButton.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          setManagerLayout();
        }
      });
    }

    //function containing customer button functionality
    void customerScreenButtons() {
      //customer can enter checkout screen
      submitCustNameButton.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          //gets customer name information from drop down menus
          lastName = custLastNameText.getText();
          firstName = custFirstNameText.getText();

          setCheckoutLayout(CustomerCheckout);
          
          cLayout.show(Container, "3");
        }
      });
    }

    //allows user to see their order and add new items
    void checkoutButtons() {
      addEntree.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          CustomizePanel.add(submitToppings);
          CustomizeFrame.add(CustomizePanel);
          CustomizeFrame.setVisible(true);
        }
      });

      //add toppings to entree
      submitToppings.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          //gets all selected topping from checkboxes
          for(int i = 0; i < toppingList.length; i++) {
            if(toppingList[i].isSelected() == true) {
              selectedTopping = selectedTopping + toppingList[i].getText() + " ";
            }
          }

          selectedEntree = String.valueOf(entreeList.getSelectedItem());
          
          //gets price of entree from database
          String query = "SELECT price FROM entrees WHERE name = '" + selectedEntree + "'";
          double price = getSQLdouble(query, "price");
          updateTotal(price);

          selectedEntree = selectedEntree + ": " + selectedTopping;
          totalEntree = totalEntree + selectedEntree + ", ";

          selectedTopping = "";
          
          //adds entree to order panel
          custOrder.setEditable(false);
          custOrder.setText(custOrder.getText() + "($" + price + ") " + selectedEntree + "\n");
          
          CustomerCheckout.revalidate();
          CustomerCheckout.repaint();

          CustomizeFrame.dispose();
 
        }
      });
      
      //adds sides to the order pane
      addSide.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          //gets side from drop down menu
          selectedSide = String.valueOf(sideList.getSelectedItem());
          totalSide = totalSide + selectedSide + ", ";
          
          //finds price for side from database
          String query = "SELECT price FROM sides WHERE name = '" + selectedSide + "'";
          double price = getSQLdouble(query, "price");
          updateTotal(price);

          //adds side to order panel
          custOrder.setEditable(false);
          custOrder.setText(custOrder.getText() + "($" + price + ") " + selectedSide + "\n");
          
          CustomerCheckout.revalidate();
          CustomerCheckout.repaint();
          
        }
      });

      //adds drinks to order panel
      addDrink.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          //gets drink from drop down menu
          selectedDrink = String.valueOf(drinkList.getSelectedItem());
          totalDrink = totalDrink + selectedDrink + ", ";

          //finds price for drink from database
          String query = "SELECT price FROM drinks WHERE name = '" + selectedDrink + "'";
          double price = getSQLdouble(query, "price");
          updateTotal(price);

          //adds drink to order panel
          custOrder.setEditable(false);
          custOrder.setText(custOrder.getText() + "($" + price + ") " + selectedDrink + "\n");
          
          CustomerCheckout.revalidate();
          CustomerCheckout.repaint();
          
        }
      });

      //adds dessert to order panel
      addDessert.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          //gets dessert from drop down menu
          selectedDessert = String.valueOf(dessertList.getSelectedItem());
          totalDessert = totalDessert + selectedDessert + ", ";

          //finds price for dessert from database
          String query = "SELECT price FROM desserts WHERE name = '" + selectedDessert + "'";
          double price = getSQLdouble(query, "price");
          updateTotal(price);
          
          //adds dessert to order panel
          custOrder.setEditable(false);
          custOrder.setText(custOrder.getText() + "($" + price + ") " + selectedDessert + "\n");
          
          CustomerCheckout.revalidate();
          CustomerCheckout.repaint();
          
        }
      });
      
      //displays recommended food to customer and employee
      tryFood.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          //array of all recommended items based on previous orders
          String[] food = getCustRecomm(firstName.toUpperCase(), lastName.toUpperCase());
          tryFoodOut.setEditable(false);
          tryFoodOut.setText("Entree: " + food[0] +
                            "\nSide: " + food[1] +
                            "\nDrink: " + food[2] +
                            "\nDessert: " + food[3]);
          tryFood.setEnabled(false);
        }
      });

      submitOrderButton.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {

          
          //gets current date
          Calendar cal = Calendar.getInstance();
          SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
          String date = df.format(cal.getTime());
          int new_id = 0;

          try (Statement stmt = conn.createStatement()) {
            
            //gets next id from database
            String s0 = "SELECT MAX(id) FROM order_history";
            ResultSet result = stmt.executeQuery(s0);            
            while(result.next()) {
              new_id = Integer.parseInt(result.getString("max")) + 1;
            }     
          
          } catch(SQLException exc) {
            JOptionPane.showMessageDialog(null,"Error accessing Database.");
          }
          try (Statement stmt = conn.createStatement()) {
            
            
            //create an SQL statement for submitting order to order_history
            String s1 = "VALUES('"+ lastName.toUpperCase()+"', ' "+firstName.toUpperCase()+"', '"+date+"', '"+ totalEntree+"', '"+ totalSide+"', '"+ totalDrink+"', '"+ totalDessert+"', "+String.valueOf(new_id)+")";                  
            String sqlStatement = "INSERT INTO order_history " + s1;
           
            //send statement to DBMS
            stmt.executeUpdate(sqlStatement);
            
          
          } catch(SQLException exc) {
            JOptionPane.showMessageDialog(null,"Error accessing Database.");
          }
          
          //resets values, order is complete
          setDefault();
          cLayout.show(Container, "1");
          
        }
      });
    }
    
    //function containing all manager functionality
    void managerModeButtons() {
      priceButton.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          //finds if selected item is entree, side, drink, or dessert
          index = menuList.getSelectedIndex();
          String chosen = "";
          if (index < entreeList.getItemCount()) {
            chosen = "entrees";
          }
          else if (index < entreeList.getItemCount() + sideList.getItemCount()) {
            chosen = "sides";
          }
          else if (index < entreeList.getItemCount() + sideList.getItemCount() + drinkList.getItemCount()) {
            chosen = "drinks";
          }
          else {
            chosen = "desserts";
          }

          try (Statement stmt = conn.createStatement()) {
            //gets new price from manager input
            Double newPrice = Double.parseDouble(changePrice.getText());
            //sends SQL command to update price in database
            String qs0 = "UPDATE " + chosen + " SET price = " + newPrice + " WHERE name = " + "'" + menuList.getSelectedItem() + "'";
            stmt.executeUpdate(qs0);
          
          } catch(SQLException exc) {
            JOptionPane.showMessageDialog(null,"Error accessing Database for Price Change.");
          }
        }
      });
      
      
      stockButton.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {

          //lists all items entrees, sides, drinks, and desserts
          index = menuList.getSelectedIndex();
          String chosen = "";
          if (index < entreeList.getItemCount()) {
            chosen = "entrees";
          }
          else if (index < entreeList.getItemCount() + sideList.getItemCount()) {
            chosen = "sides";
          }
          else if (index < entreeList.getItemCount() + sideList.getItemCount() + drinkList.getItemCount()) {
            chosen = "drinks";
          }
          else {
            chosen = "desserts";
          }

          try (Statement stmt = conn.createStatement()) {

            int newStock = Integer.parseInt(changeStock.getText());
            
            //changes stock to value entered by manager
            String s0 = "UPDATE " + chosen;
            String s1 = " SET stock = " + newStock;
            String s2 = " WHERE name = '" + menuList.getSelectedItem() + "'";
            String sqlStatement = s0 + s1 + s2;
            stmt.executeUpdate(sqlStatement);            
          
          } catch(SQLException exc) {
            JOptionPane.showMessageDialog(null,"Error accessing Database for Maintenance.");
          }
          
        }
      });

      //allows manager to add new item to menu
      addButton.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          AddItem.add(addNewItemLabel);
          AddItem.add(addNewItem);
          AddItem.add(addNewCategoryLabel);
          AddItem.add(categoryList);
          AddItem.add(addNewPriceLabel);
          AddItem.add(addNewPrice);
          AddItem.add(addNewStockLabel);
          AddItem.add(addNewStock);
          AddItem.add(submitAddButton);
          AddItem.add(backButton);
          
          cLayout.show(Container, "7");
        }
      });

      //submits new item to menu
      submitAddButton.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {

          //get values from entered data
          String newName = addNewItem.getText();
          String newCategory = String.valueOf(categoryList.getSelectedItem());
          Double newPrice = Double.parseDouble(addNewPrice.getText());
          int newStock = Integer.parseInt(addNewStock.getText());

          try (Statement stmt = conn.createStatement()) {
            
            //gets next id from database
            int new_id= 0;
            String s0 = "SELECT MAX(id) FROM " + newCategory;
            ResultSet result = stmt.executeQuery(s0);            
            while(result.next()) {
              new_id = Integer.parseInt(result.getString("max")) + 1;
            }   

            //sends sql command to insert new item into database
            s0 = "INSERT INTO " + newCategory + " (name, id, price, stock, sold) VALUES ('" + newName + "', " + new_id + ", " + newPrice + ", " + newStock + ", 0)";
            stmt.executeUpdate(s0);
            
          
          } catch(SQLException exc) {
            JOptionPane.showMessageDialog(null,"Error accessing Database.");
          }

          //if statements check to see which list item is added to
          if(newCategory == "entrees") {
            menuList.insertItemAt(newName, entreeList.getItemCount());
            entreeList.addItem(newName);
          }
          else if(newCategory == "sides") {
            menuList.insertItemAt(newName, entreeList.getItemCount() + sideList.getItemCount());
            sideList.addItem(newName);
          }
          else if(newCategory == "drinks") {
            menuList.insertItemAt(newName, entreeList.getItemCount() + sideList.getItemCount() + drinkList.getItemCount());
            drinkList.addItem(newName);
          }
          else {
            dessertList.addItem(newName);
            menuList.addItem(newName);
          }
  

          //resets manager layout after submitting changes
          setManagerLayout();
          ManagerMode.revalidate();
          ManagerMode.repaint();
          
          cLayout.show(Container, "4");
        }
      });

      //allows manager to remove item from menu
      dropButton.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          DropItem.add(removeItemLabel);
          DropItem.add(menuList);
          DropItem.add(addNewCategoryLabel);
          DropItem.add(categoryList);
          DropItem.add(submitDropButton);
          DropItem.add(backButton);
          
          cLayout.show(Container, "8");
        }
      });
      
      //submits removal of item from menu
      submitDropButton.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {

          //gets data entered into drop down menus
          String dropName = String.valueOf(menuList.getSelectedItem());
          String dropCategory = String.valueOf(categoryList.getSelectedItem());

          try (Statement stmt = conn.createStatement()) {

            //deletes item in database
            String s0 = "DELETE FROM " + dropCategory + " WHERE name='" + dropName + "'";
            stmt.executeUpdate(s0);
            
          
          } catch(SQLException exc) {
            JOptionPane.showMessageDialog(null,"Error accessing Database.");
          }

          //if statements determine which list to remove item
          if(dropCategory == "entrees") {
            entreeList.removeItem(dropName);
          }
          else if(dropCategory == "drinks") {
            drinkList.removeItem(dropName);
          }
          else if(dropCategory == "sides"){
            sideList.removeItem(dropName);
          }
          else{
            dessertList.removeItem(dropName);
          }
          menuList.removeItem(dropName);

          //resets layout for manager mode
          setManagerLayout();
          ManagerMode.revalidate();
          ManagerMode.repaint();
          
          
          cLayout.show(Container, "4");
        }
      });

      //button goes back to manager screen
      backButton.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          
          cLayout.show(Container, "4");
        }
      });
    }

    //function sends SQL command to return an int value from database
    //ie. getting number of items in a table
    int getSQLint(String query, String column) {
      int value = 0;
      try (Statement stmt = conn.createStatement()) {
        ResultSet result = stmt.executeQuery(query);            
        while(result.next()) {
          value = Integer.parseInt(result.getString(column));
        }     
      } catch (Exception e) { 
          JOptionPane.showMessageDialog(null, "Error accessing Database for Int with: " + query);
      }
      return value;
    }

    //function sends SQL command to return a double value from database
    //ie. getting price of item in a table
    double getSQLdouble(String query, String column) {
      double value = 0;
      try (Statement stmt = conn.createStatement()) {
        ResultSet result = stmt.executeQuery(query);            
        while(result.next()) {
          value = Double.parseDouble(result.getString(column));
        }     
      } catch (Exception e) { 
          JOptionPane.showMessageDialog(null, "Error accessing Database for Double with: " + query);
      }
      return value;
    }

    //function sends SQL command to return an array of values from database
    //ie. array containing all items ordered from customer
    String[] getSQLarray(String query, String column, int count) {
      String[] array = new String[count];
      try (Statement stmt = conn.createStatement()) {
        ResultSet result = stmt.executeQuery(query);            
        for (int i = 0; result.next(); ++i) {
          array[i] = result.getString(column);
        }     
      } catch (Exception e) { 
          JOptionPane.showMessageDialog(null, "Error accessing Database for Array with: " + query);
      }
      return array;
    }

    //updates total label on checkout screen
    void updateTotal(double price) {
      totalPrice += price;
      totalPrice = Double.parseDouble(String.format("%.2f", totalPrice)); //format to two decimal places
      if (totalPrice * 10 == (int)(totalPrice * 10)) { //checks if value contains only one decimal place
        total.setText("Total: $" + totalPrice + "0");
      } else {
        total.setText("Total: $" + totalPrice);
      }
    }

    //sets layout for customer and employee checkout screens
    void setCheckoutLayout(JPanel Checkout) {
      /* Checkout Layout */
      Checkout.setLayout(new GridBagLayout());
      GridBagConstraints CC_c = new GridBagConstraints();

      //Menu Layout
      menuPanel.setLayout(new GridLayout(4, 2, 1, 1));

      menuPanel.add(entreeList);
      menuPanel.add(addEntree);

      menuPanel.add(sideList);
      menuPanel.add(addSide);

      menuPanel.add(drinkList);
      menuPanel.add(addDrink);

      menuPanel.add(dessertList);
      menuPanel.add(addDessert);

      CC_c.gridx = 0;
      CC_c.gridy = 0;
      Checkout.add(menuPanel, CC_c);

      //Recommended Layout
      recommPanel.add(tryFood);
      recommPanel.add(tryFoodOut);
      CC_c.gridx = 1;
      CC_c.gridy = 0;
      Checkout.add(recommPanel, CC_c);

      //Order Layout
      orderPanel.setLayout(new GridBagLayout());
      GridBagConstraints oP_c = new GridBagConstraints();
      //scrollbar for order panel
      sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

      oP_c.gridx = 1;
      oP_c.gridy = 0;
      orderPanel.add(total, oP_c);

      oP_c.gridx = 1;
      oP_c.gridy = 1;
      orderPanel.add(submitOrderButton, oP_c);

      oP_c.gridx = 1;
      oP_c.gridy = 2;
      orderPanel.add(exitToHome, oP_c);

      CC_c.gridx = 0;
      CC_c.gridy = 1;
      CC_c.gridwidth = 2;

      oP_c.gridx = 0;
      oP_c.gridy = 0;
      oP_c.gridheight = 3;
      orderPanel.add(sp, oP_c);
      Checkout.add(orderPanel, CC_c);

      tryFood.setEnabled(true);
    }

    //sets layout for manager mode screen
    void setManagerLayout() {

      updateTrendingItems();
      String query = "SELECT price FROM entrees WHERE name = '" + low1s + "'";
      double price = getSQLdouble(query, "price");
      String priceNameReccom = low2s + " (Price: " + price + ")";
      
      String stockNameReccom = "";
      try (Statement stmt = conn.createStatement()) {
        int min = 0;
        //find lowest stock item in entrees table in database
        String s1 = "SELECT MIN(stock) FROM entrees";
        min = getSQLint(s1, "min");

        //find entree name with lowest stock from database
        String s2 = "SELECT name FROM entrees WHERE stock = " + min;
        ResultSet result = stmt.executeQuery(s2);            
        while(result.next()) {
          stockNameReccom = result.getString("name");
        }
        stockNameReccom = stockNameReccom + " (Stock: " + min + ")";
      
      } catch(SQLException exc) {
        JOptionPane.showMessageDialog(null,"Error accessing Database for Recommendations.");
      }

      /* Manager Mode Layout */
      ManagerMode.setLayout(new GridBagLayout());
      GridBagConstraints MM_c = new GridBagConstraints();     

      //Override Layout
      overridePanel.setLayout(new GridBagLayout());
      GridBagConstraints oP_c = new GridBagConstraints();     

      //Change Price Row
      oP_c.gridx = 0;
      oP_c.gridy = 1;
      oP_c.insets = new Insets(2, 3, 2, 3);
      overridePanel.add(changePriceLabel, oP_c);

      oP_c.gridx = 1;
      oP_c.gridy = 1;
      overridePanel.add(changePrice, oP_c);

      oP_c.gridx = 2;
      oP_c.gridy = 1;
      overridePanel.add(priceButton, oP_c);

      //Change Stock Row
      oP_c.gridx = 0;
      oP_c.gridy = 2;
      overridePanel.add(changeStockLabel, oP_c);

      oP_c.gridx = 1;
      oP_c.gridy = 2;
      overridePanel.add(changeStock, oP_c);

      oP_c.gridx = 2;
      oP_c.gridy = 2;
      overridePanel.add(stockButton, oP_c);

      //Menu List
      oP_c.gridx = 0;
      oP_c.gridy = 0;
      oP_c.gridwidth = 3;
      overridePanel.add(menuList, oP_c);
      
      //Add Override Panel
      MM_c.gridx = 0;
      MM_c.gridy = 0;
      MM_c.insets = new Insets(10, 10, 10, 50);
      ManagerMode.add(overridePanel, MM_c);


      //Add/Remove Panel
      addRemovePanel.setLayout(new GridLayout(2, 1, 1, 1));
      addRemovePanel.add(addButton);
      addRemovePanel.add(dropButton);

      //Add Add/Remove Panel
      MM_c.gridx = 1;
      MM_c.gridy = 0;
      MM_c.insets = new Insets(10, 50, 10, 10);
      ManagerMode.add(addRemovePanel, MM_c);


      //Recommendations Panel
      managerRecommPanel.setLayout(new GridLayout(4, 1, 1, 1));
      priceReccom.setText("Recommended Menu Item Price Change: " + priceNameReccom);
      managerRecommPanel.add(priceReccom);

      stockReccom.setText("Recommended Menu Item Stock Change: " + stockNameReccom);
      managerRecommPanel.add(stockReccom);
      managerRecommPanel.add(trends);
      managerRecommPanel.add(trends2);


      //Add Recommendations Panel
      MM_c.gridx = 0;
      MM_c.gridy = 1;
      MM_c.gridwidth = 2;
      MM_c.insets = new Insets(10, 10, 10, 10);
      ManagerMode.add(managerRecommPanel, MM_c);
      ManagerMode.add(exitToHome);
      
      cLayout.show(Container, "4");
    }

    //function returns array of recommended order items based on order history
    //ie. returns recommended entree, side, drink, dessert
    String[] getCustRecomm(String firstName, String lastName) {
      String[] recomms = new String[4];
      Random rn = new Random();
      Map<String, Integer> entrees = new HashMap<String, Integer>();
      Map<String, Integer> sides = new HashMap<String, Integer>();
      Map<String, Integer> drinks = new HashMap<String, Integer>();
      Map<String, Integer> desserts = new HashMap<String, Integer>();
      
      //finds number of orders from customer in database
      String query = "SELECT COUNT(*) FROM order_history WHERE last_name = '" + lastName + "' AND first_name = ' " + firstName + "'";
      int count = getSQLint(query, "count");

      if (count != 0) {
        //arrays containing all ordered items from customer
        String[] entreeHistory = new String[count];
        String[] sideHistory = new String[count];
        String[] drinkHistory = new String[count];
        String[] dessertHistory = new String[count];
        
        //gets all orders from specified customer
        query = "SELECT * FROM order_history WHERE last_name = '" + lastName + "' AND first_name = ' " + firstName + "'";
        try (Statement stmt = conn.createStatement()) {
          ResultSet result = stmt.executeQuery(query);            
          for (int i = 0; result.next(); ++i) {
            entreeHistory[i] = result.getString("entree");
            sideHistory[i] = result.getString("side");
            drinkHistory[i] = result.getString("drink");
            dessertHistory[i] = result.getString("dessert");
          }     
        } catch (Exception e) { 
            JOptionPane.showMessageDialog(null, "Error accessing Database for List with: " + query);
        }
        
        //mapping out customers entree history onto hashmap to determine number of each entree
        for (int i = 0; i < entreeNames.length; ++i) {
          for (int j = 0; j < entreeHistory.length; ++j) {
            if (entreeHistory[j].contains(entreeNames[i])) {
              if (entrees.containsKey(entreeNames[i])) {
                entrees.put(entreeNames[i], entrees.get(entreeNames[i]) + 1);
              } else {
                entrees.put(entreeNames[i], 1);
              }
            }
            //mapping out customers side history onto hashmap to determine number of each side
            if (sideHistory[j].contains(sideNames[i])) {
              if (sides.containsKey(sideNames[i])) {
                sides.put(sideNames[i], sides.get(sideNames[i]) + 1);
              } else {
                sides.put(sideNames[i], 1);
              }
            }
            //mapping out customers drink history onto hashmap to determine number of each drink
            if (drinkHistory[j].contains(drinkNames[i])) {
              if (drinks.containsKey(drinkNames[i])) {
                drinks.put(drinkNames[i], drinks.get(drinkNames[i]) + 1);
              } else {
                drinks.put(drinkNames[i], 1);
              }
            }
            //mapping out customers dessert history onto hashmap to determine number of each dessert
            if (dessertHistory[j].contains(dessertsNames[i])) {
              if (desserts.containsKey(dessertsNames[i])) {
                desserts.put(dessertsNames[i], desserts.get(dessertsNames[i]) + 1);
              } else {
                desserts.put(dessertsNames[i], 1);
              }
            }
          }
        }
        //goes through hashmap and determines which entree has most entries
        if (!entrees.isEmpty()) {
          int entreeMax = Collections.max(entrees.values());
          for (Map.Entry<String, Integer> entry : entrees.entrySet()) {
            if (entry.getValue() == entreeMax) {
                recomms[0] = entry.getKey();
                break;
            }
          }
        } else { //if hashmap is empty, output random entree
          recomms[0] = entreeNames[rn.nextInt(entreeNames.length)];
        }
        //goes through hashmap and determines which side has most entries
        if (!sides.isEmpty()) {
          int sideMax = Collections.max(sides.values());
          for (Map.Entry<String, Integer> entry : sides.entrySet()) {
            if (entry.getValue() == sideMax) {
                recomms[1] = entry.getKey();
                break;
            }
          }
        } else { //if hashmap is empty, output random side
          recomms[1] = sideNames[rn.nextInt(sideNames.length)];
        }
        //goes through hashmap and determines which drink has most entries
        if (!drinks.isEmpty()) {
          int drinkMax = Collections.max(drinks.values());
          for (Map.Entry<String, Integer> entry : drinks.entrySet()) {
            if (entry.getValue() == drinkMax) {
              recomms[2] = entry.getKey();
              break;
            }
          }
        } else { //if hashmap is empty, output random drink
          recomms[2] = drinkNames[rn.nextInt(drinkNames.length)];
        }
        //goes through hashmap and determines which dessert has most entries
        if (!desserts.isEmpty()) {
          int dessertMax = Collections.max(desserts.values());
          for (Map.Entry<String, Integer> entry : desserts.entrySet()) {
            if (entry.getValue() == dessertMax) {
              recomms[3] = entry.getKey();
              break;
            }
          }
        } else { //if hashmap is empty, output random dessert
          recomms[3] = dessertsNames[rn.nextInt(dessertsNames.length)];
        }
        //if customer is not in database, return random menu items
      } else {
        recomms[0] = entreeNames[rn.nextInt(entreeNames.length)];
        recomms[1] = sideNames[rn.nextInt(sideNames.length)];
        recomms[2] = drinkNames[rn.nextInt(drinkNames.length)];
        recomms[3] = dessertsNames[rn.nextInt(dessertsNames.length)];
      }
      
      return recomms;
    }

    void updateTrendingItems() {
      try (Statement stmt = conn.createStatement()) {
        //find upper and lower 2 trending items
        Map <String,Integer> trending = new HashMap<String,Integer>();
        ArrayList<String> trendHistory = new ArrayList<String>();
        //finds entree ordered on 2005 for lower trending items from database
        String trendQuery = "SELECT entree FROM order_history WHERE date > '2020-12-05'";
        ResultSet res = stmt.executeQuery(trendQuery);
        while(res.next()) {
          String t = res.getString("entree");
          trendHistory.add(t);
        }
        //mapping out entree order history onto hashmap to determine number of each entree
        for(String x: entreeNames) {
          for(String y: trendHistory) {
            if (y.contains(x)) {
              if (trending.containsKey(x)) {
                trending.put(x, trending.get(x) + 1);
              } else {
                trending.put(x, 1);
              }
            }
          }
        }
        //find entree with most entries from certain timespan
        int hi1 = Collections.max(trending.values());
        for (Map.Entry<String, Integer> e: trending.entrySet()) {
          if (e.getValue() == hi1) {
              high1s = e.getKey();
          }
        }
        trending.remove(high1s);
        //find entree with second most entries from certain timespan
        int hi2 = Collections.max(trending.values());
        for (Map.Entry<String, Integer> e: trending.entrySet()) {
          if (e.getValue() == hi2) {
              high2s = e.getKey();
          }
        }
        //find entree with least entries from certain timespan
        int lo1 = Collections.min(trending.values());
        for (Map.Entry<String, Integer> e: trending.entrySet()) {
          if (e.getValue() == lo1) {
              low1s = e.getKey();
          }
        }
        trending.remove(low1s);
        //find entree with second most entries from certain timespan
        int lo2 = Collections.min(trending.values());
        for (Map.Entry<String, Integer> e: trending.entrySet()) {
          if (e.getValue() == lo2) {
              low2s = e.getKey();
          }
        }
        //output trends into viewable textboxes for customer and employee
        trends.setText("Entrees trending up: " + high1s + " and " + high2s);
        trends2.setText("Entrees trending down: " + low1s + " and " + low2s);
        

      } catch(SQLException exc) {
        JOptionPane.showMessageDialog(null,"Error accessing Database for Trends.");
      }
    }

    //update menu item lists from database
    void updateMenu() {
      //entrees
      String query = "SELECT COUNT(*) FROM entrees";
      int entreeCount = getSQLint(query, "count");
      query = "SELECT name FROM entrees";
      entreeNames = getSQLarray(query, "name", entreeCount);
      entreeList = new JComboBox<String>(entreeNames);

      //sides
      query = "SELECT COUNT(*) FROM sides";
      int sideCount = getSQLint(query, "count");
      query = "SELECT name FROM sides";
      sideNames = getSQLarray(query, "name", sideCount);
      sideList = new JComboBox<String>(sideNames);

      //drinks
      query = "SELECT COUNT(*) FROM drinks";
      int drinkCount = getSQLint(query, "count");
      query = "SELECT name FROM drinks";
      drinkNames = getSQLarray(query, "name", drinkCount);
      drinkList = new JComboBox<String>(drinkNames);

      //desserts
      query = "SELECT COUNT(*) FROM desserts";
      int dessertCount = getSQLint(query, "count");
      query = "SELECT name FROM desserts";
      dessertsNames = getSQLarray(query, "name", dessertCount);
      dessertList = new JComboBox<String>(dessertsNames);
      
      //Adding Toppings as Checkboxes
      try (Statement stmt = conn.createStatement()) {
        //find number of toppings
        String sqlStatementCount = "SELECT COUNT(*) FROM toppings";
        ResultSet resultCount = stmt.executeQuery(sqlStatementCount);

        int toppingCount = 0;
        //get number of toppings
        while(resultCount.next()) {
          toppingCount = Integer.parseInt(resultCount.getString("count"));
        }         
        
        toppingList = new JCheckBox[toppingCount];
        //create an SQL statement
        String sqlStatement = "SELECT name FROM toppings";
        //send statement to DBMS
        ResultSet result = stmt.executeQuery(sqlStatement);
        int count = 0;

        //adds each topping as a checkbox
        while (result.next()) {
          toppingList[count] = new JCheckBox(result.getString("name"));
          //sets first 5 topping checkboxes to checked state
          if(count < 6)
          {
            toppingList[count].setSelected(true);
          }
          CustomizePanel.add(toppingList[count]);
          count++;
        }
      } catch(SQLException exc) {
        JOptionPane.showMessageDialog(null,"Error accessing Database for Toppings.");
      }

      //create menu list for manager mode
      int menuCount = entreeCount + sideCount + drinkCount + dessertCount;
      int menuIndex = 0;
      
      menu = new String[menuCount];
      for (int i = 0; i < entreeCount; ++i) {
        menu[menuIndex++] = entreeNames[i];
      }
      for (int i = 0; i < sideCount; ++i) {
        menu[menuIndex++] = sideNames[i];
      }
      for (int i = 0; i < drinkCount; ++i) {
        menu[menuIndex++] = drinkNames[i];
      }
      for (int i = 0; i < dessertCount; ++i) {
        menu[menuIndex++] = dessertsNames[i];
      }
      
      menuList = new JComboBox<String>(menu);
    }

    //sets all order items to default values 
    //so new order can be made
    void setDefault() {
      totalPrice = 0.0;
      custOrder.setText("Your Order: \n\n");
      selectedTopping = "";
      lastName = "";
      firstName = "";
      selectedDessert = "";
      selectedDrink = "";
      selectedEntree = "";
      selectedSide = "";
      totalDessert = "";
      totalDrink = "";
      totalEntree = "";
      totalSide = "";
      custFirstNameText.setText("");
      custLastNameText.setText("");
      tryFoodOut.setText("");
      total.setText("Total: $0.00");
      addNewItem.setText("");
      addNewPrice.setText("");
      addNewStock.setText("");
      changePrice.setText("");
      changeStock.setText("");
    }
  }

  public static void main(String args[]) {
    dbSetup my = new dbSetup();

    //Building the connection
     Connection conn = null;
     try {
        Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/db902_group13_project2",
           my.user, my.pswd);
     } catch (Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
     }//end try catch

     JOptionPane.showMessageDialog(null,"Opened database successfully");
    

     try{    
       JOptionPane.showMessageDialog(null, "WELCOME TO TOP OF THE HILL BURGER");
       //start GUI
       GUI gui = new GUI(conn);
  
       
   } catch (Exception e){
     JOptionPane.showMessageDialog(null,"Error accessing Database (General).");
   }
  }//end main
}//end Class