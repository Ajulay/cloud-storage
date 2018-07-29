package com.cloud.storage.client;

import com.cloud.storage.common.nets.MyNet;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ClientMain extends Application {
    private Stage stage;
    private Scene basicScene;

    public Stage getStage() {
        return stage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view.fxml"));
        Parent root = loader.load();
        basicScene = new Scene(root);

        showPrimaryScene(primaryStage);

        Controller controller = loader.getController();
        controller.setMainApps(this);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                stage.close();
                MyNet.close();
            }
        });

    }

    public void showPrimaryScene(Stage primaryStage) {
        primaryStage.setTitle("Cloud Storage Client");
        primaryStage.setScene(basicScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


}
