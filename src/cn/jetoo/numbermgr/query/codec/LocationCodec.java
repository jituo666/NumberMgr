
package cn.jetoo.numbermgr.query.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import android.content.Context;

/**
 * Created with IntelliJ IDEA. User: wangweiwei Date: 13-2-16 Time: 下午3:05 To
 * change this template use File | Settings | File Templates.
 */
public interface LocationCodec {
    public void encode(String input, int versionCode) throws IOException;

    public void decode(InputStream input) throws IOException;

    public void decode(String input) throws IOException;

    public void output(String output) throws IOException, UnsupportedEncodingException;

    public void loadExtra(InputStream district, InputStream intl, InputStream pub) throws IOException;

    public void loadExtra(String district, String international, String pub) throws IOException;

    String getLocation(String number);

    String getPublicLocation(String number);

    String getLocation(String number, boolean showProvince, boolean showOperator);
    String getLocationWithSplitor(String number);

    List<String> getCitiesForProvince(String province);

    String getProviceForCity(String city);

    int getCount();

    void setCount(int count);

    void init(Context context) throws IOException;

    void setVersionCode(int versionCode);

    int getVersionCode();
}
