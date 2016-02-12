package tagger;

import core.*;
import utils.Bigram;
import utils.PartOfSpeech;
import utils.Trigram;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ahmet on 21/01/16.
 */
public class Tagger {

    public static void main(String[] args) {

        Trainer trainer = new Trainer(System.getProperty("user.dir")+"/datas/metusabancı_train_surface");
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

        HashMap<Bigram<String, String>, Double> my_bigramCountMap = trainer.getBigramCountMap();
        HashMap<Bigram<String, String>, HashMap<String, Double>> my_trigramTransmissionPairMap = trainer.getTrigramTransmissionPairMap();
        HashMap<Bigram<String, String>, HashMap<String, Double>> my_trigramTransmissionProbabilityMap = trainer.getTrigramTransmissionProbabilityMap();


        Smoother smoother = new Smoother(System.getProperty("user.dir")+"/datas/metusabancı_test_surface_2", my_POS_tag_count, my_bigramCountMap, my_transition_prob,
                my_obs_count, my_trigramTransmissionPairMap, my_trigram, my_emission_prob, my_emission_pair_count);

        //smoother.addOne(3);
        //smoother.kneserNeySmooothing();
        //smoother.interpolationForBoth();
        smoother.interpolationSmoothingForTransitionWithTagRatioEmission();
        //smoother.tagRatioBasedEmission();

        ArrayList<String> my_unseen_suffix_list = smoother.getUnseenSuffixList();
        HashMap<String, Double> my_suffix_count_map = smoother.getLaplace_suffixCountMap();
        HashMap<String, HashMap<String, Double>> my_s_emission_pair_count = smoother.getLaplace_emissionPairMap();
        HashMap<String, HashMap<String, Double>> my_s_emission_prob_a = smoother.getLaplace_emissionProbabilitiesMap();
        HashMap<Bigram<String, String>, HashMap<String, Double>> my_s_trigramProbabilityMap_a = smoother.getLaplace_trigramTransmissionProbabilityMap();

        HashMap<String, HashMap<String, Double>> my_s_transition_prob_kn = smoother.getKneserNey_bigramTransmissionProbabilityMap();
        HashMap<Bigram<String, String>, HashMap<String, Double>> my_s_trigramProbabilityMap_kn = smoother.getKneserNey_trigramTransmissionProbabilityMap();
        HashMap<String, HashMap<String, Double>> my_s_emission_prob_kn = smoother.getKneserNey_emissionProbabilitiesMap();

        HashMap<String, HashMap<String, Double>> my_s_emission_prob_i = smoother.getInterpolation_emissionProbabilitiesMap();
        HashMap<String, HashMap<String, Double>> my_s_transition_prob_i = smoother.getInterpolation_bigramTransmissionProbabilityMap();
        HashMap<Bigram<String, String>, HashMap<String, Double>> my_s_trigramProbabilityMap_i = smoother.getInterpolation_trigramTransmissionProbabilityMap();

        ArrayList<ArrayList<String>> my_unt_sentences_suffixes = smoother.getUnTaggedSuffixesList();
        ArrayList<ArrayList<String>> generated_sentences_Tags = new ArrayList<>();


        for (ArrayList<String> a : my_unt_sentences_suffixes){
            String[] obs = a.toArray(new String[0]);
            ArrayList<String> generatedTags = TrigramViterbi.forwardViterbi(obs, PartOfSpeech.tag_list, my_start_prob, my_s_transition_prob_i, my_s_trigramProbabilityMap_i,  my_s_emission_prob_kn);
            generated_sentences_Tags.add(generatedTags);
        }

/*
        for (ArrayList<String> a : my_unt_sentences_suffixes){
            String[] obs = a.toArray(new String[0]);
            ArrayList<String> generatedTags = BigramViterbi.forwardViterbi(obs, PartOfSpeech.tag_list, my_start_prob, my_s_transition_prob_i,  my_s_emission_prob_kn);
            generated_sentences_Tags.add(generatedTags);
        }
*/

        Scorer scorer = new Scorer(System.getProperty("user.dir")+"/datas/metusabancı_tagged_test_surface_2", generated_sentences_Tags);
        double my_score = scorer.getScore();

        System.out.println("\n" + my_score);
        System.out.println("Tamamlandı");


    }

}
