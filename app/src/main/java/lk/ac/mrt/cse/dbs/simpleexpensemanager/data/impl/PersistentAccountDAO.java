package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.SQ_Database;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {

    private SQ_Database database;

    public PersistentAccountDAO(SQ_Database database) {
        this.database = database;
    }

    @Override
    public List<String> getAccountNumbersList() {

        List<String> accountNumbersList = new ArrayList<>();  // create list for store account_numbers
        List<Account> accountList = getAccountsList();
        int len = accountList.size();
        int i = 0;
        while (i<len){
            Account account = accountList.get(i);
            String accountNumber = account.getAccountNo();
            accountNumbersList.add(accountNumber);
            i++;
        }
        return accountNumbersList;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accountList = new ArrayList<>();

        Cursor cursor = database.getWritableDatabase().query(SQ_Database.ACCOUNT_TABLE, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            while (true){

                Account account = new Account(cursor.getString(0), cursor.getString(1),cursor.getString(2), Double.parseDouble(cursor.getString(3)));
                accountList.add(account);
                if (!cursor.moveToNext()){
                    break;
                }
            }
        }
        cursor.close();
        return accountList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        // Read the database.
        Cursor cursor = database.getWritableDatabase().query(SQ_Database.ACCOUNT_TABLE, null, SQ_Database.ACC_NO + "=?", new String[] { accountNo }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            Account account = new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), Double.parseDouble(cursor.getString(3)));
            cursor.close();
            return account;
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQ_Database.ACC_NO, account.getAccountNo());
        values.put(SQ_Database.BANK_NAME, account.getBankName());
        values.put(SQ_Database.ACC_HOLDER_NAME, account.getAccountHolderName());
        values.put(SQ_Database.BALANCE, account.getBalance());


        sqLiteDatabase.insert(SQ_Database.ACCOUNT_TABLE, null, values);
        sqLiteDatabase.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();

        //Deleting
        sqLiteDatabase.delete(SQ_Database.ACCOUNT_TABLE, SQ_Database.ACC_NO+ " = ?", new String[] { accountNo });
        sqLiteDatabase.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Account account = this.getAccount(accountNo);
        SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();

        ContentValues values = new ContentValues();


        if(expenseType.equals(ExpenseType.EXPENSE)) //check the expense type and do correct operation
                values.put(SQ_Database.BALANCE, account.getBalance() - amount);
        else if(expenseType.equals(ExpenseType.INCOME)){
                values.put(SQ_Database.BALANCE, account.getBalance() + amount);
        }

        sqLiteDatabase.update(SQ_Database.ACCOUNT_TABLE, values, SQ_Database.ACC_NO + " = ?", new String[] { accountNo });
    }
}

