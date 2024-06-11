package mypos_db;


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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import models.Order;
import models.OrderDAO;
import models.OrderDetail;
import models.Product;
import models.ProductDAO;
import models.ReadCategoryProduct;
import models.ReadCategoryProductFromDB;

public class IcePosOrderDBApp extends Application {
    
   //***********產生資料DAO來使用訂單輸入資料庫之功能
    private OrderDAO orderDao = new OrderDAO();

    //取得產品字典
    private ProductDAO prodcutDao = new ProductDAO();

    //1宣告全域變數)並取得三種菜單的磁磚窗格，隨時被取用。
    private TilePane menuClassicice = getProductCategoryMenu("經典雪花冰");
    private TilePane menufruitice = getProductCategoryMenu("水果雪花冰");
    private TilePane menutopping = getProductCategoryMenu("配料");

 //4.宣告一個容器(全域變數) menuContainerPane，裝不同種類的菜單，菜單類別選擇按鈕被按下，立即置放該種類的菜單。
    private VBox menuContainerPane = new VBox();

    //ObservableList    order_list有新增或刪除都會處動table的更新，也就是發生任何改變時都被通知
    private ObservableList<OrderDetail> order_list;

    // 產品資訊 必須要全部讀出來 查詢品名及價格會用到
    //方式0 原來寫死的
    //private final TreeMap<String, Product> product_dict = ReadCategoryProduct.readProduct();
    //方式1
    ProductDAO productDao = new ProductDAO();
    private TreeMap<String, Product> product_dict = productDao.readAllProductToTreeMap();
    
    //方式2
    //private TreeMap<String, Product> product_dict = ReadCategoryProductFromDB.readProduct();

    //顯示訂單詳情表格
    private TableView table;

