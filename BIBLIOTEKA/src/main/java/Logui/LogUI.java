package Logui;

import account.AccountUI;
import admin.AdminUI;

import java.util.Scanner;

public class LogUI {


    private AdminUI adminUI = new AdminUI();
    private LogUIRepository log = new LogUIRepository();
    private boolean flagAc = true;
    private Scanner scanner = new Scanner(System.in);
    private String login;

    private AccountUI accountUI = new AccountUI();

    public void runLog() {
        while (flagAc) {

            System.out.println("Witaj w bibliotece !");
            System.out.println("Wpisz co byś chciał zrobic.");
            System.out.println("1.\tRejestracja.\n" +
                    "2.\tZalogować się.\n" +
                    "3.\tWyjść.\n");
            int select = scanner.nextInt();
            scanner.nextLine();

            switch (select) {

                case 1:
                    registerAccount();
                    break;
                case 2:
                    int nextOptions = loginAccount();
                    if (nextOptions == 0) {
                        break;
                    } else if (nextOptions == 1) {
                        adminUI.runAdmin();
                    } else {
                        accountUI.runAccount(login);
                    }
                    break;
                case 3:
                    flagAc = false;
                    break;


            }


        }
    }

    private int loginAccount() {
        System.out.println("Witam, podaj swoje dane do logowania.");
        System.out.println("Login: ");
        login = scanner.next();
        System.out.println("Password: ");
        String password = scanner.next();

        int result = log.login(login, password);

        int informationWhatThisIsAccount = 0;

        switch (result) {
            case 0:
                System.out.println("Podales bledne dane.");
                informationWhatThisIsAccount = 0;
            break;
            case 1:
                System.out.println("Udało Ci sie zalogować.");
                informationWhatThisIsAccount = 2;
            break;
            case 2:
                System.out.println("Udało Ci sie zalogować na koncie Administratora.");
                informationWhatThisIsAccount = 1;
            break;
        }
        return informationWhatThisIsAccount;
    }

    private void registerAccount() {

        String type = "";
        System.out.println("Witaj,utwórz swoje konto.");

        System.out.println("Podaj login do konta: ");
        String name = scanner.next();

        System.out.println("Podaj haslo do konta: ");
        String password = scanner.next();

        System.out.println("Jesteś: ");
        System.out.println("1.Studentem.");
        System.out.println("2.Osoba indywidualna.");
        int status = scanner.nextInt();

        if(status == 1){
            type = "Student";
        }else{
            type = "Normal";
        }

        log.register(name, password,type);

    }
}
