package core;

import utils.Bigram;
import utils.Parse;
import utils.PartOfSpeech;
import utils.Trigram;

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
    private HashMap<Bigram<String, String>, Integer> uns_bigramCountMap = new HashMap<>();
    private HashMap<String, Float> uns_tagProbabilitiesMap = new HashMap<>();
    private HashMap<Trigram, Integer> uns_trigramCountMap = new HashMap<>();
    private HashMap<String, Integer> uns_suffixCountMap = new HashMap<>();

    private HashMap<String, HashMap<String, Integer>> uns_bigramTransmissionPairMap = new HashMap<>();
    private HashMap<String, HashMap<String, Integer>> uns_emissionPairMap = new HashMap<>();
    private HashMap<String, HashMap<String, Float>> uns_emissionProbabilitiesMap = new HashMap<>();

    private HashMap<String, Integer> laplace_suffixCountMap = new HashMap<>();
    private HashMap<String, HashMap<String, Integer>> laplace_emissionPairMap = new HashMap<>();
    private HashMap<String, HashMap<String, Float>> laplace_emissionProbabilitiesMap = new HashMap<>();

    private HashMap<Bigram<String, String>, HashMap<String, Integer>> uns_trigramTransmissionPairMap = new HashMap<>();

    private HashMap<Bigram<String, String>, HashMap<String, Integer>> laplace_trigramTransmissionPairMap = new HashMap<>();
    private HashMap<Bigram<String, String>, HashMap<String, Float>> laplace_trigramTransmissionProbabilityMap = new HashMap<>();

    private ArrayList<String> unseenSuffixList = new ArrayList<>();

    private HashMap<String, HashMap<String, Float>>  kneserNey_bigramTransmissionProbabilityMap = new HashMap<>();
    private HashMap<Bigram<String, String>, HashMap<String, Float>> kneserNey_trigramTransmissionProbabilityMap = new HashMap<>();
    private HashMap<String, HashMap<String, Float>> kneserNey_emissionProbabilitiesMap = new HashMap<>();
    float kneserNey_D_bigram = 0f;
    float kneserNey_D_trigram = 0f;

    public Smoother(HashMap<String, Integer> uns_tagCountMap, HashMap<String, Integer> uns_suffixCountMap, HashMap<String, HashMap<String, Integer>> uns_bigramTransmissionPairMap,
                    HashMap<String, HashMap<String, Float>> emissionProbabilitiesMap, HashMap<String, HashMap<String, Integer>> uns_emissionPairMap){

        this.uns_emissionPairMap = (HashMap<String, HashMap<String, Integer>>) uns_emissionPairMap.clone();
        this.uns_tagCountMap = (HashMap<String, Integer>) uns_tagCountMap.clone();
        this.uns_suffixCountMap = (HashMap<String, Integer>) uns_suffixCountMap.clone();
        this.uns_bigramTransmissionPairMap = (HashMap<String, HashMap<String, Integer>>) uns_bigramTransmissionPairMap.clone();
        this.uns_emissionProbabilitiesMap = (HashMap<String, HashMap<String, Float>>) uns_emissionProbabilitiesMap.clone();

    }

    public Smoother(String fileName, HashMap<String, Integer> uns_tagCountMap, HashMap<String, Integer> uns_suffixCountMap, HashMap<String, HashMap<String, Integer>> uns_bigramTransmissionPairMap,
                    HashMap<String, HashMap<String, Float>> emissionProbabilitiesMap, HashMap<String, HashMap<String, Integer>> uns_emissionPairMap){

        this.uns_emissionPairMap = (HashMap<String, HashMap<String, Integer>>) uns_emissionPairMap.clone();
        this.uns_tagCountMap = (HashMap<String, Integer>) uns_tagCountMap.clone();
        this.uns_suffixCountMap = (HashMap<String, Integer>) uns_suffixCountMap.clone();
        this.uns_bigramTransmissionPairMap = (HashMap<String, HashMap<String, Integer>>) uns_bigramTransmissionPairMap.clone();
        this.uns_emissionProbabilitiesMap = (HashMap<String, HashMap<String, Float>>) uns_emissionProbabilitiesMap.clone();

        Parse.parseTrainFile(fileName, unt_sentences);
    }

    public Smoother(HashMap<String, Integer> uns_tagCountMap, HashMap<Bigram<String, String>, Integer> uns_bigramCountMap, HashMap<String, HashMap<String, Integer>> uns_bigramTransmissionPairMap,
                    HashMap<String, Integer> uns_suffixCountMap, HashMap<Bigram<String, String>, HashMap<String, Integer>> uns_trigramTransmissionPairMap, HashMap<Trigram, Integer> uns_trigramCountMap,
                    HashMap<String, HashMap<String, Float>> emissionProbabilitiesMap, HashMap<String, HashMap<String, Integer>> uns_emissionPairMap){

        this.uns_trigramTransmissionPairMap = (HashMap<Bigram<String, String>, HashMap<String, Integer>>) uns_trigramTransmissionPairMap.clone();
        this.uns_emissionPairMap = (HashMap<String, HashMap<String, Integer>>) uns_emissionPairMap.clone();
        this.uns_tagCountMap = (HashMap<String, Integer>) uns_tagCountMap.clone();
        this.uns_bigramCountMap = (HashMap<Bigram<String, String>, Integer>) uns_bigramCountMap.clone();
        this.uns_suffixCountMap = (HashMap<String, Integer>) uns_suffixCountMap.clone();
        this.uns_bigramTransmissionPairMap = (HashMap<String, HashMap<String, Integer>>) uns_bigramTransmissionPairMap.clone();
        this.uns_trigramCountMap = (HashMap<Trigram, Integer>) uns_trigramCountMap.clone();
        this.uns_emissionProbabilitiesMap = (HashMap<String, HashMap<String, Float>>) uns_emissionProbabilitiesMap.clone();

    }

    public Smoother(String fileName, HashMap<String, Integer> uns_tagCountMap, HashMap<Bigram<String, String>, Integer> uns_bigramCountMap, HashMap<String, HashMap<String, Integer>> uns_bigramTransmissionPairMap,
                    HashMap<String, Integer> uns_suffixCountMap, HashMap<Bigram<String, String>, HashMap<String, Integer>> uns_trigramTransmissionPairMap, HashMap<Trigram, Integer> uns_trigramCountMap,
                    HashMap<String, HashMap<String, Float>> emissionProbabilitiesMap, HashMap<String, HashMap<String, Integer>> uns_emissionPairMap){

        this.uns_trigramTransmissionPairMap = (HashMap<Bigram<String, String>, HashMap<String, Integer>>) uns_trigramTransmissionPairMap.clone();
        this.uns_emissionPairMap = (HashMap<String, HashMap<String, Integer>>) uns_emissionPairMap.clone();
        this.uns_tagCountMap = (HashMap<String, Integer>) uns_tagCountMap.clone();
        this.uns_bigramCountMap = (HashMap<Bigram<String, String>, Integer>) uns_bigramCountMap.clone();
        this.uns_suffixCountMap = (HashMap<String, Integer>) uns_suffixCountMap.clone();
        this.uns_bigramTransmissionPairMap = (HashMap<String, HashMap<String, Integer>>) uns_bigramTransmissionPairMap.clone();
        this.uns_trigramCountMap = (HashMap<Trigram, Integer>) uns_trigramCountMap.clone();
        this.uns_emissionProbabilitiesMap = (HashMap<String, HashMap<String, Float>>) uns_emissionProbabilitiesMap.clone();

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

    public HashMap<Bigram<String, String>, HashMap<String, Float>> getLaplace_trigramTransmissionProbabilityMap() {
        return laplace_trigramTransmissionProbabilityMap;
    }

    public ArrayList<ArrayList<String>> getUnTaggedSuffixesList() {
        return unTaggedSuffixesList;
    }

    public ArrayList<String> getUnseenSuffixList() {
        return unseenSuffixList;
    }

    public HashMap<String, HashMap<String, Float>> getLaplace_emissionProbabilitiesMap() {
        return laplace_emissionProbabilitiesMap;
    }

    public HashMap<String, HashMap<String, Integer>> getLaplace_emissionPairMap() {
        return laplace_emissionPairMap;
    }

    public HashMap<String, Integer> getLaplace_suffixCountMap() {
        return laplace_suffixCountMap;
    }

    public void addOneToEmissionPair(){

        laplace_suffixCountMap = (HashMap<String, Integer>) uns_suffixCountMap.clone();
        for (String unseenSuffix : unseenSuffixList){
            laplace_suffixCountMap.put(unseenSuffix, 0);
        }

        for (String s : PartOfSpeech.tag_list){
            if (uns_emissionPairMap.containsKey(s)){
                HashMap<String, Integer> emitteds = uns_emissionPairMap.get(s);
                HashMap<String, Integer> t_prob = new HashMap<String, Integer>();
                Iterator it = laplace_suffixCountMap.keySet().iterator();
                while (it.hasNext()){
                    String obs = (String)it.next();
                    if (emitteds.containsKey(obs)){
                        t_prob.put(obs, emitteds.get(obs) + 1);
                    } else {
                        t_prob.put(obs, 1);
                    }
                    laplace_emissionPairMap.put(s, t_prob);
                }
            } else {
                HashMap<String, Integer> t_prob = new HashMap<String, Integer>();
                Iterator it = laplace_suffixCountMap.keySet().iterator();
                while (it.hasNext()){
                    String obs = (String)it.next();
                    t_prob.put(obs, 1);
                    laplace_emissionPairMap.put(s, t_prob);
                }
            }

        }
    }

    public void calculateEmissionProbabilities(){

        int suffixCount = laplace_suffixCountMap.size();

        for (String tag : PartOfSpeech.tag_list){
            HashMap<String, Integer> emitteds = laplace_emissionPairMap.get(tag);
            HashMap<String, Float> e_prob = new HashMap<String, Float>();
            Iterator s_it = laplace_suffixCountMap.keySet().iterator();
            while (s_it.hasNext()){
                String suffix = (String)s_it.next();
                e_prob.put(suffix, (float) emitteds.get(suffix)/(uns_tagCountMap.get(tag) + suffixCount));
                laplace_emissionProbabilitiesMap.put(tag, e_prob);
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
                Bigram<String, String> bigram = new Bigram<>(first, second);
                HashMap<String, Integer> t_count = new HashMap<>();
                if (uns_trigramTransmissionPairMap.containsKey(bigram)){
                    t_count = uns_trigramTransmissionPairMap.get(bigram);
                }
                int denominator = uns_bigramCountMap.get(bigram) + 12;
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
                laplace_trigramTransmissionProbabilityMap.put(bigram, t_prob);
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

    public void calculateKneserNey_D_forTrigram(){
        int n1 = 0;
        int n2 = 0;

        Iterator it_f = uns_trigramTransmissionPairMap.values().iterator();
        while (it_f.hasNext()){
            HashMap<String, Integer> t_count = (HashMap<String, Integer>) it_f.next();
            Iterator it_l = t_count.values().iterator();
            while (it_l.hasNext()){
                int number = (int) it_l.next();

                if (number == 1){
                    n1++;
                } else if (number == 2){
                    n2++;
                }
            }
        }

        kneserNey_D_trigram = (float)n1 / (n1 + 2 * n2);
    }

    public void calculateKneserNey_D_forBigram(){

        int n1 = 0;
        int n2 = 0;

        Iterator it = uns_bigramCountMap.values().iterator();
        while (it.hasNext()){
            int number = (int) it.next();
            if (number == 1){
                n1++;
            } else if (number == 2){
                n2++;
            }
        }
        kneserNey_D_bigram = (float)n1 / (n1 + 2 * n2);
    }

    public void calculateKneserNey_bigramTransmissionProbabilities(){

        for (String t1 : PartOfSpeech.tag_list){
            if (uns_bigramTransmissionPairMap.containsKey(t1)){
                HashMap<String, Integer> t_count = uns_bigramTransmissionPairMap.get(t1);
                for (String t2 : PartOfSpeech.tag_list){
                    float knBase = 0f;
                    float inte = 0f;
                    float conti = 0f;
                    float knProb = 0f;
                    HashMap<String, Float> t_prob = new HashMap<>();
                    if (t_count.containsKey(t2)){
                        knBase = Math.max((t_count.get(t2)-kneserNey_D_bigram), 0) / uns_tagCountMap.get(t1);
                        inte = getGamaForBigram(t1);
                        conti = getContinuationForBigram(t2);
                        knProb = knBase + inte * conti;
                        t_prob.put(t2, knProb);
                    } else {
                        inte = getGamaForBigram(t1);
                        conti = getContinuationForBigram(t2);
                        knProb = inte * conti;
                        t_prob.put(t2, knProb);
                    }
                    kneserNey_bigramTransmissionProbabilityMap.put(t1, t_prob);
                }
            } else {
                /**
                 * Problem ??
                 */
            }
        }
    }

    public void calculateKneserNey_trigramTransmissionProbabilities(){

        for (String t1 : PartOfSpeech.tag_list){
            for (String t2 : PartOfSpeech.tag_list) {
                Bigram<String, String> firstTwo = new Bigram<>(t1, t2);
                HashMap<String, Integer> t_count = uns_trigramTransmissionPairMap.get(firstTwo);
                if (uns_trigramTransmissionPairMap.containsKey(firstTwo)) {
                    for (String t3 : PartOfSpeech.tag_list) {
                        float knBase = 0f;
                        float inte = 0f;
                        float conti = 0f;
                        float knProb = 0f;
                        HashMap<String, Float> t_prob = new HashMap<>();
                        if (t_count.containsKey(t3)){
                            knBase = Math.max(t_count.get(t3)-kneserNey_D_trigram, 0) / uns_bigramCountMap.get(firstTwo);
                            inte = getGamaForTrigram(t1, t2);
                            conti = getContinuationForTrigram(t2, t3);
                            knProb = knBase + inte * conti;
                            t_prob.put(t3, knProb);
                        } else {
                            inte = getGamaForTrigram(t1, t2);
                            conti = getContinuationForTrigram(t2, t3);
                            knProb = inte * conti;
                            t_prob.put(t3, knProb);
                        }
                        kneserNey_trigramTransmissionProbabilityMap.put(firstTwo, t_prob);
                    }
                } else {
                    /**
                     *  Problem ??
                     */
                }
            }
        }
    }

    public float getGamaForBigram(String t1){
        int count = 0;
        for (String t2 : PartOfSpeech.tag_list){
            Bigram<String, String> bigram = new Bigram<>(t1, t2);
            if (uns_bigramCountMap.get(bigram) != 0){
                count++;
            }
        }
        return ((float)kneserNey_D_bigram * count) / uns_tagCountMap.get(t1);
    }

    public float getContinuationForBigram(String t2){
        int numerator = 0;
        int denomunator = 0;
        for (String t1 : PartOfSpeech.tag_list){
            for (String ta2 : PartOfSpeech.tag_list){
                Bigram<String, String> biagram = new Bigram<>(t1, ta2);
                if (uns_bigramCountMap.get(biagram) != 0){
                    denomunator++;
                    if (ta2.equals(t2)){
                        numerator++;
                    }
                }
            }
        }
        return (float)numerator / denomunator;
    }

    // Hızlandırmak için trigram count'larıdaki sıfırlar silinebilinir.
    public float getGamaForTrigram(String t1, String t2) {
        int count = 0;
        for (String t3 : PartOfSpeech.tag_list){
            Trigram trigram = new Trigram(t1, t2, t3);
            if (uns_trigramCountMap.get(trigram) != 0){
                count++;
            }
        }
        Bigram biagram = new Bigram(t1, t2);
        return ((float)kneserNey_D_trigram * count) / uns_bigramCountMap.get(biagram);
    }

    public float getContinuationForTrigram(String t2, String t3) {
        int numerator = 0;
        int denomunator = 0;
        for (String ta1 : PartOfSpeech.tag_list){
            for (String ta3 : PartOfSpeech.tag_list){
                Trigram trigram = new Trigram(ta1, t2, ta3);
                if (uns_trigramCountMap.get(trigram) != 0){
                    denomunator++;
                    if (ta3.equals(t3)){
                        numerator++;
                    }
                }
            }
        }
        return (float)numerator / denomunator;
    }

    public void calculateTagProbabilities(){
        int totalStartPoint = 0;
        Iterator it = uns_tagCountMap.values().iterator();
        while (it.hasNext()){
            totalStartPoint = totalStartPoint + (Integer) it.next();
        }

        for (String s : PartOfSpeech.tag_list){
            if (uns_tagCountMap.containsKey(s)){
                uns_tagProbabilitiesMap.put(s, ((float) uns_tagCountMap.get(s)/totalStartPoint));
            } else {
                uns_tagProbabilitiesMap.put(s, 0f);
            }
        }
    }

    public void calculateKneserNeyEmissionProbabilities(){
        laplace_suffixCountMap = (HashMap<String, Integer>) uns_suffixCountMap.clone();
        for (String unseenSuffix : unseenSuffixList){
            laplace_suffixCountMap.put(unseenSuffix, 0);
        }

        for (String s : PartOfSpeech.tag_list){
            if (uns_emissionProbabilitiesMap.containsKey(s)){
                HashMap<String, Float> emitteds = uns_emissionProbabilitiesMap.get(s);
                HashMap<String, Float> t_prob = new HashMap<String, Float>();
                Iterator it = laplace_suffixCountMap.keySet().iterator();
                while (it.hasNext()){
                    String obs = (String)it.next();
                    if (!emitteds.containsKey(obs)){
                    } else {
                        t_prob.put(obs, uns_tagProbabilitiesMap.get(s));
                    }
                    uns_emissionProbabilitiesMap.put(s, t_prob);
                }
            } else {
                HashMap<String, Float> t_prob = new HashMap<String, Float>();
                Iterator it = laplace_suffixCountMap.keySet().iterator();
                while (it.hasNext()){
                    String obs = (String)it.next();
                    t_prob.put(obs, uns_tagProbabilitiesMap.get(s));
                    uns_emissionProbabilitiesMap.put(s, t_prob);
                }
            }

        }
    }


}
