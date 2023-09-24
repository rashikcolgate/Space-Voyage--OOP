import java.awt.*;
import java.awt.event.*;
import java.util.*;
// !! Worked with Chris on Milestone #2 !!

//A Simple version of the scrolling game, featuring Avoids, Gets, and RareGets
//Players must reach a score threshold to win
//If player runs out of HP (via too many Avoid collisions) they lose
public class SimpleGame extends ScrollingGameEngine {
    
    //Dimensions of game window
    private static final int DEFAULT_WIDTH = 900;
    private static final int DEFAULT_HEIGHT = 600;  
    
    //Starting PlayerEntity coordinates
    private static final int STARTING_PLAYER_X = 0;
    private static final int STARTING_PLAYER_Y = 0;
    
    //Score needed to win the game
    private static final int SCORE_TO_WIN = 500;
    
    //Maximum that the game speed can be increased to
    //(a percentage, ex: a value of 300 = 300% speed, or 3x regular speed)
    private static final int MAX_GAME_SPEED = 300;
    //Interval that the speed changes when pressing speed up/down keys
    private static final int SPEED_CHANGE = 20;    
    
    private static final String INTRO_SPLASH_FILE = "assets/splashy.png";
    private static final String BG_FILE = "assets/background.jpg";        
    //Key pressed to advance past the splash screen
    public static final int ADVANCE_SPLASH_KEY = KeyEvent.VK_ENTER;
    
    //Interval that Entities get spawned in the game window
    //ie: once every how many ticks does the game attempt to spawn new Entities
    private static final int SPAWN_INTERVAL = 45;
    
    
    //A Random object for all your random number generation needs!
    public static final Random rand = new Random();
    
    
    
    
    
    //Player's current score
    private int score;
    
    //Stores a reference to game's PlayerEntity object for quick reference
    //(This PlayerEntity will also be in the displayList)
    public PlayerEntity player;
    
    
    
    
    
