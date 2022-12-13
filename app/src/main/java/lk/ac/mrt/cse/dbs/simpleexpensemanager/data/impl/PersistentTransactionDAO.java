package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.SQ_Database;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {

    private SQ_Database database;

    public PersistentTransactionDAO(SQ_Database database) {
        this.database = database;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SQ_Database.ACC_NO, accountNo);
        values.put(SQ_Database.EXPENSE_TYPE, expenseType.name());
        values.put(SQ_Database.AMOUNT, amount);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            values.put(SQ_Database.DATE, new SimpleDateFormat("dd/MM/YYYY", Locale.getDefault()).format(date));
        }

        sqLiteDatabase.insert(SQ_Database.TRANSACTION_TABLE, null, values);

        sqLiteDatabase.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {

        List<Transaction> transactionList = new ArrayList<>(); // create list for store all transaction logs

        // Read the database
        Cursor cursor = database.getReadableDatabase().query(SQ_Database.TRANSACTION_TABLE, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            while (true){

                Date date = null;
                try {
                    date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(cursor.getString(1));

                    //Creating Transaction objects.
                    Transaction transaction = new Transaction(date, cursor.getString(2), ExpenseType.valueOf(cursor.getString(3)), Double.parseDouble(cursor.getString(4)));
                    transactionList.add(transaction);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (!cursor.moveToNext()){
                    break;
                }
            }
        }

        cursor.close();

        return transactionList;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        List<Transaction> transactionList = new ArrayList<>(); // create list for store all transaction logs

        // Read the database
        Cursor cursor = database.getReadableDatabase().query(SQ_Database.TRANSACTION_TABLE, null, null, null, null, null, null,null);

        if (cursor.moveToFirst()) {
            while (true){

                Date date = null;
                try {
                    date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(cursor.getString(1));


                    Transaction transaction = new Transaction(date, cursor.getString(2), ExpenseType.valueOf(cursor.getString(3)), Double.parseDouble(cursor.getString(4)));
                    transactionList.add(transaction);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (!cursor.moveToNext()){
                    break;
                }
            }
        }

        cursor.close();
        if(transactionList.size()<limit){return transactionList;}
        return transactionList.subList(transactionList.size() - limit, transactionList.size());
    }
}