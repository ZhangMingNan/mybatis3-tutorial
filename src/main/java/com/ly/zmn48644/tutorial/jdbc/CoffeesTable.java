/*
 * Copyright (c) 1995, 2011, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ly.zmn48644.tutorial.jdbc;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CoffeesTable {

    private String dbName;
    private Connection con;
    private String dbms;


    public CoffeesTable(Connection connArg, String dbNameArg, String dbmsArg) {
        super();
        this.con = connArg;
        this.dbName = dbNameArg;
        this.dbms = dbmsArg;

    }

    public void createTable() throws SQLException {
        String createString =
                "create table COFFEES " + "(COF_NAME varchar(32) NOT NULL, " +
                        "SUP_ID int NOT NULL, " + "PRICE numeric(10,2) NOT NULL, " +
                        "SALES integer NOT NULL, " + "TOTAL integer NOT NULL, " +
                        "PRIMARY KEY (COF_NAME), " +
                        "FOREIGN KEY (SUP_ID) REFERENCES SUPPLIERS (SUP_ID))";
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(createString);
        } catch (SQLException e) {
            JDBCTutorialUtilities.printSQLException(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public void populateTable() throws SQLException {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate("insert into COFFEES " +
                    "values('Colombian', 00101, 7.99, 0, 0)");
            stmt.executeUpdate("insert into COFFEES " +
                    "values('French_Roast', 00049, 8.99, 0, 0)");
            stmt.executeUpdate("insert into COFFEES " +
                    "values('Espresso', 00150, 9.99, 0, 0)");
            stmt.executeUpdate("insert into COFFEES " +
                    "values('Colombian_Decaf', 00101, 8.99, 0, 0)");
            stmt.executeUpdate("insert into COFFEES " +
                    "values('French_Roast_Decaf', 00049, 9.99, 0, 0)");
        } catch (SQLException e) {
            JDBCTutorialUtilities.printSQLException(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }


    /**
     * 更新咖啡的周销售额
     *
     * @param salesForWeek
     * @throws SQLException
     */
    public void updateCoffeeSales(HashMap<String, Integer> salesForWeek) throws SQLException {

        PreparedStatement updateSales = null;
        PreparedStatement updateTotal = null;

        //更新语句
        String updateString =
                "update COFFEES " + "set SALES = ? where COF_NAME = ?";
        //更新语句
        String updateStatement =
                "update COFFEES " + "set TOTAL = TOTAL + ? where COF_NAME = ?";

        try {
            con.setAutoCommit(false);
            //更新销售额
            updateSales = con.prepareStatement(updateString);
            //更新数量
            updateTotal = con.prepareStatement(updateStatement);

            for (Map.Entry<String, Integer> e : salesForWeek.entrySet()) {
                //设置参数
                updateSales.setInt(1, e.getValue().intValue());
                updateSales.setString(2, e.getKey());
                //执行更新
                updateSales.executeUpdate();

                updateTotal.setInt(1, e.getValue().intValue());
                updateTotal.setString(2, e.getKey());
                updateTotal.executeUpdate();
                //提交事务
                con.commit();
            }
        } catch (SQLException e) {
            JDBCTutorialUtilities.printSQLException(e);
            if (con != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    con.rollback();
                } catch (SQLException excep) {
                    JDBCTutorialUtilities.printSQLException(excep);
                }
            }
        } finally {
            if (updateSales != null) {
                updateSales.close();
            }
            if (updateTotal != null) {
                updateTotal.close();
            }
            con.setAutoCommit(true);
        }
    }

    public void modifyPrices(float percentage) throws SQLException {
        Statement stmt = null;
        try {

            //TYPE_SCROLL_SENSITIVE 光标可以向前和向后滚动，结果集对创建结果集之后发生的其他数据库的更改敏感。
            //CONCUR_UPDATABLE 可更新结果集
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            //修改价格的时候,先用查询语句查询出所有的结果
            ResultSet uprs = stmt.executeQuery("SELECT * FROM COFFEES");

            while (uprs.next()) {
                //针对结果集进行更新
                float f = uprs.getFloat("PRICE");
                uprs.updateFloat("PRICE", f * percentage);
                uprs.updateRow();
            }

        } catch (SQLException e) {
            JDBCTutorialUtilities.printSQLException(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }


    public void modifyPricesByPercentage(String coffeeName, float priceModifier,
                                         float maximumPrice) throws SQLException {
        con.setAutoCommit(false);

        Statement getPrice = null;
        Statement updatePrice = null;
        ResultSet rs = null;
        String query =
                "SELECT COF_NAME, PRICE FROM COFFEES " + "WHERE COF_NAME = '" +
                        coffeeName + "'";

        try {
            Savepoint save1 = con.setSavepoint();
            getPrice =
                    con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            updatePrice = con.createStatement();

            if (!getPrice.execute(query)) {
                System.out.println("Could not find entry for coffee named " +
                        coffeeName);
            } else {
                rs = getPrice.getResultSet();
                rs.first();
                float oldPrice = rs.getFloat("PRICE");
                float newPrice = oldPrice + (oldPrice * priceModifier);
                System.out.println("Old price of " + coffeeName + " is " + oldPrice);
                System.out.println("New price of " + coffeeName + " is " + newPrice);
                System.out.println("Performing update...");
                updatePrice.executeUpdate("UPDATE COFFEES SET PRICE = " + newPrice +
                        " WHERE COF_NAME = '" + coffeeName + "'");
                System.out.println("\nCOFFEES table after update:");
                CoffeesTable.viewTable(con);
                if (newPrice > maximumPrice) {
                    System.out.println("\nThe new price, " + newPrice +
                            ", is greater than the maximum " + "price, " +
                            maximumPrice +
                            ". Rolling back the transaction...");
                    con.rollback(save1);
                    System.out.println("\nCOFFEES table after rollback:");
                    CoffeesTable.viewTable(con);
                }
                con.commit();
            }
        } catch (SQLException e) {
            JDBCTutorialUtilities.printSQLException(e);
        } finally {
            if (getPrice != null) {
                getPrice.close();
            }
            if (updatePrice != null) {
                updatePrice.close();
            }
            con.setAutoCommit(true);
        }
    }


    public void insertRow(String coffeeName, int supplierID, float price,
                          int sales, int total) throws SQLException {
        Statement stmt = null;
        try {
            stmt =
                    con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet uprs = stmt.executeQuery("SELECT * FROM COFFEES");

            //将光标移动到结果集中的特殊行，该行可用于将新行插入数据库。当前光标位置被记住。
            uprs.moveToInsertRow();

            //更新 row 中的每个字段的值
            uprs.updateString("COF_NAME", coffeeName);
            uprs.updateInt("SUP_ID", supplierID);
            uprs.updateFloat("PRICE", price);
            uprs.updateInt("SALES", sales);
            uprs.updateInt("TOTAL", total);

            //插入行
            uprs.insertRow();
            //将光标移动到第一行之前
            uprs.beforeFirst();

        } catch (SQLException e) {
            JDBCTutorialUtilities.printSQLException(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public void batchUpdate() throws SQLException {

        Statement stmt = null;
        try {

            this.con.setAutoCommit(false);
            stmt = this.con.createStatement();

            stmt.addBatch("INSERT INTO COFFEES " +
                    "VALUES('Amaretto', 49, 9.99, 0, 0)");
            stmt.addBatch("INSERT INTO COFFEES " +
                    "VALUES('Hazelnut', 49, 9.99, 0, 0)");
            stmt.addBatch("INSERT INTO COFFEES " +
                    "VALUES('Amaretto_decaf', 49, 10.99, 0, 0)");
            stmt.addBatch("INSERT INTO COFFEES " +
                    "VALUES('Hazelnut_decaf', 49, 10.99, 0, 0)");

            //执行批量更新接口
            int[] updateCounts = stmt.executeBatch();
            this.con.commit();

        } catch (BatchUpdateException b) {
            JDBCTutorialUtilities.printBatchUpdateException(b);
        } catch (SQLException ex) {
            JDBCTutorialUtilities.printSQLException(ex);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            this.con.setAutoCommit(true);
        }
    }

    public static void viewTable(Connection con) throws SQLException {
        Statement stmt = null;
        //定义查询语句
        String query = "select COF_NAME, SUP_ID, PRICE, SALES, TOTAL from COFFEES";
        try {

            //从连接中获取statement
            //普通的sql声明用于没有参数
            //Statement: Used to implement simple SQL statements with no parameters.
            //预编译的sql声明,用于带有参数.
            //PreparedStatement: (Extends Statement.) Used for precompiling SQL statements that might contain input parameters. See Using Prepared Statements for more information.
            //应用于存储过程的sql声明
            //CallableStatement: (Extends PreparedStatement.) Used to execute stored procedures that may contain both input and output parameters. See Stored Procedures for more information.
            stmt = con.createStatement();

            //执行sql,获取结果集
            ResultSet rs = stmt.executeQuery(query);
            //从 结果集 中获取数据.
            while (rs.next()) {
                String coffeeName = rs.getString("COF_NAME");
                int supplierID = rs.getInt("SUP_ID");
                float price = rs.getFloat("PRICE");
                int sales = rs.getInt("SALES");
                int total = rs.getInt("TOTAL");
                System.out.println(coffeeName + ", " + supplierID + ", " + price +
                        ", " + sales + ", " + total);
            }

        } catch (SQLException e) {
            JDBCTutorialUtilities.printSQLException(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public static void alternateViewTable(Connection con) throws SQLException {
        Statement stmt = null;
        String query = "select COF_NAME, SUP_ID, PRICE, SALES, TOTAL from COFFEES";
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String coffeeName = rs.getString(1);
                int supplierID = rs.getInt(2);
                float price = rs.getFloat(3);
                int sales = rs.getInt(4);
                int total = rs.getInt(5);
                System.out.println(coffeeName + ", " + supplierID + ", " + price +
                        ", " + sales + ", " + total);
            }
        } catch (SQLException e) {
            JDBCTutorialUtilities.printSQLException(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public Set<String> getKeys() throws SQLException {
        HashSet<String> keys = new HashSet<String>();
        Statement stmt = null;
        String query = "select COF_NAME from COFFEES";
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                keys.add(rs.getString(1));
            }
        } catch (SQLException e) {
            JDBCTutorialUtilities.printSQLException(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
        return keys;

    }


    public void dropTable() throws SQLException {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            if (this.dbms.equals("mysql")) {
                stmt.executeUpdate("DROP TABLE IF EXISTS COFFEES");
            } else if (this.dbms.equals("derby")) {
                stmt.executeUpdate("DROP TABLE COFFEES");
            }
        } catch (SQLException e) {
            JDBCTutorialUtilities.printSQLException(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public static void main(String[] args) throws IOException {

        //通过工具类读取配置文件,这里可以思考mybatis的演化过程
        JDBCTutorialUtilities myJDBCTutorialUtilities = new JDBCTutorialUtilities("properties/mysql-sample-properties.xml");
        Connection myConnection = null;
        try {
            //获取数据库连接
            myConnection = myJDBCTutorialUtilities.getConnection();


            //具体的业务逻辑封装类
            CoffeesTable myCoffeeTable =
                    new CoffeesTable(myConnection, myJDBCTutorialUtilities.dbName,
                            myJDBCTutorialUtilities.dbms);

            //通过查询方法获取
            System.out.println("\nCOFFEES 表内容如下:");
            CoffeesTable.viewTable(myConnection);

            System.out.println("\n提高 coffee 价格 25%");
            myCoffeeTable.modifyPrices(1.25f);

            System.out.println("\n插入一个新记录:");
            myCoffeeTable.insertRow("Kona", 150, 10.99f, 0, 0);
            CoffeesTable.viewTable(myConnection);

            System.out.println("\n更新每周销量:");
            HashMap<String, Integer> salesCoffeeWeek =
                    new HashMap<String, Integer>();
            salesCoffeeWeek.put("Colombian:哥伦比亚咖啡", 175);
            salesCoffeeWeek.put("French_Roast:法国烘焙", 150);
            salesCoffeeWeek.put("Espresso:浓缩咖啡", 60);
            salesCoffeeWeek.put("Colombian_Decaf:哥伦比亚无咖啡因", 155);
            salesCoffeeWeek.put("French_Roast_Decaf:法国烘焙无咖啡因", 90);
            myCoffeeTable.updateCoffeeSales(salesCoffeeWeek);
            CoffeesTable.viewTable(myConnection);

            System.out.println("\n修改价格根据百分率");
            //修改哥伦比亚咖啡价格
            myCoffeeTable.modifyPricesByPercentage("Colombian", 0.10f, 9.00f);


            System.out.println("\nCOFFEES 修改前表数据:");

            myCoffeeTable.viewTable(myConnection);

            System.out.println("\n执行批量更新");
            myCoffeeTable.batchUpdate();
            myCoffeeTable.viewTable(myConnection);


        } catch (SQLException e) {
            JDBCTutorialUtilities.printSQLException(e);
        } finally {
            JDBCTutorialUtilities.closeConnection(myConnection);
        }
    }
}
