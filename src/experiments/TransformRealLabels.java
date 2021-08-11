package experiments;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class TransformRealLabels {
    public static void main(String[] args) {
        String[] extensions = new String[]{".txt"};
        List<String> files = readFilesInFolder("instances/realworld/ground_truth", extensions);

        for (String file : files) {
            File fileReaded = new File(file);
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(fileReaded));
                String line;
                StringBuilder toWrite = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    String[] aux = line.split("\\t*\\s+");
                    int c = 1;
                    for (String s : aux) {
                        if (s.equals("")) continue;
                        toWrite.append(c).append("\t").append(s).append("\n");
                        c++;
                    }
                }

                try {
                    PrintWriter pw = new PrintWriter(file.split("\\.txt")[0] + "_rlabels.txt");
                    pw.print(toWrite);
                    pw.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private static List<String> readFilesInFolder(String folder, String[] extensions) {
        List files = null;

        try {
            files = Files.list(Paths.get(folder)).map(String::valueOf).filter((path) -> {
                int var3 = extensions.length;

                for (int var4 = 0; var4 < var3; ++var4) {
                    String ext = extensions[var4];
                    if (path.substring(Math.max(path.lastIndexOf("/"), path.lastIndexOf("\\")) + 1).startsWith(".")) {
                        return false;
                    }

                    if (path.endsWith(ext) || path.matches(ext)) {
                        return true;
                    }
                }

                return false;
            }).sorted().collect(Collectors.toList());
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return files;
    }
}
