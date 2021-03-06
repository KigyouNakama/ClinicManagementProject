/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cm.controller.BacSi;

import cm.ConnectToServer;
import cm.controller.DangNhap.DangNhapController;
import cm.model.Dichvu;
import cm.model.DonDichVu;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author vincent
 */
public class DichVuController implements Initializable, PaneInterface {

    private BacSiController parentPane;
    ConnectToServer con;

    @FXML
    private TableView<Dichvu> DichvuTable;
    @FXML
    private TableColumn ColDichvuMa;
    @FXML
    private TableColumn ColDichvuTen;
    @FXML
    private TableColumn ColDichvuChucnang;
    @FXML
    private TableColumn ColDichvuGia;
    @FXML
    private ComboBox<String> cbLoc;
    @FXML
    private Label lblTenDichVu;
    @FXML
    private TextArea taChucNang;
    @FXML
    private TextArea taThem;
    @FXML
    private Label lblGia;
    @FXML
    private TextField tfLoc;
    @FXML
    private VBox paneThemDichVu;
    private ObservableList<Dichvu> DichvuData = FXCollections.observableArrayList();
    private ObservableList<DonDichVu> DonDichVuData = FXCollections.observableArrayList();
    private TiepNhanController tiepNhanCtrl = ControllerMediator.getInstance().getTiepNhanCtrl();
    private int Ma;
    private float Giaf;
    private String Gia;
    private String Them = "";
    private String S = "";
    private int[] arrayInt = new int[100];
    private int i = 0;
    private int n = 0;
    private int flag = 0;
    public Stage dStage = new Stage();

    /*
     Visible when press button 'Ke don thuoc' in TiepNhan.
     when button 'Thuoc' is pressed, it is unvisible
     */
    public void setPaneThemDichVu(boolean b) {
        paneThemDichVu.setVisible(b);
    }

