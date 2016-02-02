package core;

import utils.LastTwo;
import utils.Parse;
import utils.PartOfSpeech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by ahmetu on 27.01.2016.
 */
public class Smoother {

    //unt : untagged
    private ArrayList<String> unt_sentences = new ArrayList<>();
    private ArrayList<ArrayList<String>> unTaggedSuffixesList = new ArrayList<>();

    // uns : unsmoothed
    private HashMap<String, Integer> uns_tagCountMap = new HashMap<>();
    private HashMap<LastTwo<String, String>, Integer> uns_bigramCountMap = new HashMap<>();
    private HashMap<String, Integer> uns_suffixCountMap = new HashMap<>();

    private HashMap<String, HashMap<String, Integer>> uns_emissionPairMap = new HashMap<>();

    private HashMap<String, Integer> s_suffixCountMap = new HashMap<>();
    private HashMap<String, HashMap<String, Integer>> s_emissionPairMap = new HashMap<>();
    private HashMap<String, HashMap<String, Float>> s_emissionProbabilitiesMap = new HashMap<>();

    private HashMap<LastTwo<String, String>, HashMap<String, Integer>> uns_trigramTransmissionPairMap = new HashMap<>();

    private HashMap<LastTwo<String, String>, HashMap<String, Integer>> s_trigramTransmissionPairMap = new HashMap<>();
    private HashMap<LastTwo<String, String>, HashMap<String, Float>> s_trigramTransmissionProbabilityMap = new HashMap<>();

    private ArrayList<String> unseenSuffixList = new ArrayList<>();

    public Smoother(HashMap<String, Integer> uns_tagCountMap, HashMap<String, Integer> uns_suffixCountMap,
                    HashMap<String, HashMap<String, Integer>> uns_emissionPairMap){

        this.uns_emissionPairMap = (HashMap<String, HashMap<String, Integer>>) uns_emissionPairMap.clone();
        this.uns_tagCountMap = (HashMap<String, Integer>) uns_tagCountMap.clone();
        this.uns_suffixCountMap = (HashMap<String, Integer>) uns_suffixCountMap.clone();

    }

    public Smoother(String fileName, HashMap<String, Integer> uns_tagCountMap, HashMap<String, Integer> uns_suffixCountMap,
                    HashMap<String, HashMap<String, Integer>> uns_emissionPairMap){

        this.uns_emissionPairMap = (HashMap<String, HashMap<String, Integer>>) uns_emissionPairMap.clone();
        this.uns_tagCountMap = (HashMap<String, Integer>) uns_tagCountMap.clone();
        this.uns_suffixCountMap = (HashMap<String, Integer>) uns_suffixCountMap.clone();

        Parse.parseTrainFile(fileName, unt_sentences);
    }

    public Smoother(HashMap<String, Integer> uns_tagCountMap, HashMap<LastTwo<String, String>, Integer> uns_bigramCountMap,
                    HashMap<String, Integer> uns_suffixCountMap, HashMap<LastTwo<String, String>, HashMap<String, Integer>> uns_trigramTransmissionPairMap,
                    HashMap<String, HashMap<String, Integer>> uns_emissionPairMap){

        this.uns_trigramTransmissionPairMap = (HashMap<LastTwo<String, String>, HashMap<String, Integer>>) uns_trigramTransmissionPairMap.clone();
        this.uns_emissionPairMap = (HashMap<String, HashMap<String, Integer>>) uns_emissionPairMap.clone();
        this.uns_tagCountMap = (HashMap<String, Integer>) uns_tagCountMap.clone();
        this.uns_bigramCountMap = (HashMap<LastTwo<String, String>, Integer>) uns_bigramCountMap.clone();
        this.uns_suffixCountMap = (HashMap<String, Integer>) uns_suffixCountMap.clone();

    }

    public Smoother(String fileName, HashMap<String, Integer> uns_tagCountMap, HashMap<LastTwo<String, String>, Integer> uns_bigramCountMap,
                    HashMap<String, Integer> uns_suffixCountMap, HashMap<LastTwo<String, String>, HashMap<String, Integer>> uns_trigramTransmissionPairMap,
                    HashMap<String, HashMap<String, Integer>> uns_emissionPairMap){

        this.uns_trigramTransmissionPairMap = (HashMap<LastTwo<String, String>, HashMap<String, Integer>>) uns_trigramTransmissionPairMap.clone();
        this.uns_emissionPairMap = (HashMap<String, HashMap<String, Integer>>) uns_emissionPairMap.clone();
        this.uns_tagCountMap = (HashMap<String, Integer>) uns_tagCountMap.clone();
        this.uns_bigramCountMap = (HashMap<LastTwo<String, String>, Integer>) uns_bigramCountMap.clone();
        this.uns_suffixCountMap = (HashMap<String, Integer>) uns_suffixCountMap.clone();

        Parse.parseTrainFile(fileName, unt_sentences);
    }

