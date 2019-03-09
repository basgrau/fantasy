# Neue Infrastruktur Sage

Ziel soll zeigen, wie eine moderne Infrastruktur aussehen sollte. Denn HTTP ist synchron. NIEMALS <b>asynchron</b>.

## Technologie
Die genutzten Technologien sind:
* JMS mit mehreren Queue, da das Pub/Sub nicht recht wollte, mit Liberty.
* WebSockets zur Benachrichtung, wenn erfolgreich
* JavaFX für den Test Client

## Namen

### Human-FX
Dies stellt den Client dar.
Gestartet wird die Anwendung ``mvn compile exec:javamvn compile exec:java``

### Herkules-WAR
Einstieg in die Kommunikation und Kontakt zum Client:
* Stellt WebServlet zum Schicken einer MDB Nachricht bereit (zum Test)
* Stellt WebServlet zum StatusCheck bereit (zum Test)
* Stellt WebSocket bereit

### Zeus-WAR
Ist die erste nachgelagerte Komponente, nicht mehr menschlich. 
* Greift auf die Queue MDBO zu Annahme zu
* und gibt Nachricht an die Queue MDBU weiter

### Hades-WAR
Die Unterwelt, das nachgelagerte Backend.
* Greift auf die MDBU zur Annahme zu
* Setzt die Session-ID der WebSocketverbindung in Map
* Aktualisiert zu der den Status

Der Status einer SessionID kann sich über die RestSchnittstelle:
``http://localhost:9124/fantasy-hades-war/unterwelt/api/status/16``
abrufen lassen, hierbei ist ``16`` die aktuelle SessionID.

Achtung in der aktuellen Umsetzung, je Client nur eine WebSocket Session!

## Probleme

### Build von Human-FX schlägt fehl
In diesem Fall ist oft das ``fantasy-hermes-jar`` nicht erfolgreich gebaut und im Repo abgelegt. Das Gleiche kann auch mit dem ``fantasy-parent`` passieren.
Lösung: ``mvn clean package install``.
 
## Ablauf

![alt overview](pics/overview.png "Übersicht")