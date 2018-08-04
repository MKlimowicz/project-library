package admin;

import books.Book;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class AdminUI {
    //1.DODAWANIE KSIAZEK
    //2.USUWANIE KSIAZEK
    //3.EDYTOWANIE KSIAZEK
    //4.DRUKOWANIE KSIAZEK
    //5.DODAWANIE KONTA USUWANIE KONTA
    private Scanner scanner = new Scanner(System.in);
    private boolean flagExit = true;
    private AdminUIRepository adminUIRepository = new AdminUIRepository();
    private Object reservationBooks;

    public void runAdmin() {

        System.out.println("Witaj na koncie administratora");

        while (flagExit) {

            System.out.println("1.Dodaj ksiazke.");
            System.out.println("2.Usuń ksiazke.");
            System.out.println("3.Edytuj ksiazke.");
            System.out.println("4.Wyswietl ksiazki.");
            System.out.println("5.Dodaj nowe konto Administratora.");
            System.out.println("6.Wydanie zarezerwowanej ksiązki.");
            System.out.println("7.Usunięcie rezerwacji.");
            System.out.println("8.Usunięcie zadluzenia.");
            System.out.println("9.Wyświetł wszystkie zarejestrowane osoby oraz ich książki.");
            System.out.println("10.Wyjscie z programu.");
            int select = scanner.nextInt();
            scanner.nextLine();


            switch (select) {
                case 1:
                    addBook();
                    break;
                case 2:
                    deleteBook();
                    break;
                case 3:
                    updateBook();
                    break;
                case 4:
                    printBooks();
                    break;
                case 5:
                    addNewAdminAccount();
                    break;
                case 6:
                    getReservationBooks();
                    break;
                case 7:
                    deleteReservation();
                    break;
                case 8:
                    deleteCashPenalty();
                    break;
                case 9:
                    printAllAccountWithBooks();
                    break;
                case 10:
                    prolongingTheBook();
                    break;
                case 11:
                    System.out.println("Wylogowales sie z konta administratora");
                    flagExit = false;
                    break;


            }

        }


    }

    private void prolongingTheBook() {


        System.out.println("Podaj id ksiażki która chcesz przedłużyć.Można przedłużyć maksymalnie na tydzień.");
        int number = scanner.nextInt();

        int volume = adminUIRepository.prolongingTheBook(number);

        if(volume  > 0){
            System.out.println("Udało Ci sie przedłużyć,przedłuzyć");
        }else{
            System.out.println("Coś poszło nie tak. Bądź juz przedłużałeś");
        }
    }

    private void printAllAccountWithBooks() {

         adminUIRepository.printAllAccountWithBooks();

    }

    private void deleteCashPenalty() {
        System.out.println("Jeśli znasz id konta na którym chcesz usunąć zadłuzenie wpisz 1.");
        System.out.println("Jeśli chcesz zobaczyc listę osób z zadłużeniem wpisz 2.");
        int x = scanner.nextInt();

        switch (x) {
            case 1:
                adminUIRepository.printPoepleWithLiabilities();
                deleteAccountWithLiabilities();
                break;
            case 2:
                deleteAccountWithLiabilities();
                break;
        }

    }

    private void deleteAccountWithLiabilities() {
        System.out.println("Podaj id osoby której chcesz usunać zadłużenie: ");
        int idAccount = scanner.nextInt();
        adminUIRepository.deleteLiabilities(idAccount);
    }


    private void addNewAdminAccount() {

        System.out.println("Podaj login:");
        String login = scanner.next();
        System.out.println("Podaj haslo:");
        String password = scanner.next();

        int result = adminUIRepository.addAccount(login, password);

        if (result > 0) {
            System.out.println("Dodales nowe konto.");
        } else {
            System.out.println("Nie udało Ci sie dodać konta.Podany login jest zajęty.");
        }

    }

    private void updateBook() {

        String name = "";
        String lastName = "";
        String title = "";
        List<Book> books = printBooks();
        System.out.println("Podaj id ksiazki ktore chcesz edytować: ");
        int y = scanner.nextInt();

        System.out.println("Co byś chciał edytować: ");
        System.out.println("1.Imie autora.");
        System.out.println("2.Nazwisko autora.");
        System.out.println("3.Tytul ksiazki.");
        int x = scanner.nextInt();
        scanner.nextLine();

        switch (x) {
            case 1:
                System.out.println("Podaj nowe imie autora: ");
                name = scanner.nextLine();
                books.get(y - 1).setFirstNameAuthor(name);


                break;
            case 2:

                System.out.println("Podaj nowe nazwisko autora: ");
                lastName = scanner.nextLine();
                books.get(y - 1).setLastNameAuthor(lastName);
                break;
            case 3:

                System.out.println("Podaj nowy tytul ksiazki: ");
                title = scanner.nextLine();
                books.get(y - 1).setTitle(title);
                break;
        }
        int i = adminUIRepository.updateBook(books.get(y - 1));

        if (i > 0) {
            switch (x) {
                case 1:
                    System.out.println("Zmieniles imie na: " + name);
                    break;

                case 2:
                    System.out.println("Zmieniles nazwisko na: " + lastName);
                    break;
                case 3:
                    System.out.println("Zmieniles tytul na: " + title);
                    break;
            }
        } else {
            System.out.println("Nie udało Ci z edytować.");
        }
    }


    private void deleteBook() {

        printBooks();

        System.out.println("Podaj id ksiązki którą chcesz usunać: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        int amountBooks = adminUIRepository.deleteBook(id);
        if (amountBooks > 0) {
            System.out.println("Usunales książke, o id: " + id);
        } else {
            System.out.println("Nie udało Ci sie usunać książki.");
        }


    }

    private List<Book> printBooks() {
        List<Book> books = adminUIRepository.printBooks();
        for (int i = 0; i < books.size(); i++) {
            System.out.println(i + 1 + "." + books.get(i));
        }
        return books;
    }

    private void addBook() {
        String choose = "";

        System.out.println("Podaj tytuł");
        String title = scanner.nextLine();
        System.out.println("Podaj imie autora");
        String name = scanner.nextLine();
        System.out.println("Podaj nazwisko autora");
        String lastName = scanner.nextLine();

        System.out.println("Wybierz z listy categorie: ");
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

        Book book = new Book(title, name, lastName);
        book.setCategory(choose);

        int amountBooks = adminUIRepository.addBook(book);

        if (amountBooks > 0) {
            System.out.println("Dodałeś książke. Tytuł:" + title + ". Imie autora: " + name + ". Nazwisko autora: " + lastName);
        } else {
            System.out.println("Nie udało Ci sie dodać książki.");
        }
    }


    //REZERWACJE KSIAZKI.

    public void getReservationBooks() {

        //wyswietl rezerwacje z imieniem , id osoby oraz nazwe ksiazki.

        List<Book> bookList = adminUIRepository.printBooksWithStatusReservation();
        if (bookList.isEmpty()) {
            System.out.println("Żadna ksiązka nie jest zarezerwowana.");
        } else {
            printListBooks(bookList);
        }

        System.out.println("Podaj id osoby która zarezerwowałą książke: ");
        int idaccount = scanner.nextInt();

        adminUIRepository.deleteResevation(idaccount);

    }

    private void deleteReservation() {


        List<Book> bookList = adminUIRepository.printBooksWithStatusReservation();

        printListBooks(bookList);

        System.out.println("Podaj id ksiązki ktora chcesz od rezerwować: ");
        int x = scanner.nextInt();


        int information = adminUIRepository.deleteReservationOnBook(x);

        if (information > 0) {
            System.out.println("Ksiazka zostala odrezerwowana.");
        } else {
            System.out.println("Coś poszło nie tak.");
        }

    }

    private void printListBooks(List<Book> bookList) {
        for (int i = 0; i < bookList.size(); i++) {

            System.out.println(bookList.get(i) + "status: " + bookList.get(i).getStatus_reservation());

        }
    }
}
