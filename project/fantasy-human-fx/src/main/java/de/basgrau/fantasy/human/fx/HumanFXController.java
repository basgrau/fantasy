package de.basgrau.fantasy.human.fx;

import java.net.UnknownHostException;
import java.util.ResourceBundle;

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
import javafx.scene.control.TextArea;
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
	private TextArea txtGoetterNachrichtText;
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
	public HumanFXController() {
		// Init UserConfig
		setUserConfig();

		// Output Client ID
		System.out.println(userConfig.getHumanClientID());
	}

	/**
	 * setUserConfig.
	 * 
	 */
	private void setUserConfig() {
		userConfig = new UserConfig();
		String humanId = "defaultClientId";
		
        try {
        	ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getPackageName().toString()+".config");
            humanId = bundle.getString("humanid");
        } catch (Exception e) {
			System.err.println("Properties 'config.properties' nicht gefunden.");
			System.err.println(e.getMessage());
		}
        
		userConfig.setHumanClientID(humanId);
		userConfig.setSendungsNummern("empty");
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

		// Set GoetterNachricht
		txtGoetterNachrichtText.setText(generateXMLNachrichtTemplate(userConfig.getHumanClientID()));
		
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
	 * generateXMLNachrichtTemplate.
	 * @param humanClientID Id
	 * @return Xml Template
	 */
	private String generateXMLNachrichtTemplate(String humanClientID) {
		StringBuilder sb = new StringBuilder();
		sb.append("<goetternachricht>\n");
		sb.append("<steuerung>\n");
		sb.append(" <user>"+humanClientID+"</user>\n");
		sb.append("<absender>HFX</absender>\n");
		sb.append("<empfaenger>ZEUS</empfaenger>\n");
		sb.append(" </steuerung>\n");
		sb.append(" <daten>\n");
		sb.append(" <gebet id=\"0\">WOHNORT</gebet>\n");
		sb.append(" <gebet id=\"1\">ERFOLG</gebet>\n");
		sb.append(" <gabe id=\"0\">Reis</gabe>\n");
		sb.append(" <gabe id=\"1\">Wein</gabe>\n");
		sb.append("</daten>\n");
		sb.append("</goetternachricht>");
		
		return sb.toString();
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
