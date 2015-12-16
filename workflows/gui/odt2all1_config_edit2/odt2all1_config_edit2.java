/* Copyright (C) 2014-2015  Stephan Kreutzer
 *
 * This file is part of odt2all1_config_edit2.
 *
 * odt2all1_config_edit2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * odt2all1_config_edit2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with odt2all1_config_edit2. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/gui/odt2all1_config_edit2.java
 * @brief Editor for configuring the input files for odt2all1 workflow.
 * @author Stephan Kreutzer
 * @since 2014-12-09
 */



import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent; 
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.Iterator;
import java.util.Collections;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;



public class odt2all1_config_edit2
  extends JFrame
  implements ActionListener
{
    public static void main(String[] args)
    {
        System.out.print("odt2all1_config_edit2  Copyright (C) 2014-2015  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "or the project website http://www.publishing-systems.org.\n\n");

        File configFile = null;

        if (args.length == 1)
        {
            configFile = new File(args[0]);

            if (configFile.exists() != true)
            {
                System.out.print("odt2all1_config_edit2: '" + configFile.getAbsolutePath() + "' doesn't exist.\n");
                System.exit(-1);
            }

            if (configFile.isFile() != true)
            {
                System.out.print("odt2all1_config_edit2: '" + configFile.getAbsolutePath() + "' isn't a file.\n");
                System.exit(-1);
            }

            if (configFile.canRead() != true)
            {
                System.out.print("odt2all1_config_edit2: '" + configFile.getAbsolutePath() + "' isn't readable.\n");
                System.exit(-1);
            }
        }
        else
        {
            final JFileChooser chooser = new JFileChooser("Choose/create odt2all1 configuration file");
            chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setApproveButtonText("Select/Create");

            chooser.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
                    {
                        final File resultFile = (File) e.getNewValue();
                    }
                }
            });

            FileFilter fileFilter = new FileNameExtensionFilter("odt2all1 configuration file (*.xml)", "xml");
            chooser.addChoosableFileFilter(fileFilter);
            chooser.setFileFilter(fileFilter);

            chooser.setVisible(true);
            final int result = chooser.showDialog(null, null);
            chooser.setVisible(false); 

            if (result == JFileChooser.APPROVE_OPTION)
            {
                configFile = chooser.getSelectedFile();
            }
            else
            {
                System.exit(2);
            }
            
            if (configFile.exists() != true)
            {
                try
                {
                    configFile.createNewFile();
                    
                    BufferedWriter writer = new BufferedWriter(
                                            new OutputStreamWriter(
                                            new FileOutputStream(configFile),
                                            "UTF8"));
                    
                    writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                    writer.write("<!-- This file was created by odt2all1_config_edit2, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");
                    writer.write("<odt2all1-config>\n");
                    writer.write("</odt2all1-config>\n");

                    writer.flush();
                    writer.close();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                    System.exit(-1);
                }
                
                System.out.println("odt2all1_config_edit2: '" + configFile.getAbsolutePath() + "' created.");
            }
            else
            {
                if (configFile.canRead() != true)
                {
                    System.out.print("odt2all1_config_edit2: '" + configFile.getAbsolutePath() + "' isn't readable.\n");
                    System.exit(-1);
                }
            }
        }

        odt2all1_config_edit2 frame = new odt2all1_config_edit2(configFile);
        frame.setLocation(100, 100);
        frame.pack();
        frame.setVisible(true);
    }

    public odt2all1_config_edit2(File configFile)
    {
        super("File Setup for a Configuration File of odt2all1");

        this.programPath = odt2all1_config_edit2.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        try
        {
            this.programPath = new File(this.programPath).getCanonicalPath() + File.separator;
            this.programPath = URLDecoder.decode(this.programPath, "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        this.configFile = configFile;

        this.odtLabelList = new ArrayList<JLabel>();
        this.odtFileList = new ArrayList<JTextField>();
        this.buttonUpList = new ArrayList<JButton>();
        this.buttonDownList = new ArrayList<JButton>();
        this.buttonRemoveList = new ArrayList<JButton>();

        JPanel panelConfig = new JPanel();

        GridBagLayout gridbag = new GridBagLayout();
        panelConfig.setLayout(gridbag);

        this.labelConfigurationFile = new JLabel("Configuration",
                                                 SwingConstants.LEFT);

        GridBagConstraints gridbagConstraints = new GridBagConstraints();
        gridbagConstraints.gridwidth = GridBagConstraints.RELATIVE;
        gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridbagConstraints.weightx = 0.0;
        panelConfig.add(this.labelConfigurationFile, gridbagConstraints);

        this.textFieldConfigurationFile = new JTextField(45);
        this.textFieldConfigurationFile.setEditable(false);
        this.textFieldConfigurationFile.setText(this.configFile.getAbsolutePath());

        gridbagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridbagConstraints.weightx = 1.0;
        panelConfig.add(this.textFieldConfigurationFile, gridbagConstraints);

        panelConfig.setBorder(BorderFactory.createEtchedBorder()); 


        JPanel panelHead = new JPanel();

        gridbag = new GridBagLayout();
        panelHead.setLayout(gridbag);

        gridbagConstraints = new GridBagConstraints();        
        gridbagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridbagConstraints.weightx = 1.0;
        panelHead.add(panelConfig, gridbagConstraints);
        this.getContentPane().add(panelHead, BorderLayout.PAGE_START);


        this.panelMain = new JPanel();

        if (readConfigurationFile() == true)
        {
            ImageIcon iconCorrect = new ImageIcon(this.programPath + "correct.png");
            this.labelConfigurationFile.setIcon(iconCorrect);
        }
        else
        {
            ImageIcon iconIncorrect = new ImageIcon(this.programPath + "incorrect.png");
            this.labelConfigurationFile.setIcon(iconIncorrect);
        }

        this.buttonAdd = new JButton("Add new ODT file");
        this.buttonAdd.addActionListener(this);

        this.generateGUI();

        this.panelMain.setBorder(BorderFactory.createEtchedBorder()); 
        this.getContentPane().add(this.panelMain, BorderLayout.CENTER);


        JPanel panelButtons = new JPanel();

        JButton buttonExit = new JButton("Exit");
        buttonExit.addActionListener(this);
        panelButtons.add(buttonExit);

        JButton buttonCheck = new JButton("Check");
        buttonCheck.addActionListener(this);
        panelButtons.add(buttonCheck);
        
        JButton buttonSave = new JButton("Save");
        buttonSave.addActionListener(this);
        panelButtons.add(buttonSave);
        
        JButton buttonAbout = new JButton("About");
        buttonAbout.addActionListener(this);
        panelButtons.add(buttonAbout);
        
        panelButtons.setBorder(BorderFactory.createEtchedBorder());
        
        
        JPanel panelBottom = new JPanel();
        
        gridbag = new GridBagLayout();
        panelBottom.setLayout(gridbag);

        gridbagConstraints = new GridBagConstraints();        
        gridbagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridbagConstraints.weightx = 1.0;
        panelBottom.add(panelButtons, gridbagConstraints);

        getContentPane().add(panelBottom, BorderLayout.PAGE_END);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event)
            {
                event.getWindow().setVisible(false);
                event.getWindow().dispose();
                System.exit(2);
            }
        });

        this.CheckFields();
        this.pack();
    }

    protected boolean generateGUI()
    {
        this.panelMain.removeAll();
        this.odtLabelList.clear();
        this.buttonUpList.clear();
        this.buttonDownList.clear();
        this.buttonRemoveList.clear();

        GridBagLayout gridbag = new GridBagLayout();
        this.panelMain.setLayout(gridbag);

        { 
            ImageIcon iconUp = new ImageIcon(this.programPath + "up.png");
            ImageIcon iconDown = new ImageIcon(this.programPath + "down.png");
            ImageIcon iconRemove = new ImageIcon(this.programPath + "remove.png");

            GridBagConstraints gridbagConstraints = new GridBagConstraints();

            Iterator<JTextField> iter = this.odtFileList.iterator();
            int i = 1;

            while (iter.hasNext())
            {
                JLabel label = new JLabel("ODT #" + i,
                                          SwingConstants.LEFT);

                gridbagConstraints.gridwidth = 1;
                gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
                gridbagConstraints.weightx = 0.0;
                this.odtLabelList.add(label);
                panelMain.add(label, gridbagConstraints);

                JTextField textField = iter.next();

                gridbagConstraints.gridwidth = 2;
                gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
                gridbagConstraints.weightx = 1.0;
                panelMain.add(textField, gridbagConstraints);
                
                JButton buttonUp = new JButton(iconUp);
                buttonUp.setToolTipText("Move up");
                buttonUp.addActionListener(this);
                
                gridbagConstraints.gridwidth = 3;
                gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
                gridbagConstraints.weightx = 0.0;
                
                if (i == 1)
                {
                    buttonUp.setEnabled(false);
                }
                
                buttonUpList.add(buttonUp);
                panelMain.add(buttonUp, gridbagConstraints);
                
                JButton buttonDown = new JButton(iconDown);
                buttonDown.setToolTipText("Move down");
                buttonDown.addActionListener(this);
                
                gridbagConstraints.gridwidth = GridBagConstraints.RELATIVE;
                gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
                gridbagConstraints.weightx = 0.0;
                
                if (i == this.odtFileList.size())
                {
                    buttonDown.setEnabled(false);
                }
                
                buttonDownList.add(buttonDown);
                panelMain.add(buttonDown, gridbagConstraints);
                
                JButton buttonRemove = new JButton(iconRemove);
                buttonRemove.setToolTipText("Remove");
                buttonRemove.addActionListener(this);
                
                gridbagConstraints.gridwidth = GridBagConstraints.REMAINDER;
                gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
                gridbagConstraints.weightx = 0.0;
                
                buttonRemoveList.add(buttonRemove);
                panelMain.add(buttonRemove, gridbagConstraints);

                i++;
            }
        }
        
        {
            GridBagConstraints gridbagConstraints = new GridBagConstraints();
            gridbagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridbagConstraints.weightx = 1.0;
            this.panelMain.add(this.buttonAdd, gridbagConstraints);
        }
        
        this.CheckFields();
        this.pack();
    
        return true;
    }

    public void actionPerformed(ActionEvent event)
    {
        if (event.getActionCommand().length() > 0)
        {
            String clickedButton = event.getActionCommand();

            if (clickedButton.equalsIgnoreCase("Add new ODT file") == true)
            {
                this.AddNewODTInputFile();
                this.generateGUI();
            }
            else if (clickedButton.equalsIgnoreCase("Exit") == true)
            {
                System.exit(2);
            }
            else if (clickedButton.equalsIgnoreCase("Check") == true)
            {
                this.CheckFields();
            }
            else if (clickedButton.equalsIgnoreCase("Save") == true)
            {
                if (CheckFields() == true)
                {
                    if (writeConfigurationFile() == true)
                    {
                        ImageIcon iconCorrect = new ImageIcon(this.programPath + "correct.png");
                        this.labelConfigurationFile.setIcon(iconCorrect);
                    }
                    else
                    {
                        ImageIcon iconIncorrect = new ImageIcon(this.programPath + "incorrect.png");
                        this.labelConfigurationFile.setIcon(iconIncorrect);
                    }
                }
            }
            else if (clickedButton.equalsIgnoreCase("About") == true)
            {
                AboutDialog aboutDialog = new AboutDialog(this);
                aboutDialog.setVisible(true);
            }
            else
            {
            
            }
        }
        else
        {
            Object source = event.getSource();
        
            Iterator<JButton> iter = this.buttonUpList.iterator();
            int i = 0;
            boolean up = false;
            boolean down = false;
            boolean remove = false;

            while (iter.hasNext())
            {
                JButton buttonUp = iter.next();
                JButton buttonDown = this.buttonDownList.get(i);
                JButton buttonRemove = this.buttonRemoveList.get(i);
                
                if (source == buttonUp)
                {
                    up = true;
                    break;
                }
                else if (source == buttonDown)
                {
                    down = true;
                    break;
                }
                else if (source == buttonRemove)
                {
                    remove = true;
                    break;
                }

                i++;
            }
            
            if (up == true ||
                down == true ||
                remove == true)
            {
                if (up == true)
                {
                    if (i > 0)
                    {
                        Collections.swap(this.odtFileList, i, i-1);
                    }
                }
                else if (down == true)
                {
                    if (i < (this.odtFileList.size() - 1))
                    {
                        Collections.swap(this.odtFileList, i, i+1);
                    }
                }
                else if (remove == true)
                {
                    this.odtFileList.remove(i);
                }
                
                this.generateGUI();
            }
        }
    }
    
    protected boolean AddNewODTInputFile()
    {
        final JFileChooser chooser = new JFileChooser("Select new ODT input file");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        chooser.setCurrentDirectory(this.configFile.getAbsoluteFile().getParentFile());

        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
                {
                    final File resultFile = (File) e.getNewValue();
                }
            }
        });

        FileFilter fileFilter = new FileNameExtensionFilter("ODT files", "odt", "odt");
        chooser.addChoosableFileFilter(fileFilter);
        chooser.setFileFilter(fileFilter);

        chooser.setVisible(true);
        final int result = chooser.showOpenDialog(null);
        chooser.setVisible(false); 

        if (result == JFileChooser.APPROVE_OPTION)
        {
            File odtInputFile = chooser.getSelectedFile();
           
            JTextField textField = new JTextField(30);
            textField.setText(odtInputFile.getAbsolutePath());

            this.odtFileList.add(textField);
        }
        
        return true;
    }
    
    protected boolean CheckFields()
    {
        boolean valid = true;

        ImageIcon iconCorrect = new ImageIcon(this.programPath + "correct.png");
        ImageIcon iconIncorrect = new ImageIcon(this.programPath + "incorrect.png");
        
        if (this.odtFileList.size() > 0)
        {
            Iterator<JTextField> iter = this.odtFileList.iterator();
            int i = 0;

            while (iter.hasNext())
            {
                boolean odtFileValid = true;
            
                JLabel label = this.odtLabelList.get(i);
                JTextField textField = iter.next();

                String odtFileText = textField.getText();
                File odtFile = new File(odtFileText);

                if (odtFile.isAbsolute() == true)
                {
                    if (odtFile.exists() != true)
                    {
                        odtFileValid = false;
                        textField.setToolTipText("'" + odtFile.getAbsolutePath() + "' doesn't exist!");
                    }
                    else
                    {
                        if (odtFile.isFile() != true)
                        {
                            odtFileValid = false;
                            textField.setToolTipText("Isn't a file!");
                        }
                    }
                    
                    if (odtFileValid == true)
                    {
                        textField.setToolTipText("Absolute path to ODT input file");
                    }
                }
                else
                {
                    String relativePath = this.configFile.getAbsoluteFile().getParent();
                    
                    if (relativePath.substring((relativePath.length() - File.separator.length()) - new String(".").length(), relativePath.length()).equalsIgnoreCase(File.separator + "."))
                    {
                        // Remove dot that references the local, current directory.
                        relativePath = relativePath.substring(0, relativePath.length() - new String(".").length());
                    }
                    
                    if (relativePath.substring(relativePath.length() - File.separator.length(), relativePath.length()).equalsIgnoreCase(File.separator) != true)
                    {
                        relativePath += File.separator;
                    }
                    
                    relativePath += odtFileText;
                    odtFile = new File(relativePath);
                    
                    if (odtFile.exists() != true)
                    {
                        odtFileValid = false;
                        textField.setToolTipText("'" + odtFile.getAbsolutePath() + "' doesn't exist!");
                    }
                    else
                    {
                        if (odtFile.isFile() != true)
                        {
                            odtFileValid = false;
                            textField.setToolTipText("Isn't a file!");
                        }
                    }
                
                    if (odtFileValid == true)
                    {
                        textField.setToolTipText("Relative path of the ODT input file (relative to the directory of the configuration file)");
                    }
                }

                if (odtFileValid == true)
                {
                    label.setIcon(iconCorrect);
                }
                else
                {
                    label.setIcon(iconIncorrect);
                    valid = false;
                }

                i++;
            }
        }
        else
        {
            this.labelConfigurationFile.setIcon(iconIncorrect);
            valid = false;
        }

        this.pack();
        
        return valid;
    }
  
    protected boolean readConfigurationFile()
    {
        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(this.configFile);
            document.getDocumentElement().normalize();

            NodeList rootNodeList = document.getElementsByTagName("odt2all1-config");

            if (rootNodeList.getLength() <= 0)
            {
                return false;
            }

            NodeList rootSubNodeList = rootNodeList.item(0).getChildNodes();
            boolean odtEntryFound = false;

            for (int i = 0; i < rootSubNodeList.getLength(); i++)
            {
                Node inNode = rootSubNodeList.item(i);

                if (inNode.getNodeName().equalsIgnoreCase("input-file") == true)
                {
                    NamedNodeMap attributes = inNode.getAttributes();
                    Node pathAttribute = attributes.getNamedItem("path");
                    
                    if (pathAttribute == null)
                    {
                        System.out.println("odt2all1_config_edit2: Input file entry is missing the 'path' attribute in '" + this.configFile.getAbsolutePath() + "'.");
                        System.exit(-1);
                    }

                    odtEntryFound = true;

                    JTextField textField = new JTextField(30);
                    textField.setText(pathAttribute.getTextContent());
                    this.odtFileList.add(textField);
                }
            }
            
            if (odtEntryFound != true)
            {
                return false;
            }
        }
        catch (ParserConfigurationException ex)
        {
            ex.printStackTrace();
            System.exit(-4);
        }
        catch (SAXException ex)
        {
            ex.printStackTrace();
            System.exit(-5);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-6);
        }

        return true;
    }
    
    protected boolean writeConfigurationFile()
    {
        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(this.configFile);
            document.setXmlStandalone(true);
            document.getDocumentElement().normalize();


            NodeList rootNodeList = document.getElementsByTagName("odt2all1-config");

            if (rootNodeList.getLength() <= 0)
            {
                return false;
            }

            NodeList rootSubNodeList = rootNodeList.item(0).getChildNodes();

            for (int i = rootSubNodeList.getLength() - 1; i >= 0; i--)
            {
                Node subNode = rootSubNodeList.item(i);

                if (subNode.getNodeName().equalsIgnoreCase("input-file") == true)
                {
                    rootNodeList.item(0).removeChild(subNode);
                }
            }


            Iterator<JTextField> iter = this.odtFileList.iterator();
            int i = 0;

            while (iter.hasNext())
            {
                JTextField textField = iter.next();

                Element odtNode = document.createElement("input-file");
                odtNode.setAttribute("path", textField.getText());

                rootNodeList.item(0).appendChild(odtNode);

                i++;
            }


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult streamResult =  new StreamResult(this.configFile);
            transformer.transform(source, streamResult);
        }
        catch (ParserConfigurationException ex)
        {
            ex.printStackTrace();
            System.exit(-7);
        }
        catch (SAXException ex)
        {
            ex.printStackTrace();
            System.exit(-8);
        }
        catch (TransformerConfigurationException ex)
        {
            ex.printStackTrace();
            System.exit(-9);
        }
        catch (TransformerException ex)
        {
            ex.printStackTrace();
            System.exit(-10);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-11);
        }

        return true;
    }


    protected File configFile;

    protected JPanel panelMain;

    private JLabel labelConfigurationFile;
    private JTextField textFieldConfigurationFile;

    private JButton buttonAdd;

    private ArrayList<JLabel> odtLabelList;
    private ArrayList<JTextField> odtFileList;
    private ArrayList<JButton> buttonUpList;
    private ArrayList<JButton> buttonDownList;
    private ArrayList<JButton> buttonRemoveList;

    private String programPath;
}