    //顯示訂單詳情白板
    private TextArea display = new TextArea();
    
    
    // 產品菜單磁磚窗格，置放你需要用到的菜單
    public TilePane getProductCategoryMenu(String category) {

        //取得產品清單(呼叫靜態方法取得)
        TreeMap<String, Product> product_dict = ReadCategoryProduct.readProduct();
        
        
        //磁磚窗格
        TilePane category_menu = new TilePane();
        category_menu.setVgap(10);
        category_menu.setHgap(60);
        category_menu.setPrefColumns(2);


        //將產品清單內容一一置放入產品菜單磁磚窗格
        for (String item_id : product_dict.keySet()) {
            if (product_dict.get(item_id).getCategory().equals(category)) {
                
                
                //定義新增一筆按鈕
                Button btn = new Button();
                btn.setPrefSize(115, 100);

                 Image img;
            ImageView imgview;
                
                //按鈕元件顯示圖片
               try {
                img = new Image("/imgs/" + product_dict.get(item_id).getPhoto()); //讀出圖片
                //按鈕元件顯示圖片Creating a graphic (image)
                imgview = new ImageView(img);//圖片顯示物件
            } catch (Exception ex) {
                System.out.println("讀取圖片例外");
                imgview = new ImageView();//空白的圖片
            }
                imgview.setFitHeight(90);
                imgview.setPreserveRatio(true);
                btn.setGraphic(imgview);
                category_menu.getChildren().add(btn);

                // 煉乳選項 配料不出現煉乳
            CheckBox condensedMilkCheckBox = null;
            VBox itemBox = new VBox();
            itemBox.setSpacing(5);
            itemBox.getChildren().add(btn);
            if (!category.equals("配料")) {
                condensedMilkCheckBox = new CheckBox("加煉乳");
                itemBox.getChildren().add(condensedMilkCheckBox);
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
                category_menu.getChildren().add(itemBox);
            }
        }
        return category_menu;
    }
    
     //3.多一個窗格(可以用磁磚窗格最方便)置放菜單類別選擇按鈕，置放於主視窗的最上方區域。
    public TilePane getMenuSelectionContainer() {

         //定義"經典雪花冰類"按鈕
         
        
        
        Button btnClassicice = new Button();
        btnClassicice.setText("經典雪花冰");
        btnClassicice.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                menuContainerPane.getChildren().clear();//先刪除原有的窗格再加入新的類別窗格
                menuContainerPane.getChildren().add(menuClassicice);
            }
        });
       //定義"水果雪花冰類"按鈕
        Button btnfruitice = new Button("水果雪花冰");
        btnfruitice.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                menuContainerPane.getChildren().clear();//先刪除原有的窗格再加入新的類別窗格
                menuContainerPane.getChildren().add(menufruitice);
            }
        });
        //定義"配料類"按鈕
        Button btntopping = new Button("配料加點區");
        btntopping.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                menuContainerPane.getChildren().clear();//先刪除原有的窗格再加入新的類別窗格
                menuContainerPane.getChildren().add(menutopping);
            }
        });

        //使用磁磚窗格，放置前面三個按鈕
        TilePane conntainerCategoryMenuBtn = new TilePane();
        conntainerCategoryMenuBtn.getStylesheets().add("css/myButton.css");
        //conntainerCategoryMenuBtn.setAlignment(Pos.CENTER_LEFT);
        //conntainerCategoryMenuBtn.setPrefColumns(2); 
        conntainerCategoryMenuBtn.setVgap(10);
        conntainerCategoryMenuBtn.setHgap(10);

        conntainerCategoryMenuBtn.getChildren().add(btnClassicice);
        conntainerCategoryMenuBtn.getChildren().add(btnfruitice);
        conntainerCategoryMenuBtn.getChildren().add(btntopping);
        
         //自訂css
        btnClassicice.setStyle("-fx-background-color: #AFDED3; -fx-text-fill: #009442;");
        btnfruitice.setStyle("-fx-background-color: #FFD1DC; -fx-text-fill: #FF3356;");
        btntopping.setStyle("-fx-background-color: #FFFFCA; -fx-text-fill: #FFB81F;");
        //使用bootstrap
        //btnClassicice.getStyleClass().setAll("button", "success");
        //System.out.println(btnClassicice.getStyleClass());

        return conntainerCategoryMenuBtn;
    } // getMenuSelectionContainer()方法

    // 初始化訂單表格
    public void initializeOrderTable() {
        //訂單order_list初始化----------------------        
        //訂單陣列串列初始化FXCollections類別有很多靜態方法可以操作ObservableList的"訂單陣列串列"
        //可以這樣取得一個空的串列
        order_list = FXCollections.observableArrayList();

        //表格talbe初始化----------------------   
        table = new TableView<>();
        table.setEditable(true); //表格允許修改
        table.setPrefHeight(300);

        //定義第一個欄位column"品名"，其值來自於Order物件的某個String變數
        TableColumn<OrderDetail, String> order_item_name = new TableColumn<>("品名");
        //置放哪個變數值?指定這個欄位 對應到OrderDetail的"product_name"實例變數值
        order_item_name.setCellValueFactory(new PropertyValueFactory("product_name"));

        //若要允許修改此欄位
        order_item_name.setCellFactory(TextFieldTableCell.forTableColumn());

        order_item_name.setPrefWidth(100); //設定欄位寬度
        order_item_name.setMinWidth(100);
        //定義欄位column"價格"
        TableColumn<OrderDetail, Integer> order_item_price = new TableColumn<>("價格");
        order_item_price.setCellValueFactory(new PropertyValueFactory("product_price"));
        //定義欄位column"數量"
        TableColumn<OrderDetail, Integer> order_item_qty = new TableColumn<>("數量");
        order_item_qty.setCellValueFactory(new PropertyValueFactory("quantity"));

        //這個欄位值內容可以被修改，在表格內是以文字格式顯示
        //new IntegerStringConverter()是甚麼? 文字轉數字 數字轉文字
        //因為quantity是整數，因此須將使用者輸入的字串格式轉為整數，才能異動OrderDetail物件，否則會報錯!
        order_item_qty.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        TableColumn<OrderDetail, Boolean> condensedMilkCol = new TableColumn<>("加煉乳");
        condensedMilkCol.setCellValueFactory(new PropertyValueFactory<>("addCondensedMilk"));
        condensedMilkCol.setMinWidth(50);

        //傳統寫法: 內部匿名類別inner anonymous class(可用滑鼠左鍵自動切換各種寫法)
        //定義修改完成後驅動的事件，使用lambda函式寫法，用->符號，有以下三種寫法
        order_item_qty.setOnEditCommit(event -> {//(event 也可以(var event)((下面兩行
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
        checkTotal();

        //把4個欄位加入table中
        table.getColumns().addAll(order_item_name, order_item_price, order_item_qty, condensedMilkCol);

        //表格最後一欄是空白，不要顯示!
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    } //initializeOrderTable()
    
    //表格新增項目刪除項目之操作區塊
    private TilePane getOrderOperationContainer() {

        //定義刪除一筆按鈕
        Button btnDelete = new Button("刪除這一筆");
        btnDelete.setStyle("-fx-background-color:#FFB471; -fx-text-fill: #FFFFFF;");
        btnDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //從table中取得目前被選到的項目
                Object selectedItem = table.getSelectionModel().getSelectedItem();
                //從表格table中或是從order_list這一筆，擇一進行
                //table.getItems().remove(selectedItem);
                order_list.remove(selectedItem);
                checkTotal();
            }
        });

        //定義結帳按鈕
        Button btnCheck = new Button("結帳");
        btnCheck.setStyle("-fx-background-color:#12406A; -fx-text-fill: #FFFFFF;");
        btnCheck.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                checkSave();
            }
        });
        //放置任務功能按鈕
        TilePane operationBtnTile = new TilePane();
        //operationBtnTile.setAlignment(Pos.CENTER_LEFT);
        //operationBtnTile.setPrefColumns(6);
        operationBtnTile.setVgap(10);
        operationBtnTile.setHgap(10);

        //btnAdd.setDisable(true);
        //operationBtnTile.getChildren().add(btnAdd);
        operationBtnTile.getChildren().add(btnDelete);
        operationBtnTile.getChildren().add(btnCheck);

        //operationBtnTile.setStyle("-fx-background-color:#A6C2CE;");
        
        return operationBtnTile;
    }

    //計算總金額
    private void checkTotal() {
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
    
    
    //根容器 所有的元件都放在裡面container VBox
    //取得跟容器的方法
    public VBox get_root_pane() {
        //根容器 所有的元件都放在裡面container，
        //最後再放進布景中scene，布景再放進舞台中stage
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10, 10, 10, 10));
        root.getStylesheets().add("/css/bootstrap3.css");
        root.setStyle("-fx-background-color:#FFE5E2;");
        
        //塞入菜單選擇區塊
        TilePane menuSelectionTile = getMenuSelectionContainer();
        root.getChildren().add(menuSelectionTile);
        //menuSelectionTile.setStyle("-fx-background-color:#A6C2CE;");
        //#FFFFFF

        //取得菜單磁磚窗格並放入根容器
        // 塞入菜單區塊 
        menuContainerPane.getChildren().add(menuClassicice);
        root.getChildren().add(menuContainerPane);
        
        //menuContainerPane.setStyle("-fx-background-color:#A6C2CE;");
        
         //塞入增加表格刪除項目操作之容器
        root.getChildren().add(getOrderOperationContainer());
        
        //塞入表格
        initializeOrderTable(); //表格要初始化
        root.getChildren().add(table);
        //塞入白板display
        //display.setPrefColumnCount(10);
        display.setPrefRowCount(2);
        //display.setStyle("-fx-font-size: 30px;"); //
        display.getStylesheets().add("css/myTextArea.css");
        root.getChildren().add(display);

        

        return root;
    }
    
     @Override
    public void start(Stage stage) throws Exception {

        //取得 root pane
        VBox root = get_root_pane();
        //塞入布景
        Scene scene = new Scene(root);
        stage.setTitle("訂單表格");
        
        stage.setScene(scene);
        stage.show();
        System.out.println("執行start()");

    }
    
    
     //**
    //結帳存檔
    private void checkSave() {

        display.setText("結帳中，列印發票...\n");

        //append_order_to_csv(); //將這一筆訂單附加儲存到檔案或資料庫
        //這裡要取得不重複的order_num編號
        String order_num = orderDao.getMaxOrderNum();

        if (order_num == null) {
            order_num = "ord-100";
        }

        System.out.println(order_num);
        System.out.println(order_num.split("-")[1]);

        //將現有訂單編號加上1
        int serial_num = Integer.parseInt(order_num.split("-")[1]) + 1;
        System.out.println(serial_num);

        //每家公司都有其訂單或產品的編號系統，這裡用ord-xxx表之
        String new_order_num = "ord-" + serial_num;

        //int sum = checkTotal();
        int total = 0;
        //將所有的訂單顯示在表格   計算總金額
        for (OrderDetail od : order_list) {
            //加總
            total += od.getProduct_price() * od.getQuantity();
        }

        //Cart crt = new Cart(new_order_num, "2021-05-01", 123, userName);
        Order crt = new Order();
        crt.setOrder_num(new_order_num);
        crt.setTotal_price(total);
        crt.setCustomer_name("無姓名");
        crt.setCustomer_phtone("無電話");
        crt.setCustomer_address("無地址");

        //寫入一筆訂單道資料庫
        orderDao.insertCart(crt);

        //逐筆寫入訂單明細
        for (int i = 0; i < order_list.size(); i++) {
            OrderDetail item = new OrderDetail();
            item.setOrder_num(new_order_num); //設定訂單編號
            item.setProduct_id(order_list.get(i).getProduct_id()); //設定產品編號
            item.setQuantity(order_list.get(i).getQuantity());//設定訂購數量 多少杯
            item.setAddCondensedMilk(order_list.get(i).isAddCondensedMilk());

            orderDao.insertOrderDetailItem(item);
        }

        order_list.clear();
    }
    
   


    public static void main(String[] args) {
        launch(args);
    }

}
