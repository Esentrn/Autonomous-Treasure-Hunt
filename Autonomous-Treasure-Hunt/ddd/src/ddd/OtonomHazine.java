package ddd;

import java.util.Set;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

class Lokasyon {

    private int x;
    private int y;

    public Lokasyon(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

}

class Karakter {

    private String id;
    private String ad;
    private Lokasyon lokasyon;
    private OtonomHazine otonomhazine;
    // Ziyaret edilen ve görüş alanındaki hazine olmayan düğümleri tutacak set
    HashSet<Dugum> nonTreasureNodes = new HashSet<>();


    public Karakter(String id, String ad, Lokasyon lokasyon, OtonomHazine otonomhazine) {
    this.id = id;
    this.ad = ad;
    this.lokasyon = lokasyon;
    this.otonomhazine = otonomhazine;
}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    
    // Ad için get ve set metotları
    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    // Lokasyon için get ve set metotları
    public Lokasyon getLokasyon() {
        return lokasyon;
    }

    public void setLokasyon(Lokasyon lokasyon) {
        this.lokasyon = lokasyon;
    }

    // En kısa yol hesaplama metodu (Bu metodun içeriği, kullanacağınız algoritmaya göre değişebilir)
    public void enKisaYolHesapla() {
        
        // Eğer hazineler varsa ve A* algoritması bir yol bulursa, karakter hazineye gider
        boolean foundPath = findAndMoveToTreasures(nonTreasureNodes);

        // Eğer hazineye gitmek için A* algoritması kullanılmadıysa veya yol bulunamadıysa, karakter rastgele hareket eder
        if (!foundPath) {
            Random rnd = new Random();
            List<Integer> moves = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
            Collections.shuffle(moves); // Hareketlerin sırasını rastgele değiştir

            for (int move : moves) {
                Dugum nextMove = getNextMove(move, nonTreasureNodes);
                if (nextMove != null) {
                    moveCharacterTo(nextMove);
                    break;
                }
            }
        }
        otonomhazine.updateSis(otonomhazine.getKarakterX(), otonomhazine.getKarakterY(), 3);
        // Hazine toplama işlemi burada gerçekleştiriliyor
        otonomhazine.hazineTopla();
    }
    
   

// Karakterin belirli bir yönde hareket etmesini sağlayan yardımcı metot
    private Dugum getNextMove(int direction, HashSet<Dugum> nonTreasureNodes) {
        Dugum nextMove = null;
        switch (direction) {
            case 0:
                nextMove = new Dugum(otonomhazine.getKarakterX(), otonomhazine.getKarakterY() - 1);
                break;
            case 1:
                nextMove = new Dugum(otonomhazine.getKarakterX(), otonomhazine.getKarakterY() + 1);
                break;
            case 2:
                nextMove = new Dugum(otonomhazine.getKarakterX() - 1, otonomhazine.getKarakterY());
                break;
            case 3:
                nextMove = new Dugum(otonomhazine.getKarakterX() + 1, otonomhazine.getKarakterY());
                break;
        }
        if (nextMove != null && isValidMove(nextMove.getY(), nextMove.getX()) && !nonTreasureNodes.contains(nextMove)) {
            return nextMove;
        }
        return null;
    }

// Karakteri belirli bir düğüme hareket ettiren metot
    private void moveCharacterTo(Dugum dugum) {
        otonomhazine.setKarakterX(dugum.getX()); 
        otonomhazine.setKarakterY(dugum.getY());
        otonomhazine.updatePanel();
        // Görüş alanındaki hazine olmayan düğümleri ekle
        addNonTreasureNodes(dugum, nonTreasureNodes);
    }

// Görüş alanındaki hazine olmayan düğümleri ekleyen metot
    private void addNonTreasureNodes(Dugum current, HashSet<Dugum> nonTreasureNodes) {
        int viewRange = 3; // Görüş mesafesi
        for (int x = Math.max(0, current.getX() - viewRange); x <= Math.min(otonomhazine.getHaritaBoyut() - 1, current.getX() + viewRange); x++) {
            for (int y = Math.max(0, current.getY() - viewRange); y <= Math.min(otonomhazine.getHaritaBoyut() - 1, current.getY() + viewRange); y++) {
                Dugum node = new Dugum(x, y);
                if (!isTreasure(node)) {
                    nonTreasureNodes.add(node);
                }
            }
        }
    }

    private boolean isTreasure(Dugum node) {
        // Eğer 'engeller' dizisindeki ilgili konumda (node.getY(), node.getX()) hazine varsa (örneğin, değer 2 ise),
        // true döndürülür. Aksi takdirde, false döndürülür.
        return otonomhazine.getEngeller()[node.getY()][node.getX()] == 2;
    }

