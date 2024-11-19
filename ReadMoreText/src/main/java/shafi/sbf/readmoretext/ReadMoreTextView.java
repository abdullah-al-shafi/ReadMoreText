package shafi.sbf.readmoretext;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class ReadMoreTextView extends androidx.appcompat.widget.AppCompatTextView {

    private static final int DEFAULT_TRIM_LENGTH = 50; // Default trim length
    private static final String ELLIPSIS = "...";
    private String fullText;
    private String trimmedText;
    private boolean isExpanded = false;
    private int trimLength;
    private int readMoreColor;
    private int showLessColor;
    // Customizable text for collapsed and expanded state
    private String readMoreText = " Read More"; // Default text
    private String showLessText = " Show Less"; // Default text

    public ReadMoreTextView(Context context) {
        super(context);
        init(null);
    }

    public ReadMoreTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ReadMoreTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        // Set default trim length
        trimLength = DEFAULT_TRIM_LENGTH;

        // Fetch custom attributes
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ReadMoreTextView);

            // Get custom trim length, if specified
            trimLength = typedArray.getInteger(R.styleable.ReadMoreTextView_trimLength, DEFAULT_TRIM_LENGTH);

            // Get colors for "Read More" and "Show Less"
            readMoreColor = typedArray.getColor(R.styleable.ReadMoreTextView_readMoreColor,
                    ContextCompat.getColor(getContext(), android.R.color.holo_blue_light)); // Default blue color
            showLessColor = typedArray.getColor(R.styleable.ReadMoreTextView_showLessColor,
                    ContextCompat.getColor(getContext(), android.R.color.holo_red_light));  // Default red color

            typedArray.recycle();
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleText();
            }
        });
    }

    // Set the full text, this should be called when setting text to the TextView.
    public void setTrimmedText(String text) {
        this.fullText = text;

        if (text.length() > trimLength) {
            trimmedText = String.valueOf(createSpannableTrimmedText());
            setText(createSpannableTrimmedText());
        } else {
            setText(text);
           // trimmedText = text;
        }
      //  setText(trimmedText);
    }

    // Toggle between showing full text and trimmed text.
    private void toggleText() {
        if (isExpanded) {
           // setText(trimmedText);
            setText(createSpannableTrimmedText());
        } else {
            setText(createSpannableShowLessText());
        }
        isExpanded = !isExpanded;
    }

    // Helper method to create trimmed text with "Read More" clickable span
    private SpannableString createSpannableTrimmedText() {
        if (fullText == null || fullText.isEmpty()) {
            throw new IllegalStateException("fullText cannot be null or empty");
        }

        // Ensure trimLength does not exceed the fullText length
        int safeTrimLength = Math.min(trimLength, fullText.length());
        String subText = fullText.substring(0, safeTrimLength);

        // Handle edge cases where fullText is too short for ELLIPSIS and readMoreText
        if (safeTrimLength + ELLIPSIS.length() + readMoreText.length() > fullText.length()) {
            return new SpannableString(fullText);
        }

        String trimmed = subText + ELLIPSIS + readMoreText;
        SpannableString spannableString = new SpannableString(trimmed);

        // Apply bold and color to "Read More"
        int start = trimmed.length() - readMoreText.length();
        int end = trimmed.length();
        spannableString.setSpan(new ForegroundColorSpan(readMoreColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    // Helper method to create full text with "Show Less" clickable span
    private SpannableString createSpannableShowLessText() {
        String full = fullText + showLessText;
        SpannableString spannableString = new SpannableString(full);

        // Apply bold and color to "Show Less"
        spannableString.setSpan(new ForegroundColorSpan(showLessColor),
                full.length() - showLessText.length(), full.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                full.length() - showLessText.length(), full.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    // Optionally expose this method to set trim length
    public void setTrimLength(int length) {
        this.trimLength = length;
        if (fullText != null) {
            setTrimmedText(fullText); // Reapply the text with new trim length
        }
    }

    // Set the custom "Read More" text
    public void setReadMoreText(String readMoreText) {
        this.readMoreText = readMoreText;
        if (!isExpanded && fullText != null) {
            setTrimmedText(fullText); // Update the trimmed text
        }
    }

    // Set the custom "Show Less" text
    public void setShowLessText(String showLessText) {
        this.showLessText = showLessText;
        if (isExpanded && fullText != null) {
            setText(createSpannableShowLessText()); // Update the expanded text
        }
    }
}

