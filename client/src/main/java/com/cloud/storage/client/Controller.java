package com.cloud.storage.client;

import com.cloud.storage.client.util.MyFile;
import com.cloud.storage.common.TransmitClass;

import com.cloud.storage.common.nets.MyNet;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.util.Callback;

import java.io.*;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static com.cloud.storage.common.UtilConstants.*;

public class Controller {

    ClientMain mainApps;
    TransmitClass tc;
    String tmpStringForClient;
    String tmpStringForCloud;
    final String FILE_CLIENT_PLACE = "client/myFiles";
    Controller controller;
    RegController regController;
    boolean regOk = false;
    String dirName;
    Path p;
    final int CONTAINER_SIZE = 5;
   // Alert alert = new Alert(Alert.AlertType.INFORMATION);
    @FXML
    TableView<MyFile> tableView;

   @FXML
    TableColumn<MyFile, String> fileName;

   @FXML
   TableColumn<MyFile, String> fileSize;

   @FXML
    ListView<String> listView;

   @FXML
    Button bnSend;

    @FXML
    Button bnRemove;

    @FXML
    Button bnUpdate;

    @FXML
    Button bnLoadCl;

    @FXML
    Button bnUpdateCl;

    @FXML
    Button bnRemoveCl;

    @FXML
    Button bnReg;

    @FXML
    TextField tfLogin;

    @FXML
    PasswordField pfPass;

    @FXML
    Button bnAuth;

    @FXML
    Label lblLogin;

    @FXML
    Label lblPass;

    @FXML
    HBox hBoxLbls;

    @FXML
    HBox hBoxAreas;

    @FXML
    HBox hBoxBns;

    @FXML
    AnchorPane ancCloud;

    @FXML
    ImageView img;


    ObservableList<String> fileList;
    private ObservableList<MyFile> myFileData = FXCollections.observableArrayList();


