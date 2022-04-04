package suncor.com.android.model.pap.transaction;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TransactionTest {

    /*Normal and Member Locking Scenarios*/
    @Test
    public void getTransactionStatus_no_redeem_returns_Normal() {
        Transaction transaction = new Transaction();

        transaction.setTestValues(
                100,
                false,
                0
        );

       assertEquals( transaction.getTransactionStatus("0","100"), Transaction.TransactionStatus.NORMAL );
    }

    @Test
    public void getTransactionStatus_redeem100_000_get60_000_returns_Partial_Redemption() {
        Transaction transaction = new Transaction();
        transaction.setTestValues(
                100,
                false,
                60_000
        );
       assertEquals( transaction.getTransactionStatus("100000","100"), Transaction.TransactionStatus.PARTIAL_REDEMPTION );
    }


    @Test
    public void getTransactionStatus_redeem100_000_get100_000_returns_Normal() {
        Transaction transaction = new Transaction();
        transaction.setTestValues(
                100,
                false,
                100_000
        );
       assertEquals( transaction.getTransactionStatus("100000","100"), Transaction.TransactionStatus.NORMAL );
    }

    @Test
    public void getTransactionStatus_redeem100_000_get_0_returns_No_Redemption() {
        Transaction transaction = new Transaction();
        transaction.setTestValues(
                100,
                false,
                0
        );
       assertEquals( transaction.getTransactionStatus("100000","100"), Transaction.TransactionStatus.NO_REDEMPTION );
    }

    @Test
    public void getTransactionStatus_CLPE_DOWN_returns_Normal() {
        Transaction transaction = new Transaction();

        transaction.setTestValues(
                0,
                true,
                0
        );

        assertEquals( transaction.getTransactionStatus("100000","100"), Transaction.TransactionStatus.NORMAL );

        transaction.setTestValues(
                100,
                true,
                60000
        );

        assertEquals( transaction.getTransactionStatus("100000","100"), Transaction.TransactionStatus.NORMAL );
    }

    /*Under Pump Test Cases*/
    @Test
    public void getTransactionStatus_UnderPump_fuel_100_pump_60_redeem_100_000_get_50_000_returns_Partial_Redemption() {
        Transaction transaction = new Transaction();
        transaction.setTestValues(
                60,
                false,
                50_000
        );
        assertEquals( transaction.getTransactionStatus("100000","100"), Transaction.TransactionStatus.PARTIAL_REDEMPTION );
    }

    @Test
    public void getTransactionStatus_UnderPump_fuel_100_pump_60_redeem_100_000_get_0_returns_No_Redemption() {
        Transaction transaction = new Transaction();
        transaction.setTestValues(
                60,
                false,
                0
        );
        assertEquals( transaction.getTransactionStatus("100000","100"), Transaction.TransactionStatus.NO_REDEMPTION );
    }

    @Test
    public void getTransactionStatus_UnderPump_fuel_100_pump_60_redeem_100_000_get_60_000_returns_Normal() {
        Transaction transaction = new Transaction();
        transaction.setTestValues(
                60,
                false,
                60_000
        );
        assertEquals( transaction.getTransactionStatus("100000","100"), Transaction.TransactionStatus.NORMAL );
    }

    @Test
    public void getTransactionStatus_UnderPump_fuel_100_pump_60_redeem_50_000_get_50_000_returns_Normal() {
        Transaction transaction = new Transaction();
        transaction.setTestValues(
                60,
                false,
                50_000
        );
        assertEquals( transaction.getTransactionStatus("50000","100"), Transaction.TransactionStatus.NORMAL );
    }

 @Test
    public void getTransactionStatus_UnderPump_fuel_120_pump_110_redeem_100_000_get_100_000_returns_Normal() {
        Transaction transaction = new Transaction();
        transaction.setTestValues(
                110,
                false,
                100_000
        );
        assertEquals( transaction.getTransactionStatus("100000","120"), Transaction.TransactionStatus.NORMAL );
    }


}