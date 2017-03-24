package wx.csba.model.entity;

import lombok.Data;

@Data
public class Greeting {

    long counter;

    String message;

    /**
     * @param counter
     * @function 默认构造函数
     */
    public Greeting(long counter, String message) {
        this.counter = counter;
        this.message = message;
    }

}