    public SimpleGame(){
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    public SimpleGame(int gameWidth, int gameHeight){
        super(gameWidth, gameHeight);
        super.setSplashImage(INTRO_SPLASH_FILE);
    }
    
    
    //Performs all of the initialization operations that need to be done before the game starts
    protected void preGame(){
        this.setBackgroundColor(Color.BLACK);
        super.setBackgroundImage(BG_FILE);
        player = new PlayerEntity(STARTING_PLAYER_X, STARTING_PLAYER_Y);
        displayList.add(player); 
        score = 0;
    }
    
    
    //Called on each game tick
    protected void updateGame(){
        //scroll all scrollable Entities on the game board
        scrollEntities();  
         
        //Spawn new entities only at a certain interval
        if (ticksElapsed % SPAWN_INTERVAL == 0){            
            spawnNewEntities();
            garbageCollectEntities();
        }
        
        //Update the title text on the top of the window
        setTitleText("HP: " + player.getHP() + " Score: " + score);        
    }
    
    
    //Scroll all scrollable entities per their respective scroll speeds
    protected void scrollEntities(){
        for (int i = 0; i < displayList.size(); i++) {
            Entity obj = displayList.get(i);
            if (obj instanceof Scrollable) {
                ((Scrollable) obj).scroll();
            }
            if (obj instanceof Consumable) {
                ArrayList<Entity[]> arr = checkCollision(obj);
                for(Entity[] order : arr){
                    System.out.println(order);
                    handlePlayerCollision(order);
                }
            }
        }
    }
    
    
    //Handles "garbage collection" of the displayList
    //Removes entities from the displayList that are no longer relevant
    //(i.e. will no longer need to be drawn in the game window).
    protected void garbageCollectEntities(){
        for (int i = 0; i<displayList.size();i++) {
            Entity obj = displayList.get(i);
            if (obj.getX()<STARTING_PLAYER_X-obj.getWidth()) { //remove it at the left end of the screen
                displayList.remove(obj);
            }
        }
    }
    
    
    public int getCollisionSource(Entity[] order) {
        for (int i = 0; i < order.length; i++) {
            if (order[i] instanceof PlayerEntity || order[i] instanceof LaserEntity) {
                return i;
            } 
        }
        return -1;
    }

    //Called whenever it has been determined that the PlayerEntity collided with a consumable
    public void handlePlayerCollision(Entity[] order){
        int sourceIdx = getCollisionSource(order);
        Entity collisionSource = order[sourceIdx];
        Consumable collidedWith;
        if (sourceIdx == 0) {
            collidedWith = (Consumable) order[1];
        }
        else {
            collidedWith = (Consumable) order[0];
        }
        System.out.println(collidedWith);
        if (collisionSource instanceof PlayerEntity) {
            player.modifyHP(collidedWith.getDamageValue());
        }
        else if (collisionSource instanceof LaserEntity && collidedWith instanceof RareGetEntity) {
            player.modifyHP(collidedWith.getDamageValue());
        }
        if (score + collidedWith.getPointsValue() >= 0) {
            score += collidedWith.getPointsValue();
        }
        displayList.remove((Entity) collidedWith);
        if (isGameOver()) {
            isPaused = true;
            postGame();
        }
    }
    
    
    //Spawn new Entities on the right edge of the game board
    private void spawnNewEntities(){
        int choice = rand.nextInt(10); 
        Entity obj;
        if (choice >= 0 && choice < 6){
            obj = new GetEntity(getWindowWidth(), rand.nextInt(getWindowHeight() - GetEntity.GET_HEIGHT));
        }
        else if (choice >= 5 && choice <= 8){
            obj = new AvoidEntity(getWindowWidth(), rand.nextInt(getWindowHeight() - AvoidEntity.AVOID_HEIGHT));
        }
        else{
            obj = new RareGetEntity(getWindowWidth(), rand.nextInt(getWindowHeight() - RareGetEntity.GET_HEIGHT));
        }
        displayList.add(obj);
    }
    
    
    //Called once the game is over, performs any end-of-game operations
    protected void postGame(){
        //super.setTitleText("Game is over! You need to do something here!");
        if (score >= SCORE_TO_WIN) {
            super.setTitleText("Game is over! You win!");
        }
        else if (player.getHP() <= 0) {
            super.setTitleText("Game is over! You lost!");
        }
    }
    
    
    //Determines if the game is over or not
    //Game can be over due to either a win or lose state
    protected boolean isGameOver(){
        if (score >= SCORE_TO_WIN) {
            return true;
        }
        else if (player.getHP() <= 0) {
            return true;
        }
        return false;
    }
    
    
    
    //Reacts to a single key press on the keyboard
    protected void handleKeyPress(int key){
        
        setDebugText("Key Pressed!: " + KeyEvent.getKeyText(key));
        //if a splash screen is active, only react to the "advance splash" key... nothing else!
        if (getSplashImage() != null){
            if (key == ADVANCE_SPLASH_KEY)
                super.setSplashImage(null);
            
            return;
        }
        if (!isPaused) {
            if (key == ScrollingGameEngine.UP_KEY) {
                if (player.getY()-player.getMovementSpeed() >=0) {
                    player.setY(player.getY()-player.getMovementSpeed());
                }
            }else if (key == ScrollingGameEngine.DOWN_KEY) {
                if (player.getY()+player.getMovementSpeed() <= (getWindowHeight()-player.getHeight())) {
                    player.setY(player.getY()+player.getMovementSpeed());
                }
            }else if (key == ScrollingGameEngine.LEFT_KEY) {
                if (player.getX()-player.getMovementSpeed() >= 0) {
                    player.setX(player.getX()-player.getMovementSpeed());
                }
            }else if (key == ScrollingGameEngine.RIGHT_KEY) {
                if (player.getX()+player.getMovementSpeed() <= (getWindowWidth()-player.getWidth())) {
                    player.setX(player.getX()+player.getMovementSpeed());
                }
            }
        }

        if (key == ScrollingGameEngine.KEY_PAUSE_GAME && !isPaused) {
            isPaused = true;
        }else if (key == ScrollingGameEngine.KEY_PAUSE_GAME && isPaused) {
            isPaused = false;
        }

        if (key == ScrollingGameEngine.SPEED_UP_KEY && getGameSpeed() < MAX_GAME_SPEED) {
            setGameSpeed(getGameSpeed() + SPEED_CHANGE);
        }else if (key == ScrollingGameEngine.SPEED_DOWN_KEY && getGameSpeed() > SPEED_CHANGE) {
            setGameSpeed(getGameSpeed() - SPEED_CHANGE);
        }
    }    
    
    
    //Handles reacting to a single mouse click in the game window
    //Won't be used in Simple Game... you could use it in Creative Game though!
    protected MouseEvent handleMouseClick(MouseEvent click){
        if (click != null){ //ensure a mouse click occurred
            int clickX = click.getX();
            int clickY = click.getY();
            setDebugText("Click at: " + clickX + ", " + clickY);
        }
        return click;//returns the mouse event for any child classes overriding this method
    }

}