    public boolean findAndMoveToTreasures(HashSet<Dugum> nonTreasureNodes) {
        Dugum enYakinHazine = enYakinHazineyiBul();
        if (enYakinHazine != null) {
            AStar astar = new AStar(otonomhazine.haritayiDugumlereDonustur(otonomhazine.getEngeller()));
            ArrayList<Dugum> yol = astar.yolBul(new Dugum(otonomhazine.getKarakterX(), otonomhazine.getKarakterY()), enYakinHazine);
            if (yol != null && !yol.isEmpty()) {
                for (Dugum dugum : yol) {
                    if (!nonTreasureNodes.contains(dugum)) {
                        karakteriHareketEttir(dugum.getX(), dugum.getY());
                        nonTreasureNodes.add(dugum); // Hareket edilen düğümü kaydet
                        try {
                            Thread.sleep(300); // Karakterin hareketini göstermek için kısa bir bekleme yapılır
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    private Dugum enYakinHazineyiBul() {
        Dugum enYakinHazine = null;
        double enKisaMesafe = Double.MAX_VALUE;
        for (int x = 0; x < otonomhazine.getHaritaBoyut(); x++) {
            for (int y = 0; y <otonomhazine.getHaritaBoyut(); y++) {
                if (otonomhazine.getEngeller()[y][x] == 2) { // Eğer bu karede hazine varsa
                    double mesafe = Math.sqrt(Math.pow(x - otonomhazine.getKarakterX(), 2) + Math.pow(y - otonomhazine.getKarakterY(), 2));
                    if (mesafe < enKisaMesafe) {
                        enKisaMesafe = mesafe;
                        enYakinHazine = new Dugum(x, y);
                    }
                }
            }
        }
        return enYakinHazine;
    }

    private void karakteriHareketEttir(int x, int y) {
        otonomhazine.setKarakterX(x);
        otonomhazine.setKarakterY(y);
        otonomhazine.updateSis(otonomhazine.getKarakterX(), otonomhazine.getKarakterY(), 3);
        otonomhazine.updatePanel(); // Karakterin yeni konumunu güncelleyen metot
    }
    
    
    private boolean isValidMove(int y, int x) {
        // Yeni konum geçerli mi kontrol et
        return y >= 0 && y < otonomhazine.getHaritaBoyut() && x >= 0 && x < otonomhazine.getHaritaBoyut() && otonomhazine.getEngeller()[y][x] != 1;
    }
    
       

    /*
public boolean findAndMoveToTreasures() {
    boolean pathFound = false;
    for (int i = 0; i < haritaBoyut; i++) {
        for (int j = 0; j < haritaBoyut; j++) {
            if (engeller[i][j] == 2) { // Eğer bu karede hazine varsa
                Dugum baslangicDugum = new Dugum(karakterX, karakterY); // Karakterin bulunduğu konum başlangıç düğümü olacak
                Dugum hedefDugum = new Dugum(j, i); // Hedef düğüm, hazine konumu
                AStar astar = new AStar(haritayiDugumlereDonustur(engeller)); // A* algoritması nesnesi oluşturuldu
                ArrayList<Dugum> yol = astar.yolBul(baslangicDugum, hedefDugum); // A* algoritması ile yol bulundu
                if (yol != null) { // Eğer yol bulunmuşsa
                    pathFound = true;
                    // Karakterin hareket etmesi için gerekli güncellemeler yapılır
                    for (Dugum dugum : yol) {
                        if (!dugum.equals(baslangicDugum)) { // Başlangıç düğümü dışındaki düğümler
                            karakterX = dugum.getX(); // Karakterin X koordinatı güncellenir
                            karakterY = dugum.getY(); // Karakterin Y koordinatı güncellenir
                            updatePanel(); // Panel güncellenir
                            try {
                                Thread.sleep(500); // Karakterin hareketini göstermek için kısa bir bekleme yapılır
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break; // Hazine bulunduğunda döngüden çık
                }
            }
        }
        if (pathFound) {
            break; // Yol bulunduğunda dış döngüden de çık
        }
    }
    return pathFound;
}
     */
}

abstract class Engel {

    protected int boyutX;
    protected int boyutY;
    protected Color temaRenk;
    protected Lokasyon lokasyon;

    public Engel(int boyutX, int boyutY, Color temaRenk, Lokasyon lokasyon) {
        this.boyutX = boyutX;
        this.boyutY = boyutY;
        this.temaRenk = temaRenk;
        this.lokasyon = lokasyon;
    }

   
    public abstract void ciz(Graphics g, int x, int y, int birimKareBoyutu, int haritaBoyut);  // Engeli ekrana çizme metodu

      public Lokasyon getLokasyon() {
        return lokasyon;
    }
}

class SabitEngel extends Engel {

    public SabitEngel(int boyutX, int boyutY, Color temaRenk, Lokasyon lokasyon) {
        super(boyutX, boyutY, temaRenk, lokasyon);
    }

    @Override
    public void ciz(Graphics g, int x, int y, int birimKareBoyutu, int haritaBoyut) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

class HareketliEngel extends Engel {

    public HareketliEngel(int boyutX, int boyutY, Color temaRenk, Lokasyon lokasyon) {
        super(boyutX, boyutY, temaRenk, lokasyon);
    }

    @Override
    public void ciz(Graphics g, int x, int y, int birimKareBoyutu, int haritaBoyut) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

class Agac extends SabitEngel {

    public Agac(int boyutX, int boyutY, Color temaRenk, Lokasyon lokasyon) {
        super(boyutX, boyutY, temaRenk, lokasyon);
    }

    public void ciz(Graphics g, int x, int y, int birimKareBoyutu, int haritaBoyut) {
        if (y < haritaBoyut / 2) { // Corrected condition
            Image resim = new ImageIcon("agac2.png").getImage();
            g.drawImage(resim, y * birimKareBoyutu, x * birimKareBoyutu, boyutX * birimKareBoyutu, boyutY * birimKareBoyutu, null);
        } else {
            Image resim = new ImageIcon("agac.png").getImage();
            g.drawImage(resim, y * birimKareBoyutu, x * birimKareBoyutu, boyutX * birimKareBoyutu, boyutY * birimKareBoyutu, null);
        }
    }
}

class Kaya extends SabitEngel {

    public Kaya(int boyutX, int boyutY, Color temaRenk, Lokasyon lokasyon) {
        super(boyutX, boyutY, temaRenk, lokasyon);
    }

    public void ciz(Graphics g, int x, int y, int birimKareBoyutu, int haritaBoyut) {
        if (y < haritaBoyut / 2) { // Corrected condition
            Image resim = new ImageIcon("kaya2.png").getImage();
            g.drawImage(resim, y * birimKareBoyutu, x * birimKareBoyutu, boyutX * birimKareBoyutu, boyutY * birimKareBoyutu, null);
        } else {
            Image resim = new ImageIcon("kaya.png").getImage();
            g.drawImage(resim, y * birimKareBoyutu, x * birimKareBoyutu, boyutX * birimKareBoyutu, boyutY * birimKareBoyutu, null);
        }
    }
}

class Duvar extends SabitEngel {

    public Duvar(int boyutX, int boyutY, Color temaRenk, Lokasyon lokasyon) {
        super(boyutX, boyutY, temaRenk, lokasyon);
    }

    public void ciz(Graphics g, int x, int y, int birimKareBoyutu, int haritaBoyut) {
        if (y < haritaBoyut / 2) { // Corrected condition
            Image resim = new ImageIcon("duvar4.jpg").getImage();
            g.drawImage(resim, y * birimKareBoyutu, x * birimKareBoyutu, boyutX * birimKareBoyutu, boyutY * birimKareBoyutu, null);
        } else {
            Image resim = new ImageIcon("duvar3.jpg").getImage();
            g.drawImage(resim, y * birimKareBoyutu, x * birimKareBoyutu, boyutX * birimKareBoyutu, boyutY * birimKareBoyutu, null);
        }
    }
}

class Dag extends SabitEngel {

    public Dag(int boyutX, int boyutY, Color temaRenk, Lokasyon lokasyon) {
        super(boyutX, boyutY, temaRenk, lokasyon);
    }

    public void ciz(Graphics g, int x, int y, int birimKareBoyutu, int haritaBoyut) {
        if (y < haritaBoyut / 2) { // Corrected condition
            Image resim = new ImageIcon("dag2.png").getImage();
            g.drawImage(resim, y * birimKareBoyutu, x * birimKareBoyutu, boyutX * birimKareBoyutu, boyutY * birimKareBoyutu, null);
        } else {
            Image resim = new ImageIcon("dag.png").getImage();
            g.drawImage(resim, y * birimKareBoyutu, x * birimKareBoyutu, boyutX * birimKareBoyutu, boyutY * birimKareBoyutu, null);
        }
    }
}

class Kus extends HareketliEngel {

    public Kus(int boyutX, int boyutY, Color temaRenk, Lokasyon lokasyon) {
        super(2, 2, temaRenk, lokasyon);
    }

    public void ciz(Graphics g, int x, int y, int birimKareBoyutu, int haritaBoyut) {

        Image resim = new ImageIcon("kus.png").getImage();
        g.drawImage(resim, y * birimKareBoyutu, x * birimKareBoyutu, boyutX * birimKareBoyutu, boyutY * birimKareBoyutu, null);
    }

}

class Ari extends HareketliEngel {

    public Ari(int boyutX, int boyutY, Color temaRenk, Lokasyon lokasyon) {
        super(2, 2, temaRenk, lokasyon);
    }

    @Override
    public void ciz(Graphics g, int x, int y, int birimKareBoyutu, int haritaBoyut) {

        Image resim = new ImageIcon("arı.png").getImage();
        g.drawImage(resim, y * birimKareBoyutu, x * birimKareBoyutu, boyutX * birimKareBoyutu, boyutY * birimKareBoyutu, null);
    }

}

class HazineSandik extends Engel {

    private String tur; // altin, gumus, zumrut, bakir
    private boolean gorunur = true;

    public HazineSandik(String tur, Lokasyon lokasyon) {
        super(1, 1, Color.blue, lokasyon);
        this.tur = tur;
    }

    public void setGorunurluk(boolean gorunur) {
        this.gorunur = gorunur;
    }

    public boolean isGorunur() {
        return gorunur;
    }

    public String getTur() {
        return tur;
    }

  
    @Override
    public void ciz(Graphics g, int x, int y, int birimKareBoyutu, int haritaBoyut) {
        if (tur == "altın") {
            Image resim = new ImageIcon("hazine.png").getImage();
            g.drawImage(resim, y * birimKareBoyutu, x * birimKareBoyutu, boyutX * birimKareBoyutu, boyutY * birimKareBoyutu, null);
        } else if (tur == "zumrut") {
            Image resim = new ImageIcon("zumrut.png").getImage();
            g.drawImage(resim, y * birimKareBoyutu, x * birimKareBoyutu, boyutX * birimKareBoyutu, boyutY * birimKareBoyutu, null);
        } else if (tur == "gumus") {
            Image resim = new ImageIcon("gumus.png").getImage();
            g.drawImage(resim, y * birimKareBoyutu, x * birimKareBoyutu, boyutX * birimKareBoyutu, boyutY * birimKareBoyutu, null);
        } else if (tur == "bakır") {
            Image resim = new ImageIcon("bakır.png").getImage();
            g.drawImage(resim, y * birimKareBoyutu, x * birimKareBoyutu, boyutX * birimKareBoyutu, boyutY * birimKareBoyutu, null);
        }

    }
}


public class OtonomHazine extends JFrame {

    private int haritaBoyut;
    private JPanel[][] cells;
    private int[][] engeller;
    private int karakterX, karakterY;
    private Lokasyon lokasyon = new Lokasyon(karakterX, karakterY);
   // private Karakter karakter = new Karakter(1, "oyuncu", lokasyon);
    private JPanel buttonPanel;
    private Color yazTemaRenk = Color.YELLOW;
    private static int birimKareBoyutu;
    private Engel[][] engelObje;
    private Kus kus;
    private Ari ari;
    private int[][] hazineler;
    private boolean[][] engelMatris = new boolean[haritaBoyut][haritaBoyut];
    //  private List<Node> hazinedugumler;
    private int kusHareketMesafesi = 5;
    private int ariHareketMesafesi = 3;
    private Timer kusTimer;
    private Timer ariTimer;
    private Set<Dugum> ziyaretEdilenKonumlar = new HashSet<>();
    // private ArrayList<int[]> toplananlar = new ArrayList<>();
    private Karakter karakter;

    public int getHaritaBoyut() {
        return haritaBoyut;
    }

    public int[][] getEngeller() {
        return engeller;
    }

   
    public OtonomHazine(String id,String ad) {
        
        this.karakter = new Karakter("id", "ad", new Lokasyon(0, 0),this);
        JButton yeniHaritaButton = new JButton("Yeni Harita Oluştur");
        yeniHaritaButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Harita boyutunu girin:");
            try {
                haritaBoyut = Integer.parseInt(input);
                initializeGame();
                haritayiDugumlereDonustur(engeller);
                updatePanel();
                printMatrix();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Geçersiz bir sayı girdiniz. Lütfen tekrar deneyin.");
            }
        });

        setLayout(new BorderLayout());

        buttonPanel = new JPanel();
        buttonPanel.add(yeniHaritaButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buttonPanel, BorderLayout.LINE_START);
        add(panel, BorderLayout.PAGE_START);
        JButton baslat = new JButton("Başlat");
        baslat.addActionListener(e -> {

            Timer moveTimer = new Timer();
            TimerTask moveTask = new TimerTask() {
                @Override
                public void run() {
                    karakter.enKisaYolHesapla();
                 
                }
            };
            moveTimer.schedule(moveTask, 0, 10);
        });

//hazinedugumler = new ArrayList<>();
        //hazineler = new int[haritaBoyut][haritaBoyut]; // Bu satırı ekleyin
        kus = new Kus(2, 2, Color.CYAN, new Lokasyon(0, 0));
        ari = new Ari(2, 2, Color.RED, new Lokasyon(0, 0)); // Initialize ari object

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // moveCharacter(e.getKeyCode());
                printMatrix();
                updatePanel(); // Ekranı güncelle

            }
        });
        kusTimer = new Timer();
        ariTimer = new Timer();
        startTimers();

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                hareketliEngelleriHareketEttir();
                hareketliEngelleriHareketEttir2();
            }
        };
        timer.schedule(task, 0, 500);
        JButton sisButonu = new JButton("Sis Ekle");
        sisButonu.addActionListener(e -> {

            for (int i = 0; i < haritaBoyut; i++) {
                Arrays.fill(sisliMi[i], true); // Sisi kaldır

                updatePanel(); // Paneli yeniden çiz
            }
        });

        // Sis butonunu panelinize ekleyin
        buttonPanel.add(sisButonu);
        buttonPanel.add(baslat);
        setFocusable(true);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

// Toplanan hazine sayısını ve türünü tutacak değişken
    private int toplananHazineSayisi = 0;
    private List<Object[]> toplananlar = new ArrayList<>(); // Hazine türü de eklendi

    public void hazineTopla() {
        int row = karakterY;
        int col = karakterX;

        if (row >= 0 && row < haritaBoyut && col >= 0 && col < haritaBoyut) {
            // Karakterin üzerinde hazine var mı kontrol et
            if (engeller[row][col] == 2) {
                HazineSandik hazine = (HazineSandik) engelObje[row][col];
                if (hazine.isGorunur()) {
                    hazine.setGorunurluk(false); // Hazineyi kaybet
                    toplananHazineSayisi++; // Toplanan hazine sayısını artır
                    toplananlar.add(new Object[]{row, col, hazine.getTur()}); // Hazine türünü de ekleyin

                    System.out.println("Toplanan hazine sayısı: " + toplananHazineSayisi);

                    // Hazine sandığını haritadan kaldır ve paneli güncelle
                    engeller[row][col] = 0;
                    engelObje[row][col] = null;

                    updatePanel();
                }
            }
        }
    }

    public void toplananlariYazdir(JTextArea textArea) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < toplananHazineSayisi; i++) {
            sb.append("Hazine ").append(i + 1).append(": Tür - ").append(toplananlar.get(i)[2]).append(", Satır - ").append(toplananlar.get(i)[0]).append(", Sütun - ").append(toplananlar.get(i)[1]).append("\n");
        }
        textArea.setText(sb.toString());
    }

