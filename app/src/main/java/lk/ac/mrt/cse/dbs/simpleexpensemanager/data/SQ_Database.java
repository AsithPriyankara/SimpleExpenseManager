package lk.ac.mrt.cse.dbs.simpleexpensemanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class SQ_Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "200484M";
    private static final int DATABASE_VERSION = 1;

    public static SQ_Database instance;
    public static final String ACC_NO = "accountNo";
    public static final String BANK_NAME = "bankName";
    public static final String ACC_HOLDER_NAME = "accountHolderName";
    public static final String BALANCE = "balance";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String DATE = "date";
    public static final String EXPENSE_TYPE = "expenseType";
    public static final String AMOUNT = "amount";



    public static final String ACCOUNT_TABLE = "accounts";          //create database to accounts and transactions
    public static final String TRANSACTION_TABLE = "transactions";


    private static final String ACCOUNT_TABLE_CREATE = "CREATE TABLE " + ACCOUNT_TABLE + "(" +
                                                       ACC_NO + " TEXT PRIMARY KEY," + BANK_NAME +
                                                       " TEXT," + ACC_HOLDER_NAME + " TEXT," + BALANCE +
                                                       " REAL" + ")";


    private static final String TRANSACTIONS_TABLE_CREATE = "CREATE TABLE " + TRANSACTION_TABLE +
                                                            "(" + TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                                                             DATE + " TEXT," + ACC_NO + " TEXT," + EXPENSE_TYPE +
                                                             " TEXT," + AMOUNT + " REAL," +
                                                             "FOREIGN KEY(" + ACC_NO + ") REFERENCES "+ ACCOUNT_TABLE +
                                                             "(" + ACC_NO + ") )";

    public SQ_Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQ_Database getInstance(Context context) {
        if (instance == null) {
            instance = new SQ_Database(context);
        }
        return instance;
    };

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ACCOUNT_TABLE_CREATE);
        sqLiteDatabase.execSQL(TRANSACTIONS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + ACCOUNT_TABLE + "'");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + TRANSACTION_TABLE + "'");
        onCreate(sqLiteDatabase);
    }


}
