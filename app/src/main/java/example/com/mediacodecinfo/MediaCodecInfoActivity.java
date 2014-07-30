package example.com.mediacodecinfo;

import android.app.Activity;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MediaCodecInfoActivity extends Activity {

    private static String TAG = "CodecInfo";
    private String textBoxString;
    private TextView textBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_codec_info);
        textBox = (TextView) findViewById(R.id.textBox);
        printCodecs();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.media_codec_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void printCodecs() {
        int numCodecs = MediaCodecList.getCodecCount();
        textBoxString = "Total Number of Codecs are : " + numCodecs + "\n";
        Log.d(TAG, "Total Number of Codecs are : " + numCodecs);

        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            textBoxString += "\n\nCodec : " + i + " Type : " + (codecInfo.isEncoder() ? "Encoder" : "Decoder") + "\n Types : ";
            Log.d(TAG, "\nFor codec " + i + " Encoder : " + codecInfo.isEncoder());
            Log.d(TAG, "This Codec supports the following types: ");
            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                Log.d(TAG, types[j]);
                //textBoxString +=  + (j < (types.length - 1) ? ", " : ".");
                textBoxString += "\n For Type : " + types[j] + " \n Color Formats: ";
                MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(types[j]);
                for (int k = 0; k < capabilities.colorFormats.length; k++) {
                    textBoxString += capabilities.colorFormats[k] + ", ";
                }
                for (int k = 0; k < capabilities.profileLevels.length; k++) {
                    textBoxString += "\nProfile: " + capabilities.profileLevels[k].profile + ", Level: " + capabilities.profileLevels[k].level;
                }
            }
        }
        textBox.setText(textBoxString);
    }
}
