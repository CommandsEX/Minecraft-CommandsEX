package com.github.ikeirnez.commandsex.builder;

import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JProgressBar;

public class DownloadProgress extends JFrame {

    private JPanel contentPane;
    private Window window;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            new DownloadProgress("20MB Test File", new URL("http://download.thinkbroadband.com/20MB.zip"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the frame.
     */
    public DownloadProgress(final String what, URL url) {
        super("Downloading...");
        window = this;
        final Download download = new Download(url);

        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 448, 149);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JLabel lblDownloading = new JLabel("Downloading " + what);
        lblDownloading.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblDownloading.setHorizontalAlignment(SwingConstants.CENTER);

        final JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                                .addComponent(progressBar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                                .addComponent(lblDownloading, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE))
                                .addContainerGap())
                );
        gl_contentPane.setVerticalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblDownloading)
                        .addGap(18)
                        .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(163, Short.MAX_VALUE))
                );
        contentPane.setLayout(gl_contentPane);

        Thread thread = new Thread() {
            public boolean running = true;

            public void run(){
                while (running){
                    switch (download.getStatus()){
                    default:
                        break;
                    case (Download.DOWNLOADING): {
                        progressBar.setValue((int) ((Float) download.getProgress()).floatValue());
                        break;
                    }
                    case (Download.COMPLETE): {
                        progressBar.setValue(100);
                        JOptionPane.showMessageDialog(contentPane, "Successfully downloaded " + what, "Download Success", JOptionPane.INFORMATION_MESSAGE);
                        processWindowEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
                        cancel();
                        break;
                    }
                    case (Download.ERROR): {
                        JOptionPane.showMessageDialog(contentPane, "There was an error while downloading " + download.getUrl(), "Download Error", JOptionPane.ERROR_MESSAGE);
                        cancel();
                        break;
                    }
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            public void cancel(){
                running = false;
            }
        };

        thread.start();
    }
}
