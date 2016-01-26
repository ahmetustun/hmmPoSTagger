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

        analyser.countTransmissionPair(corpus);
        analyser.countEmissionPair(corpus);
        analyser.calculateStartProbabilities();
        analyser.calculateTransmissionProbability();
        analyser.calculateEmissionProbabilities();

        HashMap<String, Integer> my_start_count = analyser.getStartCount();
        HashMap<String, Integer> my_POS_tag_count = analyser.getTagCount();
        HashMap<String, Integer> my_obs_count = analyser.getSuffixCount();
        HashMap<String, HashMap<String, Integer>> my_transmission_pair_count = analyser.getTransmissionPair();
        HashMap<String, HashMap<String, Integer>> my_emission_pair_count = analyser.getEmissionPair();
        HashMap<String, Float> my_start_prob = analyser.getStartProbabilities();
        HashMap<String, HashMap<String, Float>> my_transmission_prob = analyser.getTransmissionProbabilities();
        HashMap<String, HashMap<String, Float>> my_emission_prob = analyser.getEmissionProbabilities();

        System.out.println("Tamamlandı");

    }

}
