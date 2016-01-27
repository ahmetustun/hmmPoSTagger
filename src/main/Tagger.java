package main;

import core.Trainer;
import core.Viterbi;
import utils.Parse;
import utils.PartOfSpeech;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ahmetu on 26.01.2016.
 */
public class Tagger {

    public static void main(String[] args) {

        Trainer trainer = new Trainer(args[0]);
        trainer.analyse();


        ArrayList<String> untaggedSentences = new ArrayList<>();
        Parse.parseTrainFile(args[1], untaggedSentences);

        for (String sentence : untaggedSentences){

            String[] words = sentence.split(Parse.boşluk_a);
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
        }
    }

}
