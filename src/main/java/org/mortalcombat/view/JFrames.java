package org.mortalcombat.view;

import org.mortalcombat.controller.Controller;
import org.mortalcombat.model.Enemy;
import org.mortalcombat.model.Player;
import org.mortalcombat.model.Item;
import org.mortalcombat.model.Result;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class JFrames extends JFrame {
    Player player;
    Enemy enemy;
    Controller controller;

    public JFrames(Controller controller) {
        this.controller = controller;
        initComponents();
        writeToTable(controller.importData());
    }

    /**
     * Старт игры
     */
    private void startGame() {
        locationDialog.dispose();

        int locations;
        try {
            locations = Integer.parseInt(locationNumberField.getText());
        } catch (NumberFormatException e) {
            locations = 3;
        }

        controller.initGame(locations);
        GameFrame.setVisible(rootPaneCheckingEnabled);
        GameFrame.setSize(1024, 768);

        newRoundTexts();
    }

    private void chooseLocations() {
        locationDialog.setVisible(true);
    }

    /**
     * Инициализация отображения игрока
     */
    private void initPlayer() {
        player = controller.getPlayer();
        playerName.setText(player.getName() );
        //ImageIcon imageIcon = new ImageIcon( player.getImage() );
        ImageIcon imageIcon = new ImageLoader().loadIcon(player.getImage());
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(250, 300,  java.awt.Image.SCALE_SMOOTH);
        playerImage.setIcon(new ImageIcon(newimg));
        playerLevel.setText(player.getLevel() + " level");
        damage.setText(Integer.toString(player.getDamage()));
        playerHealth.setText(player.getHealth() + "/" + player.getMaxHealth());
        playerProgressBar.setMaximum(player.getMaxHealth());
        playerProgressBar.setValue(player.getHealth());
    }

    /**
     * Инициализация отображения врага
     */
    private void initEnemy() {
        enemy = controller.getEnemy();
        enemyName.setText(enemy.getName());
        //ImageIcon imageIcon = new ImageIcon(enemy.getImage());
        ImageIcon imageIcon = new ImageLoader().loadIcon(enemy.getImage());
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(250, 300,  java.awt.Image.SCALE_SMOOTH);
        enemyImage.setIcon(new ImageIcon(newimg));
        enemyLevel.setText(enemy.getLevel() + " level");
        enemyDamage.setText(Integer.toString(enemy.getDamage()));
        enemyHealth.setText(enemy.getHealth() + "/" + enemy.getMaxHealth());
        enemyProgressBar.setMaximum(enemy.getMaxHealth());
        enemyProgressBar.setValue(enemy.getHealth());
    }

    /**
     * Инициализация отображения предметов
     */
    public void initItems() {
        Item[] items = controller.getItems();
        itemsRadio1.setText(items[0].getName() + ", " + items[0].getAmount() + " шт");
        itemsRadio2.setText(items[1].getName() + ", " + items[1].getAmount() + " шт");
        itemsRadio3.setText(items[2].getName() + ", " + items[2].getAmount() + " шт");
    }

    /**
     * Обновление информации интерфейсы для нового раунда
     */
    private void newRoundTexts() {
        initEnemy();
        initPlayer();
        initItems();

        statusLabel.setText("");
        locations.setBackground(new java.awt.Color(255, 255, 255));
        locations.setForeground(new java.awt.Color(113, 90, 16));
        locations.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        locations.setText(controller.getCurrentLocation() + " of " + controller.getLocations());
        //locations.setText( String.valueOf(controller.getCurrentLocation()) );
        enemies.setBackground(new java.awt.Color(255, 255, 255));
        enemies.setForeground(new java.awt.Color(113, 90, 16));
        enemies.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        enemies.setText(controller.getCurrentEnemy() + " of " + controller.getEnemies());
        //enemies.setText(String.valueOf(controller.getCurrentEnemy()));





        experience.setText(player.getExperience() + "/" + player.getNextExperience());
        points.setText(Integer.toString(player.getPoints()));
        if (controller.getTurn()) {
            turnLabel.setText("Your turn");
        } else {
            turnLabel.setText(enemy.getName() + "'s turn");
        }
        actionLabel.setText("");

    }

    /**
     * Обновление информации интерфейса после каждого хода
     */
    private void updateRoundTexts() {
        playerProgressBar.setValue(player.getHealth());
        enemyProgressBar.setValue(enemy.getHealth());
        enemyHealth.setText(enemy.getHealth() + "/" + enemy.getMaxHealth());
        playerHealth.setText(player.getHealth() + "/" + player.getMaxHealth());
        if (controller.getTurn()) {
            turnLabel.setText("Your turn");
        } else {
            turnLabel.setText(enemy.getName() + "'s turn");
        }
    }

    /**
     * Игрок атакует
     */
    private void actionAttack() {
        controller.attack();
        playerAction();
    }

    /**
     * Игрок защищается
     */
    private void actionDefend() {
        controller.defend();
        playerAction();
    }

    /**
     * Игрок пытается ослабить противника
     */
    private void actionWeaken() {
        controller.weaken();
        playerAction();
    }

    /**
     * Обновление отображения информации после действия игрока
     */
    private void playerAction() {
        HashMap<String, String> labels = controller.makeTurn();
        statusLabel.setText(labels.get("status"));
        actionLabel.setText(labels.get("action"));
        if (labels.get("progressbar") != null) {
            Color color1 = new java.awt.Color(0, 155, 0);
            Color color2 = new java.awt.Color(255, 0, 104);
            if (Objects.equals(labels.get("progressbar"), "true")) {
                if (Objects.equals(playerProgressBar.getForeground(), color1)) playerProgressBar.setForeground(color2);
                else playerProgressBar.setForeground(color1);
            } else {
                if (Objects.equals(enemyProgressBar.getForeground(), color1)) enemyProgressBar.setForeground(color2);
                else enemyProgressBar.setForeground(color1);
            }

        }
        if (controller.resurrect()) resurrect();

        if (controller.checkLevel()) {
            levelDialog.setVisible(true);
            itemsButton.setEnabled(false);
            attackButton.setEnabled(false);
            defendButton.setEnabled(false);
            weakenButton.setEnabled(false);
        } else {
            updateRoundTexts();
        }

        if (controller.isEnd()) endRound();
    }

    /**
     * Завершение раунда
     */
    private void endRound() {
        HashMap<String, String> labels = controller.resultRound();
        if (Objects.equals(labels.get("action"), "endGame")) {
            if (Objects.equals(labels.get("dialog"), "1")) {
                recordDialog.setVisible(true);
                recordDialog.setBounds(150, 150, 600, 500);
                victoryLabel1.setText(labels.get("victoryLabel"));
            } else {
                noRecordDialog.setVisible(true);
                noRecordDialog.setBounds(150, 150, 600, 500);
                victoryLabel1.setText(labels.get("victoryLabel"));
            }
            GameFrame.dispose();
        } else {
            endRoundLabel.setText(labels.get("endRoundLabel"));
            endRoundDialog.setVisible(true);
            endRoundDialog.setBounds(300, 150, 700, 600);
        }

    }

    /**
     * Следующий раунд
     */
    private void nextRound() {
        newRoundTexts();
        endRoundDialog.dispose();
    }

    /**
     * Восстановление игрока
     */
    private void resurrect() {
        Item item = controller.getItem(2);
        playerProgressBar.setValue(player.getHealth());
        playerHealth.setText(player.getHealth() + "/" + player.getMaxHealth());
        itemsRadio3.setText(item.getName() + ", " + item.getAmount() + " шт");
        statusLabel.setText("Крест возраждения вернул вас в игру");
    }

    /**
     * Повышение уровня игрока
     */
    private void levelUP() {
        String choice;
        if (levelRadio1.isSelected()) {
            choice = "HP";
        } else {
            choice = "DMG";
        }

        controller.levelUP(choice);
        levelDialog.dispose();
        itemsButton.setEnabled(true);
        attackButton.setEnabled(true);
        defendButton.setEnabled(true);
        weakenButton.setEnabled(true);

        newRoundTexts();
    }

    /**
     * Обновления таблицы результатов
     */
    public void writeToTable(ArrayList<Result> results) {
        DefaultTableModel model = (DefaultTableModel) resultsTable.getModel();
        for (int i = 0; i < results.size(); i++) {
            if (i < 10) {
                model.setValueAt(results.get(i).getName(), i, 0);
                model.setValueAt(results.get(i).getPoints(), i, 1);
            }
        }
    }

    private void jTextField1ActionPerformed() {
        // TODO add your handling code here:
    }

    /**
     * Добавление в таблицу результатов игрока
     */
    private void jButton6ActionPerformed() {
        ArrayList<Result> results = controller.addGameResults(playerNameField.getText());
        writeToTable(results);
        recordDialog.dispose();
        playerNameField.setText("");
    }

    private void jButton7ActionPerformed() {
        jDialog3.dispose();
    }

    /**
     * Просмотр таблицы результатов
     */
    private void viewResults() {
        jDialog3.setVisible(true);
        jDialog3.setBounds(100, 100, 580, 450);
    }

    private void endGame(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        noRecordDialog.dispose();
    }

    /**
     * Использование предметов
     */
    private void useItem(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useItemButtonActionPerformed
        int choice = 0;
        if (itemsRadio1.isSelected()) {
            choice = 1;
        } else if (itemsRadio2.isSelected()) {
            choice = 2;
        } else if (itemsRadio3.isSelected()) {
            choice = 3;
        }

        boolean isUsed = controller.useItem(choice);
        itemsErrorDialog.setBounds(300, 200, 400, 300);
        itemsErrorDialog.setVisible(true);

        if (!isUsed) itemsDialog.setVisible(true);
        else itemsErrorDialog.dispose();

        playerProgressBar.setValue(player.getHealth());
        playerHealth.setText(player.getHealth() + "/" + player.getMaxHealth());
        initItems();
    }//GEN-LAST:event_useItemButtonActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        itemsDialog.setVisible(true);
        itemsDialog.setBounds(300, 200, 430, 350);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        itemsErrorDialog.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    /**
     * Инициализация графического интерфейса игры
     */
    private void initComponents() {
        GameFrame = new javax.swing.JFrame();
        jPanel2 = new javax.swing.JPanel();
        playerImage = new javax.swing.JLabel();
        attackButton = new javax.swing.JButton();
        defendButton = new javax.swing.JButton();
        playerProgressBar = new javax.swing.JProgressBar();
        enemyImage = new javax.swing.JLabel();
        enemyProgressBar = new javax.swing.JProgressBar();
        enemyName = new javax.swing.JLabel();
        damageLabel1 = new javax.swing.JLabel();
        damageLabel2 = new javax.swing.JLabel();
        damage = new javax.swing.JLabel();
        enemyDamage = new javax.swing.JLabel();
        playerLevel = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        playerHealth = new javax.swing.JLabel();
        enemyHealth = new javax.swing.JLabel();
        experienceLabel = new javax.swing.JLabel();
        pointsLabel = new javax.swing.JLabel();
        experience = new javax.swing.JLabel();
        points = new javax.swing.JLabel();
        enemyLevel = new javax.swing.JLabel();
        turnLabel = new javax.swing.JLabel();
        actionLabel = new javax.swing.JLabel();
        playerName = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        itemsButton = new javax.swing.JButton();
        endRoundDialog = new javax.swing.JDialog();
        jPanel3 = new javax.swing.JPanel();
        endRoundLabel = new javax.swing.JLabel();
        proceedButton = new javax.swing.JButton();
        recordDialog = new javax.swing.JDialog();
        jPanel4 = new javax.swing.JPanel();
        victoryLabel1 = new javax.swing.JLabel();
        playerNameField = new javax.swing.JTextField();
        saveLabel2 = new javax.swing.JLabel();
        saveLabel1 = new javax.swing.JLabel();
        endButton = new javax.swing.JButton();
        jDialog3 = new javax.swing.JDialog();
        jPanel5 = new javax.swing.JPanel();
        recordsLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultsTable = new javax.swing.JTable();
        closeResultsButton = new javax.swing.JButton();
        noRecordDialog = new javax.swing.JDialog();
        jPanel6 = new javax.swing.JPanel();
        victoryLabel2 = new javax.swing.JLabel();
        sorryLabel = new javax.swing.JLabel();
        endGameButton = new javax.swing.JButton();
        itemsDialog = new javax.swing.JDialog();
        itemsPanel = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        itemsRadio1 = new javax.swing.JRadioButton();
        itemsRadio2 = new javax.swing.JRadioButton();
        itemsRadio3 = new javax.swing.JRadioButton();
        useItemButton = new javax.swing.JButton();
        itemsGroup = new javax.swing.ButtonGroup();
        itemsErrorDialog = new javax.swing.JDialog();
        jPanel8 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        header = new javax.swing.JLabel();
        startButton = new javax.swing.JButton();
        resultsButton = new javax.swing.JButton();
        MKImage = new javax.swing.JLabel();

        weakenButton = new javax.swing.JButton();

        locationLabel = new javax.swing.JLabel("Количество локаций :");
        locationNumberField = new javax.swing.JTextField("3");
        locationButton = new javax.swing.JButton();
        locationDialog = new javax.swing.JDialog();



        currentLocation = new javax.swing.JLabel("  Location : ");
        currentLocation.setBackground(new java.awt.Color(255, 255, 255));
        currentLocation.setForeground(new java.awt.Color(113, 90, 16));
        currentLocation.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        currentLocation.setFont(new java.awt.Font("Comic Sans MS", 0, 12));

        locations = new javax.swing.JLabel("1");
        locations.setBackground(new java.awt.Color(255, 255, 255));
        locations.setForeground(new java.awt.Color(113, 90, 16));
        locations.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        locations.setFont(new java.awt.Font("Comic Sans MS", 0, 12));

        currentEnemy = new javax.swing.JLabel("  Enemy :");
        currentEnemy.setBackground(new java.awt.Color(255, 255, 255));
        currentEnemy.setForeground(new java.awt.Color(113, 90, 16));
        currentEnemy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        currentEnemy.setFont(new java.awt.Font("Comic Sans MS", 0, 12));

        enemies = new javax.swing.JLabel("1");
        enemies.setBackground(new java.awt.Color(255, 255, 255));
        enemies.setForeground(new java.awt.Color(113, 90, 16));
        enemies.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        enemies.setFont(new java.awt.Font("Comic Sans MS", 0, 12));

        levelGroup = new javax.swing.ButtonGroup();
        levelLabel = new javax.swing.JLabel("Вы повысили уровень! \nВы можете выбрать, \nкакую характеристику улучшить");
        levelRadio1 = new javax.swing.JRadioButton("Здоровье");
        levelRadio2 = new javax.swing.JRadioButton("Урон");
        levelButton = new javax.swing.JButton("Далее");
        levelDialog = new javax.swing.JDialog();

        levelRadio1.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        levelRadio1.setForeground(new java.awt.Color(0, 0, 0));

        levelRadio2.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        levelRadio2.setForeground(new java.awt.Color(0, 0, 0));

        itemsGroup.add(itemsRadio1);
        itemsGroup.add(itemsRadio2);
        itemsGroup.add(itemsRadio3);

        levelGroup.add(levelRadio1);
        levelGroup.add(levelRadio2);
        levelRadio1.setSelected(true);

        levelButton.setBackground(new java.awt.Color(239, 237, 14));
        levelButton.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        levelButton.setForeground(new java.awt.Color(0, 0, 0));


        levelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                levelUP();
            }
        });

        levelDialog.setBounds(150, 150, 500, 200);
        javax.swing.GroupLayout levelDialogLayout = new javax.swing.GroupLayout(levelDialog.getContentPane());
        levelDialog.getContentPane().setLayout(levelDialogLayout);
        levelDialogLayout.setHorizontalGroup(
                levelDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(levelDialogLayout.createParallelGroup()
                                .addComponent(levelLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(levelRadio1)
                                .addComponent(levelRadio2)
                                .addComponent(levelButton))

        );
        levelDialogLayout.setVerticalGroup(
                levelDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(levelDialogLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(levelLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(levelRadio1)
                                .addComponent(levelRadio2)
                                .addComponent(levelButton)
                                .addContainerGap())
        );


        locationButton.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        locationButton.setText("Продолжить");
        locationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startGame();
            }
        });

        locationDialog.setBounds(150, 150, 200, 150);
        javax.swing.GroupLayout locationDialogLayout = new javax.swing.GroupLayout(locationDialog.getContentPane());
        locationDialog.getContentPane().setLayout(locationDialogLayout);
        locationDialogLayout.setHorizontalGroup(
                locationDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)

                        .addGroup(locationDialogLayout.createParallelGroup()
                                .addComponent(locationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(locationNumberField)
                                .addComponent(locationButton))

        );
        locationDialogLayout.setVerticalGroup(
                locationDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(locationDialogLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(locationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(locationNumberField)
                                .addComponent(locationButton)
                                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        playerImage.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource("images/Kitana.jpg"))); // NOI18N

        weakenButton.setBackground(new java.awt.Color(255, 0, 204));
        weakenButton.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        weakenButton.setForeground(new java.awt.Color(0, 0, 0));
        weakenButton.setText("Weaken");
        weakenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionWeaken();
            }
        });


        attackButton.setBackground(new java.awt.Color(255, 0, 0));
        attackButton.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        attackButton.setForeground(new java.awt.Color(0, 0, 0));
        attackButton.setText("Attack");
        attackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionAttack();
            }
        });

        defendButton.setBackground(new java.awt.Color(255, 204, 0));
        defendButton.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        defendButton.setForeground(new java.awt.Color(0, 0, 0));
        defendButton.setText("Defend");
        defendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionDefend();
            }
        });

        playerProgressBar.setBackground(new java.awt.Color(204, 204, 204));
        playerProgressBar.setForeground(new java.awt.Color(0, 255, 0));
        playerProgressBar.setMaximum(80);
        playerProgressBar.setMinimum(-1);

        enemyProgressBar.setBackground(new java.awt.Color(204, 204, 204));
        enemyProgressBar.setForeground(new java.awt.Color(0, 255, 0));
        enemyProgressBar.setMinimum(-1);

        enemyName.setBackground(new java.awt.Color(0, 0, 0));
        enemyName.setFont(new java.awt.Font("Comic Sans MS", 2, 14)); // NOI18N

        damageLabel1.setBackground(new java.awt.Color(255, 255, 255));
        damageLabel1.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        damageLabel1.setForeground(new java.awt.Color(0, 0, 0));
        damageLabel1.setText("Damage");

        damageLabel2.setBackground(new java.awt.Color(255, 255, 255));
        damageLabel2.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        damageLabel2.setForeground(new java.awt.Color(0, 0, 0));
        damageLabel2.setText("Damage");

        damage.setBackground(new java.awt.Color(255, 255, 255));
        damage.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        damage.setForeground(new java.awt.Color(255, 0, 0));
        damage.setText("16");

        enemyDamage.setBackground(new java.awt.Color(255, 255, 255));
        enemyDamage.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        enemyDamage.setForeground(new java.awt.Color(255, 0, 0));
        enemyDamage.setText("16");

        playerLevel.setBackground(new java.awt.Color(255, 255, 255));
        playerLevel.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        playerLevel.setForeground(new java.awt.Color(0, 0, 0));
        playerLevel.setText("1 уровень");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Comic Sans MS", 3, 36)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 153));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("FIGHT");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        playerHealth.setBackground(new java.awt.Color(255, 255, 255));
        playerHealth.setFont(new java.awt.Font("Comic Sans MS", 0, 14)); // NOI18N
        playerHealth.setForeground(new java.awt.Color(102, 102, 102));
        playerHealth.setText("80/80");

        enemyHealth.setBackground(new java.awt.Color(255, 255, 255));
        enemyHealth.setFont(new java.awt.Font("Comic Sans MS", 0, 14)); // NOI18N
        enemyHealth.setForeground(new java.awt.Color(102, 102, 102));
        enemyHealth.setText("80/80");

        experienceLabel.setBackground(new java.awt.Color(255, 255, 255));
        experienceLabel.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        experienceLabel.setForeground(new java.awt.Color(128, 92, 31));
        experienceLabel.setText("experience");

        pointsLabel.setBackground(new java.awt.Color(255, 255, 255));
        pointsLabel.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        pointsLabel.setForeground(new java.awt.Color(128, 92, 31));
        pointsLabel.setText("points");

        experience.setBackground(new java.awt.Color(255, 255, 255));
        experience.setFont(new java.awt.Font("Comic Sans MS", 0, 14)); // NOI18N
        experience.setForeground(new java.awt.Color(113, 90, 16));
        experience.setText("0/40");

        points.setBackground(new java.awt.Color(255, 255, 255));
        points.setFont(new java.awt.Font("Comic Sans MS", 0, 14)); // NOI18N
        points.setForeground(new java.awt.Color(113, 90, 16));
        points.setText("00");

        enemyLevel.setBackground(new java.awt.Color(255, 255, 255));
        enemyLevel.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        enemyLevel.setForeground(new java.awt.Color(0, 0, 0));
        enemyLevel.setText("1 уровень");

        turnLabel.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N
        turnLabel.setForeground(new java.awt.Color(0, 0, 0));
        turnLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        actionLabel.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        actionLabel.setForeground(new java.awt.Color(204, 0, 0));
        actionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        playerName.setFont(new java.awt.Font("Comic Sans MS", 2, 14)); // NOI18N
        playerName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        playerName.setText("Kitana");

        statusLabel.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N
        statusLabel.setForeground(new java.awt.Color(0, 0, 0));
        statusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        itemsButton.setBackground(new java.awt.Color(174, 183, 106));
        itemsButton.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        itemsButton.setForeground(new java.awt.Color(0, 0, 0));
        itemsButton.setText("Предметы");
        itemsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(currentLocation)
                                .addComponent(locations)
                                .addComponent(currentEnemy)
                                .addComponent(enemies))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addComponent(enemyHealth)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                                .addGap(262, 262, 262)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(experienceLabel))
                                                .addContainerGap())
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                .addComponent(enemyProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                                .addGap(92, 92, 92)
                                                                                                .addComponent(pointsLabel))
                                                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                                .addGap(111, 111, 111)
                                                                                                .addComponent(points)
                                                                                                .addGap(108, 108, 108)
                                                                                                .addComponent(experience))))
                                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                .addComponent(damageLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(enemyDamage, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                .addGap(10, 10, 10)
                                                                                .addComponent(enemyName, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addComponent(enemyImage, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                .addGap(69, 69, 69)
                                                                                .addComponent(turnLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addGap(0, 0, Short.MAX_VALUE))
                                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                .addGap(26, 26, 26)
                                                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                        .addComponent(actionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE))))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                                                .addGap(0, 0, Short.MAX_VALUE)
                                                                .addComponent(itemsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(46, 46, 46)))
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addComponent(playerLevel)
                                                                .addGap(26, 26, 26))
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                .addComponent(playerProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addGap(18, 18, 18)
                                                                                .addComponent(playerHealth))
                                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                .addComponent(damageLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(damage, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                .addGap(45, 45, 45))
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addComponent(playerImage, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(60, 60, 60))
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addComponent(playerName, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(144, 144, 144))
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addComponent(attackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(46, 46, 46)
                                                                .addComponent(defendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(46, 46, 46)
                                                                .addComponent(weakenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(68, 68, 68))))))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(16, 16, 16)
                                        .addComponent(enemyLevel)
                                        .addContainerGap(854, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(currentLocation)
                        .addComponent(locations)
                        .addComponent(currentEnemy)
                        .addComponent(enemies)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addContainerGap(21, Short.MAX_VALUE)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(63, 63, 63)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(playerProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                                                        .addComponent(enemyProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addGap(8, 8, 8)
                                                                .addComponent(playerHealth))
                                                        .addComponent(enemyHealth, javax.swing.GroupLayout.Alignment.TRAILING)))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(40, 40, 40)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(experienceLabel)
                                                        .addComponent(pointsLabel))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(experience)
                                                        .addComponent(points))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(damageLabel2)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(damageLabel1)
                                                .addComponent(enemyDamage)
                                                .addComponent(damage)))
                                .addGap(27, 27, 27)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(enemyImage, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(enemyName, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addGap(16, 16, 16)
                                                                .addComponent(playerLevel)
                                                                .addGap(7, 7, 7)
                                                                .addComponent(playerImage, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addGap(33, 33, 33)
                                                                .addComponent(actionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(turnLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(18, 18, 18)
                                                .addComponent(playerName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(itemsButton, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                .addComponent(attackButton, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                                                                .addComponent(defendButton, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                                                                .addComponent(weakenButton, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)))
                                                .addGap(14, 14, 14))))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(229, 229, 229)
                                        .addComponent(enemyLevel)
                                        .addContainerGap(380, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout MainFrameLayout = new javax.swing.GroupLayout(GameFrame.getContentPane());
        GameFrame.getContentPane().setLayout(MainFrameLayout);
        MainFrameLayout.setHorizontalGroup(
                MainFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(MainFrameLayout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        MainFrameLayout.setVerticalGroup(
                MainFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel3.setBackground(new java.awt.Color(179, 226, 217));

        endRoundLabel.setBackground(new java.awt.Color(204, 204, 204));
        endRoundLabel.setFont(new java.awt.Font("Comic Sans MS", 1, 42)); // NOI18N
        endRoundLabel.setForeground(new java.awt.Color(204, 0, 0));
        endRoundLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        endRoundLabel.setText("Конец раунда");
        endRoundLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        proceedButton.setBackground(new java.awt.Color(114, 218, 142));
        proceedButton.setFont(new java.awt.Font("Comic Sans MS", 0, 24)); // NOI18N
        proceedButton.setForeground(new java.awt.Color(0, 0, 0));
        proceedButton.setText("Дальше");
        proceedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextRound();
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGap(208, 208, 208)
                                                .addComponent(proceedButton, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGap(104, 104, 104)
                                                .addComponent(endRoundLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 467, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(106, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(89, 89, 89)
                                .addComponent(endRoundLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 108, Short.MAX_VALUE)
                                .addComponent(proceedButton, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(72, 72, 72))
        );

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(endRoundDialog.getContentPane());
        endRoundDialog.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
                jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jDialog1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        jDialog1Layout.setVerticalGroup(
                jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jDialog1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(255, 204, 255));

        victoryLabel1.setFont(new java.awt.Font("Comic Sans MS", 3, 24)); // NOI18N
        victoryLabel1.setForeground(new java.awt.Color(255, 0, 0));
        victoryLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        victoryLabel1.setText("Победа на вашей стороне");

        playerNameField.setBackground(new java.awt.Color(255, 255, 255));
        playerNameField.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        playerNameField.setForeground(new java.awt.Color(0, 0, 0));
        playerNameField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        playerNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed();
            }
        });

        saveLabel2.setFont(new java.awt.Font("Comic Sans MS", 0, 14)); // NOI18N
        saveLabel2.setForeground(new java.awt.Color(102, 102, 102));
        saveLabel2.setText("Введите имя своего персонажа для добавления");

        saveLabel1.setFont(new java.awt.Font("Comic Sans MS", 0, 14)); // NOI18N
        saveLabel1.setForeground(new java.awt.Color(102, 102, 102));
        saveLabel1.setText("результата в таблицу рекордов");

        endButton.setBackground(new java.awt.Color(153, 153, 255));
        endButton.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        endButton.setForeground(new java.awt.Color(51, 51, 51));
        endButton.setText("Закончить игру");
        endButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed();
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(endButton)
                                .addGap(14, 14, 14))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGap(132, 132, 132)
                                                .addComponent(saveLabel1))
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGap(60, 60, 60)
                                                .addComponent(victoryLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGap(77, 77, 77)
                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(playerNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(saveLabel2))))
                                .addContainerGap(62, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addComponent(victoryLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(saveLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(saveLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                                .addComponent(playerNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(endButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        javax.swing.GroupLayout jDialog2Layout = new javax.swing.GroupLayout(recordDialog.getContentPane());
        recordDialog.getContentPane().setLayout(jDialog2Layout);
        jDialog2Layout.setHorizontalGroup(
                jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jDialog2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        jDialog2Layout.setVerticalGroup(
                jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jDialog2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(204, 204, 255));

        recordsLabel.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        recordsLabel.setForeground(new java.awt.Color(51, 51, 51));
        recordsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        recordsLabel.setText("Таблица рекордов");

        resultsTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {null, null},
                        {null, null},
                        {null, null},
                        {null, null},
                        {null, null},
                        {null, null},
                        {null, null},
                        {null, null},
                        {null, null},
                        {null, null}
                },
                new String[]{
                        "Имя", "Количество баллов"
                }
        ) {
            Class[] types = new Class[]{
                    java.lang.String.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        });
        jScrollPane1.setViewportView(resultsTable);

        closeResultsButton.setBackground(new java.awt.Color(255, 255, 153));
        closeResultsButton.setFont(new java.awt.Font("Comic Sans MS", 0, 18)); // NOI18N
        closeResultsButton.setForeground(new java.awt.Color(51, 51, 51));
        closeResultsButton.setText("Закрыть");
        closeResultsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed();
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(closeResultsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel5Layout.createSequentialGroup()
                                                        .addGap(160, 160, 160)
                                                        .addComponent(recordsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel5Layout.createSequentialGroup()
                                                        .addGap(18, 18, 18)
                                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 527, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(recordsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(closeResultsButton, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                                .addContainerGap())
        );

        javax.swing.GroupLayout jDialog3Layout = new javax.swing.GroupLayout(jDialog3.getContentPane());
        jDialog3.getContentPane().setLayout(jDialog3Layout);
        jDialog3Layout.setHorizontalGroup(
                jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDialog3Layout.setVerticalGroup(
                jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel6.setBackground(new java.awt.Color(255, 204, 255));

        victoryLabel2.setFont(new java.awt.Font("Comic Sans MS", 3, 24)); // NOI18N
        victoryLabel2.setForeground(new java.awt.Color(255, 0, 0));
        victoryLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        victoryLabel2.setText("Победа не на вашей стороне");

        sorryLabel.setFont(new java.awt.Font("Comic Sans MS", 0, 14)); // NOI18N
        sorryLabel.setForeground(new java.awt.Color(102, 102, 102));
        sorryLabel.setText("К сожалению, Ваш результат не попал в топ 10");

        endGameButton.setBackground(new java.awt.Color(153, 153, 255));
        endGameButton.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        endGameButton.setForeground(new java.awt.Color(51, 51, 51));
        endGameButton.setText("Закончить игру");
        endGameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endGame(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel6Layout.createSequentialGroup()
                                                .addGap(48, 48, 48)
                                                .addComponent(victoryLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel6Layout.createSequentialGroup()
                                                .addGap(58, 58, 58)
                                                .addComponent(sorryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel6Layout.createSequentialGroup()
                                                .addGap(153, 153, 153)
                                                .addComponent(endGameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(68, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(60, 60, 60)
                                .addComponent(victoryLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(41, 41, 41)
                                .addComponent(sorryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(56, 56, 56)
                                .addComponent(endGameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(56, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jDialog4Layout = new javax.swing.GroupLayout(noRecordDialog.getContentPane());
        noRecordDialog.getContentPane().setLayout(jDialog4Layout);
        jDialog4Layout.setHorizontalGroup(
                jDialog4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDialog4Layout.setVerticalGroup(
                jDialog4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        itemsPanel.setBackground(new java.awt.Color(190, 182, 135));

        jLabel30.setFont(new java.awt.Font("Comic Sans MS", 0, 16)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(51, 51, 51));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("Мешок предметов");

        itemsRadio1.setBackground(new java.awt.Color(190, 182, 135));
        itemsRadio1.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        itemsRadio1.setForeground(new java.awt.Color(0, 0, 0));
        itemsRadio1.setText("Малое зелье лечение, 0 шт");

        itemsRadio2.setBackground(new java.awt.Color(190, 182, 135));
        itemsRadio2.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        itemsRadio2.setForeground(new java.awt.Color(0, 0, 0));
        itemsRadio2.setText("Большое зелье лечение, 0 шт");

        itemsRadio3.setBackground(new java.awt.Color(190, 182, 135));
        itemsRadio3.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        itemsRadio3.setForeground(new java.awt.Color(0, 0, 0));
        itemsRadio3.setText("Крест возрождения, 0 шт");

        useItemButton.setBackground(new java.awt.Color(239, 237, 14));
        useItemButton.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        useItemButton.setForeground(new java.awt.Color(0, 0, 0));
        useItemButton.setText("Использовать");
        useItemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useItem(evt);
            }
        });

        javax.swing.GroupLayout itemsPanelLayout = new javax.swing.GroupLayout(itemsPanel);
        itemsPanel.setLayout(itemsPanelLayout);
        itemsPanelLayout.setHorizontalGroup(
                itemsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(itemsPanelLayout.createSequentialGroup()
                                .addGroup(itemsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(itemsPanelLayout.createSequentialGroup()
                                                .addGap(120, 120, 120)
                                                .addGroup(itemsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(itemsRadio1, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(itemsRadio2, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(itemsRadio3, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel30, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(itemsPanelLayout.createSequentialGroup()
                                                .addGap(139, 139, 139)
                                                .addComponent(useItemButton)))
                                .addContainerGap(105, Short.MAX_VALUE))
        );
        itemsPanelLayout.setVerticalGroup(
                itemsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(itemsPanelLayout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(itemsRadio1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(itemsRadio2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(itemsRadio3)
                                .addGap(50, 50, 50)
                                .addComponent(useItemButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(54, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout itemsDialogLayout = new javax.swing.GroupLayout(itemsDialog.getContentPane());
        itemsDialog.getContentPane().setLayout(itemsDialogLayout);
        itemsDialogLayout.setHorizontalGroup(
                itemsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(itemsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        itemsDialogLayout.setVerticalGroup(
                itemsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(itemsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel8.setBackground(new java.awt.Color(243, 120, 120));

        jLabel31.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(51, 51, 51));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("Вы не можете использовать ");

        jLabel32.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(51, 51, 51));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("этот предмет");

        okButton.setBackground(new java.awt.Color(204, 204, 204));
        okButton.setFont(new java.awt.Font("Comic Sans MS", 0, 18)); // NOI18N
        okButton.setForeground(new java.awt.Color(51, 51, 51));
        okButton.setText("ОК");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
                jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel8Layout.createSequentialGroup()
                                .addContainerGap(82, Short.MAX_VALUE)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                                                .addComponent(jLabel31)
                                                .addGap(58, 58, 58))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                                                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(135, 135, 135))))
                        .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(150, 150, 150)
                                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
                jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(59, 59, 59)
                                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(52, 52, 52))
        );

        javax.swing.GroupLayout itemsErrorDialogLayout = new javax.swing.GroupLayout(itemsErrorDialog.getContentPane());
        itemsErrorDialog.getContentPane().setLayout(itemsErrorDialogLayout);
        itemsErrorDialogLayout.setHorizontalGroup(
                itemsErrorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        itemsErrorDialogLayout.setVerticalGroup(
                itemsErrorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        header.setFont(new java.awt.Font("Comic Sans MS", 2, 24)); // NOI18N
        header.setForeground(new java.awt.Color(204, 204, 0));
        header.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        header.setText("Mortal Kombat");

        startButton.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        startButton.setText("Начать новую игру");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseLocations();
            }
        });


        resultsButton.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        resultsButton.setText("Посмотреть таблицу \nрезультатов");
        resultsButton.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        resultsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewResults();
            }
        });

        //MKImage.setIcon(new javax.swing.ImageIcon("src/main/resources/images/MK.jpeg")); // NOI18N
        MKImage.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource("images/MK.jpg")));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(resultsButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(21, 21, 21))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(159, 159, 159)
                                                .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(143, 143, 143)
                                                .addComponent(MKImage)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(54, 54, 54)
                                .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(MKImage)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(resultsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup itemsGroup;
    private javax.swing.JButton startButton;
    private javax.swing.JButton itemsButton;
    private javax.swing.JButton okButton;
    private javax.swing.JButton resultsButton;
    private javax.swing.JButton attackButton;
    private javax.swing.JButton defendButton;
    private javax.swing.JButton proceedButton;
    private javax.swing.JButton endButton;
    private javax.swing.JButton closeResultsButton;
    private javax.swing.JButton endGameButton;
    private javax.swing.JButton useItemButton;
    private javax.swing.JDialog endRoundDialog;
    private javax.swing.JDialog recordDialog;
    private javax.swing.JDialog jDialog3;
    private javax.swing.JDialog noRecordDialog;
    private javax.swing.JDialog itemsDialog;
    private javax.swing.JDialog itemsErrorDialog;
    private javax.swing.JFrame GameFrame;
    private javax.swing.JLabel header;
    private javax.swing.JLabel enemyDamage;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel playerHealth;
    private javax.swing.JLabel enemyHealth;
    private javax.swing.JLabel experienceLabel;
    private javax.swing.JLabel pointsLabel;
    private javax.swing.JLabel experience;
    private javax.swing.JLabel points;
    private javax.swing.JLabel endRoundLabel;
    private javax.swing.JLabel enemyLevel;
    private javax.swing.JLabel playerImage;
    private javax.swing.JLabel victoryLabel1;
    private javax.swing.JLabel saveLabel2;
    private javax.swing.JLabel saveLabel1;
    private javax.swing.JLabel recordsLabel;
    private javax.swing.JLabel victoryLabel2;
    private javax.swing.JLabel sorryLabel;
    private javax.swing.JLabel turnLabel;
    private javax.swing.JLabel actionLabel;
    private javax.swing.JLabel playerName;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel MKImage;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel enemyImage;
    private javax.swing.JLabel enemyName;
    private javax.swing.JLabel playerLevel;
    private javax.swing.JLabel damageLabel1;
    private javax.swing.JLabel damageLabel2;
    private javax.swing.JLabel damage;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel itemsPanel;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JProgressBar playerProgressBar;
    private javax.swing.JProgressBar enemyProgressBar;
    private javax.swing.JRadioButton itemsRadio1;
    private javax.swing.JRadioButton itemsRadio2;
    private javax.swing.JRadioButton itemsRadio3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable resultsTable;
    private javax.swing.JTextField playerNameField;
    // End of variables declaration//GEN-END:variables

    private javax.swing.JLabel locationLabel;
    private javax.swing.JTextField locationNumberField;
    private javax.swing.JButton locationButton;
    private javax.swing.JDialog locationDialog;

    private javax.swing.JLabel currentEnemy;
    private javax.swing.JLabel enemies;
    private javax.swing.JLabel currentLocation;
    private javax.swing.JLabel locations;

    private javax.swing.ButtonGroup levelGroup;
    private javax.swing.JLabel levelLabel;
    private javax.swing.JRadioButton levelRadio1;
    private javax.swing.JRadioButton levelRadio2;
    private javax.swing.JButton levelButton;
    private javax.swing.JDialog levelDialog;

    private javax.swing.JButton weakenButton;
}
