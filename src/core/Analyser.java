package core;

import Constant.POS_Tags;
import Constant.Parse_point;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Created by ahmet on 21/01/16.
 */
public class Analyser {

    private String corpus;
    String[] words;

    private HashMap<String, Integer> start_count;
    private HashMap<String, Integer> POS_tag_count;
    private HashMap<String, Integer> obs_count;
    private HashMap<String, HashMap<String, Integer>> ctag_ptag_num;
    private HashMap<String, HashMap<String, Integer>> suffix_tag_num;
    private HashMap<String, Float> start_probabilities;
    private HashMap<String, HashMap<String, Float>> transmission_probabilities;
    private HashMap<String, HashMap<String, Float>> emission_probabilities;

    public Analyser(String corpus) {

        start_count = new HashMap<String, Integer>();
        POS_tag_count = new HashMap<String, Integer>();
        obs_count = new HashMap<String, Integer>();
        ctag_ptag_num = new HashMap<String, HashMap<String, Integer>>();
        suffix_tag_num = new HashMap<String, HashMap<String, Integer>>();
        start_probabilities = new HashMap<String, Float>();
        transmission_probabilities = new HashMap<String, HashMap<String, Float>>();
        emission_probabilities = new HashMap<String, HashMap<String, Float>>();

        this.corpus = corpus;
        words = corpus.split(Parse_point.boşluk_a);

    }

    public HashMap<String, Integer> getStart_count() {
        return start_count;
    }

    public HashMap<String, Integer> getPOS_tag_count() {
        return POS_tag_count;
    }

    public HashMap<String, HashMap<String, Integer>> getCtag_ptag_num() {
        return ctag_ptag_num;
    }

    public HashMap<String, HashMap<String, Integer>> getSuffix_tag_num() {
        return suffix_tag_num;
    }

    public HashMap<String, Float> getStart_probabilities() {
        return start_probabilities;
    }

    public HashMap<String, HashMap<String, Float>> getTransmission_probabilities() {
        return transmission_probabilities;
    }

    public HashMap<String, HashMap<String, Float>> getEmission_probabilities() {
        return emission_probabilities;
    }

    public HashMap<String, Integer> getObs_count() {
        return obs_count;
    }

    public void count_transmission_pair() {

        String prev = "START";
        for (String s : words) {
            String[] word_tag_pair = s.split(Parse_point.tag_a);
            String tag = word_tag_pair[1];

            if (POS_tag_count.containsKey(tag)) {

                int n = POS_tag_count.get(tag) + 1;
                POS_tag_count.put(tag, n);
            } else {
                POS_tag_count.put(tag, 1);
            }

            if (prev.equals("START")) {
                if (start_count.containsKey(tag)) {
                    int n = start_count.get(tag) + 1;
                    start_count.put(tag, n);
                } else {
                    start_count.put(tag, 1);
                }
            } else {
                if (ctag_ptag_num.containsKey(prev)) {
                    HashMap<String, Integer> tag_num = ctag_ptag_num.get(prev);
                    if (tag_num.containsKey(tag)) {
                        int n = tag_num.get(tag) + 1;
                        tag_num.put(tag, n);
                    } else {
                        tag_num.put(tag, 1);
                    }
                    ctag_ptag_num.put(prev, tag_num);
                } else {
                    HashMap<String, Integer> tag_num = new HashMap<String, Integer>();
                    tag_num.put(tag, 1);
                    ctag_ptag_num.put(prev, tag_num);
                }
            }
            prev = tag;
        }
    }

    public void count_emission_pair() {

        for (String s : words) {
            String[] word_tag_pair = s.split(Parse_point.tag_a);
            String[] root_suffixes = word_tag_pair[0].split(Parse_point.ek_a);

            String tag = word_tag_pair[1];
            String suffix = root_suffixes[root_suffixes.length - 1];

            if (obs_count.containsKey(suffix)) {

                int n = obs_count.get(suffix) + 1;
                obs_count.put(suffix, n);
            } else {
                obs_count.put(suffix, 1);
            }

            if (suffix_tag_num.containsKey(tag)) {
                HashMap<String, Integer> tag_num = suffix_tag_num.get(tag);
                if (tag_num.containsKey(suffix)) {
                    int n = tag_num.get(suffix) + 1;
                    tag_num.put(suffix, n);
                } else {
                    tag_num.put(suffix, 1);
                }
                suffix_tag_num.put(tag, tag_num);
            } else {
                HashMap<String, Integer> tag_num = new HashMap<String, Integer>();
                tag_num.put(suffix, 1);
                suffix_tag_num.put(tag, tag_num);
            }
        }
    }

    public void calculateStartProbabilities() {

        int totalStartPoint = 0;
        Iterator it = start_count.values().iterator();
        while (it.hasNext()){
            totalStartPoint = totalStartPoint + (Integer) it.next();
        }

        for (String s : POS_Tags.tag_list){
            if (start_count.containsKey(s)){
                start_probabilities.put(s, ((float)start_count.get(s)/totalStartPoint));
            } else {
                start_probabilities.put(s, 0f);
            }
        }
    }

    public void calculate_transmission_probability() {

        for (String s : POS_Tags.tag_list){
            if (ctag_ptag_num.containsKey(s)){
                HashMap<String, Integer> transmitteds = ctag_ptag_num.get(s);
                HashMap<String, Float> t_prob = new HashMap<String, Float>();
                for (String t : POS_Tags.tag_list){
                    if (transmitteds.containsKey(t)){
                        t_prob.put(t, (float)transmitteds.get(t)/POS_tag_count.get(s));
                    } else {
                        t_prob.put(t, 0f);
                    }
                    transmission_probabilities.put(s, t_prob);
                }
            } else {
                HashMap<String, Float> t_prob = new HashMap<String, Float>();
                for (String t : POS_Tags.tag_list){
                    t_prob.put(t, 0f);
                }
                transmission_probabilities.put(s, t_prob);
            }
        }
    }

    public void calculate_emission_probabilities() {

        for (String s : POS_Tags.tag_list){
            if (suffix_tag_num.containsKey(s)){
                HashMap<String, Integer> emitteds = suffix_tag_num.get(s);
                HashMap<String, Float> t_prob = new HashMap<String, Float>();
                Iterator it = obs_count.keySet().iterator();
                while (it.hasNext()){
                    String obs = (String)it.next();
                    if (emitteds.containsKey(obs)){
                        t_prob.put(obs, (float)emitteds.get(obs)/POS_tag_count.get(s));
                    } else {
                        t_prob.put(obs, 0f);
                    }
                    emission_probabilities.put(s, t_prob);
                }
            } else {
                HashMap<String, Float> t_prob = new HashMap<String, Float>();
                Iterator it = obs_count.keySet().iterator();
                while (it.hasNext()){
                    String obs = (String)it.next();
                    t_prob.put(obs, 0f);
                    emission_probabilities.put(s, t_prob);
                }
            }
        }
    }
}
