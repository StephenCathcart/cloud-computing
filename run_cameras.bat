@echo off
SET JAR=./smart-speed-camera/target/smart-speed-camera-0.1.0.jar
start "SmartSpeedCamera1" java -jar %JAR% --server.port=8090 jms.clientid="ssc1"
start "SmartSpeedCamera2" java -jar %JAR% --server.port=8091 --app.uid="DUR-ALBST-01" --app.street="Albert Street" --app.town="Durham" --app.maxspeedlimit=40 --jms.clientid="ssc2"
start "SmartSpeedCamera3" java -jar %JAR% --server.port=8092 --app.uid="MOR-NEWST-01" --app.street="Newgate Street" --app.town="Morpeth" --app.maxspeedlimit=20 --jms.clientid="ssc3"
start "SmartSpeedCamera4" java -jar %JAR% --server.port=8093 --app.uid="ALN-GRNRD-01" --app.street="Greenwell Road" --app.town="Alnwick" --app.maxspeedlimit=50 --jms.clientid="ssc4"
start "SmartSpeedCamera5" java -jar %JAR% --server.port=8094 --app.uid="CON-PEMRD-01" --app.street="Pemberton Road" --app.town="Consett" --app.maxspeedlimit=70 --jms.clientid="ssc5"
start "SmartSpeedCamera6" java -jar %JAR% --server.port=8095 --app.uid="NCL-GRLRD-01" --app.street="Great Lime Road" --app.town="Newcastle" --app.maxspeedlimit=20 --jms.clientid="ssc6"
start "SmartSpeedCamera7" java -jar %JAR% --server.port=8096 --app.uid="NCL-GRLRD-02" --app.street="Great Lime Road" --app.town="Newcastle" --app.maxspeedlimit=20 --jms.clientid="ssc7"
pause