package com.twtstudio.bbs.bdpqchen.bbs.htmltextview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.util.AttributeSet;

import com.twtstudio.bbs.bdpqchen.bbs.htmltextview.quote.QuoteReplaceUtil;

import java.io.InputStream;
import java.util.Scanner;

public class PostHtmlTextView extends JellyBeanSpanFixTextView {

    public static final String TAG = "HtmlTextView";
    public static final boolean DEBUG = false;

    boolean linkHit;
    @Nullable
    private ClickableTableSpan clickableTableSpan;
    @Nullable
    private DrawTableLinkSpan drawTableLinkSpan;

    boolean dontConsumeNonUrlClicks = true;
    private boolean removeFromHtmlSpace = true;

    public PostHtmlTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public PostHtmlTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PostHtmlTextView(Context context) {
        super(context);
    }

    /**
     * @see HtmlTextView#setHtml(int)
     */
    public void setHtml(@RawRes int resId) {
        setHtml(resId, null);
    }

    /**
     * @see HtmlTextView#setHtml(String)
     */
    public void setHtml(@NonNull String html) {
        setHtml(html, null);
    }

    /**
     * Loads HTML from a raw resource, i.e., a HTML file in res/raw/.
     * This allows translatable resource (e.g., res/raw-de/ for german).
     * The containing HTML is parsed to Android's Spannable format and then displayed.
     *
     * @param resId       for example: R.raw.help
     * @param imageGetter for fetching images. Possible ImageGetter provided by this library:
     *                    HtmlLocalImageGetter and HtmlRemoteImageGetter
     */
    public void setHtml(@RawRes int resId, @Nullable Html.ImageGetter imageGetter) {
        InputStream inputStreamText = getContext().getResources().openRawResource(resId);

        setHtml(convertStreamToString(inputStreamText), imageGetter);
    }

    /**
     * Parses String containing HTML to Android's Spannable format and displays it in this TextView.
     * Using the implementation of Html.ImageGetter provided.
     *
     * @param html        String containing HTML, for example: "<b>Hello world!</b>"
     * @param imageGetter for fetching images. Possible ImageGetter provided by this library:
     *                    HtmlLocalImageGetter and HtmlRemoteImageGetter
     */
    public void setHtml(@NonNull String html, @Nullable Html.ImageGetter imageGetter) {
        final HtmlTagHandler htmlTagHandler = new HtmlTagHandler(getPaint());
        htmlTagHandler.setClickableTableSpan(clickableTableSpan);
        htmlTagHandler.setDrawTableLinkSpan(drawTableLinkSpan);


        html = htmlTagHandler.overrideTags(html);

        Spanned spanned = Html.fromHtml(html, imageGetter, htmlTagHandler);

        QuoteReplaceUtil.replaceQuoteSpans((Spannable) spanned);

        if (removeFromHtmlSpace) {
            setText(removeHtmlBottomPadding(spanned));
        } else {
            setText(spanned);
        }

        //这个东西和我的图片点击触控有点冲突
        // 换到了 Thread 和 post 的 xml 里面设置属性
//        setTextIsSelectable(true);

        // make links work and image
        setMovementMethod(RichTextMovementMethod.getInstance());

    }

    /**
     * Note that this must be called before setting text for it to work
     */
    public void setRemoveFromHtmlSpace(boolean removeFromHtmlSpace) {
        this.removeFromHtmlSpace = removeFromHtmlSpace;
    }

    public void setClickableTableSpan(@Nullable ClickableTableSpan clickableTableSpan) {
        this.clickableTableSpan = clickableTableSpan;
    }

    public void setDrawTableLinkSpan(@Nullable DrawTableLinkSpan drawTableLinkSpan) {
        this.drawTableLinkSpan = drawTableLinkSpan;
    }

    /**
     * http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
     */
    @NonNull
    static private String convertStreamToString(@NonNull InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    /**
     * Html.fromHtml sometimes adds extra space at the bottom.
     * This methods removes this space again.
     * See https://github.com/SufficientlySecure/html-textview/issues/19
     */
    @Nullable
    static private CharSequence removeHtmlBottomPadding(@Nullable CharSequence text) {
        if (text == null) {
            return null;
        }

        while (text.length() > 0 && text.charAt(text.length() - 1) == '\n') {
            text = text.subSequence(0, text.length() - 1);
        }
        return removeHtmlTopPadding(text);
    }

    @Nullable
    static private CharSequence removeHtmlTopPadding(@Nullable CharSequence text) {
        if (text == null) {
            return null;
        }
        while (text.length() > 0 && text.charAt(0) == '\n') {
            text = text.subSequence(1, text.length());
        }
        return text;
    }

}
