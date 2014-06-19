package cn.jetoo.numbermgr.query.codec;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import org.ardverk.collection.PatriciaTrie;
import org.ardverk.collection.StringKeyAnalyzer;
import org.ardverk.collection.Trie;

import android.content.Context;
import android.util.Log;

import cn.jetoo.numbermgr.BuildConfig;

/**
 * Created with IntelliJ IDEA. User: wangweiwei Date: 13-2-16 Time: 下午3:04 To
 * change this template use File | Settings | File Templates.
 */
public class NaiveLocationCodec implements LocationCodec {

    private static final char DISTRICT_CODE_SIGN = 'D';
    private static final char INTERNATIONAL_CODE_SIGN = 'I';
    private static final char PUBLIC_CODE_SIGN = 'P';
    private static final String TAG = NaiveLocationCodec.class.getSimpleName();
    private String[] locationIndexTable;
    private String[] operatorIndexTable;
    private int[] prefixIndexTable;
    private Set<Integer> prefixSet = new HashSet<Integer>();
    private Map<String, List<String>> provinceToCitiesMap = new HashMap<String, List<String>>();
    private Map<String, String> cityToProvinceMap = new HashMap<String, String>();
    private int[] compressedLocationData;
    private int[] compressedOperatorData;
    private int count;
    private int versionCode;
    private volatile boolean inited = false;

    private static final int IP_PREFIX_LEN = 5;
    private static final Set<String> IP_DIAL_PREFIX = new HashSet<String>() {
        private static final long serialVersionUID = 8258948768278578200L;

        {
            add("17951");
            add("12593");
            add("17911");
            add("17901");
            add("11801");
        }
    };
    private static final Pattern ILLEGAL_NUMBER_PATTERN = Pattern.compile("[^+\\d]");
    private Trie<String, String> trie = null;
    private static NaiveLocationCodec INSTANCE = null;

