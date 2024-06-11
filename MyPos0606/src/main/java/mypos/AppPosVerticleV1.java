package mypos;

import java.util.TreeMap;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import models.OrderDetail;
import models.Product;
import models.ReadCategoryProduct;

public class AppPosVerticleV1 extends Application {

    //1宣告全域變數)並取得三種菜單的磁磚窗格，隨時被取用。
    private TilePane menuClassicice = getProductCategoryMenu("經典雪花冰");
    private TilePane menufruitice = getProductCategoryMenu("水果雪花冰");
    private TilePane menutopping = getProductCategoryMenu("配料");

    //ObservableList有新增或刪除都會觸動table的更新，也就是發生任何改變時都被通知
    //以下視窗元件有操作到models目錄下的OrderDetail，因此要修改專案模組設定
    private ObservableList<OrderDetail> order_list;
    //顯示訂單詳情表格
    private TableView<OrderDetail> table;

    // 產品資訊
    private final TreeMap<String, Product> product_dict = ReadCategoryProduct.readProduct();

    //顯示訂單詳情白板
    private final TextArea display = new TextArea();

    // 產品菜單磁磚窗格，置放你需要用到的菜單
    public TilePane getProductCategoryMenu(String category) {

        //取得產品清單(呼叫靜態方法取得)
        TreeMap<String, Product> product_dict = ReadCategoryProduct.readProduct();
        //磁磚窗格
        TilePane category_menu = new TilePane(); //
        category_menu.setVgap(10);  //垂直間隙
        category_menu.setHgap(10);
        //設定一個 row有4個columns，放不下就放到下一個row
        category_menu.setPrefColumns(4);

        //將產品清單內容一一置放入產品菜單磁磚窗格
        for (String item_id : product_dict.keySet()) {
            if (product_dict.get(item_id).getCategory().equals(category)) {
                //定義新增一筆按鈕
                Button btn = new Button();
                btn.setPrefSize(120, 120);

                //按鈕元件顯示圖片
                Image img = new Image("/imgs/" + product_dict.get(item_id).getPhoto());
                ImageView imgview = new ImageView(img);
                imgview.setFitHeight(90);
                imgview.setPreserveRatio(true);
                btn.setGraphic(imgview);
                category_menu.getChildren().add(btn);

                // 煉乳選項 配料不出現煉乳
                CheckBox condensedMilkCheckBox = null;
                if (!category.equals("配料")) {
                    condensedMilkCheckBox = new CheckBox("加煉乳");
                    category_menu.getChildren().add(condensedMilkCheckBox);
                }
                //定義按鈕事件
                final CheckBox finalCondensedMilkCheckBox = condensedMilkCheckBox;
                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        boolean addCondensedMilk = finalCondensedMilkCheckBox != null && finalCondensedMilkCheckBox.isSelected();
                        addToCart(item_id, addCondensedMilk);
                        System.out.println(product_dict.get(item_id).getName() + " - 加煉乳: " + addCondensedMilk);
                    }
                });
            }
        }
        return category_menu;
    }//getProductCategoryMenu()

    //2.宣告一個容器(全域變數) menuContainerPane，裝不同種類的菜單，菜單類別選擇按鈕被按下，立即置放該種類的菜單。
    VBox menuContainerPane = new VBox();

    //3.多一個窗格(可以用磁磚窗格最方便)置放菜單類別選擇按鈕，置放於主視窗的最上方區域。
    public TilePane getMenuSelectionContainer() {

        //定義"經典雪花冰類"按鈕
        Button btnClassicice = new Button();
        btnClassicice.setText("經典雪花冰");
        btnClassicice.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                select_category_menu(e);
            }
        });
        //定義"水果雪花冰類"按鈕
        Button btnfruitice = new Button("水果雪花冰");
        btnfruitice.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                select_category_menu(e);
            }
        });
        //定義"配料類"按鈕
        Button btntopping = new Button("配料加點區");
        btntopping.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                select_category_menu(e);
            }
        });

        //使用磁磚窗格，放置前面三個按鈕
        TilePane conntainerCategoryMenuBtn = new TilePane();
        conntainerCategoryMenuBtn.getStylesheets().add("css/myButton.css");
        //conntainerCategoryMenuBtn.setAlignment(Pos.CENTER_LEFT);
        //conntainerCategoryMenuBtn.setPrefColumns(6); //
        conntainerCategoryMenuBtn.setVgap(10);
        conntainerCategoryMenuBtn.setHgap(10);

        conntainerCategoryMenuBtn.getChildren().add(btnClassicice);
        conntainerCategoryMenuBtn.getChildren().add(btnfruitice);
        conntainerCategoryMenuBtn.getChildren().add(btntopping);

        return conntainerCategoryMenuBtn;
    } // getMenuSelectionContainer()方法

    // 前述三個類別按鈕可以呼叫以下事件
    public void select_category_menu(ActionEvent event) {
        String category = ((Button) event.getSource()).getText();
        menuContainerPane.getChildren().clear();//先刪除原有的窗格再加入新的類別窗格
        switch (category) {
            case "經典雪花冰":
                menuContainerPane.getChildren().add(menuClassicice);
                break;
            case "水果雪花冰":
                menuContainerPane.getChildren().add(menufruitice);
                break;
            case "配料加點區":
                menuContainerPane.getChildren().add(menutopping);
                break;
            default:
                break;
        }

    }//select_category_menu

    //====表格新增項目刪除項目之操作區塊
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
    public void start(Stage Stage) {

        // 設定視窗標題
        Stage.setTitle("雪花冰 POS");

        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10, 10, 10, 10));
        root.getStylesheets().add("/css/bootstrap3.css");

        // 設置菜單類別選擇按鈕容器
        TilePane menuSelectionContainer = getMenuSelectionContainer();
        root.getChildren().add(menuSelectionContainer);

        // 設置菜單容器
        menuContainerPane.getChildren();  // 預設顯示經典雪花冰菜單
        root.getChildren().add(menuContainerPane);

        // 訂單操作區
        TilePane orderOperationContainer = getOrderOperationContainer();
        root.getChildren().add(orderOperationContainer);

        // 初始化訂單表格
        initializeOrderTable();
        root.getChildren().add(table);

        //加入白板
        display.setEditable(false);
        display.setMinHeight(100);
        root.getChildren().add(display);

        // 設定場景
        Scene scene = new Scene(root, 600, 600);
        Stage.setScene(scene);
        Stage.show();
    }// start()

    public static void main(String[] args) {
        launch(args);
    }

}
