package de.basgrau.hermes.shared;

/**
 * GoetterNachricht.
 * 
 * @author basgrau
 *
 */
public class GoetterNachricht {
	/** . */
	private String sendungsnummer;
	/** . */
	private String humanClientId;
	/** . */
	private String text;

	public String getSendungsnummer() {
		return sendungsnummer;
	}

	public void setSendungsnummer(String sendungsnummer) {
		this.sendungsnummer = sendungsnummer;
	}

	public String getHumanClientId() {
		return humanClientId;
	}

	public void setHumanClientId(String humanClientId) {
		this.humanClientId = humanClientId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
