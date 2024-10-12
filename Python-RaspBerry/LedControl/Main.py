#!/usr/bin/env python

import os
import glob
import time
import random
import threading
import subprocess as sp

from queue import Queue
from bluetooth import *


#----------OTHER FILES IMPORT------------#
import BluetoothThread
from BluetoothThread import thread_reading_message
#--------------- IMPORTATION LEDs THREADs ----------------#
import LedsThread
from LedsThread import MainLedProgram


import LedsThreadExterior
from LedsThreadExterior import MainLedProgramExterior

#import LedsThreadExteriorCalandre
#from LedsThreadExteriorCalandre import MainLedProgramExteriorCalandre
#---------------------------------------------------------#

class MessageHandler:
    error=False                 #Pour signaler d'éventuelles erreur lors de la réception de message
    #Attributs Intérieurs   =====================================================#  
    interiorType = "None"           #A changer par un fonction animation allumage
    interiorFunction = "SimpleColor"
    interiorCurrentFunction = "Allumage"    #Ajouter animation allumage
    interiorColor1 = "0/0/127"
    interiorColor2 = "0/0/127"
    interiorTimer = 0.08
    interiorSlideSize = 10
    
    #Attributs Extérieurs   =====================================================#
    exteriorType = "None"      #A changer par un fonction animation allumage
    exteriorFunction = "SimpleColor"
    exteriorColor1 = "0/0/127"
    exteriorColor2 = "0/0/127"
    exteriorTimer = 0.08
    exteriorSlideSize = 10

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

#-------------------CREATING OBJECT SHARED BETWEEN THREAD-----------------------#
messageHandler = MessageHandler()

#---------------------------STARTING THREADS------------------------------------#

ledThread = threading.Thread(target= MainLedProgram, args=[messageHandler])
ledThread.daemon = True
ledThread.start()

ledThreadExte = threading.Thread(target = MainLedProgramExterior, args=[messageHandler])
ledThreadExte.daemon = True
ledThreadExte.start()

#ledThreadExteCal = threading.Thread(target = MainLedProgramExteriorCalandre, args=[messageHandler])
#ledThreadExteCal.daemon = True
#ledThreadExteCal.start()
#------------------------------------------------------------------------------#

stdoutdata1 = sp.getoutput("hcitool con")

while True:          
    print ("Waiting for connection on RFCOMM channel %d" % port)
    stdoutdata1 = sp.getoutput("hcitool con")

    client_sock, client_info = server_sock.accept()
    print ("Accepted connection from "), client_info

    try:
        client_sock.getpeername()
        still_connected = True
    except:
        still_conndeezedeected = False

    print(still_connected)

    if client_sock:
        print(client_sock.getpeername())
        reader = threading.Thread(target=thread_reading_message, args=(client_sock,messageHandler,))
        if not reader.is_alive():
            reader.start()
            
