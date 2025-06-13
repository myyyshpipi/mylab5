package org.mortalcombat.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.mortalcombat.model.Enemy;
import org.mortalcombat.model.Player;
import org.mortalcombat.model.ShaoKahn;
import org.mortalcombat.model.Fighter;
import org.mortalcombat.fabric.EnemyFabric;
import org.mortalcombat.model.Item;
import org.mortalcombat.model.Result;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static org.mortalcombat.model.Action.*;

/**
 * Обработчик всех действий игрока и врага и хода игры
 */
public class Controller {
    private Player player;
    private Enemy enemy;
    private final Item[] items = new Item[3];
    private final ArrayList<Result> results = new ArrayList<>();

    private boolean playerTurn = true;
    private final int[] nextLevelExperience = {40, 90, 180, 260, 410, 1000};
    private final Enemy[] enemies = new Enemy[6];
    private int locations;
    private int currentLocation;
    private int currentEnemy;
    private int locationEnemies;
    private final int maxEnemies = 5;
    private final int minEnemies = 2;

    /**
     * Инициализация настроек игры
     */
    public void initGame(int locations) {
        newPlayer();
        Random random = new Random();
        this.locations = locations;
        currentLocation = 1;
        currentEnemy = 1;
        locationEnemies = random.nextInt(maxEnemies - minEnemies + player.getLevel()) + minEnemies;

        EnemyFabric fabric = new EnemyFabric();
        for (int i = 0; i < 6; i++) {
            enemies[i] = fabric.create(i);
        }
        newEnemy();
        newItems();
    }
    public void newPlayer() {
        player = new Player(1, 80, 16);
    }
    public void newEnemy() {
        if (currentEnemy == locationEnemies) {
            Random random = new Random();
            enemy = enemies[random.nextInt(2) + 4];
        } else enemy = enemies[(int) (Math.random() * 4)];
    }

    public void newItems() {
        Random random = new Random();
        items[0] = new Item("Малое зелье", random.nextInt(7));
        items[1] = new Item("Большое зелье", random.nextInt(5));
        items[2] = new Item("Крест возрождения", random.nextInt(2));
    }

    public void defend() {
        player.setAction(DEFEND);
    }

    public void attack() {
        player.setAction(ATTACK);
    }

    public void weaken() {
        player.setAction(WEAKEN);
    }

    /**
     * Создание хода
     */
    public HashMap<String, String> makeTurn() {
        enemy.action(player.getActions());

        if (playerTurn) {
            playerTurn = false;
            return resultTurn(player, enemy);
        } else {
            playerTurn = true;
            return resultTurn(enemy, player);
        }

    }

