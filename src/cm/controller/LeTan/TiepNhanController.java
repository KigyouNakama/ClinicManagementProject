/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cm.controller.LeTan;

import cm.ConnectToServer;
import cm.model.BenhNhan;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author vinh
 */
public class TiepNhanController implements Initializable{
    ConnectToServer con;
    
    @FXML
    private Label lblTen;
    @FXML
    private Label lblNgaySinh;
    @FXML
    private Label lblGioiTinh;
    @FXML
    private Label lblDiaChi;
    @FXML
    private Label lblPhone;
    @FXML
    private TextField tfTen1;
    @FXML
    DatePicker date1;
    @FXML
    private ToggleGroup groupLevel1;
    @FXML
    private RadioButton rbNam1;
    @FXML
    private RadioButton rbNu1;
    @FXML
    private TextField tfDiaChi1;
    @FXML
    private TextField tfPhone1;
    @FXML
    private TextField tfTen2;
    @FXML
    DatePicker date2;
    @FXML
    private ToggleGroup groupLevel2;
    @FXML
    private RadioButton rbNam2;
    @FXML
    private RadioButton rbNu2;
    @FXML
    private TextField tfDiaChi2;
    @FXML
    private TextField tfPhone2;
    @FXML
    private TableView<BenhNhan> BenhNhanTable;
    @FXML
    private TableColumn MaColumn;
    @FXML
    private TableColumn TenColumn;
    @FXML
    private TableColumn NgaySinhColumn;
    @FXML
    private TableColumn GioiTinhColumn;
    @FXML
    private TableColumn PhoneColumn;
    @FXML
    private TableColumn ThoiGianColumn;
    @FXML
    private TableColumn TrangThaiColumn;
    @FXML
    private ComboBox<String> cbSearch;
    @FXML
    private TextField tfFilter;
    @FXML
    private ComboBox<String> cbSearchTime;
    
    private String gioitinh1;
    private String gioitinh2;
    private BenhNhan bnSelected;
    
