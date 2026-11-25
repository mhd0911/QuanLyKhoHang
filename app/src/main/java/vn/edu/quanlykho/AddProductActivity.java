        package vn.edu.quanlykho;

import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

public final class AddProductActivity extends WarehouseActivity {
    private EditText name;
    private EditText price;
    private EditText quantity;
    private Spinner category;
    private Switch active;
    private EditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        name = findViewById(R.id.product_name);
        price = findViewById(R.id.product_price);
        quantity = findViewById(R.id.product_quantity);
        category = findViewById(R.id.product_category);
        active = findViewById(R.id.product_active);
        description = findViewById(R.id.product_description);

        ArrayAdapter<CharSequence> categories = ArrayAdapter.createFromResource(this,
                R.array.product_categories, android.R.layout.simple_spinner_item);
        categories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categories);
        findViewById(R.id.back).setOnClickListener(view -> finish());
        findViewById(R.id.save_product).setOnClickListener(view -> saveProduct());
    }

    private void saveProduct() {
        String productName = name.getText().toString().trim();
        if (productName.isEmpty()) {
            showError(name, R.string.name_required);
            return;
        }
        Integer productPrice = readInteger(price, 1, R.string.price_invalid);
        if (productPrice == null) {
            return;
        }
        Integer productQuantity = readInteger(quantity, 0, R.string.quantity_invalid);
        if (productQuantity == null) {
            return;
        }

        Product product = new Product(0, productName, productPrice, productQuantity,
                category.getSelectedItem().toString(), active.isChecked(),
                description.getText().toString().trim());
        try {
            database.insertProduct(product);
        } catch (SQLiteException exception) {
            notifyUser(R.string.save_failed);
            return;
        }
        notifyUser(R.string.product_saved);
        setResult(RESULT_OK);
        finish();
    }

    private Integer readInteger(EditText field, int minimum, int errorMessage) {
        String input = field.getText().toString().trim();
        try {
            if (input.matches("[0-9]+")) {
                int number = Integer.parseInt(input);
                if (number >= minimum) {
                    return number;
                }
            }
        } catch (NumberFormatException ignored) {
            // The typed value exceeds the supported integer range.
        }
        showError(field, errorMessage);
        return null;
    }

    private void showError(EditText field, int message) {
        field.setError(getString(message));
        field.requestFocus();
    }
}
