//A RareGetEntity is a special kind of GetEntity that spawns more infrequently than the regular GetEntity
//When consumed, RareGetEntities restores the PlayerEntity's HP in addition to awarding points
//Otherwise, behaves the same as a regular GetEntity
public class RareGetEntity extends GetEntity{
    
    //Location of image file to be drawn for a RareGetEntity
    private static final String RAREGET_IMAGE_FILE = "assets/Gold_coin.gif";
    
    public RareGetEntity(){
        this(0, 0);        
    }
    
    public RareGetEntity(int x, int y){
        super(x, y, RAREGET_IMAGE_FILE);  
    }

    public int getDamageValue() {
        return 1;
    }

    public int getPointsValue() {
        //implement me!
        return 0;
    }
}
