package vn.edu.quanlykho;

import android.app.AlertDialog;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public final class ProductDetailActivity extends WarehouseActivity {
    public static final String EXTRA_PRODUCT_ID = "vn.edu.quanlykho.PRODUCT_ID";
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);
        findViewById(R.id.back).setOnClickListener(view -> finish());
        findViewById(R.id.return_to_list).setOnClickListener(view -> finish());
        findViewById(R.id.stock_in).setOnClickListener(view -> changeQuantity(1));
        findViewById(R.id.sell_one).setOnClickListener(view -> changeQuantity(-1));
        findViewById(R.id.delete_product).setOnClickListener(view -> confirmDelete());
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProduct(database.getProductById(productId));
    }

    private void showProduct(Product product) {
        if (product == null) {
            notifyUser(R.string.product_missing);
            finish();
            return;
        }
        setText(R.id.detail_name, product.name);
        setText(R.id.detail_category, product.category);
        setText(R.id.detail_price, money(product.price));
        setText(R.id.detail_quantity, String.valueOf(product.quantity));
        ((TextView) findViewById(R.id.detail_quantity)).setTextColor(
                getColor(product.isLowStock() ? R.color.danger : R.color.ink));
        setText(R.id.detail_value, money(product.stockValue()));
        setText(R.id.detail_active, getString(product.active ? R.string.active : R.string.inactive));
        String description = product.description;
        setText(R.id.detail_description, description == null || description.isEmpty()
                ? getString(R.string.no_description) : description);
        findViewById(R.id.detail_warning)
                .setVisibility(product.isLowStock() ? View.VISIBLE : View.GONE);
    }

    private void setText(int id, String text) {
        ((TextView) findViewById(id)).setText(text);
    }

    private void changeQuantity(int delta) {
        Product product = database.getProductById(productId);
        if (product == null) {
            showProduct(null);
            return;
        }
        if (delta < 0 && product.quantity == 0) {
            notifyUser(R.string.out_of_stock);
            return;
        }
        if (delta > 0 && product.quantity == Integer.MAX_VALUE) {
            notifyUser(R.string.quantity_limit);
            return;
        }

        try {
            database.updateQuantity(productId, product.quantity + delta);
        } catch (SQLiteException exception) {
            notifyUser(R.string.update_failed);
            return;
        }
        showProduct(database.getProductById(productId));
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_product)
                .setMessage(R.string.delete_confirmation)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, button) -> deleteProduct())
                .show();
    }

    private void deleteProduct() {
        try {
            if (database.deleteProduct(productId)) {
                notifyUser(R.string.product_deleted);
            } else {
                notifyUser(R.string.product_missing);
            }
        } catch (SQLiteException exception) {
            notifyUser(R.string.delete_failed);
            return;
        }
        finish();
    }
}
