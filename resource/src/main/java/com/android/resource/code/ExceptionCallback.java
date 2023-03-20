package com.android.resource.code;

import java.io.IOException;
import org.json.JSONException;

/**
 * Created by android on 17/10/17.
 */

public abstract class ExceptionCallback {
    public abstract void onIOException(IOException e);
    public abstract void onJSONException(JSONException e);
}
