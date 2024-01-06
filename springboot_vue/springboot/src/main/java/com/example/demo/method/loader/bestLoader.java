package com.example.demo.method.loader;

import com.mchange.v2.c3p0.ComboPooledDataSource;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
//-v center.csv enterprise.csv model.csv staff.csv in_stoke_test.csv task2_test_data_final_public.tsv update_final_test.tsv delete_final.tsv




public class bestLoader {
    private static final int BATCH_SIZE = 500;
    private static final Map<String, Center> centerMap = new HashMap<>();
    private static final Map<String, Enterprise> enterpriseMap = new HashMap<>();
    private static final Map<String, Model> modelMap = new HashMap<>();
    private static final Map<String, Staff> staffMap = new HashMap<>();
    private static final Map<String, stockIns> stockMap = new HashMap<>();
    private static final Map<String, Order> orderMap = new HashMap<>();
    private static final Map<String, Integer> deleteMap = new HashMap<>();
    private static final Map<String, Contract> contractMap = new HashMap<>();
    private static final Map<String,bill> billMap=new HashMap<>();
    private static final ComboPooledDataSource dataSource = new ComboPooledDataSource();

    private static Connection con5 = null;

    private static PreparedStatement stmtCenter = null;
    private static PreparedStatement stmtEnterprise = null;
    private static PreparedStatement stmtModel = null;
    private static PreparedStatement stmtStaff = null;
    private static PreparedStatement stmtContract = null;
    private static PreparedStatement stmtAddStock = null;
    private static PreparedStatement stmtUpdateStock = null;
    private static PreparedStatement stmtAddOrder = null;
    private static PreparedStatement stmtUpdateOrder = null;
    private static PreparedStatement stmtDeleteOrder = null;
    private static PreparedStatement stmtMethodDelete = null;
    private static PreparedStatement stmtGetInfo = null;

    public static String[] fileName;
    private static Boolean done = false;
    private static boolean ready = false;

    public static boolean verbose = false;

