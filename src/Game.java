import logic.Point;
import model.Duck;
import model.GameObject;
import model.Rock;
import model.WaterLily;
import task.DuckWeightDecreaserTask;
import task.NavigatorTask;
import view.UI;
import static logic.Maths.*;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Game implements Runnable {

    private String title;
    private Thread thread;
    private Timer timer;
    private boolean running;
    private List<Duck> ducks;
    private List<Rock> rocks;
    private List<WaterLily> lilies;
    private List<GameObject> gameObjects;
    private UI userInterface;


    public Game(String title, int width, int height){
        this.title = title;
        this.timer = new Timer();
        this.ducks = new ArrayList<>();
        this.rocks = new ArrayList<>();
        this.lilies = new ArrayList<>();
        this.gameObjects = new ArrayList<>();
        this.userInterface = new UI(title, width, height);

        deployUnits();
    }

    public void deployUnits() {
        //duck
        generateRandomDucks(3);

        //rock
        generateRandomRocks(4);

        //Waterlily
        generateRandomLillies(5);

    }

    public void generateRandomDucks(int amount) {
        for (int i=0; i<amount; i++) {
            Duck duck = new Duck("duck"+i, 0, 0);
            double xCoor = getRandomNumberInRange(0, getWidth() - duck.getWidth());
            double yCoor = getRandomNumberInRange(0, getHeight() - duck.getHeight());
            duck.setX((int) xCoor);
            duck.setY((int) yCoor);
            duck.setWeight(500);
            this.ducks.add(duck);
            duck.setAlive(true);
            duck.setVisible(true);
            this.gameObjects.add(duck);
        }
    }

    public void generateRandomLillies(int amount){
        for (int i=0; i<amount; i++) {
            WaterLily lily = new WaterLily("lily"+i, 0, 0);
            int xCoor = (int) (Math.random() * this.getWidth() - lily.getWidth() + 1 ); //this will get us a random value between 0 and width
            int yCoor = (int) (Math.random() * this.getHeight() - lily.getHeight() + 1); //this will get us a random value between 0 and height
            lily.setX(xCoor);
            lily.setY(yCoor);
            this.lilies.add(lily);
            lily.setAlive(true);
            lily.setVisible(true);
            this.gameObjects.add(lily);
        }
    }

    public void generateRandomRocks(int amount) {
        for (int i=0; i<amount; i++) {
            Rock rock = new Rock("rock"+i, 0, 0);
            int xCoor = (int) (Math.random() * this.getWidth() - rock.getWidth() + 1 ); //this will get us a random value between 0 and width
            int yCoor = (int) (Math.random() * this.getHeight() - rock.getHeight() + 1); //this will get us a random value between 0 and height
            rock.setX(xCoor);
            rock.setY(yCoor);
            this.rocks.add(rock);
            rock.setVisible(true);
            this.gameObjects.add(rock);
        }
    }

    public void scheduleDucksWeightLoss(){
        DuckWeightDecreaserTask duckWeightDecreaser = new DuckWeightDecreaserTask();
        duckWeightDecreaser.setDucks(this.ducks);
        this.timer.scheduleAtFixedRate(duckWeightDecreaser, 5*1000, 5*1000);
    }

    private void scheduleDucksNavigation() {
        for (Duck duck: getDucks()) {
            NavigatorTask navigatorTask = new NavigatorTask(duck, new Point(100, 100));
            navigatorTask.setBoundaries(new Rectangle(getWidth() - 100, getHeight() - 100));
            this.timer.scheduleAtFixedRate(navigatorTask, 0, 10);
        }
    }

    public void init() throws IOException, InterruptedException {
        userInterface.getScene().setGameObjects(gameObjects);
        userInterface.setVisible(true);
        this.scheduleDucksWeightLoss();
        this.scheduleDucksNavigation();
    }

    public synchronized void start() {
        if (running)
            return;
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void update() {
        lilies = getLilies();
        for (WaterLily lily: lilies){
            if (lily.isAlive()){
                return;
            } else {
                generateRandomLillies(1);
            }
        }
    }

    public synchronized void stop() throws InterruptedException {
        if (!running)
            return;
        thread.join();
    }

    @Override
    public void run() {
        try {
            init();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        while (running) {
            try {
                Thread.sleep(10);
                update();
                this.userInterface.repaint();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Duck> getDucks() {
        return ducks;
    }

    public void setDucks(List<Duck> ducks) {
        this.ducks = ducks;
    }

    public List<WaterLily> getLilies() {
        return lilies;
    }

    public void setLilies(List<WaterLily> lilies) {
        this.lilies = lilies;
    }

    public List<Rock> getRocks() {
        return rocks;
    }

    public void setRocks(List<Rock> rocks) {
        this.rocks = rocks;
    }

    public int getWidth() {
        return userInterface.getWidth();
    }

    public int getHeight() {
        return userInterface.getHeight();
    }
}



