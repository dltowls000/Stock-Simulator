import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.text.*;

public class Client extends javax.swing.JFrame {
    private Sector sector;
    private List<Sector> sectors;
    private User user;
    private StringBuilder stockContent;
    private LocalDateTime now;
    private DateTimeFormatter formatter;
    private String formattedDateTime;
    private int countNewsMin = 30;
    private int countNewsSec = 0;
    private int stockMin = 0;
    private int stockSec = 10;
    private volatile boolean running = true;
    private UIManager uiManager;
    private Assets assets;

    public Client() {}

    private void connecting(Client client){
        uiManager = new UIManager(Client.this);
        uiManager.getConnect().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 창이 닫힐 때 자원을 해제하도록 설정
        uiManager.getConnect().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // Connect 창이 닫힐 때 호출될 메소드
                File file = new File("database.txt");
                if(!file.exists()){
                    uiManager.viewSetNickname();
                    user = new User("database.txt");
                    assets = new Assets(user);
                    uiManager.setAssets(assets);
                }
                else{
                    user = new User("database.txt");
                    assets = new Assets(user);
                    uiManager.setAssets(assets);
                    sectors = new ArrayList<>();
                    sectors.add(new Sector("기술"));
                    sectors.add(new Sector("예술"));
                    sectors.add(new Sector("게임"));
                    readStocksFromFile("stock.txt", sectors); // 주식 파일 불러오기
                    sector = sectors.get(2);
                    initComponents(client);
                }
            }
        });
    }

    public void readStocksFromFile(String filename, List<Sector> sectors) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0].trim();
                    int price = Integer.parseInt(parts[1].trim());
                    String sectorName = parts[2].trim();

                    Stock stock = new Stock(name, price);
                    addStockToSector(sectorName, stock, sectors);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        }
    }

    private void addStockToSector(String sectorName, Stock stock, List<Sector> sectors) {
        for (Sector sector : sectors) {
            if (sector.getName().equalsIgnoreCase(sectorName)) {
                sector.addStock(stock);
                return;
            }
        }
        // If the sector does not exist, create it and add the stock
        sector = new Sector(sectorName);
        sector.addStock(stock);
        sectors.add(sector);
    }

    @SuppressWarnings("unchecked")
    private void initComponents(Client client)  {
        now = LocalDateTime.now();
        formatter = DateTimeFormatter.ofPattern("hh : mm : ss a", Locale.US);
        formattedDateTime = now.format(formatter);
        jPanel1 = new javax.swing.JPanel();
        reloadStock = new javax.swing.JButton();
        dataDeleteButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        buyStock = new javax.swing.JButton();
        sellStock = new javax.swing.JButton();
        selectStock = new javax.swing.JToggleButton();
        stockComboBox = new JComboBox<String>();
        saveButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        stockPrice = new javax.swing.JLabel();
        shopButton = new javax.swing.JButton();
        partTimeButton = new javax.swing.JButton();
        assetsButton = new javax.swing.JButton();
        rankingButton = new javax.swing.JButton();
        reloadNews = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(1000, 1000));

        reloadStock.setBackground(new java.awt.Color(204, 255, 255));
        reloadStock.setFont(new java.awt.Font("한컴 고딕", 0, 13)); // NOI18N
        reloadStock.setText("주가 갱신");
        reloadStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadStockActionPerformed(evt);
            }
        });

        dataDeleteButton.setBackground(new java.awt.Color(255, 102, 102));
        dataDeleteButton.setFont(new java.awt.Font("한컴 고딕", 0, 13)); // NOI18N
        dataDeleteButton.setText("데이터 삭제");
        dataDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataDeleteButtonActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(216, 228, 230));

        buyStock.setFont(new java.awt.Font("한컴 고딕", 0, 13)); // NOI18N
        buyStock.setText("주식 매수");
        buyStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buyStockActionPerformed(evt);
            }
        });

        sellStock.setFont(new java.awt.Font("한컴 고딕", 0, 13)); // NOI18N
        sellStock.setText("주식 매도");
        sellStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sellStockActionPerformed(evt);
            }
        });

        selectStock.setFont(new java.awt.Font("한컴 고딕", 0, 13)); // NOI18N
        selectStock.setText("주식 분야");
        selectStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectStockActionPerformed(evt);
            }
        });

        saveButton.setBackground(new java.awt.Color(255, 255, 204));
        saveButton.setFont(new java.awt.Font("한컴 고딕", 0, 13)); // NOI18N
        saveButton.setText("게임 저장");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(selectStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(buyStock)
                                        .addComponent(sellStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap(29, Short.MAX_VALUE)
                                .addComponent(buyStock, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(sellStock, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(selectStock, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29))
        );

        jPanel3.setBackground(new java.awt.Color(216, 228, 230));

        shopButton.setFont(new java.awt.Font("한컴 고딕", 0, 13)); // NOI18N
        shopButton.setText("상점");
        shopButton.setPreferredSize(new java.awt.Dimension(82, 23));
        shopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shopButtonActionPerformed(evt);
            }
        });

        partTimeButton.setFont(new java.awt.Font("한컴 고딕", 0, 13)); // NOI18N
        partTimeButton.setText("알바");
        partTimeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                partTimeButtonActionPerformed(evt);
            }
        });

        assetsButton.setFont(new java.awt.Font("한컴 고딕", 0, 13)); // NOI18N
        assetsButton.setText("총 자산");
        assetsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                assetsButtonActionPerformed(evt);
            }
        });

        rankingButton.setFont(new java.awt.Font("한컴 고딕", 0, 13)); // NOI18N
        rankingButton.setText("랭킹");
        rankingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rankingButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(partTimeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(assetsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(rankingButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(shopButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(7, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(shopButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(partTimeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(assetsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(rankingButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(29, Short.MAX_VALUE))
        );

        reloadNews.setBackground(new java.awt.Color(204, 255, 204));
        reloadNews.setFont(new java.awt.Font("한컴 고딕", 0, 13)); // NOI18N
        reloadNews.setText("신문 갱신");
        reloadNews.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadNewsActionPerformed(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 268, Short.MAX_VALUE)
        );

        jPanel5.setBackground(new java.awt.Color(153, 153, 153));
        jLabel1.setFont(new java.awt.Font("한컴 고딕", 1, 14)); // NOI18N
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        jLabel1.setText("주가 차트 | 현재 시각 : " + formattedDateTime +" | 주가 변동까지 남은 시간 : " + String.format("%02d분 %02d초", stockMin, stockSec));


        jPanel7.setBackground(new java.awt.Color(51, 51, 51));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(stockPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 566, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(stockPrice, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                                .addContainerGap())
        );

        changeStockContent();

        stockPrice.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        stockPrice.setFont(new java.awt.Font("한컴 고딕", 1, 14));
        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addGap(15, 15, 15)
                                                .addComponent(jLabel1))
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addGap(37, 37, 37)
                                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(38, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(39, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(250, 250, 250));

        jLabel2.setFont(new java.awt.Font("한컴 고딕", 1, 14)); // NOI18N
        TimerThread timerThread = new TimerThread(sectors); // TimerThread 객체 생성
        timerThread.start(); // 타이머 시작
        jLabel2.setText(String.format("Daily News! | 신문 갱신까지 남은 시간 : %02d분 %02d초", countNewsMin, countNewsSec));

        jPanel8.setBackground(new java.awt.Color(255, 153, 102));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
                jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
                jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 85, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel6Layout.createSequentialGroup()
                                                .addComponent(jLabel2)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(9, Short.MAX_VALUE))
        );

        stockComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { sectors.get(0).getName(), sectors.get(1).getName(), sectors.get(2).getName()}));
        stockComboBox.setSelectedIndex(-1);
        stockComboBox.setVisible(false);
        stockComboBox.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                int selectedIndex = stockComboBox.getSelectedIndex();
                switch (selectedIndex) {
                    case 0:
                        sector = sectors.get(selectedIndex);
                        changeStockContent();
                        break;
                    case 1:
                        sector = sectors.get(selectedIndex);
                        changeStockContent();
                        break;
                    case 2:
                        sector = sectors.get(selectedIndex);
                        changeStockContent();
                        break;
                }
                stockComboBoxActionPerformed(evt);
            }
            });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(reloadStock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(dataDeleteButton, javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(reloadNews, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addComponent(stockComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(17, 17, 17))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(reloadStock, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(dataDeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGap(117, 117, 117)
                                                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(stockComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(reloadNews, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(58, 58, 58))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(22, 22, 22))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 915, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 773, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );

        client.pack();
        client.setVisible(true);
        client.setLocationRelativeTo(null);
    }

    private void reloadStockActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void dataDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void sellStockActionPerformed(java.awt.event.ActionEvent evt) {
        uiManager.viewOrder();
    }

    private void buyStockActionPerformed(java.awt.event.ActionEvent evt) {
        uiManager.viewOrder();
    }

    private void shopButtonActionPerformed(java.awt.event.ActionEvent evt) {
        uiManager.viewShop();
    }

    private void partTimeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        uiManager.viewPartTime();
    }

    private void assetsButtonActionPerformed(java.awt.event.ActionEvent evt) {
        user.setMoney(user.getMoney() - 1);
        System.out.println(user.getMoney());
        assets.setUser(user);
        uiManager.viewAssets();
    }

    private void reloadNewsActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void selectStockActionPerformed(java.awt.event.ActionEvent evt) {
        stockComboBox.setVisible(selectStock.isSelected());
    }

    private void rankingButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void updateTimerLabel() {
        jLabel2.setText(String.format("Daily News! | 신문 갱신까지 남은 시간 : %02d분 %02d초", countNewsMin, countNewsSec));
    }

    private void updateStockLabel(){
        jLabel1.setText("주가 차트 | 현재 시각 : " + formattedDateTime +" | 주가 변동까지 남은 시간 : " + String.format("%02d분 %02d초", stockMin, stockSec));
    }

    private void stopTimerThread() {
        running = false;
    }

    private void stockComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        stockComboBox.setVisible(false);
        stockComboBox.setSelectedIndex(-1);
        selectStock.setSelected(false);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Client client = new Client();
                client.connecting(client);
            }
        });
    }

    public void changeStockContent(){
        stockContent = new StringBuilder("<html>");
        stockContent.append("<span style='color:black;'>");
        stockContent.append("[");
        stockContent.append(sector.getName());
        stockContent.append("]");
        stockContent.append("<br>");
        stockContent.append("<br>");

        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setGroupingUsed(true);

        for (Stock stock : sector.getStocks()) {
            if(stock.getDelisting()){
                stockContent.append("<span style='color:orange;'>");
            }
            else {
                if (stock.getStat()) {
                    stockContent.append("<span style='color:green;'>");
                } else {
                    if (!stock.getNotChange()) {
                        stockContent.append("<span style='color:red;'>");
                    } else {
                        stockContent.append("<span style='color:gray;'>");
                    }
                }
            }
            stockContent.append(stock.getName())
                    .append(" : (")
                    .append(formatter.format(stock.getPrice()))
                    .append(" 원) ");

            if(stock.getDelisting()){
                stockContent.append(">> [상장 폐지로 인한 가격 조정]");
                stock.setDelisting(false);
            }
            else {
                if (stock.getStat()) {
                    stockContent.append(stock.getChangedPrice());
                    stockContent.append("▲");
                } else {
                    if (!stock.getNotChange()) {
                        stockContent.append(stock.getChangedPrice());
                        stockContent.append("▼");
                    } else {
                        stockContent.append(stock.getChangedPrice());
                        stockContent.append("-");
                    }
                }
            }
            stockContent.append("</span><br/><br/>");
        }
        stockContent.append("</html>");

        stockPrice.setText(stockContent.toString());
    }

    public class TimerThread extends Thread {
        private List<Sector> sectors;
        public TimerThread(List<Sector> sectors){
            this.sectors = sectors;
        }
        @Override
        public void run() {
            while (running) {
                now = LocalDateTime.now();
                formatter = DateTimeFormatter.ofPattern("hh : mm : ss a", Locale.US);
                formattedDateTime = now.format(formatter);
                try {
                    Thread.sleep(1000); // 1초 대기

                    if (stockSec == 0){
                        if (stockMin == 0){
                            stockMin = 3;
                            stockSec = 0;
                            for (Sector sc : sectors) {
                                sc.updateStockPrices(sc);
                            }
                            changeStockContent();
                        }
                        else{
                            stockMin--;
                            stockSec = 59;
                        }
                    }else{
                        stockSec--;
                    }

                    if (countNewsSec == 0) {
                        if (countNewsMin == 0) {
                            countNewsMin = 30;
                            countNewsSec = 0;
                        } else {
                            countNewsMin--;
                            countNewsSec = 59;
                        }
                    } else {
                        countNewsSec--;
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            updateStockLabel();
                            updateTimerLabel();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private javax.swing.JButton reloadStock;
    private javax.swing.JButton rankingButton;
    private javax.swing.JButton reloadNews;
    private javax.swing.JButton dataDeleteButton;
    private javax.swing.JButton buyStock;
    private javax.swing.JButton sellStock;
    private javax.swing.JToggleButton selectStock;
    private javax.swing.JComboBox<String> stockComboBox;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton shopButton;
    private javax.swing.JButton partTimeButton;
    private javax.swing.JButton assetsButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel stockPrice;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
}
