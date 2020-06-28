package tk.yallandev.saintmc.common.utils.supertype;

import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;

/**
 * 
 * @author yandv
 * 
 * This is a class which you can write a object with time when it was created and store it.
 * 
 */

@Getter
public class Conservative<T> {
    
    public Conservative(T objectToConserve) {
        if (objectToConserve == null)
            throw new NullPointerException("Object can not be null");
        
        this.objectToConserve = objectToConserve;
        this.time = System.currentTimeMillis();
    }
    
    private T objectToConserve;
    private long time;

    @Override
    public String toString() {
        return CommonConst.GSON.toJson(this);
    }
    
}
