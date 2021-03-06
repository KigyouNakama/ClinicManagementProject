/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cm.controller.DuocSi;

import cm.ConnectToDatabase;
import cm.ConnectToServer;
import cm.controller.DangNhap.DangNhapController;
import cm.model.BenhNhan;
import cm.model.KeDonThuoc;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author linhsan
 */
public class TiepNhanController implements Initializable {
    @FXML
    private TableView<KeDonThuoc> DonThuocTable;
    @FXML
    private Label lblHoTen;
    @FXML
    private TableColumn colTenThuoc;
    @FXML
    private TableColumn colSoLuong;
    @FXML
    private TableColumn colGiaThuoc;
    @FXML
    private ComboBox<String> cbSearch;
    @FXML
    private TextField tfFilter;
    @FXML
    private ComboBox<String> cbSearchTime;
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
    
    private ObservableList<BenhNhan> BenhNhanData = FXCollections.observableArrayList();
    private ObservableList<KeDonThuoc> DonThuocData = FXCollections.observableArrayList();
    
    
    private ConnectToServer con;
    private BenhNhan bnselected;
    private int maPK;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            
            cbSearchInit();
            addBenhNhanData();
            initTable();
        } catch (SQLException ex) {
            Logger.getLogger(TiepNhanController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    private void cbSearchInit() {
        cbSearchTime.getItems().addAll("Hôm Nay" , "Tất Cả");
        cbSearchTime.setValue("Tất Cả");
        cbSearch.getItems().addAll("Mã","Họ Tên");
        cbSearch.setValue("Họ Tên");
    }
    
    public void addBenhNhanData() throws SQLException{
        //hien thi benh nhan theo thu tu co thoi gian kham gan nhat
        String sql = "SELECT * FROM Benh_Nhan natural join (select * from Phien_Kham order by Thoi_Gian_Kham desc) as pk "
                        + "where Trang_Thai_BN = 'phòng thuốc' "
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
    public void initTable(){
        MaColumn.setCellValueFactory(new PropertyValueFactory<>("Ma"));
        TenColumn.setCellValueFactory(new PropertyValueFactory<>("HoTen"));
        NgaySinhColumn.setCellValueFactory(new PropertyValueFactory<>("NgaySinh"));
        GioiTinhColumn.setCellValueFactory(new PropertyValueFactory<>("GioiTinh"));
        ThoiGianColumn.setCellValueFactory(new PropertyValueFactory<>("ThoiGian"));
        PhoneColumn.setCellValueFactory(new PropertyValueFactory<>("Phone"));
        
        
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
                (observable, oldValue, newValue) -> showDonThuoc(newValue));
        
        
    }
    private void showDonThuoc(BenhNhan benhnhan){
        bnselected = benhnhan;
        if (benhnhan != null){
            try {
                con = new ConnectToServer();
                
                
                String sql = "select Ma_Phien_Kham from Phien_Kham where Ma_Benh_Nhan = '"+ benhnhan.getMa()
                        +"' order by Thoi_Gian_Kham desc limit 1 ";
                con.sendToServer(sql);
                while(true) {
                    Object ob =con.receiveFromServer();
                    /*
                    * if returned object is not CacheRowSet, inform and end the loop
                    * else use CacheRowSet and end the loop
                   */
                   if (ob.getClass().toString().equals("class java.lang.String")) {
                       break;
                   }
                   else{
                       CachedRowSet crs = (CachedRowSet)ob;
                       crs.next();
                       maPK = crs.getInt("Ma_Phien_Kham");
                       break;
                   }
                }
                con.sendToServer("done");
                addDonThuoc(maPK);
                initTableThuoc();
                lblHoTen.setText(bnselected.getHoTen());
                

            } catch (SQLException ex) {
                Logger.getLogger(TiepNhanController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            DonThuocData.clear();
        }
        
        
    }
    private void initTableThuoc(){
        
        colTenThuoc.setCellValueFactory(new PropertyValueFactory<>("tenThuoc"));
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuongKe"));
        colGiaThuoc.setCellValueFactory(new PropertyValueFactory<>("chiPhiThuoc"));
        
        DonThuocTable.setItems(DonThuocData);
    }
    private void addDonThuoc(int maPhienKham){
        try {
            con = new ConnectToServer();
            String sql = "select Thuoc.Ma_Thuoc as Ma_Thuoc, So_Luong , Ten_Thuoc, So_Luong_Ke , (So_Luong_Ke*Gia_Thuoc) as Chi_Phi_Thuoc "
                    + "from Don_Thuoc ,Thuoc "
                    + "where Ma_Phien_Kham = '"
                    + maPhienKham+"' and Don_Thuoc.Ma_Thuoc = Thuoc.Ma_Thuoc";
            DonThuocData.clear();
            
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
                    while (crs.next()){
                        KeDonThuoc thuoc = new KeDonThuoc();
                        thuoc.setMa(crs.getInt("Ma_Thuoc"));
                        thuoc.setTenThuoc(crs.getString("Ten_Thuoc"));
                        thuoc.setSoLuongKe(crs.getInt("So_Luong_Ke"));
                        thuoc.setSoLuong(crs.getInt("So_Luong"));
                        thuoc.setChiPhiThuoc(crs.getFloat("Chi_Phi_Thuoc"));
                        DonThuocData.add(thuoc);

                    }
                    break;
                }
            }
            con.sendToServer("done");
            
        } catch (SQLException ex) {
            Logger.getLogger(TiepNhanController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    @FXML
    private void GiaoThuoc(ActionEvent event) throws SQLException {
        if (bnselected != null){
            Alert alert = new Alert(AlertType.CONFIRMATION , "Xác Nhận Giao Thuốc?" ,ButtonType.YES ,ButtonType.NO);
            alert.setTitle("Xác Nhận");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES){
                con =new ConnectToServer();
                String sql = "update Benh_Nhan set Trang_Thai_BN = 'thanh toán' where Ma_Benh_Nhan = '"+bnselected.getMa()+"'";
                
                con.sendToServer(sql);
              
                sql = "update Don_Thuoc set Ten_Dang_Nhap = '" +  DangNhapController.getTenDangNhap() + "' "
                        + "where Ma_Phien_Kham = '" + maPK + "'" ;
                con.sendToServer(sql);

                

                DonThuocData.forEach(thuoc -> {
                    String sql2 = "update Don_Thuoc natural join Phien_Kham set Chi_Phi_Thuoc = '"
                            +thuoc.getChiPhiThuoc()
                            +"' where Ma_Benh_Nhan = '"
                            + bnselected.getMa()
                            +"'";
                    con.sendToServer(sql2);
                    int soLuong= thuoc.getSoLuong() - thuoc.getSoLuongKe();
                    String sql3 = "update Thuoc set So_Luong = '"+ soLuong
                            +"'where Ma_Thuoc = '"
                            + thuoc.getMaThuoc()+"'";
                    con.sendToServer(sql3);
                });
                con.sendToServer("done");
                addBenhNhanData();
            }   
        }
        else{
            Alert alert2 = new Alert(AlertType.ERROR ,"Chưa Chọn Bệnh Nhân" , ButtonType.OK);
            alert2.showAndWait();
        }
        
    }

    @FXML
    private void Huy(ActionEvent event) {
        if (bnselected != null){
            try {
                Alert alert = new Alert(AlertType.CONFIRMATION , "Xác Nhận Hủy Đơn Thuốc?" , ButtonType.YES , ButtonType.NO);
                alert.setTitle("Xác Nhận");
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES){
                    con = new ConnectToServer();
                    String sql = "update Benh_Nhan set Trang_Thai_BN = 'thanh toán' where Ma_Benh_Nhan = '"+bnselected.getMa() +"'";
                    
                    con.sendToServer(sql);
                    sql = "update Don_Thuoc set Ten_Dang_Nhap = '" +  DangNhapController.getTenDangNhap() + "'"
                            + "where Ma_Phien_Kham = '" + maPK + "'";
                    con.sendToServer(sql);
                    con.sendToServer("done");
                    addBenhNhanData();
                }

            } catch (SQLException ex) {
                Logger.getLogger(TiepNhanController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            Alert alert2 = new Alert(AlertType.ERROR ,"Chưa Chọn Bệnh Nhân" , ButtonType.OK);
            alert2.showAndWait();
        }
    }
    
}
