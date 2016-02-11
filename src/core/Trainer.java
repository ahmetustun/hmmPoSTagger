package core;

import utils.Bigram;
import utils.PartOfSpeech;
import utils.Parse;
import utils.Trigram;

import java.util.*;

/**
 * Created by ahmet on 21/01/16.
 */
public class Trainer {

    private ArrayList<String> sentences = new ArrayList<>();

    private HashMap<String, Double> startCountMap = new HashMap<>();
    private HashMap<String, Double> tagCountMap = new HashMap<>();
    private HashMap<Bigram<String, String>, Double> bigramCountMap = new HashMap<>();
    private HashMap<Trigram<String, String, String>, Double> trigramCountMap = new HashMap<>();

    private HashMap<String, Double> suffixCountMap = new HashMap<>();

    private HashMap<String, HashMap<String, Double>> bigramTransmissionPairMap = new HashMap<>();
    private HashMap<String, HashMap<String, Double>> emissionPairMap = new HashMap<>();

    private HashMap<String, Double> startProbabilitiesMap = new HashMap<>();
    private HashMap<String, HashMap<String, Double>> bigramTransmissionProbabilitiesMap = new HashMap<>();
    private HashMap<String, HashMap<String, Double>> emissionProbabilitiesMap = new HashMap<>();

    private HashMap<Bigram<String, String>, HashMap<String, Double>> trigramTransmissionPairMap = new HashMap<>();
    private HashMap<Bigram<String, String>, HashMap<String, Double>> trigramTransmissionProbabilityMap = new HashMap<>();

    public Trainer(String fileName) {
        for (String s : PartOfSpeech.tag_list){
            tagCountMap.put(s, 0d);
            for (String k : PartOfSpeech.tag_list){
                Bigram<String, String> bigram = new Bigram<>(s, k);
                bigramCountMap.put(bigram, 0d);
                for (String m : PartOfSpeech.tag_list){
                    Trigram<String, String, String> trigram = new Trigram<>(s, k, m);
                    trigramCountMap.put(trigram, 0d);
                }
            }
        }
        Parse.parseTrainFile(fileName, sentences);
    }

    public Trainer() {
        for (String s : PartOfSpeech.tag_list){
            tagCountMap.put(s, 0d);
            for (String k : PartOfSpeech.tag_list){
                Bigram<String, String> bigram = new Bigram<>(s, k);
                bigramCountMap.put(bigram, 0d);
                for (String m : PartOfSpeech.tag_list){
                    Trigram<String, String, String> trigram = new Trigram<>(s, k, m);
                    trigramCountMap.put(trigram, 0d);
                }
            }
        }
    }

    public HashMap<String, Double> getStartCountMap() {
        return startCountMap;
    }

    public HashMap<String, Double> getTagCountMap() {
        return tagCountMap;
    }

    public HashMap<Bigram<String, String>, Double> getBigramCountMap() {
        return bigramCountMap;
    }

    public HashMap<Trigram<String, String, String>, Double> getTrigramCountMap() {
        return trigramCountMap;
    }

    public HashMap<String, HashMap<String, Double>> getBigramTransmissionPairMap() {

        return bigramTransmissionPairMap;
    }

    public HashMap<String, HashMap<String, Double>> getEmissionPairMap() {
        return emissionPairMap;
    }

    public HashMap<String, Double> getStartProbabilitiesMap() {
        return startProbabilitiesMap;
    }

    public HashMap<String, HashMap<String, Double>> getBigramTransmissionProbabilitiesMap() {
        return bigramTransmissionProbabilitiesMap;
    }

    public HashMap<String, HashMap<String, Double>> getEmissionProbabilitiesMap() {
        return emissionProbabilitiesMap;
    }

    public HashMap<String, Double> getSuffixCountMap() {
        return suffixCountMap;
    }

    public HashMap<Bigram<String, String>, HashMap<String, Double>> getTrigramTransmissionPairMap() {
        return trigramTransmissionPairMap;
    }

    public HashMap<Bigram<String, String>, HashMap<String, Double>> getTrigramTransmissionProbabilityMap() {
        return trigramTransmissionProbabilityMap;
    }