    public static void openDB(String host, String dbname,
                              String user, String pwd) throws SQLException {

        try {
            //
            dataSource.setDriverClass("org.postgresql.Driver");
//            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            System.err.println("Cannot find the Postgres driver. Check CLASSPATH.");
            System.exit(1);
        }
        String url = "jdbc:postgresql://" + host + "/" + dbname;
        dataSource.setUser(user);
        dataSource.setPassword(pwd);
        dataSource.setJdbcUrl(url);

        try {

            dataSource.setInitialPoolSize(8);
            dataSource.setMaxPoolSize(12);
            con5 = dataSource.getConnection();
            if (verbose) {
                System.out.println("Successfully connected to the database "
                        + dbname + " as " + user);
            }
            con5.setAutoCommit(false);
        } catch (SQLException e) {
            System.err.println("Database connection failed");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        try {

            stmtAddStock = con5.prepareStatement("insert into inventory(id,product_model,supply_center,quantity,purchase_price)"
                    + "values(?,?,?,?,?)");

            stmtUpdateStock = con5.prepareStatement("update inventory set quantity=?,purchase_price=? where product_model=?"
                    + " and supply_center=?");

            stmtAddOrder = con5.prepareStatement("insert into orders(id,contract_num,enterprise,product_model,quantity," +
                    "contract_manager,contract_date,estimated_delivery_date, lodgement_date,salesman_num,contract_type)"
                    + " values(?,?,?,?,?,?,?,?,?,?,?)");

            stmtUpdateOrder = con5.prepareStatement("update orders set quantity=? , estimated_delivery_date=? , " +
                    "lodgement_date=? where id=?");

            stmtDeleteOrder = con5.prepareStatement("delete from orders where contract_num=? and product_model=?"
                    + " and salesman_num=?");

            stmtMethodDelete = con5.prepareStatement("delete from orders where id =?");

            stmtGetInfo = con5.prepareStatement("select * from (select *," +
                    " rank() over (partition by (contract_num, salesman_num) order by estimated_delivery_date , product_model)" +
                    " as rank from orders) as s where s.rank = ? and contract_num = ? and salesman_num = ?");


        } catch (SQLException e) {
            System.err.println("Insert statement failed");
            System.err.println(e.getMessage());
            closePreStmt();
            closeDB();
            System.exit(1);
        }
    }

    public static void closeDB() {
        if (con5 != null) {
            try {
                if (stmtAddStock != null && stmtUpdateStock != null && stmtAddOrder != null && stmtUpdateOrder != null && stmtDeleteOrder != null
                        && stmtMethodDelete != null && stmtGetInfo != null) {
                    stmtAddStock.close();
                    stmtUpdateStock.close();
                    stmtAddOrder.close();
                    stmtUpdateOrder.close();
                    stmtDeleteOrder.close();
                    stmtMethodDelete.close();
                    stmtGetInfo.close();
                }
                con5.close();
            } catch (Exception e) {
                // Forget about it
            }
        }
//        dataSource.close();
    }

    public static void loadCenter()
            throws SQLException {
        Connection con = dataSource.getConnection();
        if (con != null) {
            stmtCenter = con.prepareStatement("insert into center(id,name)"
                    + " values(?,?)");
            con.setAutoCommit(false);
            int i = 0;
            Center center;
            for (Map.Entry<String, Center> ignored : centerMap.entrySet()) {
                center = ignored.getValue();
                i++;
                stmtCenter.setInt(1, center.id);
                stmtCenter.setString(2, center.name);
                stmtCenter.addBatch();
                if (i % BATCH_SIZE == 0) {
                    stmtCenter.executeBatch();
                    stmtCenter.clearBatch();
                }
            }
            if (centerMap.size() % BATCH_SIZE != 0) {
                stmtCenter.executeBatch();
                stmtCenter.clearBatch();
            }
            con.commit();
            stmtCenter.close();
            con.close();
        }
    }

    public static void loadContract()
            throws SQLException {
        Connection con = dataSource.getConnection();
        if (con != null) {
            int i = 0;
            Contract contract;
            stmtContract = con.prepareStatement("insert into contract(id,number) " + " values(?,?)");
            con.setAutoCommit(false);
            for (Map.Entry<String, Contract> ignored : contractMap.entrySet()) {
                contract = ignored.getValue();
                i++;
                stmtContract.setInt(1, i);
                stmtContract.setString(2, contract.name);
                stmtContract.addBatch();
                if (i % BATCH_SIZE == 0) {
                    stmtContract.executeBatch();
                    stmtContract.clearBatch();
                }
            }
            if (contractMap.size() % BATCH_SIZE != 0) {
                stmtContract.executeBatch();
                stmtContract.clearBatch();
            }
            con.commit();
            stmtContract.close();
            con.close();
        }
    }

    public static void loadEnterprise()
            throws SQLException {
        Connection con = dataSource.getConnection();
        if (con != null) {
            stmtEnterprise = con.prepareStatement("insert into enterprise(id,name,country,city,supply_center,industry)"
                    + " values(?,?,?,?,?,?)");
            con.setAutoCommit(false);
            int i = 0;
            Enterprise enterprise;
            for (Map.Entry<String, Enterprise> ignored : enterpriseMap.entrySet()) {
                enterprise = ignored.getValue();
                i++;
                stmtEnterprise.setInt(1, enterprise.id);
                stmtEnterprise.setString(2, enterprise.name);
                stmtEnterprise.setString(3, enterprise.country);

                stmtEnterprise.setString(4, enterprise.city);
                stmtEnterprise.setString(5, enterprise.supply_center);
                stmtEnterprise.setString(6, enterprise.industry);
                stmtEnterprise.addBatch();
                if (i % BATCH_SIZE == 0) {
                    stmtEnterprise.executeBatch();
                    stmtEnterprise.clearBatch();
                }
            }
            if (enterpriseMap.size() % BATCH_SIZE != 0) {
                stmtEnterprise.executeBatch();
                stmtEnterprise.clearBatch();
            }
            con.commit();
            stmtEnterprise.close();
            con.close();
        }
    }

    public static void loadModel()
            throws SQLException {
        Connection con = dataSource.getConnection();
        if (con != null) {
            stmtModel = con.prepareStatement("insert into model(id,number,model,name,unit_price) " + " values(?,?,?,?,?)");
            con.setAutoCommit(false);
            int i = 0;
            Model model;
            for (Map.Entry<String, Model> ignored : modelMap.entrySet()) {
                model = ignored.getValue();
                i++;
                stmtModel.setInt(1, model.id);
                stmtModel.setString(2, model.number);
                stmtModel.setString(3, model.model);
                stmtModel.setString(4, model.name);
                stmtModel.setInt(5, model.unit_price);
                stmtModel.addBatch();
                if (i % BATCH_SIZE == 0) {
                    stmtModel.executeBatch();
                    stmtModel.clearBatch();
                }
            }
            if (modelMap.size() % BATCH_SIZE != 0) {
                stmtModel.executeBatch();
                stmtModel.clearBatch();
            }
            con.commit();
            stmtModel.close();
            con.close();
        }


    }

    public static void loadStaff()
            throws SQLException {
        Connection con = dataSource.getConnection();

        if (con != null) {
            stmtStaff = con.prepareStatement("insert into staff(id,name,age,gender,number,supply_center,mobile_number,type)"
                    + " values(?,?,?,?,?,?,?,?)");
            con.setAutoCommit(false);
            Staff staff;
            int i = 0;
            for (Map.Entry<String, Staff> ignored : staffMap.entrySet()) {
                staff = ignored.getValue();
                i++;
                stmtStaff.setInt(1, staff.id);
                stmtStaff.setString(2, staff.name);
                stmtStaff.setInt(3, staff.age);
                stmtStaff.setString(4, staff.gender);
                stmtStaff.setString(5, staff.number);
                stmtStaff.setString(6, staff.supply_center);
                stmtStaff.setString(7, staff.mobile_number);
                stmtStaff.setString(8, staff.type);
                stmtStaff.addBatch();
                if (i % BATCH_SIZE == 0) {
                    stmtStaff.executeBatch();
                    stmtStaff.clearBatch();
                }
            }
            if (staffMap.size() % BATCH_SIZE != 0) {
                stmtStaff.executeBatch();
                stmtStaff.clearBatch();
            }
            con.commit();
            stmtStaff.close();
            con.close();
        }
    }

    public  void loadBill() throws SQLException{
        Connection con = dataSource.getConnection();
        Statement statement=con.createStatement();
        ResultSet resultSet=statement.executeQuery("select id,product_model,enterprise,quantity from orders");
        while (resultSet.next()){
            String id = resultSet.getString("id");
            String model=resultSet.getString("product_model");
            String enterprise=resultSet.getString("enterprise");
            String quantity=resultSet.getString("quantity");
            int profit=(stockMap.get(model+","+enterpriseMap.get(enterprise).supply_center).purchase_price
                    -modelMap.get(model).unit_price)*Integer.parseInt(quantity);
            bill Bill= new bill(Integer.parseInt(id),model,profit);
            billMap.put(id,Bill);
        }
        PreparedStatement stmtBill = con.prepareStatement("insert into bill(order_id,model,profit)"
                + " values(?,?,?)");
        con.setAutoCommit(false);
        bill Bill;
        int i = 0;
        for (Map.Entry<String, bill> ignored : billMap.entrySet()) {
            Bill = ignored.getValue();
            i++;
            stmtBill.setInt(1, Bill.order_id);
            stmtBill.setString(2, Bill.model);
            stmtBill.setInt(3, Bill.profit);
            stmtBill.addBatch();
            if (i % BATCH_SIZE == 0) {
                stmtBill.executeBatch();
                stmtBill.clearBatch();
            }
        }
        if (billMap.size() % BATCH_SIZE != 0) {
            stmtBill.executeBatch();
            stmtBill.clearBatch();
        }
        con.commit();
        stmtBill.close();
        con.close();

    }

    public String  showBill() throws SQLException {
//        loadBill();
        Connection con =dataSource.getConnection();
        ResultSet resultSet=con.createStatement().executeQuery("select * from bill");
        StringBuilder sb=new StringBuilder();
        sb.append("[");
        int x = 1;
        while (resultSet.next()) {
            if (x == 1) {
                sb.append("{");
            } else sb.append(",{");
            x++;
            String x1 = resultSet.getString("order_id");
            String x2 = resultSet.getString("model");
            String x3 = resultSet.getString("profit");

            sb.append(" \"order_id\" :");
            sb.append(x1).append(" ,");
            sb.append(" \"model\" :");
            sb.append("\"").append(x2).append("\"").append(",");
            sb.append(" \"profit\" :");
            sb.append("\"").append(x3).append("\"").append("}");

        }
        sb.append("]");

        con.close();

        return sb.toString();
    }

    public static void stock_in(String fileName) {
        if (con5 != null) {
            try (BufferedReader infile
                         = new BufferedReader(new FileReader(fileName))) {
                int i = 0, count = 0;
                String line;
                String[] parts;
                line = infile.readLine();
                while ((line = infile.readLine()) != null) {
                    parts = line.split(",");
                    if (parts.length > 1) {
                        String ckey, skey, mkey;
                        if (parts.length == 7) {
                            ckey = parts[1];
                            skey = parts[3];
                            mkey = parts[2];
                        } else {
                            ckey = parts[1] + "," + parts[2];
                            skey = parts[4];
                            mkey = parts[3];
                        }
                        if (staffMap.get(skey) == null || centerMap.get(ckey) == null
                                || modelMap.get(mkey) == null ||
                                !ckey.equals(staffMap.get(skey).supply_center) ||
                                !staffMap.get(skey).type.equals("Supply Staff")) {
//                        System.out.println(parts[0]);

                        } else {
                            String unique;
                            stockIns stockIn;
                            if (parts.length == 7) {
                                stockIn = new stockIns(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3],
                                        Integer.parseInt(parts[6]),Integer.parseInt(parts[5]));
                                unique = parts[2] + "," + ckey;
                            } else {
                                stockIn = new stockIns(Integer.parseInt(parts[0]), ckey, parts[3], parts[4],
                                        Integer.parseInt(parts[7]),Integer.parseInt(parts[6]));
                                unique = parts[3] + "," + ckey;
                            }

                            if (stockMap.get(unique) != null) {
                                stockIn.quantity += stockMap.get(unique).quantity;
                                // 更新库存的数量和价格
                                stmtUpdateStock.setInt(1, stockIn.quantity);
                                stmtUpdateStock.setInt(2, stockIn.purchase_price);
                                stmtUpdateStock.setString(3, stockIn.model);
                                stmtUpdateStock.setString(4, stockIn.center);
                                stmtUpdateStock.addBatch();
                            } else {
                                //增加新的一行
                                i++;// inventory_id
                                stmtAddStock.setInt(1, i);
                                stmtAddStock.setString(2, stockIn.model);
                                stmtAddStock.setString(3, stockIn.center);
                                stmtAddStock.setInt(4, stockIn.quantity);
                                stmtAddStock.setInt(5, stockIn.purchase_price);
                                stmtAddStock.addBatch();
                            }
                            stockMap.put(unique, stockIn);
                            count++;
                            if (count % BATCH_SIZE == 0) {
                                stmtAddStock.executeBatch();
                                stmtUpdateStock.executeBatch();
                                stmtAddStock.clearBatch();
                                stmtUpdateStock.clearBatch();
                            }
                        }
                    }
                }
                if (count % BATCH_SIZE != 0) {
                    stmtAddStock.executeBatch();
                    stmtUpdateStock.executeBatch();
                    stmtAddStock.clearBatch();
                    stmtUpdateStock.clearBatch();
                }
                System.out.println(count);
                con5.commit();
            } catch (SQLException se) {
                System.err.println("SQL error: " + se.getMessage());
                try {
                    con5.rollback();
                    stmtAddStock.close();
                    stmtUpdateStock.close();
                } catch (Exception e2) {
                }
                closeDB();
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Fatal error: " + e.getMessage());
                try {
                    con5.rollback();
                    stmtAddStock.close();
                    stmtUpdateStock.close();
                } catch (Exception e2) {
                }
                closeDB();
                System.exit(1);
            }
        }
    }

