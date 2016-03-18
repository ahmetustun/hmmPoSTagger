package tagger;

import core.*;
import utils.Bigram;
import utils.PartOfSpeech;
import utils.Trigram;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ahmet on 21/01/16.
 */

public class Tagger {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

        String train = "/datas/metu/5K_train_ig_with_punc";
        String test = "/datas/metu/1K_test_ig_with_punc";
        String compare = "/datas/metu/1K_compare_ig_with_punc";
        boolean isTnT = true;
        boolean report = false;

        Trainer trainer = new Trainer(System.getProperty("user.dir")+train);
        trainer.analyse(3);

        HashMap<String, Double> my_start_count = trainer.getStartCountMap();
        HashMap<String, Double> my_POS_tag_count = trainer.getTagCountMap();
        HashMap<String, Double> my_obs_count = trainer.getSuffixCountMap();
        HashMap<String, HashMap<String, Double>> my_transmission_pair_count = trainer.getBigramTransmissionPairMap();
        HashMap<String, HashMap<String, Double>> my_emission_pair_count = trainer.getEmissionPairMap();
        HashMap<String, Double> my_start_prob = trainer.getStartProbabilitiesMap();
        HashMap<String, HashMap<String, Double>> my_transition_prob = trainer.getBigramTransmissionProbabilitiesMap();
        HashMap<String, HashMap<String, Double>> my_emission_prob = trainer.getEmissionProbabilitiesMap();
        HashMap<Trigram<String, String, String>, Double> my_trigram = trainer.getTrigramCountMap();
        HashMap<Bigram<String, String>, Double> my_stopProbForTrigram = trainer.getStopProbabilityMapForTrigram();

        HashMap<Bigram<String, String>, Double> my_bigramCountMap = trainer.getBigramCountMap();
        HashMap<Bigram<String, String>, HashMap<String, Double>> my_trigramTransmissionPairMap = trainer.getTrigramTransmissionPairMap();
        HashMap<Bigram<String, String>, HashMap<String, Double>> my_trigramTransmissionProbabilityMap = trainer.getTrigramTransmissionProbabilityMap();

        if (report) {
            System.out.println("*********************** Before Smoothing ***********************");
            Reporter.reportTagCount(my_POS_tag_count);
            Reporter.reportTagRatio(my_POS_tag_count);
            Reporter.reportSuffixCount(my_obs_count);
            Reporter.reportSuffixRatio(my_obs_count);
            Reporter.reportStartProbabilities(my_start_prob);
            Reporter.reportStopProbabilitiesForTrigram(my_stopProbForTrigram);
            Reporter.reportBiagramTransitionProbabilities(my_transition_prob);
            Reporter.reportTrigramTransitionProbabilities(my_trigramTransmissionProbabilityMap);
            Reporter.reportEmissionProbabilities(my_emission_prob);
            System.out.println("****************************************************************");
        }

        Smoother smoother = new Smoother(System.getProperty("user.dir")+test, my_POS_tag_count, my_start_prob, my_bigramCountMap, my_transition_prob,
                my_obs_count, my_trigramTransmissionPairMap, my_trigramTransmissionProbabilityMap, my_trigram, my_emission_prob, my_emission_pair_count);


        double max_score = 0d;

//        for (int e=1; e<10; e++){
//            for (int s=1; s<10; s++){
//                for (int b=1; b<10; b++){
//                    for (int t1=1; t1<10; t1++){



                            //smoother.addOne(3);
                            //smoother.kneserNeySmooothing();
                            smoother.interpolationForBoth();
                            //smoother.interpolationSmoothingForTransitionWithTagRatioEmission();
                            //smoother.tagRatioBasedEmission();

                            ArrayList<String> my_unseen_suffix_list = smoother.getUnseenSuffixList();
                            HashMap<String, Double> my_suffix_count_map = smoother.getLaplace_suffixCountMap();
                            HashMap<String, Double> my_s_start_prob = smoother.getInterpolation_startProbabilitiesMap();
                            HashMap<String, HashMap<String, Double>> my_s_emission_pair_count = smoother.getLaplace_emissionPairMap();
                            HashMap<String, HashMap<String, Double>> my_s_emission_prob_a = smoother.getLaplace_emissionProbabilitiesMap();
                            HashMap<Bigram<String, String>, HashMap<String, Double>> my_s_trigramProbabilityMap_a = smoother.getLaplace_trigramTransmissionProbabilityMap();

                            HashMap<String, HashMap<String, Double>> my_s_transition_prob_kn = smoother.getKneserNey_bigramTransmissionProbabilityMap();
                            HashMap<Bigram<String, String>, HashMap<String, Double>> my_s_trigramProbabilityMap_kn = smoother.getKneserNey_trigramTransmissionProbabilityMap();
                            HashMap<String, HashMap<String, Double>> my_s_emission_prob_kn = smoother.getKneserNey_emissionProbabilitiesMap();

                            HashMap<String, HashMap<String, Double>> my_s_emission_prob_i = smoother.getInterpolation_emissionProbabilitiesMap();
                            HashMap<String, HashMap<String, Double>> my_s_transition_prob_i = smoother.getInterpolation_bigramTransmissionProbabilityMap();
                            HashMap<Bigram<String, String>, HashMap<String, Double>> my_s_trigramProbabilityMap_i = smoother.getInterpolation_trigramTransmissionProbabilityMap();

                            if (report) {
                                System.out.println("*********************** After Smoothing ***********************");
                                Reporter.reportStartProbabilities(my_s_start_prob);
                                Reporter.reportBiagramTransitionProbabilities(my_s_transition_prob_i);
                                Reporter.reportTrigramTransitionProbabilities(my_s_trigramProbabilityMap_i);
                                Reporter.reportEmissionProbabilities(my_s_emission_prob_i);
                                System.out.println("****************************************************************");
                            }

                            ArrayList<ArrayList<String>> my_unt_sentences_suffixes = smoother.getUnTaggedSuffixesList();
                            ArrayList<ArrayList<String>> generated_sentences_Tags = new ArrayList<>();


                            if (!isTnT){
                                for (ArrayList<String> a : my_unt_sentences_suffixes){
                                    String[] obs = a.toArray(new String[0]);
                                    ArrayList<String> generatedTags = BigramViterbi.forwardViterbi(obs, PartOfSpeech.tag_list, my_s_start_prob, my_s_transition_prob_i,  my_s_emission_prob_i);
                                    generated_sentences_Tags.add(generatedTags);
                                }
                            } else {
                                for (ArrayList<String> a : my_unt_sentences_suffixes){
                                    String[] obs = a.toArray(new String[0]);
                                    ArrayList<String> generatedTags = TrigramViterbi.forwardViterbi(obs, PartOfSpeech.tag_list, my_s_start_prob, my_s_transition_prob_i, my_s_trigramProbabilityMap_i,  my_s_emission_prob_i);
                                    generated_sentences_Tags.add(generatedTags);
                                }
                            }

                            Scorer scorer = new Scorer(System.getProperty("user.dir")+compare, generated_sentences_Tags);
                            double my_score = scorer.getScore();

                            if (my_score > max_score){
                                max_score = my_score;
                            }
//                    }
//                }
//            }
//        }

        System.out.println("\n" + max_score);
        System.out.println("Tamamlandı");


    }

}
