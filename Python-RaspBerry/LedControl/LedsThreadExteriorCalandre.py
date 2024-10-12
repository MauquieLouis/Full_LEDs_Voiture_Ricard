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
LED_COUNTEC      = 8     # Number of LED pixels.
LED_PINEC        = 27      # GPIO pin connected to the pixels (18 uses PWM!).
#LED_PIN        = 10      # GPIO pin connected to the pixels (10 uses SPI /dev/spidev0.0).
LED_FREQ_HZEC    = 800000  # LED signal frequency in hertz (usually 800khz)
LED_DMAEC        = 10      # DMA channel to use for generating signal (try 10)
LED_BRIGHTNESSEC = 10     # Set to 0 for darkest and 255 for brightest
LED_INVERTEC     = False   # True to invert the signal (when using NPN transistor level shift)
LED_CHANNELEC    = 0       # set to '1' for GPIOs 13, 19, 41, 45 or 53



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

def rainbow(strip, wait_ms=100, iterations=1):
    """Draw rainbow that fades across all pixels at once."""
    for j in range(256*iterations):
        for i in range(strip.numPixels()):
            strip.setPixelColor(i, wheel((i+j) & 255))
        strip.show()
        time.sleep(wait_ms/1000.0)

def rainbowCycle(strip, wait_ms=20, iterations=5):
    """Draw rainbow that uniformly distributes itself across all pixels."""
    for j in range(256*iterations):
        for i in range(strip.numPixels()):
            strip.setPixelColor(i, wheel((int(i * 256 / strip.numPixels()) + j) & 255))
        strip.show()
        time.sleep(wait_ms/1000.0)

def theaterChaseRainbow(strip, wait_ms=50):
    """Rainbow movie theater light style chaser animation."""
    for j in range(256):
        for q in range(3):
            for i in range(0, strip.numPixels(), 3):
                strip.setPixelColor(i+q, wheel((i+j) % 255))
            strip.show()
            time.sleep(wait_ms/1000.0)
            for i in range(0, strip.numPixels(), 3):
                strip.setPixelColor(i+q, 0)

def changeColor(strip, colorString):
    colors = colorString.split("/")
    red = int(colors[0])
    green = int(colors[1])
    blue = int(colors[2])
    for i in range(0, strip.numPixels()):
        strip.setPixelColor(i,Color(red, green, blue))
    strip.show()
    #time.sleep(0.1)

def police(strip):
    for i in range(0,50):
        changeColor(strip, "0/0/180");
        time.sleep(0.08)
        changeColor(strip, "0/0/0");
        time.sleep(0.08)
        changeColor(strip, "0/0/180");
        time.sleep(0.08)
        changeColor(strip, "0/0/0");
        time.sleep(0.3)
                
def MainLedProgramExteriorCalandre(messageHandler):
    print("Main Program")
# Main program logic follows:
    #if __name__ == '__main__':
    print("Main Program Inside")
    # Process arguments
    parser = argparse.ArgumentParser()
    parser.add_argument('-c', '--clear', action='store_true', help='clear the display on exit')
    args = parser.parse_args()

    # Create NeoPixel object with appropriate configuration.
    stripEC = Adafruit_NeoPixel(LED_COUNTEC, LED_PINEC, LED_FREQ_HZEC, LED_DMAEC, LED_INVERTEC, LED_BRIGHTNESSEC, LED_CHANNELEC)
    # Intialize the library (must be called once before other functions).
    stripEC.begin()

    print ('Press Ctrl-C to quit.')
    if not args.clear:
        print('Use "-c" argument to clear LEDs on exit')

    try:
        #strip.setPixelColor(1,Color(200,0,0))
        #strip.show()

        while True:
            police(stripEC)
            #changeColor(strip, messageHandler.interior)
            #print(messageHandler.interior)
            #print ('Color wipe animations.')
            #colorWipe(stripEC, Color(255, 0, 0))  # Red wipe
            #colorWipe(stripEC, Color(0, 255, 0))  # Blue wipe
            #colorWipe(stripEC, Color(0, 0, 255))  # Green wipe
            #print ('Theater chase animations.')
            #print(messageHandler.interior)
            #theaterChase(stripEC, Color(127, 127, 127))  # White theater chase
            #theaterChase(stripEC, Color(127,   0,   0))  # Red theater chase
            #theaterChase(stripEC, Color(  0,   0, 127))  # Blue theater chase            print ('Rainbow animations.')
            #rainbow(stripEC)
            #rainbowCycle(stripEC)
            #theaterChaseRainbow(stripEC)
    except KeyboardInterrupt:
        if args.clear:
            colorWipe(stripEC, Color(0,0,0), 10)


#MainLedProgram()