    private boolean isValidMove(int y, int x) {
        // Yeni konum geçerli mi kontrol et
        return y >= 0 && y < haritaBoyut && x >= 0 && x < haritaBoyut && engeller[y][x] != 1;
    }

    private void placeHazineSandiklar() {
        Random rand = new Random();
        int maxTrialCount = 10000;
        int treasureCount = 20; // Toplam hazine sayısı
        int perTypeCount = 5; // Her türden hazine sayısı
        String[] turler = {"altın", "gumus", "zumrut", "bakır"};
        int[] typeCounts = {0, 0, 0, 0}; // Her tür için sayacı sıfırla

        hazineler = new int[haritaBoyut][haritaBoyut];

        while (Arrays.stream(typeCounts).sum() < treasureCount) {
            for (int typeIndex = 0; typeIndex < turler.length; typeIndex++) {
                while (typeCounts[typeIndex] < perTypeCount) {
                    int trialCount = 0;
                    boolean placed = false;
                    while (!placed && trialCount < maxTrialCount) {
                        int row = rand.nextInt(haritaBoyut);
                        int col = rand.nextInt(haritaBoyut);

                        // Geçerli indeks kontrolü ve hazine yerleştirme koşulları
                        if (row >= 0 && row < haritaBoyut && col >= 0 && col < haritaBoyut && engeller[row][col] == 0) {
                            // Hazine sandığı oluşturun ve haritaya ekleyin
                            HazineSandik hazine = new HazineSandik(turler[typeIndex], new Lokasyon(row, col));
                            if (isValidPlacement(row, col, hazine)) {
                                placeEngel(row, col, hazine);
                                engeller[row][col] = 2; // Hazine yerleştirildikten sonra engeller dizisini güncelle
                                engelObje[row][col] = hazine;
                                typeCounts[typeIndex]++; // Bu türden hazine sayısını artır
                                placed = true;
                            }
                        } else {
                            // Geçersiz indeks, uygun işlemi yapın veya hata mesajını yazdırın.
                            System.err.println("Geçersiz indeks: row=" + row + ", col=" + col);
                        }
                        trialCount++;
                    }
                    if (!placed) {
                        System.err.println("Hazine yerleştirilemedi, maksimum deneme sayısına ulaşıldı.");
                        break;
                    }
                }
            }
        }
    }

