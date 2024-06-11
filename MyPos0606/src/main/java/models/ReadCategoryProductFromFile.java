package models;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

public class ReadCategoryProductFromFile {

    //dictionary  (key, value or content)
    //Map --> dict  {'key':'value'}
    public static TreeMap<String, Product> readProduct() {
        //read_product_from_file(); //從檔案或資料庫讀入產品菜單資訊

        Scanner input;
        //放所有產品  產品編號  產品物件Product
        TreeMap<String, Product> product_dict = new TreeMap();

        try {
            input = new Scanner(new File("mydataset/products.csv"), "utf-8");
            while (input.hasNextLine()) {
                String[] item = input.nextLine().split(",");
                //System.out.println(row);
                Product product = new Product(
                        item[0],
                        item[1],
                        item[2],
                        Integer.parseInt(item[3]), //價格轉為int
                        item[4],
                        item[5]);
                //將這一筆放入字典變數product_dict中 
                product_dict.put(product.getProduct_id(), product);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("檔案讀取錯誤!");
        }
        return product_dict;
    }

    public static void main(String[] args) {

        System.out.println(ReadCategoryProductFromFile.readProduct());

    }

}
