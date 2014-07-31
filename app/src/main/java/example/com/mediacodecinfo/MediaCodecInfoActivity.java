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

    private void printCodecsNamed() {
        int numCodecs = MediaCodecList.getCodecCount();
        textBoxString = "Total Number of Codecs are : " + numCodecs + "\n";
        Log.d(TAG, "Total Number of Codecs are : " + numCodecs);

        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            textBoxString += "\n\nCodec : " + i + " ( " + (codecInfo.isEncoder() ? "Encoder" : "Decoder") + ")\nName:" + codecInfo.getName() + "\nTypes : ";
            Log.d(TAG, "\nFor codec " + i + " Encoder : " + codecInfo.isEncoder());
            Log.d(TAG, "This Codec supports the following types: ");
            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                Log.d(TAG, types[j]);
                //textBoxString +=  + (j < (types.length - 1) ? ", " : ".");
                textBoxString += "\n For Type : " + types[j] + " \n Color Formats: ";
                VCNameResolver codecNames = new VCNameResolver(types[j]);
                MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(types[j]);
                for (int k = 0; k < capabilities.colorFormats.length; k++) {
                    textBoxString += capabilities.colorFormats[k] + ", ";
                }
                for (int k = 0; k < capabilities.profileLevels.length; k++) {
                    String profileName = codecNames.getProfileName(capabilities.profileLevels[k].profile);
                    profileName = (profileName == null) ? capabilities.profileLevels[k].profile + "" : profileName;

                    String levelName = codecNames.getLevelName(capabilities.profileLevels[k].level);
                    levelName = (levelName == null) ? capabilities.profileLevels[k].level + "" : levelName;

                    textBoxString += "\nProfile: " + profileName
                            + ", Level: " + levelName;
                }
            }
        }
        textBox.setText(textBoxString);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_codec_info);
        textBox = (TextView) findViewById(R.id.textBox);
        //printCodecs();
        printCodecsNamed();
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
            textBoxString += "\n\nCodec : " + i + " ( " + (codecInfo.isEncoder() ? "Encoder" : "Decoder") + ")\nName:" + codecInfo.getName() + "\nTypes : ";
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

    private static enum CodecDetail {
        CODEC_DETAIL_LEVEL, CODEC_DETAIL_PROFILE;
    }

    private abstract class NameResolver {
        private int id;
        private String name;
        private CodecDetail detail;

        public NameResolver(int id, String name, CodecDetail detail) {
            this.id = id;
            this.name = name;
            this.detail = detail;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public CodecDetail getCodecDetailType() {
            return detail;
        }
    }

    private class BaseProfileName extends NameResolver {

        public BaseProfileName(int id, String name) {
            super(id, name, CodecDetail.CODEC_DETAIL_PROFILE);
        }
    }

    private class BaseLevelName extends NameResolver {

        public BaseLevelName(int id, String name) {
            super(id, name, CodecDetail.CODEC_DETAIL_LEVEL);
        }
    }

    private final class BaseProfile {
        private String type;

        private BaseProfileName[] profiles;

        BaseProfile(String type, BaseProfileName[] profiles) {
            this.type = type;
            this.profiles = profiles;
        }

        public String getType() {
            return type;
        }

        public String getName(int id) {
            for (BaseProfileName profile : profiles) {
                if (profile.getId() == id) {
                    return profile.getName();
                }
            }
            return null;
        }
    }

    private final class BaseLevel {
        private String type;
        private BaseLevelName[] levels;

        BaseLevel(String type, BaseLevelName[] levels) {
            this.type = type;
            this.levels = levels;
        }

        public String getType() {
            return type;
        }

        public String getName(int id) {
            for (BaseLevelName level : levels) {
                if (level.getId() == id) {
                    return level.getName();
                }
            }
            return null;
        }
    }

    private class VCNameResolver {
        String type;

        BaseProfile[] profiles = new BaseProfile[]{
                new BaseProfile("video/avc", new BaseProfileName[]
                        {
                                new BaseProfileName(1, "AVCProfileBaseline"),
                                new BaseProfileName(2, "AVCProfileMain"),
                                new BaseProfileName(4, "AVCProfileExtended"),
                                new BaseProfileName(8, "AVCProfileHigh"),
                                new BaseProfileName(16, "AVCProfileHigh10"),
                                new BaseProfileName(32, "AVCProfileHigh422"),
                                new BaseProfileName(64, "AVCProfileHigh444")
                        }),
                new BaseProfile("video/3gpp", new BaseProfileName[]
                        {
                                new BaseProfileName(1, "H263ProfileBaseline"),
                                new BaseProfileName(2, "H263ProfileH320Coding"),
                                new BaseProfileName(4, "H263ProfileBackwardCompatible"),
                                new BaseProfileName(8, "H263ProfileISWV2"),
                                new BaseProfileName(16, "H263ProfileISWV3"),
                                new BaseProfileName(32, "H263ProfileHighCompression"),
                                new BaseProfileName(64, "H263ProfileInternet"),
                                new BaseProfileName(128, "H263ProfileInterlace"),
                                new BaseProfileName(256, "H263ProfileHighLatency")
                        }),
                new BaseProfile("video/mp4v-es", new BaseProfileName[]
                        {
                                new BaseProfileName(1, "MPEG4ProfileSimple"),
                                new BaseProfileName(2, "MPEG4ProfileSimpleScalable"),
                                new BaseProfileName(4, "MPEG4ProfileCore"),
                                new BaseProfileName(8, "MPEG4ProfileMain"),
                                new BaseProfileName(16, "MPEG4ProfileNbit"),
                                new BaseProfileName(32, "MPEG4ProfileScalableTexture"),
                                new BaseProfileName(64, "MPEG4ProfileSimpleFace"),
                                new BaseProfileName(128, "MPEG4ProfileSimpleFBA"),
                                new BaseProfileName(256, "MPEG4ProfileBasicAnimated"),
                                new BaseProfileName(512, "MPEG4ProfileHybrid"),
                                new BaseProfileName(1024, "MPEG4ProfileAdvancedRealTime"),
                                new BaseProfileName(2048, "MPEG4ProfileCoreScalable"),
                                new BaseProfileName(4096, "MPEG4ProfileAdvancedCoding"),
                                new BaseProfileName(8192, "MPEG4ProfileAdvancedCore"),
                                new BaseProfileName(16384, "MPEG4ProfileAdvancedScalable"),
                                new BaseProfileName(32768, "MPEG4ProfileAdvancedSimple")
                        }),
                new BaseProfile("video/x-vnd.on2.vp8", new BaseProfileName[]
                        {
                                new BaseProfileName(1, "VP8ProfileMain")
                        })
        };

        BaseLevel[] levels = new BaseLevel[]{
                new BaseLevel("video/avc", new BaseLevelName[]
                        {
                                new BaseLevelName(1, "AVCLevel1"),
                                new BaseLevelName(2, "AVCLevel1b"),
                                new BaseLevelName(4, "AVCLevel11"),
                                new BaseLevelName(8, "AVCLevel12"),
                                new BaseLevelName(16, "AVCLevel13"),
                                new BaseLevelName(32, "AVCLevel2"),
                                new BaseLevelName(64, "AVCLevel21"),
                                new BaseLevelName(128, "AVCLevel22"),
                                new BaseLevelName(256, "AVCLevel3"),
                                new BaseLevelName(512, "AVCLevel31"),
                                new BaseLevelName(1024, "AVCLevel32"),
                                new BaseLevelName(2048, "AVCLevel4"),
                                new BaseLevelName(4096, "AVCLevel41"),
                                new BaseLevelName(8192, "AVCLevel42"),
                                new BaseLevelName(16384, "AVCLevel5"),
                                new BaseLevelName(32768, "AVCLevel51")
                        }),
                new BaseLevel("video/3gpp", new BaseLevelName[]
                        {
                                new BaseLevelName(1, "H263Level10"),
                                new BaseLevelName(2, "H263Level20"),
                                new BaseLevelName(4, "H263Level30"),
                                new BaseLevelName(8, "H263Level40"),
                                new BaseLevelName(16, "H263Level45"),
                                new BaseLevelName(32, "H263Level50"),
                                new BaseLevelName(64, "H263Level60"),
                                new BaseLevelName(128, "H263Level70")
                        }),
                new BaseLevel("video/mp4v-es", new BaseLevelName[]
                        {
                                new BaseLevelName(1, "MPEG4Level0"),
                                new BaseLevelName(2, "MPEG4Level0b"),
                                new BaseLevelName(4, "MPEG4Level1"),
                                new BaseLevelName(8, "MPEG4Level2"),
                                new BaseLevelName(16, "MPEG4Level3"),
                                new BaseLevelName(32, "MPEG4Level4"),
                                new BaseLevelName(64, "MPEG4Level4a"),
                                new BaseLevelName(128, "MPEG4Level5")
                        }),
                new BaseLevel("video/x-vnd.on2.vp8", new BaseLevelName[]
                        {
                                new BaseLevelName(1, "VP8Level_Version0"),
                                new BaseLevelName(2, "VP8Level_Version1"),
                                new BaseLevelName(4, "VP8Level_Version2"),
                                new BaseLevelName(8, "VP8Level_Version3")
                        })
        };


        public VCNameResolver(String type) {
            this.type = type;
        }

        public String getProfileName(int id) {
            for (BaseProfile profile : profiles) {
                if (profile.getType().equals(this.type)) {
                    return profile.getName(id);
                }
            }
            return null;
        }

        public String getLevelName(int id) {
            for (BaseLevel level : levels) {
                if (level.getType().equals(this.type)) {
                    return level.getName(id);
                }
            }
            return null;
        }
    }
}