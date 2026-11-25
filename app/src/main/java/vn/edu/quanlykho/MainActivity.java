package vn.edu.quanlykho;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public final class MainActivity extends WarehouseActivity {
    private static final int REQUEST_ADD_PRODUCT = 1;
    private ListView list;
    private ProductAdapter adapter;
    private boolean scrollToNewProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = findViewById(R.id.products);
        adapter = new ProductAdapter(new ArrayList<>());
        list.setEmptyView(findViewById(R.id.empty_products));
        list.setAdapter(adapter);
        list.setOnItemClickListener((parent, view, position, rowId) -> {
            Product product = adapter.getItem(position);
            Intent intent = new Intent(this, ProductDetailActivity.class);
            intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.id);
            startActivity(intent);
        });
        findViewById(R.id.open_add).setOnClickListener(view ->
                startActivityForResult(new Intent(this, AddProductActivity.class),
                        REQUEST_ADD_PRODUCT));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_PRODUCT && resultCode == RESULT_OK) {
            scrollToNewProduct = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Product> products = database.getAllProducts();
        adapter.setNotifyOnChange(false);
        adapter.clear();
        adapter.addAll(products);
        adapter.notifyDataSetChanged();
        if (scrollToNewProduct && !products.isEmpty()) {
            list.setSelection(products.size() - 1);
        }
        scrollToNewProduct = false;
        ((TextView) findViewById(R.id.product_count)).setText(String.valueOf(products.size()));
        ((TextView) findViewById(R.id.total_value)).setText(money(database.getTotalValue()));
        ((TextView) findViewById(R.id.low_count))
                .setText(String.valueOf(database.countLowStock()));
    }

    private final class ProductAdapter extends ArrayAdapter<Product> {
        ProductAdapter(List<Product> products) {
            super(MainActivity.this, R.layout.row_product, products);
        }

        @Override
        public View getView(int position, View recycledView, ViewGroup parent) {
            View row = recycledView;
            if (row == null) {
                row = getLayoutInflater().inflate(R.layout.row_product, parent, false);
            }
            Product product = getItem(position);
            ((TextView) row.findViewById(R.id.row_name)).setText(product.name);
            ((TextView) row.findViewById(R.id.row_price)).setText(money(product.price));
            ((TextView) row.findViewById(R.id.row_category)).setText(product.category);
            TextView quantity = row.findViewById(R.id.row_quantity);
            quantity.setText(getString(R.string.quantity_short, product.quantity));
            quantity.setTextColor(getColor(product.isLowStock() ? R.color.danger : R.color.ink));
            row.findViewById(R.id.row_warning)
                    .setVisibility(product.isLowStock() ? View.VISIBLE : View.GONE);
            return row;
        }
    }
}