    public static void placeOrder(String fileName) {

        if (con5 != null) {
            try (BufferedReader infile
                         = new BufferedReader(new FileReader(fileName))) {
                int i = 0;
                String line;
                String[] parts;
                line = infile.readLine();
                while ((line = infile.readLine()) != null) {
                    parts = line.split("\t");
                    if (parts.length > 1) {
                        String unique = parts[2] + "," + enterpriseMap.get(parts[1]).supply_center;//this is stock_unique
                        if (!staffMap.get(parts[8]).type.equals("Salesman") || stockMap.get(unique) == null
                                || Integer.parseInt(parts[3]) > stockMap.get(unique).quantity) {

                        } else {
                            String order_unique = parts[0] + "," + parts[2] + "," + parts[8];
                            i++;
                            //添加订单
                            Order order = new Order(i, parts[0], parts[1], parts[2], parts[8], Integer.parseInt(parts[3])
                                    , parts[6], parts[7]);
                            orderMap.put(order_unique, order);
                            String de = parts[0] + "," + parts[8];
                            if (deleteMap.get(de) == null) {
                                deleteMap.put(de, 1);
                            } else {
                                int result = deleteMap.get(de);
                                result += 1;
                                deleteMap.put(de, result);
                            }
                            stmtAddOrder.setInt(1, i);
                            stmtAddOrder.setString(2, parts[0]);
                            stmtAddOrder.setString(3, parts[1]);
                            stmtAddOrder.setString(4, parts[2]);
                            stmtAddOrder.setInt(5, Integer.parseInt(parts[3]));
                            stmtAddOrder.setString(6, parts[4]);
                            stmtAddOrder.setString(7, parts[5]);
                            stmtAddOrder.setString(8, parts[6]);
                            stmtAddOrder.setString(9, parts[7]);
                            stmtAddOrder.setString(10, parts[8]);
                            stmtAddOrder.setString(11, parts[9]);
                            stmtAddOrder.addBatch();
                            //库存减少并做更新
                            stockMap.get(unique).quantity -= Integer.parseInt(parts[3]);
                            stmtUpdateStock.setInt(1, stockMap.get(unique).quantity);
                            stmtUpdateStock.setString(3, parts[2]);
                            stmtUpdateStock.setString(4, stockMap.get(unique).center);
                            stmtUpdateStock.addBatch();
                            if (i % BATCH_SIZE == 0) {
                                stmtAddOrder.executeBatch();
                                stmtUpdateStock.executeBatch();
                                stmtAddOrder.clearBatch();
                                stmtUpdateStock.clearBatch();
                            }
                        }
                    }

                }
                if (i % BATCH_SIZE != 0) {
                    stmtAddOrder.executeBatch();
                    stmtUpdateStock.executeBatch();
                    stmtAddOrder.clearBatch();
                    stmtUpdateStock.clearBatch();
                }

                System.out.println(i);
                con5.commit();

            } catch (SQLException se) {
                System.err.println("SQL error: " + se.getMessage());
                try {
                    con5.rollback();
                    stmtAddOrder.close();
                    stmtUpdateStock.close();
                } catch (Exception e2) {
                }
                closeDB();
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Fatal error: " + e.getMessage());
                try {
                    con5.rollback();
                    stmtAddOrder.close();
                    stmtUpdateStock.close();
                } catch (Exception e2) {
                }
                closeDB();
                System.exit(1);
            }
        }

    }

