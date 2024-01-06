//package com.example.demo.method.client;
//
//import java.sql.SQLException;
//
//public class Client {
//
//    public static void main(String[] args) {
//        try {
//            DataManipulation dm = new DataFactory().createDataManipulation("database");
////            System.out.println(args[0]);
//            StringBuilder re=new StringBuilder();
//           re.append( dm.getAllStaffCount());
//           re.append(dm.getContractCount());
//            re.append(dm.getOrderCount());
//            re.append(dm.getNeverSoldProduct());
//            re.append(dm.getFavoriteProductModel());
//            re.append(dm.getAvgStockByCenter());
////            re.append(dm.getProductByNumber("'A50L172'"));
////            re.append(dm.getContractInfo("'CSE0000106'"));
//           System.out.println(re.toString());
//        } catch (IllegalArgumentException e) {
//            System.err.println(e.getMessage());
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
//
