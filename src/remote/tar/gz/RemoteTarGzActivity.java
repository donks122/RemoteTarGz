package remote.tar.gz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

import remote.tar.gz.utils.SizeLimitInputStream;
import remote.tar.gz.utils.Utils;
import android.app.Activity;
import android.os.Bundle;

public class RemoteTarGzActivity extends Activity {
    /** Called when the activity is first created. */
  public static final String MOUNT = android.os.Environment.MEDIA_MOUNTED;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        String downloadPath = "REQUIRED PATH ON SDCARD";
        
        String string = "Remote TGZ url";
        try {
          
          File cacheDir;
          if (android.os.Environment.getExternalStorageState().equals(MOUNT))
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(),downloadPath);
          else
            cacheDir = this.getCacheDir();
          if (!cacheDir.exists())
            cacheDir.mkdirs();
          
          URL url = new URL(string);
          HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
          ucon.setRequestMethod("GET");
          ucon.setReadTimeout(5000);
          ucon.setConnectTimeout(5000);
          ucon.connect();

          TarInputStream taristr = new TarInputStream(
              new GZIPInputStream(
                  ucon.getInputStream()
              )
          );
          
          TarEntry tarEntry;

          while ((tarEntry = taristr.getNextEntry()) != null) {
            String filename = tarEntry.getName();
            File outputFile = new File(cacheDir, filename);

            if (tarEntry.isDirectory()) {
              if (!outputFile.exists())
                if (!outputFile.mkdirs())
                  throw new IllegalStateException(
                      String.format("Couldn't create directory %s.",outputFile.getAbsolutePath()));
            } else {
              OutputStream outputFileStream = new FileOutputStream(outputFile);
              InputStream is = new SizeLimitInputStream(taristr,tarEntry.getSize());
              Utils.CopyStream(is, outputFileStream);
              outputFileStream.close();
            }
          }
          
        }catch (Exception e) {
          // TODO: handle exception
        }
    }
}