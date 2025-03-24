package kz.book.eater;

import java.util.List;

public class BookItem {
    private int id;
    private String title, description, author;
    private byte[] image;
    private List<String> audio, genres;
    private String chapter;

    public BookItem(int id, String title, String description, String author, byte[] image, List<String> audio, List<String> genres, String chapter) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = author;
        this.image = image;
        this.audio = audio;
        this.genres = genres;
        this.chapter = chapter;
    }

    public BookItem(int id, String title, String description, String author, byte[] image, List<String> genres, String chapter) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = author;
        this.image = image;
        this.genres = genres;
        this.chapter = chapter;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public byte[] getImage() {
        return image;
    }

    public List<String> getAudio() {
        return audio;
    }

    public List<String> getGenres() {
        return genres;
    }

    public String getChapter() {
        return chapter;
    }
}