    //override tu Initializable interface
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbSearchInit();
        addDichVuData();
        initTable(DichvuData);
        ControllerMediator.getInstance().setDichVuCtrl(this);
    }

    @FXML
    private void handleBtnThem(ActionEvent event) {
        DonDichVu dichvu = new DonDichVu();
        dichvu.setTenDangNhap(DangNhapController.getTenDangNhap());
        dichvu.setMaDichVu(DichvuTable.getSelectionModel().getSelectedItem().getMa());
        dichvu.setTenDichVu(lblTenDichVu.getText());
        // dichvu.setChucNang(taChucNang.getText());
        //dichvu.setGia(Giaf);
        flag = 0;
        for (i = 0; i <= n; i++) {
            if (arrayInt[i] == Ma) {
                flag = 1;
                break;
            }
        }
        if (flag == 0) {
            n++;
            arrayInt[n] = Ma;
            DonDichVuData.add(dichvu);
            Them = S.concat(lblTenDichVu.getText());
            S = Them.concat(", ");
            taThem.setText(Them);
            tiepNhanCtrl.themTaDichVu(Them);
        }

        // DichvuTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> addDichvu(newValue);
    }

    @FXML
    private void handleBtnTroLai(ActionEvent event) {
        initTable(DichvuData);
        taThem.setText(Them);
        tiepNhanCtrl.themTaDichVu(Them);
        parentPane.setPane("tiepnhan");
    }

    @FXML
    private void handleBtnXong(ActionEvent event) throws SQLException, IOException {
        Alert dialogStage = new Alert(AlertType.CONFIRMATION);
        dialogStage.setTitle("Dich Vụ Sử Dụng");
        dialogStage.setHeaderText("Bạn có muốn chỉnh sửa Dịch Vụ đang sử dụng");
        //dialogStage.setContentText("Chọn ");

        ButtonType buttonTypeOne = new ButtonType("Sửa Dịch Vụ");
        ButtonType buttonTypeTwo = new ButtonType("Xóa Dịch Vụ");
        ButtonType buttonTypeCancel = new ButtonType("Thoát", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogStage.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
        Optional<ButtonType> result = dialogStage.showAndWait();
        if (dialogStage.getResult() == buttonTypeOne) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/cm/view/BacSi/DonDichVu.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Scene scene = new Scene(page);
            dStage.setScene(scene);
            dStage.showAndWait();
        } else if (dialogStage.getResult() == buttonTypeTwo) {
            Them = "";
            S = "";
            for (int i = 0; i <= n; i++) {
                arrayInt[i] = 0;
            }
            taChucNang.setText("");
            taThem.setText("");
            tiepNhanCtrl.themTaDichVu("");
            DonDichVuData.clear();
            initTable(DichvuData);
        }
    }

    private void addDichVuData() {
        try {
            con = new ConnectToServer();
            String sql = "SELECT* FROM Dich_Vu ORDER BY Ma_Dich_Vu ASC";
            con.sendToServer(sql);
            while (true) {
                Object ob = con.receiveFromServer();
                /*
                 * if returned object is not CacheRowSet, inform and end the loop
                 * else use CacheRowSet and end the loop
                 */
                if (ob.getClass().toString().equals("class java.lang.String")) {
                    break;
                } else {
                    CachedRowSet crs = (CachedRowSet) ob;
                    while (crs.next()) {
                        Dichvu dichvu = new Dichvu();
                        dichvu.setMa(crs.getInt("Ma_Dich_Vu"));
                        dichvu.setTenDichVu(crs.getString("Ten_Dich_Vu"));
                        dichvu.setChucNang(crs.getString("Chuc_Nang"));
                        dichvu.setGia(crs.getFloat("Gia_Dich_Vu"));
                        DichvuData.add(dichvu);
                        System.out.println(DichvuData.size());

                    }
                    break;
                }
            }
            con.sendToServer("done");
        } catch (SQLException ex) {
            Logger.getLogger(DichVuController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cbSearchInit() {
        cbLoc.getItems().addAll("Mã", "Tên Dịch Vụ", "Giá");
        cbLoc.setValue("Mã");
    }

    public void initTable(ObservableList<Dichvu> Data) {
        ColDichvuMa.setCellValueFactory(new PropertyValueFactory<>("ma"));
        ColDichvuTen.setCellValueFactory(new PropertyValueFactory<>("tenDichVu"));
        ColDichvuChucnang.setCellValueFactory(new PropertyValueFactory<>("chucNang"));
        ColDichvuGia.setCellValueFactory(new PropertyValueFactory<>("gia"));

        FilteredList<Dichvu> filteredData = new FilteredList<>(Data, p -> true);
        DichvuTable.setItems(filteredData);
        tfLoc.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(dichvu -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                switch (cbLoc.getValue()) {
                    case "Mã":
                        if (dichvu.getMa() == Integer.parseInt(newValue)) {
                            return true;
                        }
                        break;
                    case "Tên Dịch Vụ":
                        if (dichvu.getTenDichVu().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        }
                        break;
                    case "Giá":
                        if (dichvu.getGia() == Float.parseFloat(newValue)) {
                            return true;
                        }
                }
                return false;
            });
        });
        DichvuTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showDetails(newValue));
    }

    private void showDetails(Dichvu dichvu) {
        if (dichvu != null) {
            Ma = dichvu.getMa();
            lblTenDichVu.setText(dichvu.getTenDichVu());
            Giaf = dichvu.getGia();
            Gia = Float.toString(dichvu.getGia());
            taChucNang.setText(dichvu.getChucNang());
            lblGia.setText(Gia);
        } else {
            lblTenDichVu.setText("");
            lblGia.setText("");
        }
    }

    public ObservableList<DonDichVu> getDonDichVuData() {
        return DonDichVuData;
    }

    //xoa observable list

    public void deleteMemoryDV() {
        Them = "";
        S = "";
         for (int i = 0; i <= n; i++) {
                arrayInt[i] = 0;
            }
        taThem.setText(Them);
        tiepNhanCtrl.themTaDichVu(Them);
        DonDichVuData.clear();
    }

    //override tu PaneInterface interface

    @Override
    public void setScreenParent(BacSiController mainPane) {
        parentPane = mainPane;
    }

}
