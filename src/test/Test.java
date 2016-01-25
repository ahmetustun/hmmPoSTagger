package test;

import core.Analyser;

/**
 * Created by ahmet on 21/01/16.
 */
public class Test {

    public static void main(String[] args) {

        String corpus = "<s>/START Ahmet_∅/NOUN ev_e/NOUN gel_di/VERB";

        Analyser analyser = new Analyser(corpus);

        analyser.construct_transmission_count_matrix();
        analyser.construct_transmission_prop_matrix();
        analyser.print_matrixes();

    }

}
