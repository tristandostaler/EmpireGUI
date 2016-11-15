# EmpireGUI
This is a GUI for the projet https://github.com/PowerShellEmpire/Empire

# Important notice!
We are thinking about redoing the project in ASP NET Core with the new .NET Core (multi-platform).  
https://www.microsoft.com/net/core  
We will do a prototype, test it and if it works as expected, we will go ahead with this idea.  
This modification will allow us to be faster in the development (thanks C# and visual studio) while being able to add a few interesting features. You will need to run the EmpireASP (new name) on the same machine as the empire server and connect to it trough your browser. There will be no need anymore to handle the SSH connection. If you do connect via SSH (which we recommend), you could use putty (for example).  
For the moment, both project will be supported. We will release a beta version as soon as possible.  
Feel free to post questions/comments in the associated issue: https://github.com/tristandostaler/EmpireGUI/issues/6  
We are looking for developpers to help us get the new version faster! Your name will be added to the list bellow!  


Created by:  
Tristan Dostaler - https://github.com/tristandostaler  
Cydrick Trudel  - https://github.com/CydrickTrudel - http://stackoverflow.com/users/2278844/cydrickt  
Mathieu St-Jean Champion  
Jean-Philippe Champoux  
Guillaume Beaudry  
Frédérick Lebel  


EmpireGUI offers a GUI for the projet PowerShell Empire (pse). Almost everything is dynamic so it should support all releases of pse (if they don't modify the architecture too much). There is something like 3 words hard coded except for the json variables types. It has been coded in java with the goal of supporting all platforms. We plan on doing the gui for android too (by sharing parts of the code).

EmpireGUI can be used to create Listeners, delete Listeners, launch modules on Agents (and receive the output when available) and use stagers (and receive the output when available).

EmpireGUI can be used locally (on the same machine as the PowerShell Empire server), on the local network or via SSH. You can start PowerShell Empire with the following command to enable the REST API: ./empire --username empireadmin --password thePassword --headless
When you connect to it trough the GUI, you provide the user and password for the pse server (here empireadmin/thePassword) and if needed, you can check the checkbox to use ssh. You then provide the ssh address, port, username and password for the connection. 

![alt tag](https://raw.githubusercontent.com/tristandostaler/EmpireGUI/master/ScreenShots/LoginScreen.PNG)


You can see in the picture below the overall look and feel. We are generating the "launcher" stager with defaults value and the listener named "MainListener". 

![alt tag](https://raw.githubusercontent.com/tristandostaler/EmpireGUI/master/ScreenShots/GeneratingLauncherStager.PNG)


EmpireGUI also display info when available. In this example, we received the launcher stager code:

![alt tag](https://raw.githubusercontent.com/tristandostaler/EmpireGUI/master/ScreenShots/LauncherStagerGenerated.PNG)

Small demo (thx to Vect0r!): https://www.youtube.com/watch?v=U0QIW4ocqXU  
More screenShots are available in the folder "ScreenShots".

# Installation, usage and supported OS
To use EmpireGUI, you need to have Java >= 8 installed on your OS:  
https://www.java.com/fr/download/  
https://www.java.com/fr/download/manual.jsp  

You can then directly download EmpireGUI.jar in the directory Builds (or clone the repo and go in Builds/). In Windows, you simply double click the EmpireGUI.jar. In Linux, you need to make the jar executable before you can double click it.  
Be sure you have the java binary in your Path.  

EmpireGUI was tested on Windows 8, 8.1 and 10, on Ubuntu and on Kali Linux. Normally, any windows/linux supporting Java >= 8 should be alright.  


# Actual version: 1.2.1  
Features added:  

* You can now get the reports and the complete logs when you select an agent. Keylogging are in the logs.
* You can now give the token directly (fixes a bug in the empire server)
* There is now a Files tab where you can make basic file handling between the server and the client.
* You can now get the reports for every agents
* A lot of fixes and refactoring
* Now the TODO list is in the top of the MainView class in MainView.java just before the constructor

To fix the import problem of javafx on ubuntu, do "sudo apt-get install openjfx".
Source: http://stackoverflow.com/questions/32630354/the-import-javafx-cannot-be-resolved

For the moment, a few features are missing but are in the TODO list.

Feel free to help us make this project better! Pull requests are welcome!
