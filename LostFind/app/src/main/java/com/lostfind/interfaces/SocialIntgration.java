package com.lostfind.interfaces;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by CHANDRASAIMOHAN on 2/29/2016.
 */
public interface SocialIntgration {
    public void  googleInitDone(boolean result);
    public void receiveGoogleApiClient(GoogleApiClient googleApiClient);
}
