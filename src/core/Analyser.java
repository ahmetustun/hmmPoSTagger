package core;

import utils.PartOfSpeech;
import utils.Parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by ahmet on 21/01/16.
 */
public class Analyser {

    private ArrayList<String> sentences;

    private HashMap<String, Integer> startCount;
    private HashMap<String, Integer> tagCount;
    private HashMap<String, Integer> suffixCount;

    private HashMap<String, HashMap<String, Integer>> transmissionPair;
    private HashMap<String, HashMap<String, Integer>> emissionPair;

    private HashMap<String, Float> startProbabilities;
    private HashMap<String, HashMap<String, Float>> transmissionProbabilities;
    private HashMap<String, HashMap<String, Float>> emissionProbabilities;

    public Analyser(String fileName) {

        sentences = new ArrayList<>();
        startCount = new HashMap<String, Integer>();
        tagCount = new HashMap<String, Integer>();
        suffixCount = new HashMap<String, Integer>();
        transmissionPair = new HashMap<String, HashMap<String, Integer>>();
        emissionPair = new HashMap<String, HashMap<String, Integer>>();
        startProbabilities = new HashMap<String, Float>();
        transmissionProbabilities = new HashMap<String, HashMap<String, Float>>();
        emissionProbabilities = new HashMap<String, HashMap<String, Float>>();

        Parse.parseTrainFile(fileName, sentences);

    }

    public Analyser() {

        sentences = new ArrayList<>();
        startCount = new HashMap<String, Integer>();
        tagCount = new HashMap<String, Integer>();
        suffixCount = new HashMap<String, Integer>();
        transmissionPair = new HashMap<String, HashMap<String, Integer>>();
        emissionPair = new HashMap<String, HashMap<String, Integer>>();
        startProbabilities = new HashMap<String, Float>();
        transmissionProbabilities = new HashMap<String, HashMap<String, Float>>();
        emissionProbabilities = new HashMap<String, HashMap<String, Float>>();

    }

    public HashMap<String, Integer> getStartCount() {
        return startCount;
    }

    public HashMap<String, Integer> getTagCount() {
        return tagCount;
    }

    public HashMap<String, HashMap<String, Integer>> getTransmissionPair() {
        return transmissionPair;
    }

    public HashMap<String, HashMap<String, Integer>> getEmissionPair() {
        return emissionPair;
    }

    public HashMap<String, Float> getStartProbabilities() {
        return startProbabilities;
    }

    public HashMap<String, HashMap<String, Float>> getTransmissionProbabilities() {
        return transmissionProbabilities;
    }

    public HashMap<String, HashMap<String, Float>> getEmissionProbabilities() {
        return emissionProbabilities;
    }

    public HashMap<String, Integer> getSuffixCount() {
        return suffixCount;
    }

    public void countTransmissionPair(String sentence) {

        String[] words = sentence.split(Parse.boşluk_a);

        String prev = "START";
        for (String s : words) {
            String[] word_tag_pair = s.split(Parse.tag_a);
            String tag = word_tag_pair[1];

            if (tagCount.containsKey(tag)) {

                int n = tagCount.get(tag) + 1;
                tagCount.put(tag, n);
            } else {
                tagCount.put(tag, 1);
            }

            if (prev.equals("START")) {
                if (startCount.containsKey(tag)) {
                    int n = startCount.get(tag) + 1;
                    startCount.put(tag, n);
                } else {
                    startCount.put(tag, 1);
                }
            } else {
                if (transmissionPair.containsKey(prev)) {
                    HashMap<String, Integer> tag_num = transmissionPair.get(prev);
                    if (tag_num.containsKey(tag)) {
                        int n = tag_num.get(tag) + 1;
                        tag_num.put(tag, n);
                    } else {
                        tag_num.put(tag, 1);
                    }
                    transmissionPair.put(prev, tag_num);
                } else {
                    HashMap<String, Integer> tag_num = new HashMap<String, Integer>();
                    tag_num.put(tag, 1);
                    transmissionPair.put(prev, tag_num);
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

            if (suffixCount.containsKey(suffix)) {

                int n = suffixCount.get(suffix) + 1;
                suffixCount.put(suffix, n);
            } else {
                suffixCount.put(suffix, 1);
            }

            if (emissionPair.containsKey(tag)) {
                HashMap<String, Integer> tag_num = emissionPair.get(tag);
                if (tag_num.containsKey(suffix)) {
                    int n = tag_num.get(suffix) + 1;
                    tag_num.put(suffix, n);
                } else {
                    tag_num.put(suffix, 1);
                }
                emissionPair.put(tag, tag_num);
            } else {
                HashMap<String, Integer> tag_num = new HashMap<String, Integer>();
                tag_num.put(suffix, 1);
                emissionPair.put(tag, tag_num);
            }
        }
    }

    public void calculateStartProbabilities() {

        int totalStartPoint = 0;
        Iterator it = startCount.values().iterator();
        while (it.hasNext()){
            totalStartPoint = totalStartPoint + (Integer) it.next();
        }

        for (String s : PartOfSpeech.tag_list){
            if (startCount.containsKey(s)){
                startProbabilities.put(s, ((float) startCount.get(s)/totalStartPoint));
            } else {
                startProbabilities.put(s, 0f);
            }
        }
    }

    public void calculateTransmissionProbability() {

        for (String s : PartOfSpeech.tag_list){
            if (transmissionPair.containsKey(s)){
                HashMap<String, Integer> transmitteds = transmissionPair.get(s);
                HashMap<String, Float> t_prob = new HashMap<String, Float>();
                for (String t : PartOfSpeech.tag_list){
                    if (transmitteds.containsKey(t)){
                        t_prob.put(t, (float)transmitteds.get(t)/ tagCount.get(s));
                    } else {
                        t_prob.put(t, 0f);
                    }
                    transmissionProbabilities.put(s, t_prob);
                }
            } else {
                HashMap<String, Float> t_prob = new HashMap<String, Float>();
                for (String t : PartOfSpeech.tag_list){
                    t_prob.put(t, 0f);
                }
                transmissionProbabilities.put(s, t_prob);
            }
        }
    }

    public void calculateEmissionProbabilities() {

        for (String s : PartOfSpeech.tag_list){
            if (emissionPair.containsKey(s)){
                HashMap<String, Integer> emitteds = emissionPair.get(s);
                HashMap<String, Float> t_prob = new HashMap<String, Float>();
                Iterator it = suffixCount.keySet().iterator();
                while (it.hasNext()){
                    String obs = (String)it.next();
                    if (emitteds.containsKey(obs)){
                        t_prob.put(obs, (float)emitteds.get(obs)/ tagCount.get(s));
                    } else {
                        t_prob.put(obs, 0f);
                    }
                    emissionProbabilities.put(s, t_prob);
                }
            } else {
                HashMap<String, Float> t_prob = new HashMap<String, Float>();
                Iterator it = suffixCount.keySet().iterator();
                while (it.hasNext()){
                    String obs = (String)it.next();
                    t_prob.put(obs, 0f);
                    emissionProbabilities.put(s, t_prob);
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
