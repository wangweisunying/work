/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Production;

import static Production.LectinConstains.cf;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Wei Wang
 */
public class LectinZoomer_UIController implements Initializable {

    public static ArrayList<Data> dupData;
    public static HashMap<String, HashMap<String ,UnitData>> finalUnit;
    public static HashMap<String, String> juLocMap;
    public static String[] testName;
    public static Map<String ,String[]> DupMap;
    public static Map<String ,List<String>> preUnit;
    
    private HashMap<String, HashMap> modifiedUnit;
    @FXML
    private TextField tf_g1;
    @FXML
    private TextField tf_g2;
    @FXML
    private TextField tf_g3;
    @FXML
    private TextField tf_g4;
    @FXML
    private TextField tf_g5;
    @FXML
    private TextField tf_g6;
    @FXML
    private TextField tf_g7;
    @FXML
    private TextField tf_g8;
    @FXML
    private TextField tf_g9;
    @FXML
    private TextField tf_g10;
    @FXML
    private TextField tf_g11;
    @FXML
    private TextField tf_g12;
    @FXML
    private TextField tf_g13;
    @FXML
    private TextField tf_g14;
    @FXML
    private TextField tf_g15;
    @FXML
    private TextField tf_g16;
    @FXML
    private TextField tf_g17;
    @FXML
    private TextField tf_g18;
    @FXML
    private TextField tf_g19;
    @FXML
    private TextField tf_g20;
    @FXML
    private TextField tf_g21;
    @FXML
    private TextField tf_g22;
    @FXML
    private TextField tf_g23;
    @FXML
    private TextField tf_a1;
    @FXML
    private TextField tf_a2;
    @FXML
    private TextField tf_a3;
    @FXML
    private TextField tf_a4;
    @FXML
    private TextField tf_a5;
    @FXML
    private TextField tf_a6;
    @FXML
    private TextField tf_a7;
    @FXML
    private TextField tf_a8;
    @FXML
    private TextField tf_a9;
    @FXML
    private TextField tf_a10;
    @FXML
    private TextField tf_a11;
    @FXML
    private TextField tf_a12;
    @FXML
    private TextField tf_a13;
    @FXML
    private TextField tf_a14;
    @FXML
    private TextField tf_a15;
    @FXML
    private TextField tf_a16;
    @FXML
    private TextField tf_a17;
    @FXML
    private TextField tf_a18;
    @FXML
    private TextField tf_a19;
    @FXML
    private TextField tf_a20;
    @FXML
    private TextField tf_a21;
    @FXML
    private TextField tf_a22;
    @FXML
    private TextField tf_a23;
    @FXML
    private TableColumn testCode_tc;
    @FXML
    private TableColumn name_tc;
    @FXML
    private TableColumn curJun_tc;
    @FXML
    private TableColumn curUnit_tc;
    @FXML
    private TableColumn preJun_tc;
    @FXML
    private TableColumn preUnit_tc;
    @FXML
    private Button Recalculate_btn;
    @FXML
    private Button insert_btn;
    @FXML
    private TextArea log_ta;
    @FXML
    private TableView<Data> table;
    /**
     * Initializes the controller class.
     */
    TextField[] allCf;
    TableColumn[] allTableColumn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        allCf = new TextField[]{tf_a1, tf_g1, tf_a2, tf_g2, tf_a3, tf_g3, tf_a4, tf_g4, tf_a5, tf_g5, tf_a6, tf_g6, tf_a7, tf_g7, tf_a8, tf_g8,
            tf_a9, tf_g9, tf_a10, tf_g10, tf_a11, tf_g11, tf_a12, tf_g12, tf_a13, tf_g13, tf_a14, tf_g14, tf_a15, tf_g15, tf_a16, tf_g16,
            tf_a17, tf_g17, tf_a18, tf_g18, tf_a19, tf_g19, tf_a20, tf_g20, tf_a21, tf_g21, tf_a22, tf_g22, tf_a23, tf_g23,};
        allTableColumn = new TableColumn[]{testCode_tc, curJun_tc, curUnit_tc, preJun_tc, preUnit_tc};
//        dupData = new ArrayList();

//        ObservableList<Data> content  = FXCollections.observableArrayList(
//                new Data("abc" , "AEW" ,"123" , "123" ,"1213" , "12"),
//                new Data("1213" , "123" ,"123" , "123" ,"1123" , "123"),
//                new Data("123" , "123" ,"123" , "1213" ,"123", "123"),
//                new Data("1213" , "123" ,"123" , "1123" ,"123","123"),
//                new Data("123" , "1231" ,"1123" , "123" ,"123","123")    
//        );
        if(cf == null){
            cf = new float[46];
            for (int i = 0; i < 46; i++) {
                cf[i] = 1.0f;
                allCf[i].setText(String.valueOf(1.0));
            }
        }else{
            for (int i = 0; i < cf.length; i++) {
                allCf[i].setText(String.valueOf(cf[i]));
            }            
        }



