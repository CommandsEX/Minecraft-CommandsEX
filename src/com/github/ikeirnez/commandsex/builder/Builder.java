package com.github.ikeirnez.commandsex.builder;

import javax.swing.JApplet;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import org.yaml.snakeyaml.Yaml;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.LinkedHashMap;
import java.awt.Dimension;

public class Builder extends JApplet {

    private JLabel lblVersion;

    public static void main(String[] args){

    }

    /**
     * Create the applet.
     */
    public Builder() {
        getContentPane().setPreferredSize(new Dimension(500, 550));
        getContentPane().setSize(getContentPane().getPreferredSize());
        JButton btnLoadSettings = new JButton("Load Settings");

        JButton btnSelectVersion = new JButton("Select Version");
        btnSelectVersion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new SelectVersion();
            }
        });

        lblVersion = new JLabel("CommandsEX Version");
        lblVersion.setHorizontalAlignment(SwingConstants.RIGHT);
        lblVersion.setVisible(false);

        JLabel lblCurrentlyBuildingAgainst = new JLabel("Currently Building Against");
        lblCurrentlyBuildingAgainst.setHorizontalAlignment(SwingConstants.RIGHT);
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(groupLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                                .addGroup(groupLayout.createSequentialGroup()
                                        .addComponent(btnLoadSettings)
                                        .addPreferredGap(ComponentPlacement.RELATED, 231, Short.MAX_VALUE)
                                        .addComponent(btnSelectVersion)
                                        .addGap(13))
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
                                                        .addComponent(lblVersion, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(lblCurrentlyBuildingAgainst, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                        .addContainerGap())))
                );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                .addComponent(btnLoadSettings)
                                .addComponent(btnSelectVersion))
                                .addGap(3)
                                .addComponent(lblCurrentlyBuildingAgainst)
                                .addGap(1)
                                .addComponent(lblVersion)
                                .addContainerGap(234, Short.MAX_VALUE))
                );
        getContentPane().setLayout(groupLayout);

        setJarVersion("2.0");

        File cexFolder = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());

        if (!cexFolder.isDirectory()){
            cexFolder = cexFolder.getParentFile();
        }

        File cex = new File(cexFolder, "CommandsEX.jar");

        if (cex.exists()){
            try {
                Yaml yaml = new Yaml();
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, Object> hMap = (LinkedHashMap<String, Object>) yaml.load(com.github.ikeirnez.commandsex.BuilderAccess.getPluginYML());
                setJarVersion((String) hMap.get("version"));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void setJarVersion(String version){
        if (version == null){
            lblVersion.setVisible(false);
            return;
        }

        lblVersion.setText("CommandsEX v" + version);
        lblVersion.setVisible(true);
    }
}
