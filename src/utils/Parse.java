package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Created by ahmetu on 25.01.2016.
 */
public class Parse {

    public static String boşluk_a = " ";
    public static String ek_a = "_";
    public static String tag_a = "/";

    public static void parseTrainFile(String fileName , final ArrayList<String> sentences){
        try (Stream<String> lines = Files.lines(Paths.get(fileName), Charset.defaultCharset())) {
            lines.forEachOrdered(line -> sentences.add(line));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