    public static void updateOrder(String fileName) {

        if (con5 != null) {
            try (BufferedReader infile
                         = new BufferedReader(new FileReader(fileName))) {
                int i = 0;
                String line;
                String[] parts;
                line = infile.readLine();
                while ((line = infile.readLine()) != null) {
                    parts = line.split("\t");
                    String order_unique = parts[0] + "," + parts[1] + "," + parts[2];
                    String stock_unique = parts[1] + "," + staffMap.get(parts[2]).supply_center;
                    if (orderMap.get(order_unique) == null || stockMap.get(stock_unique).quantity -
                            (Integer.parseInt(parts[3]) - orderMap.get(order_unique).quantity) < 0) {

                    } else {
                        i++;
                        int add;
                        if (Integer.parseInt(parts[3]) == 0) {
                            stmtDeleteOrder.setString(1, parts[0]);
                            stmtDeleteOrder.setString(2, parts[1]);
                            stmtDeleteOrder.setString(3, parts[2]);
                            String de = parts[0] + "," + parts[2];
                            int result = deleteMap.get(de);
                            result -= 1;
                            if (result == 0) {
                                deleteMap.remove(de);
                            } else {
                                deleteMap.put(de, result);
                            }
                            stmtDeleteOrder.addBatch();
                        } else {
                            stmtUpdateOrder.setInt(1, Integer.parseInt(parts[3]));
                            stmtUpdateOrder.setString(2, parts[4]);
                            stmtUpdateOrder.setString(3, parts[5]);
                            stmtUpdateOrder.setInt(4, orderMap.get(order_unique).id);
                            stmtUpdateOrder.addBatch();
                        }
                        add = Integer.parseInt(parts[3]) - orderMap.get(order_unique).quantity;
                        stockMap.get(stock_unique).quantity -= add;
                        stmtUpdateStock.setInt(1, stockMap.get(stock_unique).quantity);
                        stmtUpdateStock.setString(3, parts[1]);
                        stmtUpdateStock.setString(4, stockMap.get(stock_unique).center);
                        stmtUpdateStock.addBatch();
                        if (i % BATCH_SIZE == 0) {
                            stmtUpdateOrder.executeBatch();
                            stmtUpdateStock.executeBatch();
                            stmtDeleteOrder.executeBatch();
                            stmtUpdateOrder.clearBatch();
                            stmtUpdateStock.clearBatch();
                            stmtDeleteOrder.clearBatch();
                        }
                    }
                }
                if (i % BATCH_SIZE != 0) {
                    stmtUpdateOrder.executeBatch();
                    stmtUpdateStock.executeBatch();
                    stmtDeleteOrder.executeBatch();
                    stmtUpdateOrder.clearBatch();
                    stmtUpdateStock.clearBatch();
                    stmtDeleteOrder.clearBatch();
                }
                System.out.println(i);
                con5.commit();

            } catch (SQLException se) {
                System.err.println("SQL error: " + se.getMessage());
                try {
                    con5.rollback();
                    stmtUpdateOrder.close();
                    stmtUpdateStock.close();
                    stmtDeleteOrder.close();
                } catch (Exception e2) {
                }
                closeDB();
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Fatal error: " + e.getMessage());
                try {
                    con5.rollback();
                    stmtUpdateOrder.close();
                    stmtUpdateStock.close();
                    stmtDeleteOrder.close();
                } catch (Exception e2) {
                }
                closeDB();
                System.exit(1);
            }
        }
    }

