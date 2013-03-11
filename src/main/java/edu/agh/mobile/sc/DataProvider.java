package edu.agh.mobile.sc;

import android.content.Context;

import java.util.Map;

/**
 * @author Przemyslaw Dadel
 */
public interface DataProvider {

    Map<String, Object> getData(Context context);

}
