/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ddd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GirisEkrani extends JFrame {
    private JTextField idField;
    private JTextField adField;

    public GirisEkrani() {
        setTitle("Otonom Hazine Giriş Ekranı");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1094, 800);
        setLocationRelativeTo(null); // Ekranı ortala

        // Arka plan resmini yükle
        ImageIcon background = new ImageIcon("giris.jpg");
        JLabel backgroundLabel = new JLabel(background);
        setContentPane(backgroundLabel);
        setLayout(new BorderLayout());

        // Ana paneli oluştur
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Tablo resmini çizin
                ImageIcon tableIcon = new ImageIcon("tabela.png");
                int x = (getWidth() - tableIcon.getIconWidth()) / 2;
                int y = (getHeight() - tableIcon.getIconHeight()) / 2 + 310; // Tabloyu 10 piksel aşağıya kaydır
                tableIcon.paintIcon(this, g, x, y);
            }
        };
        panel.setOpaque(false); // Panelin arka planını saydam yap

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(565, 0, 0, 0); // Giriş alanlarını 60 piksel yukarıya kaydır
        panel.add(createInputPanel(), gbc);

        // Paneli ana panele ekle
        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    // Giriş alanlarını oluştur
    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.setOpaque(false); // Arkaplanı saydam yap

        JLabel idLabel = new JLabel("Karakter ID:");
        idField = new JTextField();

        JLabel adLabel = new JLabel("Karakter Adı:");
        adField = new JTextField();

        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(adLabel);
        inputPanel.add(adField);

        // Oyuna başla butonu
        JButton baslaButton = new JButton("Oyuna Başla");
        baslaButton.addActionListener(e -> {
            String id = idField.getText();
            String ad = adField.getText();
            // OtonomHazine sınıfını çağır ve giriş ekranını kapat
            new OtonomHazine(id,ad);
            dispose(); // Giriş ekranını kapat
        });
        inputPanel.add(baslaButton);

        return inputPanel;
    }
    
}