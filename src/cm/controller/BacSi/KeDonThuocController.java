/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cm.controller.BacSi;

import cm.model.KeDonThuoc;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.NumberStringConverter;

/**
 * FXML Controller class
 *
 * @author Mai Hoang Duc
 */
public class KeDonThuocController implements Initializable {

    @FXML
    private TableView<KeDonThuoc> KeDonThuocTable;
    @FXML
    private TableColumn ColKedonTenthuoc;
    @FXML
    private TableColumn ColKedonSoluong;
    @FXML
    private TableColumn ColKedonGhichu;
    @FXML
    private Label lblShow;

    private ThuocController ThuocCtrl = ControllerMediator.getInstance().getThuocCtrl();
    public ObservableList<KeDonThuoc> Data = FXCollections.observableArrayList(ThuocCtrl.getKeDonThuocData());

    private int flag = 0;
    public int FlagData;
    public int test = 0;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ControllerMediator.getInstance().setKeDonThuocCtrl(this);
        initable();
        setEditable();

    }

    @FXML
    private void handleBtnOk(ActionEvent event) {
        Stage stage = ThuocCtrl.dStage;
        stage.hide();
    }

    private void initable() {
        ColKedonTenthuoc.setCellValueFactory(new PropertyValueFactory<>("tenThuoc"));
        ColKedonSoluong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        ColKedonGhichu.setCellValueFactory(new PropertyValueFactory<>("cachDungThuoc"));
        KeDonThuocTable.setItems(Data);

    }

    private void setEditable() {
        KeDonThuocTable.setEditable(true);
        ColKedonSoluong.setEditable(true);
        ColKedonSoluong.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter() {
        }));
        ColKedonSoluong.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<KeDonThuoc, Integer>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<KeDonThuoc, Integer> t) {
                KeDonThuoc thuoc = ((KeDonThuoc) t.getTableView().getItems().get(t.getTablePosition().getRow()));

                thuoc.setSoLuong(t.getNewValue());
            }
        });
        ColKedonGhichu.setEditable(true);
        ColKedonGhichu.setCellFactory(TextFieldTableCell.forTableColumn());
        ColKedonGhichu.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<KeDonThuoc, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<KeDonThuoc, String> t) {
                KeDonThuoc thuoc = ((KeDonThuoc) t.getTableView().getItems().get(t.getTablePosition().getRow()));

                thuoc.setCachDungThuoc(t.getNewValue());

            }
        });
    }

    public void forceRefresh() {
        new java.util.Timer().schedule(new java.util.TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    TableView<KeDonThuoc> docTab;
                    docTab = KeDonThuocTable;
                    docTab.getColumns().get(0).setVisible(false);
                    docTab.getColumns().get(0).setVisible(true);
                });
            }
        }, 50);
    }

}
