Um die Prototypen zu testen, können die docker-compose-Dateien verwendet werden.
Es müssen keine Docker-Images selbst gebaut werden, diese sind bereits erstellt und auf Docker-Hub gepusht und werden durch Docker-Compose automiatisch gepulled.

Um die SSE-Prototypen zu testen kann das Tool "Postman" verwendet werden.
Dafür ist in diesem Repository eine Postman-Collection vorhanden, die Postman importiert werden kann.

Um die RSocket-Prototypen zu testen kann die selbstimplementierte Testanwendung "rsocket-client" verwendet werden.

Um den MQTT-Prototyp zu testen kann ein kosteloser MQTT-Client wie MQTT.fx (https://softblade.de/download/) verwendet werden.