    public static synchronized LocationCodec getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new NaiveLocationCodec();
        }
        return INSTANCE;
    }

    private NaiveLocationCodec() {
        trie = new PatriciaTrie<String, String>(StringKeyAnalyzer.INSTANCE);
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int getVersionCode() {
        return versionCode;
    }

    @Override
    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    @Override
    public void encode(String input, int versionCode) throws IOException {
        this.versionCode = versionCode;
        HashSet<Integer> numberSet = new HashSet<Integer>();
        HashMap<Integer, String> numberToAddressMap = new HashMap<Integer, String>();
        HashMap<Integer, String> numberToOperatorMap = new HashMap<Integer, String>();
        HashMap<String, Integer> addressToIdMap = new HashMap<String, Integer>();
        HashMap<String, Integer> operatorToIdMap = new HashMap<String, Integer>();
        HashSet<Integer> numberPrefixSet = new HashSet<Integer>();
        int addressIndex = 0;
        int operatorIndex = 0;
        Scanner scanner = new Scanner(new FileInputStream(input));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] spliter = line.split("[|]");
            if (spliter.length == 5) {
                int number = Integer.valueOf(spliter[0]);
                numberPrefixSet.add(Integer.valueOf(spliter[0].substring(0, 3)));
                String address = spliter[2] + '|' + spliter[1];
                String operator = spliter[3];
                if (!numberSet.contains(number)) {
                    numberSet.add(number);
                    numberToAddressMap.put(number, address);
                    numberToOperatorMap.put(number, operator);
                }
                if (!addressToIdMap.containsKey(address)) {
                    addressToIdMap.put(address, addressIndex++);
                }
                if (!operatorToIdMap.containsKey(operator)) {
                    operatorToIdMap.put(operator, operatorIndex++);
                }
            }
        }
        scanner.close();

        ArrayList<Integer> numberArray = new ArrayList<Integer>(numberSet);
        count = numberArray.size();
        Collections.sort(numberArray);
        Map<Integer, Integer> prefixToIndexMap = new HashMap<Integer, Integer>();
        prefixIndexTable = new int[numberPrefixSet.size()];
        int index = 0;
        for (Integer prefix : numberPrefixSet) {
            prefixIndexTable[index] = prefix;
            prefixSet.add(prefix);
            prefixToIndexMap.put(prefix, index);
            index++;
        }
        locationIndexTable = new String[addressToIdMap.size()];
        for (Map.Entry<String, Integer> entry : addressToIdMap.entrySet()) {
            locationIndexTable[entry.getValue()] = entry.getKey();
        }
        operatorIndexTable = new String[operatorToIdMap.size()];
        for (Map.Entry<String, Integer> entry : operatorToIdMap.entrySet()) {
            operatorIndexTable[entry.getValue()] = entry.getKey();
        }

        ArrayList<Integer> compressedAddressList = new ArrayList<Integer>();
        for (int i = 0; i < numberArray.size();) {
            int prevNumber = numberArray.get(i);
            String prevAddress = numberToAddressMap.get(prevNumber);
            int prevAddressId = addressToIdMap.get(prevAddress);
            int j = i + 1;
            for (; j < numberArray.size(); j++) {
                int curNumber = numberArray.get(j);
                String curAddress = numberToAddressMap.get(curNumber);
                int curAddressId = addressToIdMap.get(curAddress);
                if (curNumber != prevNumber + j - i || curAddressId != prevAddressId || j - i >= 32) {
                    break;
                }
            }
            int suffix = prevNumber % 10000;
            int prefix = prevNumber / 10000;
            Integer prefixId = prefixToIndexMap.get(prefix);
            if (null == prefixId) {
                throw new IllegalArgumentException("missing number prefix:" + prefix);
            }
            suffix += prefixId * 10000;
            int data = suffix + prevAddressId * prefixIndexTable.length * 10000;
            if ((data & 0xF8000000) != 0) {
                throw new IllegalArgumentException("no enough bits to do compress");
            }
            data |= (j - i - 1) << 27;
            compressedAddressList.add(data);
            i = j;
        }
        compressedLocationData = new int[compressedAddressList.size()];
        for (int i = 0; i < compressedAddressList.size(); i++) {
            compressedLocationData[i] = compressedAddressList.get(i);
        }

        ArrayList<Integer> compressOperatorList = new ArrayList<Integer>();
        for (int i = 0; i < numberArray.size();) {
            int prevNumber = numberArray.get(i);
            String prevOperator = numberToOperatorMap.get(prevNumber);
            int prevOperatorId = operatorToIdMap.get(prevOperator);
            int j = i + 1;
            for (; j < numberArray.size(); j++) {
                int curNumber = numberArray.get(j);
                String curOperator = numberToOperatorMap.get(curNumber);
                int curOperatorId = operatorToIdMap.get(curOperator);
                if (curNumber != prevNumber + j - i || curOperatorId != prevOperatorId
                        || j - i >= 1024) {
                    break;
                }
            }
            int suffix = prevNumber % 10000;
            int prefix = prevNumber / 10000;
            Integer prefixId = prefixToIndexMap.get(prefix);
            if (prefixId == null) {
                throw new IllegalArgumentException("missing number prefix:" + prefix);
            }
            suffix += prefixId * 10000;
            int data = suffix + prevOperatorId * prefixIndexTable.length * 10000;
            if ((data & 0xFFC00000) != 0) {
                throw new IllegalArgumentException("no enough bits to do compress");
            }
            data |= (j - i - 1) << 22;
            compressOperatorList.add(data);
            i = j;
        }
        compressedOperatorData = new int[compressOperatorList.size()];
        for (int i = 0; i < compressOperatorList.size(); i++) {
            compressedOperatorData[i] = compressOperatorList.get(i);
        }
        System.out.println("input len:" + numberArray.size() + ",compressed address:"
                + compressedLocationData.length + ",compressed operator:"
                + compressedOperatorData.length);
    }

    @Override
    public void decode(InputStream input) throws IOException {
        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(input));
        versionCode = inputStream.readInt();
        count = inputStream.readInt();
        int len = inputStream.readInt();
        byte[] bytes = new byte[len];
        int readed = inputStream.read(bytes);
        if (len != readed) {
            throw new IllegalArgumentException("bad input file format");
        }
        locationIndexTable = new String(bytes, "UTF-8").split("\t");
        len = inputStream.readInt();
        bytes = new byte[len];
        readed = inputStream.read(bytes);
        if (len != readed) {
            throw new IllegalArgumentException("bad input file format");
        }
        operatorIndexTable = new String(bytes, "UTF-8").split("\t");

        len = inputStream.readInt();
        prefixIndexTable = new int[len];
        for (int i = 0; i < len; i++) {
            int prefix = inputStream.readInt();
            prefixIndexTable[i] = prefix;
            prefixSet.add(prefix);
        }

        len = inputStream.readInt();
        compressedLocationData = new int[len];
        for (int i = 0; i < len; i++) {
            compressedLocationData[i] = inputStream.readInt();
        }
        len = inputStream.readInt();
        compressedOperatorData = new int[len];
        for (int i = 0; i < len; i++) {
            compressedOperatorData[i] = inputStream.readInt();
        }
        inputStream.close();
    }

    @Override
    public void decode(String input) throws IOException {
        this.compressedLocationData = null;
        this.compressedOperatorData = null;
        this.locationIndexTable = null;
        this.operatorIndexTable = null;
        this.prefixIndexTable = null;
        decode(new FileInputStream(input));
    }

    @Override
    public String getLocation(String number) {
        return getLocation(number, true, true);
    }

    @Override
    public String getLocation(String number, boolean showProvince, boolean showOperator) {
        if (!inited) {
            return null;
        }
        if (number == null || number.length() < 2) { // check valid after strip
            return null;
        }
        number = ILLEGAL_NUMBER_PATTERN.matcher(number).replaceAll("");
        // 去除ip前缀
        if (number.length() > IP_PREFIX_LEN) {
            String prefix = number.substring(0, IP_PREFIX_LEN);
            if (IP_DIAL_PREFIX.contains(prefix)) {
                number = number.substring(IP_PREFIX_LEN);
            }
        }

        if (number.length() < 2) {// re-check valid after strip
            return null;
        }

        if (number.charAt(0) == '+' && number.length() > 4) {
            number = number.substring(1);
            if (!number.startsWith("86")) {
                Map.Entry<String, String> entry = trie.select(INTERNATIONAL_CODE_SIGN
                        + number.substring(0, 4));
                if (entry != null && number.startsWith(entry.getKey().substring(1))) {
                    return entry.getValue();
                }
            } else {
                number = number.substring(2);
            }
        } else if (number.startsWith("00") && number.length() > 4) {
            number = number.substring(2);
            if (!number.startsWith("86")) {
                Map.Entry<String, String> entry = trie.select(INTERNATIONAL_CODE_SIGN
                        + number.substring(0, 4));
                if (entry != null && number.startsWith(entry.getKey().substring(1))) {
                    return entry.getValue();
                }
            } else {
                number = number.substring(2);
            }
        } else if (number.startsWith("86")) {
            number = number.substring(2);
        }
        // 手机号
        if (number.length() >= 7 && number.length() <= 11 && number.charAt(0) == '1') {
            int prefix = Integer.parseInt(number.substring(0, 7));
            if (prefixSet.contains(prefix / 10000)) {
                String location = getLocation(prefix);
                if (null != location) {
                    StringBuilder stringBuilder = new StringBuilder();
                    int index = location.indexOf('|');
                    String province = location.substring(0, index);
                    String city = location.substring(index + 1);
                    if (showProvince) {
                        stringBuilder.append(province);
                        if (!city.equals(province)) {
                            stringBuilder.append(city);
                        }
                    } else {
                        stringBuilder.append(city);
                    }
                    if (showOperator) {
                        String operator = getOperator(prefix);
                        if (null != operator) {
                            stringBuilder.append(operator);
                        }
                    }
                    return stringBuilder.toString();
                }
            }
        }
        // 固定电话
        if (number.length() > 4 && number.charAt(0) == '0') {
            String code = null;
            char ch = number.charAt(1);
            if (ch == '1' || ch == '2') {
                code = number.substring(0, 3);
            } else {
                code = number.substring(0, 4);
            }
            Map.Entry<String, String> entry = trie.select(DISTRICT_CODE_SIGN + code);
            if (entry != null && number.startsWith(entry.getKey().substring(1))) {
                return entry.getValue();
            }
        }
        if (number.length() > 1)// public phone
        {
            Map.Entry<String, String> entry = trie.select(PUBLIC_CODE_SIGN + number);
            if (entry != null && number.startsWith(entry.getKey().substring(1))) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public String getLocationWithSplitor(String number) {
        if (!inited) {
            return null;
        }
        if (number == null || number.length() < 2) { // check valid after strip
            return null;
        }
        number = ILLEGAL_NUMBER_PATTERN.matcher(number).replaceAll("");
        // 去除ip前缀
        if (number.length() > IP_PREFIX_LEN) {
            String prefix = number.substring(0, IP_PREFIX_LEN);
            if (IP_DIAL_PREFIX.contains(prefix)) {
                number = number.substring(IP_PREFIX_LEN);
            }
        }

        if (number.length() < 2) {// re-check valid after strip
            return null;
        }

        if (number.charAt(0) == '+' && number.length() > 4) {
            number = number.substring(1);
            if (!number.startsWith("86")) {
                Map.Entry<String, String> entry = trie.select(INTERNATIONAL_CODE_SIGN
                        + number.substring(0, 4));
                if (entry != null && number.startsWith(entry.getKey().substring(1))) {
                    return entry.getValue();
                }
            } else {
                number = number.substring(2);
            }
        } else if (number.startsWith("00") && number.length() > 4) {
            number = number.substring(2);
            if (!number.startsWith("86")) {
                Map.Entry<String, String> entry = trie.select(INTERNATIONAL_CODE_SIGN
                        + number.substring(0, 4));
                if (entry != null && number.startsWith(entry.getKey().substring(1))) {
                    return entry.getValue();
                }
            } else {
                number = number.substring(2);
            }
        } else if (number.startsWith("86")) {
            number = number.substring(2);
        }
        // 手机号
        if (number.length() >= 7 && number.length() <= 11 && number.charAt(0) == '1') {
            int prefix = Integer.parseInt(number.substring(0, 7));
            if (prefixSet.contains(prefix / 10000)) {
                String location = getLocation(prefix);
                if (null != location) {
                    StringBuilder stringBuilder = new StringBuilder();
                    int index = location.indexOf('|');
                    String province = location.substring(0, index);
                    String city = location.substring(index + 1);
                    stringBuilder.append(province).append("|");
                    stringBuilder.append(city).append("|");
                    String operator = getOperator(prefix);
                    if (null != operator) {
                        stringBuilder.append(operator);
                    }
                    return stringBuilder.toString();
                }
            }
        }
        // 固定电话
        if (number.length() > 4 && number.charAt(0) == '0') {
            String code = null;
            char ch = number.charAt(1);
            if (ch == '1' || ch == '2') {
                code = number.substring(0, 3);
            } else {
                code = number.substring(0, 4);
            }
            Map.Entry<String, String> entry = trie.select(DISTRICT_CODE_SIGN + code);
            if (entry != null && number.startsWith(entry.getKey().substring(1))) {
                return entry.getValue();
            }
        }
        if (number.length() > 1)// public phone
        {
            Map.Entry<String, String> entry = trie.select(PUBLIC_CODE_SIGN + number);
            if (entry != null && number.startsWith(entry.getKey().substring(1))) {
                return entry.getValue();
            }
        }
        return null;
    }

    private String getLocation(int query) {
        if (null == this.compressedLocationData || null == this.locationIndexTable
                || null == this.prefixIndexTable) {
            throw new IllegalStateException("codec not initialized");
        }
        int left = 0;
        int right = compressedLocationData.length - 1;
        while (left < right) {
            int m = (left + right + 1) >> 1;
            int data = compressedLocationData[m];
            int suffix = (data & 0x07FFFFFF) % (prefixIndexTable.length * 10000);
            int number = suffix % 10000 + prefixIndexTable[suffix / 10000] * 10000;
            if (query < number) {
                right = m - 1;
            } else {
                left = m;
            }
        }
        if (left != right) {
            return null;
        }
        int data = compressedLocationData[left];
        int suffix = (data & 0x07FFFFFF) % (prefixIndexTable.length * 10000);
        int success = (data & 0xF8000000) >>> 27;
        int number = suffix % 10000 + prefixIndexTable[suffix / 10000] * 10000;
        int index = (data & 0x07FFFFFF) / (prefixIndexTable.length * 10000);
        if (query >= number && query <= number + success && index < locationIndexTable.length) {
            return locationIndexTable[index];
        }
        return null;
    }

    private String getOperator(int query) {
        if (null == this.compressedOperatorData || null == this.operatorIndexTable
                || null == this.prefixIndexTable) {
            throw new IllegalStateException("codec not initialized");
        }
        int left = 0;
        int right = compressedOperatorData.length - 1;
        while (left < right) {
            int m = (left + right + 1) >> 1;
            int data = compressedOperatorData[m];
            int suffix = (data & 0x003FFFFF) % (prefixIndexTable.length * 10000);
            int number = suffix % 10000 + prefixIndexTable[suffix / 10000] * 10000;
            if (query < number) {
                right = m - 1;
            } else {
                left = m;
            }
        }
        if (left != right) {
            return null;
        }
        int data = compressedOperatorData[left];
        int suffix = (data & 0x003FFFFF) % (prefixIndexTable.length * 10000);
        int success = (data & 0xFFC00000) >>> 22;
        int number = suffix % 10000 + prefixIndexTable[suffix / 10000] * 10000;
        int index = (data & 0x003FFFFF) / (prefixIndexTable.length * 10000);
        if (query >= number && query <= number + success && index < operatorIndexTable.length) {
            return operatorIndexTable[index];
        }
        return null;
    }

    @Override
    public void output(String output) throws IOException, UnsupportedEncodingException {
        DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(output)));
        outputStream.writeInt(versionCode);
        outputStream.writeInt(count);
        // output location index table
        StringBuilder locationStringBuilder = new StringBuilder();
        for (int i = 0; i < locationIndexTable.length - 1; i++) {
            locationStringBuilder.append(locationIndexTable[i]).append('\t');
        }
        locationStringBuilder.append(locationIndexTable[locationIndexTable.length - 1]);
        byte[] bytes = locationStringBuilder.toString().getBytes("UTF-8");
        outputStream.writeInt(bytes.length);
        outputStream.write(bytes);

        // output operator index table
        StringBuilder operatorStringBuilder = new StringBuilder();
        for (int i = 0; i < operatorIndexTable.length - 1; i++) {
            operatorStringBuilder.append(operatorIndexTable[i]).append('\t');
        }
        operatorStringBuilder.append(operatorIndexTable[operatorIndexTable.length - 1]);
        bytes = operatorStringBuilder.toString().getBytes("UTF-8");
        outputStream.writeInt(bytes.length);
        outputStream.write(bytes);

        // output prefix index table
        outputStream.writeInt(prefixIndexTable.length);
        for (int i = 0; i < prefixIndexTable.length; i++) {
            outputStream.writeInt(prefixIndexTable[i]);
        }

        // output compressed address
        outputStream.writeInt(compressedLocationData.length);
        for (int i = 0; i < compressedLocationData.length; i++) {
            outputStream.writeInt(compressedLocationData[i]);
        }
        // output compressed operator
        outputStream.writeInt(compressedOperatorData.length);
        for (int i = 0; i < compressedOperatorData.length; i++) {
            outputStream.writeInt(compressedOperatorData[i]);
        }
        outputStream.close();
    }

    @Override
    public void loadExtra(InputStream district, InputStream intl, InputStream pub)
            throws IOException {
        loadDistrictCode(district, DISTRICT_CODE_SIGN);
        loadExtra(intl, INTERNATIONAL_CODE_SIGN);
        loadExtra(pub, PUBLIC_CODE_SIGN);
    }

    @Override
    public void loadExtra(String district, String international, String pub) throws IOException {
        // To change body of implemented methods use File | Settings | File
        // Templates.
        loadExtra(new FileInputStream(district), new FileInputStream(international),
                new FileInputStream(pub));
    }

    private void loadDistrictCode(InputStream in, char sign) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        BufferedReader reader = new BufferedReader(new InputStreamReader(bufferedInputStream));
        String line = null;
        String province = null;
        while ((line = reader.readLine()) != null) {
            int index = line.indexOf('|');
            if (index < 0) {
                province = line;
                if (!provinceToCitiesMap.containsKey(province)) {
                    provinceToCitiesMap.put(province, new ArrayList<String>());
                }
                continue;
            }
            String number = line.substring(0, index);
            String district = line.substring(index + 1);
            provinceToCitiesMap.get(province).add(district);
            cityToProvinceMap.put(district, province);
            trie.put(sign + number, province.equals(district) ? district : province + district);
        }
        reader.close();
    }

    @Override
    public List<String> getCitiesForProvince(String province) {
        return provinceToCitiesMap.get(province);
    }

    @Override
    public String getProviceForCity(String city) {
        return cityToProvinceMap.get(city);
    }

    private void loadExtra(InputStream in, char sign) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        BufferedReader reader = new BufferedReader(new InputStreamReader(bufferedInputStream));
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] splits = line.split("\\|");
            if (splits.length < 2) {
                continue;
            }
            trie.put(sign + splits[0], splits[1]);
        }
        reader.close();
    }

    @Override
    public String getPublicLocation(String number) {
        if (number == null || number.length() < 2) {
            return null;
        }
        number = ILLEGAL_NUMBER_PATTERN.matcher(number).replaceAll("");
        Map.Entry<String, String> entry = trie.select(PUBLIC_CODE_SIGN + number);
        if (number.startsWith(entry.getKey().substring(1))) {
            return entry.getValue();
        }
        return null;
    }

    @Override
    public void init(Context context) throws IOException {
        inited = false;
        File updatedLocationFile = new File(context.getFilesDir(), "location/update.ldb");
        if (updatedLocationFile.exists()) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "load location db from:" + updatedLocationFile.getAbsolutePath());
            }
            try {
                decode(new FileInputStream(updatedLocationFile));
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, e.toString(), e);
                }
                // fallback to base.ldb
                decode(context.getAssets().open("base.ldb"));
            }
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "load location db from base.ldb");
            }
            decode(context.getAssets().open("base.ldb"));
        }
        loadExtra(context.getAssets().open("fixed_phone"),
                context.getAssets().open("intl_phone"),
                context.getAssets().open("public_phone"));
        inited = true;
    }

}