   public void initialize() throws IOException {
       makeVisibleBase(false);
       // Добавление в таблицу данных из наблюдаемого списка
       tableView.setItems(getMyFileData());
       fileList = FXCollections.observableArrayList();
       tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->  {if(newValue!=null) tmpStringForClient = newValue.fileNameProperty().getValue();});
       listView.setItems(fileList);
       listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
             if(newValue!=null) {tmpStringForCloud = newValue.substring(0,newValue.indexOf(","));}
             else {
                 tmpStringForCloud=newValue;
             }
       });


       controller = this;

       upDateDataOnClient();

        bnSend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                transmitCopy(tmpStringForClient);
            }
        });

       bnRemove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                removeOnClient(tmpStringForClient);
            }
        });

       bnUpdate.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent event) {
               upDateDataFromCloud();

           }
       });

       bnLoadCl.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent event) {
               transmitMoveToClient(tmpStringForCloud);
           }
       }
       );

       bnUpdateCl.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent event) {
               upDateDataFromClient();
           }
       });

       bnRemoveCl.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent event) {

               transmitRemoveFromCloud(tmpStringForCloud);
           }
       });

       bnReg.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent event) {
               FXMLLoader loaderReg = new FXMLLoader();
               loaderReg.setLocation(getClass().getResource("/RegWindow.fxml"));
               Parent regRoot = null;
               try {
                   regRoot = loaderReg.load();
               } catch (IOException e) {
                   e.printStackTrace();
               }
               mainApps.getStage().setTitle("Cloud Storage Client:Registration");
               mainApps.getStage().setScene(new Scene(regRoot));
               regController = loaderReg.getController();
               regController.setMainApps(mainApps);
               regController.setController(controller);
               mainApps.getStage().show();
           }
       });

       bnAuth.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent event) {
               StringBuilder sb = new StringBuilder();
               sb.append(tfLogin.getText());
               sb.append(" ");
               long hashPass = getHashPass(pfPass.getText());

               sb.append(hashPass);
               connectAuth(sb.toString());
               try {
                   tc = MyNet.getTC();

                   if (new String(tc.getBuffer()).startsWith("/authok ")) {
                   dirName = new String(tc.getBuffer()).split("\\s")[1];
                   mainApps.getStage().setTitle(tfLogin.getText());

                   Alert a = new Alert(Alert.AlertType.INFORMATION);
                   a.initModality(Modality.APPLICATION_MODAL);
                   a.setContentText("Authorization passed...");
                   a.showAndWait();
                   makeVisibleAuth(false);
                   createThread();
                   } else {
                       Alert a = new Alert(Alert.AlertType.INFORMATION);
                       a.initModality(Modality.APPLICATION_MODAL);
                       a.setContentText("You are not passed registration...");
                       a.showAndWait();

                   }
               } catch (IOException e) {
                   e.printStackTrace();
               } catch (ClassNotFoundException e) {
                   e.printStackTrace();
               }
           }
       });

       fileName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MyFile, String>, ObservableValue<String>>() {
           @Override
           public ObservableValue<String> call(TableColumn.CellDataFeatures<MyFile, String> param) {

               return  param.getValue().fileNameProperty();
           }
       });

       fileSize.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MyFile, String>, ObservableValue<String>>() {
           @Override
           public ObservableValue<String> call(TableColumn.CellDataFeatures<MyFile, String> param) {
               StringProperty sp = new SimpleStringProperty();
               sp.set(param.getValue().getFileSizeProperty()+"");//? другой вариант не вышел
               return sp;
           }
       });

       img.fitWidthProperty().bind(ancCloud.widthProperty());
       img.fitHeightProperty().bind(ancCloud.heightProperty());

    //
   }
    private void upDateDataOnClient() throws IOException {
       if (Platform.isFxApplicationThread()) {
           myFileData.clear();
           Files.walkFileTree(Paths.get("client/myFiles"), new SimpleFileVisitor<Path>() {
               @Override
               public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                   myFileData.add(new MyFile(file.toFile().getParent() + "/" + file.toFile().getName()));
                   return FileVisitResult.CONTINUE;
               }
           });
       }else{
           Platform.runLater(new Runnable() {
               @Override
               public void run() {
                   try {
                   myFileData.clear();

                       Files.walkFileTree(Paths.get("client/myFiles"), new SimpleFileVisitor<Path>() {
                           @Override
                           public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                               myFileData.add(new MyFile(file.toFile().getParent() + "/" + file.toFile().getName()));
                               return FileVisitResult.CONTINUE;
                           }
                       });
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           });
       }
    }

    private void upDateDataFromClient(){

        for (int i = 0; i < myFileData.size(); i++) {
            transmitCopy(myFileData.get(i).getFileNameProperty());
        }
   }

        private void upDateDataFromCloud(){
            for (int i = 0; i < fileList.size(); i++) {
               transmitMoveToClient(fileList.get(i).substring(0, fileList.get(i).indexOf(",")));
            }
    }

    private void createThread() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                getNameFilesFromCloud();
                while(true){
                    try {
                        tc = (TransmitClass) MyNet.getObjctDecodrInstance().readObject();
                        if(tc.getMark()==FILE){
                            Path p = Paths.get(FILE_CLIENT_PLACE, tc.getFileName());
                            Files.write(p,tc.getBuffer());
                        upDateDataOnClient();
                        }
                        if (tc.getMark()==STRING){
                            String sss = new String(tc.getBuffer());
                            if(sss.startsWith("/filesInCloud")){
                                Platform.runLater(()->upDateCloudStorageData(sss));
                            } else{
                                Alert a = new Alert(Alert.AlertType.INFORMATION);
                                a.initModality(Modality.APPLICATION_MODAL);
                                a.setContentText(new String(tc.getBuffer()));
                                a.showAndWait();
                            }



                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        System.out.println("socket close");
                        break;
                    }
                }
            }
        });
       // t.setDaemon(true);
        t.start();
    }

    private void makeVisibleAuth(boolean b) {
       lblLogin.setVisible(b);
       lblLogin.setManaged(b);
       tfLogin.setVisible(b);
       tfLogin.setManaged(b);
       lblPass.setVisible(b);
       lblPass.setManaged(b);
       pfPass.setVisible(b);
       pfPass.setManaged(b);
       bnAuth.setVisible(b);
       bnAuth.setManaged(b);
       bnReg.setVisible(b);
       bnReg.setManaged(b);
       makeVisibleBase(!b);
    }
    private void makeVisibleBase(boolean b) {
        hBoxAreas.setVisible(b);
        hBoxAreas.setManaged(b);
        hBoxBns.setVisible(b);
        hBoxBns.setManaged(b);
        hBoxLbls.setVisible(b);
        hBoxLbls.setManaged(b);
        ancCloud.setVisible(!b);
        ancCloud.setManaged(!b);
    }



    public long getHashPass(String text) {
       char[] chars = text.toCharArray();
       long sum = 0;
        for (int i = 0; i < chars.length; i++) {
            sum+=chars[i];
        }
        return sum;
    }

    private void connectAuth(String pass)  {
        tc = new TransmitClass(pass, AUTH_INFO);
        tc.setFileName(pass.split("\\s")[0]);
        try {
            MyNet.sendData(tc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void transmitMoveToClient (String filename){
        try {
          filename = dirName+"/"+filename;
          MyNet.sendData(new TransmitClass("/requestFile " + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void transmitRemoveFromCloud (String filename){
     if (filename == null) return;
       try {
            filename = dirName + "/"+filename;
            tc = new TransmitClass("/delete " + filename);
            tc.setFileName(filename);
            MyNet.sendData(tc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeOnClient(String filename){
       Path p = Paths.get("client/myFiles/"+filename);
        try {
            Files.delete(p);
            removeMyFileAndSet(filename);
        } catch (IOException e) {
            System.out.println("Delete of " + filename + " failed...");
        }

    }
    public void getNameFilesFromCloud() {

        String s = "";
        try {
            tc = new TransmitClass("/getFiles");
            tc.setFileName(dirName);
            MyNet.sendData(tc); //service command with dirName
            tc = MyNet.getTC();
            if((s=new String(tc.getBuffer())).startsWith("/filesInCloud\n")){
               upDateCloudStorageData(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void transmitCopy (String filename)  {
        try {
        filename = dirName + "/" + filename;
        p = Paths.get("client/myFiles", filename.split("/")[1]);
        byte[] b = null;
        long fLength = p.toFile().length();

        if (fLength>CONTAINER_SIZE) {
            b = new byte[CONTAINER_SIZE];
            RandomAccessFile raf = new RandomAccessFile(p.toFile(), "r");
            int lvl = (int)fLength % CONTAINER_SIZE;  //size of last array fo transmit
            int arrLength = (int)fLength/CONTAINER_SIZE;

            for (int i = 0; i < arrLength; i++) {
                raf.seek(i * CONTAINER_SIZE);
                raf.read(b);
                if(lvl==0&&i==arrLength-1){
                    tc = new TransmitClass(p.toString(), b, i+1, true, FILE);
                    tc.setFileName(filename);}
                else{
                    tc = new TransmitClass(p.toString(), b, i + 1, FILE);
                    tc.setFileName(filename);

                }

                MyNet.sendData(tc);
            }
            if(lvl>0){
                 b = new byte[lvl];
                 raf.seek(fLength-lvl);
                 raf.read(b);
                 tc =  new TransmitClass(p.toString(), b, arrLength+1, true, FILE);
                 tc.setFileName(filename);
                 System.out.println("here..." + tc.getPart());
                 MyNet.sendData(tc);
            }
            p = null;

        } else {

                b = Files.readAllBytes(p);
                tc = new TransmitClass(p.toString(), b, 0, FILE);
                tc.setFileName(filename);
                MyNet.sendData(tc);
                p = null;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setMainApps(ClientMain mainApps) {
        this.mainApps = mainApps;

    }

    public ObservableList<MyFile> getMyFileData() {
        return myFileData;
    }

    public ObservableList<MyFile> removeMyFileAndSet(String filename) {
        for (int i = 0; i < myFileData.size(); i++) {
            if (myFileData.get(i).getFileNameProperty().equals(filename)) {
                myFileData.remove(i);
                break;
            }
        }
        return myFileData;
    }

    private void upDateCloudStorageData(String s){
        String[] array = s.split("\\n");
        fileList.clear();
        for (int i = 1; i < array.length; i++) {
            fileList.add(array[i]);
        }
    }

    public TextField getTfLogin() {
        return tfLogin;
    }

    public PasswordField getPfPass() {
        return pfPass;
    }

}