    public static void deleteOrder(String fileName) {

        if (con5 != null) {
            try (BufferedReader infile
                         = new BufferedReader(new FileReader(fileName))) {
                int i = 0;
                String line;
                String[] parts;
                line = infile.readLine();
                while ((line = infile.readLine()) != null) {
                    parts = line.split("\\s+");
                    if (parts.length > 1) {
                        String de = parts[0] + "," + parts[1];
                        if (deleteMap.get(de) == null) {

                        } else {
                            String id = null;
                            String stock_unique = "";
                            int quantity = 0;
                            stmtGetInfo.setInt(1, Integer.parseInt(parts[2]));
                            stmtGetInfo.setString(2, parts[0]);
                            stmtGetInfo.setString(3, parts[1]);
                            ResultSet resultSet = stmtGetInfo.executeQuery();
                            while (resultSet.next()) {
                                id = resultSet.getString("id");
                                stock_unique = resultSet.getString("product_model") + "," +
                                        staffMap.get(resultSet.getString("salesman_num")).supply_center;
                                quantity = Integer.parseInt(resultSet.getString("quantity"));
                            }
                            if (id != null) {
                                i++;
                                stmtMethodDelete.setInt(1, Integer.parseInt(id));
                                stockMap.get(stock_unique).quantity += quantity;
                                stmtUpdateStock.setInt(1, stockMap.get(stock_unique).quantity);
                                stmtUpdateStock.setString(3, stockMap.get(stock_unique).model);
                                stmtUpdateStock.setString(4, stockMap.get(stock_unique).center);
                                stmtUpdateStock.addBatch();
                                stmtMethodDelete.addBatch();
                            }
                        }
                        if (i % BATCH_SIZE == 0) {
                            stmtMethodDelete.executeBatch();
                            stmtUpdateStock.executeBatch();
                            stmtUpdateStock.clearBatch();
                            stmtMethodDelete.clearBatch();
                        }
                    }
                }
                if (i % BATCH_SIZE != 0) {
                    stmtMethodDelete.executeBatch();
                    stmtUpdateStock.executeBatch();
                    stmtUpdateStock.clearBatch();
                    stmtMethodDelete.clearBatch();
                }
                System.out.println(i);
                con5.commit();

            } catch (SQLException se) {
                System.err.println("SQL error: " + se.getMessage());
                try {
                    con5.rollback();
                    stmtMethodDelete.close();
                    stmtUpdateStock.close();
                } catch (Exception e2) {
                }
                closeDB();
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Fatal error: " + e.getMessage());
                try {
                    con5.rollback();
                    stmtMethodDelete.close();
                    stmtUpdateStock.close();
                } catch (Exception e2) {
                }
                closeDB();
                System.exit(1);
            }
        }
    }

    static class MyTread extends Thread {
        int n;

        public MyTread(int n) {
            this.n = n;
        }

