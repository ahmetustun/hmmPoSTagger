package core;

import Constant.Parse_point;

import java.util.HashMap;

/**
 * Created by ahmet on 21/01/16.
 */
public class Analyser {

    private String corpus;
    String[] words;

    private int[][] transmission_count;
    private float[][] transmission_prop;

    private int[][] emission_count;
    private float[][] emission_prop;

    private HashMap<String, Integer> POS_tag_map;
    private HashMap<String, Integer> suffix_map;

    private HashMap<String, Integer> start_count;
    private HashMap<String, Integer> POS_tag_count;
    private HashMap<String, Integer> obs_count;

    public Analyser(String corpus){

        start_count = new HashMap<String, Integer>();
        POS_tag_count = new HashMap<String, Integer>();
        obs_count = new HashMap<String, Integer>();

        this.corpus = corpus;
        words = corpus.split(Parse_point.boşluk_a);

        transmission_count = new int[13][13];
        transmission_prop = new float[13][13];

        for (int i=0; i<13; i++){
            for (int j=0; j<13; j++){
                transmission_count[i][j] = 0;
            }
        }

        for (int i=0; i<13; i++){
            for (int j=0; j<13; j++){
                transmission_prop[i][j] = 0;
            }
        }

        // POS_tag_set: START, ADJ, ADV, CONJ, DET, DUP, INTERJ, NOUN, NUM, POSTP, PRON, PUNC, VERB
        POS_tag_map = new HashMap<String, Integer>();
        POS_tag_map.put("START", 0);
        POS_tag_map.put("ADJ", 1);
        POS_tag_map.put("ADV", 2);
        POS_tag_map.put("CONJ", 3);
        POS_tag_map.put("DET", 4);
        POS_tag_map.put("DUP", 5);
        POS_tag_map.put("INTERJ", 6);
        POS_tag_map.put("NOUN", 7);
        POS_tag_map.put("NUM", 8);
        POS_tag_map.put("POSTP", 9);
        POS_tag_map.put("PRON", 10);
        POS_tag_map.put("PUNC", 11);
        POS_tag_map.put("VERB", 12);

        suffix_map = new HashMap<String, Integer>();

    }

    public void construct_transmission_count_matrix(){

        int prev = -1;
        int curr = -1;

        for (String s : words){

            String[] word_tag_pair = s.split(Parse_point.tag_a);
            curr = POS_tag_map.get(word_tag_pair[1]);

            if (prev != -1){
                transmission_count[prev][curr]++;
            }

            prev = curr;
        }
    }

    public void construct_transmission_prop_matrix(){

        int[] total_counts = {1,0,0,0,0,0,0,0,0,0,0,0,0};

        for (int i=0; i<13; i++){
            for (int j=0; j<13; j++){
                total_counts[i] = total_counts[i] + transmission_count[j][i];
            }
        }

        for (int i=0; i<13; i++){
            for (int j=0; j<13; j++){
                transmission_prop[i][j] = ((float)transmission_count[i][j]) / total_counts[i];
            }
        }
    }

    public HashMap<String, Integer> getStart_count() {
        return start_count;
    }

    public HashMap<String, Integer> getPOS_tag_count() {
        return POS_tag_count;

    }

    public HashMap<String, Integer> getObs_count() {
        return obs_count;
    }

    public HashMap<String, HashMap<String, Integer>> count_transmission_pair() {

        HashMap<String, HashMap<String, Integer>> ctag_ptag_num = new HashMap<String, HashMap<String, Integer>>();

        String prev = "START";
        for (String s : words){
            String[] word_tag_pair = s.split(Parse_point.tag_a);
            String tag = word_tag_pair[1];

            if (POS_tag_count.containsKey(tag)){

                int n = POS_tag_count.get(tag) + 1;
                POS_tag_count.put(tag, n);
            } else {
                POS_tag_count.put(tag, 1);
            }

            if (prev.equals("START")){
                if (start_count.containsKey(tag)){
                    int n = start_count.get(tag) + 1;
                    start_count.put(tag, n);
                } else {
                    start_count.put(tag, 1);
                }
            } else {
                if (ctag_ptag_num.containsKey(tag)){
                    HashMap<String, Integer> tag_num = ctag_ptag_num.get(tag);
                    if (tag_num.containsKey(prev)){
                        int n = tag_num.get(prev) + 1;
                        tag_num.put(prev, n);
                    } else {
                        tag_num.put(prev, 1);
                    }
                    ctag_ptag_num.put(tag, tag_num);
                } else {
                    HashMap<String, Integer> tag_num = new HashMap<String, Integer>();
                    tag_num.put(prev, 1);
                    ctag_ptag_num.put(tag, tag_num);
                }
            }
            prev = tag;
        }
        return ctag_ptag_num;
    }

    public HashMap<String, HashMap<String, Integer>> count_emission_pair() {

        HashMap<String, HashMap<String, Integer>> suffix_tag_num = new HashMap<String, HashMap<String, Integer>>();

        for (String s : words){
            String[] word_tag_pair = s.split(Parse_point.tag_a);
            String[] root_suffixes = word_tag_pair[0].split(Parse_point.ek_a);

            String tag = word_tag_pair[1];
            String suffix = root_suffixes[root_suffixes.length-1];

            if (obs_count.containsKey(suffix)){

                int n = obs_count.get(suffix) + 1;
                obs_count.put(suffix, n);
            } else {
                obs_count.put(suffix, 1);
            }

            if (suffix_tag_num.containsKey(suffix)){
                HashMap<String, Integer> tag_num = suffix_tag_num.get(suffix);
                if (tag_num.containsKey(tag)){
                    int n = tag_num.get(tag) + 1;
                    tag_num.put(tag, n);
                } else {
                    tag_num.put(tag, 1);
                }
                suffix_tag_num.put(suffix, tag_num);
            } else {
                HashMap<String, Integer> tag_num = new HashMap<String, Integer>();
                tag_num.put(tag, 1);
                suffix_tag_num.put(suffix, tag_num);
            }
        }
        return suffix_tag_num;
    }

    public void print_matrixes(){

        System.out.println("**************************");
        System.out.println("Transmission Counts: \n");

        for (int i=0; i<13; i++){
            for (int j=0; j<13; j++){
                System.out.print(transmission_count[j][i] + "\t");
            }
            System.out.print("\n");
        }

        System.out.println("");
        System.out.println("**************************");
        System.out.println("Transmission Propabilities: \n");

        for (int i=0; i<13; i++){
            for (int j=0; j<13; j++){
                System.out.print(transmission_prop[j][i] + "\t");
            }
            System.out.print("\n");
        }

    }

}
