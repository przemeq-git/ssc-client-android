package edu.agh.mobile.sc;

import android.content.Context;
import android.util.Log;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Przemyslaw Dadel
 */
public class CPUDataProvider implements DataProvider {

    @Override
    public Map<String, Object> getData(Context context) {

        final Map<String, Object> result = new HashMap<String, Object>();

        final File cpuInfoFile = new File("/proc/cpuinfo");
        if (cpuInfoFile.exists()) {

            try {
                final BufferedReader br = new BufferedReader(new FileReader(cpuInfoFile));
                try {

                    String line;
                    while ((line = br.readLine()) != null) {
                        Log.d(Constants.SC_LOG_TAG, "CPU: " + line);
                        String[] split = trim(line.split(":"));
                        if (split.length >= 2) {
                            result.put(split[0], split[1]);
                        }
                    }

                } catch (IOException e) {
                    Log.e(Constants.SC_LOG_TAG, "Could not read CPU INFO", e);
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            Log.e(Constants.SC_LOG_TAG, "Could not close the stream", e);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                //should not happen
                Log.e(Constants.SC_LOG_TAG, "/proc/cpufile file disappeared", e);
            }
        }

        return result;


    }

    private String[] trim(String[] split) {
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();
        }
        return split;
    }
}
