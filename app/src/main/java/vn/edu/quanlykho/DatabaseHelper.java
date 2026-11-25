package vn.edu.quanlykho;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public final class DatabaseHelper extends SQLiteOpenHelper {
    private static final String[] PRODUCT_COLUMNS = {
            "id", "name", "price", "quantity", "category", "active", "description"
    };

    public DatabaseHelper(Context context) {
        super(context.getApplicationContext(), "warehouse.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL UNIQUE, password TEXT NOT NULL)");
        db.execSQL("CREATE TABLE products ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL CHECK(length(trim(name)) > 0), "
                + "price INTEGER NOT NULL CHECK(price BETWEEN 1 AND 2147483647), "
                + "quantity INTEGER NOT NULL CHECK(quantity BETWEEN 0 AND 2147483647), "
                + "category TEXT NOT NULL, active INTEGER NOT NULL CHECK(active IN (0, 1)), "
                + "description TEXT)");

        insertUser(db, "admin", "123456");
        insertProduct(db, new Product(0, "Chuột không dây", 150000, 20,
                "Điện tử", true, "Chuột kết nối không dây."));
        insertProduct(db, new Product(0, "Bàn phím cơ", 450000, 3,
                "Điện tử", true, "Bàn phím cơ switch xanh, có đèn nền."));
        insertProduct(db, new Product(0, "Sổ tay A5", 25000, 40,
                "Văn phòng phẩm", true, "Sổ ghi chép khổ A5."));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new SQLiteException("Chưa có quy trình nâng cấp cơ sở dữ liệu.");
    }

    public boolean checkLogin(String username, String password) {
        try (Cursor cursor = getReadableDatabase().query("users", new String[]{"id"},
                "username = ? AND password = ?", new String[]{username, password},
                null, null, null, "1")) {
            return cursor.moveToFirst();
        }
    }

    public long insertUser(String username, String password) {
        return insertUser(getWritableDatabase(), username, password);
    }

    private static long insertUser(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        return db.insertOrThrow("users", null, values);
    }

    public long insertProduct(Product product) {
        return insertProduct(getWritableDatabase(), product);
    }

    private static long insertProduct(SQLiteDatabase db, Product product) {
        ContentValues values = new ContentValues();
        values.put("name", product.name);
        values.put("price", product.price);
        values.put("quantity", product.quantity);
        values.put("category", product.category);
        values.put("active", product.active ? 1 : 0);
        values.put("description", product.description);
        return db.insertOrThrow("products", null, values);
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query("products", PRODUCT_COLUMNS,
                null, null, null, null, "id ASC")) {
            while (cursor.moveToNext()) {
                products.add(readProduct(cursor));
            }
        }
        return products;
    }

    public Product getProductById(int id) {
        try (Cursor cursor = getReadableDatabase().query("products", PRODUCT_COLUMNS,
                "id = ?", idArgs(id), null, null, null)) {
            return cursor.moveToFirst() ? readProduct(cursor) : null;
        }
    }

    private static Product readProduct(Cursor cursor) {
        return new Product(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                cursor.getInt(3), cursor.getString(4), cursor.getInt(5) == 1,
                cursor.getString(6));
    }

    public boolean updateQuantity(int id, int quantity) {
        ContentValues values = new ContentValues();
        values.put("quantity", quantity);
        return getWritableDatabase().update("products", values, "id = ?", idArgs(id)) == 1;
    }

    public boolean deleteProduct(int id) {
        return getWritableDatabase().delete("products", "id = ?", idArgs(id)) == 1;
    }

    public BigInteger getTotalValue() {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT COALESCE(SUM(price * quantity), 0) FROM products", null)) {
            cursor.moveToFirst();
            return BigInteger.valueOf(cursor.getLong(0));
        } catch (SQLiteException exception) {
            String message = exception.getMessage();
            if (message == null || !message.contains("integer overflow")) {
                throw exception;
            }
        }

        // Each row fits in a long, but the sum of many rows can exceed SQLite's integer limit.
        BigInteger total = BigInteger.ZERO;
        try (Cursor cursor = db.rawQuery("SELECT price * quantity FROM products", null)) {
            while (cursor.moveToNext()) {
                total = total.add(BigInteger.valueOf(cursor.getLong(0)));
            }
        }
        return total;
    }

    public int countLowStock() {
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM products WHERE quantity < ?",
                new String[]{String.valueOf(Product.LOW_STOCK_THRESHOLD)})) {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
    }

    private static String[] idArgs(int id) {
        return new String[]{String.valueOf(id)};
    }
}
