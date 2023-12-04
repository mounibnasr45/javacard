package jc_prg;

import javacard.framework.*;
import mypackage.AddMoneyFrame;
import com.sun.javacard.apduio.Apdu;

public class MyApplet extends Applet {
	
	 // Define the offsets for persistent data
    private static final short OFFSET_BALANCE = (short) 0;
    
    
    public static final byte CLA_MYAPPLET = (byte) 0x00; 
    private static final byte INS_ADD_MONEY = 0x01;
    private static final byte INS_WITHDRAW_MONEY = 0x02;
    private static final byte INS_CHECK_BALANCE = 0x03;


 // Persistent data array
    private byte[] persistentData;

    // Balance variable
    private short balance;

    protected MyApplet() {
        // Allocate persistent data array
        persistentData = new byte[2];

        // Load the balance from persistent memory
        balance = Util.getShort(persistentData, OFFSET_BALANCE);

        // If the balance is not initialized, set it to 0
        if (balance == JCSystem.NOT_A_TRANSIENT_OBJECT) {
            balance = 0;
            JCSystem.requestObjectDeletion();
        }
    }

    public static void install(byte[] bArray, short bOffset, byte bLength) 
    		throws ISOException {
        new MyApplet().register();
    }
    
    
    
    public void process(APDU apdu) throws ISOException {
        byte[] buffer = apdu.getBuffer();
        if (this.selectingApplet()) return;
        if (buffer[ISO7816.OFFSET_CLA] != CLA_MYAPPLET) {
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }
        switch (buffer[ISO7816.OFFSET_INS]) {
            case INS_ADD_MONEY:
                addMoney(apdu);
                break;
        case INS_WITHDRAW_MONEY:
        	System.out.println("Withdraw Money Frame opened !");
            withdrawMoney(buffer);
            break;
        case INS_CHECK_BALANCE:
            checkBalance(apdu);
            break;
        default:
            ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
    }
		
	}


    private void withdrawMoney(byte[] buffer) {


        short amount = Util.getShort(buffer, ISO7816.OFFSET_CDATA);
        if (amount > balance) {
            ISOException.throwIt(ISO7816.SW_DATA_INVALID);
        }

        balance -= amount;
    }

    private void addMoney(APDU apdu) {
		

    	byte[] buffer = apdu.getBuffer();

    	// Lc byte denotes the number of bytes in the
    	// data field of the command APDU
    	byte numBytes = buffer[ISO7816.OFFSET_LC];

    	// indicate that this APDU has incoming data
    	// and receive data starting from the offset
    	// ISO7816.OFFSET_CDATA following the 5 header
    	// bytes.
    	byte byteRead = (byte) (apdu.setIncomingAndReceive());

    	// it is an error if the number of data bytes
    	// read does not match the number in Lc byte
    	if ((numBytes != 1) || (byteRead != 1))
    		ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);

    	// get the credit amount
    	byte creditAmount = buffer[ISO7816.OFFSET_CDATA];
    	// check the new balance
    	

    	// credit the amount
    	balance = (short) (balance + creditAmount);
    	
    	// Update the persistent balance
        Util.setShort(persistentData, OFFSET_BALANCE, balance);
    		
    	}

    
    private void checkBalance(APDU apdu) {
    	byte[] buffer = apdu.getBuffer();
		
    	short le = apdu.setOutgoing();

    	if (le < 2)
    		ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);

    	apdu.setOutgoingLength((byte) 2);
    		
    	buffer[0] = (byte) (balance >> 8);
    	buffer[1] = (byte) (balance & 0xFF);
    		
    	Util.setShort(buffer, (short)0, balance);
    		
    	apdu.sendBytes((short) 0, (short) 2);
    		System.out.println(Util.getShort(buffer, ISO7816.OFFSET_CDATA));
    		System.out.println("ttttttttttttttest");
    	}

}
