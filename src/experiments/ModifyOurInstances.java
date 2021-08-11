package experiments;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ModifyOurInstances {
    public static void main(String[] args){
        String[] extensions = new String[]{".txt"};
        List<String> files = readFilesInFolder("/Users/sergio/OneDrive - Universidad Rey Juan Carlos/Investigacion URJC/MOCD/instances/snap_rev1/snap_traducidas_rev1/", extensions);
        String outDir = "instances/lfr/forPreviousCode_rev/";
        File outDirCreator = new File(outDir);
        outDirCreator.mkdirs();
        for (String file : files) {
            File fileReaded = new File(file);
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(fileReaded));
                String line;
                br.readLine();
                String[] splits = file.split("/");
                try {
                    PrintWriter pw = new PrintWriter(outDirCreator+"/"+splits[splits.length-1]);
                    while ((line = br.readLine()) != null) {
                        String[] aux = line.split(" ");
                        int a = Integer.parseInt(aux[0]);
                        int b = Integer.parseInt(aux[1]);
                        pw.println((a+1)+" "+(b+1));
                    }
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
