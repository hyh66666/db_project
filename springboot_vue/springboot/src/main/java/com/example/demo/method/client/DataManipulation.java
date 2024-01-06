package com.example.demo.method.client;

import java.sql.SQLException;

public interface DataManipulation {
    public String getAllStaffCount() throws SQLException;//6
    public String getContractCount() throws SQLException;//7
    public String getOrderCount() throws SQLException;//8
    public String getNeverSoldProduct() throws SQLException;//9
    public String getFavoriteProductModel() throws SQLException;//10
    public String getAvgStockByCenter() throws SQLException;//11
    public String getProductByNumber(String input) throws SQLException;//12
    public String getContractInfo(String input) throws SQLException;

    public String getContractInfoFirst(String input) throws SQLException;
    public String getContractInfoSecond(String input) throws SQLException;




    
}