    public void countBigramTransmissionPair(String sentence) {

        String[] words = sentence.split(Parse.boşluk_a);

        String prev = "START";
        for (String s : words) {
            String[] word_tag_pair = s.split(Parse.tag_a);
            String tag = "";
            if (word_tag_pair.length < 2){
                tag = "Noun";
            } else {
                tag = word_tag_pair[1];
            }

            if (tagCountMap.containsKey(tag)) {

                double n = tagCountMap.get(tag) + 1d;
                tagCountMap.put(tag, n);
            } else {
                tagCountMap.put(tag, 1d);
            }

            if (prev.equals("START")) {
                if (startCountMap.containsKey(tag)) {
                    double n = startCountMap.get(tag) + 1d;
                    startCountMap.put(tag, n);
                } else {
                    startCountMap.put(tag, 1d);
                }
            } else {
                if (bigramTransmissionPairMap.containsKey(prev)) {
                    HashMap<String, Double> tag_num = bigramTransmissionPairMap.get(prev);
                    if (tag_num.containsKey(tag)) {
                        double n = tag_num.get(tag) + 1d;
                        tag_num.put(tag, n);
                    } else {
                        tag_num.put(tag, 1d);
                    }
                    bigramTransmissionPairMap.put(prev, tag_num);
                } else {
                    HashMap<String, Double> tag_num = new HashMap<String, Double>();
                    tag_num.put(tag, 1d);
                    bigramTransmissionPairMap.put(prev, tag_num);
                }
            }
            prev = tag;
        }
    }

    public void countTrigramTransmissionPair(String sentence){
        String[] words = sentence.split(Parse.boşluk_a);

        String first = "START";
        String second = "START";
        for (String s : words) {
            String[] word_tag_pair = s.split(Parse.tag_a);
            String tag = "";
            if (word_tag_pair.length < 2){
                tag = "Noun";
            } else {
                tag = word_tag_pair[1];
            }

            if (tagCountMap.containsKey(tag)) {
                double n = tagCountMap.get(tag) + 1d;
                tagCountMap.put(tag, n);
            } else {
                tagCountMap.put(tag, 1d);
            }

            if (first.equals("START")) {
                if (startCountMap.containsKey(tag)) {
                    double n = startCountMap.get(tag) + 1d;
                    startCountMap.put(tag, n);
                } else {
                    startCountMap.put(tag, 1d);
                }
            } else if (!second.equals("START")) {

                Bigram<String, String> bigram = new Bigram<>(first, second);
                if (bigramCountMap.containsKey(bigram)) {
                    double c = bigramCountMap.get(bigram) + 1d;
                    bigramCountMap.put(bigram, c);
                } else {
                    bigramCountMap.put(bigram, 1d);
                }

                Trigram trigram = new Trigram(first, second, tag);
                if (trigramCountMap.containsKey(trigram)) {
                    double nu = trigramCountMap.get(trigram) + 1d;
                    trigramCountMap.put(trigram, nu);
                } else {
                    trigramCountMap.put(trigram, 1d);
                }

                if (trigramTransmissionPairMap.containsKey(bigram)) {
                    HashMap<String, Double> tag_num = trigramTransmissionPairMap.get(bigram);
                    if (tag_num.containsKey(tag)) {
                        double n = tag_num.get(tag) + 1d;
                        tag_num.put(tag, n);
                    } else {
                        tag_num.put(tag, 1d);
                    }
                    trigramTransmissionPairMap.put(bigram, tag_num);
                } else {
                    HashMap<String, Double> tag_num = new HashMap<String, Double>();
                    tag_num.put(tag, 1d);
                    trigramTransmissionPairMap.put(bigram, tag_num);
                }
            }
            first = second;
            second = tag;
        }
    }

    public void countEmissionPair(String sentence) {

        String[] words = sentence.split(Parse.boşluk_a);

        for (String s : words) {
            String[] word_tag_pair = s.split(Parse.tag_a);
            String[] root_suffixes = word_tag_pair[0].split(Parse.ek_a);

            String tag = "";
            if (word_tag_pair.length < 2){
                tag = "Noun";
            } else {
                tag = word_tag_pair[1];
            }

            String suffix = root_suffixes[root_suffixes.length - 1];

            if (suffixCountMap.containsKey(suffix)) {

                double n = suffixCountMap.get(suffix) + 1d;
                suffixCountMap.put(suffix, n);
            } else {
                suffixCountMap.put(suffix, 1d);
            }

            if (emissionPairMap.containsKey(tag)) {
                HashMap<String, Double> tag_num = emissionPairMap.get(tag);
                if (tag_num.containsKey(suffix)) {
                    double n = tag_num.get(suffix) + 1d;
                    tag_num.put(suffix, n);
                } else {
                    tag_num.put(suffix, 1d);
                }
                emissionPairMap.put(tag, tag_num);
            } else {
                HashMap<String, Double> tag_num = new HashMap<String, Double>();
                tag_num.put(suffix, 1d);
                emissionPairMap.put(tag, tag_num);
            }
        }
    }

