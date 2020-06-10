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
import java.util.Optional;
import java.util.Scanner;

public class CalcCodeLine {
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_RED = "\u001B[31m";
  private static final String ANSI_GREEN = "\u001B[32m";
  private static final String ANSI_YELLOW = "\u001B[33m";
  private static final String ANSI_PURPLE = "\u001B[35m";
  private static final String ANSI_CYAN = "\u001B[36m";

  private static ArrayList<String> files = new ArrayList<String>();

  public static void main(final String[] args) throws Exception {
    Scanner in = new Scanner(System.in); 
  
    System.out.println(ANSI_YELLOW + "Enter types extensions of files which need calculate use delimiter ',' "); 
    System.out.println(ANSI_YELLOW + "Enter '*' for counting all files " ); 
    System.out.print(ANSI_YELLOW + "> ");
    String[] extensions = in.nextLine().split(",");
    String directory;
    String[] exludeDirectories;
    File folder;
    while(true){
      System.out.println(ANSI_GREEN + "Enter root directory" ); 
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
    System.out.println(ANSI_CYAN + "Enter exlude directories either press Enter, use delimiter ',' " ); 
    System.out.print(ANSI_CYAN + "> ");
    exludeDirectories = in.nextLine().split(",");
    in.close(); 
     
    ArrayList<String> paths = listFilesForFolder(folder, extensions, exludeDirectories);
    
   System.out.println(ANSI_PURPLE + "Common code line count = " + ANSI_RESET + getCountLine(paths));
  }

  private static int getCountLine(final ArrayList<String> paths) throws IOException {
    final ArrayList<FileInputStream> fileStreams = new ArrayList<>();

    for (String path : paths) {
      fileStreams.add(new FileInputStream(path));
    }

    final Enumeration<FileInputStream> streams = Collections.enumeration(fileStreams);
    final SequenceInputStream allStreams = new SequenceInputStream(streams);
    final BufferedReader reader = new BufferedReader(new InputStreamReader(allStreams));
    
    int lines = 0;
    String line;
    while ((line = reader.readLine()) != null) {
      if(!line.isEmpty())
        lines++;
    }
    reader.close();
    return lines;
  }

  private static ArrayList<String> listFilesForFolder(final File folder, String[] extensions, String[] exludeDir) {
    for (File fileEntry : folder.listFiles()) {
      if (fileEntry.isDirectory()) {
        if(Arrays.asList(exludeDir).contains(fileEntry.getPath())){
          continue;
        }
        listFilesForFolder(fileEntry, extensions, exludeDir);
      } 
      else if(checkExtFile(fileEntry.getName(), extensions)){
        files.add(fileEntry.getPath());
      }
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

  private static boolean checkExtFile(String filename, String[] extensions) {
    if(extensions[0].equals("*")){
      return true;
    }
    if(Arrays.asList(extensions).contains(getExtFile(filename))){
      return true;
    }
    return false;
  }

}
