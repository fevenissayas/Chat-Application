package org.example.chatapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;

public class Client4 extends Application {
    private PrintWriter out;
    private BufferedReader in;
    private String username;


    @Override
    public void start(Stage primaryStage) throws Exception {

        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(10);
        root.setStyle("-fx-background-color: rgb(255,255,255);");

        Scene scene = new Scene(root, 700, 600);
        primaryStage.setTitle("Client2");
        primaryStage.setScene(scene);
        primaryStage.show();


        Label title = new Label("ChatApp");
        title.setStyle("-fx-font-family:'Arial Black'; -fx-text-fill: Black; -fx-font-size:30px;");

        TextArea chat_Area = new TextArea();
        chat_Area.setPrefHeight(600);
        chat_Area.setStyle("-fx-control-inner-background: rgb(42,170,138); -fx-text-fill: white;" +
                "-fx-font-family: 'Arial'; -fx-font-size: 14px;");
        chat_Area.setEditable(false);
        chat_Area.setWrapText(true);


        TextField input_Message = new TextField();
        input_Message.setPromptText("Write a message...");
        input_Message.setPrefWidth(550);
        input_Message.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");


        Button button_send = new Button("Send");
        button_send.setStyle("-fx-background-color: rgb(45,210,189); -fx-text-fill: white;" +
                " -fx-font-family: 'Arial'; -fx-font-size: 14px;");

        connectToServer();

        HBox input_Box = new HBox(10, input_Message, button_send);

        root.getChildren().addAll(title,chat_Area, input_Box);


        button_send.setOnAction(event -> sendMessage(input_Message, chat_Area));
        input_Message.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage(input_Message, chat_Area);
            }
        });


        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    String final_message = message;
                    Platform.runLater(() -> chat_Area.appendText(final_message + "\n"));
                }
            } catch (IOException e) {
                System.out.println("Error receiving message");
                e.printStackTrace();
            }
        }).start();
    }

    private void connectToServer() throws IOException {
        try {
            Socket socket = new Socket("localhost", 4000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            TextInputDialog username_asker = new TextInputDialog();
            username_asker.setTitle("Username");
            username_asker.setHeaderText("Enter your username:");
            username_asker.setContentText("Username:");
            username = username_asker.showAndWait().orElse("Unknown");
            out.println(username);
        }
        catch(IOException e){
            System.out.println("Error connecting to server");
        }
    }

    private void sendMessage(TextField inputMessage, TextArea chatArea) {
        String message = inputMessage.getText();
        if (!message.isEmpty()) {
            out.println(message);
            inputMessage.clear();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}

