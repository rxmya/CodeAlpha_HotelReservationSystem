import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Reservation {

    private final String guestName;
    private final Room room;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;

    public Reservation(String guestName, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        this.guestName = guestName;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public String getGuestName() {
        return guestName;
    }

    public Room getRoom() {
        return room;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return String.format("Booking for %s in Room %d from %s to %s",
                guestName, room.getRoomNumber(),
                checkInDate.format(formatter), checkOutDate.format(formatter));
    }
}