class AboutDialog extends JDialog
{
    public AboutDialog(JFrame parent)
    {
        generateGUI(parent);
    }

    public final void generateGUI(JFrame parent)
    {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(Box.createRigidArea(new Dimension(0, 10)));

        String programPath = AboutDialog.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        try
        {
            programPath = new File(programPath).getCanonicalPath() + File.separator;
            programPath = URLDecoder.decode(programPath, "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        ImageIcon icon = new ImageIcon(programPath + "publishing_systems_logo.png");
        JLabel label = new JLabel(icon);
        label.setAlignmentX(0.5f);
        add(label);

        add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel notice = new JLabel("<html><head><title>About odt2all1_config_edit2</title></head><body>" +
                                   "odt2all1_config_edit2  Copyright (C) 2014  Stephan Kreutzer<br/><br/>" +
                                   "This program comes with ABSOLUTELY NO WARRANTY.<br/>" +
                                   "This is free software, and you are welcome to redistribute it<br/>" +
                                   "under certain conditions. See the GNU Affero General Public<br/>" +
                                   "License 3 or any later version for details. Also, see the source code<br/>" +
                                   "repository https://github.com/publishing-systems/automated_digital_publishing/<br/>" +
                                   "or the project website http://www.publishing-systems.org.<br/>" +
                                   "</body></html>");
                                 
        notice.setFont(notice.getFont().deriveFont(notice.getFont().getStyle() & ~Font.BOLD));
        notice.setAlignmentX(0.5f);
        add(notice);

        add(Box.createRigidArea(new Dimension(0, 10)));

        JButton closeButton = new JButton("Close");

        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });

        closeButton.setAlignmentX(0.5f);
        add(closeButton);

        setModalityType(ModalityType.APPLICATION_MODAL);

        setTitle("About");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setSize(520, 415);
    }
}

