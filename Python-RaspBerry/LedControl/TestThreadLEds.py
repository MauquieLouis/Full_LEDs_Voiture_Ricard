import threading
import LedsThread
from LedsThread import MainLedProgram
import time



class TestObjet:
    x=5
    
lastMessage="begin"

noe= TestObjet()
print("Depart noé = %s"%noe.x)

ledThread = threading.Thread(target= MainLedProgram, args=[noe])
ledThread.start()
print("LedTHread ALive")

while True:
    print("noé = %s"%noe.x)
    time.sleep(1);