    private void startTimers() {
        kusTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> moveKus());
            }
        }, 0, 500); // Update the period to a positive value (e.g., 500 milliseconds)

        ariTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> moveAri());
            }
        }, 0, 500); // Update the period to a positive value (e.g., 500 milliseconds)
    }

    private void moveKus() {
        int newY = Math.min(0, kus.getLokasyon().getY());
        kus.getLokasyon().setY(newY);

        //System.out.println("Kus moved to: " + kus.getLokasyon().getX());
        updatePanel();
    }

    private void moveAri() {
        int newX = Math.max(0, ari.getLokasyon().getX() - 1);
        ari.getLokasyon().setX(newX);
        // System.out.println("Ari moved to: " + ari.getLokasyon().getX());
        updatePanel();
    }
    private int hareketSayaci = 0;
    private int hareketSayaci2 = 0;

    private void hareketliEngelleriHareketEttir() {
        moveKus(); // Kus'un hareketini sağla

        hareketSayaci++;

        if (hareketSayaci <= kusHareketMesafesi) {

            moveEngelsRight2();
        } else if (hareketSayaci > kusHareketMesafesi && hareketSayaci <= 10) {

            moveEngelsLeft2();
        } else {
            hareketSayaci = 0; // Sayaç sıfırlanıyor, tekrar sağa hareket edecek
        }

        updatePanel();
    }

    private void hareketliEngelleriHareketEttir2() {

        moveAri();
        hareketSayaci2++;

        if (hareketSayaci2 <= ariHareketMesafesi) {
            moveEngelsRight();

        } else if (hareketSayaci2 > ariHareketMesafesi && hareketSayaci2 <= 6) {
            moveEngelsLeft();

        } else {
            hareketSayaci2 = 0; // Sayaç sıfırlanıyor, tekrar sağa hareket edecek
        }

        updatePanel();
    }

    private void moveEngelsRight() {
        for (int i = 0; i < haritaBoyut; i++) {
            for (int j = haritaBoyut - 1; j >= 0; j--) {
                if (engeller[i][j] == 1 && j < haritaBoyut - 1 && engeller[i][j + 1] == 0) {
                    if (engelObje[i][j] instanceof Ari) {
                        moveAriRight(i, j);
                    }
                }
            }
        }
    }

    private void moveEngelsLeft() {
        for (int i = 0; i < haritaBoyut; i++) {
            for (int j = 0; j < haritaBoyut - 1; j++) {
                if (engeller[i][j] == 1 && j >= 1 && engeller[i][j - 1] == 0) {
                    if (engelObje[i][j] instanceof Ari) {
                        moveAriLeft(i, j);

                    }
                }
            }
        }
    }

    private void moveEngelsRight2() {
        for (int j = 0; j < haritaBoyut; j++) {
            for (int i = haritaBoyut - 1; i >= 0; i--) {
                if (engeller[i][j] == 1 && i < haritaBoyut - 1 && engeller[i + 1][j] == 0) {
                    if (engelObje[i][j] instanceof Kus) {
                        moveKusDown(i, j);
                    }
                }
            }
        }
    }

    private void moveEngelsLeft2() {
        for (int j = 0; j < haritaBoyut; j++) {
            for (int i = 0; i < haritaBoyut - 1; i++) {
                if (engeller[i][j] == 1 && i >= 1 && engeller[i - 1][j] == 0) {
                    if (engelObje[i][j] instanceof Kus) {
                        moveKusUp(i, j);

                    }
                }
            }
        }
    }

