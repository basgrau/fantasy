package de.basgrau.fantasy.human.fx;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HumanFX extends Application {

	@Override
	public void start(Stage stage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));

			Scene scene = new Scene(root, 1280, 850);

			stage.setTitle("Human FX (Der menschlische Client)");
			stage.setScene(scene);
			stage.centerOnScreen();

			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