    public void calculateStartProbabilities() {

        double totalStartPoint = 0;
        Iterator it = startCountMap.values().iterator();
        while (it.hasNext()){
            totalStartPoint = totalStartPoint + (double) it.next();
        }

        for (String s : PartOfSpeech.tag_list){
            if (startCountMap.containsKey(s)){
                startProbabilitiesMap.put(s, ((double) startCountMap.get(s)/totalStartPoint));
            } else {
                startProbabilitiesMap.put(s, 0d);
            }
        }
    }

    public void calculateBigramTransmissionProbability() {

        for (String s : PartOfSpeech.tag_list){
            if (bigramTransmissionPairMap.containsKey(s)){
                HashMap<String, Double> transmitteds = bigramTransmissionPairMap.get(s);
                HashMap<String, Double> t_prob = new HashMap<String, Double>();
                for (String t : PartOfSpeech.tag_list){
                    if (transmitteds.containsKey(t)){
                        t_prob.put(t, (double)transmitteds.get(t)/ tagCountMap.get(s));
                    } else {
                        t_prob.put(t, 0d);
                    }
                    bigramTransmissionProbabilitiesMap.put(s, t_prob);
                }
            } else {
                HashMap<String, Double> t_prob = new HashMap<String, Double>();
                for (String t : PartOfSpeech.tag_list){
                    t_prob.put(t, 0d);
                }
                bigramTransmissionProbabilitiesMap.put(s, t_prob);
            }
        }
    }

    public void calculateTrigramTransmissionProbability(){
        for (String f : PartOfSpeech.tag_list){
            for (String se : PartOfSpeech.tag_list){
                Bigram<String, String> bigram = new Bigram<>(f, se);
                if (trigramTransmissionPairMap.containsKey(bigram)){
                    HashMap<String, Double> transmitteds = trigramTransmissionPairMap.get(bigram);
                    HashMap<String, Double> t_prob = new HashMap<String, Double>();
                    for (String t : PartOfSpeech.tag_list){
                        if (transmitteds.containsKey(t)){
                            t_prob.put(t, (double)transmitteds.get(t)/ bigramCountMap.get(bigram));
                        } else {
                            t_prob.put(t, 0d);
                        }
                        trigramTransmissionProbabilityMap.put(bigram, t_prob);
                    }
                } else {
                    HashMap<String, Double> t_prob = new HashMap<String, Double>();
                    for (String t : PartOfSpeech.tag_list){
                        t_prob.put(t, 0d);
                    }
                    trigramTransmissionProbabilityMap.put(bigram, t_prob);
                }
            }
        }
    }

    public void calculateEmissionProbabilities() {

        for (String s : PartOfSpeech.tag_list){
            if (emissionPairMap.containsKey(s)){
                HashMap<String, Double> emitteds = emissionPairMap.get(s);
                HashMap<String, Double> e_prob = new HashMap<String, Double>();
                Iterator it = suffixCountMap.keySet().iterator();
                while (it.hasNext()){
                    String obs = (String)it.next();
                    if (emitteds.containsKey(obs)){
                        e_prob.put(obs, (double)emitteds.get(obs)/ tagCountMap.get(s));
                    } else {
                        e_prob.put(obs, 0d);
                    }
                    emissionProbabilitiesMap.put(s, e_prob);
                }
            } else {
                HashMap<String, Double> e_prob = new HashMap<String, Double>();
                Iterator it = suffixCountMap.keySet().iterator();
                while (it.hasNext()){
                    String obs = (String)it.next();
                    e_prob.put(obs, 0d);
                    emissionProbabilitiesMap.put(s, e_prob);
                }
            }
        }
    }

    public void analyse(int ngram){

        switch (ngram){
            case 2:{
                for (String sentence : sentences){
                    countEmissionPair(sentence);
                    countBigramTransmissionPair(sentence);
                }
                calculateBigramTransmissionProbability();
                break;
            }
            case 3:{
                for (String sentence : sentences){
                    countEmissionPair(sentence);
                    countBigramTransmissionPair(sentence);
                    countTrigramTransmissionPair(sentence);
                }
                calculateBigramTransmissionProbability();
                calculateTrigramTransmissionProbability();
                break;
            }
        }

        calculateStartProbabilities();
        calculateEmissionProbabilities();

    }
}
