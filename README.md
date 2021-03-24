# A File Explorer created in Java Swing
***
### It displays all the System files. Also lets you rename, delete and edit the files.
---
#### JTree displays the file System files in a tree format, JTable displays the list of files in the selected path.
---
Displaying System files

![Capture_FileExplorer]()


Right clicking on the JTree and JTable opens the Popup Menu of Options to manipulate files

![Capture_FileExplorer_TablePopup]()
![Capture_FileExplorer_TreePopup]()


Selecting the Rename Options, pops a JOptionPane to input the new file name

![Capture_FileExplorer_RenameButton]()


Selecting the Delete Option, pops a JOptionPane to confirm if the user wants to delete the file

![Capture_FileExplorer_DeleteButton]()


Selecting the Open option, opens a new JDialog which displays the contents of the file, which can be edited

![Capture_FileExplorer_OpenButton]()


Contents of the edited file can be saved by clicking on Edit -> Save

![Capture_FileExplorer_SaveOption]()


Selecting a file or directory in the JTree and Clicking the Refresh Button, Refreshes the JTree to point on that particular path

![Capture_FileExplorer_RefreshButton]()


To go back to the Home Directory, click on the below Home Button

![Capture_FileExplorer_HomeButton]()