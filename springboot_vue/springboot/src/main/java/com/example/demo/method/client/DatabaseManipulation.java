package com.example.demo.method.client;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.sql.*;

public class DatabaseManipulation implements DataManipulation {
    private ResultSet resultSet;
    private int orderId=50001;
    private static final ComboPooledDataSource dataSource1 = new ComboPooledDataSource();



    private static void getConnection() {
        String host = "localhost";
        String dbname = "project2";
        String user = "test";
        String pwd = "huang520";
        String port = "5432";
        try {
            dataSource1.setDriverClass("org.postgresql.Driver");
        } catch (Exception e) {
            System.err.println("Cannot find the PostgreSQL driver. Check CLASSPATH.");
            System.exit(1);
        }
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbname;
        dataSource1.setUser(user);
        dataSource1.setPassword(pwd);
        dataSource1.setJdbcUrl(url);

        dataSource1.setInitialPoolSize(8);
        dataSource1.setMaxPoolSize(12);
    }

    static {
        getConnection();
    }


@Override
    public String getAllStaffCount() throws SQLException {
    Connection con=dataSource1.getConnection();

    StringBuilder sb = new StringBuilder();
    sb.append("[");
    String sql = "select type,count(type) from staff group by type;";
    try {
        Statement statement = con.createStatement();
        resultSet = statement.executeQuery(sql);
        int x=1;
        while (resultSet.next()) {
            if(x==1){
            sb.append("{");}
            else sb.append(",{");
            x++;
            String x1=resultSet.getString("type");
            String x2=resultSet.getString("count");
            sb.append("\"type\" :");
           sb.append("\""+x1+"\"");
           sb.append(",");
           sb.append("\"count\" :");
           sb.append(x2+"}");
        }
        sb.append("]");
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        con.close();
    }
    return sb.toString();

    }//6
    @Override
    public String getContractCount() throws SQLException {
        Connection con=dataSource1.getConnection();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        String sql = "select count(*) from contract;";
        try {
            Statement statement = con.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                sb.append("{ \"count\" :");
               String x2=resultSet.getString("count");
               sb.append(x2+"}");
            }
            sb.append("]");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            con.close();
        }
        return sb.toString();
    }//7
    @Override

    public String getOrderCount() throws SQLException {
        Connection con=dataSource1.getConnection();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        String sql = "select count(*) from orders;";
        try {
            Statement statement = con.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                sb.append("{ \"count\" :");
                String x2=resultSet.getString("count");
                sb.append(x2+"}]");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            con.close();
        }
        return sb.toString();
    }//8
    @Override
    public String getNeverSoldProduct() throws SQLException {
        Connection con=dataSource1.getConnection();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        String sql = "select count(*) from\n" +
                "(select  distinct product_model from inventory where inventory.quantity!=0\n" +
                "except\n" +
                "select distinct product_model from orders ) as q9;";
        try {
            Statement statement = con.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                sb.append("{ \"count\" :");
                String x2=resultSet.getString("count");
                sb.append(x2+"}]");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            con.close();
        }
        return sb.toString();
    }//9
    @Override
    public String getFavoriteProductModel() throws SQLException {
        Connection con=dataSource1.getConnection();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        String sql = "select product_model,sum(quantity) quan from orders group by product_model order by quan desc limit 1;";
        try {
            Statement statement = con.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                sb.append("{ \"product_model\" :");
                String x1=resultSet.getString("product_model");
                String x2=resultSet.getString("quan");
                sb.append("\""+x1+"\"");
                sb.append(",");
                sb.append("\"quan\" : ");
                sb.append(x2+"}]");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            con.close();
        }
        return sb.toString();

    }//10
    @Override
    public String getAvgStockByCenter() throws SQLException {
        Connection con=dataSource1.getConnection();
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            String sql = "select\n" +
                    "      case supply_center\n" +
                    "          when '\"Hong Kong, Macao and Taiwan regions of China\"' then 'Hong Kong, Macao and Taiwan regions of China'\n" +
                    "          else  supply_center\n" +
                    "              end as supply_center\n" +
                    "          ,cast(sum(quantity)*1.0/count(supply_center)*1.0 as decimal(8,1)) from inventory group by supply_center order by supply_center;" ;
            try {
                Statement statement = con.createStatement();
                resultSet = statement.executeQuery(sql);
                int x=1;
                while (resultSet.next()) {
                    if(x==1){
                        sb.append("{");}
                    else sb.append(",{");
                    x++;
                    String x1=resultSet.getString("supply_center");
                    String x2=resultSet.getString("numeric");

                    sb.append("\"supply_center\" :");
                    sb.append("\""+x1+"\"");
                    sb.append(",");
                    sb.append("\"numeric\" :");
                    sb.append(x2+"}");
                }
                sb.append("]");
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                con.close();
            }
            return sb.toString();

        }//11
    @Override
    public String getProductByNumber(String input) throws SQLException {
        Connection con=dataSource1.getConnection();
        StringBuilder sb = new StringBuilder();
//        sb.append("Q12\n"+"supply_center                    product_model                    quantity "+"\n");
        sb.append("[");
        String sql = "select " +
                "      case supply_center\n" +
                "          when '\"Hong Kong, Macao and Taiwan regions of China\"' then 'Hong Kong, Macao and Taiwan regions of China'\n" +
                "          else  supply_center\n" +
                "              end as supply_center\n" +
                ",model,quantity from inventory join model m on inventory.product_model = m.model where m.number="+input;
        try {
            Statement statement = con.createStatement();
            resultSet = statement.executeQuery(sql);
            int x=1;
            while (resultSet.next()) {
                if(x==1){
                    sb.append("{");}
                else sb.append(",{");
                x++;
                String x1=resultSet.getString("supply_center");
                String x2=resultSet.getString("model");
                String x3=resultSet.getString("quantity");

                sb.append("\"supply_center\" :");
                sb.append("\""+x1+"\"");
                sb.append(",");
                sb.append("\"model\" :");
                sb.append("\""+x2+"\" ,");
                sb.append("\"quantity\" :");
                sb.append(x3+"}");

            }
            sb.append("]");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            con.close();
        }
        return sb.toString();

    }//12
    @Override
    public String getContractInfo(String input) throws SQLException {//biaozhu
        Connection con=dataSource1.getConnection();
        StringBuilder sb = new StringBuilder();
//        sb.append("Q13\n");
        String sql1 = "select contract_num,s.name,enterprise,e.supply_center from orders join enterprise e on e.name = orders.enterprise join staff s on s.number = orders.contract_manager where contract_num="
                +input+ " limit 1;";
        String sql2 ="select product_model,s.name,quantity,m.unit_price,estimated_delivery_date,lodgement_date from orders\n" +
                "join model m on m.model = orders.product_model\n" +
                "join staff s on s.number = orders.salesman_num\n" +
                "where contract_num="+input+";";
        try {
            Statement statement = con.createStatement();
            resultSet = statement.executeQuery(sql1);
            while (resultSet.next()) {
                String x1=resultSet.getString("contract_num");
                String x2=resultSet.getString("name");
                String x3=resultSet.getString("enterprise");
                String x4=resultSet.getString("supply_center");
                sb.append("contract_number:"+x1+"\n");
                sb.append("enterprise:"+x3+"\n");
                sb.append("manager:"+x2+"\n");
                sb.append("supply_center:"+x4+"\n");

                resultSet = statement.executeQuery(sql2);
                if(!resultSet.wasNull()){
                    sb.append("product_model                    salesman          quantity  unit_price  estimate_delivery_date      lodgement_date          "+"\n");
                    while(resultSet.next()){
                         x1=resultSet.getString("product_model");
                         x2=resultSet.getString("name");
                         x3=resultSet.getString("quantity");
                         x4=resultSet.getString("unit_price");
                         String x6=resultSet.getString("estimated_delivery_date");
                         String x7=resultSet.getString("lodgement_date");
                         sb.append(x1);
                        for(int x=0;x<33-x1.length();x++){sb.append(" ");}
                        sb.append(x2);
                        for(int x=0;x<18-x2.length();x++){sb.append(" ");}
                        sb.append(x3);
                        for(int x=0;x<10-x3.length();x++){sb.append(" ");}
                        sb.append(x4);
                        for(int x=0;x<12-x4.length();x++){sb.append(" ");}
                        sb.append(x6);
                        for(int x=0;x<28-x6.length();x++){sb.append(" ");}
                        sb.append(x7+"\n");

                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            con.close();
        }
        return sb.toString();
    }
    public String getContractInfoFirst(String input) throws SQLException {
        Connection con=dataSource1.getConnection();
        StringBuilder sb = new StringBuilder();
//        sb.append("Q13\n");
        sb.append("[");
        String sql1 = "select contract_num,s.name,enterprise,e.supply_center from orders join enterprise e on e.name = orders.enterprise join staff s on s.number = orders.contract_manager where contract_num="
                +input+ " limit 1;";
        try {
            Statement statement = con.createStatement();
            resultSet = statement.executeQuery(sql1);
            while (resultSet.next()) {
                sb.append("{");
                String x1=resultSet.getString("contract_num");
                String x2=resultSet.getString("name");
                String x3=resultSet.getString("enterprise");
                String x4=resultSet.getString("supply_center").replace("\"","");
                sb.append("\"contract_num\" :");
                sb.append("\""+x1+"\"");
                sb.append(",");
                sb.append("\"name\" :");
                sb.append("\""+x2+"\" ,");
                sb.append("\"enterprise\" :");
                sb.append("\""+x3+"\" ,");
                sb.append("\"supply_center\" :");
                sb.append("\""+x4+"\" }]");
                }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            con.close();
        }
        return sb.toString();
    }
    public String getContractInfoSecond(String input) throws SQLException {
        Connection con=dataSource1.getConnection();
        StringBuilder sb = new StringBuilder();
//        sb.append("Q13\n");

        String sql2 ="select product_model,s.name,quantity,m.unit_price,estimated_delivery_date,lodgement_date from orders\n" +
                "join model m on m.model = orders.product_model\n" +
                "join staff s on s.number = orders.salesman_num\n" +
                "where contract_num="+input+";";
        try {
            Statement statement = con.createStatement();
                resultSet = statement.executeQuery(sql2);
                if(!resultSet.wasNull()){
                    sb.append("[");

                    int x=1;
                    while (resultSet.next()) {
                        if(x==1){
                            sb.append("{");}
                        else sb.append(",{");
                        x++;
                        String  x1=resultSet.getString("product_model");
                        String  x2=resultSet.getString("name");
                        String  x3=resultSet.getString("quantity");
                        String   x4=resultSet.getString("unit_price");
                        String x6=resultSet.getString("estimated_delivery_date");
                        String x7=resultSet.getString("lodgement_date");

                        sb.append("\"product_model\" :");
                        sb.append("\""+x1+"\" ,");
                        sb.append("\"name\" :");
                        sb.append("\""+x2+"\" ,");
                        sb.append("\"quantity\" :");
                        sb.append(x3+",");
                        sb.append("\"unit_price\" :");
                        sb.append(x4+",");
                        sb.append("\"estimated_delivery_date\" :");
                        sb.append("\""+x6+"\" ,");
                        sb.append("\"lodgement_date\" :");
                        sb.append("\""+x7+"\" }");
                    }
                    sb.append("]");
                }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            con.close();
        }
        return sb.toString();
    }

}
