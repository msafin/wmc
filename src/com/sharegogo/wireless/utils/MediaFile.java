package com.sharegogo.wireless.utils;

import java.util.HashMap;
import java.util.Iterator;

/**
* MediaScanner helper class.
*/
public class MediaFile {
   // comma separated list of all file extensions supported by the media scanner
   public final static String sFileExtensions;

   // Audio file types
   public static final int FILE_TYPE_MP3     = 101;
   public static final int FILE_TYPE_M4A     = 102;
   public static final int FILE_TYPE_WAV     = 103;
   public static final int FILE_TYPE_AMR     = 104;
   public static final int FILE_TYPE_AWB     = 105;
   public static final int FILE_TYPE_WMA     = 106;
   public static final int FILE_TYPE_OGG     = 107;
   public static final int FILE_TYPE_AAC     = 108;
   public static final int FILE_TYPE_MKA     = 109;
   public static final int FILE_TYPE_MP2     = 197;
   public static final int FILE_TYPE_3GPP3   = 199;
   
   private static final int FIRST_AUDIO_FILE_TYPE = FILE_TYPE_MP3;
   private static final int LAST_AUDIO_FILE_TYPE = FILE_TYPE_3GPP3;

   // MIDI file types
   public static final int FILE_TYPE_MID     = 201;
   public static final int FILE_TYPE_SMF     = 202;
   public static final int FILE_TYPE_IMY     = 203;
   private static final int FIRST_MIDI_FILE_TYPE = FILE_TYPE_MID;
   private static final int LAST_MIDI_FILE_TYPE = FILE_TYPE_IMY;
  
   // Video file types
   public static final int FILE_TYPE_MP4     = 301;
   public static final int FILE_TYPE_M4V     = 302;
   public static final int FILE_TYPE_3GPP    = 303;
   public static final int FILE_TYPE_3GPP2   = 304;
   public static final int FILE_TYPE_WMV     = 305;
   public static final int FILE_TYPE_ASF     = 306;
   public static final int FILE_TYPE_MKV     = 307;
   public static final int FILE_TYPE_MP2TS   = 308;
   public static final int FILE_TYPE_RMVB   = 309;
   public static final int FILE_TYPE_AVI   = 310;
   public static final int FILE_TYPE_MOV   = 311;
   private static final int FIRST_VIDEO_FILE_TYPE = FILE_TYPE_MP4;
   private static final int LAST_VIDEO_FILE_TYPE = FILE_TYPE_MOV;
   
   // Image file types
   public static final int FILE_TYPE_JPEG    = 401;
   public static final int FILE_TYPE_GIF     = 402;
   public static final int FILE_TYPE_PNG     = 403;
   public static final int FILE_TYPE_BMP     = 404;
   public static final int FILE_TYPE_WBMP    = 405;
   public static final int FILE_TYPE_MPO     = 406;
   private static final int FIRST_IMAGE_FILE_TYPE = FILE_TYPE_JPEG;
//   private static final int LAST_IMAGE_FILE_TYPE = FILE_TYPE_WBMP;
   private static final int LAST_IMAGE_FILE_TYPE = FILE_TYPE_MPO;
  
   // Playlist file types
   public static final int FILE_TYPE_M3U     = 501;
   public static final int FILE_TYPE_PLS     = 502;
   public static final int FILE_TYPE_WPL     = 503;
   private static final int FIRST_PLAYLIST_FILE_TYPE = FILE_TYPE_M3U;
   private static final int LAST_PLAYLIST_FILE_TYPE = FILE_TYPE_WPL;
   
   public static class MediaFileType {
   
       public int fileType;
       public String mimeType;
       
       MediaFileType(int fileType, String mimeType) {
           this.fileType = fileType;
           this.mimeType = mimeType;
       }
   }
   
   private static HashMap<String, MediaFileType> sFileTypeMap 
           = new HashMap<String, MediaFileType>();
   private static HashMap<String, Integer> sMimeTypeMap 
           = new HashMap<String, Integer>();            
   static void addFileType(String extension, int fileType, String mimeType) {
       sFileTypeMap.put(extension, new MediaFileType(fileType, mimeType));
       sMimeTypeMap.put(mimeType, Integer.valueOf(fileType));
   }

