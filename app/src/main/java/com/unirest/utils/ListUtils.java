package com.unirest.utils;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class ListUtils {
    public static <Item> Item getRandomItem(List<Item> items) {
        return items.get((int) (Math.random() * items.size()));
    }
    public static <Item extends Comparable<Item>> List<Item> sort(List<Item> items) {
        return sort(items, false);
    }

    public static <Item extends Comparable<Item>> List<Item> sort(List<Item> items, boolean sortDescending) {
        items.sort(Item::compareTo);
        if (sortDescending) {
            Collections.reverse(items);
        }
        return items;
    }

}
