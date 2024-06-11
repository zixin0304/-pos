package mypos_db;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import models.Product;
import models.ProductDAO;

public class AppProductMaintenanceTableViewDBV1 extends Application {

        //***********產生資料DAO來使用
    private final ProductDAO productDao = new ProductDAO();
    //ObservableList    order_list有新增或刪除都會處動table的更新，也就是發生任何改變時都被通知
    private final ObservableList<Product> product_list = FXCollections.observableList(productDao.getAllProducts());
    //private final ObservableList<Product> product_list = getProductList();
    private final Label statusLabel = new Label();



    @Override
    public void start(Stage primaryStage) {
        VBox root = getRootPane();
        Scene scene = new Scene(root, 950, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("POS系統");
        primaryStage.show();
    }

    public VBox getRootPane() {
        TableView<Product> table = initializeProductTable();

        VBox vbox = new VBox(table, statusLabel);
        vbox.getStylesheets().add("/css/bootstrap3.css");

        // 添加CSS樣式
        table.getStyleClass().add("table");

        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);

        return vbox;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private TableColumn<Product, String> createColumn(String title, String propertyName) {
        TableColumn<Product, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        return column;
    }

    private TableColumn<Product, Integer> createColumn(String title, String propertyName, IntegerStringConverter converter) {
        TableColumn<Product, Integer> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(TextFieldTableCell.forTableColumn(converter));
        return column;
    }

    private <T> void setEditCommitHandler(TableColumn<Product, T> column, String propertyName) {
        column.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            switch (propertyName) {
                case "product_id":
                    product.setProduct_id(event.getNewValue().toString());
                    break;
                case "category":
                    product.setCategory(event.getNewValue().toString());
                    break;
                case "name":
                    product.setName(event.getNewValue().toString());
                    break;
                case "price":
                    product.setPrice((Integer) event.getNewValue());
                    break;
                case "photo":
                    product.setPhoto(event.getNewValue().toString());
                    break;
                case "description":
                    product.setDescription(event.getNewValue().toString());
                    break;
            }
            System.out.println(propertyName + " updated: " + product);
        });
    }

    private ObservableList<Product> getProductList() {
        ObservableList<Product> products = FXCollections.observableArrayList();
        products.add(new Product("P001", "Electronics", "Smartphone1", 299, "smartphone.png", "An advanced smartphone"));
        products.add(new Product("P002", "Electronics", "Smartphone2", 500, "smartphone.png", "An advanced smartphone"));
        // 添加更多產品...
        return products;
    }

    private TableView<Product> initializeProductTable() {
        TableView<Product> table = new TableView<>();
        table.setEditable(true);
        // 表格最後一欄是空白，不要顯示!
        //table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 定義表格欄位...
        // (與之前的程式碼相同)
        // 設定Action欄位的按鈕...
        // (與之前的程式碼相同)
        // 定義表格欄位
        TableColumn<Product, String> idColumn = createColumn("產品編號", "product_id");
        TableColumn<Product, String> categoryColumn = createColumn("類別", "category");
        TableColumn<Product, String> nameColumn = createColumn("名稱", "name");
        TableColumn<Product, Integer> priceColumn = createColumn("價格", "price", new IntegerStringConverter());
        TableColumn<Product, String> photoColumn = createColumn("照片連結", "photo");
        TableColumn<Product, String> descriptionColumn = createColumn("產品描述", "description");
        TableColumn<Product, Void> actionColumn = new TableColumn<>("功能");

        //descriptionColumn.setPrefWidth(500);
        actionColumn.setPrefWidth(300);

        // 設定欄位允許編輯後的事件處理
        setEditCommitHandler(idColumn, "product_id");
        setEditCommitHandler(categoryColumn, "category");
        setEditCommitHandler(nameColumn, "name");
        setEditCommitHandler(priceColumn, "price");
        setEditCommitHandler(photoColumn, "photo");
        setEditCommitHandler(descriptionColumn, "description");

        // 設定Action欄位的按鈕
        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Product, Void> call(final TableColumn<Product, Void> param) {
                return new TableCell<>() {

                    private final Button btnDelete = new Button("刪除");
                    private final Button btnUpdate = new Button("更新");
                    private final Button btnDuplicate = new Button("複製");
                    private final Button btnSave = new Button("儲存");

                    {
                        btnDelete.setOnAction(event -> {
                            Product product = getTableView().getItems().get(getIndex());
                            product_list.remove(product);
                            //從資料庫刪除這一筆
                            productDao.delete(product.getProduct_id());
                            statusLabel.setText("刪除: " + product.getName());
                            System.out.println("刪除: " + product.getName());
                        });
                        btnDelete.getStyleClass().setAll("button", "danger");

                        btnUpdate.setOnAction(event -> {
                            Product product = getTableView().getItems().get(getIndex());
                            // 更新邏輯
                            productDao.update(product);
                            statusLabel.setText("更新: " + product.getName());
                            System.out.println("更新: " + product.getName());
                        });
                        btnUpdate.getStyleClass().setAll("button", "primary");

                        btnDuplicate.setOnAction(event -> {
                            Product product = getTableView().getItems().get(getIndex());
                            product_list.add(new Product(product.getProduct_id(), product.getCategory(), product.getName(), product.getPrice(), product.getPhoto(), product.getDescription()));
                            statusLabel.setText("複製: " + product.getName());
                            System.out.println("複製: " + product.getName());
                        });
                        btnDuplicate.getStyleClass().setAll("button", "warning");

                        btnSave.setOnAction(event -> {
                            Product product = getTableView().getItems().get(getIndex());
                            //從table中取得目前被選到的項目，並將其轉為Product物件，再送到資料庫
                            //Product product = (Product) table.getSelectionModel().getSelectedItem();
                            productDao.insert(product);
                            //product_list.add(new Product(product.getProduct_id(), product.getCategory(), product.getName(), product.getPrice(), product.getPhoto(), product.getDescription()));
                            statusLabel.setText("儲存: " + product.getName());
                            System.out.println("儲存: " + product.getName());
                        });
                        btnSave.getStyleClass().setAll("button", "success");
                    }

                    HBox pane = new HBox(btnDelete, btnUpdate, btnDuplicate, btnSave);

                    {
                        pane.setAlignment(Pos.CENTER);
                        pane.setSpacing(10);
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : pane);
                    }
                };
            }
        };

        actionColumn.setCellFactory(cellFactory);

        table.getColumns().addAll(idColumn, categoryColumn, nameColumn, priceColumn, photoColumn, descriptionColumn, actionColumn);
        table.setItems(product_list);

        return table;
    }

}
