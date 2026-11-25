package vn.edu.quanlykho;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Locale;

abstract class WarehouseActivity extends Activity {
    protected DatabaseHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new DatabaseHelper(this);
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }

    protected String money(long value) {
        return money(BigInteger.valueOf(value));
    }

    protected String money(BigInteger value) {
        String amount = NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"))
                .format(value);
        return getString(R.string.money_format, amount);
    }

    protected void notifyUser(int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
