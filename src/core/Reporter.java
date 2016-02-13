package core;

import utils.Bigram;
import utils.PartOfSpeech;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by ahmet on 13/02/16.
 */
public class Reporter {

    public static void reportTagCount(HashMap<String, Double> POS_tag_count){
        System.out.println("=================== PoS tag count ====================");
        for (String tag : PartOfSpeech.tag_list){
            System.out.println(tag + ": " + POS_tag_count.get(tag));
        }
    }

    public static void reportTagRatio(HashMap<String, Double> POS_tag_count){
        double total = 0d;
        for (String tag : PartOfSpeech.tag_list){
            total = total + POS_tag_count.get(tag);
        }
        System.out.println("=================== PoS tag ratio ====================");
        for (String tag : PartOfSpeech.tag_list){
            System.out.println(tag + ": " + POS_tag_count.get(tag)/total);
        }
    }

    public static void reportSuffixCount(HashMap<String, Double> obs_count){
        System.out.println("=================== Suffix tag count ====================");
        Iterator it = obs_count.keySet().iterator();
        while (it.hasNext()){
            String suffix = (String) it.next();
            System.out.println(suffix + ": " + obs_count.get(suffix));
        }
    }

    public static void reportSuffixRatio(HashMap<String, Double> obs_count){
        System.out.println("=================== Suffix tag ratio ====================");
        double total = 0d;
        Iterator it = obs_count.keySet().iterator();
        while (it.hasNext()){
            String suffix = (String) it.next();
            total = total + obs_count.get(suffix);
        }Iterator it_2 = obs_count.keySet().iterator();
        while (it_2.hasNext()){
            String suffix = (String) it_2.next();
            System.out.println(suffix + ": " + obs_count.get(suffix)/total);
        }
    }

    public static void reportStartProbabilities(HashMap<String, Double> start_prob){
        System.out.println("=================== Start Probabilities ====================");
        for (String tag : PartOfSpeech.tag_list){
            System.out.println(tag + ": " + start_prob.get(tag));
        }
    }

    public static void reportBiagramTransitionProbabilities(HashMap<String, HashMap<String, Double>> transition_prob) {
        System.out.println("=================== Bigram Transition Probabilities ====================");
        for (String t1 : PartOfSpeech.tag_list){
            for (String t2 : PartOfSpeech.tag_list){
                System.out.println("P("+t2+"|"+t1+"): " + transition_prob.get(t1).get(t2));
            }
        }
    }

    public static void reportTrigramTransitionProbabilities(HashMap<Bigram<String, String>, HashMap<String, Double>> trigramTransmissionPairMap){
        System.out.println("=================== Trigram Transition Probabilities ====================");
        for (String t1 : PartOfSpeech.tag_list){
            for (String t2 : PartOfSpeech.tag_list){
                Bigram<String, String> bigram = new Bigram<>(t1,t2);
                for (String t3 : PartOfSpeech.tag_list){
                    System.out.println("P("+t3+"|"+t1+","+t2+"): " + trigramTransmissionPairMap.get(bigram).get(t3));
                }
            }
        }
    }

    public static void reportEmissionProbabilities(HashMap<String, HashMap<String, Double>> emission_prob){
        System.out.println("=================== Emission Probabilities ====================");
        for (String tag : PartOfSpeech.tag_list){
            Iterator it = emission_prob.get(tag).keySet().iterator();
            while (it.hasNext()){
                String suffix = (String) it.next();
                System.out.println("P("+suffix+"|"+tag+"): " + emission_prob.get(tag).get(suffix));
            }
        }
    }

}
