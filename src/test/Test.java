package test;

import core.Analyser;

import java.util.HashMap;

/**
 * Created by ahmet on 21/01/16.
 */
public class Test {

    public static void main(String[] args) {

        String corpus = "Ahmet_∅/NOUN ev_e/NOUN gel_di/VERB gel_di/VERB";

        Analyser analyser = new Analyser(corpus);

        HashMap<String, HashMap<String, Integer>> my_transmission = analyser.count_transmission_pair();
        HashMap<String, HashMap<String, Integer>> my_emission = analyser.count_emission_pair();

        HashMap<String, Integer> my_start_count = analyser.getStart_count();
        HashMap<String, Integer> my_POS_tag_count = analyser.getPOS_tag_count();

        System.out.println("Tamamlandı");

    }

}
