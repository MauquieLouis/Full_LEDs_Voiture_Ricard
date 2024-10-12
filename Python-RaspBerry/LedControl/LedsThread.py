#!/usr/bin/env python3
# rpi_ws281x library strandtest example
# Author: Tony DiCola (tony@tonydicola.com)
#
# Direct port of the Arduino NeoPixel library strandtest example.  Showcases
# various animations on a strip of NeoPixels.

import time
from rpi_ws281x import *
import argparse

# LED strip configuration:
LED_COUNT      = 30      # Number of LED pixels.
LED_PIN        = 19      # GPIO pin connected to the pixels (18 uses PWM!).
#LED_PIN        = 10      # GPIO pin connected to the pixels (10 uses SPI /dev/spidev0.0).
LED_FREQ_HZ    = 800000  # LED signal frequency in hertz (usually 800khz)
LED_DMA        = 10      # DMA channel to use for generating signal (try 10)
LED_BRIGHTNESS = 80     # Set to 0 for darkest and 255 for brightest
LED_INVERT     = False   # True to invert the signal (when using NPN transistor level shift)
LED_CHANNEL    = 1       # set to '1' for GPIOs 13, 19, 41, 45 or 53



# Define functions which animate LEDs in various ways.
def colorWipe(strip, color, wait_ms=50):
    """Wipe color across display a pixel at a time."""
    for i in range(strip.numPixels()):
        strip.setPixelColor(i, color)
        strip.show()
        time.sleep(wait_ms/1000.0)

def theaterChase(strip, color, wait_ms=50, iterations=10):
    """Movie theater light style chaser animation."""
    for j in range(iterations):
        for q in range(3):
            for i in range(0, strip.numPixels(), 3):
                strip.setPixelColor(i+q, color)
            strip.show()
            time.sleep(wait_ms/1000.0)
            for i in range(0, strip.numPixels(), 3):
                strip.setPixelColor(i+q, 0)

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

def rainbow(strip, messageHandler, wait_ms=0.01, iterations=5):
    """Draw rainbow that fades across all pixels at once."""
    wait_ms = float(messageHandler.interiorTimer)
    for j in range(256*iterations):
        wait_ms = float(messageHandler.interiorTimer)
        for i in range(strip.numPixels()):
            wait_ms = float(messageHandler.interiorTimer)
            strip.setPixelColor(i, wheel((i+j) & 255))
        strip.show()
        if(messageHandler.interiorFunction != "Degrade"):
            print("OUT DEGRADE ")
            return
        time.sleep(wait_ms)

def rainbowCycle(strip, messageHandler, wait_ms=0.01, iterations=5):
    """Draw rainbow that uniformly distributes itself across all pixels."""
    wait_ms = float(messageHandler.interiorTimer)
    for j in range(256*iterations):
        wait_ms = float(messageHandler.interiorTimer)
        for i in range(strip.numPixels()):
            wait_ms = float(messageHandler.interiorTimer)
            strip.setPixelColor(i, wheel((int(i * 256 / strip.numPixels()) + j) & 255))
        strip.show()
        if(messageHandler.interiorFunction != "DegradeRainow"):
            print("OUT DEGRADE RAINBOW")
            return
        time.sleep(wait_ms)

def theaterChaseRainbow(strip, messageHandler, wait_ms=50):
    """Rainbow movie theater light style chaser animation."""
    wait_ms = float(messageHandler.interiorTimer)
    for j in range(256):
        wait_ms = float(messageHandler.interiorTimer)
        for q in range(3):
            wait_ms = float(messageHandler.interiorTimer)
            for i in range(0, strip.numPixels(), 3):
                wait_ms = float(messageHandler.interiorTimer)
                if(messageHandler.interiorFunction != "Stars"):
                    print("OUT THEATERCHASERAINBOW")
                    return
                strip.setPixelColor(i+q, wheel((i+j) % 255))
            strip.show()
            time.sleep(wait_ms)
            for i in range(0, strip.numPixels(), 3):
                wait_ms = float(messageHandler.interiorTimer)
                strip.setPixelColor(i+q, 0)
                
def MakeColorFromString(colorString):
    colors = colorString.split("/")
    red = int(colors[0])
    green = int(colors[1])
    blue = int(colors[2])
    return Color(red,green,blue)

def changeColor(strip, colorString):
    colors = colorString.split("/")
    red = int(colors[0])
    green = int(colors[1])
    blue = int(colors[2])
    for i in range(0, strip.numPixels()):
        strip.setPixelColor(i,Color(red, green, blue))
    strip.show()
    #time.sleep(0.1)
#======================= S L I D E   O N E   C O L O R ========================#
def SlideOneColor(strip, messageHandler):
    #Color must be of type String "red/green/blue"
    colors1  = MakeColorFromString(messageHandler.interiorColor1)
    SlideSize = int(messageHandler.interiorSlideSize)
    SleepMs= float(messageHandler.interiorTimer)
    for i in range(SlideSize, strip.numPixels()):
        SlideSize = int(messageHandler.interiorSlideSize)
        SleepMs= float(messageHandler.interiorTimer)
        if(i>=SlideSize):
            strip.setPixelColor(i-SlideSize, Color(0,0,0))
        strip.setPixelColor(i, colors1)
        if(i==strip.numPixels()-1):
            for j in range(strip.numPixels()-SlideSize, strip.numPixels()):
                strip.setPixelColor(j, Color(0,0,0))
                strip.setPixelColor(j-(strip.numPixels()-SlideSize), colors1)
                strip.show()
                time.sleep(SleepMs)
                
        time.sleep(SleepMs)
        if(messageHandler.interiorFunction != "Slide1"):
            print("OUT SLIDE 1")
            return
        strip.show()
        
