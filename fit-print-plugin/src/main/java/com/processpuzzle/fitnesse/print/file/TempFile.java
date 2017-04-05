package com.processpuzzle.fitnesse.print.file;

import java.util.UUID;

public class TempFile {
   private final String fileExtension;
   private String systemTempFolder;
   private String tempFilePath;
   
   // constructors
   public TempFile( String fileExtension ){
      this.fileExtension = fileExtension;
      determineSystemTempFolder();
      generateTempFilePath();
   }
   
   // public accessors and mutators
   public void save( String retrievePage ) {
   }
   
   // properties
   // @formatter:off
   public String getPath() { return this.tempFilePath; }
   public String getSystemTempFolder() { return systemTempFolder; }
   // @formatter:on

   // protected, private helper methods
   private void determineSystemTempFolder(){
      systemTempFolder = System.getProperty("java.io.tmpdir");      
   }
   
   private void generateTempFilePath() {
      UUID uniqueID = UUID.randomUUID();
      this.tempFilePath = this.systemTempFolder + "/" + uniqueID.toString() + this.fileExtension;
   }

}
