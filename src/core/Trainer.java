package core;

import utils.PartOfSpeech;
import utils.Parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by ahmet on 21/01/16.
 */
public class Trainer {

    private ArrayList<String> sentences = new ArrayList<>();;

    private HashMap<String, Integer> startCountMap = new HashMap<>();
    private HashMap<String, Integer> tagCountMap = new HashMap<>();
    private HashMap<String, Integer> suffixCountMap = new HashMap<>();

    private HashMap<String, HashMap<String, Integer>> transmissionPairMap = new HashMap<>();
    private HashMap<String, HashMap<String, Integer>> emissionPairMap = new HashMap<>();

    private HashMap<String, Float> startProbabilitiesMap = new HashMap<>();
    private HashMap<String, HashMap<String, Float>> transmissionProbabilitiesMap = new HashMap<>();
    private HashMap<String, HashMap<String, Float>> emissionProbabilitiesMap = new HashMap<>();

    public Trainer(String fileName) {
        Parse.parseTrainFile(fileName, sentences);
    }

    public Trainer() {
    }

    public HashMap<String, Integer> getStartCountMap() {
        return startCountMap;
    }

    public HashMap<String, Integer> getTagCountMap() {
        return tagCountMap;
    }

    public HashMap<String, HashMap<String, Integer>> getTransmissionPairMap() {
        return transmissionPairMap;
    }

    public HashMap<String, HashMap<String, Integer>> getEmissionPairMap() {
        return emissionPairMap;
    }

    public HashMap<String, Float> getStartProbabilitiesMap() {
        return startProbabilitiesMap;
    }

    public HashMap<String, HashMap<String, Float>> getTransmissionProbabilitiesMap() {
        return transmissionProbabilitiesMap;
    }

    public HashMap<String, HashMap<String, Float>> getEmissionProbabilitiesMap() {
        return emissionProbabilitiesMap;
    }

    public HashMap<String, Integer> getSuffixCountMap() {
        return suffixCountMap;
    }

    public void countTransmissionPair(String sentence) {

        String[] words = sentence.split(Parse.boşluk_a);

        String prev = "START";
        for (String s : words) {
            String[] word_tag_pair = s.split(Parse.tag_a);
            String tag = word_tag_pair[1];

            if (tagCountMap.containsKey(tag)) {

                int n = tagCountMap.get(tag) + 1;
                tagCountMap.put(tag, n);
            } else {
                tagCountMap.put(tag, 1);
            }

            if (prev.equals("START")) {
                if (startCountMap.containsKey(tag)) {
                    int n = startCountMap.get(tag) + 1;
                    startCountMap.put(tag, n);
                } else {
                    startCountMap.put(tag, 1);
                }
            } else {
                if (transmissionPairMap.containsKey(prev)) {
                    HashMap<String, Integer> tag_num = transmissionPairMap.get(prev);
                    if (tag_num.containsKey(tag)) {
                        int n = tag_num.get(tag) + 1;
                        tag_num.put(tag, n);
                    } else {
                        tag_num.put(tag, 1);
                    }
                    transmissionPairMap.put(prev, tag_num);
                } else {
                    HashMap<String, Integer> tag_num = new HashMap<String, Integer>();
                    tag_num.put(tag, 1);
                    transmissionPairMap.put(prev, tag_num);
                }
            }
            prev = tag;
        }
    }

    public void countEmissionPair(String sentence) {

        String[] words = sentence.split(Parse.boşluk_a);

        for (String s : words) {
            String[] word_tag_pair = s.split(Parse.tag_a);
            String[] root_suffixes = word_tag_pair[0].split(Parse.ek_a);

            String tag = word_tag_pair[1];
            String suffix = root_suffixes[root_suffixes.length - 1];

            if (suffixCountMap.containsKey(suffix)) {

                int n = suffixCountMap.get(suffix) + 1;
                suffixCountMap.put(suffix, n);
            } else {
                suffixCountMap.put(suffix, 1);
            }

            if (emissionPairMap.containsKey(tag)) {
                HashMap<String, Integer> tag_num = emissionPairMap.get(tag);
                if (tag_num.containsKey(suffix)) {
                    int n = tag_num.get(suffix) + 1;
                    tag_num.put(suffix, n);
                } else {
                    tag_num.put(suffix, 1);
                }
                emissionPairMap.put(tag, tag_num);
            } else {
                HashMap<String, Integer> tag_num = new HashMap<String, Integer>();
                tag_num.put(suffix, 1);
                emissionPairMap.put(tag, tag_num);
            }
        }
    }

    public void calculateStartProbabilities() {

        int totalStartPoint = 0;
        Iterator it = startCountMap.values().iterator();
        while (it.hasNext()){
            totalStartPoint = totalStartPoint + (Integer) it.next();
        }

        for (String s : PartOfSpeech.tag_list){
            if (startCountMap.containsKey(s)){
                startProbabilitiesMap.put(s, ((float) startCountMap.get(s)/totalStartPoint));
            } else {
                startProbabilitiesMap.put(s, 0f);
            }
        }
    }

    public void calculateTransmissionProbability() {

        for (String s : PartOfSpeech.tag_list){
            if (transmissionPairMap.containsKey(s)){
                HashMap<String, Integer> transmitteds = transmissionPairMap.get(s);
                HashMap<String, Float> t_prob = new HashMap<String, Float>();
                for (String t : PartOfSpeech.tag_list){
                    if (transmitteds.containsKey(t)){
                        t_prob.put(t, (float)transmitteds.get(t)/ tagCountMap.get(s));
                    } else {
                        t_prob.put(t, 0f);
                    }
                    transmissionProbabilitiesMap.put(s, t_prob);
                }
            } else {
                HashMap<String, Float> t_prob = new HashMap<String, Float>();
                for (String t : PartOfSpeech.tag_list){
                    t_prob.put(t, 0f);
                }
                transmissionProbabilitiesMap.put(s, t_prob);
            }
        }
    }

    public void calculateEmissionProbabilities() {

        for (String s : PartOfSpeech.tag_list){
            if (emissionPairMap.containsKey(s)){
                HashMap<String, Integer> emitteds = emissionPairMap.get(s);
                HashMap<String, Float> e_prob = new HashMap<String, Float>();
                Iterator it = suffixCountMap.keySet().iterator();
                while (it.hasNext()){
                    String obs = (String)it.next();
                    if (emitteds.containsKey(obs)){
                        e_prob.put(obs, (float)emitteds.get(obs)/ tagCountMap.get(s));
                    } else {
                        e_prob.put(obs, 0f);
                    }
                    emissionProbabilitiesMap.put(s, e_prob);
                }
            } else {
                HashMap<String, Float> e_prob = new HashMap<String, Float>();
                Iterator it = suffixCountMap.keySet().iterator();
                while (it.hasNext()){
                    String obs = (String)it.next();
                    e_prob.put(obs, 0f);
                    emissionProbabilitiesMap.put(s, e_prob);
                }
            }
        }
    }

    public void analyse(){

        for (String sentence : sentences){
            countEmissionPair(sentence);
            countTransmissionPair(sentence);
        }

        calculateStartProbabilities();
        calculateTransmissionProbability();
        calculateTransmissionProbability();

    }
}
