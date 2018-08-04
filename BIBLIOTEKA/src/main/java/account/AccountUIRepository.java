package account;

import books.Book;
import clientlogin.Dbutil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class AccountUIRepository {
    List<Book> bookList = new ArrayList<>();


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // wyswietla wszystkie skiazki jakie istniejo
    public void print() {

        final String sql = "SELECT * FROM books";

        try (
                Connection conn = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ) {
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {

                String title = rs.getString("title");
                String author_name = rs.getString("author_name");
                String author_lastname = rs.getString("author_lastname");

                bookList.add(new Book(title, author_name, author_lastname));

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < bookList.size(); i++) {
            System.out.println((i + 1) + "<-." + bookList.get(i));

        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //POCZATEK -> WSZYSTKIE METODY POTRZEDNE DO WYPOŻYCZENIA KSIĄŻKI

    // wypoŻyczenie ksiazki.
    public int borrow(int idBooks, String nameAccount) {
        int idAccount = getIDAccount(nameAccount); // sprawdza id konta na ktorym wykonujemy czyność wypożyczenia.
        int result = 0;

        String sql = "INSERT INTO status (idaccount,idbooks,status)VALUES(?,?,?)"; // dodaje ksiazke jako wypozyczona do statusu


        boolean existBook = checkIsThisBookExist(idBooks); // sprawdza cy ksiazka istnieje
        boolean check = checkIfThisBookIsNotBorrow(idBooks); // sprawdza czy ksiazka nie jest juz wypozyczona
        boolean reservedBook = checkIfTheBookIsNotReserved(idBooks); // sprawdza czy ksiazka nie jest zarezerwowana


        if (!check && existBook && !reservedBook) {

            try (
                    Connection conn = Dbutil.getInstance().getConnection();
                    PreparedStatement preparedStatement = conn.prepareStatement(sql)
            ) {

                preparedStatement.setInt(1, idAccount);
                preparedStatement.setInt(2, idBooks);
                preparedStatement.setInt(3, 1);

                result = preparedStatement.executeUpdate();


            } catch (SQLException e) {
                e.printStackTrace();
            }

            checkStatusInTableBook(idBooks); // ustawia w tabeli BOOKS ze kiazka jest wypozyczona.
            addTimeStatus(idBooks, idAccount); // dodaje czas od kiedy do kiedy mozna mieć ksiażke
            return result;

        } else {
            return 0;
        }
    }

    //SPRAWDZA CZY KSIAZKA ISTNIEJE
    private boolean checkIsThisBookExist(int idBooks) {

        String sql = "SELECT * FROM books WHERE idbooks = ?";
        boolean first = false;
        try (
                Connection conn = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, idBooks);
            ResultSet rs = preparedStatement.executeQuery();
            first = rs.first();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return first;
    }

    //DODAJE CZAS DO TABELI BORROWDATE -> SŁUŻY JAKO INFORMACJA KIEDY TRZEBA ODDAĆ KSIĄŻKE
    public void addTimeStatus(int idBooks, int idAccount) {

        String typeAccount = typeAccount(idAccount);
        Date dateBackBook;
        LocalDate localDate = LocalDate.now();

        if (typeAccount.equals("Student")) {
            dateBackBook = Date.valueOf(localDate.plusDays(28));
        } else {
            dateBackBook = Date.valueOf(localDate.plusDays(14));
        }

        Date dateGetBook = Date.valueOf(localDate);

        String sql = "INSERT INTO borrowdate (idbooks,idaccount,start_date,end_date)VALUES(?,?,?,?)";

        try (
                Connection conn = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, idBooks);
            preparedStatement.setInt(2, idAccount);
            preparedStatement.setDate(3, dateGetBook);
            preparedStatement.setDate(4, dateBackBook);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    // funkcja pomocnicza dla addTimeStatus sprawdza typ danego konta zeby moc okreslic na jak dlugo moze wypozyczyc ksiazke.
    private String typeAccount(int idAccount) {

        String sql = "SELECT * FROM account WHERE idaccount = ? ";
        String type = "";
        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setInt(1, idAccount);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {

                type = rs.getString("type");


            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return type;

    }

    // ZMIENIA STATUS W TABELI BOOKS ZE KSIAZKA ZOSTALA WYPOZYCZONA
    private void checkStatusInTableBook(int idBooks) {

        String sql = "UPDATE books SET status = 1 WHERE idbooks = ?";
        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setInt(1, idBooks);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //SPRAWDZA CZY KSIAZKA NIE ZOSTALA WYPOZYCZONA
    //Musisz dopisac zapytanie czy ksiazka nie jest zarezerwowana -> WAŻNE !!!!!!!!
    private boolean checkIfThisBookIsNotBorrow(int idbooks) {

        String sql = "SELECT * FROM status WHERE idbooks = ? ";
        boolean first = true;
        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setInt(1, idbooks);
            ResultSet rs = preparedStatement.executeQuery();
            first = rs.first();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return first;
    }

    //ZWRACA ID OSOBY ZALOGOWANEJ NA KONCIE
    private int getIDAccount(String nameAccount) {
        final String sqlName = "SELECT * FROM account WHERE login = ?";
        int idaccount = 0;
        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlName);

        ) {
            preparedStatement.setString(1, nameAccount);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                idaccount = rs.getInt("idaccount");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idaccount;
    }

    //KONIEC
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //WYSWIETLA NIE WYPOZYCZONE KSIAZKI
    public List<Book> notBorrowBooksPrint() {


        final String sqlName = "SELECT * FROM books WHERE status = 0";
        return getBookList(sqlName);

    }

    // ZWRACA liste książek dla zapytania SQL bez dodatkowych parametrów
    private List<Book> getBookList(String sqlName) {
        List<Book> list = new ArrayList<>();

        list.clear();

        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlName);

        ) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                int id = resultSet.getInt("idbooks");
                String title = resultSet.getString("title");
                String name = resultSet.getString("author_name");
                String lastName = resultSet.getString("author_lastname");
                Book book = new Book(title, name, lastName);
                book.setIdbooks(id);
                list.add(book);


            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //WYSWIETLA WSZYSTKIE KSIAZKI KTORE NALEZA DO KONTA NA KTORYM JEST SIE ZALOGOWANYM
    public List<Book> printBooksOnlineAccount(String accountName) {

        String sql = "SELECT * FROM books AS b, status AS s, account AS a \n" +
                "WHERE b.idbooks = s.idbooks  AND a.idaccount = s.idaccount  AND a.login = ?";

        List<Book> list = getBookList(accountName, sql);
        return list;


    }

    //ZWROT KSIAZKI

    public int backBook(int select) {

        String sqlOne = "DELETE FROM status WHERE idbooks = ?";
        String sqlTwo = "UPDATE books SET status = 0 WHERE idbooks = ?";
        String sqlThree = "DELETE FROM borrowdate WHERE idbooks = ?";

        int i = 0;
        try (
                Connection conn = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatementOne = conn.prepareStatement(sqlOne);
                PreparedStatement preparedStatementTwo = conn.prepareStatement(sqlTwo);
                PreparedStatement preparedStatementThree = conn.prepareStatement(sqlThree)

        ) {

            preparedStatementOne.setInt(1, select);
            preparedStatementTwo.setInt(1, select);
            preparedStatementThree.setInt(1, select);

            i = preparedStatementOne.executeUpdate();
            preparedStatementTwo.executeUpdate();
            preparedStatementThree.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    public List<Time> infoHowMuchDaysWas(String nameAccount) {

        int idAccount = getIDAccount(nameAccount);
        List<Time> timeList = new ArrayList<>();

        timeList.clear();

        String sql1 = "SELECT * FROM borrowdate As b , books As s WHERE b.idbooks = s.idbooks AND b.idaccount = ? ";

        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql1);
        ) {

            preparedStatement.setInt(1, idAccount);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                String title = resultSet.getString("s.title");
                Date end_date = resultSet.getDate("b.end_date");

                Time time = new Time(title, end_date);
                timeList.add(time);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return timeList;
    }

    public List<Book> printBooksByCategory(String choose) {

        String sql = "SELECT * FROM books WHERE categoria = ?";

        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            getBookList(choose, sql);


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookList;


    }

    //ZWRACA LISTE KSIAZEK Z DODATKOWYM ZMIENNYM PARAMETREM W ZAPYTANIU SQL
    private List<Book> getBookList(String setName, String sql) {


        bookList.clear();


        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

        ) {

            preparedStatement.setString(1, setName);
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
            e.printStackTrace();
        }
        return bookList;
    }

    //REZERWACJA KSIĄŻKI
    ///////////////////////////////////////////////

    public List<Book> printBooksNotResever() {

        String sql = "SELECT * FROM books WHERE status_reservation = 0 ";
        return getBookList(sql);

    }


    public boolean reservationBook(int idBook, String name) {

        boolean information = checkIfTheBookIsNotReserved(idBook); //sprawdza czy ksiazka nie jest zarezerwowana
        boolean informationBorrow = checkIfThisBookIsNotBorrow(idBook);  // sprawdza czy ksiazka nie jest wypozyczona
        int idAccount = getIDAccount(name);
        boolean flag = true;

        Date date = Date.valueOf(LocalDate.now());
        Date endDate = Date.valueOf(LocalDate.now().plusDays(3));

        if (information && informationBorrow) {
            return false;
        } else {
            String sql = "INSERT INTO reservation (idaccount,idbooks,data,status,end_day)VALUES(?,?,?,?,?)";
            String sqlTwo = "UPDATE books SET status_reservation = 1 WHERE idbooks = ?";


            try (
                    Connection connection = Dbutil.getInstance().getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    PreparedStatement preparedStatement1 = connection.prepareStatement(sqlTwo);
            ) {

                preparedStatement.setInt(1, idAccount);
                preparedStatement.setInt(2, idBook);
                preparedStatement.setDate(3, date);
                preparedStatement.setInt(4, 1);
                preparedStatement.setDate(5, endDate);
                preparedStatement.executeUpdate();

                preparedStatement1.setInt(1, idBook);
                preparedStatement1.executeUpdate();


            } catch (SQLException e) {
                e.printStackTrace();
            }

            return flag;

        }
    }

//////////////////////////////////////////////////////////////////////////////
    //funkcja ktora musi sprawdzać czy książka została juz odebrana czy nie. Jeśli a minelo 3 dni ksiazka ma zostać odrezerwowana.

    //Usuwanie ksiazki jesli nie zostala odebrana.
    public int deleteBookIfNotReceived() {

        String sql = "SELECT * FROM reservation ";
        LocalDate date = LocalDate.now();
        int dayOfMonth = date.getDayOfMonth();

        int suma = 0;

        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {

            ResultSet resultSet = preparedStatement.executeQuery();


            while (resultSet.next()) {

                int idbooks = resultSet.getInt("idbooks");

                Date end_day = resultSet.getDate("end_day");
                LocalDate localDate = end_day.toLocalDate();
                int dayOfMonth1 = localDate.getDayOfMonth();

                suma = dayOfMonth1 - dayOfMonth;
                if (dayOfMonth > dayOfMonth1) {
                    deleteReceived(idbooks);
                }

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suma;
    }

    private void deleteReceived(int idbooks) {

        String sql = "DELETE FROM reservation WHERE idbooks = ?";
        String sqlTwo = "UPDATE books SET status_reservation = 0 WHERE idbooks = ?";

        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                PreparedStatement preparedStatementTwo = connection.prepareStatement(sqlTwo);
        ) {

            preparedStatement.setInt(1, idbooks);
            preparedStatementTwo.setInt(1, idbooks);

            preparedStatement.executeUpdate();
            preparedStatementTwo.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    ////////////////////////////////////////////////////////////////////////////////
    private boolean checkIfTheBookIsNotReserved(int idBook) {
        String sql = "SELECT * FROM reservation WHERE idbooks = ?";
        boolean first = true;
        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {

            preparedStatement.setInt(1, idBook);
            ResultSet resultSet = preparedStatement.executeQuery();

            first = resultSet.first();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return first;

    }

    public List<Book> printReservationBooksIDAccount(String name) {

        int idAccount = getIDAccount(name);
        bookList.clear();

        String sql = "SELECT b.title,b.author_name,b.author_lastname FROM books As b ,reservation As r WHERE r.idbooks = b.idbooks  AND r.idaccount = ?";

        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, idAccount);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {

                String title = rs.getString("title");
                String author_name = rs.getString("author_name");
                String author_lastname = rs.getString("author_lastname");

                bookList.add(new Book(title, author_name, author_lastname));

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookList;
    }

    /////////////////////////////////////////////////////////
    public void addCashPanalty(String name, int value) {
        int idAccount = getIDAccount(name);
        String sql = "INSERT INTO cash_penalty (idaccount,money)VALUES(?,?)";

        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, idAccount);
            preparedStatement.setInt(2, value);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    //sprawdza czy na koncie niema dlugow przed skasowaniem
    public boolean checkCashPenalty(String name) {
        int idAccount = getIDAccount(name);
        String sql = "SELECT * FROM cash_penalty WHERE idaccount = ?";
        boolean flag = false;
        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {

            preparedStatement.setInt(1, idAccount);
            ResultSet resultSet = preparedStatement.executeQuery();

            flag = resultSet.first();


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    // spradza czy konto ma oddane ksiazki przed usunieciem
    public boolean checkBorrowBook(String name) {
        int idAccount = getIDAccount(name);
        String sql = "SELECT * FROM status WHERE idaccount = ?";
        boolean flag = false;
        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setInt(1, idAccount);
            ResultSet resultSet = preparedStatement.executeQuery();

            flag = resultSet.first();


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;

    }

    //USUNIECIE KONTA
    public int deleteAccount(String name) {

        String sql = "DELETE FROM account WHERE login = ? ";
        int flag = 0;

        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, name);
            flag = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;


    }

    //Usuniecie rezerwacji po skasowaniu konta
    public void deleteReservation(String name) {
        int idAccount = getIDAccount(name);

        List<Book> bookList = printReservationBooksIDAccount(name);

        for (Book aBookList : bookList) {
            String sql = "UPDATE books SET status_reservation = 0 WHERE idbooks = ?";
            try (
                    Connection connection = Dbutil.getInstance().getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(sql)
            ) {
                preparedStatement.setInt(1, aBookList.getIdbooks());
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        String sql = "DELETE FROM reservation WHERE idaccount = ?";
        try (
                Connection connection = Dbutil.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, idAccount);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
/////////////////////////////////////////////////////////

    //Przedłużenie terminu zwrotu ksiązki.
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

    ///////////////////////////////////////////////
}
