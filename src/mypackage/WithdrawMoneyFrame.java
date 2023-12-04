package mypackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.sun.javacard.apduio.Apdu;

public class WithdrawMoneyFrame extends JFrame {

    private static final byte INS_ADD_MONEY = 0x01;
    private final Apdu apdu;
    private final MyCard card;

    public WithdrawMoneyFrame(Apdu apdu, final MyCard card) {
        this.apdu = apdu;
        this.card = card;

        setTitle("Withdraw Money");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Add components to the frame
        JPanel panel = new JPanel();
        JLabel amountLabel = new JLabel("Enter amount to withdraw:");
        final JTextField amountTextField = new JTextField(10);
        JButton withdrawButton = new JButton("Withdraw");

        withdrawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // Parse the amount from the text field
                    short amount = Short.parseShort(amountTextField.getText());

                    // Construct the APDU command for withdrawing money
                    byte[] commandApdu = {(byte) 0xB0, INS_ADD_MONEY, 0x00, 0x00, 0x01, (byte) amount};

                    // Update the balance using the MyCard instance
                    card.setBalance((short) (card.getBalance() - amount));
                    System.out.println("Money withdrawn successfully! New Balance: " + card.getBalance());

                    // Close the WithdrawMoneyFrame
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(WithdrawMoneyFrame.this, "Invalid amount. Please enter a valid number.");
                }
            }
        });

        panel.add(amountLabel);
        panel.add(amountTextField);
        panel.add(withdrawButton);

        setLayout(new FlowLayout());
        add(panel);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
