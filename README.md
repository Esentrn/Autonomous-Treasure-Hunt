# **Otonom Hazine Avı**

Bu proje, **Java Swing** kullanılarak geliştirilmiş **A* algoritması tabanlı bir yol bulma ve keşif oyunudur**. Oyunda karakter, **A* algoritmasını kullanarak haritadaki hazineleri toplamak için en kısa yolu bulur** ve eğer hazine yoksa **rastgele bir keşif yapar**. Oyunda **sabit ve hareketli engeller** bulunur ve karakterin görüş alanı **sis mekaniği** ile sınırlıdır.  

## **Özellikler**  

- **A* Algoritması** ile en kısa yol hesaplama  
- **Rastgele oluşturulan dinamik harita**  
- **Hazine toplama mekaniği**  
- **Görüş alanı (sis mekaniği)**  
- **Sabit ve hareketli engeller (ağaçlar, dağlar, duvarlar, kayalar, hareketli kuşlar ve arılar)**  
- **Otonom hareket eden karakter**  

## **Kurulum ve Çalıştırma**  

1. **Projeyi klonlayın**  
   ```sh
   git clone https://github.com/Esentrn/Autonomous-Treasure-Hunt.git
   cd otonom-hazine
   ```
2. **Java derleyicisi ve çalışma ortamının (JDK 8+) yüklü olduğundan emin olun.**  

3. **Projeyi derleyip çalıştırın:**  
   - **IDE (NetBeans, IntelliJ, Eclipse)**: `OtonomHazine.java` dosyasını çalıştırın.  
   - **Komut satırı üzerinden çalıştırmak için:**  
     ```sh
     javac -d bin -sourcepath src src/ddd/OtonomHazine.java
     java -cp bin ddd.OtonomHazine
     ```

## **Oyun Mekaniği**  

- **Başlangıç**:  
  - "Oyuna Başla" butonu ile oyuna giriş yapılır.  
  - Harita boyutu kullanıcıdan alınır ve rastgele bir harita oluşturulur.  

- **Karakterin Hareketi**:  
  - Karakter **en yakın hazineyi bulur ve A* algoritması ile en kısa yolu hesaplayarak hareket eder**.  
  - Eğer hazine yoksa **rastgele bir şekilde keşif yapar**.  
  - Karakter **hareket ettikçe sis açılır ve keşfedilen alanlar görünür hale gelir**.  

- **Engeller**:  
  - **Sabit Engeller**: Ağaç, Kaya, Duvar, Dağ  
  - **Hareketli Engeller**: **Kuş** (dikey hareket eder), **Arı** (yatay hareket eder)  

- **Hazine Toplama**:  
  - Karakter bir hazineye ulaştığında, onu alır ve **toplanan hazineler listesine ekler**.  
  - Hazine toplama işlemi oyun ekranında gösterilir.  

## **Ekran Görüntüleri**  

![Oyun Ekranı](https://github.com/Esentrn/Autonomous-Treasure-Hunt/blob/f82d3ee42d9dcbb9635a01d863f738b97109e850/Autonomous-Treasure-Hunt1.png)

![Oyun Ekranı](https://github.com/Esentrn/Autonomous-Treasure-Hunt/blob/f82d3ee42d9dcbb9635a01d863f738b97109e850/Autonomous-Treasure-Hunt2.png)

## **Oyun Videosu**  

![Video Önizleme](https://github.com/Esentrn/Autonomous-Treasure-Hunt/blob/f82d3ee42d9dcbb9635a01d863f738b97109e850/Autonomous-Treasure-Hunt.gif)

