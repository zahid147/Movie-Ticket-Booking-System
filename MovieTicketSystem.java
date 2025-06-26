import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

interface ICinemaOperations {
    void viewShowList();
    void bookSeats();
    void viewAvailableSeats();
}

abstract class CinemaBase {
    public abstract void addHall(Hall hall);
}

class StarCinema extends CinemaBase {
    private static ArrayList<Hall> hallList = new ArrayList<>();

    @Override
    public void addHall(Hall hall) {
        hallList.add(hall);
    }

    public static ArrayList<Hall> getHallList() {
        return hallList;
    }

    public static Hall getHallByNumber(int hallNumber) {
        for (Hall hall : hallList) {
            if (hall.getHallNo() == hallNumber)
                return hall;
        }
        return null;
    }
}

class Hall implements ICinemaOperations {
    private ArrayList<Show> showList;
    private ArrayList<int[][]> seatList;
    private int row, col, hallNo;
    public static int count = 0;

    public int getHallNo() {
        System.out.print("\033[H\033[2J");
        return hallNo;
    }

    public Hall(int row, int col, int hallNo) {
        this.row = row;
        this.col = col;
        this.hallNo = hallNo;
        showList = new ArrayList<>();
        seatList = new ArrayList<>();
        new StarCinema().addHall(this);
        count++;
    }

    public void entryShow(int showId, String movieName, String time) {
        showList.add(new Show(showId, movieName, time));
        seatList.add(new int[row][col]);
    }

    @Override
    public void viewShowList() {
        System.out.println("\nHall " + hallNo + " - Show List:");
        for (Show s : showList) {
            System.out.println("\tMovie: " + s.movieName + "\n\tShow ID: " + s.showId + ", Time: " + s.time);
        }
    }

    @Override
    public void bookSeats() {
        Scanner scan = MovieTicketSystem.scan;
        System.out.print("Enter show ID to book tickets: ");
        int showId = scan.nextInt();

        int showIndex = getShowIndex(showId);
        if (showIndex == -1) {
            System.out.println("\tInvalid show ID.");
            System.out.println("Please view all shows and try again!");
            return;
        }

        System.out.print("Enter number of tickets to book: ");
        int ticketCount = scan.nextInt();
        if (ticketCount <= 0) {
            System.out.println("\tInvalid ticket count. Must be positive.");
            return;
        }

        int[][] seats = seatList.get(showIndex);
        for (int t = 0; t < ticketCount; t++) {
            System.out.print("Enter row for ticket " + (t + 1) + ": ");
            int r = scan.nextInt()-1;
            System.out.print("Enter column for ticket " + (t + 1) + ": ");
            int c = scan.nextInt()-1;

            if (r < 0 || r >= row || c < 0 || c >= col) {
                System.out.println("\n\tInvalid seat position.\n5 rows and 10 columns available\n");
                t--;
                continue;
            }

            if (seats[r][c] == 1) {
                System.out.println("\tSeat (" + (r+1) + "," + (c+1) + ") already booked.");
                t--;
            } else {
                seats[r][c] = 1;
                System.out.println("\tSeat (" + (r+1) + "," + (c+1) + ") successfully booked.");
                saveBookingToFile(hallNo, showId, r, c); // File I/O
            }
        }
    }

    @Override
    public void viewAvailableSeats() {
        Scanner scan = MovieTicketSystem.scan;
        System.out.print("Enter show ID to view seats: ");
        int showId = scan.nextInt();

        int showIndex = getShowIndex(showId);
        if (showIndex == -1) {
            System.out.println("\tInvalid show ID.");
            System.out.println("Please view all shows and try again!");
            return;
        }

        int[][] seats = seatList.get(showIndex);
        System.out.println("Available seats for Show " + showId + ":");
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                System.out.print(seats[i][j] == 0 ? "O " : "X ");
            }
            System.out.println();
        }
    }

    private int getShowIndex(int showId) {
        for (int i = 0; i < showList.size(); i++) {
            if (showList.get(i).showId == showId)
                return i;
        }
        return -1;
    }

    // File I/O method to store bookings
    // No idea how this works!!!
    private void saveBookingToFile(int hallNo, int showId, int row, int col) {
        try (FileWriter fw = new FileWriter("bookings.txt", true);
            BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("Hall: " + hallNo + ", Show: " + showId + ", Seat: (" + row + "," + col + ")\n");
        } catch (IOException e) {
            System.out.println("Error saving booking to file.");
        }
    }
}

class Show {
    int showId;
    String movieName;
    String time;

    public Show(int showId, String movieName, String time) {
        this.showId = showId;
        this.movieName = movieName;
        this.time = time;
    }
}

public class MovieTicketSystem {
    public static Scanner scan = new Scanner(System.in);
    public static void main(String[] args) {

        Hall hallOne = new Hall(5, 10, 1);
        Hall hallTwo = new Hall(6, 8, 2);

        hallOne.entryShow(101, "Demon Slayer -Kimetsu no Yaiba- The Movie: Infinity Castle", "24/07/2025 3:00 PM");
        hallTwo.entryShow(102, "Lost in Starlight", "24/07/2025 7:00 PM");

        while (true) {
            System.out.println("\n\tMOVIE THEATER TICKET MANAGEMENT SYSTEM");
            System.out.println("1: View all shows");
            System.out.println("2: View available seats");
            System.out.println("3: Book tickets");
            System.out.println("4: Exit");

            System.out.print("Enter your choice: ");
            int choice = scan.nextInt();

            switch (choice) {
                case 1:
                    for (Hall h : StarCinema.getHallList()) {
                        h.viewShowList();
                    }
                    break;
                case 2:
                    System.out.print("Enter hall number: ");
                    int hallNum2 = scan.nextInt();
                    Hall h2 = StarCinema.getHallByNumber(hallNum2);
                    if (h2 == null) {
                        System.out.println("\tInvalid hall number.");
                        System.out.println(Hall.count + " halls available!");
                    } else {
                        h2.viewAvailableSeats();
                    }
                    break;
                case 3:
                    System.out.print("Enter hall number: ");
                    int hallNum3 = scan.nextInt();
                    Hall h3 = StarCinema.getHallByNumber(hallNum3);
                    if (h3 == null) {
                        System.out.println("\tInvalid hall number.");
                        System.out.println(Hall.count + " halls available!");
                    } else {
                        h3.bookSeats();
                    }
                    break;
                case 4:
                    System.out.println("Exiting. Have a nice day!");
                    return;
                default:
                    System.out.println("\tInvalid option.");
                    break;
            }
            System.out.print("\n\nPress Enter!\n");
            scan.nextLine();
            scan.nextLine();
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }
}
