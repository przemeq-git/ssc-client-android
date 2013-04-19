package edu.agh.mobile.sc.commands;

import android.content.Context;
import android.os.Bundle;

/**
 * @author Przemyslaw Dadel
 */
public interface Command {

    boolean accepts(Bundle extras);

    void execute(Context context, Bundle extras);
}
