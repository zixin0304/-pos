package mypos;

import java.util.TreeMap;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import models.OrderDetail;
import models.Product;
import models.ReadCategoryProduct;

public class AppOrderV1 extends Application {

    //ObservableList有新增或刪除都會觸動table的更新，也就是發生任何改變時都被通知
    //以下視窗元件有操作到models目錄下的OrderDetail，因此要修改專案模組設定
    private ObservableList<OrderDetail> order_list;
    //顯示訂單詳情表格
    private TableView<OrderDetail> table;

    // 產品資訊
    private final TreeMap<String, Product> product_dict = ReadCategoryProduct.readProduct();

    //顯示訂單詳情白板
    private final TextArea display = new TextArea();

    public TilePane getOrderOperationContainer() {

        //========================
        //新增項目、刪除項目之操作區塊------------------------
        //定義新增一筆按鈕
        Button btnAdd = new Button();
        btnAdd.setText("新增一筆");
        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addToCart("p-j-101", false);
                System.out.println("新增一筆");
            }
        });

       //定義刪除一筆按鈕，使用lambda寫法
        Button btnDelete = new Button("刪除一筆");
        btnDelete.setOnAction((ActionEvent event) -> {
            OrderDetail selectedOrder = table.getSelectionModel().getSelectedItem();
            table.getItems().remove(selectedOrder);
            checkTotal();
            System.out.println("刪除一筆");
        });

        //定義結帳按鈕
        Button btnCheck = new Button("結帳");
        btnCheck.setOnAction((ActionEvent e) -> {
            //目前沒有功能
            //checkSave();
        });

        //放置任務功能按鈕的窗格容器-----------------
        TilePane operationBtnTile = new TilePane();
        //operationBtnTile.setAlignment(Pos.CENTER_LEFT);
        //operationBtnTile.setPrefColumns(6);
        operationBtnTile.setVgap(10);
        operationBtnTile.setHgap(10);

        //置放新增刪除按鈕到操作區塊---------------
        operationBtnTile.getChildren().add(btnAdd);
        operationBtnTile.getChildren().add(btnDelete);
        operationBtnTile.getChildren().add(btnCheck);

        //將操作區塊置放到根容器---------
        //root.getChildren().addAll(operationBtnTile);
        //=====================================
        return operationBtnTile;
    } //getOperationBtnTile()

     //####初始化所有元件與事件並將所有元件放入root
    public void initializeOrderTable() {

        //################################
        //訂單order_list初始化----------------------        
        //訂單陣列串列初始化FXCollections類別有很多靜態方法可以操作ObservableList的"訂單陣列串列"
        //可以這樣取得一個空的串列
        order_list = FXCollections.observableArrayList();
        //若已知有兩筆訂單可以這樣設定
        /*
        order_list = FXCollections.observableArrayList(
                new OrderDetail("p-101", "葡萄汁", 80, 3),
                new OrderDetail("p-102", "番茄汁", 70, 1)
        );
        
        //也可以這樣加入一筆訂單
        order_list.add(new OrderDetail("p-103", "西瓜汁", 80, 3));
        //checkTotal();
         */

        //表格talbe初始化----------------------   
        table = new TableView<>();
        table.setEditable(true); //表格允許修改
        table.setPrefHeight(300);

        //定義第一個欄位column"品名"，其值來自於Order物件的某個String變數
        TableColumn<OrderDetail, String> order_item_name = new TableColumn<>("品名");
        //置放哪個變數值?指定這個欄位 對應到OrderDetail的"product_name"實例變數值
        order_item_name.setCellValueFactory(new PropertyValueFactory("product_name"));

        //若要允許修改此欄位
        //order_item_name.setCellFactory(TextFieldTableCell.forTableColumn()    );
        order_item_name.setPrefWidth(100); //設定欄位寬度
        order_item_name.setMinWidth(100);
        //定義欄位column"價格"
        TableColumn<OrderDetail, Integer> order_item_price = new TableColumn<>("價格");
        order_item_price.setCellValueFactory(new PropertyValueFactory("product_price"));
        //定義欄位column"數量"
        TableColumn<OrderDetail, Integer> order_item_qty = new TableColumn<>("數量");
        order_item_qty.setCellValueFactory(new PropertyValueFactory("quantity"));

        //這個欄位值內容可以被修改，在表格內是以文字格式顯示
        //new IntegerStringConverter()是甚麼?
        //因為quantity是整數，因此須將使用者輸入的字串格式轉為整數，才能異動OrderDetail物件，否則會報錯!
        order_item_qty.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        //傳統寫法: 內部匿名類別inner anonymous class(可用滑鼠左鍵自動切換各種寫法)
        //定義修改完成後驅動的事件，使用lambda函式寫法，用->符號，有以下三種寫法
        order_item_qty.setOnEditCommit(event -> {
            //order_item_qty.setOnEditCommit((var event) -> {
            //order_item_qty.setOnEditCommit((CellEditEvent<OrderDetail, Integer> event) -> {
            int row_num = event.getTablePosition().getRow();//哪一筆(編號)被修改
            int new_val = event.getNewValue(); //取得使用者輸入的新數值 (整數)
            OrderDetail target = event.getTableView().getItems().get(row_num); //取得該筆果汁的參考位址
            //修改成新的數值 該筆訂單存放於order_list
            target.setQuantity(new_val);

            //更新總價
            checkTotal();

            System.out.println("哪個產品被修改數量:" + order_list.get(row_num).getProduct_name()); //顯示修改後的數值
            System.out.println("數量被修改為:" + order_list.get(row_num).getQuantity()); //顯示修改後的數值
        });

        //表格內置放的資料來自於order_list，有多個項目，依據置放順序顯示
        table.setItems(order_list);

        //table也可以這樣加入一筆訂單
        //table.getItems().add(new OrderDetail("p-104", "奇異果汁", 50, 2));
        //checkTotal();
        //把3個欄位加入table中
        table.getColumns().addAll(order_item_name, order_item_price, order_item_qty);

        //表格最後一欄是空白，不要顯示!
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //###################################
    } //initializeOrderTable()

    //計算總金額
    public void checkTotal() {
        double total = 0;
        //將所有的訂單顯示在表格   計算總金額
        for (OrderDetail od : order_list) {
            //加總
            total += od.getProduct_price() * od.getQuantity();
        }

        //顯示總金額於display
        String totalmsg = String.format("%s %d\n", "總金額:", Math.round(total));
        display.setText(totalmsg);
    }

    // 購物車新增一筆商品
    public void addToCart(String item_id, boolean addCondensedMilk) {
        Product product = product_dict.get(item_id);

        // 檢查購物車中是否已經有該商品
        boolean duplication = false;
        for (OrderDetail order : order_list) {
            if (order.getProduct_id().equals(item_id) && order.isAddCondensedMilk() == addCondensedMilk) {
                order.setQuantity(order.getQuantity() + 1);
                duplication = true;
                table.refresh();
                System.out.println(item_id + "此筆已經加入購物車，數量+1");
                break;
            }
        }

        // 如果購物車中沒有該商品，則新增一筆
        if (!duplication) {

            order_list.add(new OrderDetail(
                    product.getProduct_id(),
                    product.getName(),
                    product.getPrice(),
                    1,
                    addCondensedMilk
            ));
        }

        // 計算總金額並顯示訂單
        checkTotal();
    }

    @Override
    public void start(Stage stage) {

        //根容器 所有的元件都放在裡面container，最後再放進布景中scene，布景再放進舞台中stage
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10, 10, 10, 10));
        root.getStylesheets().add("/css/bootstrap3.css");

        //呼叫取得新增刪除按鈕窗格
        root.getChildren().add(getOrderOperationContainer());
        initializeOrderTable(); //表格要初始化

        //root套入表格
        root.getChildren().addAll(table);

        //加入白板
        root.getChildren().add(display);

        Scene scene = new Scene(root);
        stage.setTitle("訂單表格");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