   static {
       addFileType("MP3", FILE_TYPE_MP3, "audio/mpeg");
       addFileType("M4A", FILE_TYPE_M4A, "audio/mp4");
       addFileType("WAV", FILE_TYPE_WAV, "audio/x-wav");
       addFileType("AMR", FILE_TYPE_AMR, "audio/amr");
       addFileType("AWB", FILE_TYPE_AWB, "audio/amr-wb");

       // for media file whose MIME type is 'audio/3gpp'
       addFileType("3gp", FILE_TYPE_3GPP3, "audio/3gpp");
       addFileType("MP2", FILE_TYPE_MP2, "audio/mpeg");

      
       addFileType("WMA", FILE_TYPE_WMA, "audio/x-ms-wma");
       addFileType("OGG", FILE_TYPE_OGG, "application/ogg");
       addFileType("OGA", FILE_TYPE_OGG, "application/ogg");
       addFileType("AAC", FILE_TYPE_AAC, "audio/aac");
       addFileType("MKA", FILE_TYPE_MKA, "audio/x-matroska");

       addFileType("MID", FILE_TYPE_MID, "audio/midi");
       addFileType("MIDI", FILE_TYPE_MID, "audio/midi");
       addFileType("XMF", FILE_TYPE_MID, "audio/midi");
       addFileType("RTTTL", FILE_TYPE_MID, "audio/midi");
       addFileType("SMF", FILE_TYPE_SMF, "audio/sp-midi");
       addFileType("IMY", FILE_TYPE_IMY, "audio/imelody");
       addFileType("RTX", FILE_TYPE_MID, "audio/midi");
       addFileType("OTA", FILE_TYPE_MID, "audio/midi");
       
       addFileType("MPEG", FILE_TYPE_MP4, "video/mpeg");
       addFileType("MP4", FILE_TYPE_MP4, "video/mp4");
       addFileType("M4V", FILE_TYPE_M4V, "video/mp4");
       addFileType("3GP", FILE_TYPE_3GPP, "video/3gpp");
       addFileType("3GPP", FILE_TYPE_3GPP, "video/3gpp");
       addFileType("3G2", FILE_TYPE_3GPP2, "video/3gpp2");
       addFileType("3GPP2", FILE_TYPE_3GPP2, "video/3gpp2");
       addFileType("MKV", FILE_TYPE_MKV, "video/x-matroska");
       addFileType("WEBM", FILE_TYPE_MKV, "video/x-matroska");
       addFileType("TS", FILE_TYPE_MP2TS, "video/mp2ts");
       addFileType("WMV", FILE_TYPE_WMV, "video/wmv");
     
       addFileType("WMV", FILE_TYPE_WMV, "video/x-ms-wmv");
       addFileType("ASF", FILE_TYPE_ASF, "video/x-ms-asf");
       addFileType("RMVB", FILE_TYPE_RMVB, "video/rmvb");
       addFileType("AVI", FILE_TYPE_AVI, "video/avi");
       addFileType("MOV", FILE_TYPE_MOV, "video/mov");
       
       addFileType("JPG", FILE_TYPE_JPEG, "image/jpeg");
       addFileType("JPEG", FILE_TYPE_JPEG, "image/jpeg");
       addFileType("GIF", FILE_TYPE_GIF, "image/gif");
       addFileType("PNG", FILE_TYPE_PNG, "image/png");
       addFileType("BMP", FILE_TYPE_BMP, "image/x-ms-bmp");
       addFileType("WBMP", FILE_TYPE_WBMP, "image/vnd.wap.wbmp");
       addFileType("MPO", FILE_TYPE_MPO, "image/mpo");

       addFileType("M3U", FILE_TYPE_M3U, "audio/x-mpegurl");
       addFileType("PLS", FILE_TYPE_PLS, "audio/x-scpls");
       addFileType("WPL", FILE_TYPE_WPL, "application/vnd.ms-wpl");

       // compute file extensions list for native Media Scanner
       StringBuilder builder = new StringBuilder();
       Iterator<String> iterator = sFileTypeMap.keySet().iterator();
       
       while (iterator.hasNext()) {
           if (builder.length() > 0) {
               builder.append(',');
           }
           builder.append(iterator.next());
       } 
       sFileExtensions = builder.toString();
   }
   
   public static boolean isAudioFileType(int fileType) {
       return ((fileType >= FIRST_AUDIO_FILE_TYPE &&
               fileType <= LAST_AUDIO_FILE_TYPE) ||
               (fileType >= FIRST_MIDI_FILE_TYPE &&
               fileType <= LAST_MIDI_FILE_TYPE));
   }
   
   public static boolean isVideoFileType(int fileType) {
       return (fileType >= FIRST_VIDEO_FILE_TYPE &&
               fileType <= LAST_VIDEO_FILE_TYPE);
   }
   
   public static boolean isImageFileType(int fileType) {
       return (fileType >= FIRST_IMAGE_FILE_TYPE &&
               fileType <= LAST_IMAGE_FILE_TYPE);
   }
   
   public static boolean isPlayListFileType(int fileType) {
       return (fileType >= FIRST_PLAYLIST_FILE_TYPE &&
               fileType <= LAST_PLAYLIST_FILE_TYPE);
   }
   
   public static MediaFileType getFileType(String path) {
       int lastDot = path.lastIndexOf(".");
       if (lastDot < 0)
           return null;
       return sFileTypeMap.get(path.substring(lastDot + 1).toUpperCase());
   }
   
   public static int getFileTypeForMimeType(String mimeType) {
       Integer value = sMimeTypeMap.get(mimeType);
       return (value == null ? 0 : value.intValue());
   }

   public static int getFileTypeBySuffix(String filename){
       MediaFileType mdeiaFileType = getFileType(filename);
       if(null == mdeiaFileType){
           return -1;
       }
       return mdeiaFileType.fileType;
   }

   public static String getMimeTypeBySuffix(String filename){
       MediaFileType mdeiaFileType = getFileType(filename);
       if(null == mdeiaFileType){
           return null;
       }
       return mdeiaFileType.mimeType;
   }

}
