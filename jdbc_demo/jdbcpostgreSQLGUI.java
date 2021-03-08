import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.*;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Random;
/*
CSCE 315
9-25-2019
 */


public class jdbcpostgreSQLGUI {

  public static class GUI extends JFrame {
    JFrame frame = new JFrame("GUI");  
    JPanel Container = new JPanel();
    JPanel Home = new JPanel();
    JPanel EmployeeCheckout = new JPanel();
    JPanel CustomerCheckout = new JPanel();
    JPanel ManagerMode = new JPanel();

    //general exit button back to home
    JButton exitToHome = new JButton("Exit");
    
    //buttons for home
    JButton customerButton = new JButton("Customer");
    JButton employeeButton = new JButton("Employee");

    //buttons for employee mode and self-checkout mode
    //self-checkout does not have managerButton
    JButton managerButton = new JButton("Manager Mode");
    JButton submitButton = new JButton("Submit Order");
    JComboBox<String> entreeList = null, sideList = null, drinkList = null, dessertList = null, menuList = null;
    String[] entreeNames, sideNames, drinkNames, dessertsNames;

    //buttons for manager mode
    JButton priceButton = new JButton("Price-Override($)");
    JButton maintenanceButton = new JButton("Inventory Update");

    //text entries for employee mode and self-checkout
    JLabel custFirstNameLabel = new JLabel("Enter First Name");
    JTextField custFirstNameText = new JTextField(10);
    
    JLabel custLastNameLabel = new JLabel("Enter Last Name");
    JTextField custLastNameText = new JTextField(10);

    JLabel managerChangeLabel = new JLabel("Enter New Value");
    JTextField managerChange = new JTextField(10);

    JButton tryFood = new JButton("We Recommend... ");
    JLabel tryFoodOut = new JLabel("");

    JLabel stockReccom = new JLabel("");
    JLabel priceReccom = new JLabel("Recommended Menu Item Price Change: ");


