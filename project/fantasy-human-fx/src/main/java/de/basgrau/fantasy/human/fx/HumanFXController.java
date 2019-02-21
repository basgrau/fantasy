package de.basgrau.fantasy.human.fx;

import java.net.InetAddress;
import java.net.UnknownHostException;

import de.basgrau.fantasy.human.fx.util.HumanFxUtil;
import de.basgrau.fantasy.human.fx.websocket.HerkulesWebSocketClientConnector;
import de.basgrau.fantasy.human.task.AlleSendungenUpdateTask;
import de.basgrau.fantasy.human.task.CloseWebSocketTask;
import de.basgrau.fantasy.human.task.GoetterNachrichtenUpdateTask;
import de.basgrau.fantasy.human.task.OpenWebSocketTask;
import de.basgrau.fantasy.human.task.SendWsNachrichtUpdateTask;
import de.basgrau.fantasy.human.task.SendungsNummernUpdateTask;
import de.basgrau.hermes.shared.UserConfig;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * HumanFXController.
 * 
 * @author basgrau
 *
 */
public class HumanFXController {

	private static UserConfig userConfig;
	private static HerkulesWebSocketClientConnector hWSCC;
	private ObservableList<String> wsNachrichtenListData = FXCollections.observableArrayList();
	private ObservableList<String> sendungsNummernListData = FXCollections.observableArrayList();
	private ObservableList<String> goetterNachrichtenListData = FXCollections.observableArrayList();
	private ObservableList<String> alleSendungenListData = FXCollections.observableArrayList();
	private ObservableList<String> alleVerarbeitetenSendungenListData = FXCollections.observableArrayList();

	@FXML
	private Label labelStatus;
	@FXML
	private Label labelVerarbeitung;
	@FXML
	private RadioButton rbtnStatus;
	@FXML
	private TextField txtWsNachricht;
	@FXML
	private TextField txtSendungsNummer;
	@FXML
	private TextField txtGoetterNachrichtText;
	@FXML
	private ListView<String> wsNachrichtenListView;
	@FXML
	private ListView<String> sendungsNummernListView;
	@FXML
	private ListView<String> goetterNachrichtenListView;
	@FXML
	private ListView<String> alleSendungenListView;
	@FXML
	private ListView<String> alleVerarbeitetenSendungenListView;

	/**
	 * HumanFXController.
	 * 
	 * @throws UnknownHostException Fehler
	 */
	public HumanFXController() throws UnknownHostException {
		// Init UserConfig
		userConfig = new UserConfig();
		userConfig.setHumanClientID("HumanClient" + InetAddress.getLocalHost().getHostAddress());
		userConfig.setSendungsNummern("empty");

		// Output Client ID
		System.out.println(userConfig.getHumanClientID());
	}

	@FXML
	private void initialize() {
		initUI();

		// Init Socket
		hWSCC = new HerkulesWebSocketClientConnector();
	}

	/**
	 * initUI.
	 */
	private void initUI() {
		if (rbtnStatus == null) {
			System.err.println("Is Null");
			return;
		}
		rbtnStatus.setDisable(true);
		HumanFxUtil.setWebSocketStatus(rbtnStatus, false);

		// Set WsNachrichten List / View
		setListView(wsNachrichtenListData, wsNachrichtenListView, "");

		// Set Sendungsnummer List / View
		setListView(sendungsNummernListData, sendungsNummernListView, "");

		// Set goetterNachrichten List / View
		setListView(goetterNachrichtenListData, goetterNachrichtenListView, " angenommen");

		// Set AlleSendungen List / View
		setListView(alleSendungenListData, alleSendungenListView, " 200");

		// Set AlleVerarbeitetenSendungen List / View
		setListView(alleVerarbeitetenSendungenListData, alleVerarbeitetenSendungenListView, "");

		// Init Status Label
		labelStatus.setText("Status:");

		// Start Keep WebSocket Connected
		Timeline webSocketConnectorTimeline = new Timeline(
				new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						String id = HumanFxUtil.keepSocketConnected(userConfig, hWSCC, rbtnStatus, labelStatus,
								alleVerarbeitetenSendungenListData);
						if (!"-".equals(id)) {
							wsNachrichtenListData.add("Session-ID: " + id);
						}
					}
				}));
		webSocketConnectorTimeline.setCycleCount(Timeline.INDEFINITE);
		webSocketConnectorTimeline.play();
	}

	/**
	 * setListView.
	 * 
	 * @param listData   Data
	 * @param listView   View
	 * @param filterText Filter
	 */
	private void setListView(ObservableList<String> listData, ListView<String> listView, String filterText) {
		// Init wsNachrichtenListView.
		listView.setItems(listData);
		listView.setCellFactory((list) -> {
			return new ListCell<String>() {
				@Override
				protected void updateItem(String text, boolean empty) {
					super.updateItem(text, empty);

					if (text == null || empty) {
						setText(null);
					} else {
						if (!"".equals(filterText)) {
							if (text.contains(filterText)) {
								this.setTextFill(Color.GREEN);
							} else {
								this.setTextFill(Color.ORANGE);
							}
						}

						setText(text);
					}
				}

			};
		});
	}

	@FXML
	private void handleSendWsNachrichtAction(ActionEvent event) {
		new Thread(new SendWsNachrichtUpdateTask(wsNachrichtenListData, txtWsNachricht.getText(), hWSCC)).start();
	}

	@FXML
	private void handleConnectToWebSocketAction(ActionEvent event) {
		new Thread(new OpenWebSocketTask(wsNachrichtenListData, userConfig, hWSCC, rbtnStatus, labelStatus,
				labelVerarbeitung, alleVerarbeitetenSendungenListData)).start();
	}

	@FXML
	private void handleClearWsNachrichtenListAction(ActionEvent event) {
		wsNachrichtenListData.clear();
	}

	@FXML
	private void handleSendTestNachrichtToWebSocketAction(ActionEvent event) {
		new Thread(new SendWsNachrichtUpdateTask(wsNachrichtenListData, txtWsNachricht.getText(), hWSCC)).start();
	}

	@FXML
	private void handleCloseWebSocketAction(ActionEvent event) {
		new Thread(new CloseWebSocketTask(wsNachrichtenListData, userConfig, hWSCC, rbtnStatus, labelStatus)).start();
	}

	@FXML
	private void handleSendeGoetterNachrichtAction(ActionEvent event) {
		new Thread(new GoetterNachrichtenUpdateTask(userConfig, txtGoetterNachrichtText.getText(),
				goetterNachrichtenListData)).start();
	}

	@FXML
	private void handleCheckStatusSendungsnummerAction(ActionEvent event) {
		new Thread(new SendungsNummernUpdateTask(sendungsNummernListData, txtSendungsNummer.getText(),
				userConfig.getHumanClientID())).start();
	}

	@FXML
	private void handleGetAlleSendungenAction(ActionEvent event) {
		new Thread(new AlleSendungenUpdateTask(alleSendungenListData, userConfig.getHumanClientID())).start();
	}

}
