package kz.book.eater;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JsonReader {
    private static Context context;

    public static void setContext(Context context) {
        JsonReader.context = context;
    }

    public static List<BookItem> readJson() {
        try (InputStream is = context.getAssets().open("books.json")) {
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            JSONArray array = new JSONArray(new String(buffer));
            List<BookItem> items = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                byte[] img = getImage(object.getString("image"));
                //List<String> txt = getChapters(object.getString("text"));
                List<String> genres = new ArrayList<>();
                for (int j = 0; j < object.getJSONArray("genres").length(); j++) {
                    genres.add(object.getJSONArray("genres").getString(j));
                }
                try {
                    object.getString("audio");
                    items.add(new BookItem(
                            object.getInt("id"),
                            object.getString("title"),
                            object.getString("description"),
                            object.getString("author"),
                            img,
                            Arrays.stream(context.getAssets().list(object.getString("audio")))
                                    .map(fileName -> {
                                        try {
                                            return object.getString("audio") + "/" + fileName;
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    })
                                    .collect(Collectors.toList()),
                            genres,
                            object.getString("text")
                    ));
                } catch (JSONException e) {
                    items.add(new BookItem(
                            object.getInt("id"),
                            object.getString("title"),
                            object.getString("description"),
                            object.getString("author"),
                            img,
                            genres,
                            object.getString("text")
                    ));
                }
            }
            return items;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    private static byte[] getImage(String name) throws IOException {
        try (InputStream imageStream = context.getAssets().open(name);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byte[] bufferImage = new byte[1024];
            int length;

            while ((length = imageStream.read(bufferImage)) != -1) {
                byteArrayOutputStream.write(bufferImage, 0, length);
            }

            return byteArrayOutputStream.toByteArray();
        }
    }

//    private static byte[][] getAudioFiles(String name) throws IOException {
//        String[] audioFiles = context.getAssets().list(name);
//        byte[][] mp3 = new byte[audioFiles.length][];  // Массив для хранения данных о файлах
//
//        for (int i = 0; i < audioFiles.length; i++) {
//            // Используем InputStream для чтения файла
//            try (InputStream inputStream = context.getAssets().open(name + "/" + audioFiles[i])) {
//                // Получаем размер файла
//                long fileSize = inputStream.available();
//
//                // Определяем, нужно ли разделять файл на части для обработки (если он слишком большой)
//                if (fileSize < Integer.MAX_VALUE) {  // Если файл не слишком большой, работаем с ним как с массивом
//                    byte[] fileData = new byte[(int) fileSize]; // Преобразуем в массив, если файл маленький
//                    inputStream.read(fileData);
//                    mp3[i] = fileData; // Сохраняем в массив
//                } else {
//                    // Если файл большой, читаем его частями (меньше нагрузки на память)
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    byte[] buffer = new byte[1024];  // Буфер для чтения данных
//                    int length;
//
//                    // Чтение файла по частям
//                    while ((length = inputStream.read(buffer)) != -1) {
//                        byteArrayOutputStream.write(buffer, 0, length);
//                    }
//
//                    // Преобразуем собранные данные в массив байтов
//                    mp3[i] = byteArrayOutputStream.toByteArray();
//                }
//            }
//        }
//        return mp3;
//    }
}