    CardLayout cLayout = new CardLayout();
    Connection conn;
    int index=0;
    public GUI(Connection con) {


      this.conn = con;
      Container.setLayout(cLayout);
      Container.add(Home, "1");
      Container.add(EmployeeCheckout, "2");
      Container.add(CustomerCheckout, "3");
      Container.add(ManagerMode, "4");

      cLayout.show(Container, "1");
      
      /* Home Page */
      Home.add(customerButton);
      Home.add(employeeButton);
      
      //Adding Sides
      try (Statement stmt = conn.createStatement()) {
        //create an SQL statement
        String sqlStatement = "SELECT name FROM sides";
        //send statement to DBMS
        ResultSet result = stmt.executeQuery(sqlStatement);
        
        sideNames = new String[10];
        String sideName = "";
        int count = 0;

        while (result.next()) {
          sideNames[count] = result.getString("name");
          sideName = result.getString("name");
          //System.out.println(sideName);
          count++;
        }

        sideList = new JComboBox<String>(sideNames);
      
      } catch(SQLException exc) {
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }

      //Adding Entrees
      try (Statement stmt = conn.createStatement()) {
        //create an SQL statement
        String sqlStatement = "SELECT name FROM entrees";
        //send statement to DBMS
        ResultSet result = stmt.executeQuery(sqlStatement);
        
        entreeNames = new String[10];
        String entreeName = "";
        int count = 0;

        while (result.next()) {
          entreeNames[count] = result.getString("name");
          entreeName = result.getString("name");
          //System.out.println(entreeName);
          count++;
        }

        entreeList = new JComboBox<String>(entreeNames);
        
      } catch(SQLException exc) {
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }
          
      //Added Drinks
      try (Statement stmt = conn.createStatement()) {
        //create an SQL statement
        String sqlStatement = "SELECT name FROM drinks";
        //send statement to DBMS
        ResultSet result = stmt.executeQuery(sqlStatement);
        
        drinkNames = new String[10];
        String drinkName = "";
        int count = 0;

        while (result.next()) {
          drinkNames[count] = result.getString("name");
          drinkName = result.getString("name");
          //System.out.println(drinkName);
          count++;
        }

        drinkList = new JComboBox<String>(drinkNames); 
            
      } catch(SQLException exc) {
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }
      
      //Adding Desserts
      try (Statement stmt = conn.createStatement()) {
        //create an SQL statement
        String sqlStatement = "SELECT name FROM desserts";
        //send statement to DBMS
        ResultSet result = stmt.executeQuery(sqlStatement);
        
        dessertsNames = new String[10];
        String dessertsName = "";
        int count = 0;

        while (result.next()) {
          dessertsNames[count] = result.getString("name");
          dessertsName = result.getString("name");
          //System.out.println(dessertsName);
          count++;
        }

        dessertList = new JComboBox<String>(dessertsNames);
      
      } catch(SQLException exc) {
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }
      
      
      String[] menu = new String[40];

      for(int i = 0; i < 10; i++)
      {
        menu[i] = entreeNames[i];
        menu[i+10] = sideNames[i];
        menu[i+20] = drinkNames[i];
        menu[i+30] = dessertsNames[i];
      }
      
      menuList = new JComboBox<String>(menu);

      /* Buttons */
      exitToHome.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          Home.add(customerButton);
          Home.add(employeeButton);
          // try {   
          //   conn.close();
          //   JOptionPane.showMessageDialog(null,"Connection Closed.");
          // } catch(Exception exc) {
          //   JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
          // }//end try catch
          cLayout.show(Container, "1");
        }
      });

      employeeButton.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          
          /* Employee Checkout Page */
          EmployeeCheckout.add(exitToHome);
          EmployeeCheckout.add(managerButton);
          EmployeeCheckout.add(entreeList);
          EmployeeCheckout.add(sideList);
          EmployeeCheckout.add(drinkList);
          EmployeeCheckout.add(dessertList);
          EmployeeCheckout.add(custFirstNameLabel);
          EmployeeCheckout.add(custFirstNameText);
          EmployeeCheckout.add(custLastNameLabel);
          EmployeeCheckout.add(custLastNameText);
          EmployeeCheckout.add(submitButton);
          cLayout.show(Container, "2");
        }
      });

      customerButton.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          /* Customer Checkout Page */
          CustomerCheckout.add(exitToHome);
          CustomerCheckout.add(entreeList);
          CustomerCheckout.add(sideList);
          CustomerCheckout.add(drinkList);
          CustomerCheckout.add(dessertList);
          CustomerCheckout.add(custFirstNameLabel);
          CustomerCheckout.add(custFirstNameText);
          CustomerCheckout.add(custLastNameLabel);
          CustomerCheckout.add(custLastNameText);
          CustomerCheckout.add(submitButton);
          CustomerCheckout.add(tryFood);
          CustomerCheckout.add(tryFoodOut);

          
          
        
          
          cLayout.show(Container, "3");          
        }
      });

      managerButton.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          /* Manager Mode Page */
          ManagerMode.add(priceButton);
          ManagerMode.add(maintenanceButton);
          ManagerMode.add(employeeButton);
          ManagerMode.add(exitToHome);

          ManagerMode.add(menuList);
          ManagerMode.add(managerChangeLabel);
          ManagerMode.add(managerChange);
          ManagerMode.add(stockReccom);
          ManagerMode.add(priceReccom);
          
          String stockNameReccom = "";
          String priceNameReccom = "";
          try (Statement stmt = conn.createStatement()) {
            int min = 0;
            String s1 = "SELECT MIN(stock) FROM entrees";
            //System.out.println(s1);
            ResultSet result = stmt.executeQuery(s1);            
            while(result.next()) {
              min = Integer.parseInt(result.getString("min"));
            }     

            String s2 = "SELECT name FROM entrees WHERE stock = " + min;
            //System.out.println(s2);
            result = stmt.executeQuery(s2);            
            while(result.next()) {
              stockNameReccom = result.getString("name");
            }     

            double min2 = 0;
            s1 = "SELECT MIN(price) FROM entrees";
            //System.out.println(s1);
            result = stmt.executeQuery(s1);            
            while(result.next()) {
              min2 = Double.parseDouble(result.getString("min"));
            }     

            s2 = "SELECT name FROM entrees WHERE price = " + min2;
            //System.out.println(s2);
            result = stmt.executeQuery(s2);            
            while(result.next()) {
              priceNameReccom = result.getString("name");
            }     
          
          } catch(SQLException exc) {
            JOptionPane.showMessageDialog(null,"Error accessing Database.");
          }
          
          stockReccom.setText("Recommended Menu Item Stock Change: " + stockNameReccom);
          priceReccom.setText("Recommended Menu Item Price Change: " + priceNameReccom);
          
          cLayout.show(Container, "4");
        }
      });

      tryFood.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          /* Manager Mode Page */
          Random rn = new Random();
          String food = entreeNames[rn.nextInt(10)];
          tryFoodOut.setText(food);

          
          
        }
      });

      priceButton.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          index = menuList.getSelectedIndex();
          String chosen = "";
          if(index < 10) {
            chosen = "entrees";
          }
          else if(index < 20) {
            chosen = "sides";
          }
          else if(index < 30) {
            chosen = "drinks";
          }
          else {
            chosen = "desserts";
          }
          try (Statement stmt = conn.createStatement()) {

            Double newPrice = Double.parseDouble(managerChange.getText());
            String qs0 = "UPDATE " + chosen + " SET price = " + newPrice + " WHERE name = " + "'" + menuList.getSelectedItem() + "'";
            //System.out.println(qs0);
            stmt.executeUpdate(qs0);
          
          } catch(SQLException exc) {
            JOptionPane.showMessageDialog(null,"Error accessing Database.");
          }
          
        }
      });
      
      maintenanceButton.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          index = menuList.getSelectedIndex();
          String chosen = "";
          if(index < 10) {
            chosen = "entrees";
          }
          else if(index < 20) {
            chosen = "sides";
          }
          else if(index < 30) {
            chosen = "drinks";
          }
          else {
            chosen = "desserts";
          }

          try (Statement stmt = conn.createStatement()) {

            int newStock = Integer.parseInt(managerChange.getText());
            
            String s0 = "UPDATE " + chosen;
            String s1 = " SET stock = " + newStock;
            String s2 = " WHERE name = '" + menuList.getSelectedItem() + "'";
            String sqlStatement = s0 + s1 + s2;
            ////System.out.println(sqlStatement);
            stmt.executeUpdate(sqlStatement);            
          
          } catch(SQLException exc) {
            JOptionPane.showMessageDialog(null,"Error accessing Database.");
          }
          
        }
      });
      

      submitButton.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
          String selectedDessert = String.valueOf(dessertList.getSelectedItem());
          String selectedDrink = String.valueOf(drinkList.getSelectedItem());
          String selectedEntree = String.valueOf(entreeList.getSelectedItem());
          String selectedSide = String.valueOf(sideList.getSelectedItem());
          String lastName = custLastNameText.getText();
          String firstName = custFirstNameText.getText();

          Calendar cal = Calendar.getInstance();
          SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
          String date = df.format(cal.getTime());
          int new_id = 0;

          try (Statement stmt = conn.createStatement()) {
            
            String s0 = "SELECT MAX(id) FROM order_history";
            //System.out.println(s0);
            ResultSet result = stmt.executeQuery(s0);            
            while(result.next()) {
              new_id = Integer.parseInt(result.getString("max")) + 1;
            }     
          
          } catch(SQLException exc) {
            JOptionPane.showMessageDialog(null,"Error accessing Database.");
          }
          try (Statement stmt = conn.createStatement()) {
            
            
            //create an SQL statement
            String s1 = "VALUES('"+ lastName.toUpperCase()+"', '"+firstName.toUpperCase()+"', '"+date+"', '"+ selectedEntree+"', '"+ selectedSide+"', '"+ selectedDrink+"', '"+ selectedDessert+"', "+String.valueOf(new_id)+")";  
            //System.out.println(s1);                  
            String sqlStatement = "INSERT INTO order_history " + s1;
           
            //send statement to DBMS
            stmt.executeUpdate(sqlStatement);
            
          
          } catch(SQLException exc) {
            JOptionPane.showMessageDialog(null,"Error accessing Database.");
          }
        }
      });
      
      frame.add(Container);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.pack();
      frame.setSize(800, 400);
      frame.setVisible(true);
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
     //create a statement object
       
       JOptionPane.showMessageDialog(null, "WELCOME TO TOP OF THE HILL BURGER");

       //HomePage home = new HomePage();
       //EmployeeCheckout emp_checkout = new EmployeeCheckout();
       GUI gui = new GUI(conn);

       
       
   } catch (Exception e){
     JOptionPane.showMessageDialog(null,"Error accessing Database.");
   }
   
   //JOptionPane.showMessageDialog(null);
    //closing the connection
    /*try {   
      conn.close();
      JOptionPane.showMessageDialog(null,"Connection Closed.");
    } catch(Exception e) {
      JOptionPane.showMessageDialog(null,"Connection NOT Closed.");*/
    //}//end try catch
  }//end main
}//end Class
