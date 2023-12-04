package mypackage;

import java.awt.FlowLayout;
import javax.swing.*;

public class CheckMoneyFrame extends JFrame {

    private final MyCard card;

    public CheckMoneyFrame(MyCard card) {
        this.card = card;
        setTitle("Check Balance");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Add components to the frame
        JPanel panel = new JPanel();
        JLabel balanceLabel = new JLabel("Your current balance is:");

        // Convert the short array to a readable String
        StringBuilder balanceStr = new StringBuilder();
        balanceStr.append(card.getBalance());

        JLabel balanceValueLabel = new JLabel(balanceStr.toString());

        panel.add(balanceLabel);
        panel.add(balanceValueLabel);

        setLayout(new FlowLayout());
        add(panel);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
