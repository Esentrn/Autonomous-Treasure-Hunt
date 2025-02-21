package ddd;


import java.util.Comparator;
import java.util.PriorityQueue;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
/**
 *
 * @author HpNtb
 */
public class AStar {
    private PriorityQueue<Dugum> openList;
    private ArrayList<Dugum> closedList;
    private Dugum[][] dugumHaritasi;
    private int maxGorunenMesafe = 3; // Karakterin görebileceği maksimum mesafe

    public AStar(Dugum[][] dugumHaritasi) {
        this.dugumHaritasi = dugumHaritasi;
        this.openList = new PriorityQueue<>(new OncelikliKuyruk.DugumKarsilastir());
        this.closedList = new ArrayList<>();
    }

    public ArrayList<Dugum> yolBul(Dugum baslangicDugum, Dugum hedefDugum) {
    // Açık liste ve kapalı liste oluşturulur
    PriorityQueue<Dugum> openList = new PriorityQueue<>(new DugumKarsilastir());
       
    HashSet<Dugum> closedList = new HashSet<>();

    // Başlangıç düğümünün maliyetleri ayarlanır
    baslangicDugum.setgCost(0);
    baslangicDugum.sethCost(maliyetHesapla(baslangicDugum, hedefDugum));
    openList.add(baslangicDugum);

    while (!openList.isEmpty()) {
        Dugum current = openList.poll();
        closedList.add(current);
        
 
        if (current.equals(hedefDugum)) {
            return yoluOlustur(current);
        }

        for (Dugum komsu : current.komsulariGetir(dugumHaritasi)) {
            if (closedList.contains(komsu)) continue;

            double tempGCost = current.getgCost() + maliyetHesapla(current, komsu);
            if (tempGCost < komsu.getgCost() || !openList.contains(komsu)) {
                komsu.setgCost(tempGCost);
                komsu.sethCost(maliyetHesapla(komsu, hedefDugum));
                komsu.setParentDugum(current);

                if (!openList.contains(komsu)) openList.add(komsu);
            }
        }
    }

    return null; // Hedefe ulaşılamadı
}
 public static double maliyetHesapla(Dugum dugum1, Dugum dugum2) {
        return Math.abs(dugum1.getX() - dugum2.getX()) + Math.abs(dugum1.getY() - dugum2.getY());
    }
private ArrayList<Dugum> yoluOlustur(Dugum hedefDugum) {
    ArrayList<Dugum> path = new ArrayList<>();
    Dugum current = hedefDugum;
    while (current != null) {
        path.add(0, current); // Yolu ters çevir
        current = current.getParentDugum();
    }
    return path;
}

