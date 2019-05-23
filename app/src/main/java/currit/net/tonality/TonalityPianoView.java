package currit.net.tonality;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import mn.tck.semitone.PianoView;
import mn.tck.semitone.Util;

public class TonalityPianoView extends PianoView {
    final static String PREF_SCALE = "scale";
    final static String PREF_SCALE_ROOT = "scale_root";
    final static int PREF_SCALE_DEFAULT = 0;
    final static int PREF_SCALE_ROOT_DEFAULT = 0;

    public int scale = PREF_SCALE_DEFAULT;
    public int rootNote = PREF_SCALE_ROOT_DEFAULT;
    Paint whiteScalePaint, blackScalePaint;
    Paint[] scaleColors;

    public TonalityPianoView(Context context, AttributeSet attrs) {
        super(context, attrs);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        whiteScalePaint = new Paint();
        whiteScalePaint.setColor(ContextCompat.getColor(getContext(), R.color.whiteScale));
        blackScalePaint = new Paint();
        blackScalePaint.setColor(ContextCompat.getColor(getContext(), R.color.blackScale));
        setScale(sp.getInt(PREF_SCALE, PREF_SCALE_DEFAULT), sp.getInt(PREF_SCALE_ROOT, PREF_SCALE_ROOT_DEFAULT));

        this.concert_a = 440; // set a default to avoid DIV0 errors

    }

    public void setup(int concert_a, boolean sustain, boolean labelnotes, boolean labelc) {
        this.concert_a = concert_a;
        this.sustain = sustain;
        this.labelnotes = labelnotes;
        this.labelc = labelc;
    }

    public void setScale(int newScale, int newRoot) {
        scale = newScale;
        rootNote = newRoot;
        if (scale > 0) {
            // get scale array from resources and adjust colors used for drawing the keyboard accordingly
            TypedArray ta = getResources().obtainTypedArray(R.array.scales);
            int[] scaleArray = getResources().getIntArray(ta.getResourceId(scale, 0));
            scaleColors = new Paint[12];
            int realKey;
            for (int i = 0; i < scaleArray.length; i++) {
                realKey = (i + rootNote) % 12;
                scaleColors[realKey] = (scaleArray[i] == 0
                        ? (isBlack(realKey) ? blackPaint : whitePaint)
                        : (isBlack(realKey) ? blackScalePaint : whiteScalePaint));
            }
            ta.recycle();
        } else {
            // set default colors if no scale is selected
            scaleColors = new Paint[]{whitePaint, blackPaint, whitePaint, blackPaint, whitePaint, whitePaint, blackPaint, whitePaint, blackPaint, whitePaint, blackPaint, whitePaint};
        }

        // store in preferences
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putInt(PREF_SCALE, scale);
        editor.putInt(PREF_SCALE_ROOT, rootNote);
        editor.apply();

        invalidate();
    }

    /**
     * Override the original pianoview onDraw to be able to highlight a scale
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth(), height = getHeight();

        whiteWidth = width / keys;
        whiteHeight = height / rows;
        blackWidth = whiteWidth * 2 / 3;
        blackHeight = whiteHeight / 2;
        blackPaint.setTextSize(Util.maxTextSize("G0", whiteWidth * 2 / 3));

        for (int row = 0; row < rows; ++row) {
            for (int key = 0; key < keys; ++key) {
                int x = whiteWidth * key, y = whiteHeight * row;
                int p = pitches[row][key];

                canvas.drawRect(x, y, x + whiteWidth, y + whiteHeight - YPAD, grey3Paint);
                canvas.drawRect(x + OUTLINE, y, x + whiteWidth - OUTLINE,
                        y + whiteHeight - OUTLINE * 2 - YPAD,
                        pressed[p] ? grey4Paint : scaleColors[p % 12]);

                // draw labels if labelnotes is true and either labelc is "off" or we are at a C note
                if (labelnotes && (!labelc || p % 12 == 0)) canvas.drawText(
                        Util.notenames[(p + 3) % 12] + (p / 12 - 1),
                        x + whiteWidth / 2, y + whiteHeight * 4 / 5, blackPaint);

                if (hasBlackLeft(p)) canvas.drawRect(
                        x, y,
                        x + blackWidth / 2, y + blackHeight,
                        pressed[p - 1] ? grey1Paint : scaleColors[(p - 1) % 12]);
                if (hasBlackRight(p)) canvas.drawRect(
                        x + whiteWidth - blackWidth / 2, y,
                        x + whiteWidth, y + blackHeight,
                        pressed[p + 1] ? grey1Paint : scaleColors[(p + 1) % 12]);

            }
        }
    }


    private boolean isBlack(int p) {
        return p % 12 == 1 || p % 12 == 3 || p % 12 == 6 || p % 12 == 8 || p % 12 == 10;
    }

}