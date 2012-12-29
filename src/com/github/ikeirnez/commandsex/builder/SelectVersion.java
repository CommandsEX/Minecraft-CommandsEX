package com.github.ikeirnez.commandsex.builder;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;

public class SelectVersion extends JFrame {

    private JPanel contentPane;

    public static void main(String[] args) {
        new SelectVersion();
    }

    /**
     * Create the frame.
     */
    public SelectVersion() {
        super("Select Version");
        setPreferredSize(new Dimension(491, 126));
        //setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 112);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        
        JLabel lblPleaseSelectA = new JLabel("Please Select a CommandsEX Version");
        lblPleaseSelectA.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblPleaseSelectA.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton btnLatestStable = new JButton("Latest Stable");
        btnLatestStable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try {
                    new DownloadProgress("Latest Stable", new URL("http://dev.bukget.org/3/plugins/bukkit/commandsex/latest/download"));
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
            }
        });
        
        JButton btnCustom = new JButton("Custom");
        btnCustom.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                
            }
        });
        
        JButton btnLatestDev = new JButton("Latest Dev");
        btnLatestDev.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new DownloadProgress("Latest Dev", Utils.getLatestJenkinsDownload());
            }
        });
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                            .addGap(10)
                            .addComponent(lblPleaseSelectA, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE))
                        .addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(btnLatestStable)
                            .addGap(74)
                            .addComponent(btnLatestDev, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(75)
                            .addComponent(btnCustom, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        gl_contentPane.setVerticalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addComponent(lblPleaseSelectA)
                    .addGap(18)
                    .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnLatestStable)
                        .addComponent(btnCustom)
                        .addComponent(btnLatestDev))
                    .addContainerGap(193, Short.MAX_VALUE))
        );
        contentPane.setLayout(gl_contentPane);
        
        setSize(getPreferredSize());
    }
}
