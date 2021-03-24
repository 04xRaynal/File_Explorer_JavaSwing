package raynal.file_explorer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

public class FileExplorer extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	Container c;
	JTextField textField;
	JTextArea textArea;
	JTree tree;
	JButton refresh, home;
	JTable table;
	JScrollPane treeScrollPane;
	JScrollPane tableScrollPane;
	
	String[] columnHeads = {"File Name", "Size (in Bytes)", "Read Only", "Hidden", "Path" };
	String[][] rowData = {{"", "", "", "", ""}};
	
	DefaultMutableTreeNode rootNode;
	JSplitPane base;
	JPopupMenu popMenu;
	JMenuItem open, rename, delete;
	
	Image desktopIcon = Toolkit.getDefaultToolkit().getImage("src\\resources\\computer-icon.png").getScaledInstance(60, 60, Image.SCALE_SMOOTH);
	Image fileIcon = Toolkit.getDefaultToolkit().getImage("src\\resources\\file-icon.png").getScaledInstance(15, 15, Image.SCALE_SMOOTH);
	JPanel westWrapper;
	
	String s;
	Path path;
	
	
	public FileExplorer() {
		try {   UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());   }
		catch (ClassNotFoundException e) {}
        catch (InstantiationException e) {}
        catch (IllegalAccessException e) {}
        catch (UnsupportedLookAndFeelException e) {}        //Refines the look of the ui
		
		c = getContentPane();
		c.setLayout(new BorderLayout());
		
		textField = new JTextField();
		refresh = new JButton("Refresh");
		home = new JButton("Home");
		home.setEnabled(false);
		base = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, home, refresh);
		
		rootNode = createTree(new File("Computer"));				//String 'Computer' will be shown at the top as the Root Node
		DefaultMutableTreeNode diskNode ;
		File[] roots = File.listRoots();							//Lists the disks of your System
		for(int i = 0; i < roots.length; i++) {
			diskNode = createTree(new File(roots[i] + ""));
			rootNode.add(diskNode);
		}
		
		westWrapper = new JPanel();
		tree = new JTree(rootNode);
		treeScrollPane = new JScrollPane(tree);
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
		renderer.setLeafIcon(new ImageIcon(fileIcon));					//setting icon image for leaf files 
		westWrapper.add(treeScrollPane);
		
		table = new JTable(rowData, columnHeads);
		tableScrollPane = new JScrollPane(table);
		
		c.add(textField, BorderLayout.NORTH);
		c.add(westWrapper, BorderLayout.WEST);
		c.add(tableScrollPane, BorderLayout.CENTER);
		c.add(base, BorderLayout.SOUTH);
		
		
		popMenu = new JPopupMenu("Options");
		open = new JMenuItem("Open");
		rename = new JMenuItem("Rename");
		delete = new JMenuItem("Delete");
		
		popMenu.add(open);
		popMenu.add(new JSeparator());
		popMenu.add(rename);
		popMenu.add(new JSeparator());
		popMenu.add(delete);
		
		
		tree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3) {
					doMouseRightClicked(e);
				}
				else {
					doMouseClicked(e);
				}
			}
		});
		
		
		refresh.addActionListener(this);
		home.addActionListener(this);
		
		setIconImage(desktopIcon);
		setTitle("File Explorer");
		setSize(600, 400);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new FileExplorer();
				
			}
		});
		
	}
	
	
	DefaultMutableTreeNode createTree(File temp) {
		DefaultMutableTreeNode topNode = new DefaultMutableTreeNode(temp);
		
		if(! (temp.exists() && temp.isDirectory()))         	//if it is not a directory, return
				return topNode;
		
		fillTree(topNode, temp.getPath());
		
		return topNode;
	}
	
	
	void fillTree(DefaultMutableTreeNode root, String fileName) {
		File file = new File(fileName);
		
		File[] fileList = file.listFiles();
		
		if(fileList != null) {
			for(int i = 0; i < fileList.length; i++) {
				final DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(fileList[i].getName());
				
				root.add(tempNode);
				
				final String newFileNmae = new String(fileName + "\\" + fileList[i].getName());
				
				Thread t = new Thread() {
					public void run() {
						fillTree(tempNode, newFileNmae);
					}
				};
				t.start();
			}
		}
	}
	
	
	void doMouseClicked(MouseEvent e) {
		TreePath treePath = tree.getPathForLocation(e.getX(), e.getY());
		
		if(treePath == null)
			return;
		
		/*
		 * The above treePath returns path as [Computer, C:\, Home, User, test.txt] when converted via toString()
		 * for folder names which end with ], throw a runtime error as ]] is not expected in the end of the string in replaceFirst() regex 
		 * Hence below the path string is checked for ] as the last character
		 * if yes, it is removed via substring
		 */
		String s = treePath.toString();
		if(s.charAt(s.length() - 1) == ']') {
			s = s.substring(0, s.length() - 1);
		}
		
		//below replaceFirst(), replaces any character in the set (open square bracket, close square bracket) with the empty string. The \\ is to escape the square brackets within the set
		s = s.replaceFirst("[\\[\\]]", "");
		s = s.replace(", ", "\\");							//', ' is replaced with '\'
		
		textField.setText(s);
		s = s.substring(9);									//substring starts at 9 to skip the 'Computer\' at start of the string, as Computer was defined by us and is not a valid path.
		showFiles(s);
	}
	
	
	void doMouseRightClicked(MouseEvent e) {
		popMenu.show(this, e.getX(), e.getY());
		
		TreePath treePath = tree.getPathForLocation(e.getX(), e.getY());
		
		if(treePath == null)
			return;
		
		/*
		 * The above treePath returns path as [Computer, C:\, Home, User, test.txt] when converted via toString()
		 * for folder names which end with ], throw a runtime error as ]] is not expected in the end of the string in replaceFirst() regex 
		 * Hence below the path string is checked for ] as the last character
		 * if yes, it is removed via substring
		 */
		String s = treePath.toString();
		if(s.charAt(s.length() - 1) == ']') {
			s = s.substring(0, s.length() - 1);
		}
		
		//below replaceFirst(), replaces any character in the set (open square bracket, close square bracket) with the empty string. The \\ is to escape the square brackets within the set
		s = s.replaceFirst("[\\[\\]]", "");
		s = s.replace(", ", "\\");							//', ' is replaced with '\'
		
		s = s.substring(9);									//substring starts at 9 to skip the 'Computer\' at start of the string, as Computer was defined by us and is not a valid path.
		path = Paths.get(s);
		
		open.addActionListener(this);
		rename.addActionListener(this);
		delete.addActionListener(this);
	}
	
	
	void doTableMouseClicked(MouseEvent e) {
		int rowClicked= table.getSelectedRow();
		int columnClicked = 4;						//5th column(index 4) has the path of file
		
		s = table.getValueAt(rowClicked, columnClicked).toString();
		path = Paths.get(s);
		
		open.addActionListener(this);
		rename.addActionListener(this);
		delete.addActionListener(this);
	}
	
	
	void showFiles(String fileName) {
		File tempFile = new File(fileName);
		
		c.remove(tableScrollPane);
		table = new JTable(rowData, columnHeads);
		tableScrollPane = new JScrollPane(table);
		c.setVisible(false);
		c.add(tableScrollPane, BorderLayout.CENTER);
		c.setVisible(true);
		
		if(! tempFile.exists())
			return;
		
		File[] fileList = tempFile.listFiles();
		int fileCounter = 0;
		
		if(fileList != null) {
			rowData = new String[fileList.length][5];
			
			for(int i = 0; i < fileList.length; i++) {
				rowData[fileCounter][0] = new String(fileList[i].getName());
				rowData[fileCounter][1] = new String(fileList[i].length() + "");
				rowData[fileCounter][2] = new String(! fileList[i].canWrite() + "");
				rowData[fileCounter][3] = new String(fileList[i].isHidden() + "");
				rowData[fileCounter][4] = new String(fileList[i].getPath());
				
				fileCounter++;
			}
			
			/*
			 * Removing the previous Listeners
			 * As we are replacing the whole component whenever we refresh or update,
			 * we need to replace the listeners as well to avoid multiple listeners running on the same action.
			 */
			for(MouseListener ml: table.getMouseListeners()) {
				table.removeMouseListener(ml);
			}
			//open, rename, delete are the popupMenu listeners
			for(ActionListener al: open.getActionListeners()) {
				open.removeActionListener(al);
			}
			for(ActionListener al: rename.getActionListeners()) {
				rename.removeActionListener(al);
			}
			for(ActionListener al: delete.getActionListeners()) {
				delete.removeActionListener(al);
			}
			
			c.remove(table);
			c.remove(tableScrollPane);
			
			table = new JTable(rowData, columnHeads);
			tableScrollPane = new JScrollPane(table);
			table.setComponentPopupMenu(popMenu);
			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					doTableMouseClicked(e);
				}
			});
			c.setVisible(false);
			c.add(tableScrollPane, BorderLayout.CENTER);
			c.setVisible(true);
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == refresh) {
			/*
			 * Removing the previous Listeners
			 * As we are replacing the whole component whenever we refresh or update,
			 * we need to replace the listeners as well to avoid multiple listeners running on the same action.
			 */
			for(MouseListener ml: tree.getMouseListeners()) {
				tree.removeMouseListener(ml);
			}
			//open, rename, delete are the popupMenu listeners
			for(ActionListener al: open.getActionListeners()) {
				open.removeActionListener(al);
			}
			for(ActionListener al: rename.getActionListeners()) {
				rename.removeActionListener(al);
			}
			for(ActionListener al: delete.getActionListeners()) {
				delete.removeActionListener(al);
			}
			
			
			home.setEnabled(true);
			//Creating a subTree of only the selected path, it replaces the rootTree
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(new File("Computer"));			//Has 'Computer' as the node
			DefaultMutableTreeNode subNode = createTree(new File(textField.getText().substring(8)));	//Below Computer, is the selected Node
			node.add(subNode);
			
			if(node != null)
				tree = new JTree(node);
			
			c.remove(treeScrollPane);
			c.remove(westWrapper);
			treeScrollPane = new JScrollPane(tree);
			DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
			renderer.setLeafIcon(new ImageIcon(fileIcon));
			c.setVisible(false);
			westWrapper = new JPanel();
			westWrapper.add(treeScrollPane);
			c.add(westWrapper, BorderLayout.WEST);
			
			tree.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if(e.getButton() == MouseEvent.BUTTON3) {
						doMouseRightClicked(e);
					}
					else {
						doMouseClicked(e);
					}
				}
			});
			
			c.setVisible(true);
		}
		
		
		if(e.getSource() == home) {
			rowData = new String[][] {{"", "", "", "", ""}};				//clearing the table data
			/*
			 * Removing the previous Listeners
			 * As we are replacing the whole component whenever we refresh or update,
			 * we need to replace the listeners as well to avoid multiple listeners running on the same action.
			 */
			for(MouseListener ml: tree.getMouseListeners()) {
				tree.removeMouseListener(ml);
			}
			//open, rename, delete are the popupMenu listeners
			for(ActionListener al: open.getActionListeners()) {
				open.removeActionListener(al);
			}
			for(ActionListener al: rename.getActionListeners()) {
				rename.removeActionListener(al);
			}
			for(ActionListener al: delete.getActionListeners()) {
				delete.removeActionListener(al);
			}
			
			c.remove(treeScrollPane);
			c.remove(westWrapper);
			c.remove(tableScrollPane);
			
			//Creating the whole tree from scratch again
			rootNode = createTree(new File("Computer"));				//Computer string displayed as the root
			DefaultMutableTreeNode diskNode ;
			File[] roots = File.listRoots();							//Lists the root of your System
			for(int i = 0; i < roots.length; i++) {
				diskNode = createTree(new File(roots[i] + ""));
				rootNode.add(diskNode);
			}
			
			tree = new JTree(rootNode);
			treeScrollPane = new JScrollPane(tree);
			DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
			renderer.setLeafIcon(new ImageIcon(fileIcon));
			westWrapper = new JPanel();
			westWrapper.add(treeScrollPane);
			
			table = new JTable(rowData, columnHeads);
			tableScrollPane = new JScrollPane(table);
			
			textField.setText("");
			
			c.setVisible(false);
			c.add(westWrapper, BorderLayout.WEST);
			c.add(tableScrollPane, BorderLayout.CENTER);
			
			tree.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if(e.getButton() == MouseEvent.BUTTON3) {
						doMouseRightClicked(e);
					}
					else {
						doMouseClicked(e);
					}
				}
			});
			
			home.setEnabled(false);
			refresh.setEnabled(true);
			
			c.setVisible(true);
		}
		
		
		if(e.getSource() == open) {					//reading the selected file and displaying it in a dialog
			JDialog dialog = new JDialog();
			JRootPane rootPane = dialog.getRootPane();
			
			JTextArea textArea = new JTextArea();
			JScrollPane scrollPane = new JScrollPane(textArea);
			scrollPane.setBounds(5, 5, 280, 235);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			
			JMenuBar menuBar = new JMenuBar();
			JMenu menu = new JMenu("Edit");
			JMenuItem menuItem = new JMenuItem("Save");
			menu.add(menuItem);
			menuBar.add(menu);
			
			StringBuilder sb = new StringBuilder();
			int readInt;
			try {
				BufferedReader reader = new BufferedReader(new FileReader(new File (s)));
				while((readInt = reader.read()) != -1) {
					sb.append((char) readInt);
				}
				reader.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			textArea.setText(sb.toString());
			
			//Can edit the text in text area and save the file via the Save button in Edit Menu
			menuItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setDialogTitle("Save your File");
					File selectedFile = null;
					
					int response = fileChooser.showSaveDialog(c);
					if(response == JFileChooser.APPROVE_OPTION) {
						selectedFile = fileChooser.getSelectedFile();
					}
					try {
						BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile));
						writer.write(textArea.getText());
						writer.flush();
						writer.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
			
			rootPane.setJMenuBar(menuBar);
			rootPane.getContentPane().add(scrollPane);
			dialog.setLayout(null);
			dialog.setSize(300,300);
			dialog.setVisible(true);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		}
		
		
		if(e.getSource() == rename) {		//OptionPane which prompts you to enter the new name for the file
			String newName = JOptionPane.showInputDialog(c, "Enter new name for the file: ", "Rename File", JOptionPane.INFORMATION_MESSAGE);
			
			try {
				Files.move(path, path.resolveSibling(newName));			//moves the file to the provided path and handles if file with same name exists(overwrites it) 
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		
		if(e.getSource() == delete) {		//OptionPane which prompts to delete the selected file
			int delete_confirmation = JOptionPane.showConfirmDialog(c, "File will be deleted, Are you sure?", "Delete File", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			try {
				if(delete_confirmation == 0) {
					Files.delete(path);
				}
			}
			catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
