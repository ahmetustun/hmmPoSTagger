package core;

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

    public Analyser(String corpus){

        this.corpus = corpus;
        words = corpus.split(Const.boşluk_a);

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

            String[] word_tag_pair = s.split(Const.tag_a);
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

    public void construct_emission_count_matrix(){

        int c = 0;
        for (String s : words){
            String[] word_tag_pair = s.split(Const.tag_a);
            suffix_map.put(word_tag_pair[word_tag_pair.length-1], c);
            c++;
        }

        emission_count = new int[12][suffix_map.size()];
        emission_prop = new float[12][suffix_map.size()];


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
