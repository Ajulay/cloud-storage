package com.cloud.storage.client;

import com.cloud.storage.common.TransmitClass;
import com.cloud.storage.common.nets.MyNet;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

import static com.cloud.storage.common.UtilConstants.REG_INFO;

public class RegController {


  ClientMain mainApps;
  TransmitClass tc;
  Controller controller;

  @FXML
  Button bnCancel;

  @FXML
  Button bnOk;

  @FXML
  TextField tfLogin;

  @FXML
  PasswordField pfPass1;

  @FXML
  PasswordField pfPass2;

  @FXML
  Label lblResult;

  private boolean bLoginHasText = false;
  private boolean bPass1HasText = false;
  private boolean bPass2HasText = false;

  public void setMainApps(ClientMain mainApps) {
    this.mainApps = mainApps;
  }

  public ClientMain getMainApps() {
    return mainApps;
  }

  public void setController(Controller controller) {
    this.controller = controller;
  }
  public void initialize(){
    tfLogin.textProperty().addListener(new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (newValue.length()>0)
              bLoginHasText = true;
            else
              bLoginHasText = false;

            isDataOk();
        }

    });
    pfPass1.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (newValue.length()>0)
          bPass1HasText = true;
        else
          bPass1HasText = false;

        isDataOk();
      }

    });

    pfPass2.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (newValue.length()>0)
          bPass2HasText = true;
        else
          bPass2HasText = false;

        isDataOk();
      }
    });

    lblResult.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if(newValue.equals("/regok")){

            controller.getTfLogin().setText(tfLogin.getText());
            controller.getPfPass().setText(pfPass1.getText());
            mainApps.showPrimaryScene(mainApps.getStage());

        }
      }
    });
    bnOk.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {

        tc = new TransmitClass(tfLogin.getText() + " " + controller.getHashPass(pfPass1.getText()), REG_INFO);

        try {
          MyNet.sendData(tc);
          tc=(TransmitClass) MyNet.getObjctDecodrInstance().readObject();
          lblResult.setText(new String(tc.getBuffer()));

        } catch (IOException e) {
          System.out.println("No sending...");
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private void isDataOk(){
    if (bLoginHasText&&bPass1HasText&&bPass2HasText){
      if (pfPass1.getText().equals(pfPass2.getText())){
        bnOk.setDisable(false);
      }else
        bnOk.setDisable(true);
    }
  }

  public Label getLblResult() {
    return lblResult;
  }
}
