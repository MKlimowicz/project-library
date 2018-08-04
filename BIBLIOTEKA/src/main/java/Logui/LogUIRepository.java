package Logui;

import clientlogin.Dbutil;

import java.sql.*;

public class LogUIRepository {
    private PreparedStatement preparedStatement = null;
    private ResultSet rs = null;
    private Connection conn = null;
    private Statement statement = null;
    private CallableStatement callableStatement = null;


    public void register(String login,String password,String type){

       boolean information =  checkThatThisLoginNoExistInAdminAccount(login);

       if(!information) {
           final String sqlQuery = "INSERT INTO account (login,password,type)VALUES(?,?,?)";

           try (
                   Connection conn = Dbutil.getInstance().getConnection();
                   PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)

           ) {
               preparedStatement.setString(1, login);
               preparedStatement.setString(2, password);
               preparedStatement.setString(3, type);
               preparedStatement.executeUpdate();


           } catch (SQLException e1) {

               System.out.println("Podany login juz istnieje.");
           }
       }else{
           System.out.println("Podany login juz istnieje");
       }

        System.out.println("Udało Ci sie utworzyć konto.");

    }

    private boolean checkThatThisLoginNoExistInAdminAccount(String login) {

        String sql = "SELECT * FROM adminaccount WHERE login = ?";
        boolean result = false;
        try (
                Connection conn = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sql)
        ) {

            preparedStatement.setString(1, login);

            ResultSet rs = preparedStatement.executeQuery();
            result = rs.first();


        } catch (SQLException e) {
            e.printStackTrace();

        }
        return result;

    }


    public int login(String login, String password) {

        boolean adminAccount = adminAccount(login, password);
        boolean account = normalAccount(login, password);

        if(adminAccount) {
            return 2;
        }else if(account) {
            return 1;
        }else{
            return 0;
        }
    }

    private boolean adminAccount(String login, String password) {
        boolean result = false;
        final String sqlQuery = "SELECT * FROM adminaccount WHERE login = ? AND password = ?";
        try(
                Connection conn = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
        ) {

            preparedStatement.setString(1,login);
            preparedStatement.setString(2,password);

            ResultSet resultSet = preparedStatement.executeQuery();

            result = resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  result;
    }



    private boolean normalAccount(String login, String password) {
        boolean result = false;
        final String sqlQuery = "SELECT * FROM account WHERE login = ? AND password = ?";
        try(
                Connection conn = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
        ) {

            preparedStatement.setString(1,login);
            preparedStatement.setString(2,password);

            ResultSet resultSet = preparedStatement.executeQuery();

            result = resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  result;
    }
}
