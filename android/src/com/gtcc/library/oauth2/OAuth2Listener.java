package com.gtcc.library.oauth2;

import android.os.Bundle;

public interface OAuth2Listener {

    public void onComplete(Bundle values);


    public void onError(OAuth2Exception e);

    
    public void onCancel();
}