    private ObservableList<BenhNhan> BenhNhanData = FXCollections.observableArrayList();
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            
            cbSearchInit();
            rbNam1.setOnAction(e -> gioitinh1 = "Nam");
            rbNu1.setOnAction(e -> gioitinh1 = "Nữ");
            rbNam2.setOnAction(e -> gioitinh2 = "Nam");
            rbNu2.setOnAction(e -> gioitinh2 = "Nữ");
            addBenhNhanData();
            initTable();
        } catch (SQLException ex) {
            Logger.getLogger(TiepNhanController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void addBenhNhanData() throws SQLException{
        //hien thi benh nhan theo thu tu co thoi gian kham gan nhat
        String sql = "SELECT * FROM Benh_Nhan natural join (select * from Phien_Kham order by Thoi_Gian_Kham desc) as pk "
                        + "group by Ma_Benh_Nhan "
                        + "order by Thoi_Gian_Kham desc;";
        BenhNhanData.clear();
        
        con = new ConnectToServer();
        con.sendToServer(sql);
        
        while(true){
            Object ob = con.receiveFromServer();
            /*
             * if returned object is not CacheRowSet, inform and end the loop
             * else use CacheRowSet and end the loop
            */
            if (ob.getClass().toString().equals("class java.lang.String")) {
                break;
            }
            else{
                CachedRowSet crs = (CachedRowSet)ob;
                while(crs.next()){
                    BenhNhan benhnhan = new BenhNhan();
                    benhnhan.setMa(crs.getInt("Ma_Benh_Nhan"));
                    benhnhan.setHoTen(crs.getString("Ho_Ten_BN"));
                    benhnhan.setNgaySinh(crs.getString("Ngay_Sinh_BN"));
                    benhnhan.setGioiTinh(crs.getString("Gioi_Tinh_BN"));
                    benhnhan.setPhone(crs.getString("SDT_BN"));
                    benhnhan.setThoiGian(crs.getString("Thoi_Gian_Kham"));
                    benhnhan.setTrangThai(crs.getString("Trang_Thai_BN"));
                    benhnhan.setDiaChi(crs.getString("Dia_chi_BN"));
                    BenhNhanData.add(benhnhan);
                }
                break;
            }
            
        }
        con.sendToServer("done");
        
    }
    private void cbSearchInit() {
        cbSearchTime.getItems().addAll("Hôm Nay" , "Tất Cả");
        cbSearchTime.setValue("Tất Cả");
        cbSearch.getItems().addAll("Mã","Họ Tên","Trạng Thái");
        cbSearch.setValue("Họ Tên");
    }
    public void initTable(){
        MaColumn.setCellValueFactory(new PropertyValueFactory<>("Ma"));
        TenColumn.setCellValueFactory(new PropertyValueFactory<>("HoTen"));
        NgaySinhColumn.setCellValueFactory(new PropertyValueFactory<>("NgaySinh"));
        GioiTinhColumn.setCellValueFactory(new PropertyValueFactory<>("GioiTinh"));
        ThoiGianColumn.setCellValueFactory(new PropertyValueFactory<>("ThoiGian"));
        PhoneColumn.setCellValueFactory(new PropertyValueFactory<>("Phone"));
        TrangThaiColumn.setCellValueFactory(new PropertyValueFactory<>("TrangThai"));
        
        
        FilteredList<BenhNhan> filteredData = new FilteredList<>(BenhNhanData, p -> true);
        
       cbSearchTime.valueProperty().addListener((observable, oldValue , newValue) -> {
           filteredData.setPredicate(benhnhan -> {
               switch(newValue){
                   case "Hôm Nay":
                       if (benhnhan.getThoiGian().contains(LocalDate.now().toString()))
                           return true;
                       break;
                   case "Tất Cả":
                       return true;
               }
               return false;
           });
       });
       FilteredList<BenhNhan> filteredData2 = new FilteredList<>(filteredData , p->true);
        tfFilter.textProperty().addListener((observable, oldValue , newValue) -> {
            filteredData2.setPredicate( benhnhan -> {
                if (newValue == null || newValue.isEmpty()){
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                switch(cbSearch.getValue()) {
                    case "Mã":
                        if (benhnhan.getMa() == Integer.parseInt(newValue)){
                            return true;
                        }
                        break;
                    case "Họ Tên":
                        if (benhnhan.getHoTen().toLowerCase().contains(lowerCaseFilter)){
                            return true;
                        }
                        break;
                    case "Trạng Thái":
                        if (benhnhan.getTrangThai().toLowerCase().contains(lowerCaseFilter)){
                            return true;
                        }
                        break;
                }
                       
                
                return false;
            });
        });
        BenhNhanTable.setItems(filteredData2);
        BenhNhanTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showDetails(newValue));
        
        
    }
    
    private void showDetails(BenhNhan benhnhan) {
        bnSelected = benhnhan;
        if(benhnhan != null){
            //Show thong tin o tab thong tin
            lblTen.setText(benhnhan.getHoTen());
            lblNgaySinh.setText(benhnhan.getNgaySinh());
            lblGioiTinh.setText(benhnhan.getGioiTinh());
            lblPhone.setText(benhnhan.getPhone());
            lblDiaChi.setText(benhnhan.getDiaChi());
            
            //Show thong tin o tab chinh sua
            
            tfTen2.setText(benhnhan.getHoTen());
            
           
            
            date2.setValue(LocalDate.parse(benhnhan.getNgaySinh(), DateTimeFormatter.ISO_DATE));
            switch (benhnhan.getGioiTinh()){
                case "Nam":
                    rbNam2.setSelected(true);
                    gioitinh2 = "Nam";
                    break;
                case "Nữ":
                    rbNu2.setSelected(true);
                    gioitinh2 = "Nữ";
                    break;
            }
            tfDiaChi2.setText(benhnhan.getDiaChi());
            tfPhone2.setText(benhnhan.getPhone());
            
        }
        else{
            lblTen.setText("");
            lblNgaySinh.setText("");
            lblGioiTinh.setText("");
            lblPhone.setText("");
            lblDiaChi.setText("");
            
            
            tfTen2.setText("");
            date2.setValue(null);
            rbNam2.setSelected(false);
            rbNu2.setSelected(false);
            gioitinh2 = "";
            tfDiaChi2.setText("");
            tfPhone2.setText("");
        }
    }
    
    
    @FXML
    private void taoPhienKhamMoi(ActionEvent e) throws IOException{
        if (bnSelected.getTrangThai().equals("kết thúc")){
            Alert alert = new Alert(AlertType.CONFIRMATION, "Xác Nhận Tạo Phiên Khám Mới Cho Bệnh Nhân?", ButtonType.YES , ButtonType.NO);
            alert.setTitle("Xác Nhận");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES){
                try {
                    con = new ConnectToServer();
                    
                    String sql = "Update Benh_Nhan set Trang_Thai_BN = 'phòng khám' where Ma_Benh_Nhan =  '" + bnSelected.getMa()+"'";
                    con.sendToServer(sql);
                    //tao phien kham moi
                    long timeNow = Calendar.getInstance().getTimeInMillis();
                    Timestamp ts = new java.sql.Timestamp(timeNow);
                    sql = "INSERT INTO Phien_Kham(Ma_Benh_Nhan, Thoi_Gian_Kham) VALUES ( '" + bnSelected.getMa() +"','" +ts.toString() +"')";
                    con.sendToServer(sql);
                    con.sendToServer("done");
                    
                    //cap nhat lai bang
                    addBenhNhanData();
                    
                    
                } catch (SQLException ex) {
                    Logger.getLogger(TiepNhanController.class.getName()).log(Level.SEVERE, null, ex);
                }     
            }
        }
        else{
            Alert alert2 = new Alert(AlertType.INFORMATION,"Bệnh nhân đang trong phiên khám!", ButtonType.OK);
            alert2.setTitle("Xác Nhận");
            alert2.showAndWait();
        }
        
    }
    @FXML
    private void Them (ActionEvent e) throws IOException {
        try {
            Alert dialogStage = new Alert(AlertType.CONFIRMATION, "Bạn có xác nhận thêm bệnh nhân?", ButtonType.YES ,ButtonType.NO) ;
            dialogStage.setTitle("Xác Nhận");
            
            dialogStage.showAndWait();
            if (dialogStage.getResult() == ButtonType.YES) {
                if (!tfTen1.getText().isEmpty()
                        && !date1.getValue().toString().isEmpty()
                        && !gioitinh1.isEmpty()
                        && !tfDiaChi1.getText().isEmpty()
                        && !tfPhone1.getText().isEmpty()
                        ) {
                    //them vao obsevablelist
                    BenhNhan benhnhan = new BenhNhan();
                    benhnhan.setHoTen(tfTen1.getText());
                    benhnhan.setNgaySinh(date1.getValue().toString());
                    benhnhan.setGioiTinh(gioitinh1);
                    benhnhan.setDiaChi(tfDiaChi1.getText());
                    benhnhan.setPhone(tfPhone1.getText());
                    benhnhan.setTrangThai("phòng khám");
                    //Them thong tin benh nhan vao co so du lieu
                    
                    con =new ConnectToServer();
                    String sql = "insert into Benh_Nhan values (NULL,'"
                            +benhnhan.getHoTen()+"','"
                            +benhnhan.getNgaySinh()+"','"
                            +benhnhan.getDiaChi()+"','"
                            +benhnhan.getGioiTinh()+"','"
                            +benhnhan.getPhone()+"','"
                            +benhnhan.getTrangThai()+"');";
                    con.sendToServer(sql);
                    // lay ma benhn nhan da nhap
                    
                    sql = "select * from `Benh_Nhan` where Ho_Ten_BN = '"+benhnhan.getHoTen()+"' and SDT_BN = '" + benhnhan.getPhone()+"'";
                    con.sendToServer(sql);
                    while(true){
                        Object ob = con.receiveFromServer();
                        if(ob.getClass().toString().equals("class java.lang.String")){
                            break;
                        }
                        else{
                            CachedRowSet crs = (CachedRowSet)ob;
                            crs.next();
                            benhnhan.setMa(crs.getInt("Ma_Benh_Nhan"));
                            break;
                        }
                    }

                    //them thoi gian kham vao bang Phien_Kham cho benh nhan
                    
                    long timeNow = Calendar.getInstance().getTimeInMillis();
                    Timestamp ts = new java.sql.Timestamp(timeNow);
                    sql = "INSERT INTO Phien_Kham(Ma_Benh_Nhan, Thoi_Gian_Kham) VALUES ('"+ benhnhan.getMa() +"','"+ts.toString()+"')";
                    con.sendToServer(sql);
                    benhnhan.setThoiGian(ts.toString());
                    //cap nhat lai bang
                    refreshTabThemMoi();    
                    con.sendToServer("done");
                    addBenhNhanData();
                    
                }
                else{
                    Alert alert2 =new Alert(Alert.AlertType.ERROR , "Thiếu Thông Tin" , ButtonType.OK);
                    alert2.showAndWait();
                }
                
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(TiepNhanController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void refreshTabThemMoi(){
        tfTen1.setText("");
        date1.setValue(null);
        rbNam1.setSelected(false);
        rbNu1.setSelected(false);
        gioitinh1 = "";
        tfDiaChi1.setText("");
        tfPhone1.setText("");
    }
   
    @FXML 
    private void Huy(ActionEvent e) throws IOException {
        refreshTabThemMoi();
    }
        
    @FXML
    private void Sua(ActionEvent e) throws IOException, SQLException{
        Alert alert = new Alert(AlertType.CONFIRMATION, "Xác nhận sửa thông tin?" , ButtonType.YES , ButtonType.NO);
        alert.setTitle("Xác Nhận");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES){
            if (!tfTen2.getText().isEmpty()
                    && !date2.getValue().toString().isEmpty()
                    && !gioitinh2.isEmpty()
                    && !tfDiaChi2.getText().isEmpty()
                    && !tfPhone2.getText().isEmpty()
                    ) {
                //cap nhat vao du lieu
                
                con = new ConnectToServer();
                String sql = "Update Benh_Nhan set Ho_Ten_BN = '"
                        +tfTen2.getText()+"',Ngay_Sinh_BN = '"
                        +date2.getValue().toString()+"', Dia_Chi_BN = '"
                        +tfDiaChi2.getText()+"' , Gioi_Tinh_BN = '"
                        + gioitinh2+ "' , SDT_BN = '"
                        + tfPhone2.getText()+"' where Ma_Benh_Nhan = '"
                        + bnSelected.getMa() + "'";
                con.sendToServer(sql);
                con.sendToServer("done");
                
                //cap nhat vao bang
                
                addBenhNhanData();
                
            }
            else{
                Alert alert2 =new Alert(Alert.AlertType.ERROR , "Thiếu Thông Tin" , ButtonType.OK);
                alert2.showAndWait();
            }
        }
        
    }

    @FXML
    private void Xoa(ActionEvent e) throws IOException, SQLException{
        Alert alert = new Alert(AlertType.CONFIRMATION, "Xác nhận xóa bệnh nhân?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Xác Nhận");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES){
            con =new ConnectToServer();
            String sql = "delete from Benh_Nhan where Ma_Benh_Nhan = '" +bnSelected.getMa() + "'";
            
            con.sendToServer(sql);
            con.sendToServer("done");
            addBenhNhanData();
           
        }
    }
    
}
