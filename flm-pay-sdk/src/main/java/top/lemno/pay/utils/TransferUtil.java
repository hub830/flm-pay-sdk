package top.lemno.pay.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 说明:
 * 传输过成功数据字节转化的util
 *
 * @author 百里
 */
public class TransferUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferUtil.class);

    /**
     * 字节转16进制
     *
     * @param bytes
     * @return
     */
    public static String byte2Hex(byte[] bytes) {
        return Hex.encodeHexString(bytes).toUpperCase();
    }

    /**
     * 16进制转字节
     *
     * @param hex
     * @return
     */
    public static byte[] hex2Byte(String hex) {
        try {
            return Hex.decodeHex(hex.toCharArray());
        } catch (DecoderException e) {
            LOGGER.error("hex2byte error:{}", e.getMessage(), e);
            return null;
        }

    }

    /**
     * 字节转成bit（字节数组表示）
     *
     * @param bytes
     * @return
     */
    public static byte[] byte2Bit(byte[] bytes) {
        int len = bytes.length;
        int bitLen = len * 8;
        byte[] bitArr = new byte[bitLen];
        for (int i = 0; i < len; i++) {
            byte b = bytes[i];
            bitArr[i * 8] = (byte) ((b >> 7) & 0x1);
            bitArr[i * 8 + 1] = (byte) ((b >> 6) & 0x1);
            bitArr[i * 8 + 2] = (byte) ((b >> 5) & 0x1);
            bitArr[i * 8 + 3] = (byte) ((b >> 4) & 0x1);
            bitArr[i * 8 + 4] = (byte) ((b >> 3) & 0x1);
            bitArr[i * 8 + 5] = (byte) ((b >> 2) & 0x1);
            bitArr[i * 8 + 6] = (byte) ((b >> 1) & 0x1);
            bitArr[i * 8 + 7] = (byte) ((b >> 0) & 0x1);
        }
        return bitArr;
    }

    /**
     * bit（字节数组标识）转字节
     *
     * @param bits
     * @return
     */
    public static byte[] bit2Byte(byte[] bits) {
        int bitLen = bits.length;
        if (bitLen % 8 != 0) {
            return new byte[]{0};
        }

        int len = bitLen / 8;
        byte[] byteArr = new byte[len];
        StringBuilder sbd = new StringBuilder();
        for (int i = 0; i < bits.length; i++) {
            sbd.append(bits[i]);
            if ((i + 1) % 8 == 0) {
                int byteIndex = (i - 7) / 8;
                if (sbd.charAt(0) == '0') {
                    // 正数
                    byteArr[byteIndex] = (byte) Integer.parseInt(sbd.toString(), 2);
                } else {
                    // 负数
                    byteArr[byteIndex] = (byte) (Integer.parseInt(sbd.toString(), 2) - 256);
                }
                sbd.setLength(0);
            }
        }
        return byteArr;
    }

}