// moveKusUp fonksiyonu
    private void moveKusUp(int i, int j) {
        if (i > 0) { // Dizinin üst sınırını kontrol et
            engeller[i][j] = 0;
            engeller[i - 1][j] = 1;
            engelObje[i - 1][j] = engelObje[i][j];
            engelObje[i][j] = null;
            engelObje[i - 1][j].getLokasyon().setX(i - 1); // Yeni konumu güncelle
        }
    }

// moveKusDown fonksiyonu
    private void moveKusDown(int i, int j) {
        if (i < haritaBoyut - 1) { // Dizinin alt sınırını kontrol et
            engeller[i][j] = 0;
            engeller[i + 1][j] = 1;
            engelObje[i + 1][j] = engelObje[i][j];
            engelObje[i][j] = null;
            engelObje[i + 1][j].getLokasyon().setX(i + 1); // Yeni konumu güncelle
        }
    }

    private void moveAriRight(int i, int j) {
        engeller[i][j] = 0;
        engeller[i][j + 1] = 1;
        engelObje[i][j + 1] = engelObje[i][j];
        engelObje[i][j] = null;
        engelObje[i][j + 1].getLokasyon().setY(j + 1); // Yeni konumu güncelle
    }

// moveAriLeft fonksiyonu
    private void moveAriLeft(int i, int j) {
        engeller[i][j] = 0;
        engeller[i][j - 1] = 1;
        engelObje[i][j - 1] = engelObje[i][j];
        engelObje[i][j] = null;
        engelObje[i][j - 1].getLokasyon().setY(j - 1); // Yeni konumu güncelle
    }

    private void initializeGame() {
        cells = new JPanel[haritaBoyut][haritaBoyut];
        engeller = new int[haritaBoyut][haritaBoyut];
        karakterX = haritaBoyut / 2;
        karakterY = haritaBoyut / 2;
        engelObje = new Engel[haritaBoyut][haritaBoyut];
        initializeSis();
        placeHazineSandiklar();
        placeEngellers();

    }

    private void placeEngellers() {
        Random rand = new Random();
        int maxDenemeSayisi = 10000;

        int engelSayi = 0;

        int a;
        int denemeSayisi = 0;
        int dag = 0;
        while (dag < 2 && denemeSayisi < maxDenemeSayisi) {
            int row = rand.nextInt(haritaBoyut);
            int col = rand.nextInt(haritaBoyut);

            System.out.println("row: " + row + ", col: " + col); // Ekle

            if (row != karakterY && col != karakterX && haritaBoyut > 0) {
                Engel engel;

                if (col < haritaBoyut / 2) {
                    engel = new Dag(15, 15, Color.white, new Lokasyon(row, col));
                } else {
                    engel = new Dag(15, 15, Color.RED, new Lokasyon(row, col));
                }
                if (isValidPlacement(row, col, engel)) {
                    placeEngel(row, col, engel);
                    dag++;
                    engelSayi++;
                    System.out.println("aaaaaaaaaaaaaaaaaaaa");
                }
            }

            denemeSayisi++;
            if (denemeSayisi == maxDenemeSayisi) {
                System.out.println("Engel yerleştirme işlemi başarısız oldu. Maksimum deneme sayısına ulaşıldı.");
            }
        }

        int duvar = 0;
        denemeSayisi = 0;
        while (duvar < 2 && denemeSayisi < maxDenemeSayisi) {
            int row = rand.nextInt(haritaBoyut);
            int col = rand.nextInt(haritaBoyut);

            if (row != karakterY && col != karakterX && haritaBoyut > 0) {
                Engel engel;

                if (col < haritaBoyut / 2) {
                    engel = new Duvar(10, 1, Color.cyan, new Lokasyon(row, col));
                } else {
                    engel = new Duvar(10, 1, Color.ORANGE, new Lokasyon(row, col));
                }

                if (isValidPlacement(row, col, engel)) {
                    placeEngel(row, col, engel);
                    duvar++;
                    engelSayi++;
                    System.out.println("Duvar placed successfully");
                }
            }

            denemeSayisi++;
        }

        denemeSayisi = 0;
        int agac = 0;
        while (agac < 2 && denemeSayisi < maxDenemeSayisi) {
            int row = rand.nextInt(haritaBoyut);
            int col = rand.nextInt(haritaBoyut);

            System.out.println("row: " + row + ", col: " + col); // Ekle

            if (row != karakterY && col != karakterX && haritaBoyut > 0) {
                Engel engel;

                a = 2 + rand.nextInt(5);
                if (col < haritaBoyut / 2) {
                    engel = new Agac(a, a, Color.GREEN, new Lokasyon(row, col));

                } else {
                    engel = new Agac(a, a, Color.GRAY, new Lokasyon(row, col));

                }
                if (isValidPlacement(row, col, engel)) {
                    placeEngel(row, col, engel);
                    agac++;
                    engelSayi++;
                }
            }

            denemeSayisi++;
            if (denemeSayisi == maxDenemeSayisi) {
                System.out.println("Engel yerleştirme işlemi başarısız oldu. Maksimum deneme sayısına ulaşıldı.");
            }
        }

        denemeSayisi = 0;
        int kaya = 0;
        while (kaya < 2 && denemeSayisi < maxDenemeSayisi) {
            int row = rand.nextInt(haritaBoyut);
            int col = rand.nextInt(haritaBoyut);

            System.out.println("row: " + row + ", col: " + col); // Ekle

            if (row != karakterY && col != karakterX && haritaBoyut > 0) {
                Engel engel;

                a = 2 + rand.nextInt(4);
                if (col < haritaBoyut / 2) {
                    engel = new Kaya(a, a, Color.BLACK, new Lokasyon(row, col));
                } else {
                    engel = new Kaya(a, a, Color.darkGray, new Lokasyon(row, col));
                }
                if (isValidPlacement(row, col, engel)) {
                    placeEngel(row, col, engel);
                    engelSayi++;
                    kaya++;
                }
            }
            denemeSayisi++;
            if (denemeSayisi == maxDenemeSayisi) {
                System.out.println("Engel yerleştirme işlemi başarısız oldu. Maksimum deneme sayısına ulaşıldı.");
            }
        }

        denemeSayisi = 0;
       int b = 0;
        if (haritaBoyut < 30) {
            b = 20;
        } 
        else if (haritaBoyut <80) {
            b = 40;
        }
        else if (haritaBoyut <120){
            b = 80;
        }
        else if (haritaBoyut <180){
            b = 100;
        }
        
        else{
            b=120;
}
        for (; engelSayi < b; engelSayi++) {

            while (denemeSayisi < maxDenemeSayisi) {
                int row = rand.nextInt(haritaBoyut);
                int col = rand.nextInt(haritaBoyut);

                System.out.println("row: " + row + ", col: " + col); // Ekle

                if (row != karakterY && col != karakterX && haritaBoyut > 0) {
                    Engel engel;
                    int engelTipi = rand.nextInt(6);

                    System.out.println("engelTipi: " + engelTipi); // Ekle
                    switch (engelTipi) {
                        case 0:
                            a = 1 + rand.nextInt(5);
                            if (col < haritaBoyut / 2) {

                                engel = new Agac(a, a, Color.GREEN, new Lokasyon(row, col));
                            } else {
                                engel = new Agac(a, a, Color.GRAY, new Lokasyon(row, col));
                            }

                            break;
                        case 1:
                            a = 1 + rand.nextInt(4);
                            if (col < haritaBoyut / 2) {
                                engel = new Kaya(a, a, Color.BLACK, new Lokasyon(row, col));
                            } else {
                                engel = new Kaya(a, a, Color.darkGray, new Lokasyon(row, col));
                            }

                            break;
                        case 2:

                            if (col < haritaBoyut / 2) {
                                engel = new Duvar(10, 1, Color.cyan, new Lokasyon(row, col));
                            } else {
                                engel = new Duvar(10, 1, Color.ORANGE, new Lokasyon(row, col));
                            }

                            break;
                        case 3:
                            if (col < haritaBoyut / 2) {
                                engel = new Dag(15, 15, Color.white, new Lokasyon(row, col));
                            } else {
                                engel = new Dag(15, 15, Color.RED, new Lokasyon(row, col));
                            }

                            break;
                        default:
                            engel = new Agac(3, 3, Color.PINK, new Lokasyon(row, col));
                    }

                    if (isValidPlacement(row, col, engel)) {
                        placeEngel(row, col, engel);
                        engelSayi++;
                        break;
                    }
                }
                denemeSayisi++;
            }

            if (denemeSayisi == maxDenemeSayisi) {
                System.out.println("Engel yerleştirme işlemi başarısız oldu. Maksimum deneme sayısına ulaşıldı.");
            }
        }

        denemeSayisi = 0;
        int kus = 0;
        int c;
        if (haritaBoyut < 50) {
            c = 1;
        } else {
            c = 2;
        }
        while (kus != c && denemeSayisi < maxDenemeSayisi) {
            int row = rand.nextInt(haritaBoyut);
            int col = rand.nextInt(haritaBoyut);

            System.out.println("row: " + row + ", col: " + col); // Ekle

            if (row != karakterY && col != karakterX && haritaBoyut > 0) {
                Engel engel;

                engel = new Kus(2, 2, Color.cyan, new Lokasyon(row, col));
                if (isValidPlacement(row, col, engel)) {
                    placeEngel(row, col, engel);
                    engelSayi++;
                    kus++;

                }
            }

            denemeSayisi++;
            if (denemeSayisi == maxDenemeSayisi) {
                System.out.println("Engel yerleştirme işlemi başarısız oldu. Maksimum deneme sayısına ulaşıldı.");
       }
}

        denemeSayisi = 0;
        int ari = 0;
        while (ari < 2 && denemeSayisi < maxDenemeSayisi) {
            int row = rand.nextInt(haritaBoyut);
            int col = rand.nextInt(haritaBoyut);

            System.out.println("row: " + row + ", col: " + col); // Ekle

            if (row != karakterY && col != karakterX && haritaBoyut > 0) {
                Engel engel;

                engel = new Ari(3, 3, Color.RED, new Lokasyon(row, col));
                if (isValidPlacement(row, col, engel)) {
                    placeEngel(row, col, engel);
                    engelSayi++;
                    ari++;

                    // Arının hareket alanını işaretle
                }
            }

            denemeSayisi++;
            if (denemeSayisi == maxDenemeSayisi) {
                System.out.println("Engel yerleştirme işlemi başarısız oldu. Maksimum deneme sayısına ulaşıldı.");
            }
        }

    }

    private boolean isValidPlacement(int row, int col, Engel engel) {
        if (col < haritaBoyut / 2 && col + engel.boyutX > haritaBoyut / 2) {
            return false;
        }

        if (engel instanceof Kus) {
            if (row < kusHareketMesafesi || row + engel.boyutY + kusHareketMesafesi > haritaBoyut) {
                return false;
            }
            for (int i = row - kusHareketMesafesi; i < row + engel.boyutY + kusHareketMesafesi; i++) {
                for (int j = col; j < col + engel.boyutX; j++) {
                    if (i >= haritaBoyut || j >= haritaBoyut || engeller[i][j] == 1 || engeller[i][j] == 2) {
                        System.out.println("WWWWWWWWWWWWWWWWWWWWWWWW");
                        return false;
                    }
                }
            }

        }

        if (engel instanceof Ari) {
            if (col < ariHareketMesafesi || col + engel.boyutX + ariHareketMesafesi > haritaBoyut) {
                return false;
            }
            for (int i = row; i < row + engel.boyutY; i++) {
                for (int j = col - ariHareketMesafesi; j < col + engel.boyutX + ariHareketMesafesi; j++) {
                    if (i >= haritaBoyut || j >= haritaBoyut || engeller[i][j] == 1 || engeller[i][j] == 2) {
                        System.out.println("XXXXXXXXXX");
                        return false;
                    }
                }
            }

        }

        // Diğer durumlar için geçiş kontrolleri
        for (int i = row; i < row + engel.boyutY; i++) {
            for (int j = col; j < col + engel.boyutX; j++) {
                if (i >= haritaBoyut || j >= haritaBoyut || engeller[i][j] == 1 || engeller[i][j] == 2) {
                    return false;
                }
            }
        }
        return true;
    }

