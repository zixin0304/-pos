package mypos_integration;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mypos_db.AppProductMaintenanceDBV1;
import mypos_db.AppProductMaintenanceTableViewDBV1;
import mypos_db.IcePosOrderDBApp;


//https://www.tutorialspoint.com/how-to-create-a-tabpane-in-javafx
//https://jenkov.com/tutorials/javafx/tabpane.html
//https://www.geeksforgeeks.org/javafx-tabpane-class/
//https://www.geeksforgeeks.org/javafx-menubar-and-menu/
public class PosTabPaneMenu extends Application {

    @Override
    public void start(Stage stage) {

   
        //menubarg上面長條 menu在裡面
        // 主功能選單create a menu
        Menu menu = new Menu("選擇功能");
        Menu menuAbout = new Menu("關於");

        // 子項目選單create menuitems
        MenuItem menuitem_customerOrderEntry = new MenuItem("客戶交易輸入");
        MenuItem menuitem_productMaintenance = new MenuItem("產品新增刪除維護");
        MenuItem menuitem_productMaintenance2 = new MenuItem("產品新增刪除維護2");
        //MenuItem menuitem_dailyOrderAnalysis = new MenuItem("每日訂單分析");

    // 將子項目放入主選單 add menu items to menu
        menu.getItems().add(menuitem_customerOrderEntry);
        menu.getItems().add(menuitem_productMaintenance);
        menu.getItems().add(menuitem_productMaintenance2);
        //menu.getItems().add(menuitem_dailyOrderAnalysis);

        // 下拉式選單長條create a menubar
        MenuBar menubar = new MenuBar();

        // add menu to menubar
        menubar.getMenus().add(menu);
        menubar.getMenus().add(menuAbout);

        //取得各個功能畫面的root VBox容器
        VBox order_system = new IcePosOrderDBApp().get_root_pane();
        VBox product_maintenance = new AppProductMaintenanceDBV1().get_root_pane();
        VBox product_maintenance2 = new AppProductMaintenanceTableViewDBV1().getRootPane();

             product_maintenance2.setStyle("-fx-background-color:#FFE5E2;");
        
        // Tab主窗格
        TabPane tabPane = new TabPane();
        // Tab子窗格
        Tab tab_customerOrderEntry = new Tab("客戶交易輸入", order_system);
        Tab tab_productMaintenance = new Tab("產品新增刪除維護", product_maintenance);
        Tab tab_productMaintenance2 = new Tab("產品新增刪除維護2", product_maintenance2);
        //Tab tab_dailyOrderAnalysis = new Tab("每日訂單分析", new Label("訂單分析"));
        

        //將Tab子窗格放入主窗格
        tabPane.getTabs().add(tab_customerOrderEntry);
        tabPane.getTabs().add(tab_productMaintenance);
        tabPane.getTabs().add(tab_productMaintenance2);
        //tabPane.getTabs().add(tab_dailyOrderAnalysis);

        // 訂單輸入模組:額外有一個獨立的視窗 產生新的一個Stage舞台視窗 讓訂單輸入獨立視窗顯示
        // 必須產生額外的一個根容器 new AppOrderEntryDBV1().get_root_pane()
        Stage stageOrderEntry = new Stage();
        stageOrderEntry.setTitle("客戶訂單輸入");
        stageOrderEntry.setScene(new Scene(new IcePosOrderDBApp().get_root_pane()));

        // 子選單被點選之後的事件必須在此定義create events for menu items
        // 定義共同的選單事件action event
        EventHandler<ActionEvent> envent_forMenuItem = (ActionEvent e) -> {
            MenuItem fromTab = (MenuItem) e.getSource();
            if (fromTab.equals(menuitem_customerOrderEntry)) {
              // 方式1:  產生新的一個舞台視窗 讓訂單輸入獨立視窗顯示
                // 視窗若被關閉，仍存在記憶體中，只要再次顯示即可。
                stageOrderEntry.show();

                // 方式2: 顯示在Tab窗格內
                // 判斷要tabPane顯示哪一個子App 若被子Tab被關閉時，需要被重新加入
                if (!tabPane.getTabs().contains(tab_customerOrderEntry)) {
                    System.out.println("加入客戶交易模組到主Tab中");
                    tabPane.getTabs().add(tab_customerOrderEntry); //加入畫面
                    tabPane.getSelectionModel().select(tab_customerOrderEntry); //作用在此tab
                }

              //子選單若選取了產品維護  
            } else if (fromTab.equals(menuitem_productMaintenance)) {

                if (!tabPane.getTabs().contains(tab_productMaintenance)) {
                    tabPane.getTabs().add(tab_productMaintenance);
                    tabPane.getSelectionModel().select(tab_productMaintenance);
                }
               //子選單若選取了產品維護2
            } else if (fromTab.equals(menuitem_productMaintenance2)) {
                
                if (!tabPane.getTabs().contains(tab_productMaintenance2)) {
                    tabPane.getTabs().add(tab_productMaintenance2);
                    tabPane.getSelectionModel().select(tab_productMaintenance2);
                }
                
              
            } 
            //子選單若選取了每日報表分析
            /*
            else if (fromTab.equals(menuitem_dailyOrderAnalysis)) {
                if (!tabPane.getTabs().contains(tab_dailyOrderAnalysis)) {
                    tabPane.getTabs().add(tab_dailyOrderAnalysis);
                    tabPane.getSelectionModel().select(tab_dailyOrderAnalysis);
                }
            }
*/
            //印出目前的tab名稱觀察看看
            //System.out.println(tabPane.getTabs());
        }; //  注意這裡要有一個分號!!

        // 加掛事件add envent
        menuitem_customerOrderEntry.setOnAction(envent_forMenuItem);
        menuitem_productMaintenance.setOnAction(envent_forMenuItem);
        menuitem_productMaintenance2.setOnAction(envent_forMenuItem);
        //menuitem_dailyOrderAnalysis.setOnAction(envent_forMenuItem);

   
        // create a VBox as root
        VBox root = new VBox();
        root.getChildren().add(menubar);
        root.getChildren().add(tabPane);
        
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("雪花冰POS系統");

        stage.show();
    }

}
