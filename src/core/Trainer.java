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

    private HashMap<String, Float> startCountMap = new HashMap<>();
    private HashMap<String, Float> tagCountMap = new HashMap<>();
    private HashMap<Bigram<String, String>, Float> bigramCountMap = new HashMap<>();
    private HashMap<Trigram<String, String, String>, Float> trigramCountMap = new HashMap<>();

    private HashMap<String, Float> suffixCountMap = new HashMap<>();

    private HashMap<String, HashMap<String, Float>> bigramTransmissionPairMap = new HashMap<>();
    private HashMap<String, HashMap<String, Float>> emissionPairMap = new HashMap<>();

    private HashMap<String, Float> startProbabilitiesMap = new HashMap<>();
    private HashMap<String, HashMap<String, Float>> bigramTransmissionProbabilitiesMap = new HashMap<>();
    private HashMap<String, HashMap<String, Float>> emissionProbabilitiesMap = new HashMap<>();

    private HashMap<Bigram<String, String>, HashMap<String, Float>> trigramTransmissionPairMap = new HashMap<>();
    private HashMap<Bigram<String, String>, HashMap<String, Float>> trigramTransmissionProbabilityMap = new HashMap<>();

    public Trainer(String fileName) {
        for (String s : PartOfSpeech.tag_list){
            tagCountMap.put(s, 0f);
            for (String k : PartOfSpeech.tag_list){
                Bigram<String, String> bigram = new Bigram<>(s, k);
                bigramCountMap.put(bigram, 0f);
                for (String m : PartOfSpeech.tag_list){
                    Trigram<String, String, String> trigram = new Trigram<>(s, k, m);
                    trigramCountMap.put(trigram, 0f);
                }
            }
        }
        Parse.parseTrainFile(fileName, sentences);
    }

    public Trainer() {
        for (String s : PartOfSpeech.tag_list){
            tagCountMap.put(s, 0f);
            for (String k : PartOfSpeech.tag_list){
                Bigram<String, String> bigram = new Bigram<>(s, k);
                bigramCountMap.put(bigram, 0f);
                for (String m : PartOfSpeech.tag_list){
                    Trigram<String, String, String> trigram = new Trigram<>(s, k, m);
                    trigramCountMap.put(trigram, 0f);
                }
            }
        }
    }

    public HashMap<String, Float> getStartCountMap() {
        return startCountMap;
    }

    public HashMap<String, Float> getTagCountMap() {
        return tagCountMap;
    }

    public HashMap<Bigram<String, String>, Float> getBigramCountMap() {
        return bigramCountMap;
    }

    public HashMap<Trigram<String, String, String>, Float> getTrigramCountMap() {
        return trigramCountMap;
    }

    public HashMap<String, HashMap<String, Float>> getBigramTransmissionPairMap() {

        return bigramTransmissionPairMap;
    }

    public HashMap<String, HashMap<String, Float>> getEmissionPairMap() {
        return emissionPairMap;
    }

    public HashMap<String, Float> getStartProbabilitiesMap() {
        return startProbabilitiesMap;
    }

    public HashMap<String, HashMap<String, Float>> getBigramTransmissionProbabilitiesMap() {
        return bigramTransmissionProbabilitiesMap;
    }

    public HashMap<String, HashMap<String, Float>> getEmissionProbabilitiesMap() {
        return emissionProbabilitiesMap;
    }

    public HashMap<String, Float> getSuffixCountMap() {
        return suffixCountMap;
    }

    public HashMap<Bigram<String, String>, HashMap<String, Float>> getTrigramTransmissionPairMap() {
        return trigramTransmissionPairMap;
    }

    public HashMap<Bigram<String, String>, HashMap<String, Float>> getTrigramTransmissionProbabilityMap() {
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

                float n = tagCountMap.get(tag) + 1f;
                tagCountMap.put(tag, n);
            } else {
                tagCountMap.put(tag, 1f);
            }

            if (prev.equals("START")) {
                if (startCountMap.containsKey(tag)) {
                    float n = startCountMap.get(tag) + 1f;
                    startCountMap.put(tag, n);
                } else {
                    startCountMap.put(tag, 1f);
                }
            } else {
                if (bigramTransmissionPairMap.containsKey(prev)) {
                    HashMap<String, Float> tag_num = bigramTransmissionPairMap.get(prev);
                    if (tag_num.containsKey(tag)) {
                        float n = tag_num.get(tag) + 1f;
                        tag_num.put(tag, n);
                    } else {
                        tag_num.put(tag, 1f);
                    }
                    bigramTransmissionPairMap.put(prev, tag_num);
                } else {
                    HashMap<String, Float> tag_num = new HashMap<String, Float>();
                    tag_num.put(tag, 1f);
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
                float n = tagCountMap.get(tag) + 1f;
                tagCountMap.put(tag, n);
            } else {
                tagCountMap.put(tag, 1f);
            }

            if (first.equals("START")) {
                if (startCountMap.containsKey(tag)) {
                    float n = startCountMap.get(tag) + 1f;
                    startCountMap.put(tag, n);
                } else {
                    startCountMap.put(tag, 1f);
                }
            } else if (!second.equals("START")) {

                Bigram<String, String> bigram = new Bigram<>(first, second);
                if (bigramCountMap.containsKey(bigram)) {
                    float c = bigramCountMap.get(bigram) + 1f;
                    bigramCountMap.put(bigram, c);
                } else {
                    bigramCountMap.put(bigram, 1f);
                }

                Trigram trigram = new Trigram(first, second, tag);
                if (trigramCountMap.containsKey(trigram)) {
                    float nu = trigramCountMap.get(trigram) + 1f;
                    trigramCountMap.put(trigram, nu);
                } else {
                    trigramCountMap.put(trigram, 1f);
                }

                if (trigramTransmissionPairMap.containsKey(bigram)) {
                    HashMap<String, Float> tag_num = trigramTransmissionPairMap.get(bigram);
                    if (tag_num.containsKey(tag)) {
                        float n = tag_num.get(tag) + 1f;
                        tag_num.put(tag, n);
                    } else {
                        tag_num.put(tag, 1f);
                    }
                    trigramTransmissionPairMap.put(bigram, tag_num);
                } else {
                    HashMap<String, Float> tag_num = new HashMap<String, Float>();
                    tag_num.put(tag, 1f);
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

                float n = suffixCountMap.get(suffix) + 1f;
                suffixCountMap.put(suffix, n);
            } else {
                suffixCountMap.put(suffix, 1f);
            }

            if (emissionPairMap.containsKey(tag)) {
                HashMap<String, Float> tag_num = emissionPairMap.get(tag);
                if (tag_num.containsKey(suffix)) {
                    float n = tag_num.get(suffix) + 1f;
                    tag_num.put(suffix, n);
                } else {
                    tag_num.put(suffix, 1f);
                }
                emissionPairMap.put(tag, tag_num);
            } else {
                HashMap<String, Float> tag_num = new HashMap<String, Float>();
                tag_num.put(suffix, 1f);
                emissionPairMap.put(tag, tag_num);
            }
        }
    }

    public void calculateStartProbabilities() {

        float totalStartPoint = 0;
        Iterator it = startCountMap.values().iterator();
        while (it.hasNext()){
            totalStartPoint = totalStartPoint + (Float) it.next();
        }

        for (String s : PartOfSpeech.tag_list){
            if (startCountMap.containsKey(s)){
                startProbabilitiesMap.put(s, ((float) startCountMap.get(s)/totalStartPoint));
            } else {
                startProbabilitiesMap.put(s, 0f);
            }
        }
    }

    public void calculateBigramTransmissionProbability() {

        for (String s : PartOfSpeech.tag_list){
            if (bigramTransmissionPairMap.containsKey(s)){
                HashMap<String, Float> transmitteds = bigramTransmissionPairMap.get(s);
                HashMap<String, Float> t_prob = new HashMap<String, Float>();
                for (String t : PartOfSpeech.tag_list){
                    if (transmitteds.containsKey(t)){
                        t_prob.put(t, (float)transmitteds.get(t)/ tagCountMap.get(s));
                    } else {
                        t_prob.put(t, 0f);
                    }
                    bigramTransmissionProbabilitiesMap.put(s, t_prob);
                }
            } else {
                HashMap<String, Float> t_prob = new HashMap<String, Float>();
                for (String t : PartOfSpeech.tag_list){
                    t_prob.put(t, 0f);
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
                    HashMap<String, Float> transmitteds = trigramTransmissionPairMap.get(bigram);
                    HashMap<String, Float> t_prob = new HashMap<String, Float>();
                    for (String t : PartOfSpeech.tag_list){
                        if (transmitteds.containsKey(t)){
                            t_prob.put(t, (float)transmitteds.get(t)/ bigramCountMap.get(bigram));
                        } else {
                            t_prob.put(t, 0f);
                        }
                        trigramTransmissionProbabilityMap.put(bigram, t_prob);
                    }
                } else {
                    HashMap<String, Float> t_prob = new HashMap<String, Float>();
                    for (String t : PartOfSpeech.tag_list){
                        t_prob.put(t, 0f);
                    }
                    trigramTransmissionProbabilityMap.put(bigram, t_prob);
                }
            }
        }
    }

    public void calculateEmissionProbabilities() {

        for (String s : PartOfSpeech.tag_list){
            if (emissionPairMap.containsKey(s)){
                HashMap<String, Float> emitteds = emissionPairMap.get(s);
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
