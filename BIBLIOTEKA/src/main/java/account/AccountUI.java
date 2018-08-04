package account;

import books.Book;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AccountUI {
    private AccountUIRepository accountUIRepository;
    private String name;
    private Scanner scanner;

    public AccountUI() {
        this.scanner = new Scanner(System.in);
        this.accountUIRepository = new AccountUIRepository();

    }

    public void runAccount(String nameAccount) {
        boolean flagExit = true;
        name = nameAccount;
        //Wyswietlenie info czy ile zostalo Ci dni do addania ksiazki.
        //Jesli przkroczyles informuje Cie o naliczanej karze za kazdy dzien.

        int suma = accountUIRepository.deleteBookIfNotReceived();

        if (suma < 0) {
            System.out.println("Usunieto rezerwacje.");
        }else if(suma == 0){
            System.out.println("Dziś jest ostatni dzień do odebrania książki.");
        }
        else {
            System.out.println("Pozostało Ci do odebrania książki: " + suma + " dni.");
        }
        System.out.println(suma);

        infoAboutReturnBooks(nameAccount);

        while (flagExit) {
            System.out.println("Witaj na swoim koncie.");
            System.out.println("1.\tWyświetl wszystkie książki\n" +
                    "2.\tWyświetl dostępne (niewypożyczone) książki oraz nie zarezerwowane\n" +
                    "3.\tWyświetl książki wypożyczone przez Ciebie\n" +
                    "4.\tWyświetl ksiązki według kategori\n" +
                    "5.\tWypożycz książkę\n" +
                    "6.\tRezerwacja książki\n" +
                    "7.\tOddaj książkę\n" +
                    "8.\tMoje rezerwacje\n" +
                    "9.\tUsuń konto\n" +
                    "10.\tPrzedłużenie ksiażki.\n" +
                    "11.\tWyjdź z programu\n");
            int select = this.scanner.nextInt();
            this.scanner.nextLine();
            switch (select) {
                case 1:
                    printBooks();
                    break;
                case 2:
                    printBooksNotBorrow();
                    break;
                case 3:
                    printLoginAccountBooks(nameAccount);
                    break;
                case 4:
                    printBooksByCategory();
                    break;
                case 5:
                    borrowBooks(nameAccount);
                    break;
                case 6:
                    reservationBook();
                    break;
                case 7:
                    returnBook();
                    break;
                case 8:
                    myReservationBook();
                    break;
                case 9:
                    deleteAccount();
                    break;
                case 10:
                    prolongingTheBook();
                    break;
                case 11:
                    System.out.println("Zegnaj.");
                    flagExit = false;
                    break;
            }
        }

    }


    private void prolongingTheBook() {
        printLoginAccountBooks(name);
        System.out.println("Podaj id ksiażki która chcesz przedłużyć.Można przedłużyć maksymalnie na tydzień.");
        int number = scanner.nextInt();

        int volume = accountUIRepository.prolongingTheBook(number);

        if(volume  > 0){
            System.out.println("Udało Ci sie przedłużyć,przedłuzyć");
        }else{
            System.out.println("Coś poszło nie tak. Bądź juz przedłużałeś");
        }
    }

    private void deleteAccount() {

        boolean money = accountUIRepository.checkCashPenalty(name); // czy ma długi
        boolean borrow = accountUIRepository.checkBorrowBook(name); // czy ma wypożyczone ksiązki

        if(!money && !borrow){

            int wynik = accountUIRepository.deleteAccount(name); // usuniecie konta

            if(wynik >0){
                accountUIRepository.deleteReservation(name);
                System.out.println("Usunałeś konto.");
            }else{
                System.out.println("Coś poszło nie tak...");
            }
        }else{
            System.out.println("Nie udało Ci sie usunać konta. Sprawdz czy nie masz zadłużenia lub wypożyczonej ksiażki...");
        }


    }



    ////////////////////////////////////////////////////////////////////////


    private void infoAboutReturnBooks(String nameAccount) {

        List<Time> timeList = accountUIRepository.infoHowMuchDaysWas(nameAccount);

        int days = 0;

        if (timeList.isEmpty()) {
            System.out.println("Nie masz żadnej wypożyczonej książki.");
        } else {


            for (Time time : timeList) {
                LocalDate localDate = time.getBookReturnDay().toLocalDate();

                days = calculationDays(localDate);

                if (days < 0) {

                   int value = days * -1;
                    System.out.println("Została naliczona kara za książke: " + time.getNameBooks() + ". w wysokości: "
                            + value + "zł. Jęśli nie chcesz zeby kara wzrastała zwróć książkę.");

                             accountUIRepository.addCashPanalty(name,value); // Wpisuje do bazdy danych informacje o zadluzeniu

                } else {
                    System.out.println("Pozostało Ci: " + days + " dni. Do oddania książki zatytułowanej: " + time.getNameBooks());
                }

            }
        }


    }

    private int calculationDays(LocalDate bookReturnDay) {

        LocalDate localDate = LocalDate.now();

        List<Integer> listDayMonth = new ArrayList<>();
        listDayMonth.add(31);
        listDayMonth.add(28);
        listDayMonth.add(31);
        listDayMonth.add(30);
        listDayMonth.add(31);
        listDayMonth.add(30);
        listDayMonth.add(31);
        listDayMonth.add(31);
        listDayMonth.add(30);
        listDayMonth.add(31);
        listDayMonth.add(30);
        listDayMonth.add(31);

        int sumDaysOne = 0;
        int sumDaysTwo = 0;

        Month month = bookReturnDay.getMonth();
        for (int i = 0; i < month.getValue() - 1; i++) {
            int x = listDayMonth.get(i);
            sumDaysOne = sumDaysOne + x;
        }


        int monthValue = localDate.getMonthValue();

        for (int i = 0; i < monthValue - 1; i++) {
            int x = listDayMonth.get(i);
            sumDaysTwo = sumDaysTwo + x;
        }

        return (sumDaysOne + bookReturnDay.getDayOfMonth()) - (sumDaysTwo + localDate.getDayOfMonth());

    }

    ////////////////////////////////////////////////////////////////////////

    private void returnBook() {

        printLoginAccountBooks(name);
        System.out.println("Podaj książke którą chcesz zwrócić: ");
        int select = scanner.nextInt();

        int check = accountUIRepository.backBook(select);

        if (check > 0) {
            System.out.println("Oddales ksiazke.");
        } else {
            System.out.println("Cos poszlo nie tak. Sprobuj ponownie.");
        }


    }

    private void printLoginAccountBooks(String accountName) {
        List<Book> bookList = accountUIRepository.printBooksOnlineAccount(accountName);

        if (bookList.size() == 0) {
            System.out.println("Nie masz zadnych ksiazek.");
        } else {

            printBook(bookList);
        }


    }


    private void printBooksNotBorrow() {

        List<Book> bookList = accountUIRepository.notBorrowBooksPrint();

        printBook(bookList);

    }

    private void borrowBooks(String nameAccount) {

        System.out.println();
        printBooksNotBorrow();
        System.out.println();

        System.out.println("Podaj id ksiązki która chcesz wypożyczyć: ");
        int id = scanner.nextInt();


        int borrow = accountUIRepository.borrow(id, nameAccount);

        if (borrow > 0) {
            System.out.println("Udało Ci sie wypożyczyć książke");
        } else {
            System.out.println("Nie udało Cie sie wypożyczyć ksiązki, sprobuj ponownie. Ksiązka moze byc juz wypozyczona.(SPRAWDZ TO)");
        }


    }

    ///REZERWACJA KSIĄŻKI

    ////////////////////////////////////////////////////////////////////////////////////////////


    //sprawdzanie czy ksiazka nie jest oddana i ile dni zostało oraz zaraz po zalogowaniu informuje jesli ksiazke mozna odebrac.


    private void myReservationBook() {

        System.out.println("Twoje zarezerwowane książki: ");
        List<Book> bookList = accountUIRepository.printReservationBooksIDAccount(name);

        printBook(bookList);

    }


    //Koniec

    private void reservationBook() {

        printBooksNotReserver();

        System.out.println("Podaj id ksiązki która chcesz zarezerwować: ");
        int idBook = scanner.nextInt();

        boolean information = accountUIRepository.reservationBook(idBook, name);

        if (!information) {
            System.out.println("Nie udało sie zarezerwować. Sprawdź czy podaleś dobre id lub czy nie jest juz zarezerwowana.");
        } else {
            System.out.println("Udało Ci sie zarezerwować książke.");
        }


    }

    //WYSWIETLANIE KSIAZEK
    private void printBooksByCategory() {

        String choose = "";
        System.out.println("Podaj kategorie książek jakie chcesz zobaczyć: ");
        System.out.println("1.Historia.");
        System.out.println("2.Programowanie.");
        System.out.println("3.Sztuka.");
        System.out.println("4.Przygodowe.");
        int select = scanner.nextInt();


        switch (select) {
            case 1:
                choose = "historia";
                break;
            case 2:
                choose = "programowanie";
                break;
            case 3:
                choose = "sztuka";
                break;
            case 4:
                choose = "przygodowe";
                break;


        }

        List<Book> bookList = accountUIRepository.printBooksByCategory(choose);

        printBook(bookList);


    }

    private void printBooksNotReserver() {

        List<Book> bookList = accountUIRepository.printBooksNotResever();
        printBook(bookList);


    }

    private void printBooks() {
        this.accountUIRepository.print();
    }

    private void printBook(List<Book> bookList) {
        for (Book book : bookList) {
            System.out.println(book);
        }
    }
}