        ObservableList<Data> content = FXCollections.observableArrayList();
        content.addAll(dupData);
        table.setEditable(true);

        testCode_tc.setCellValueFactory(new PropertyValueFactory("testCode"));
        name_tc.setCellValueFactory(new PropertyValueFactory("name"));
        curJun_tc.setCellValueFactory(new PropertyValueFactory("curJun"));
        curUnit_tc.setCellValueFactory(new PropertyValueFactory("curUnit"));
        curUnit_tc.setCellFactory(col -> {
            TableCell<Data, String> cell = TextFieldTableCell.<Data>forTableColumn().call((TableColumn<Data, String>) col);
            cell.itemProperty().addListener(event1 -> {
                TableRow row = cell.getTableRow();
                Data item = (Data) cell.getTableRow().getItem();
                if (row == null || item == null) {
                    return;
                }
                if (Float.parseFloat(cell.getItem()) < 2) {
                    item.curUnit.set(cell.getItem());
                    cell.setStyle("-fx-background-color: green");
                } else if (Float.parseFloat(cell.getItem()) >= 4) {
                    item.curUnit.set(cell.getItem());
                    cell.setStyle("-fx-background-color: red");
                } else {
                    item.curUnit.set(cell.getItem());
                    cell.setStyle("-fx-background-color: yellow");
                }
            });
            return cell;
        });
        preJun_tc.setCellValueFactory(new PropertyValueFactory("preJun"));

        preUnit_tc.setCellFactory(new Callback<TableColumn<Data, String>, TableCell<Data, String>>() {
            @Override
            public TableCell<Data, String> call(TableColumn<Data, String> param) {
                return new TableCell<Data, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        if (!empty) {
                            int currentIndex = indexProperty().getValue() < 0 ? 0 : indexProperty().getValue();
                            String preUnit = param.getTableView().getItems().get(currentIndex).getPreUnit();
                            setText(preUnit);
                            if (Float.parseFloat(preUnit) < 2) {
                                setStyle("-fx-background-color: green");
                            } else if (Float.parseFloat(preUnit) >= 4) {
                                setStyle("-fx-background-color: red");
                            } else {
                                setStyle("-fx-background-color: yellow");
                            }
                        }

                    }

                };

            }
        });

        table.setItems(content);

    }

    @FXML
    private void recalculate_action(ActionEvent event) throws IOException {
        Button btn  = (Button)event.getSource();
        Stage stage = (Stage)btn.getScene().getWindow();
        stage.close();
        //grab the modification
   
        for (int i = 0; i < allCf.length; i++) {
            cf[i] = Float.parseFloat(allCf[i].getText());
        }
        //modified && reshowUI
        modifiedUnit = new HashMap();
        
        for(String loc : finalUnit.keySet()){  
            HashMap<String ,UnitData> map = new HashMap();
            for(int i = 0 ; i < cf.length ; i++){
                
                String julien = finalUnit.get(loc).get(testName[i]).julien_barcode;
                String unit = String.valueOf(Float.parseFloat(finalUnit.get(loc).get(testName[i]).unit) * cf[i]);
                String pillar_id = finalUnit.get(loc).get(testName[i]).pillar_plate_id;
                String row = finalUnit.get(loc).get(testName[i]).row;
                int col = finalUnit.get(loc).get(testName[i]).col;
                map.put(testName[i] , new UnitData(julien , unit , pillar_id , row ,col));
                
                
            }
            modifiedUnit.put(loc, map);
        }
        
        
               // init the dup data 
        dupData.clear();
        for(String curJun : DupMap.keySet()){
            String loc = juLocMap.get(curJun);
            String preJun = DupMap.get(curJun)[0];
            String name = DupMap.get(curJun)[1];
            
            System.out.println(preUnit.get(preJun));
            for(int i = 0 ; i < preUnit.get(preJun).size() ; i++){       
               HashMap<String ,UnitData > curMap  = modifiedUnit.get(loc); 
               System.out.println(curMap.get(testName[i]).unit);
               System.out.println(i);
               String cur_unit  = curMap.get(testName[i]).unit;
               String pre_unit = preUnit.get(preJun).get(i);
               //add filter before showing UI
               //add filter before showing UI end
               dupData.add(new Data(testName[i] , name , curJun , cur_unit ,preJun, pre_unit ));
            }
                 
        }
        Parent root = FXMLLoader.load(getClass().getResource("LectinZoomer_UI.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void insert_action(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setContentText("Are you sure?");
        Optional result = alert.showAndWait();
        int size = table.getColumns().size();
        String[][] mUnit_arr = new String[size][3];
        if (result.get() == ButtonType.OK) {
            for (int i = 0; i < size; i++) {
                mUnit_arr[i][0] = table.getColumns().get(2).getText(); //julien_barcode
                mUnit_arr[i][1] = table.getColumns().get(0).getText(); //testCode
                mUnit_arr[i][2] = table.getColumns().get(3).getText(); //Unit
            }

            //insert intoDB
        }

    }

}
