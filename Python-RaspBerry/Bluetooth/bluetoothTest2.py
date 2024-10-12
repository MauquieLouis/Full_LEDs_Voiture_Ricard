#!/usr/bin/env python

import os
import glob
import time
import random
import threading

from rpi_ws281x import *
import argparse


from bluetooth import *




#---------------------LED strip configuration-------------------------#
LED_COUNT      = 64      # Number of LED pixels.
LED_PIN        = 18      # GPIO pin connected to the pixels (18 uses PWM!).
#LED_PIN        = 10      # GPIO pin connected to the pixels (10 uses SPI /dev/spidev0.0).
LED_FREQ_HZ    = 800000  # LED signal frequency in hertz (usually 800khz)
LED_DMA        = 10      # DMA channel to use for generating signal (try 10)
LED_BRIGHTNESS = 50     # Set to 0 for darkest and 255 for brightest
LED_INVERT     = False   # True to invert the signal (when using NPN transistor level shift)
LED_CHANNEL    = 0       # set to '1' for GPIOs 13, 19, 41, 45 or 53
#---------------------------------------------------------------------#

#----------------------------LED FUNCTIONS----------------------------#
def wheel(pos):
    """Generate rainbow colors across 0-255 positions."""
    if pos < 85:
        return Color(pos * 3, 255 - pos * 3, 0)
    elif pos < 170:
        pos -= 85
        return Color(255 - pos * 3, 0, pos * 3)
    else:
        pos -= 170
        return Color(0, pos * 3, 255 - pos * 3)

def rainbow(strip, wait_ms=100, iterations=1):
    """Draw rainbow that fades across all pixels at once."""
    for j in range(256*iterations):
        for i in range(strip.numPixels()):
            strip.setPixelColor(i, wheel((i+j) & 255))
        strip.show()
        time.sleep(wait_ms/1000.0)
#---------------------------------------------------------------------#

def ledModifier(in_queue):
    parser = argparse.ArgumentParser()
    parser.add_argument('-c', '--clear', action='store_true', help='clear the display on exit')
    args = parser.parse_args()

    # Create NeoPixel object with appropriate configuration.
    strip = Adafruit_NeoPixel(LED_COUNT, LED_PIN, LED_FREQ_HZ, LED_DMA, LED_INVERT, LED_BRIGHTNESS, LED_CHANNEL)
    # Intialize the library (must be called once before other functions).
    strip.begin()

    print ('Press Ctrl-C to quit.')
    if not args.clear:
        print('Use "-c" argument to clear LEDs on exit')

    try:
        #strip.setPixelColor(1,Color(200,0,0))
        #strip.show()

        while True:
            rainbow(strip)

    except KeyboardInterrupt:
        if args.clear:
            colorWipe(strip, Color(0,0,0), 10)p


def read_temp():
    return random.random()

server_sock=BluetoothSocket( RFCOMM )
server_sock.bind(("",PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

advertise_service( server_sock, "TestServer",
                   service_id = uuid,
                   service_classes = [ uuid, SERIAL_PORT_CLASS ],
                   profiles = [ SERIAL_PORT_PROFILE ], 
#                   protocols = [ OBEX_UUID ] 
                    )
def thread_reading_message(client_sock, out_queue):
    while True:
        try:
            data = client_sock.recv(1024)
            if len(data) != 0: 
                print ("received [%s]" % data)
                #print(data)
                data = data.decode("utf-8").split("b")
                print(data)
                if(data[0] == "red"):
                    #red Led
                    pass
                if(data[0] == "blue"):
                    #blue Led
                    pass
                if(data[0] == "green"):
                    #green Led
                    pass
                if(data[0] == "rainbow"):
                    #rainbow mode Led
                    pass

            #if data == 'temp': #break
            #    data = str(read_temp())+'!'
            #    client_sock.send(data)
            #else:
            #    data = 'WTF!' 
            #    client_sock.send(data)
            #print ("sending [%s]" % data)

        except IOError:
            pass

        except KeyboardInterrupt:

            print ("disconnected")

            client_sock.close()
            server_sock.close()
            print ("all done")

            #break
    


while True:          
    print ("Waiting for connection on RFCOMM channel %d" % port)

    client_sock, client_info = server_sock.accept()
    print ("Accepted connection from "), client_info

    if client_sock:
        reader = threading.Thread(target=thread_reading_message, args=(client_sock,))
        if not reader.is_alive():
            reader.start()
            