    /**
     * Результат хода
     */
    public HashMap<String, String> resultTurn(Fighter actingFighter, Fighter respondFighter) {
        HashMap<String, String> labels = new HashMap<>();
        labels.put("status", "");

        if (actingFighter.isVulnerable() && respondFighter.getAction() == ATTACK) {
            actingFighter.resetVulnerable();
            respondFighter.buff();
            labels.put("status", actingFighter.getName() + " removed weakening and " + respondFighter.getName() + " got buff");
            labels.put("progressbar", String.valueOf(actingFighter instanceof Player));
        }
        if (respondFighter.isVulnerable() && actingFighter.getAction() == ATTACK) {
            respondFighter.resetVulnerable();
            actingFighter.buff();
            labels.put("status", respondFighter.getName() + " removed weakening and " + actingFighter.getName() + " got buff");
            labels.put("progressbar", String.valueOf(respondFighter instanceof Player));
        }

        switch (actingFighter.getAction()) {
            case REGENERATE:
                switch (respondFighter.getAction()) {
                    case DEFEND: {
                        actingFighter.heal((int) ((actingFighter.getMaxHealth() - actingFighter.getHealth()) * 0.5));
                        labels.put("action", actingFighter.getName() + " regenerated");
                        break;
                    }
                    case DEBUFF: {
                        actingFighter.heal((int) ((actingFighter.getMaxHealth() - actingFighter.getHealth()) * 0.6));
                        labels.put("action", actingFighter.getName() + " regenerated");
                        break;
                    }
                    case ATTACK: {
                        actingFighter.takeDamage(respondFighter.getDamage() * 2);
                        labels.put("action", respondFighter.getName() + " stopped regeneration");
                        break;
                    }
                }
                break;
            case ATTACK:
                switch (respondFighter.getAction()) {
                    case DEFEND: {
                        if (actingFighter instanceof ShaoKahn & Math.random() < 0.15) {
                            respondFighter.takeDamage((int) (actingFighter.getDamage() * 0.5));
                            labels.put("action", "Your block is broken");
                        } else {
                            actingFighter.takeDamage((int) (respondFighter.getDamage() * 0.5));
                            labels.put("action", respondFighter.getName() + " counterattacked");
                        }
                        break;
                    }
                    case ATTACK, DEBUFF: {
                        respondFighter.takeDamage(actingFighter.getDamage());
                        labels.put("action", actingFighter.getName() + " attacked");
                        break;
                    }
                }
                break;
            case DEFEND:
                switch (respondFighter.getAction()) {
                    case DEFEND: {
                        if (Math.random() <= 0.5) respondFighter.setAction(DEBUFF);
                        labels.put("action", "Both defended themselves");
                        break;
                    }
                    case ATTACK: {
                        labels.put("action", actingFighter.getName() + " didn't attack");
                        break;
                    }
                    case DEBUFF: {
                        labels.put("action", "Nobody attacked");
                        break;
                    }
                }
                break;
            case DEBUFF:
                labels.put("status", actingFighter.getName() + " was debuffed");
                actingFighter.setAction(ATTACK);
                switch (respondFighter.getAction()) {
                    case DEFEND: {
                        labels.put("action", respondFighter.getName() + " didn't attacked");
                        break;
                    }
                    case ATTACK: {
                        actingFighter.takeDamage(respondFighter.getDamage());
                        labels.put("action", respondFighter.getName() + " attacked");
                        break;
                    }
                    case WEAKEN: {
                        labels.put("action", actingFighter.getName() + " was weakened");
                        break;
                    }
                }
                break;
            case WEAKEN:
                switch (respondFighter.getAction()) {
                    case DEFEND: {
                        if (Math.random() <= 0.75) {
                            respondFighter.setVulnerable();
                            labels.put("action", respondFighter.getName() + " was weakened");
                            labels.put("progressbar", String.valueOf(respondFighter instanceof Player));
                        } else {
                            labels.put("action", respondFighter.getName() + " wasn't weakened");
                        }
                        break;
                    }
                    case ATTACK: {
                        actingFighter.takeDamage(respondFighter.getDamage());
                        labels.put("action", respondFighter.getName() + " wasn't weakened and attacked");
                        break;
                    }
                    case WEAKEN: {
                        labels.put("action", "Nobody was weakened");
                        break;
                    }
                }
                break;
        }
        return labels;
    }

    /**
     * Результат раунда в локации
     */
    public HashMap<String, String> resultRound() {
        if (currentEnemy == locationEnemies) {
            // обновление локации
            Random random = new Random();
            currentEnemy = 1;
            currentLocation++;
            locationEnemies = minEnemies + random.nextInt(maxEnemies - minEnemies);
        } else {
            //окончание раунда
            currentEnemy++;
        }
        return endRound();
    }

    /**
     * Останавливаем игру если у одного из соперников кончилось здоровье
     *
     */
    public boolean isEnd() {
        return player.getHealth() == 0 || enemy.getHealth() == 0;
    }

    /**
     * Завершение раунда
     */
    public HashMap<String, String> endRound() {
        HashMap<String, String> labels = new HashMap<>();
        labels.put("action", "endRound");
        if (player.getHealth() > 0) {
            labels.put("endRoundLabel", "You win");

            if (enemy instanceof ShaoKahn) {
                addItems(38, 23, 8);
                player.addPointsBoss();
            } else {
                addItems(25, 15, 5);
                player.addPoints();
            }
        } else {
            labels.put("endRoundLabel", enemy.getName() + " win");
        }
        enemy.reset();
        player.reset();

        newEnemy();

        if (currentLocation == locations) return endGame();

        labels.put("currentEnemy", String.valueOf(currentEnemy));
        labels.put("currentLocation", String.valueOf(currentLocation));
        labels.put("locationEnemies", String.valueOf(locationEnemies));

        playerTurn = true;
        return labels;
    }

    /**
     * Завершение игры
     */
    public HashMap<String, String> endGame() {
        HashMap<String, String> labels = new HashMap<>();
        labels.put("action", "endGame");
        String text = "Победа не на вашей стороне";
        //System.out.println("Player : " + player.getHealth());
        //System.out.println("Enemies : " + enemy.getHealth());
        if (player.getHealth() > 0) {
            player.addPoints();
            text = "Победа на вашей стороне";
        } else {
             text = "Победа не на вашей стороне";
        }

        int place = 0;
        for (Result result : results) {
            if (player.getPoints() < result.getPoints()) {
                place++;
            }
        }

        if (place < 10) {
            labels.put("dialog", "1");
            labels.put("victoryLabel", text);
        } else {
            labels.put("dialog", "2");
            labels.put("victoryLabel", text);
        }
        playerTurn = true;
        return labels;
    }

