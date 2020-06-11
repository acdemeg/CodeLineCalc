import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;


public class CalcCode {
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_RED = "\u001B[31m";
  private static final String ANSI_GREEN = "\u001B[32m";
  private static final String ANSI_CYAN = "\u001B[36m";
  private static final String ANSI_RED_BRIGHT = "\u001B[31;1m";
  private static final String ANSI_GREEN_BRIGHT = "\u001B[32;1m";
  private static final String ANSI_YELLOW_BRIGHT = "\u001B[33;1m";
  private static final String ANSI_PURPLE_BRIGHT = "\u001B[35;1m";
  private static final String ANSI_CYAN_BRIGHT = "\u001B[36;1m";
  private static final String ANSI_BLUE_BRIGHT = "\u001b[34;1m";

  private static final String[] colors = { 
    ANSI_RED_BRIGHT, 
    ANSI_YELLOW_BRIGHT, 
    ANSI_BLUE_BRIGHT, 
    ANSI_PURPLE_BRIGHT,
    ANSI_GREEN_BRIGHT, 
    ANSI_CYAN_BRIGHT 
  };

  private static int numberColor = 0;
  private static ArrayList<String> files = new ArrayList<String>();

  public static void main(final String[] args) throws Exception {
    Scanner in = new Scanner(System.in); 

    String directory;
    String[] exludeDirectories;
    File folder;
    while(true){
      System.out.println(ANSI_GREEN + "Enter root directory(absolute path) without last symbol '/' " ); 
      System.out.print(ANSI_GREEN +  "> ");
      directory = in.nextLine();
      folder = new File(directory);
      if(!folder.isDirectory()){
        System.out.println(ANSI_RED + "Needed enter valid directory!"); 
        System.out.print(ANSI_GREEN + "> ");
        continue;
      }
      break;
    }
    System.out.println(ANSI_CYAN + "Enter exlude directories(relativ pointed upper) either press Enter, use delimiter ',' " ); 
    System.out.print(ANSI_CYAN + "> ");
    exludeDirectories = in.nextLine().split(",");
    System.out.print(ANSI_RESET);
    in.close(); 

    final String dir = directory;
    exludeDirectories = Arrays.stream(exludeDirectories).map(s -> dir + "/" + s).toArray(String[]::new);
     
    ArrayList<String> paths = listFilesForFolder(folder, exludeDirectories);
    printCountLine(paths);
  }

  private static void printCountLine(final ArrayList<String> paths) throws IOException {
    final String[] filesExts = paths.stream().map(file -> getExtFile(file)).toArray(String[]::new);
    final Set<String> extetensions = new HashSet<>(Arrays.asList(filesExts));

    int commonLines = 0;
    for(String ext : extetensions){
      ArrayList<FileInputStream> fileStreamsByExt = new ArrayList<>();
      for (String path : paths) {
        if(ext.equals(getExtFile(path))){
          fileStreamsByExt.add(new FileInputStream(path));
        }
      }
      Enumeration<FileInputStream> streams = Collections.enumeration(fileStreamsByExt);
      SequenceInputStream allStreamsByExt = new SequenceInputStream(streams);
      BufferedReader reader = new BufferedReader(new InputStreamReader(allStreamsByExt));
      int lines = 0;
      String line;
      while ((line = reader.readLine()) != null) {
        if(!line.isEmpty())
          lines++;
      }
      commonLines += lines;
      reader.close();
      System.out.println(getColor() + ext + " = " + lines);
    }    
    System.out.println(ANSI_RESET + "Common code line count = " + commonLines);
  }

  private static ArrayList<String> listFilesForFolder(final File folder, String[] exludeDir) {
    for (File fileEntry : folder.listFiles()) {
      if (fileEntry.isDirectory()) {
        if(Arrays.asList(exludeDir).contains(fileEntry.getPath())){
          continue;
        }
        listFilesForFolder(fileEntry, exludeDir);
      } 
      else files.add(fileEntry.getPath());
    }
    return files;
  }

  private static String getExtFile(String filename) {
    Optional<String> res =  Optional.ofNullable(filename)
      .filter(f -> f.contains("."))
      .map(f -> f.substring(filename.lastIndexOf(".") + 1));
      if (res.isPresent()) {
        return res.get();
      }
      return ""; 
  }

  private static String getColor(){
    if(numberColor == colors.length - 1){
      numberColor = 0;
      return colors[numberColor];
    }
    numberColor += 1;
    return colors[numberColor];
  }

}
