
package com.huawei.adplugin.adproxy;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.banner.BannerView;
import com.huawei.adplugin.Const.BannerAdPositionCode;
import com.huawei.adplugin.Const.BannerAdSize;
import com.huawei.adplugin.adlistener.IAdStatusListener;

public class BannerAdProxy extends AdListener {
    private static final int DEFAULT_WIDTH = 320;

    private Activity mActivity;

    private BannerView mBannerView;

    private IAdStatusListener mAdListener;

    private Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

    private String mAdId;

    private String mAdSizeType = BannerAdSize.USER_DEFINED;

    private int mPositionCode = BannerAdPositionCode.POSITION_TOP;

    private int mHorizontalOffset = 0;

    private int mVerticalOffset = 0;

    private boolean mIsHide = false;

    private int mCustomWidth = DEFAULT_WIDTH;

    public BannerAdProxy(Activity activity, IAdStatusListener listener) {
        mActivity = activity;
        mAdListener = listener;
    }

    public void setAdId(String adId) {
        mAdId = adId;
    }

    public void setAdSizeType(String adSizeType) {
        mAdSizeType = adSizeType;
    }

    public void loadAd(final AdParam adRequest) {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mBannerView == null) {
                    mBannerView = new BannerView(mActivity);
                    mBannerView.setBackgroundColor(Color.TRANSPARENT);
                    mBannerView.setVisibility(View.GONE);
                    mBannerView.setAdListener(BannerAdProxy.this);
                    mActivity.addContentView(mBannerView, getBannerViewLayoutParams());
                }
                mBannerView.setAdId(mAdId);
                mBannerView.setBannerAdSize(getTargetBannerAdSize(mAdSizeType));
                if (BannerAdSize.BANNER_SIZE_INVALID.equals(mBannerView.getBannerAdSize())) {
                    return;
                }