#===================== S L I D E   T W O   C O L O R S =======================#
def SlideTwoColors(strip, messageHandler):
    colors1  = MakeColorFromString(messageHandler.interiorColor1)
    colors2  = MakeColorFromString(messageHandler.interiorColor2)
    SlideSize = int(messageHandler.interiorSlideSize)
    SleepMs= float(messageHandler.interiorTimer)
    for i in range(SlideSize, strip.numPixels()):
        SlideSize = int(messageHandler.interiorSlideSize)
        SleepMs= float(messageHandler.interiorTimer)
        if(i>=SlideSize):
            strip.setPixelColor(i-SlideSize, Color(0,0,0))
        strip.setPixelColor(i, colors1)
        strip.setPixelColor(i-(int(SlideSize/2)),colors2)
        if(i==strip.numPixels()-1):
            for j in range(strip.numPixels()-SlideSize, strip.numPixels()):
                SlideSize = int(messageHandler.interiorSlideSize)
                SleepMs= float(messageHandler.interiorTimer)
                strip.setPixelColor(j, Color(0,0,0))
                strip.setPixelColor(j-(strip.numPixels()-SlideSize), colors1)
                strip.show()
                time.sleep(SleepMs)
                
        time.sleep(SleepMs)
        if(messageHandler.interiorFunction != "Slide2"):
            print("OUT SLIDE 2")
            return
        strip.show()
#===================== SNAKE   T W O   C O L O R S =======================#
def SnakeTwoColors(strip, messageHandler):
    colors1  = MakeColorFromString(messageHandler.interiorColor1)
    colors2  = MakeColorFromString(messageHandler.interiorColor2)
    #SlideSize doit être un multiple de strip.numPixels() / strip.numPixels() = 180 normalement
    SlideSize = int(messageHandler.interiorSlideSize)
    SleepMs= float(messageHandler.interiorTimer)
    colors = [colors1, colors2]
    nbSnake = int(strip.numPixels()/SlideSize)
    for i in range(0, SlideSize+1):
        SleepMs= float(messageHandler.interiorTimer)
        for j in range(0, nbSnake):
            if(j%2 == 0):
                strip.setPixelColor(i+(j*SlideSize), colors[0])
            else:
                strip.setPixelColor(i+(j*SlideSize), colors[1])
        time.sleep(SleepMs)
        strip.show()
    for i in range(0, SlideSize+1):
        SleepMs= float(messageHandler.interiorTimer)
        for j in range(0, nbSnake):
            if(j%2 == 0):
                strip.setPixelColor(i+(j*SlideSize), colors[1])
            else:
                strip.setPixelColor(i+(j*SlideSize), colors[0])
        time.sleep(SleepMs)
        strip.show()

        
def police(strip):
    for i in range(0,500):
        changeColorPolice(strip, "0/0/180");
        time.sleep(0.08)
        changeColorPolice(strip, "0/0/0");
        time.sleep(0.08)
        changeColorPolice(strip, "0/0/180");
        time.sleep(0.08)
        changeColorPolice(strip, "0/0/0");
        time.sleep(0.3)
    
#============================================ TURN OFF FUNCTION ===================================#
def turnOff(strip):
    for i in range(0, strip.numPixels()):
        strip.setPixelColor(i,Color(0,0,0))
    strip.show()         
    
                
def MainLedProgram(messageHandler):
    print("Main Program")
# Main program logic follows:
    #if __name__ == '__main__':
    print("Main Program Inside")
    # Process arguments
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
        #changeColor(strip, messageHandler.interiorColor)
        while True:
            #print("In While Interior")
            #time.sleep(0.1)
            #for i in range(0, strip.numPixels()):
            #    strip.setPixelColor(i,Color(150,0,150))
            #    strip.show()   
            #--------------COULEUR SIMPLE-----------------#
            if(messageHandler.interiorFunction == "SimpleColor"):
               changeColor(strip, messageHandler.interiorColor1)
            #---------------------------------------------#
            #--------------SLIDE 1 SIMPLE-----------------#
            if(messageHandler.interiorFunction == "Slide1"):
               SlideOneColor(strip, messageHandler)
            #---------------------------------------------#
            #--------------SLIDE 2 SIMPLE-----------------#
            if(messageHandler.interiorFunction == "Slide2"):
               SlideTwoColors(strip, messageHandler)
            #---------------------------------------------#
            #--------------SNAKE 2 SIMPLE-----------------#
            if(messageHandler.interiorFunction == "Snake2"):
               SnakeTwoColors(strip, messageHandler)
            #---------------------------------------------#
            #-------------RAINBOWS SIMPLE-----------------#
            if(messageHandler.interiorFunction == "DegradeRainow"):
               rainbowCycle(strip, messageHandler)
            #---------------------------------------------#
            #--------------RAINBOW SIMPLE-----------------#
            if(messageHandler.interiorFunction == "Degrade"):
               rainbow(strip, messageHandler)
            #---------------------------------------------#
            #--------------- STARS SIMPLE-----------------#
            if(messageHandler.interiorFunction == "Stars"):
                theaterChaseRainbow(strip, messageHandler)
            #---------------------------------------------#
            #---------------TURN OFF LEDS-----------------#
            if(messageHandler.interiorFunction == "TurnOff"):
                turnOff(strip)
    except KeyboardInterrupt:
        if args.clear:
            colorWipe(strip, Color(0,0,0), 10)


#MainLedProgram()
