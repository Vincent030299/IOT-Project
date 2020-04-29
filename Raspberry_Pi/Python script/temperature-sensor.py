import time
from w1thermsensor import W1ThermSensor
sensor = W1ThermSensor()
import urllib.request

# Get temp and send it to teampspeak
def getTemperature():
    temperature = sensor.get_temperature()
    thingspeakURL = "https://api.thingspeak.com/update?api_key=BBA70YI4315XKNYM&field1=" + str(temperature)
    urllib.request.urlopen(thingspeakURL)
    print("The temperature is %s celsius" % temperature)
    

while True:
    getTemperature()
    time.sleep(300)