package cc.shinichi.drawlongpicturedemo.data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 工藤
 * @email 18883840501@163.com
 * cc.shinichi.drawlongpicturedemo.data
 * create at 2018/8/27  18:07
 * description:
 */
public class Info implements Serializable {

    String content;
    List<String> imageList;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }
}