    private ArrayList<Dugum> constructPath(Dugum dugum) {
        ArrayList<Dugum> path = new ArrayList<>();
        Dugum current = dugum;
        while (current != null) {
            path.add(current);
            current = current.getParentDugum();
        }
        return path;
    }
}
 class Dugum {

    private int x;  // Sütun
    private int y;  // Satır
    private double hCost;  // Sezgisel maliyet
    private double gCost;  // Gerçek maliyet
    private double fCost;  // Toplam maliyet
    private Dugum parentDugum;  // Önceki düğüm

    private final double alfa = 1;  // Alfa sabiti
    private final double beta = 1;  // Beta sabiti

    // Düğümün konumunu belirleyen kurucu metod
    public Dugum(int x, int y) {
        this.x = x;
        this.y = y;
        this.fCost = -1;  // Başlangıçta toplam maliyet -1 olarak ayarlanır (henüz hesaplanmamış)
        this.hCost = -1;  // Başlangıçta sezgisel maliyet -1 olarak ayarlanır (henüz hesaplanmamış)
        this.gCost = -1;  // Başlangıçta gerçek maliyet -1 olarak ayarlanır (henüz hesaplanmamış)
    }

    // Düğümün önceki düğümünü döndüren metot
    public Dugum getParentDugum() {
        return parentDugum;
    }

    // Düğümün önceki düğümünü ayarlayan metot
    public void setParentDugum(Dugum parentDugum) {
        this.parentDugum = parentDugum;
    }

    // Düğümün sütununu döndüren metot
    public int getX() {
        return x;
    }

    // Düğümün satırını döndüren metot
    public int getY() {
        return y;
    }

    // Düğümün sezgisel maliyetini döndüren metot
    public double gethCost() {
        return hCost;
    }

    // Düğümün sezgisel maliyetini ayarlayan metot
    public void sethCost(double hCost) {
        this.hCost = hCost;
    }

    // Düğümün gerçek maliyetini döndüren metot
    public double getgCost() {
        return gCost;
    }

    // Düğümün gerçek maliyetini ayarlayan metot
    public void setgCost(double gCost) {
        this.gCost = gCost;
    }

    // Düğümün toplam maliyetini döndüren metot
    public double getfCost() {
        return alfa * gCost + beta * hCost;  // Alfa ve beta sabitleriyle toplam maliyet hesaplanır
    }

    // Düğümün toplam maliyetini ayarlayan metot
    public void setfCost(double fCost) {
        this.fCost = fCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dugum dugum = (Dugum) o;
        return x == dugum.x && y == dugum.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    
    
    public ArrayList<Dugum> komsulariGetir(Dugum[][] dugumHaritasi) {
    ArrayList<Dugum> komsular = new ArrayList<>();
    int[] yonler = {-1, 0, 1, 0, -1}; // Yatay ve dikey hareketler için yön vektörleri

    for (int i = 0; i < 4; i++) {
        int dx = this.getX() + yonler[i];
        int dy = this.getY() + yonler[i + 1];

        // Harita sınırları içinde ve engel olmayan komşuları ekle
        if (dx >= 0 && dx < dugumHaritasi.length && dy >= 0 && dy < dugumHaritasi[0].length && dugumHaritasi[dy][dx].gethCost() != Double.MAX_VALUE) {
            komsular.add(dugumHaritasi[dy][dx]);
        }
    }

    return komsular;
}

// Manhattan mesafesini kullanarak maliyet hesaplayan metot
public double maliyetHesapla(Dugum digerDugum) {
    return Math.abs(this.getX() - digerDugum.getX()) + Math.abs(this.getY() - digerDugum.getY());
}

}

 class OncelikliKuyruk extends PriorityQueue {

    private PriorityQueue<Dugum> kuyruk;  // Öncelikli kuyruk nesnesi

    // OncelikliKuyruk sınıfının kurucusu
    public OncelikliKuyruk() {
        kuyruk = new PriorityQueue<>(new DugumKarsilastir());  // DugumKarsilastir sınıfına göre öncelikli kuyruk oluşturulur
    }

    // Kuyruğa bir düğüm ekleyen metot
    public void dugumEkle(Dugum dugum){
        kuyruk.add(dugum);
    }

    // Kuyruktan en öncelikli düğümü çıkaran metot
    public Dugum dugumGetir(){
        return kuyruk.poll();
    }

    // Kuyruğu döndüren metot
    public PriorityQueue<Dugum> getKuyruk() {
        return kuyruk;
    }

   // Dugum nesnelerini karşılaştırmak için kullanılan iç içe sınıf
   public static class DugumKarsilastir implements Comparator<Dugum> {

    // Dugum nesnelerini karşılaştıran metot
    @Override
    public int compare(Dugum o1, Dugum o2) {
        // Toplam maliyetlere göre karşılaştırma
        if(o1.getfCost() > o2.getfCost()){
            return 1;
        } else if(o1.getfCost() < o2.getfCost()){
            return -1;
        } else {
            // Eğer toplam maliyetler eşitse, sezgisel maliyetlere göre karşılaştırma
            if(o1.gethCost() > o2.gethCost())
                return 1;
            else if(o1.gethCost() < o2.gethCost())
                return -1;
        }
        return 0;  // Toplam maliyet ve sezgisel maliyetler de eşitse, düğümler eşittir
    }
}
}
class DugumKarsilastir implements Comparator<Dugum> {
    @Override
    public int compare(Dugum o1, Dugum o2) {
        if (o1.getfCost() > o2.getfCost()) {
            return 1;
        } else if (o1.getfCost() < o2.getfCost()) {
            return -1;
        } else {
            return 0;
 }
}
}