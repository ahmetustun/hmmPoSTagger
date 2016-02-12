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
    private HashMap<String, Double> uns_tagCountMap = new HashMap<>();
    private HashMap<Bigram<String, String>, Double> uns_bigramCountMap = new HashMap<>();
    private HashMap<String, Double> uns_tagProbabilitiesMap = new HashMap<>();
    private HashMap<Bigram<String, String>, Double> uns_bigramProbabilitiesMap = new HashMap<>();
    private HashMap<Trigram, Double> uns_trigramCountMap = new HashMap<>();
    private HashMap<String, Double> uns_suffixCountMap = new HashMap<>();

    private HashMap<String, HashMap<String, Double>> uns_bigramTransmissionProbabilitiesMap = new HashMap<>();
    private HashMap<String, HashMap<String, Double>> uns_emissionPairMap = new HashMap<>();
    private HashMap<String, HashMap<String, Double>> uns_emissionProbabilitiesMap = new HashMap<>();

    private HashMap<String, Double> laplace_suffixCountMap = new HashMap<>();
    private HashMap<String, HashMap<String, Double>> laplace_emissionPairMap = new HashMap<>();
    private HashMap<String, HashMap<String, Double>> laplace_emissionProbabilitiesMap = new HashMap<>();

    private HashMap<Bigram<String, String>, HashMap<String, Double>> uns_trigramTransmissionPairMap = new HashMap<>();
    private HashMap<Bigram<String, String>, HashMap<String, Double>> uns_trigramTransmissionProbabilitiesMap = new HashMap<>();

    private HashMap<Bigram<String, String>, HashMap<String, Double>> laplace_trigramTransmissionPairMap = new HashMap<>();
    private HashMap<Bigram<String, String>, HashMap<String, Double>> laplace_trigramTransmissionProbabilityMap = new HashMap<>();

    private ArrayList<String> unseenSuffixList = new ArrayList<>();

    private HashMap<String, HashMap<String, Double>>  kneserNey_bigramTransmissionProbabilityMap = new HashMap<>();
    private HashMap<Bigram<String, String>, HashMap<String, Double>> kneserNey_trigramTransmissionProbabilityMap = new HashMap<>();
    private HashMap<String, HashMap<String, Double>> kneserNey_emissionProbabilitiesMap = new HashMap<>();

    private HashMap<String, HashMap<String, Double>> interpolation_emissionProbabilitiesMap = new HashMap<>();
    private HashMap<String, HashMap<String, Double>>  interpolation_bigramTransmissionProbabilityMap = new HashMap<>();
    private HashMap<Bigram<String, String>, HashMap<String, Double>> interpolation_trigramTransmissionProbabilityMap = new HashMap<>();

    double kneserNey_D_bigram = 0.6f;
    double kneserNey_D_trigram = 0.6f;
    double additiveNumber = 0.4f;
    double interpolationBeta = 0.8f;

    public Smoother(HashMap<String, Double> uns_tagCountMap, HashMap<String, Double> uns_suffixCountMap, HashMap<String, HashMap<String, Double>> uns_bigramTransmissionProbabilitiesMap,
                    HashMap<String, HashMap<String, Double>> emissionProbabilitiesMap, HashMap<String, HashMap<String, Double>> uns_emissionPairMap){

        this.uns_emissionPairMap = (HashMap<String, HashMap<String, Double>>) uns_emissionPairMap.clone();
        this.uns_tagCountMap = (HashMap<String, Double>) uns_tagCountMap.clone();
        this.uns_suffixCountMap = (HashMap<String, Double>) uns_suffixCountMap.clone();
        this.uns_bigramTransmissionProbabilitiesMap = (HashMap<String, HashMap<String, Double>>) uns_bigramTransmissionProbabilitiesMap.clone();
        this.uns_emissionProbabilitiesMap = (HashMap<String, HashMap<String, Double>>) emissionProbabilitiesMap.clone();

    }

    public Smoother(String fileName, HashMap<String, Double> uns_tagCountMap, HashMap<String, Double> uns_suffixCountMap, HashMap<String, HashMap<String, Double>> uns_bigramTransmissionProbabilitiesMap,
                    HashMap<String, HashMap<String, Double>> emissionProbabilitiesMap, HashMap<String, HashMap<String, Double>> uns_emissionPairMap){

        this.uns_emissionPairMap = (HashMap<String, HashMap<String, Double>>) uns_emissionPairMap.clone();
        this.uns_tagCountMap = (HashMap<String, Double>) uns_tagCountMap.clone();
        this.uns_suffixCountMap = (HashMap<String, Double>) uns_suffixCountMap.clone();
        this.uns_bigramTransmissionProbabilitiesMap = (HashMap<String, HashMap<String, Double>>) uns_bigramTransmissionProbabilitiesMap.clone();
        this.uns_emissionProbabilitiesMap = (HashMap<String, HashMap<String, Double>>) emissionProbabilitiesMap.clone();

        Parse.parseTrainFile(fileName, unt_sentences);
    }

    public Smoother(HashMap<String, Double> uns_tagCountMap, HashMap<Bigram<String, String>, Double> uns_bigramCountMap, HashMap<String, HashMap<String, Double>> uns_bigramTransmissionProbabilitiesMap,
                    HashMap<String, Integer> uns_suffixCountMap, HashMap<Bigram<String, String>, HashMap<String, Double>> uns_trigramTransmissionPairMap, HashMap<Trigram, Double> uns_trigramCountMap,
                    HashMap<Bigram<String, String>, HashMap<String, Double>> uns_trigramTransmissionProbabilitiesMap,
                    HashMap<String, HashMap<String, Double>> emissionProbabilitiesMap, HashMap<String, HashMap<String, Double>> uns_emissionPairMap){

        this.uns_trigramTransmissionPairMap = (HashMap<Bigram<String, String>, HashMap<String, Double>>) uns_trigramTransmissionPairMap.clone();
        this.uns_trigramTransmissionProbabilitiesMap = (HashMap<Bigram<String, String>, HashMap<String, Double>>) uns_trigramTransmissionProbabilitiesMap.clone();
        this.uns_emissionPairMap = (HashMap<String, HashMap<String, Double>>) uns_emissionPairMap.clone();
        this.uns_tagCountMap = (HashMap<String, Double>) uns_tagCountMap.clone();
        this.uns_bigramCountMap = (HashMap<Bigram<String, String>, Double>) uns_bigramCountMap.clone();
        this.uns_suffixCountMap = (HashMap<String, Double>) uns_suffixCountMap.clone();
        this.uns_bigramTransmissionProbabilitiesMap = (HashMap<String, HashMap<String, Double>>) uns_bigramTransmissionProbabilitiesMap.clone();
        this.uns_trigramCountMap = (HashMap<Trigram, Double>) uns_trigramCountMap.clone();
        this.uns_emissionProbabilitiesMap = (HashMap<String, HashMap<String, Double>>) emissionProbabilitiesMap.clone();

    }

    public Smoother(String fileName, HashMap<String, Double> uns_tagCountMap, HashMap<Bigram<String, String>, Double> uns_bigramCountMap, HashMap<String, HashMap<String, Double>> uns_bigramTransmissionProbabilitiesMap,
                    HashMap<String, Double> uns_suffixCountMap, HashMap<Bigram<String, String>, HashMap<String, Double>> uns_trigramTransmissionPairMap, HashMap<Bigram<String, String>, HashMap<String, Double>> uns_trigramTransmissionProbabilitiesMap,
                    HashMap<Trigram<String, String, String>, Double> uns_trigramCountMap,
                    HashMap<String, HashMap<String, Double>> emissionProbabilitiesMap, HashMap<String, HashMap<String, Double>> uns_emissionPairMap){

        this.uns_trigramTransmissionPairMap = (HashMap<Bigram<String, String>, HashMap<String, Double>>) uns_trigramTransmissionPairMap.clone();
        this.uns_trigramTransmissionProbabilitiesMap = (HashMap<Bigram<String, String>, HashMap<String, Double>>) uns_trigramTransmissionProbabilitiesMap.clone();
        this.uns_emissionPairMap = (HashMap<String, HashMap<String, Double>>) uns_emissionPairMap.clone();
        this.uns_tagCountMap = (HashMap<String, Double>) uns_tagCountMap.clone();
        this.uns_bigramCountMap = (HashMap<Bigram<String, String>, Double>) uns_bigramCountMap.clone();
        this.uns_suffixCountMap = (HashMap<String, Double>) uns_suffixCountMap.clone();
        this.uns_bigramTransmissionProbabilitiesMap = (HashMap<String, HashMap<String, Double>>) uns_bigramTransmissionProbabilitiesMap.clone();
        this.uns_trigramCountMap = (HashMap<Trigram, Double>) uns_trigramCountMap.clone();
        this.uns_emissionProbabilitiesMap = (HashMap<String, HashMap<String, Double>>) emissionProbabilitiesMap.clone();

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

    public HashMap<Bigram<String, String>, HashMap<String, Double>> getLaplace_trigramTransmissionProbabilityMap() {
        return laplace_trigramTransmissionProbabilityMap;
    }

    public HashMap<String, HashMap<String, Double>> getInterpolation_bigramTransmissionProbabilityMap() {
        return interpolation_bigramTransmissionProbabilityMap;
    }

    public HashMap<Bigram<String, String>, HashMap<String, Double>> getInterpolation_trigramTransmissionProbabilityMap() {
        return interpolation_trigramTransmissionProbabilityMap;
    }

    public ArrayList<ArrayList<String>> getUnTaggedSuffixesList() {
        return unTaggedSuffixesList;
    }

    public ArrayList<String> getUnseenSuffixList() {
        return unseenSuffixList;
    }

    public HashMap<String, HashMap<String, Double>> getLaplace_emissionProbabilitiesMap() {
        return laplace_emissionProbabilitiesMap;
    }

    public HashMap<String, HashMap<String, Double>> getKneserNey_bigramTransmissionProbabilityMap() {
        return kneserNey_bigramTransmissionProbabilityMap;
    }

    public HashMap<Bigram<String, String>, HashMap<String, Double>> getKneserNey_trigramTransmissionProbabilityMap() {
        return kneserNey_trigramTransmissionProbabilityMap;
    }

    public HashMap<String, HashMap<String, Double>> getInterpolation_emissionProbabilitiesMap() {
        return interpolation_emissionProbabilitiesMap;
    }

    public HashMap<String, HashMap<String, Double>> getKneserNey_emissionProbabilitiesMap() {
        return kneserNey_emissionProbabilitiesMap;

    }

    public HashMap<String, HashMap<String, Double>> getLaplace_emissionPairMap() {
        return laplace_emissionPairMap;

    }

    public HashMap<String, Double> getLaplace_suffixCountMap() {
        return laplace_suffixCountMap;
    }

    public void addOneToEmissionPair(){

        laplace_suffixCountMap = (HashMap<String, Double>) uns_suffixCountMap.clone();
        for (String unseenSuffix : unseenSuffixList){
            laplace_suffixCountMap.put(unseenSuffix, 0d);
        }

        for (String s : PartOfSpeech.tag_list){
            if (uns_emissionPairMap.containsKey(s)){
                HashMap<String, Double> emitteds = uns_emissionPairMap.get(s);
                HashMap<String, Double> t_prob = new HashMap<String, Double>();
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
                HashMap<String, Double> t_prob = new HashMap<String, Double>();
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
            HashMap<String, Double> emitteds = laplace_emissionPairMap.get(tag);
            HashMap<String, Double> e_prob = new HashMap<String, Double>();
            Iterator s_it = laplace_suffixCountMap.keySet().iterator();
            while (s_it.hasNext()){
                String suffix = (String)s_it.next();
                e_prob.put(suffix, (Double) emitteds.get(suffix)/(uns_tagCountMap.get(tag) + additiveNumber*suffixCount));
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
                HashMap<String, Double> t_count = new HashMap<>();
                if (uns_trigramTransmissionPairMap.containsKey(bigram)){
                    t_count = uns_trigramTransmissionPairMap.get(bigram);
                }
                double denominator = uns_bigramCountMap.get(bigram) + 12*additiveNumber;
                HashMap<String, Double> t_prob = new HashMap<>();
                for (String tag : PartOfSpeech.tag_list){
                    double numerator = 0f;
                    if (t_count.containsKey(tag)){
                        numerator = (double)(t_count.get(tag) + additiveNumber);
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
        calculateBigramRatio();
        countUnseenSuffixesForTestCorpus();
        calculateEmissionProbabilitiesWithTagRatio();
    }

    public void interpolationBasedEmission() {
        calculateTagProbabilities();
        calculateBigramRatio();
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
        interpolationForTrigram_2();
    }

    public void interpolationSmoothingForTransitionWithTagRatioEmission(){
        tagRatioBasedEmission();
        interpolationForBiagram();
        interpolationForTrigram_2();
    }

    public void calculateKneserNey_D_forTrigram(){
        double n1 = 0d;
        double n2 = 0d;

        Iterator it_f = uns_trigramTransmissionPairMap.values().iterator();
        while (it_f.hasNext()){
            HashMap<String, Double> t_count = (HashMap<String, Double>) it_f.next();
            Iterator it_l = t_count.values().iterator();
            while (it_l.hasNext()){
                double number = (double) it_l.next();

                if (number == 1){
                    n1++;
                } else if (number == 2){
                    n2++;
                }
            }
        }

        kneserNey_D_trigram = (double)n1 / (n1 + 2 * n2);
    }

    public void calculateKneserNey_D_forBigram(){

        int n1 = 0;
        int n2 = 0;

        Iterator it = uns_bigramCountMap.values().iterator();
        while (it.hasNext()){
            double number = (double) it.next();
            if (number == 1){
                n1++;
            } else if (number == 2){
                n2++;
            }
        }
        kneserNey_D_bigram = (double)n1 / (n1 + 2 * n2);
    }

    public void calculateKneserNey_bigramTransmissionProbabilities(){
        double knBase = 0d;
        double inte = 0d;
        double conti = 0d;
        double knProb = 0d;
        for (String t1 : PartOfSpeech.tag_list){
            double unigramCount = uns_tagCountMap.get(t1);
            HashMap<String, Double> t_prob = new HashMap<>();
            if (unigramCount != 0){
                for (String t2 : PartOfSpeech.tag_list){
                    Bigram<String, String> tags = new Bigram<>(t1, t2);
                    double bigramCount = uns_bigramCountMap.get(tags);
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
        double knBase = 0d;
        double inte = 0d;
        double conti = 0d;
        double knProb = 0d;
        for (String t1 : PartOfSpeech.tag_list){
            for (String t2 : PartOfSpeech.tag_list) {
                Bigram<String, String> firstTwo = new Bigram<>(t1, t2);
                double biagramCount = uns_bigramCountMap.get(firstTwo);
                if (biagramCount != 0) {
                    HashMap<String, Double> t_prob = new HashMap<>();
                    for (String t3 : PartOfSpeech.tag_list) {
                        Trigram<String, String, String> tags = new Trigram<>(t1, t2, t3);
                        double trigramCount = uns_trigramCountMap.get(tags);
                        double ratio = trigramCount / biagramCount;
                        double c_kn = kneserNey_bigramTransmissionProbabilityMap.get(t1).get(t2) * uns_tagCountMap.get(t1);
                        knBase = Math.max((c_kn * ratio)-kneserNey_D_trigram, 0) / c_kn;
                        inte = getGamaForTrigram(t1, t2) * c_kn;
                        HashMap<String, Double> t_p = kneserNey_bigramTransmissionProbabilityMap.get(t2);
                        conti = t_p.get(t3);
                        knProb = knBase + inte * conti;
                        t_prob.put(t3, knProb);
                    }
                    kneserNey_trigramTransmissionProbabilityMap.put(firstTwo, t_prob);
                } else {
                    HashMap<String, Double> t_prob = new HashMap<>();
                    for (String t3 : PartOfSpeech.tag_list) {
                        double c_kn = kneserNey_bigramTransmissionProbabilityMap.get(t1).get(t2) * uns_tagCountMap.get(t1);
                        knBase = 0;
                        inte = getGamaForTrigram(t1, t2) * c_kn;
                        HashMap<String, Double> t_p = kneserNey_bigramTransmissionProbabilityMap.get(t2);
                        conti = t_p.get(t3);
                        knProb = knBase + inte * conti;
                        t_prob.put(t3, knProb);
                    }
                    kneserNey_trigramTransmissionProbabilityMap.put(firstTwo, t_prob);
                }
            }
        }
    }

    public double getGamaForBigram(String t1){
        int count = 0;
        for (String t2 : PartOfSpeech.tag_list){
            Bigram<String, String> bigram = new Bigram<>(t1, t2);
            if (uns_bigramCountMap.get(bigram) != 0){
                count++;
            }
        }
        return ((double)kneserNey_D_bigram * count) / uns_tagCountMap.get(t1);
    }

    public double getContinuationForBigram(String t2){
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
        return (double)numerator / denomunator;
    }

    // Hızlandırmak için trigram count'larıdaki sıfırlar silinebilinir.
    public double getGamaForTrigram(String t1, String t2) {
        Bigram biagram = new Bigram(t1, t2);
        if (uns_bigramCountMap.get(biagram) == 0){
            return 0.2d;
        } else {
            int count = 0;
            for (String t3 : PartOfSpeech.tag_list){
                Trigram trigram = new Trigram(t1, t2, t3);
                if (uns_trigramCountMap.get(trigram) != 0){
                    count++;
                }
            }
            return ((double)kneserNey_D_trigram * count) / uns_bigramCountMap.get(biagram);
        }
    }

    public double getContinuationForTrigram(String t2, String t3) {
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
        return (double)numerator / denomunator;
    }

    public void calculateTagProbabilities(){
        double totalStartPoint = 0;
        Iterator it = uns_tagCountMap.values().iterator();
        while (it.hasNext()){
            totalStartPoint = totalStartPoint + (double) it.next();
        }

        for (String s : PartOfSpeech.tag_list){
            if (uns_tagCountMap.containsKey(s)){
                uns_tagProbabilitiesMap.put(s, ((double) uns_tagCountMap.get(s)/totalStartPoint));

                if (((double) uns_tagCountMap.get(s)/totalStartPoint) > 1){
                    System.out.println();
                }

            } else {
                uns_tagProbabilitiesMap.put(s, 0d);
            }
        }
    }

    public void calculateEmissionProbabilitiesWithTagRatio(){
        for (String s : PartOfSpeech.tag_list){
            if (uns_emissionProbabilitiesMap.containsKey(s)){
                HashMap<String, Double> emitteds = uns_emissionProbabilitiesMap.get(s);
                for (String unseenSuffix : unseenSuffixList){
                    emitteds.put(unseenSuffix, uns_tagProbabilitiesMap.get(s));

                    if (uns_tagProbabilitiesMap.get(s) > 1){
                        System.out.println();
                    }

                }
                kneserNey_emissionProbabilitiesMap.put(s, emitteds);
            } else {
                HashMap<String, Double> e_prob = new HashMap<String, Double>();
                for (String unseenSuffix : unseenSuffixList){
                    e_prob.put(unseenSuffix, uns_tagProbabilitiesMap.get(s));

                    if (uns_tagProbabilitiesMap.get(s) > 1){
                        System.out.println();
                    }

                }
                kneserNey_emissionProbabilitiesMap.put(s, e_prob);
            }
        }
    }

    public void interpolationForEmission() {
            laplace_suffixCountMap = (HashMap<String, Double>) uns_suffixCountMap.clone();
            for (String unseenSuffix : unseenSuffixList){
                laplace_suffixCountMap.put(unseenSuffix, 0d);
            }

            double total = 0d;
            Iterator<String> keys = laplace_suffixCountMap.keySet().iterator();
            while (keys.hasNext()){
                String suffix = keys.next();
                total = total + laplace_suffixCountMap.get(suffix);
            }

            for (String s : PartOfSpeech.tag_list){
                    HashMap<String, Double> emitteds = uns_emissionProbabilitiesMap.get(s);
                    HashMap<String, Double> t_prob = new HashMap<String, Double>();
                    Iterator it = laplace_suffixCountMap.keySet().iterator();
                    while (it.hasNext()){
                        String obs = (String)it.next();
                        if (emitteds.containsKey(obs)){
                            t_prob.put(obs, (interpolationBeta * emitteds.get(obs) + (1 - interpolationBeta) * ( 1 / total)));

                            if ((interpolationBeta * emitteds.get(obs) + (1 - interpolationBeta) * ( 1 / total)) > 1){
                                System.out.println();
                            }

                        } else {
                            t_prob.put(obs, (1 - interpolationBeta) * ( 1 / total));

                            if ((1 - interpolationBeta) * ( 1 / total) > 1){
                                System.out.println();
                            }

                        }
                        interpolation_emissionProbabilitiesMap.put(s, t_prob);
                    }
            }
    }

    public void calculateBigramRatio() {
        double totalStartPoint = 0;
        Iterator it = uns_bigramCountMap.values().iterator();
        while (it.hasNext()){
            totalStartPoint = totalStartPoint + (double) it.next();
        }

        for (String s : PartOfSpeech.tag_list){
            for (String t : PartOfSpeech.tag_list){
                Bigram<String, String> bigram = new Bigram<>(s, t);
                if (uns_bigramCountMap.containsKey(bigram)){
                    uns_bigramProbabilitiesMap.put(bigram, ((double) uns_bigramCountMap.get(bigram)/totalStartPoint));

                    if (((double) uns_bigramCountMap.get(bigram)/totalStartPoint) > 1){
                        System.out.println();
                    }

                } else {
                    uns_bigramProbabilitiesMap.put(bigram, 0d);
                }
            }
        }
    }

    public void interpolationForBiagram(){
        double lambda = 0.5d;

        for (String t1 : PartOfSpeech.tag_list){
            if (uns_bigramTransmissionProbabilitiesMap.containsKey(t1)){
                HashMap<String, Double> uns_t_prob = uns_bigramTransmissionProbabilitiesMap.get(t1);
                HashMap<String, Double> t_prob = new HashMap<String, Double>();
                for (String t2 : PartOfSpeech.tag_list){
                    double ratio = 0d;
                    if (uns_t_prob.containsKey(t2)){
                        ratio = lambda * uns_t_prob.get(t2) + (1d - lambda) * uns_tagProbabilitiesMap.get(t2);

                        if (ratio > 1){
                            System.out.println(  );
                        }

                    } else {
                        ratio = lambda * uns_tagProbabilitiesMap.get(t2);

                        if (ratio > 1){
                            System.out.println(  );
                        }
                    }
                    t_prob.put(t2, ratio);
                }
                interpolation_bigramTransmissionProbabilityMap.put(t1, t_prob);
            } else {
                HashMap<String, Double> t_prob = new HashMap<String, Double>();
                for (String t2 : PartOfSpeech.tag_list){
                    double ratio = lambda * uns_tagProbabilitiesMap.get(t2);

                    if (ratio > 1){
                        System.out.println(  );
                    }

                    t_prob.put(t2, ratio);
                }
                interpolation_bigramTransmissionProbabilityMap.put(t1, t_prob);
            }
        }
    }

    public void interpolationForTrigram() {
        double lambda_1 = 0.6d;
        double lambda_2 = 0.2d;
        double lambda_3 = 0.2d;

        for (String t1 : PartOfSpeech.tag_list){
            for (String t2 : PartOfSpeech.tag_list){
                Bigram<String, String> bigram = new Bigram<>(t1, t2);
                double biagramCount = uns_bigramCountMap.get(bigram);
                if (biagramCount != 0) {
                    HashMap<String, Double> uns_t_prob = uns_trigramTransmissionProbabilitiesMap.get(bigram);
                    HashMap<String, Double> t_prob = new HashMap<String, Double>();
                    for (String t3 : PartOfSpeech.tag_list){
                        double ratio = 0f;
                        if (uns_t_prob.containsKey(t3)){
                            ratio = lambda_1 * uns_t_prob.get(t3) + lambda_2 * interpolation_bigramTransmissionProbabilityMap.get(t2).get(t3) + lambda_3 * uns_tagProbabilitiesMap.get(t3);

                            if (ratio > 1){
                                System.out.println(  );
                            }

                        } else {
                            ratio = lambda_2 * interpolation_bigramTransmissionProbabilityMap.get(t2).get(t3) + lambda_3 * uns_tagProbabilitiesMap.get(t3);

                            if (ratio > 1){
                                System.out.println(  );
                            }

                        }
                        t_prob.put(t3, ratio);
                    }
                    interpolation_trigramTransmissionProbabilityMap.put(bigram, t_prob);
                } else {
                    HashMap<String, Double> t_prob = new HashMap<String, Double>();
                    for (String t3 : PartOfSpeech.tag_list){
                        double ratio = ratio = lambda_2 * interpolation_bigramTransmissionProbabilityMap.get(t2).get(t3) + lambda_3 * uns_tagProbabilitiesMap.get(t3);

                        if (ratio > 1){
                            System.out.println(  );
                        }

                        t_prob.put(t3, ratio);
                    }
                    interpolation_trigramTransmissionProbabilityMap.put(bigram, t_prob);
                }
            }
        }
    }

    public void interpolationForTrigram_2() {
        double lambda_1 = 0.6d;
        double lambda_2 = 0.3d;
        double lambda_3 = 0.1d;

        for (String t1 : PartOfSpeech.tag_list){
            for (String t2 : PartOfSpeech.tag_list){
                Bigram<String, String> bigram = new Bigram<>(t1, t2);
                double biagramCount = uns_bigramCountMap.get(bigram);
                if (biagramCount != 0) {
                    HashMap<String, Double> uns_t_prob = uns_trigramTransmissionProbabilitiesMap.get(bigram);
                    HashMap<String, Double> t_prob = new HashMap<String, Double>();
                    for (String t3 : PartOfSpeech.tag_list){
                        double ratio = 0f;
                        if (uns_t_prob.containsKey(t3)){
                            ratio = lambda_1 * uns_t_prob.get(t3) + lambda_2 * uns_bigramTransmissionProbabilitiesMap.get(t2).get(t3) + lambda_3 * uns_tagProbabilitiesMap.get(t3);

                            if (ratio > 1){
                                System.out.println(  );
                            }

                        } else {
                            ratio = lambda_2 * interpolation_bigramTransmissionProbabilityMap.get(t2).get(t3) + lambda_3 * uns_tagProbabilitiesMap.get(t3);

                            if (ratio > 1){
                                System.out.println(  );
                            }

                        }
                        t_prob.put(t3, ratio);
                    }
                    interpolation_trigramTransmissionProbabilityMap.put(bigram, t_prob);
                } else {
                    HashMap<String, Double> t_prob = new HashMap<String, Double>();
                    for (String t3 : PartOfSpeech.tag_list){
                        double ratio = ratio = lambda_3 * uns_tagProbabilitiesMap.get(t3);

                        if (ratio > 1){
                            System.out.println(  );
                        }

                        t_prob.put(t3, ratio);
                    }
                    interpolation_trigramTransmissionProbabilityMap.put(bigram, t_prob);
                }
            }
        }
    }
}