        public void run() {
            try {
                if (n == 1) {
                    loadCenter();
                }
                if (n == 2) {
                    loadContract();
                }
                if (n == 3) {
                    loadEnterprise();
                }
                if (n == 4) {
                    loadModel();
                }
                if (n == 5) {
                    loadStaff();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    static class stockIns {
        int id;
        String center;
        String model;
        String supply_staff;
        int quantity;
        int purchase_price;

        public stockIns(int id, String center, String model, String supply_staff, int quantity,int purchase_price) {
            this.id = id;
            this.center = center;
            this.model = model;
            this.supply_staff = supply_staff;
            this.quantity = quantity;
            this.purchase_price=purchase_price;
        }
    }

    static class Order {
        int id;
        String contract;
        String enterprise;
        String model;
        String salesman;
        int quantity;
        String estimated, lodgement;

        public Order(int id, String contract, String enterprise, String model, String salesman, int quantity,
                     String estimated, String lodgement) {
            this.id = id;
            this.contract = contract;
            this.enterprise = enterprise;
            this.model = model;
            this.salesman = salesman;
            this.quantity = quantity;
            this.estimated = estimated;
            this.lodgement = lodgement;
        }
    }

    static class Center {
        int id;
        String name;

        public Center(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    static class Enterprise {
        int id;
        String name;
        String country;
        String city;
        String supply_center;
        String industry;

        public Enterprise(int id, String name, String country, String city, String supply_center, String industry) {
            this.id = id;
            this.name = name;
            this.country = country;
            this.city = city;
            this.supply_center = supply_center;
            this.industry = industry;

        }
    }

    static class Model {
        int id;
        String number;
        String model;
        String name;
        int unit_price;

        public Model(int id, String number, String model, String name, int unit_price) {
            this.id = id;
            this.number = number;
            this.model = model;
            this.name = name;
            this.unit_price = unit_price;
        }

    }

    static class Staff {
        int id;
        String name;
        int age;
        String gender;
        String number;
        String supply_center;
        String mobile_number;
        String type;

        public Staff(int id, String name, int age, String gender, String number, String supply_center,
                     String mobile_number, String type) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.gender = gender;
            this.number = number;
            this.supply_center = supply_center;
            this.mobile_number = mobile_number;
            this.type = type;
        }
    }

    static class Contract {
        String name;

        public Contract(String name) {
            this.name = name;
        }
    }

    static class bill{
        int order_id;
        String model;
        int profit;
        public bill(int order_id,String model ,int profit){
            this.order_id=order_id;
            this.model=model;
            this.profit=profit;
        }
    }


    public static void closePreStmt()
            throws SQLException {
        stmtCenter.close();
        stmtEnterprise.close();
        stmtModel.close();
        stmtStaff.close();
        stmtContract.close();
    }

    public static void sleep() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public  void load_basic_data() throws SQLException {
        // Empty target table
        Statement stmt0;
        if (con5 != null) {
            stmt0 = con5.createStatement();
            stmt0.execute("truncate table center,enterprise,model,staff,contract,orders,inventory,bill");
            con5.commit();
            stmt0.close();
        }
        MyTread m1 = new MyTread(1);
        MyTread m2 = new MyTread(2);
        MyTread m3 = new MyTread(3);
        MyTread m4 = new MyTread(4);
        MyTread m5 = new MyTread(5);

        m1.start();
        m2.start();
        m3.start();
        m4.start();
        m5.start();
    }

    public  String WuSuoWei(String s) throws SQLException {
        String[] strings = s.split("\\$");
        for (int i = 0; i < strings.length; i++) {
            System.out.println(strings[i]);
        }
        if (strings[0].equals("insert")) {
            Connection con = dataSource.getConnection();
            Statement stmtIn = con.createStatement();
            if (strings[1].equals("center")) {
                stmtIn.execute("insert into " + strings[1] + " (id,name) " +
                        "values(" + strings[2] + ");");
            } else if (strings[1].equals("staff")) {
                stmtIn.execute("insert into " + strings[1] + " (id,name,age,gender,number,supply_center," +
                        "mobile_number,type) values(" + strings[2] + ");");
            } else if (strings[1].equals("enterprise")) {
                stmtIn.execute("insert into " + strings[1] + " (id,name,country,city,supply_center,industry) " +
                        "values(" + strings[2] + ");");
            } else if (strings[1].equals("model")) {
                stmtIn.execute("insert into " + strings[1] + " (id,number,model,name,unit_price) " +
                        "values(" + strings[2] + ");");
            } else {
                return "false";
            }
            stmtIn.close();
            con.close();
        } else if (strings[0].equals("select")) {
            Connection con = dataSource.getConnection();
            StringBuilder sb = new StringBuilder();
            String s1 = "";

            for (int i = 2; i < strings.length; i++) {
                s1 += strings[i];
                if (i != strings.length - 1) {
                    s1 += " and ";
                }
            }

            String sql2 = "select * from " + strings[1] + " where " + s1 + ";";
            Statement statement = con.createStatement();
            ResultSet resultSet;
            resultSet = statement.executeQuery(sql2);
            if (strings[1].equals("center")) {
                sb.append("[");
                int x = 1;
                while (resultSet.next()) {
                    if (x == 1) {
                        sb.append("{");
                    } else sb.append(",{");
                    x++;
                    sb.append(" \"id\" :");
                    String x1 = resultSet.getString("id");
                    sb.append(x1).append(" ,");
                    sb.append(" \"name\" :");
                    String x2 = resultSet.getString("name").replace("\"", "");
                    sb.append("\"").append(x2).append("\"").append("}");
                }
                sb.append("]");
            } else if (strings[1].equals("model")) {
                sb.append("[");
                int x = 1;
                while (resultSet.next()) {
                    if (x == 1) {
                        sb.append("{");
                    } else sb.append(",{");
                    x++;
                    String x1 = resultSet.getString("id");
                    String x2 = resultSet.getString("number");
                    String x3 = resultSet.getString("model");
                    String x4 = resultSet.getString("name");
                    String x5 = resultSet.getString("unit_price");
                    sb.append(" \"id\" :");
                    sb.append(x1).append(" ,");
                    sb.append(" \"number\" :");
                    sb.append("\"").append(x2).append("\"").append(",");
                    sb.append(" \"model\" :");
                    sb.append("\"").append(x3).append("\"").append(",");
                    sb.append(" \"name\" :");
                    sb.append("\"").append(x4).append("\"").append(",");
                    sb.append(" \"unit_price\" :");
                    sb.append("\"").append(x5).append("\"").append("}");
                }
                sb.append("]");
            } else if (strings[1].equals("enterprise")) {
                //id,name,country,city,supply_center,industry
                sb.append("[");
                int x = 1;
                while (resultSet.next()) {
                    if (x == 1) {
                        sb.append("{");
                    } else sb.append(",{");
                    x++;
                    String x1 = resultSet.getString("id");
                    String x2 = resultSet.getString("name");
                    String x3 = resultSet.getString("country");
                    String x4 = resultSet.getString("city");
                    String x5 = resultSet.getString("supply_center").replace("\"", "");
                    String x6 = resultSet.getString("industry");
                    sb.append(" \"id\" :");
                    sb.append(x1).append(" ,");
                    sb.append(" \"name\" :");
                    sb.append("\"").append(x2).append("\"").append(",");
                    sb.append(" \"country\" :");
                    sb.append("\"").append(x3).append("\"").append(",");
                    sb.append(" \"city\" :");
                    sb.append("\"").append(x4).append("\"").append(",");
                    sb.append(" \"supply_center\" :");
                    sb.append("\"").append(x5).append("\"").append(",");
                    sb.append(" \"industry\" :");
                    sb.append("\"").append(x6).append("\"").append("}");
                }
                sb.append("]");
            } else if (strings[1].equals("staff")) {
                //id,name,age,gender,number,supply_center,mobile_number,type
                sb.append("[");
                int x = 1;
                while (resultSet.next()) {
                    if (x == 1) {
                        sb.append("{");
                    } else sb.append(",{");
                    x++;
                    String x1 = resultSet.getString("id");
                    String x2 = resultSet.getString("name");
                    String x3 = resultSet.getString("age");
                    String x4 = resultSet.getString("gender");
                    String x5 = resultSet.getString("number");
                    String x6 = resultSet.getString("supply_center");
                    String x7 = resultSet.getString("mobile_number");
                    String x8 = resultSet.getString("type");

                    sb.append("{ \"id\" :");
                    sb.append(x1).append(" ,");
                    sb.append(" \"name\" :");
                    sb.append("\"").append(x2).append("\"").append(",");
                    sb.append(" \"age\" :");
                    sb.append("\"").append(x3).append("\"").append(",");
                    sb.append(" \"gender\" :");
                    sb.append("\"").append(x4).append("\"").append(",");
                    sb.append(" \"number\" :");
                    sb.append("\"").append(x5).append("\"").append(",");
                    sb.append(" \"supply_center\" :");
                    sb.append("\"").append(x6).append("\"").append(",");
                    sb.append(" \"mobile_number\" :");
                    sb.append("\"").append(x7).append("\"").append(",");
                    sb.append(" \"type\" :");
                    sb.append("\"").append(x8).append("\"").append("}");
                }
                sb.append("]");
            } else if (strings[1].equals("orders")) {

//orders(id,contract_num,enterprise,product_model,quantity,contract_manager,contract_date,
// estimated_delivery_date, lodgement_date,salesman_num,contract_type)
                sb.append("[");
                int x = 1;
                while (resultSet.next()) {
                    if (x == 1) {
                        sb.append("{");
                    } else sb.append(",{");
                    x++;
                    String x1 = resultSet.getString("id");
                    String x2 = resultSet.getString("contract_num");
                    String x3 = resultSet.getString("enterprise");
                    String x4 = resultSet.getString("product_model");
                    String x5 = resultSet.getString("quantity");
                    String x6 = resultSet.getString("contract_manager");
                    String x7 = resultSet.getString("contract_date");
                    String x8 = resultSet.getString("estimated_delivery_date");
                    String x9 = resultSet.getString("lodgement_date");
                    String x10 = resultSet.getString("salesman_num");
                    String x11 = resultSet.getString("contract_type");
                    sb.append(" \"id\" :");
                    sb.append(x1).append(" ,");
                    sb.append(" \"contract_num\" :");
                    sb.append("\"").append(x2).append("\"").append(",");
                    sb.append(" \"enterprise\" :");
                    sb.append("\"").append(x3).append("\"").append(",");
                    sb.append(" \"product_model\" :");
                    sb.append("\"").append(x4).append("\"").append(",");
                    sb.append(" \"quantity\" :");
                    sb.append("\"").append(x5).append("\"").append(",");
                    sb.append(" \"contract_manager\" :");
                    sb.append("\"").append(x6).append("\"").append(",");
                    sb.append(" \"contract_date\" :");
                    sb.append("\"").append(x7).append("\"").append(",");
                    sb.append(" \"estimated_delivery_date\" :");
                    sb.append("\"").append(x8).append("\"").append(",");
                    sb.append(" \"lodgement_date\" :");
                    sb.append("\"").append(x9).append("\"").append(",");
                    sb.append(" \"salesman_num\" :");
                    sb.append("\"").append(x10).append("\"").append(",");
                    sb.append(" \"contract_type\" :");
                    sb.append("\"").append(x11).append("\"").append("}");
                }
                sb.append("]");

            }
            else {
                return "false";
            }
            statement.close();
            con.close();
            return sb.toString();
        } else if (strings[0].equals("delete")) {
            Connection con = dataSource.getConnection();
            Statement stmtDe = con.createStatement();
            String s1 ="";

            for (int i = 2; i < strings.length; i++) {
                s1 += strings[i];
                if (i != strings.length - 1) {
                    s1 += " and ";
                }
            }

            stmtDe.execute("delete from " + strings[1] + " where " + s1 + ";");
            stmtDe.close();
            con.close();
        } else if (strings[0].equals("update")) {
            Connection con = dataSource.getConnection();
            Statement stmtUp = con.createStatement();
            String s1 = "";

            for (int i = 3; i < strings.length; i++) {
                s1 += strings[i];
                if (i != strings.length - 1) {
                    s1 += " and ";
                }
            }
            stmtUp.execute("update " + strings[1] + " set " + strings[2] + " where " + s1 + ";");
            stmtUp.close();
            con.close();
        } else {
            return "false";
        }
        return "true";
    }

    public  void toStaff1() throws SQLException {
        closeDB();
        openDB("localhost","project2","user1","12345678");
    }


    public  void toBoss() throws SQLException {
        closeDB();
        openDB("localhost","project2","test","huang520");
    }


    public static void loadData() throws SQLException {
        boolean flag;
        synchronized (done) {
            if (done) {
                flag = false;
            } else {
                flag = true;
                done = true;
            }
        }
        if (flag) {
            sleep();
            // Empty target table
            Statement stmt0;
            if (con5 != null) {
                stmt0 = con5.createStatement();
                stmt0.execute("truncate table center,enterprise,model,staff,contract,orders,inventory,bill");
                con5.commit();
                stmt0.close();
            }
            MyTread m1 = new MyTread(1);
            MyTread m2 = new MyTread(2);
            MyTread m3 = new MyTread(3);
            MyTread m4 = new MyTread(4);
            MyTread m5 = new MyTread(5);

            m1.start();
            m2.start();
            m3.start();
            m4.start();
            m5.start();
            try {
                m1.join();
                m2.join();
                m3.join();
                m4.join();
                m5.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stock_in(fileName[5]);
            placeOrder(fileName[6]);
            updateOrder(fileName[7]);
            deleteOrder(fileName[8]);
            ready = true;
            System.out.println("done!!!!");
        }
        while (!ready) {
            sleep();
        }
//        System.out.println("baibaiabaiabai");

    }

    static {
        String[] args = {"-v",
                "center.csv",
                "enterprise.csv",
                "model.csv",
                "staff.csv",
                "in_stoke_test.csv",
                "task2_test_data_final_public.tsv",
                "update_final_test.tsv",
                "delete_final.tsv"};
        try {
            init(args);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void init(String[] args) throws SQLException {
        fileName = new String[args.length];
        boolean verbose = false;
        if (args.length != 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[0].equals("-v")) {
                    fileName[i] = args[i];
                } else {
                    System.err.println("Usage: java [-v] bestLoader filename");
                    System.exit(1);
                }
            }
        } else {
            System.err.println("Usage: java [-v] GoodLoader filename");
            System.exit(1);
        }
        Properties defprop = new Properties();
        defprop.put("host", "localhost");
        defprop.put("user", "test");
        defprop.put("password", "huang520");
        defprop.put("database", "project2");
        Properties prop = new Properties(defprop);
        for (int i = 1; i < args.length; i++) {
            if (i < 5 || i == 6) {
                String root = System.getProperty("user.dir");
                String FileName = fileName[i];
                try (BufferedReader infile
                             = new BufferedReader(new FileReader(FileName))) {

                    String line;
                    String[] parts;
                    line = infile.readLine();
//            String[] column_name=line.split(",");
                    while ((line = infile.readLine()) != null) {
                        if (i != 6) {
                            parts = line.split(",");
                        } else {
                            parts = line.split("\t");
                        }
                        if (parts.length > 1) {
                            switch (i) {
                                case 1:
                                    Center center;
                                    if (parts.length == 3) {
                                        center = new Center(Integer.parseInt(parts[0]), parts[1] + "," + parts[2]);
                                        centerMap.put(parts[1] + "," + parts[2], center);
                                    } else {
                                        center = new Center(Integer.parseInt(parts[0]), parts[1]);
                                        centerMap.put(parts[1], center);
                                    }

                                    break;
                                case 2:
                                    Enterprise enterprise;
                                    if (parts.length == 6) {
                                        enterprise = new Enterprise(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3],
                                                parts[4], parts[5]);

                                    } else {
                                        enterprise = new Enterprise(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3],
                                                parts[4] + "," + parts[5], parts[6]);
                                    }
                                    enterpriseMap.put(parts[1], enterprise);
                                    break;
                                case 3:
                                    Model model = new Model(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3],
                                            Integer.parseInt(parts[4]));
                                    modelMap.put(parts[2], model);
                                    break;
                                case 4:
                                    Staff staff = null;
                                    if (parts.length == 8) {
                                        staff = new Staff(Integer.parseInt(parts[0]), parts[1], Integer.parseInt(parts[2]), parts[3],
                                                parts[4], parts[5], parts[6], parts[7]);
                                    }
                                    if (parts.length == 9) {
                                        String temp = parts[5] + "," + parts[6];
                                        staff = new Staff(Integer.parseInt(parts[0]), parts[1], Integer.parseInt(parts[2]), parts[3],
                                                parts[4], temp, parts[7], parts[8]);
                                    }
                                    staffMap.put(parts[4], staff);
                                    break;
                                case 6:
                                    Contract contract = new Contract(parts[0]);
                                    contractMap.put(parts[0], contract);
                                    break;
                            }
                        }
                    }

                } catch (IOException e) {
                    System.err.println("Fatal error: " + e.getMessage());
                    try {
                        closePreStmt();
                    } catch (Exception e2) {
                    }
                    System.exit(1);
                }
            }
        }
        openDB(prop.getProperty("host"), prop.getProperty("database"),
                prop.getProperty("user"), prop.getProperty("password"));
    }

//    public static void main(String[] args) throws SQLException {
//        loadData();
//        loadBill();
//        System.out.println(66666);
//    }

}


