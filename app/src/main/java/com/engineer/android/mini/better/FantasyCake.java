package com.engineer.android.mini.better;

/**
 * Created on 2021/8/9.
 *
 * @author rookie
 */
public class FantasyCake {
    public static final int FLAG_ADD_POTATO_SHREDS  = 0x00000001; // 加土豆丝
    public static final int FLAG_ADD_SEAWEED_STRIPS = 0x00000002; // 加海带丝
    public static final int FLAG_ADD_LETTUCE        = 0x00000004; // 加生菜

    private int style = 0; // 默认

    @Override
    public String toString() {
        if ((this.style & FLAG_ADD_SEAWEED_STRIPS) != FLAG_ADD_SEAWEED_STRIPS) {
            // 没有加海带丝时警告
            System.err.println("without add seaweed_strips");
        }

        return "FantasyCake{" +
                "style=" + Integer.toBinaryString(this.style) +
                '}';
    }

    public void addLettuce() {
        this.style |= FLAG_ADD_LETTUCE;
    }

    public void addPotatoShreds() {
        this.style |= FLAG_ADD_POTATO_SHREDS;
    }

    public void addSeaweedStrips() {
        this.style |= FLAG_ADD_SEAWEED_STRIPS;
    }

    public int checkout(int money) {
        int base = 5; // 基础价格 5

        if ((style & FLAG_ADD_LETTUCE) == FLAG_ADD_LETTUCE) {
            base = base + 1;  // 如果加生菜了，加 1
        }

        if ((style & FLAG_ADD_SEAWEED_STRIPS) == FLAG_ADD_SEAWEED_STRIPS) {
            base = base + 2; // 如果加海带丝了 加 2
        }

        if ((style & FLAG_ADD_POTATO_SHREDS) == FLAG_ADD_POTATO_SHREDS) {
            base = base + 3; // 如果加 土豆丝了 加 3
        }

        if (base > money) {
            // 如果钱超了，把土豆丝去掉，重新算
            style &= ~FLAG_ADD_POTATO_SHREDS;
            return checkout(money);
        }

        return base;
    }
}
