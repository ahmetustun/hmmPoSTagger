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
    private HashMap<String, Float> uns_tagCountMap = new HashMap<>();
    private HashMap<Bigram<String, String>, Float> uns_bigramCountMap = new HashMap<>();
    private HashMap<String, Float> uns_tagProbabilitiesMap = new HashMap<>();
    private HashMap<Trigram, Float> uns_trigramCountMap = new HashMap<>();
    private HashMap<String, Float> uns_suffixCountMap = new HashMap<>();

    private HashMap<String, HashMap<String, Float>> uns_bigramTransmissionPairMap = new HashMap<>();
    private HashMap<String, HashMap<String, Float>> uns_emissionPairMap = new HashMap<>();
    private HashMap<String, HashMap<String, Float>> uns_emissionProbabilitiesMap = new HashMap<>();

    private HashMap<String, Float> laplace_suffixCountMap = new HashMap<>();
    private HashMap<String, HashMap<String, Float>> laplace_emissionPairMap = new HashMap<>();
    private HashMap<String, HashMap<String, Float>> laplace_emissionProbabilitiesMap = new HashMap<>();

    private HashMap<Bigram<String, String>, HashMap<String, Float>> uns_trigramTransmissionPairMap = new HashMap<>();

    private HashMap<Bigram<String, String>, HashMap<String, Float>> laplace_trigramTransmissionPairMap = new HashMap<>();
    private HashMap<Bigram<String, String>, HashMap<String, Float>> laplace_trigramTransmissionProbabilityMap = new HashMap<>();

    private ArrayList<String> unseenSuffixList = new ArrayList<>();

    private HashMap<String, HashMap<String, Float>>  kneserNey_bigramTransmissionProbabilityMap = new HashMap<>();
    private HashMap<Bigram<String, String>, HashMap<String, Float>> kneserNey_trigramTransmissionProbabilityMap = new HashMap<>();
    private HashMap<String, HashMap<String, Float>> kneserNey_emissionProbabilitiesMap = new HashMap<>();

    private HashMap<String, HashMap<String, Float>> interpolation_emissionProbabilitiesMap = new HashMap<>();
    private HashMap<String, HashMap<String, Float>>  interpolation_bigramTransmissionProbabilityMap = new HashMap<>();
    private HashMap<Bigram<String, String>, HashMap<String, Float>> interpolation_trigramTransmissionProbabilityMap = new HashMap<>();

    float kneserNey_D_bigram = 0.6f;
    float kneserNey_D_trigram = 0.6f;
    float additiveNumber = 0.4f;
    float interpolationBeta = 0.2f;

    public Smoother(HashMap<String, Float> uns_tagCountMap, HashMap<String, Float> uns_suffixCountMap, HashMap<String, HashMap<String, Float>> uns_bigramTransmissionPairMap,
                    HashMap<String, HashMap<String, Float>> emissionProbabilitiesMap, HashMap<String, HashMap<String, Float>> uns_emissionPairMap){

        this.uns_emissionPairMap = (HashMap<String, HashMap<String, Float>>) uns_emissionPairMap.clone();
        this.uns_tagCountMap = (HashMap<String, Float>) uns_tagCountMap.clone();
        this.uns_suffixCountMap = (HashMap<String, Float>) uns_suffixCountMap.clone();
        this.uns_bigramTransmissionPairMap = (HashMap<String, HashMap<String, Float>>) uns_bigramTransmissionPairMap.clone();
        this.uns_emissionProbabilitiesMap = (HashMap<String, HashMap<String, Float>>) emissionProbabilitiesMap.clone();

    }

    public Smoother(String fileName, HashMap<String, Float> uns_tagCountMap, HashMap<String, Float> uns_suffixCountMap, HashMap<String, HashMap<String, Float>> uns_bigramTransmissionPairMap,
                    HashMap<String, HashMap<String, Float>> emissionProbabilitiesMap, HashMap<String, HashMap<String, Float>> uns_emissionPairMap){

        this.uns_emissionPairMap = (HashMap<String, HashMap<String, Float>>) uns_emissionPairMap.clone();
        this.uns_tagCountMap = (HashMap<String, Float>) uns_tagCountMap.clone();
        this.uns_suffixCountMap = (HashMap<String, Float>) uns_suffixCountMap.clone();
        this.uns_bigramTransmissionPairMap = (HashMap<String, HashMap<String, Float>>) uns_bigramTransmissionPairMap.clone();
        this.uns_emissionProbabilitiesMap = (HashMap<String, HashMap<String, Float>>) emissionProbabilitiesMap.clone();

        Parse.parseTrainFile(fileName, unt_sentences);
    }

    public Smoother(HashMap<String, Float> uns_tagCountMap, HashMap<Bigram<String, String>, Float> uns_bigramCountMap, HashMap<String, HashMap<String, Float>> uns_bigramTransmissionPairMap,
                    HashMap<String, Integer> uns_suffixCountMap, HashMap<Bigram<String, String>, HashMap<String, Float>> uns_trigramTransmissionPairMap, HashMap<Trigram, Float> uns_trigramCountMap,
                    HashMap<String, HashMap<String, Float>> emissionProbabilitiesMap, HashMap<String, HashMap<String, Float>> uns_emissionPairMap){

        this.uns_trigramTransmissionPairMap = (HashMap<Bigram<String, String>, HashMap<String, Float>>) uns_trigramTransmissionPairMap.clone();
        this.uns_emissionPairMap = (HashMap<String, HashMap<String, Float>>) uns_emissionPairMap.clone();
        this.uns_tagCountMap = (HashMap<String, Float>) uns_tagCountMap.clone();
        this.uns_bigramCountMap = (HashMap<Bigram<String, String>, Float>) uns_bigramCountMap.clone();
        this.uns_suffixCountMap = (HashMap<String, Float>) uns_suffixCountMap.clone();
        this.uns_bigramTransmissionPairMap = (HashMap<String, HashMap<String, Float>>) uns_bigramTransmissionPairMap.clone();
        this.uns_trigramCountMap = (HashMap<Trigram, Float>) uns_trigramCountMap.clone();
        this.uns_emissionProbabilitiesMap = (HashMap<String, HashMap<String, Float>>) emissionProbabilitiesMap.clone();

    }

    public Smoother(String fileName, HashMap<String, Float> uns_tagCountMap, HashMap<Bigram<String, String>, Float> uns_bigramCountMap, HashMap<String, HashMap<String, Float>> uns_bigramTransmissionPairMap,
                    HashMap<String, Float> uns_suffixCountMap, HashMap<Bigram<String, String>, HashMap<String, Float>> uns_trigramTransmissionPairMap,
                    HashMap<Trigram<String, String, String>, Float> uns_trigramCountMap,
                    HashMap<String, HashMap<String, Float>> emissionProbabilitiesMap, HashMap<String, HashMap<String, Float>> uns_emissionPairMap){

        this.uns_trigramTransmissionPairMap = (HashMap<Bigram<String, String>, HashMap<String, Float>>) uns_trigramTransmissionPairMap.clone();
        this.uns_emissionPairMap = (HashMap<String, HashMap<String, Float>>) uns_emissionPairMap.clone();
        this.uns_tagCountMap = (HashMap<String, Float>) uns_tagCountMap.clone();
        this.uns_bigramCountMap = (HashMap<Bigram<String, String>, Float>) uns_bigramCountMap.clone();
        this.uns_suffixCountMap = (HashMap<String, Float>) uns_suffixCountMap.clone();
        this.uns_bigramTransmissionPairMap = (HashMap<String, HashMap<String, Float>>) uns_bigramTransmissionPairMap.clone();
        this.uns_trigramCountMap = (HashMap<Trigram, Float>) uns_trigramCountMap.clone();
        this.uns_emissionProbabilitiesMap = (HashMap<String, HashMap<String, Float>>) emissionProbabilitiesMap.clone();

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

    public HashMap<String, HashMap<String, Float>> getInterpolation_bigramTransmissionProbabilityMap() {
        return interpolation_bigramTransmissionProbabilityMap;
    }

    public HashMap<Bigram<String, String>, HashMap<String, Float>> getInterpolation_trigramTransmissionProbabilityMap() {
        return interpolation_trigramTransmissionProbabilityMap;
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

    public HashMap<String, HashMap<String, Float>> getKneserNey_bigramTransmissionProbabilityMap() {
        return kneserNey_bigramTransmissionProbabilityMap;
    }

    public HashMap<Bigram<String, String>, HashMap<String, Float>> getKneserNey_trigramTransmissionProbabilityMap() {
        return kneserNey_trigramTransmissionProbabilityMap;
    }

    public HashMap<String, HashMap<String, Float>> getInterpolation_emissionProbabilitiesMap() {
        return interpolation_emissionProbabilitiesMap;
    }

    public HashMap<String, HashMap<String, Float>> getKneserNey_emissionProbabilitiesMap() {
        return kneserNey_emissionProbabilitiesMap;

    }

    public HashMap<String, HashMap<String, Float>> getLaplace_emissionPairMap() {
        return laplace_emissionPairMap;

    }

    public HashMap<String, Float> getLaplace_suffixCountMap() {
        return laplace_suffixCountMap;
    }

    public void addOneToEmissionPair(){

        laplace_suffixCountMap = (HashMap<String, Float>) uns_suffixCountMap.clone();
        for (String unseenSuffix : unseenSuffixList){
            laplace_suffixCountMap.put(unseenSuffix, 0f);
        }

        for (String s : PartOfSpeech.tag_list){
            if (uns_emissionPairMap.containsKey(s)){
                HashMap<String, Float> emitteds = uns_emissionPairMap.get(s);
                HashMap<String, Float> t_prob = new HashMap<String, Float>();
                Iterator it = laplace_suffixCountMap.keySet().iterator();
                while (it.hasNext()){
                    String obs = (String)it.next();
                    if (emitteds.containsKey(obs)){
                        t_prob.put(obs, emitteds.get(obs) + additiveNumber);
                    } else {
                        t_prob.put(obs, additiveNumber);
                    }
                    laplace_emissionPairMap.put(s, t_prob);
                }
            } else {
                HashMap<String, Float> t_prob = new HashMap<String, Float>();
                Iterator it = laplace_suffixCountMap.keySet().iterator();
                while (it.hasNext()){
                    String obs = (String)it.next();
                    t_prob.put(obs, additiveNumber);
                    laplace_emissionPairMap.put(s, t_prob);
                }
            }

        }
    }

    public void calculateEmissionProbabilities(){

        int suffixCount = laplace_suffixCountMap.size();

        for (String tag : PartOfSpeech.tag_list){
            HashMap<String, Float> emitteds = laplace_emissionPairMap.get(tag);
            HashMap<String, Float> e_prob = new HashMap<String, Float>();
            Iterator s_it = laplace_suffixCountMap.keySet().iterator();
            while (s_it.hasNext()){
                String suffix = (String)s_it.next();
                e_prob.put(suffix, (float) emitteds.get(suffix)/(uns_tagCountMap.get(tag) + additiveNumber*suffixCount));
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
                HashMap<String, Float> t_count = new HashMap<>();
                if (uns_trigramTransmissionPairMap.containsKey(bigram)){
                    t_count = uns_trigramTransmissionPairMap.get(bigram);
                }
                float denominator = uns_bigramCountMap.get(bigram) + 12*additiveNumber;
                HashMap<String, Float> t_prob = new HashMap<>();
                for (String tag : PartOfSpeech.tag_list){
                    float numerator = 0f;
                    if (t_count.containsKey(tag)){
                        numerator = (float)(t_count.get(tag) + additiveNumber);
                    } else {
                        numerator = additiveNumber;
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

    public void kneserNeySmooothing(){
        tagRatioBasedEmission();
        calculateKneserNey_D_forBigram();
        calculateKneserNey_D_forTrigram();
        calculateKneserNey_bigramTransmissionProbabilities();
        calculateKneserNey_trigramTransmissionProbabilities();
        //addOneForTrigramTransmission();
    }

    public void tagRatioBasedEmission() {
        calculateTagProbabilities();
        countUnseenSuffixesForTestCorpus();
        calculateEmissionProbabilitiesWithTagRatio();
    }

    public void interpolationBasedEmission() {
        calculateTagProbabilities();
        countUnseenSuffixesForTestCorpus();
        interpolationForEmission();
    }

    public void interpolationForBoth() {
        interpolationBasedEmission();
        //calculateKneserNey_D_forBigram();
        //calculateKneserNey_D_forTrigram();
        //calculateKneserNey_bigramTransmissionProbabilities();
        //calculateKneserNey_trigramTransmissionProbabilities();
        interpolationForBiagram();
        interpolationForTrigram();
    }

    public void interpolationSmoothingForTransitionWithTagRatioEmission(){
        tagRatioBasedEmission();
        interpolationForBiagram();
        interpolationForTrigram();
    }

    public void calculateKneserNey_D_forTrigram(){
        float n1 = 0f;
        float n2 = 0f;

        Iterator it_f = uns_trigramTransmissionPairMap.values().iterator();
        while (it_f.hasNext()){
            HashMap<String, Float> t_count = (HashMap<String, Float>) it_f.next();
            Iterator it_l = t_count.values().iterator();
            while (it_l.hasNext()){
                float number = (float) it_l.next();

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
            float number = (float) it.next();
            if (number == 1){
                n1++;
            } else if (number == 2){
                n2++;
            }
        }
        kneserNey_D_bigram = (float)n1 / (n1 + 2 * n2);
    }

    public void calculateKneserNey_bigramTransmissionProbabilities(){
        float knBase = 0f;
        float inte = 0f;
        float conti = 0f;
        float knProb = 0f;
        for (String t1 : PartOfSpeech.tag_list){
            float unigramCount = uns_tagCountMap.get(t1);
            HashMap<String, Float> t_prob = new HashMap<>();
            if (unigramCount != 0){
                for (String t2 : PartOfSpeech.tag_list){
                    Bigram<String, String> tags = new Bigram<>(t1, t2);
                    float bigramCount = uns_bigramCountMap.get(tags);
                    knBase = Math.max((bigramCount-kneserNey_D_bigram), 0) / unigramCount;
                    inte = getGamaForBigram(t1);
                    conti = getContinuationForBigram(t2);
                    knProb = knBase + inte * conti;
                    t_prob.put(t2, knProb);
                }
                kneserNey_bigramTransmissionProbabilityMap.put(t1, t_prob);
            } else {
                /**
                 * Problem ??
                 */
                System.out.println(t1 + " not found in train set");
            }
        }
    }

    public void calculateKneserNey_trigramTransmissionProbabilities(){
        float knBase = 0f;
        float inte = 0f;
        float conti = 0f;
        float knProb = 0f;
        for (String t1 : PartOfSpeech.tag_list){
            for (String t2 : PartOfSpeech.tag_list) {
                Bigram<String, String> firstTwo = new Bigram<>(t1, t2);
                float biagramCount = uns_bigramCountMap.get(firstTwo);
                if (biagramCount != 0) {
                    HashMap<String, Float> t_prob = new HashMap<>();
                    for (String t3 : PartOfSpeech.tag_list) {
                        Trigram<String, String, String> tags = new Trigram<>(t1, t2, t3);
                        float trigramCount = uns_trigramCountMap.get(tags);
                        float ratio = trigramCount / biagramCount;
                        float c_kn = kneserNey_bigramTransmissionProbabilityMap.get(t1).get(t2) * uns_tagCountMap.get(t1);
                        knBase = Math.max((c_kn * ratio)-kneserNey_D_trigram, 0) / c_kn;
                        inte = getGamaForTrigram(t1, t2) * c_kn;
                        HashMap<String, Float> t_p = kneserNey_bigramTransmissionProbabilityMap.get(t2);
                        conti = t_p.get(t3);
                        knProb = knBase + inte * conti;
                        t_prob.put(t3, knProb);
                    }
                    kneserNey_trigramTransmissionProbabilityMap.put(firstTwo, t_prob);
                } else {
                    HashMap<String, Float> t_prob = new HashMap<>();
                    for (String t3 : PartOfSpeech.tag_list) {
                        float c_kn = kneserNey_bigramTransmissionProbabilityMap.get(t1).get(t2) * uns_tagCountMap.get(t1);
                        knBase = 0;
                        inte = getGamaForTrigram(t1, t2) * c_kn;
                        HashMap<String, Float> t_p = kneserNey_bigramTransmissionProbabilityMap.get(t2);
                        conti = t_p.get(t3);
                        knProb = knBase + inte * conti;
                        t_prob.put(t3, knProb);
                    }
                    kneserNey_trigramTransmissionProbabilityMap.put(firstTwo, t_prob);
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
        Bigram biagram = new Bigram(t1, t2);
        if (uns_bigramCountMap.get(biagram) == 0){
            return 0.2f;
        } else {
            int count = 0;
            for (String t3 : PartOfSpeech.tag_list){
                Trigram trigram = new Trigram(t1, t2, t3);
                if (uns_trigramCountMap.get(trigram) != 0){
                    count++;
                }
            }
            return ((float)kneserNey_D_trigram * count) / uns_bigramCountMap.get(biagram);
        }
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
        float totalStartPoint = 0;
        Iterator it = uns_tagCountMap.values().iterator();
        while (it.hasNext()){
            totalStartPoint = totalStartPoint + (float) it.next();
        }

        for (String s : PartOfSpeech.tag_list){
            if (uns_tagCountMap.containsKey(s)){
                uns_tagProbabilitiesMap.put(s, ((float) uns_tagCountMap.get(s)/totalStartPoint));
            } else {
                uns_tagProbabilitiesMap.put(s, 0f);
            }
        }
    }

    public void calculateEmissionProbabilitiesWithTagRatio(){
        for (String s : PartOfSpeech.tag_list){
            if (uns_emissionProbabilitiesMap.containsKey(s)){
                HashMap<String, Float> emitteds = uns_emissionProbabilitiesMap.get(s);
                for (String unseenSuffix : unseenSuffixList){
                    emitteds.put(unseenSuffix, uns_tagProbabilitiesMap.get(s));
                }
                kneserNey_emissionProbabilitiesMap.put(s, emitteds);
            } else {
                HashMap<String, Float> e_prob = new HashMap<String, Float>();
                for (String unseenSuffix : unseenSuffixList){
                    e_prob.put(unseenSuffix, uns_tagProbabilitiesMap.get(s));
                }
                kneserNey_emissionProbabilitiesMap.put(s, e_prob);
            }
        }
    }

    public void interpolationForEmission() {
            laplace_suffixCountMap = (HashMap<String, Float>) uns_suffixCountMap.clone();
            for (String unseenSuffix : unseenSuffixList){
                laplace_suffixCountMap.put(unseenSuffix, 0f);
            }

            float total = 0f;
            Iterator<String> keys = laplace_suffixCountMap.keySet().iterator();
            while (keys.hasNext()){
                String suffix = keys.next();
                total = total + laplace_suffixCountMap.get(suffix);
            }

            for (String s : PartOfSpeech.tag_list){
                    HashMap<String, Float> emitteds = uns_emissionPairMap.get(s);
                    HashMap<String, Float> t_prob = new HashMap<String, Float>();
                    Iterator it = laplace_suffixCountMap.keySet().iterator();
                    while (it.hasNext()){
                        String obs = (String)it.next();
                        if (emitteds.containsKey(obs)){
                            t_prob.put(obs, (interpolationBeta * emitteds.get(obs) + (1 - interpolationBeta) * ( 1 / total)));
                        } else {
                            t_prob.put(obs, (1 - interpolationBeta) * ( 1 / total));
                        }
                        interpolation_emissionProbabilitiesMap.put(s, t_prob);
                    }
            }
    }

    public void interpolationForBiagram(){
        float lambda = 0.9f;

        for (String t1 : PartOfSpeech.tag_list){
            if (uns_bigramTransmissionPairMap.containsKey(t1)){
                HashMap<String, Float> uns_t_prob = uns_bigramTransmissionPairMap.get(t1);
                HashMap<String, Float> t_prob = new HashMap<String, Float>();
                for (String t2 : PartOfSpeech.tag_list){
                    float ratio = 0f;
                    if (uns_t_prob.containsKey(t2)){
                        ratio = lambda * uns_t_prob.get(t2) + (1f - lambda) * uns_tagProbabilitiesMap.get(t2);
                    } else {
                        ratio = lambda * uns_tagProbabilitiesMap.get(t2);
                    }
                    t_prob.put(t2, ratio);
                }
                interpolation_bigramTransmissionProbabilityMap.put(t1, t_prob);
            } else {
                HashMap<String, Float> t_prob = new HashMap<String, Float>();
                for (String t2 : PartOfSpeech.tag_list){
                    float ratio = lambda * uns_tagProbabilitiesMap.get(t2);
                    t_prob.put(t2, ratio);
                }
                interpolation_bigramTransmissionProbabilityMap.put(t1, t_prob);
            }
        }
    }

    public void interpolationForTrigram() {
        float lambda_1 = 0.6f;
        float lambda_2 = 0.3f;
        float lambda_3 = 0.1f;

        for (String t1 : PartOfSpeech.tag_list){
            for (String t2 : PartOfSpeech.tag_list){
                Bigram<String, String> bigram = new Bigram<>(t1, t2);
                float biagramCount = uns_bigramCountMap.get(bigram);
                if (biagramCount != 0) {
                    HashMap<String, Float> uns_t_prob = uns_trigramTransmissionPairMap.get(bigram);
                    HashMap<String, Float> t_prob = new HashMap<String, Float>();
                    for (String t3 : PartOfSpeech.tag_list){
                        float ratio = 0f;
                        if (uns_t_prob.containsKey(t3)){
                            ratio = lambda_1 * uns_t_prob.get(t3) + lambda_2 * interpolation_bigramTransmissionProbabilityMap.get(t2).get(t3) + lambda_3 * uns_tagProbabilitiesMap.get(t3);
                        } else {
                            ratio = lambda_2 * interpolation_bigramTransmissionProbabilityMap.get(t2).get(t3) + lambda_3 * uns_tagProbabilitiesMap.get(t3);
                        }
                        t_prob.put(t3, ratio);
                    }
                    interpolation_trigramTransmissionProbabilityMap.put(bigram, t_prob);
                } else {
                    HashMap<String, Float> t_prob = new HashMap<String, Float>();
                    for (String t3 : PartOfSpeech.tag_list){
                        float ratio = ratio = lambda_2 * interpolation_bigramTransmissionProbabilityMap.get(t2).get(t3) + lambda_3 * uns_tagProbabilitiesMap.get(t3);
                        t_prob.put(t3, ratio);
                    }
                    interpolation_trigramTransmissionProbabilityMap.put(bigram, t_prob);
                }
            }
        }
    }
}
