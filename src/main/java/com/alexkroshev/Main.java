package com.alexkroshev;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

        Options options = new Options();

        Option asc = new Option("a", null, false, "ascending type of ordering");
        asc.setRequired(false);
        options.addOption(asc);

        Option desc = new Option("d", null, false, "descending type of ordering");
        desc.setRequired(false);
        options.addOption(desc);

        Option ints = new Option("i", null, false, "integers input");
        ints.setRequired(false);
        options.addOption(ints);

        Option strings = new Option("s", null, false, "strings input");
        strings.setRequired(false);
        options.addOption(strings);

        DefaultParser parser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            helpFormatter.printHelp("merger", options);
            System.exit(1);
        }

        Ordering ordering = Ordering.ASC;
        if (cmd.hasOption("d"))
            ordering = Ordering.DESC;

        DataType dataType = null;
        if (cmd.hasOption("i") && cmd.hasOption("s")) {
            System.out.println("both flags -s and -i are forbidden");
            System.exit(1);
        }
        if (!cmd.hasOption("i") && !cmd.hasOption("s")) {
            System.out.println("either -s or -i has to be specified");
            System.exit(1);
        }
        if (cmd.hasOption("i"))
            dataType = DataType.INTEGER;
        if (cmd.hasOption("s"))
            dataType = DataType.STRING;

        List<String> remainingArgs = cmd.getArgList();
        if (remainingArgs.size() < 2) {
            System.out.println("you have to specify output file and at least one input file");
            System.exit(1);
        }
        String output = remainingArgs.get(0);
        remainingArgs.remove(0);
        ArrayList<DataIterator> dataIterators = new ArrayList<>();
        for (String input : remainingArgs) {
            dataIterators.add(new DataIterator(ordering, dataType, input));
        }

        new Merge(
                dataIterators.toArray(DataIterator[]::new),
                Files.newBufferedWriter(Path.of(output)),
                Files.newBufferedWriter(Path.of("error.txt"))
        ).merge();
    }
}
