package main;

import core.Analyser;

/**
 * Created by ahmetu on 26.01.2016.
 */
public class Tagger {

    public static void main(String[] args) {

        Analyser analyser = new Analyser(args[0]);
        analyser.tagger();

    }

}