// Engel yerleştirme fonksiyonunuzda bu fonksiyonları kullanabilirsiniz:
    private void placeEngel(int row, int col, Engel engel) {

        for (int i = row; i < row + engel.boyutY; i++) {
            for (int j = col; j < col + engel.boyutX; j++) {

                engeller[i][j] = 1;

                engelObje[i][j] = engel;
            }
        }

    }

    private boolean[][] sisliMi;

    private void initializeSis() {
        sisliMi = new boolean[haritaBoyut][haritaBoyut];
        for (int i = 0; i < haritaBoyut; i++) {
            Arrays.fill(sisliMi[i], false); // Başlangıçta tüm haritayı sis ile doldur
        }
    }

    public void updateSis(int karakterX, int karakterY, int gorusAlani) {
        for (int i = Math.max(0, karakterY - gorusAlani); i <= Math.min(haritaBoyut - 1, karakterY + gorusAlani); i++) {
            for (int j = Math.max(0, karakterX - gorusAlani); j <= Math.min(haritaBoyut - 1, karakterX + gorusAlani); j++) {
                sisliMi[i][j] = false; // Karakterin görüş alanındaki sisleri kaldır
            }
        }
    }

    public void updatePanel() {
        if (haritaBoyut > 0) {
            if (getContentPane().getComponentCount() > 1) {
                remove(getContentPane().getComponent(1));
            }

            JPanel haritaPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);

                    birimKareBoyutu = calculateBirimKareBoyutu();

                    // Draw background images
                    Image yazImage = new ImageIcon("yaz22.jpg").getImage();
                    Image kisImage = new ImageIcon("kıs22.jpg").getImage();
                    g.drawImage(kisImage, 0, 0, (haritaBoyut / 2) * birimKareBoyutu, haritaBoyut * birimKareBoyutu, null);
                    g.drawImage(yazImage, (haritaBoyut / 2) * birimKareBoyutu, 0, (haritaBoyut / 2) * birimKareBoyutu, haritaBoyut * birimKareBoyutu, null);

                    for (int i = 0; i < haritaBoyut; i++) {
                        for (int j = 0; j < haritaBoyut; j++) {

                            g.setColor(Color.BLACK);
                            g.drawRect(j * birimKareBoyutu, i * birimKareBoyutu, birimKareBoyutu, birimKareBoyutu);
                        }
                    }
                    boolean[][] cizildiMi = new boolean[haritaBoyut][haritaBoyut];
                    for (int i = 0; i < haritaBoyut; i++) {
                        for (int j = 0; j < haritaBoyut; j++) {
                            if (engeller[i][j] == 1 && !cizildiMi[i][j] && engelObje[i][j] != null) {
                                engelObje[i][j].ciz(g, i, j, birimKareBoyutu, haritaBoyut);
                                // Engel boyutuna göre cizildiMi matrisini güncelle
                                for (int k = 0; k < engelObje[i][j].boyutY; k++) {
                                    for (int l = 0; l < engelObje[i][j].boyutX; l++) {
                                        if (i + k < haritaBoyut && j + l < haritaBoyut) {
                                            cizildiMi[i + k][j + l] = true;
                                        }
                                    }
                                }
                            } else if (engeller[i][j] == 2 && !cizildiMi[i][j] && engelObje[i][j] != null) {
                                engelObje[i][j].ciz(g, i, j, birimKareBoyutu, haritaBoyut);
                                cizildiMi[i][j] = true; // Hazine sandığı çizildi olarak işaretle
                            }

                            // Karakteri çiz
                            if (!sisliMi[karakterY][karakterX]) {
                                g.setColor(Color.RED);
                                g.fillOval(karakterX * birimKareBoyutu, karakterY * birimKareBoyutu, birimKareBoyutu, birimKareBoyutu);
                            }
                        }
                    }
                    // Sis efektini çiz
                    g.setColor(new Color(192, 192, 192)); // Yarı saydam gri renk
                    for (int i = 0; i < haritaBoyut; i++) {
                        for (int j = 0; j < haritaBoyut; j++) {
                            if (sisliMi[i][j]) {
                                g.fillRect(j * birimKareBoyutu, i * birimKareBoyutu, birimKareBoyutu, birimKareBoyutu);
                            }
                        }
                    }
                }
            };

