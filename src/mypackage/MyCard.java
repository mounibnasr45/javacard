package mypackage;

public class MyCard {
    private int pin;
    private short balance;

    public MyCard(int pin) {
        this.pin = pin;
        this.balance = 0;
    }

    public short getBalance() {
        return balance;
    }

    public void setBalance(short balance) {
        this.balance = balance;
    }

    public int getPin() {
        return pin;
    }

    public short[] getBalanceArray() {
        return new short[]{balance};
    }
}
