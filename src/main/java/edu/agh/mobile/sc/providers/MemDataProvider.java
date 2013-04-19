package edu.agh.mobile.sc.providers;

import android.content.Context;
import android.util.Log;
import edu.agh.mobile.sc.Constants;

import java.io.*;
import java.util.*;

/**
 * @author Przemyslaw Dadel
 */
public class MemDataProvider implements DataProvider {

    private final static Set<String> interestingProperties = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList("memtotal", "memfree")));

    @Override
    public Map<String, Object> getData(Context context) {

        final Map<String, Object> result = new HashMap<String, Object>();

        final File memoryInfoFile = new File("/proc/meminfo");
        if (memoryInfoFile.exists()) {

            try {
                final BufferedReader br = new BufferedReader(new FileReader(memoryInfoFile));
                try {

                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.trim().length() > 0) {
                            final String[] split = trim(line.split(":"));
                            if (split.length >= 2) {
                                final String propertyName = split[0].toLowerCase();
                                if (isInterestingProperty(propertyName)) {
                                    result.put(propertyName, split[1]);
                                }
                            }
                        }
                    }

                } catch (IOException e) {
                    Log.e(Constants.SC_LOG_TAG, "Could not read memory info", e);
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
                Log.e(Constants.SC_LOG_TAG, "/proc/meminfo file disappeared", e);
            }
        }

        return result;


    }

    private boolean isInterestingProperty(String property) {
        return interestingProperties.contains(property);
    }

    private String[] trim(String[] split) {
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();
        }
        return split;
    }
}
