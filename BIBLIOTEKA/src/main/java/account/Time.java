package account;

import java.sql.Date;

public class Time {
    String nameBooks;
    Date bookReturnDay;

    public Time(String nameBooks, Date bookReturnDay) {
        this.nameBooks = nameBooks;
        this.bookReturnDay = bookReturnDay;
    }

    public String getNameBooks() {
        return nameBooks;
    }

    public void setNameBooks(String nameBooks) {
        this.nameBooks = nameBooks;
    }

    public Date getBookReturnDay() {
        return bookReturnDay;
    }

    public void setBookReturnDay(Date bookReturnDay) {
        this.bookReturnDay = bookReturnDay;
    }

    @Override
    public String toString() {
        return "Time{" +
                "nameBooks='" + nameBooks + '\'' +
                ", bookReturnDay=" + bookReturnDay +
                '}';
    }
}