                if (TextUtils.isEmpty(mBannerView.getAdId())) {
                    return;
                }
                mBannerView.loadAd(adRequest);
            }
        });
    }

    public void setCustomSize(int size) {
        mCustomWidth = size;
    }

    public void show() {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                mIsHide = false;
                if (mBannerView != null) {
                    mBannerView.resume();
                    mBannerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void hide() {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                mIsHide = true;
                if (mBannerView != null) {
                    mBannerView.pause();
                    mBannerView.setVisibility(View.GONE);
                }
            }
        });
    }

    public void destroy() {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mBannerView != null) {
                    mBannerView.destroy();
                    mBannerView.setVisibility(View.GONE);
                    ViewParent parentView = mBannerView.getParent();
                    if (parentView instanceof ViewGroup) {
                        ((ViewGroup) parentView).removeView(mBannerView);
                    }
                }
                mBannerView = null;
            }
        });
    }

    public void setBannerAdPosition(final int positionX, final int positionY) {
        mPositionCode = BannerAdPositionCode.POSITION_CUSTOM;
        mHorizontalOffset = positionX;
        mVerticalOffset = positionY;
        updatePosition();
    }

    public void setBannerAdPosition(final int positionCode) {
        mPositionCode = positionCode;
        mHorizontalOffset = 0;
        mVerticalOffset = 0;
        updatePosition();
    }

    @Override
    public void onAdClosed() {
        super.onAdClosed();
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mAdListener != null) {
                    mAdListener.onAdClosed();
                }
            }
        });
    }

    @Override
    public void onAdFailed(final int errorCode) {
        super.onAdFailed(errorCode);
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mAdListener != null) {
                    mAdListener.onAdFailed(errorCode);
                }
            }
        });
    }

    @Override
    public void onAdLeave() {
        super.onAdLeave();
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mAdListener != null) {
                    mAdListener.onAdLeftApp();
                }
            }
        });
    }

    @Override
    public void onAdOpened() {
        super.onAdOpened();
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mAdListener != null) {
                    mAdListener.onAdOpened();
                }
            }
        });
    }

    @Override
    public void onAdLoaded() {
        super.onAdLoaded();
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!mIsHide && mBannerView != null) {
                    show();
                }
                if (mAdListener != null) {
                    mAdListener.onAdLoaded();
                }
            }
        });
    }

    @Override
    public void onAdClicked() {
        super.onAdClicked();
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mAdListener != null) {
                    mAdListener.onAdClicked();
                }
            }
        });
    }

    @Override
    public void onAdImpression() {
        super.onAdImpression();
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mAdListener != null) {
                    mAdListener.onAdImpression();
                }
            }
        });
    }

    private float convertDpToPx(float dp) {
        DisplayMetrics metrics = mActivity.getResources().getDisplayMetrics();
        return dp * metrics.density;
    }

    private BannerAdSize getTargetBannerAdSize(String adSize) {
        BannerAdSize bannerAdSize = BannerAdSize.BANNER_SIZE_INVALID;
        switch (adSize) {
            case BannerAdSize.USER_DEFINED:
                bannerAdSize = getUserDefinedBannerSize();
                break;
            case BannerAdSize.BANNER_SIZE_320_50:
                bannerAdSize = BannerAdSize.BANNER_SIZE_320_50;
                break;
            case BannerAdSize.BANNER_SIZE_320_100:
                bannerAdSize = BannerAdSize.BANNER_SIZE_320_100;
                break;
            case BannerAdSize.BANNER_SIZE_468_60:
                bannerAdSize = BannerAdSize.BANNER_SIZE_468_60;
                break;
            case BannerAdSize.BANNER_SIZE_DYNAMIC:
                bannerAdSize = BannerAdSize.BANNER_SIZE_DYNAMIC;
                break;
            case BannerAdSize.BANNER_SIZE_728_90:
                bannerAdSize = BannerAdSize.BANNER_SIZE_728_90;
                break;
            case BannerAdSize.BANNER_SIZE_300_250:
                bannerAdSize = BannerAdSize.BANNER_SIZE_300_250;
                break;
            case BannerAdSize.BANNER_SIZE_SMART:
                bannerAdSize = BannerAdSize.BANNER_SIZE_SMART;
                break;
            case BannerAdSize.BANNER_SIZE_160_600:
                bannerAdSize = BannerAdSize.BANNER_SIZE_160_600;
                break;
            case BannerAdSize.BANNER_SIZE_360_57:
                bannerAdSize = BannerAdSize.BANNER_SIZE_360_57;
                break;
            case BannerAdSize.BANNER_SIZE_360_144:
                bannerAdSize = BannerAdSize.BANNER_SIZE_360_144;
                break;
            default:
                break;
        }
        return bannerAdSize;
    }

    private BannerAdSize getUserDefinedBannerSize() {
        return BannerAdSize.getCurrentDirectionBannerSize(mActivity, mCustomWidth);
    }

    private FrameLayout.LayoutParams getBannerViewLayoutParams() {
        final FrameLayout.LayoutParams adParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        adParams.gravity = BannerAdPositionCode.getLayoutGravityForPositionCode(mPositionCode);
        int safeInsetLeft = 0;
        int safeInsetTop = 0;
        if (mPositionCode == BannerAdPositionCode.POSITION_CUSTOM) {
            int leftOffset = (int) convertDpToPx(mHorizontalOffset);
            if (leftOffset < safeInsetLeft) {
                leftOffset = safeInsetLeft;
            }
            int topOffset = (int) convertDpToPx(mVerticalOffset);
            if (topOffset < safeInsetTop) {
                topOffset = safeInsetTop;
            }
            adParams.leftMargin = leftOffset;
            adParams.topMargin = topOffset;
        } else {
            adParams.leftMargin = safeInsetLeft;
            if (mPositionCode == BannerAdPositionCode.POSITION_TOP
                    || mPositionCode == BannerAdPositionCode.POSITION_TOP_LEFT
                    || mPositionCode == BannerAdPositionCode.POSITION_TOP_RIGHT) {
                adParams.topMargin = safeInsetTop;
            }
        }
        return adParams;
    }

    private void updatePosition() {
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mBannerView == null) {
                    return;
                }
                FrameLayout.LayoutParams layoutParams = getBannerViewLayoutParams();
                mBannerView.setLayoutParams(layoutParams);
            }
        });
    }
}
