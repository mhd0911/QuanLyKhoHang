# Quản lý kho hàng

Bài thực hành Android bằng Java và XML. Ứng dụng sử dụng `SQLiteOpenHelper` để lưu dữ liệu trong `warehouse.db` phiên bản 1, gồm hai bảng `users` và `products`.

## Chạy trên Android Studio

1. Giải nén gói, chọn **File → Open** rồi mở thư mục **QuanLyKhoHang** có tệp `settings.gradle`.
2. Đồng bộ Gradle. Project dùng Gradle Wrapper 8.9, Android Gradle Plugin 8.7.3 và Android SDK Platform 35, Build-Tools 35.0.0. Trong **Settings → Build, Execution, Deployment → Build Tools → Gradle**, có thể chọn JDK đi kèm Android Studio nếu đó là JDK 17 hoặc 21.
3. Chọn máy ảo hoặc điện thoại chạy Android 6.0 (API 23) trở lên, sau đó bấm **Run ▶**.
4. Đăng nhập bằng tài khoản `admin`, mật khẩu `123456`.

Để tạo APK debug trên Windows, mở Terminal tại thư mục project và chạy `.\gradlew.bat assembleDebug`. Tệp kết quả nằm tại `app/build/outputs/apk/debug/app-debug.apk`.

## Cấu trúc

| Thành phần | Chức năng |
| --- | --- |
| `LoginActivity` | Kiểm tra thông tin với bảng `users`, mở danh sách khi đăng nhập thành công. |
| `MainActivity` | Truy vấn danh sách và thống kê, đánh dấu tồn kho dưới 5, truyền ID qua Intent khi mở chi tiết. |
| `AddProductActivity` | Kiểm tra biểu mẫu, lưu sản phẩm và quay lại danh sách. |
| `ProductDetailActivity` | Truy vấn theo ID, nhập hoặc bán một đơn vị, xác nhận trước khi xóa. |
| `DatabaseHelper`, `Product` | Tạo bảng, chèn dữ liệu mẫu, thao tác SQLite và mô hình sản phẩm. |
| `app/src/main/res/layout` | Bốn màn hình XML và mẫu một dòng sản phẩm. |

## Kịch bản chạy thử

Thực hiện trên bản cài mới để có đúng số liệu ban đầu.

1. Bấm **Đăng nhập** khi bỏ trống, rồi thử `admin` với mật khẩu sai. Cả hai trường hợp đều ở lại màn hình đăng nhập và hiển thị lỗi.
2. Đăng nhập đúng. Kho có **3 sản phẩm**, tổng giá trị **5.350.000 ₫** và **1 sản phẩm sắp hết hàng**.
3. Mở **Bàn phím cơ**. Bấm **Nhập thêm 1** hai lần để tồn tăng từ 3 lên 5. Cảnh báo biến mất, giá trị tồn của bàn phím là **2.250.000 ₫**. Quay lại danh sách và kiểm tra tổng kho **6.250.000 ₫**, số sản phẩm sắp hết hàng **0**.
4. Thêm sản phẩm mới. Thử bỏ trống tên, nhập giá 0 hoặc bỏ trống số lượng để xem lỗi. Sau đó lưu **Tai nghe** với giá **250.000 ₫**, số lượng **0**, loại **Điện tử**. Sản phẩm xuất hiện trong danh sách, tổng giá trị không đổi và số sản phẩm sắp hết hàng tăng lên **1**.
5. Mở **Tai nghe** và bấm **Bán 1**. Ứng dụng báo **Hết hàng**, số lượng vẫn bằng 0. Bấm **Nhập thêm 1**, quay lại danh sách và kiểm tra tổng kho **6.500.000 ₫**.
6. Buộc dừng ứng dụng rồi mở lại và đăng nhập. Kiểm tra **Tai nghe** vẫn còn, tồn **1**. Xóa sản phẩm và xác nhận, sau đó mở lại ứng dụng lần nữa để kiểm tra sản phẩm đã biến mất.
7. Nếu xóa hết sản phẩm, danh sách hiển thị trạng thái kho trống và cả ba số thống kê về 0.