// Harita panelini ayarla
            haritaPanel.setPreferredSize(new Dimension(haritaBoyut * birimKareBoyutu, haritaBoyut * birimKareBoyutu));
            haritaPanel.setFocusable(true);

// Harita panelini ana panele ekle
            add(haritaPanel, BorderLayout.CENTER);

// Hazine sayısını gösteren etiketleri oluştur
            JLabel hazineSayisiEtiket = new JLabel("Toplanan Hazine Sayısı: " + toplananHazineSayisi);
            JTextArea hazineKonumEtiket = new JTextArea();
            hazineKonumEtiket.setEditable(false); // Kullanıcının metni düzenlemesini engelle

// Etiketler için yeni bir panel oluştur
            JPanel etiketPanel = new JPanel();
            etiketPanel.setLayout(new BoxLayout(etiketPanel, BoxLayout.Y_AXIS)); // Dikey hizalama için BoxLayout kullan

// Hazine sayısını gösteren etiketi etiket paneline ekle
            etiketPanel.add(hazineSayisiEtiket);

// Hazine konumlarını gösteren JTextArea'yı bir JScrollPane içine koy
            JScrollPane konumScrollPane = new JScrollPane(hazineKonumEtiket);
            konumScrollPane.setPreferredSize(new Dimension(200, haritaPanel.getHeight()));
            etiketPanel.add(konumScrollPane);

// Ana panelin doğu tarafına etiket panelini ekle
            JPanel anaPanel = new JPanel(new BorderLayout());
            anaPanel.add(haritaPanel, BorderLayout.CENTER);
            anaPanel.add(etiketPanel, BorderLayout.EAST);

// toplananlariYazdir metodunu çağır ve hazine konumlarını güncelle
            toplananlariYazdir(hazineKonumEtiket);

