package kz.book.eater;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User implements Serializable {
    private List<Long> purchased = new ArrayList<>();
    private List<Long> favorite = new ArrayList<>();
    private List<Long> cart = new ArrayList<>();
    private HashMap<Integer, Long> mark = new HashMap<>();

    public List<Long> getPurchased() {
        return purchased;
    }

    public void setPurchased(List<Long> purchased) {
        this.purchased = purchased;
    }

    public void addPurchased(long id) {
        purchased.add(id);
    }

    public List<Long> getFavorite() {
        return favorite;
    }

    public void setFavorite(List<Long> favorite) {
        this.favorite = favorite;
    }

    public void removeFavorite(long id) {
        favorite.remove(id);
    }

    public List<Long> getCart() {
        return cart;
    }

    public void setCart(List<Long> cart) {
        this.cart = cart;
    }

    public HashMap<Integer, Long> getMark() {
        return mark;
    }

    public void setMark(HashMap<Integer, Long> mark) {
        this.mark = mark;
    }
}
