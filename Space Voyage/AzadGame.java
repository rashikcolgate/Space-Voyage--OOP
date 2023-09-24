import java.awt.event.KeyEvent;
import java.nio.channels.GatheringByteChannel;
import java.util.ArrayList;

import org.w3c.dom.events.MouseEvent;

public class AzadGame extends SimpleGame {

    public static final int Toggle_Fire = KeyEvent.VK_SPACE;


    private void spawnNewEntities(int key){
        int choice = player.getHP(); 
        Entity obj;
        if (choice >= 4){
            if (key == Toggle_Fire){
                obj = new LaserEntity(player.getX()+15, player.getY());
                player.modifyHP(-1);
                displayList.add(obj);
            }
        }
    }

    protected void handleKeyPress(int key){
        super.handleKeyPress(key);
        System.out.println("Key press creative");
        spawnNewEntities(key);
    }

    //displayList.remove((Entity) collidedWith);
    protected void garbageCollectEntities(){
        for (int i = 0; i<displayList.size();i++) {
            Entity obj = displayList.get(i);
            if (obj.getX()< 0 - obj.getWidth()) { //remove it at the left end of the screen
                displayList.remove(obj);
            }
        }
    }

    protected void scrollEntities(){
        super.scrollEntities();
        for (int i = 0; i < displayList.size(); i++) {
            Entity obj = displayList.get(i);
            if (obj instanceof LaserEntity) {
                ((Scrollable) obj).scroll();

            }
            if (obj instanceof Consumable) {
                ArrayList<Entity[]> arr = checkCollision(obj);
                for(Entity[] order : arr){
                    handlePlayerCollision(order);
                }
            }
        }
    }
}