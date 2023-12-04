package mypackage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CardBalanceManager {

    private static final String BALANCE_FILE_PREFIX = "balance_card_";
    
    private int cardNumber;

    public CardBalanceManager(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public short readBalance() {
        String filename = BALANCE_FILE_PREFIX + cardNumber + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            return Short.parseShort(line);
        } catch (IOException | NumberFormatException e) {
            // Handle exceptions (e.g., file not found, invalid content)
            return 0;
        }
    }

    public void writeBalance(short balance) {
        String filename = BALANCE_FILE_PREFIX + cardNumber + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(String.valueOf(balance));
        } catch (IOException e) {
            // Handle exceptions (e.g., unable to write to file)
        }
    }
}
