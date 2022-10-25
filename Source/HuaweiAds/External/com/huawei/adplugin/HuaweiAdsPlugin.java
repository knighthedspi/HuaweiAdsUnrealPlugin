package com.huawei.adplugin;

import com.huawei.adplugin.adproxy.*;
import com.huawei.adplugin.adlistener.*;
import com.huawei.hms.ads.*;

import android.app.Activity;

public class HuaweiAdsPlugin {
    private static boolean isInit = false;
    private static Activity mActivity = null;
    private static BannerAdProxy bannerAdProxy = null;
    private static InterstitialAdProxy interstitialAdProxy = null;
    private static RewardAdProxy rewardAdProxy = null;

    public static void initialize(Activity activity) {
        if (!isInit) {
            isInit = true;
            mActivity = activity;
        }
    }

    public static void loadBannerAd(String adId, int position, String size, final IAdStatusListener adStatusListener) {
        if (mActivity == null) {
            return;
        }
        if (bannerAdProxy == null) {
            bannerAdProxy = new BannerAdProxy(mActivity, new IAdStatusListener() {
                @Override
                public void onAdClosed() {
                    if (adStatusListener != null) {
                        adStatusListener.onAdClosed();
                    }
                }

                @Override
                public void onAdFailed(int errorCode) {
                    if (adStatusListener != null) {
                        adStatusListener.onAdFailed(errorCode);
                    }
                }

                @Override
                public void onAdLeftApp() {
                    if (adStatusListener != null) {
                        adStatusListener.onAdLeftApp();
                    }
                }

                @Override
                public void onAdOpened() {
                    if (adStatusListener != null) {
                        adStatusListener.onAdOpened();
                    }
                }

                @Override
                public void onAdLoaded() {
                    showBannerAd();
                    if (adStatusListener != null) {
                        adStatusListener.onAdLoaded();
                    }
                }

                @Override
                public void onAdClicked() {
                    if (adStatusListener != null) {
                        adStatusListener.onAdClicked();
                    }
                }

                @Override
                public void onAdImpression() {
                   if (adStatusListener != null) {
                        adStatusListener.onAdImpression();
                   }     
                }
            });
        }
        bannerAdProxy.setAdId(adId);
        bannerAdProxy.setBannerAdPosition(position);
        bannerAdProxy.setAdSizeType(size);
        AdParam adParam = new AdParam.Builder().build();
        bannerAdProxy.loadAd(adParam);
    }

    public static void showBannerAd(){
        if (bannerAdProxy != null) {
            bannerAdProxy.show();
        }
    }

    public static void hideBannerAd() {
        if (bannerAdProxy != null) {
            bannerAdProxy.hide();
        }
    }

    public static void destroyBannerAd() {
        if (bannerAdProxy != null) {
            bannerAdProxy.destroy();
        }
    }

    public static void loadInterstitialAd(String adId, final IAdStatusListener adStatusListener) {
        if (mActivity == null) {
            return;
        }
        if (interstitialAdProxy == null) {
            interstitialAdProxy = new InterstitialAdProxy(mActivity);
        }
        interstitialAdProxy.setAdId(adId);
        interstitialAdProxy.setAdListener(new IAdStatusListener() {
            @Override
            public void onAdClosed() {
                if (adStatusListener != null) {
                    adStatusListener.onAdClosed();
                }
            }

            @Override
            public void onAdFailed(int errorCode) {
                if (adStatusListener != null) {
                    adStatusListener.onAdFailed(errorCode);
                }
            }

            @Override
            public void onAdLeftApp() {
                if (adStatusListener != null) {
                    adStatusListener.onAdLeftApp();
                }
            }

            @Override
            public void onAdOpened() {
                if (adStatusListener != null) {
                    adStatusListener.onAdOpened();
                }
            }

            @Override
            public void onAdLoaded() {
                showInterstitialAd();
                if (adStatusListener != null) {
                    adStatusListener.onAdLoaded();
                }
            }

            @Override
            public void onAdClicked() {
                if (adStatusListener != null) {
                    adStatusListener.onAdClicked();
                }
            }

            @Override
            public void onAdImpression() {
               if (adStatusListener != null) {
                    adStatusListener.onAdImpression();
               }     
            }
        });
        AdParam adParam = new AdParam.Builder().build();
        interstitialAdProxy.loadAd(adParam);
    }

    public static void showInterstitialAd() {
        if (interstitialAdProxy != null && interstitialAdProxy.isLoaded()) {
            interstitialAdProxy.show();
        }
    }

    public static void loadRewardAd(String adId, final IRewardAdLoadListener rewardLoadListener, final IRewardAdStatusListener rewardStatusListener) {
        if (mActivity == null) {
            return;
        }
        if (rewardAdProxy == null) {
            rewardAdProxy = new RewardAdProxy(mActivity, adId);
        }
        AdParam adParam = new AdParam.Builder().build();
        rewardAdProxy.loadAd(adParam, new IRewardAdLoadListener() {
            @Override
            public void onRewardAdFailedToLoad(final int errorCode) {
                if (rewardLoadListener != null) {
                    rewardLoadListener.onRewardAdFailedToLoad(errorCode);
                }
            }

            @Override
            public void onRewardedLoaded() {
                showRewardAd(rewardStatusListener);
                if (rewardLoadListener != null) {
                    rewardLoadListener.onRewardedLoaded();
                }      
            }
        });
    }

    public static void showRewardAd(IRewardAdStatusListener adStatusListener) {
        if (rewardAdProxy != null && rewardAdProxy.isLoaded() && mActivity != null) {
            rewardAdProxy.show(mActivity, adStatusListener);
        }
    }
}
