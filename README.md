# EmpireGUI
This is a GUI for the projet https://github.com/PowerShellEmpire/Empire

Created by:  
Tristan Dostaler - https://github.com/tristandostaler  
Cydrick Trudel  - https://github.com/CydrickTrudel - http://stackoverflow.com/users/2278844/cydrickt  
Mathieu St-Jean Champion  
Jean-Philippe Champoux  
Guillaume Beaudry  
Frédérick Lebel  

EmpireGUI offers a GUI for the projet PowerShell Empire (pse). Almost everything is dynamic so it should support all releases of pse (if they don't modify the architecture too much). There is something like 3 words hard coded except for the json variables types. It has been coded in java with the goal of supporting all platforms. We plan on doing the gui for android too (by sharing parts of the code).

EmpireGUI can be used locally (on the same machine as the PowerShell Empire server) or via SSH. You can start PowerShell Empire with the following command to enable the REST API: ./empire --username empireadmin --password thePassword --headless
When you connect to it trough the GUI, you provide the user and password for the pse server (here empireadmin/thePassword) and if needed, you can check the checkbox to use ssh. You then provide the ssh address, port, username and password for the connection. 

![alt tag](https://raw.githubusercontent.com/tristandostaler/EmpireGUI/master/ScreenShots/LoginScreen.PNG)


You can see in the picture below the overall look and feel. We are generating the "launcher" stager with defaults value and the listener named "MainListener". 

![alt tag](https://raw.githubusercontent.com/tristandostaler/EmpireGUI/master/ScreenShots/GeneratingLauncherStager.PNG)


More screenShots are available in the folder "ScreenShots".

To fix the import problem of javafx on ubuntu, do "sudo apt-get install openjfx".
Source: http://stackoverflow.com/questions/32630354/the-import-javafx-cannot-be-resolved

For the moment, a few features are missing (like creating listeners directly from the GUI) but are in the TODO list.

Feel free to help us make this project better! Pull requests are welcome!
