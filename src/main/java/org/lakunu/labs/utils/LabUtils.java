package org.lakunu.labs.utils;

import com.google.common.collect.ImmutableList;
import org.lakunu.labs.LabOutputHandler;

import java.util.stream.Collector;

public class LabUtils {

    public static void outputTitle(String title, LabOutputHandler outputHandler) {
        outputHandler.info("");
        outputHandler.info("------------------------------------------------------------------------");
        outputHandler.info(title);
        outputHandler.info("------------------------------------------------------------------------");
        outputHandler.info("");
    }

    public static <T> Collector<T, ImmutableList.Builder<T>, ImmutableList<T>> immutableList() {
        return Collector.of(ImmutableList.Builder::new, ImmutableList.Builder::add,
                (l, r) -> l.addAll(r.build()), ImmutableList.Builder<T>::build);
    }

}
