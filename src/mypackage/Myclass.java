package mypackage;

import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadT1Client;
import com.sun.javacard.apduio.CadTransportException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Myclass extends JFrame {
    private static final String BALANCE_FILE_PREFIX = "balance_";

    private static final byte CLA_MYAPPLET = (byte) 0x00;
    private static final byte INS_ADD_MONEY = 0x01;
    private static final byte INS_WITHDRAW_MONEY = 0x02;
    private static final byte INS_CHECK_BALANCE = 0x03;

    private static final int PIN_CARD_1 = 1234;
    private static final int PIN_CARD_2 = 1447;
    private static final int PIN_CARD_3 = 1111;

    private static final Map<Integer, MyCard> cards = new HashMap<>();
    private static MyCard currentCard;

    static {
        // Initialize cards with PINs
        cards.put(PIN_CARD_1, new MyCard(PIN_CARD_1));
        cards.put(PIN_CARD_2, new MyCard(PIN_CARD_2));
        cards.put(PIN_CARD_3, new MyCard(PIN_CARD_3));

    }
    
    
    private int currentCardPIN;

    
    static Apdu apdu;
    static CadT1Client cad;

    public Myclass() throws IOException, CadTransportException {
        // Connection to JavaCard
        Socket sckCarte;
        System.out.println("Current working directory: " + System.getProperty("user.dir"));

        try {
            sckCarte = new Socket("localhost", 9025);
            sckCarte.setTcpNoDelay(true);
            BufferedInputStream input = new BufferedInputStream(sckCarte.getInputStream());
            BufferedOutputStream output = new BufferedOutputStream(sckCarte.getOutputStream());
            cad = new CadT1Client(input, output);
        } catch (Exception e) {
            System.out.println("Error: Unable to connect to JavaCard");
            return;
        }

        try {
            cad.powerUp();
        } catch (Exception e2) {
            System.out.println("Error sending Powerup command to JavaCard");
            return;
        }

        // Applet selection
        apdu = new Apdu();
        apdu.command[Apdu.CLA] = (byte) 0x00;
        apdu.command[Apdu.INS] = (byte) 0xA4;
        apdu.command[Apdu.P1] = 0x04;
        apdu.command[Apdu.P2] = 0x00;
        byte[] appletAID = {0x4D, 0x79, 0x41, 0x70, 0x70, 0x6C, 0x65, 0x74, 0x41, 0x49, 0x44};
        apdu.setDataIn(appletAID);
        cad.exchangeApdu(apdu);

        if (apdu.getStatus() != 0x9000) {
            System.out.println("Error during applet selection");
            System.out.println(apdu.getStatus());
        } else {
            System.out.println("APDU selected :)");
            initializeCards();
            requestPIN();
        }
    }

    private void initializeCards() {
    	
        for (int pin : cards.keySet()) {
            loadBalance(pin);
        }
    }

    private void loadCard(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                System.out.println(line);
                if (parts.length == 2) {
                    int pin = Integer.parseInt(parts[0]);
                    short balance = Short.parseShort(parts[1]);
                    cards.put(pin, new MyCard(pin));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading card information: " + e);
        }
    }

    private void loadBalance(int pin) {
        String balanceFilename = BALANCE_FILE_PREFIX + pin + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(balanceFilename))) {
            String balanceStr = reader.readLine();
            if (balanceStr != null) {
                cards.get(pin).setBalance(Short.parseShort(balanceStr));
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading balance: " + e);
        }
    }

    private void requestPIN() {
        boolean validPIN = false;
        while (!validPIN) {
            String pinString = JOptionPane.showInputDialog("Enter PIN:");
            if (pinString == null) {
                System.exit(0); // User pressed Cancel or closed the dialog
            }
            try {
                int enteredPIN = Integer.parseInt(pinString);
                if (cards.containsKey(enteredPIN)) {
                    validPIN = true;
                    currentCard = cards.get(enteredPIN);
                    currentCardPIN = enteredPIN;
                    createUI(currentCard);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid PIN. Please try again.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a numeric PIN.");
            }
        }
    }

    private void createUI(final MyCard card) {
        setTitle("JavaCard Client Application - Card " + card.getPin());
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();

        JButton addMoneyButton = new JButton("Add Money");
        JButton withdrawMoneyButton = new JButton("Withdraw Money");
        JButton checkBalanceButton = new JButton("Check Balance");
        JButton quitButton = new JButton("Quit");

        panel.setLayout(new GridLayout(4, 1));
        panel.add(addMoneyButton);
        panel.add(withdrawMoneyButton);
        panel.add(checkBalanceButton);
        panel.add(quitButton);

        add(panel);

        setLocationRelativeTo(null);
        setVisible(true);

        addMoneyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("add money frame");
            }
        });

        withdrawMoneyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new WithdrawMoneyFrame(apdu, card);
            }
        });

        checkBalanceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CheckMoneyFrame(card);
            }
        });

        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveBalance(card);
                System.exit(0);
            }
        });
    }

    private void saveBalance(MyCard card) {
        try (PrintWriter writer = new PrintWriter(BALANCE_FILE_PREFIX + card.getPin() + ".txt")) {
            writer.println(card.getBalance());
        } catch (FileNotFoundException e) {
            System.out.println("Error saving balance: " + e);
        }
    }

    public static void main(String[] args) throws IOException, CadTransportException {
        new Myclass();
    }
}
