package experiments;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import structure.MOCDInstance;
import structure.MOCDSolution;
import structure.Pareto;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class EvaluateRSolution {
    public static void main(String[] args) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        String[] dirsToEvaluate = new String[]{""};
        for (String nameDir : dirsToEvaluate) {
            Pareto.reset();
            String[] extensions = new String[]{".txt"};
            List<String> files = readFilesInFolder("/Users/sergio/OneDrive - Universidad Rey Juan Carlos/Investigacion URJC/MOCD/Rev_1/ExactosPrevios/results" + nameDir +"/", extensions);
            int rowCount = 0;
            Row row = sheet.createRow(rowCount);
            int columnCount = 0;
            String[] algorithms = {"EB", "FG", "LP", "LE", "ML", "WT", "IM", "CL"};
            for (int i = 0; i < 8; i++) {
                Cell cell = row.createCell(++columnCount);
                cell.setCellValue(algorithms[i]);
            }
            for (String file : files) {
                columnCount = -1;
                File fileReaded = new File(file);
                BufferedReader br;
                row = sheet.createRow(++rowCount);
                Cell cell = row.createCell(++columnCount);
                cell.setCellValue(file);
                try {
                    br = new BufferedReader(new FileReader(fileReaded));
                    String line;
                    MOCDInstance instance;
                    if (file.contains("chinos")){
                        if (file.contains("500")){
                            instance = new MOCDInstance("instances/chinos/"+file.substring(file.lastIndexOf('/')));
                        }else{
                            instance = new MOCDInstance("instances/chinos_1000/"+file.substring(file.lastIndexOf('/')));
                        }
                    }else if (file.contains("500") || file.contains("1000")){
                        instance = new MOCDInstance("instances/lfr/"+file.substring(file.lastIndexOf('/')));
                    }else{
                        instance = new MOCDInstance("instances/realworld/"+file.substring(file.lastIndexOf('/')));

                    }
                    br.readLine();
                    while ((line = br.readLine()) != null) {
                        MOCDSolution sol = new MOCDSolution(instance);
                        for (int i = 0; i < 4; i++) {
                            br.readLine();
                        }
                        line = br.readLine();
                        String[] nodesAndComms = line.split(" ");
                        for (int i = 0; i < nodesAndComms.length-1; i++) {
                            sol.addNodeToCluster(i, Integer.parseInt(nodesAndComms[i]));
                        }
                        sol.updateEdges();
                        cell = row.createCell(++columnCount);
                        cell.setCellValue(sol.getModularity());
                        for (int i = 0; i < 4; i++) {
                            br.readLine();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("Fichero " + fileReaded + " procesado.");
            }
            try (FileOutputStream outputStream = new FileOutputStream("experiments/ExactPrevious.xlsx")) {
                workbook.write(outputStream);
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