    /**
     * Завершение игры и внесение результатов
     */
    public ArrayList<Result> addGameResults(String name) {
        results.add(new Result(name, player.getPoints()));
        results.sort(Comparator.comparing(Result::getPoints).reversed());
        exportData();
        return results;
    }

    /**
     * Восстановление игрока
     */
    public boolean resurrect() {
        if (player.getHealth() == 0 & items[2].getAmount() > 0) {
            player.setHealth((int) (player.getMaxHealth() * 0.05));
            items[2].decreaseAmount();
            return true;
        }
        return false;
    }


    public boolean checkLevel() {
        return player.getNextExperience() <= player.getExperience();
    }

    /**
     * Апгрейд уровня игрока и врагов
     */
    public void levelUP(String choice) {
        player.levelUP();
        if (Objects.equals(choice, "HP")) player.levelHP(player);
        else player.levelDMG(player);
        for (int i = 0; i < 5; i++) {
            if (nextLevelExperience[i] == player.getNextExperience()) {
                player.setNextExperience(nextLevelExperience[i + 1]);
                for (int j = 0; j < 4; j++) {
                    enemies[j].levelUP();
                    enemies[j].levelHP(player);
                    enemies[j].levelDMG(player);
                }
                break;
            }
        }

    }

    /**
     * Добавление новых предментов после сражения
     */
    public void addItems(int prob1, int prob2, int prob3) {
        Random random = new Random();
        if (random.nextDouble() * 100 < prob1) {
            items[0].increaseAmount();
        }
        if (random.nextDouble() * 100 < prob2) {
            items[1].increaseAmount();
        }
        if (random.nextDouble() * 100 < prob3) {
            items[2].increaseAmount();
        }
    }

    /**
     * Использование предмета, который выбрал игрок
     */
    public boolean useItem(int choice) {
        boolean isUsed = true;
        switch (choice) {
            case 1:
                if (items[0].getAmount() > 0) {
                    player.heal((int) (player.getMaxHealth() * 0.25));
                    items[0].decreaseAmount();
                } else isUsed = false;
                break;
            case 2:
                if (items[1].getAmount() > 0) {
                    player.heal((int) (player.getMaxHealth() * 0.5));
                    items[1].decreaseAmount();
                } else isUsed = false;
                break;
            case 3:
                isUsed = false;
                break;
        }
        return isUsed;
    }

    /**
     * Загрузка таблицы результатов
     */
    public ArrayList<Result> importData() {
        try {
            XSSFSheet sh;
            try (XSSFWorkbook book = new XSSFWorkbook("Results.xlsx")) {
                sh = book.getSheetAt(0);
            }
            for (int i = 1; i <= sh.getLastRowNum(); i++) {
                results.add(new Result(sh.getRow(i).getCell(1).getStringCellValue(), (int) sh.getRow(i).getCell(2).getNumericCellValue()));
            }
        } catch (org.apache.poi.openxml4j.exceptions.InvalidOperationException | IOException e) {
            System.out.println("No file");
        }
        return this.results;
    }

    /**
     * Выгрузка таблицы результатов
     */
    public void exportData() {
        XSSFWorkbook book = new XSSFWorkbook();
        XSSFSheet sheet = book.createSheet("Результаты ТОП 10");
        XSSFRow r = sheet.createRow(0);
        r.createCell(0).setCellValue("№");
        r.createCell(1).setCellValue("Имя");
        r.createCell(2).setCellValue("Количество баллов");
        for (int i = 0; i < results.size(); i++) {
            if (i < 10) {
                XSSFRow r2 = sheet.createRow(i + 1);
                r2.createCell(0).setCellValue(i + 1);
                r2.createCell(1).setCellValue(results.get(i).getName());
                r2.createCell(2).setCellValue(results.get(i).getPoints());
            }
        }
        File f = new File("Results.xlsx");
        try {
            book.write(new FileOutputStream(f));
            book.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean getTurn() {
        return this.playerTurn;
    }

    public Player getPlayer() {
        return player;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public Item[] getItems() {
        return items;
    }

    public Item getItem(int index) {
        return items[index];
    }

    public int getLocations() {
        return locations;
    }

    public int getCurrentLocation() {
        return currentLocation;
    }

    public int getEnemies() {
        return locationEnemies;
    }

    public int getCurrentEnemy() {
        return currentEnemy;
    }
}
