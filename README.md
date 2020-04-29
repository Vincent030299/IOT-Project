# IOT-Project

## Project uitleg
Tijdens dit keuzevak periode ben ik aan de slag gegaan met de raspberry pi en de DS18B20 temperatuur sensor.
Het idee is dat ik alles kan testen of iets te warm is of te koud. De sensor heb ik gekozen omdat je het bijna overal kan gebruiken sinds het waterproof is. De werking gaat als volgt. De temperatuur komt van de sensor, wordt d.m.v. python door gestuurd naar thingspeak (elke 5 minuten) en dat wordt via een API call opgevraagd in de app. Hierbij kan je aangeven wat de min/max temperatuur is. Er word een GET request gestuurd elke 15 minuten door de app om te kijken of het te warm of te koud is. Stel het is 1 van de twee dingen dan krijgt de gebruiker een notificatie op het scherm. 
Hieronder de uitleg van mijn pipe-line:

![Alt Text](Images/Data%20pipeline/data_pipeline.png)

## Wat heb je nodig
1. Raspberry PI 4
1. Jumpwires (M to M, F to M)
1. DS18B20 temp sensor
1. Breadboard
1. thingspeak channel
1. Android telefoon
1. Python
1. Internet
1. 1x 4.7 k ohm resistor

## IOT sensor en apparaat
Eerst ben ik gaan uitzoeken hoe ik de raspberry moest aan sluiten. Daarna ben ik gaan zoeken naar welke sensor/jumpwires/weerstanden ik nodig had. Na het aanschaffen van alle punten die hierboven staan ben ik de raspberry d.m.v. van de GPIO pins en de breadboard pins de link te leggen voor de temp sensor. Ik heb hier een diagram voor gebruikt en dat is:
![ALT_Text](https://farm5.staticflickr.com/4215/35139160190_cea3435a09_b_d.jpg)

Mijn uitwerking:
![ALT Text](/Images/Raspberry%20Pi/IMG-3821.JPG)

## Temperatuur checken/uitlezen
Voor het uitlezen van de graden heb ik de python library w1thermsensor gebruikt sinds die speciaal is gemaakt voor deze sensor.
Na het uitlezen van de graden heb ik een loop gemaakt die elke 5 min een functie called. De functie haalt de temperatuur op en stuurd die door naar mijn thingspeak channel
[Python script](https://github.com/Vincent030299/IOT-Project/blob/master/Raspberry_Pi/Python%20script/temperature-sensor.py)

## Thingspeak
Thingspeak is een IOT platform waar je meerdere channels kan aanmaken om verschillende soorten van data te storen (van een sensor bijvoorbeeld die aangesloten is op een raspberry). Die je weer in bijvoorbeeld een android app kan ophalen. Nadat de functie wordt opgeroepen wordt de data gestored in een channel.
[Mijn channel](https://thingspeak.com/channels/1048437)

## Android app
Voor de android heb ik gekozen dat als je voor het eerst opstart dat de gebruiker een minimaal/maximaal temperatuur moet aangeven (dat is hetzelfde als de edit screen). Na het aangeven te hebben wordt de data uit thingspeak 1x gehaald en gestored in een database. Dit zorgt ervoor dat er niet een API call hoef te gebeuren als je elke keer de app opstart. Als de app niet open is wordt er elke 15 minuten een call naar thingspeak gedaan om te kijken of de temperatuur actueel is. Als het te warm of te koud is krijgt de gebruiker een notificatie of het te warm of te koud is.
[Android Project/APK](https://github.com/Vincent030299/IOT-Project/tree/master/Android)

### Home screen
![](https://github.com/Vincent030299/IOT-Project/blob/master/Images/App/home_screen_app.png)
### Edit screen
![](https://github.com/Vincent030299/IOT-Project/blob/master/Images/App/edit_low_high_app.png)
### Te warm notificatie
![](https://github.com/Vincent030299/IOT-Project/blob/master/Images/App/too_hot_app.png)
### Te koud notificatie
![](https://github.com/Vincent030299/IOT-Project/blob/master/Images/App/too_cold_app.png)
