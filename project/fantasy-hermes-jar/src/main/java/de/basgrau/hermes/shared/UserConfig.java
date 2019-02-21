package de.basgrau.hermes.shared;

public class UserConfig {

	private String humanClientID;
	private String sendungsNummern;

	public UserConfig() {
		this.sendungsNummern = "empty";
	}
	
	
	public String getHumanClientID() {
		return humanClientID;
	}

	public void setHumanClientID(String humanClientID) {
		this.humanClientID = humanClientID;
	}

	public String getSendungsNummern() {
		return sendungsNummern;
	}

	public void setSendungsNummern(String sendungsNummern) {
		this.sendungsNummern = sendungsNummern;
	}

	@Override
	public String toString() {
		return "UserConfig [humanClientID=" + humanClientID + ", sendungsNummern=" + sendungsNummern + "]";
	}

}
