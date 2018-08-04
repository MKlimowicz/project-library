package admin;

import account.AccountUIRepository;
import books.Book;
import clientlogin.Dbutil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdminUIRepository {

    private List<Book> bookList = new ArrayList<>();

    public int addBook(Book book) {

        final String sqlQuery = "INSERT INTO books (title,author_name,author_lastname,categoria)VALUES(?,?,?,?)";
        int amountBooks = 0;
        try (

                Connection con = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = con.prepareStatement(sqlQuery)
        ) {

            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getFirstNameAuthor());
            preparedStatement.setString(3, book.getLastNameAuthor());
            preparedStatement.setString(4, book.getCategory());
            amountBooks = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return amountBooks;

    }

    public List<Book> printBooks() {

        bookList.clear();

        final String sqlQueary = "SELECT * FROM books";

        try (
                Connection con = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = con.prepareStatement(sqlQueary);

        ) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("idbooks");
                String title = resultSet.getString("title");
                String name = resultSet.getString("author_name");
                String lastName = resultSet.getString("author_lastname");
                Book book = new Book(title, name, lastName);
                book.setIdbooks(id);
                bookList.add(book);
            }


        } catch (SQLException e) {
            e.getSQLState();
        }

        return bookList;
    }

    public int deleteBook(int id) {
        String sqlQuery = "DELETE FROM books WHERE idbooks = ?";

        int updateBooks = 0;
        try (
                Connection conn = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)
        ) {

            preparedStatement.setInt(1, id);

            updateBooks = preparedStatement.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return updateBooks;
    }

    public int updateBook(Book book) {


        String sqlQuery = "UPDATE books SET title = ? , author_name = ? , author_lastname = ? WHERE idbooks = ?";
        int updateBooks = 0;
        try (
                Connection conn = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)
        ) {

            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getFirstNameAuthor());
            preparedStatement.setString(3, book.getLastNameAuthor());
            preparedStatement.setInt(4, book.getIdbooks());
            updateBooks = preparedStatement.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return updateBooks;

    }

    public int addAccount(String login, String password) {

        int result = 0;
        boolean information = checkThatLoginNotExistInNormalLogin(login);

        String sql = "INSERT INTO adminaccount (login,password)VALUES(?,?)";

        if (!information) {

            try (
                    Connection conn = Dbutil.getInstance().getConnection();
                    PreparedStatement preparedStatement = conn.prepareStatement(sql)
            ) {

                preparedStatement.setString(1, login);
                preparedStatement.setString(2, password);
                preparedStatement.executeUpdate();

                result = preparedStatement.executeUpdate();


            } catch (SQLException e) {
                e.printStackTrace();

            }
            return result;

        } else {
            return 0;
        }

    }

    private boolean checkThatLoginNotExistInNormalLogin(String login) {
        String sql = "SELECT * FROM account WHERE login = ?";
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

    public List<Book> printBooksWithStatusReservation() {

        bookList.clear();

        final String sqlQueary = "SELECT * FROM books";

        try (
                Connection con = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = con.prepareStatement(sqlQueary);

        ) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("idbooks");
                String title = resultSet.getString("title");
                String name = resultSet.getString("author_name");
                String lastName = resultSet.getString("author_lastname");
                int status = resultSet.getInt("status_reservation");
                Book book = new Book(title, name, lastName);
                book.setIdbooks(id);
                book.setStatus_reservation(status);
                bookList.add(book);
            }


        } catch (SQLException e) {
            e.getSQLState();
        }

        return bookList;

    }

    public int deleteReservationOnBook(int idbook) {

        String sql = "DELETE FROM reservation WHERE idbooks = ?";
        String sql1 = "UPDATE books SET status_reservation = 0 WHERE idbooks = ?";
        int update = 0;
        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                PreparedStatement preparedStatement1 = connection.prepareStatement(sql1)
        ) {

            preparedStatement.setInt(1, idbook);
            preparedStatement.executeUpdate();
            preparedStatement1.setInt(1, idbook);
            update = preparedStatement1.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return update;
    }

    public void deleteResevation(int idaccount) {
        // MUSISZ NAPRAWIĆ NIE DZIALA POPRAWINIE

        AccountUIRepository accountUIRepository = new AccountUIRepository(); // zeby nie nadpisywać zapytania. Na potrzebe dodania dlugosci wypozyczenia.
        //Musisz usunac ksiazke z rezerwacji
        //Zmienic status ksiazki ze nie jest zarezerwowana w books
        //Ustawic ze jest wypożyczona w status -> przypisac id ksiazki oraz id konta ktore wypozycza

        String sqlOne = "DELETE FROM reservation WHERE idaccount = ?";
        String sqlTwo = "UPDATE books SET status_reservation = 0 , status = 1 WHERE idbooks = ?";
        String sqlThree = "INSERT INTO status(idaccount,idbooks,status)VALUES(?,?,?)";

        int number = getNumberBookReservation(idaccount);  // dodatkowe zapytanie zeby wyciagnac id ksiazki ktora jest zarezerwowana.

        accountUIRepository.addTimeStatus(number, idaccount);

        try (
                Connection conn = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatementOne = conn.prepareStatement(sqlOne);
                PreparedStatement preparedStatementTwo = conn.prepareStatement(sqlTwo);
                PreparedStatement preparedStatementThree = conn.prepareStatement(sqlThree);
        ) {

            //1.
            preparedStatementOne.setInt(1, idaccount);
            preparedStatementOne.executeUpdate();
            //2.
            preparedStatementTwo.setInt(1, number);
            preparedStatementTwo.executeUpdate();
            //3.
            preparedStatementThree.setInt(1, idaccount);
            preparedStatementThree.setInt(2, number);
            preparedStatementThree.setInt(3, 1);
            preparedStatementThree.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    private int getNumberBookReservation(int idaccount) {

        String sql = "SELECT * FROM reservation WHERE idaccount = ?";

        int idbook = 0;
        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {

            preparedStatement.setInt(1, idaccount);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                idbook = resultSet.getInt("idbooks");

            }


        } catch (SQLException e) {

            e.printStackTrace();
        }

        return idbook;
    }

    public void printPoepleWithLiabilities() {
        String sql = "select a.login , c.money, c.idaccount  from account as a , cash_penalty as c WHERE a.idaccount = c.idaccount";

        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String login = resultSet.getString("a.login");
                int money = resultSet.getInt("c.money");
                int idaccount = resultSet.getInt("c.idaccount");

                System.out.println("id_konta: " + idaccount);
                System.out.println("Nazwa: " + login);
                System.out.println("Wysokość zadłużenia: " + money);
                System.out.println("---------------------------------");

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void deleteLiabilities(int idAccount) {
        String sql = "DELETE FROM cash_penalty WHERE idaccount = ? ";
        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {

            preparedStatement.setInt(1, idAccount);
            int i = preparedStatement.executeUpdate();

            if (i > 0) {
                System.out.println("Zadłużenie zostało usuniete.");
            } else {
                System.out.println("Coś poszło nie tak...");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void printAllAccountWithBooks() {


        String sql = "SELECT a.login,b.title,b.author_lastname,b.author_name " +
                "FROM status As s , books As b, account As a " +
                "WHERE b.idbooks = s.idbooks AND s.idaccount = a.idaccount";

        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                String login = resultSet.getString("a.login");
                String title = resultSet.getString("b.title");
                String name = resultSet.getString("b.author_name");
                String lastName = resultSet.getString("b.author_lastname");

                System.out.println("Nazwa konta:" + login);
                System.out.println("Tytuł książki:" + title +". Autor: "+name+". Nazwisko: " + lastName);


            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public int prolongingTheBook(int value) {

        int info = checkIfYouCanExtend(value);

        if (info == 0){

            LocalDate localDate = getEndDateBorrow(value);
            LocalDate localDateWith7Days = localDate.plusDays(7);
            Date date = Date.valueOf(localDateWith7Days);


            int information = changeEndDateBorrowBook(date,value);
            if(information > 0){
                return 1;
            }else{
                return 0;
            }
        }else{
            return 0;
        }

    }
    //Przedluza date oddania ksiazki o tydzien oraz zmienia number na 1.
    private int changeEndDateBorrowBook(Date date,int value) {
        String sql = "UPDATE borrowdate SET end_date = ? ,number = ? WHERE idbooks = ?";
        int infoUpdate = 0;
        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setDate(1,date);
            preparedStatement.setInt(2, 1);
            preparedStatement.setInt(3, value);

            infoUpdate = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  infoUpdate;

    }
    //Pobiera ostatni koncowa date oddania ksiazki
    private LocalDate getEndDateBorrow(int value) {
        String sql = "SELECT * FROM borrowdate WHERE idbooks = ?";
        LocalDate localDate = LocalDate.now();
        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, value);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Date date = resultSet.getDate("end_date");
                localDate = date.toLocalDate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return localDate;
    }

    //sprawdza czy ksiazka nie została juz raz przedłuzona przez tego Pana.
    public int checkIfYouCanExtend(int value) {
        String sql = "SELECT * FROM borrowdate WHERE idbooks = ?";
        int number = 1;
        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, value);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                number = resultSet.getInt("number");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return number;
    }
}
