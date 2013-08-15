package com.gtcc.library.oauth2;

import android.os.Bundle;

public interface AuthListener {

    public void onComplete(Bundle values);


    public void onError(AuthException e);

    
    public void onCancel();
}
