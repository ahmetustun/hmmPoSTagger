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

        analyser.count_transmission_pair();
        analyser.count_emission_pair();
        analyser.calculateStartProbabilities();
        analyser.calculate_transmission_probability();
        analyser.calculate_emission_probabilities();

        HashMap<String, Integer> my_start_count = analyser.getStart_count();
        HashMap<String, Integer> my_POS_tag_count = analyser.getPOS_tag_count();
        HashMap<String, Integer> my_obs_count = analyser.getObs_count();
        HashMap<String, HashMap<String, Integer>> my_transmission_pair_count = analyser.getCtag_ptag_num();
        HashMap<String, HashMap<String, Integer>> my_emission_pair_count = analyser.getSuffix_tag_num();
        HashMap<String, Float> my_start_prob = analyser.getStart_probabilities();
        HashMap<String, HashMap<String, Float>> my_transmission_prob = analyser.getTransmission_probabilities();
        HashMap<String, HashMap<String, Float>> my_emission_prob = analyser.getEmission_probabilities();

        System.out.println("Tamamlandı");

    }

}
