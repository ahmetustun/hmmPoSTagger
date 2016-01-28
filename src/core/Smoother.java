package core;

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
    private HashMap<String, Integer> uns_startCountMap;
    private HashMap<String, Integer> uns_tagCountMap;
    private HashMap<String, Integer> uns_suffixCountMap;

    private HashMap<String, HashMap<String, Integer>> uns_transmissionPairMap;
    private HashMap<String, HashMap<String, Integer>> uns_emissionPairMap;

    private HashMap<String, Integer> s_suffixCountMap = new HashMap<>();
    private HashMap<String, HashMap<String, Integer>> s_emissionPairMap = new HashMap<>();
    private HashMap<String, HashMap<String, Float>> s_emissionProbabilitiesMap = new HashMap<>();

    private ArrayList<String> unseenSuffixList = new ArrayList<>();

    public Smoother(HashMap<String, Integer> uns_startCountMap, HashMap<String, Integer> uns_tagCountMap, HashMap<String, Integer> uns_suffixCountMap,
                    HashMap<String, HashMap<String, Integer>> uns_transmissionPairMap, HashMap<String, HashMap<String, Integer>> uns_emissionPairMap){

        this.uns_emissionPairMap = uns_emissionPairMap;
        this.uns_transmissionPairMap = uns_transmissionPairMap;
        this.uns_startCountMap = uns_startCountMap;
        this.uns_tagCountMap = uns_tagCountMap;
        this.uns_suffixCountMap = uns_suffixCountMap;

    }

    public Smoother(String fileName, HashMap<String, Integer> uns_startCountMap, HashMap<String, Integer> uns_tagCountMap, HashMap<String, Integer> uns_suffixCountMap,
                    HashMap<String, HashMap<String, Integer>> uns_transmissionPairMap, HashMap<String, HashMap<String, Integer>> uns_emissionPairMap){

        this.uns_emissionPairMap = uns_emissionPairMap;
        this.uns_transmissionPairMap = uns_transmissionPairMap;
        this.uns_startCountMap = uns_startCountMap;
        this.uns_tagCountMap = uns_tagCountMap;
        this.uns_suffixCountMap = uns_suffixCountMap;

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

    public void addOneForEmission(){

        s_suffixCountMap = uns_suffixCountMap;
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

    public void addOne(){
        countUnseenSuffixesForTestCorpus();
        addOneForEmission();
        calculateEmissionProbabilities();
    }

}
