/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applet;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Bears
 */
public class LoginApplet extends javax.swing.JApplet {

    /**
     * Initializes the applet LoginApplet
     */
    @Override
    public void init() {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginApplet.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginApplet.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginApplet.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginApplet.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the applet */
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    initComponents();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is called from within the init() method to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        usernameTextField = new javax.swing.JTextField();
        loginButton = new javax.swing.JButton();
        passwordPasswordField = new javax.swing.JPasswordField();
        registerButton = new javax.swing.JButton();

        jLabel1.setText("Username");

        jLabel2.setText("Password");

        loginButton.setText("Enter");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        registerButton.setText("Register");
        registerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(usernameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(passwordPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(loginButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(registerButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(usernameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(passwordPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loginButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(registerButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        if(usernameTextField.getText().equals(""))
        {
            JOptionPane.showMessageDialog(this, "Username is required");
            return;
        }
        if(passwordPasswordField.getText().equals(""))
        {
            JOptionPane.showMessageDialog(this, "Password is required");
            return;
        }
        
        sendRequest("Login");
    }//GEN-LAST:event_loginButtonActionPerformed

    private void registerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerButtonActionPerformed
        if(usernameTextField.getText().equals(""))
        {
            JOptionPane.showMessageDialog(this, "Username is required");
            return;
        }
        
        sendRequest("RegistrationStep1");
    }//GEN-LAST:event_registerButtonActionPerformed

    private void sendRequest(String type) {
        String adresseServlet = "/hobby_Web_Applic/LoginServlet";
        URL currentPage = getDocumentBase();
        String protocol = currentPage.getProtocol();
        String host = currentPage.getHost();
        int port = currentPage.getPort();
        URL urlServlet;
        URLConnection connection;
        
        try {
            urlServlet = new URL (protocol, host, port, adresseServlet);
            connection = urlServlet.openConnection();
       
            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);
            connection.setDoOutput(true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
            PrintWriter pw = new PrintWriter(baos, true);
            String u = usernameTextField.getText(); 
            String p = passwordPasswordField.getText();
            
            String infos = null;
            
            switch(type){
                case "Login":
                    infos = "username=" + URLEncoder.encode(u) + "&password=" + URLEncoder.encode(p) + "&type=" + URLEncoder.encode(type);
                    break;
                case "RegistrationStep1":
                    infos = "username=" + URLEncoder.encode(u) + "&type=" + URLEncoder.encode(type);
                    break;
            }
            
            pw.print(infos); pw.flush();
            
            String sizeData = String.valueOf(baos.size());
            connection.setRequestProperty("Content-Length", sizeData);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            baos.writeTo(connection.getOutputStream());
            BufferedReader entree = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = entree.readLine();
            
            switch(response) {
                case "OKLogin":
                    getAppletContext().showDocument(new URL(getCodeBase()+"JSPInit.jsp?username=" + u),"_top");
                    break;
                case "KOLogin":
                    JOptionPane.showMessageDialog(this, "Username/Password is incorrect");
                    break;
                case "OKRegistration":
                    getAppletContext().showDocument(new URL(getCodeBase()+"registration.html?username=" + u),"_top");
                    break;
                case "KORegistration":
                    JOptionPane.showMessageDialog(this, "Username is already in use");
                    break;
            }
                   
        } catch (MalformedURLException ex) {
            Logger.getLogger(LoginApplet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LoginApplet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton loginButton;
    private javax.swing.JPasswordField passwordPasswordField;
    private javax.swing.JButton registerButton;
    private javax.swing.JTextField usernameTextField;
    // End of variables declaration//GEN-END:variables
}
