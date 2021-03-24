# A File Explorer created in Java Swing
***
### It displays all the System files. Also lets you rename, delete and edit the files.

#### JTree displays the System files in a tree format, JTable displays the list of files in the selected path.
---
Displaying System files

![Capture_FileExplorer](https://github.com/04xRaynal/File_Explorer_JavaSwing/blob/c37826868ebdf6027fafe371352a9f5d0ed2f824/Captured%20Images/Capture_FileExplorer.PNG)



Right clicking on the JTree and JTable opens the Popup Menu of Options to manipulate files

![Capture_FileExplorer_TablePopup](https://github.com/04xRaynal/File_Explorer_JavaSwing/blob/c37826868ebdf6027fafe371352a9f5d0ed2f824/Captured%20Images/Capture_FileExplorer_TablePopup.PNG)

![Capture_FileExplorer_TreePopup](https://github.com/04xRaynal/File_Explorer_JavaSwing/blob/c37826868ebdf6027fafe371352a9f5d0ed2f824/Captured%20Images/Capture_FileExplorer_TreePopup.PNG)



Selecting the Rename Options, pops a JOptionPane to input the new file name

![Capture_FileExplorer_RenameButton](https://github.com/04xRaynal/File_Explorer_JavaSwing/blob/c37826868ebdf6027fafe371352a9f5d0ed2f824/Captured%20Images/Capture_FileExplorer_RenameButton.PNG)



Selecting the Delete Option, pops a JOptionPane to confirm if the user wants to delete the file

![Capture_FileExplorer_DeleteButton](https://github.com/04xRaynal/File_Explorer_JavaSwing/blob/c37826868ebdf6027fafe371352a9f5d0ed2f824/Captured%20Images/Capture_FileExplorer_DeleteButton.PNG)



Selecting the Open option, opens a new JDialog which displays the contents of the file, which can be edited

![Capture_FileExplorer_OpenButton](https://github.com/04xRaynal/File_Explorer_JavaSwing/blob/c37826868ebdf6027fafe371352a9f5d0ed2f824/Captured%20Images/Capture_FileExplorer_OpenButton.PNG)



Contents of the edited file can be saved by clicking on Edit -> Save

![Capture_FileExplorer_SaveOption](https://github.com/04xRaynal/File_Explorer_JavaSwing/blob/c37826868ebdf6027fafe371352a9f5d0ed2f824/Captured%20Images/Capture_FileExplorer_SaveOption.PNG)



Selecting a file or directory in the JTree and Clicking the Refresh Button, Refreshes the JTree to point on that particular path

![Capture_FileExplorer_RefreshButton](https://github.com/04xRaynal/File_Explorer_JavaSwing/blob/c37826868ebdf6027fafe371352a9f5d0ed2f824/Captured%20Images/Capture_FileExplorer_RefreshButton.PNG)



To go back to the Home Directory, click on the below Home Button

![Capture_FileExplorer_HomeButton](https://github.com/04xRaynal/File_Explorer_JavaSwing/blob/c37826868ebdf6027fafe371352a9f5d0ed2f824/Captured%20Images/Capture_FileExplorer_HomeButton.PNG)



---
##### Bugs:
1. some unexpected file names throw a runtime error, but they can be ignored as it doesn't affect the working of the application.
2. After updating files, the explorer may not refresh automatically, refresh and home buttons will refresh the explorer to the current updated state.