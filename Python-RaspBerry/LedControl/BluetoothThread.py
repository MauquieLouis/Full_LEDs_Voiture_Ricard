#from LedsThread import ledModifier
import time

def thread_reading_message(client_sock, messageHandler):
    while True:
        try:
            data = client_sock.recv(1024)
            if len(data) != 0: 
                print ("received [%s]" % data)
                #print(data)
                data = data.decode("utf-8").split("b")
                print(data)
                data = data[0].split(":")
                #======================= I N T E R I E U R ======================#
                if(data[0] == "Int"):
                    messageHandler.interiorType = data[1]
                    #Switch sur le Type
                    #---------FONCTION BASIQUE------------#
                    if(messageHandler.interiorType =="Type"):
                        messageHandler.interiorFunction = data[2]
                    #---------COULEUR BASIQUE------------#
                    if(messageHandler.interiorType =="Color"):
                        if(data[2] == "Color1"):
                            messageHandler.interiorColor1 = data[3]
                        if(data[2] == "Color2"):
                            messageHandler.interiorColor2 = data[3]
                    #---------TIMER BASIQUE------------#
                    if(messageHandler.interiorType =="Timer"):
                        messageHandler.interiorTimer = data[2]
                    #---------SLIDER BASIQUE------------#
                    if(messageHandler.interiorType =="SlideSize"):
                        messageHandler.interiorSlideSize = data[2]
                #====================== E X T E R I E U R =======================#
                elif(data[0] == "Ext"):
                    messageHandler.exteriorType = data[1]
                    #Switch sur le Type
                    #---------FONCTION BASIQUE------------#
                    if(messageHandler.exteriorType =="Type"):
                        messageHandler.exteriorFunction = data[2]
                    #---------COULEUR BASIQUE------------#
                    if(messageHandler.exteriorType =="Color"):
                        if(data[2] == "Color1"):
                            messageHandler.exteriorColor1 = data[3]
                        if(data[2] == "Color2"):
                            messageHandler.exteriorColor2 = data[3]
                    #---------TIMER BASIQUE------------#
                    if(messageHandler.exteriorType =="Timer"):
                        messageHandler.exteriorTimer = data[2]
                    #---------SLIDER BASIQUE------------#
                    if(messageHandler.exteriorType =="SlideSize"):
                        messageHandler.exteriorSlideSize = data[2]



                    
                    messageHandler.exteriorType = data[1]
                    if(data[2]):
                        messageHandler.exteriorColor = data[2]
                else:
                    messageHandler.error = True

        except IOError:
            pass

        except KeyboardInterrupt:

            print ("disconnected")

            client_sock.close()
            server_sock.close()
            print ("all done")

            #break