// Ana paneli frame'e ekle
            add(anaPanel);

// Frame'i güncelle
            revalidate();
            repaint();

// Harita paneline odaklan
            haritaPanel.requestFocusInWindow();
        }
    }

    private int calculateBirimKareBoyutu() {
        if (haritaBoyut == 0) {
            return 1; // veya başka bir varsayılan değer
        }

        int genislik = 1000;
        int yukseklik = 802;
        int minBoyut = Math.min(genislik, yukseklik);

        if (haritaBoyut >= 750) {
            return 1;
        } else {
            return minBoyut / haritaBoyut;
        }
    }

    private void printMatrix() {
        System.out.println("Matris Durumu:");
        for (int i = 0; i < haritaBoyut; i++) {
            for (int j = 0; j < haritaBoyut; j++) {
                System.out.print(engeller[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(GirisEkrani::new);

    }

    public Dugum[][] haritayiDugumlereDonustur(int[][] engeller) {
        int haritaBoyut = engeller.length;
        Dugum[][] dugumHaritasi = new Dugum[haritaBoyut][haritaBoyut];
        OncelikliKuyruk oncelikliKuyruk = new OncelikliKuyruk();

        for (int i = 0; i < haritaBoyut; i++) {
            for (int j = 0; j < haritaBoyut; j++) {
                Dugum dugum = new Dugum(j, i);
                if (engeller[i][j] == 1) { // Engel varsa
                    dugum.sethCost(Double.MAX_VALUE); // Engel olan yerlere geçilemez olarak işaretlenir
                } else if (engeller[i][j] == 0) { // Boşluk varsa
                    dugum.sethCost(5); // Boşluklar için maliyet
                } else if (engeller[i][j] == 2) { // Hazine varsa
                    dugum.sethCost(1); // Hazineler için maliyet
                }
                dugumHaritasi[i][j] = dugum;
                oncelikliKuyruk.dugumEkle(dugum);
            }
        }

        // Kuyruktaki düğümleri sıralı bir şekilde çıkar ve kontrol et
        while (!oncelikliKuyruk.getKuyruk().isEmpty()) {
            Dugum dugum = oncelikliKuyruk.dugumGetir();
            //System.out.println("Dugum: x=" + dugum.getX() + ", y=" + dugum.getY() + ", hCost=" + dugum.gethCost());
        }

        return dugumHaritasi;
    }
     public int getKarakterX() {
        return karakterX;
    }

    public int getKarakterY() {
        return karakterY;
    }

    public void setKarakterX(int karakterX) {
        this.karakterX = karakterX;
    }

    public void setKarakterY(int karakterY) {
        this.karakterY = karakterY;
}
}
/*// Karakterin rastgele hareket etmesini sağlayan metot
    private void moveCharacterRandomly() {
        // Eğer hazineler varsa ve A* algoritması bir yol bulursa, karakter hazineye gider
        boolean foundPath = findAndMoveToTreasures();

        // Eğer hazineye gitmek için A* algoritması kullanılmadıysa veya yol bulunamadıysa, karakter rastgele hareket eder
        if (!foundPath) {
            Random rnd = new Random();
            int move = rnd.nextInt(4); // 0: up, 1: down, 2: left, 3: right
            switch (move) {
                case 0:
                    moveCharacterUp();
                    break;
                case 1:
                    moveCharacterDown();
                    break;
                case 2:
                    moveCharacterLeft();
                    break;
                case 3:
                    moveCharacterRight();
                    break;
            }
        }

        // Hazine toplama işlemi burada gerçekleştiriliyor
        hazineTopla();
    }

    // Karakterin en yakın hazineyi bulup hareket etmesini sağlayan metot
    public boolean findAndMoveToTreasures() {
        Dugum enYakinHazine = enYakinHazineyiBul();
        if (enYakinHazine != null) {
            AStar astar = new AStar(haritayiDugumlereDonustur(engeller));
            ArrayList<Dugum> yol = astar.yolBul(new Dugum(karakterX, karakterY), enYakinHazine);
            if (yol != null && !yol.isEmpty()) {
                for (Dugum dugum : yol) {
                    karakteriHareketEttir(dugum.getX(), dugum.getY());
                    try {
                        Thread.sleep(200); // Karakterin hareketini göstermek için kısa bir bekleme yapılır
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        }
        return false;
    }

    // En yakın hazineyi bulan metot
    private Dugum enYakinHazineyiBul() {
        Dugum enYakinHazine = null;
        double enKisaMesafe = Double.MAX_VALUE;

        for (int x = Math.max(0, karakterX - 3); x <= Math.min(haritaBoyut - 1, karakterX + 3); x++) {
            for (int y = Math.max(0, karakterY - 3); y <= Math.min(haritaBoyut - 1, karakterY + 3); y++) {
                if (engeller[y][x] == 2) { // Eğer bu karede hazine varsa
                    double mesafe = Math.abs(x - karakterX) + Math.abs(y - karakterY);
                    if (mesafe < enKisaMesafe) {
                        enKisaMesafe = mesafe;
                        enYakinHazine = new Dugum(x, y);
                    }
                }
            }
        }
        return enYakinHazine;
    }

    // Karakteri belirli bir konuma hareket ettiren metot
    private void karakteriHareketEttir(int x, int y) {
        karakterX = x;
        karakterY = y;
        try {
            Thread.sleep(150); // Karakterin hareketini göstermek için kısa bir bekleme yapılır
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        updatePanel();
    }

    private void moveCharacterUp() {
        if (isValidMove(karakterY - 1, karakterX)) {
            karakterY--;
            try {
                Thread.sleep(250); // Karakterin hareketini göstermek için kısa bir bekleme yapılır
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updatePanel();
        }
    }

    private void moveCharacterDown() {
        if (isValidMove(karakterY + 1, karakterX)) {
            karakterY++;
            try {
                Thread.sleep(250); // Karakterin hareketini göstermek için kısa bir bekleme yapılır
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updatePanel();
        }
    }

    private void moveCharacterLeft() {
        if (isValidMove(karakterY, karakterX - 1)) {
            karakterX--;
            try {
                Thread.sleep(250); // Karakterin hareketini göstermek için kısa bir bekleme yapılır
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updatePanel();
        }
    }

    private void moveCharacterRight() {
        if (isValidMove(karakterY, karakterX + 1)) {
            karakterX++;
            try {
                Thread.sleep(250); // Karakterin hareketini göstermek için kısa bir bekleme yapılır
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updatePanel();
}
}*/