    public void countUnseenSuffixes(String unt_sentence) {

        String[] words = unt_sentence.split(Parse.boşluk_a);
        ArrayList<String> sList = new ArrayList<>();

        for (String s : words) {

            String[] root_suffixes = s.split(Parse.ek_a);

            String suffix = root_suffixes[root_suffixes.length - 1];

            sList.add(suffix);

            if (!uns_suffixCountMap.containsKey(suffix)) {
                unseenSuffixList.add(suffix);
            }
        }
        unTaggedSuffixesList.add(sList);
    }

    public void countUnseenSuffixesForTestCorpus(){
        for (String sentence : unt_sentences){
            countUnseenSuffixes(sentence);
        }
    }

    public ArrayList<ArrayList<String>> getUnTaggedSuffixesList() {
        return unTaggedSuffixesList;
    }

    public ArrayList<String> getUnseenSuffixList() {
        return unseenSuffixList;
    }

    public HashMap<String, HashMap<String, Float>> getS_emissionProbabilitiesMap() {
        return s_emissionProbabilitiesMap;
    }

    public HashMap<String, HashMap<String, Integer>> getS_emissionPairMap() {
        return s_emissionPairMap;
    }

    public HashMap<String, Integer> getS_suffixCountMap() {
        return s_suffixCountMap;
    }

    public void addOneToEmissionPair(){

        s_suffixCountMap = (HashMap<String, Integer>) uns_suffixCountMap.clone();
        for (String unseenSuffix : unseenSuffixList){
            s_suffixCountMap.put(unseenSuffix, 0);
        }

        for (String s : PartOfSpeech.tag_list){
            if (uns_emissionPairMap.containsKey(s)){
                HashMap<String, Integer> emitteds = uns_emissionPairMap.get(s);
                HashMap<String, Integer> t_prob = new HashMap<String, Integer>();
                Iterator it = s_suffixCountMap.keySet().iterator();
                while (it.hasNext()){
                    String obs = (String)it.next();
                    if (emitteds.containsKey(obs)){
                        t_prob.put(obs, emitteds.get(obs) + 1);
                    } else {
                        t_prob.put(obs, 1);
                    }
                    s_emissionPairMap.put(s, t_prob);
                }
            } else {
                HashMap<String, Integer> t_prob = new HashMap<String, Integer>();
                Iterator it = s_suffixCountMap.keySet().iterator();
                while (it.hasNext()){
                    String obs = (String)it.next();
                    t_prob.put(obs, 1);
                    s_emissionPairMap.put(s, t_prob);
                }
            }

        }
    }

    public void calculateEmissionProbabilities(){

        int suffixCount = s_suffixCountMap.size();

        for (String tag : PartOfSpeech.tag_list){
            HashMap<String, Integer> emitteds = s_emissionPairMap.get(tag);
            HashMap<String, Float> e_prob = new HashMap<String, Float>();
            Iterator s_it = s_suffixCountMap.keySet().iterator();
            while (s_it.hasNext()){
                String suffix = (String)s_it.next();
                e_prob.put(suffix, (float) emitteds.get(suffix)/(uns_tagCountMap.get(tag) + suffixCount));
                s_emissionProbabilitiesMap.put(tag, e_prob);
            }
        }
    }

    public void addOneForEmission(){
        countUnseenSuffixesForTestCorpus();
        addOneToEmissionPair();
        calculateEmissionProbabilities();
    }

    public void addOneForTrigramTransmission(){

        for (String first : PartOfSpeech.tag_list){
            for (String second : PartOfSpeech.tag_list){
                LastTwo<String, String> lastTwo = new LastTwo<>(first, second);
                HashMap<String, Integer> t_count = new HashMap<>();
                if (uns_trigramTransmissionPairMap.containsKey(lastTwo)){
                    t_count = uns_trigramTransmissionPairMap.get(lastTwo);
                }
                int denominator = uns_bigramCountMap.get(lastTwo) + 12;
                HashMap<String, Float> t_prob = new HashMap<>();
                for (String tag : PartOfSpeech.tag_list){
                    float numerator = 0f;
                    if (t_count.containsKey(tag)){
                        numerator = (float)(t_count.get(tag) + 1);
                    } else {
                        numerator = 1;
                    }
                    t_prob.put(tag, numerator/denominator);
                }
                s_trigramTransmissionProbabilityMap.put(lastTwo, t_prob);
            }
        }
    }

    public void addOne(int ngram){
        switch (ngram){
            case 2: {
                addOneForEmission();
                break;
            }
            case 3:{
                addOneForEmission();
                addOneForTrigramTransmission();
                break;
            }
        }
    }
}
