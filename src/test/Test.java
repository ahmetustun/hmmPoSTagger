package test;

import core.Trainer;
import core.Viterbi;
import utils.Parse;
import utils.PartOfSpeech;

import java.util.HashMap;

/**
 * Created by ahmet on 21/01/16.
 */
public class Test {

    public static void main(String[] args) {

        String corpus = "Ahmet_∅/NOUN ev_e/NOUN gel_di/VERB";
        String untagged = "gel_di/VERB Ahmet_∅/NOUN ev_e/NOUN";

        Trainer trainer = new Trainer();

        trainer.countTransmissionPair(corpus);
        trainer.countEmissionPair(corpus);
        trainer.calculateStartProbabilities();
        trainer.calculateTransmissionProbability();
        trainer.calculateEmissionProbabilities();

        HashMap<String, Integer> my_start_count = trainer.getStartCountMap();
        HashMap<String, Integer> my_POS_tag_count = trainer.getTagCountMap();
        HashMap<String, Integer> my_obs_count = trainer.getSuffixCountMap();
        HashMap<String, HashMap<String, Integer>> my_transmission_pair_count = trainer.getTransmissionPairMap();
        HashMap<String, HashMap<String, Integer>> my_emission_pair_count = trainer.getEmissionPairMap();
        HashMap<String, Float> my_start_prob = trainer.getStartProbabilitiesMap();
        HashMap<String, HashMap<String, Float>> my_transmission_prob = trainer.getTransmissionProbabilitiesMap();
        HashMap<String, HashMap<String, Float>> my_emission_prob = trainer.getEmissionProbabilitiesMap();

        String[] words = untagged.split(Parse.boşluk_a);
        HashMap<String, Integer> suffixCount = new HashMap<>();

        for (String s : words) {
            String[] word_tag_pair = s.split(Parse.tag_a);
            String[] root_suffixes = word_tag_pair[0].split(Parse.ek_a);

            String tag = word_tag_pair[1];
            String suffix = root_suffixes[root_suffixes.length - 1];

            if (suffixCount.containsKey(suffix)) {

                int n = suffixCount.get(suffix) + 1;
                suffixCount.put(suffix, n);
            } else {
                suffixCount.put(suffix, 1);
            }
        }

        String[] obs_list = suffixCount.keySet().toArray(new String[0]);

        Object[] ret = Viterbi.forward_viterbi(obs_list, PartOfSpeech.tag_list,
                trainer.getStartProbabilitiesMap(), trainer.getTransmissionProbabilitiesMap(), trainer.getEmissionProbabilitiesMap());

        System.out.println(((Float) ret[0]).floatValue());
        System.out.println((String) ret[1]);
        System.out.println(((Float) ret[2]).floatValue());

        System.out.println("Tamamlandı");

    }

}
