package mypackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.sun.javacard.apduio.Apdu;

public class AddMoneyFrame extends JFrame {

    private static final byte INS_ADD_MONEY = 0x01;
    private final Apdu apdu;
    private final short[] balanceArray;

    public AddMoneyFrame(Apdu apdu, final short[] balanceArray) {
        this.apdu = apdu;
        this.balanceArray = balanceArray;

        setTitle("Add Money");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Add components to the frame
        JPanel panel = new JPanel();
        JLabel amountLabel = new JLabel("Enter amount to add:");
        final JTextField amountTextField = new JTextField(10);
        JButton addButton = new JButton("Add");

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // Parse the amount from the text field
                    short amount = Short.parseShort(amountTextField.getText());

                    // Construct the APDU command for adding money
                    byte[] commandApdu = {(byte) 0xB0, INS_ADD_MONEY, 0x00, 0x00, 0x01, (byte) amount};

                    // Update the balance using the array
                    balanceArray[0] += amount;
                    System.out.println("Money added successfully! New Balance: " + balanceArray[0]);

                    // Close the AddMoneyFrame
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(AddMoneyFrame.this, "Invalid amount. Please enter a valid number.");
                }
            }
        });

        panel.add(amountLabel);
        panel.add(amountTextField);
        panel.add(addButton);

        setLayout(new FlowLayout());
        add(panel);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
