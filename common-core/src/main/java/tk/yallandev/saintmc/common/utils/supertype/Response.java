package tk.yallandev.saintmc.common.utils.supertype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;

/**
 * 
 * @author yandv
 * 
 * This is a class which we will you to response requests with the time when it was created or delay
 * 
 */

@Getter
@AllArgsConstructor
public class Response<T> {

    private T result;
    private long time;

    public boolean hasResult() {
        return result != null;
    }

    @Override
    public String toString() {
        return CommonConst.GSON.toJson(this);
    }

}
