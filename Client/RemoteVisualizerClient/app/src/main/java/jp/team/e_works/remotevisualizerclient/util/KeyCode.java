package jp.team.e_works.remotevisualizerclient.util;

import android.view.KeyEvent;

public enum KeyCode {
    /** 不明なキー */
    UNKNOWN(KeyEvent.KEYCODE_UNKNOWN, -1),

    /** A */
    A(KeyEvent.KEYCODE_A, 65),
    /** B */
    B(KeyEvent.KEYCODE_B, 66),
    /** C */
    C(KeyEvent.KEYCODE_C, 67),
    /** D */
    D(KeyEvent.KEYCODE_D, 68),
    /** E */
    E(KeyEvent.KEYCODE_E, 69),
    /** F */
    F(KeyEvent.KEYCODE_F, 70),
    /** G */
    G(KeyEvent.KEYCODE_G, 71),
    /** H */
    H(KeyEvent.KEYCODE_H, 72),
    /** I */
    I(KeyEvent.KEYCODE_I, 73),
    /** J */
    J(KeyEvent.KEYCODE_J, 74),
    /** K */
    K(KeyEvent.KEYCODE_K, 75),
    /** L */
    L(KeyEvent.KEYCODE_L, 76),
    /** M */
    M(KeyEvent.KEYCODE_M, 77),
    /** N */
    N(KeyEvent.KEYCODE_N, 78),
    /** O */
    O(KeyEvent.KEYCODE_O, 79),
    /** P */
    P(KeyEvent.KEYCODE_P, 80),
    /** Q */
    Q(KeyEvent.KEYCODE_Q, 81),
    /** R */
    R(KeyEvent.KEYCODE_R, 82),
    /** S */
    S(KeyEvent.KEYCODE_S, 83),
    /** T */
    T(KeyEvent.KEYCODE_T, 84),
    /** U */
    U(KeyEvent.KEYCODE_U, 85),
    /** V */
    V(KeyEvent.KEYCODE_V, 86),
    /** W */
    W(KeyEvent.KEYCODE_W, 87),
    /** X */
    X(KeyEvent.KEYCODE_X, 88),
    /** Y */
    Y(KeyEvent.KEYCODE_Y, 89),
    /** Z */
    Z(KeyEvent.KEYCODE_Z, 90),

    /** F1 */
    F1(KeyEvent.KEYCODE_F1, 112),
    /** F2 */
    F2(KeyEvent.KEYCODE_F2, 113),
    /** F3 */
    F3(KeyEvent.KEYCODE_F3, 114),
    /** F4 */
    F4(KeyEvent.KEYCODE_F4, 115),
    /** F5 */
    F5(KeyEvent.KEYCODE_F5, 116),
    /** F6 */
    F6(KeyEvent.KEYCODE_F6, 117),
    /** F7 */
    F7(KeyEvent.KEYCODE_F7, 118),
    /** F8 */
    F8(KeyEvent.KEYCODE_F8, 119),
    /** F9 */
    F9(KeyEvent.KEYCODE_F9, 120),
    /** F10 */
    F10(KeyEvent.KEYCODE_F10, 121),
    /** F11 */
    F11(KeyEvent.KEYCODE_F11, 122),
    /** F12 */
    F12(KeyEvent.KEYCODE_F12, 123),

    /** テンキー 0 */
    NUMPAD_0(KeyEvent.KEYCODE_NUMPAD_0, 96),
    /** テンキー 1 */
    NUMPAD_1(KeyEvent.KEYCODE_NUMPAD_1, 97),
    /** テンキー 2 */
    NUMPAD_2(KeyEvent.KEYCODE_NUMPAD_2, 98),
    /** テンキー 3 */
    NUMPAD_3(KeyEvent.KEYCODE_NUMPAD_3, 99),
    /** テンキー 4 */
    NUMPAD_4(KeyEvent.KEYCODE_NUMPAD_4, 100),
    /** テンキー 5 */
    NUMPAD_5(KeyEvent.KEYCODE_NUMPAD_5, 101),
    /** テンキー 6 */
    NUMPAD_6(KeyEvent.KEYCODE_NUMPAD_6, 102),
    /** テンキー 7 */
    NUMPAD_7(KeyEvent.KEYCODE_NUMPAD_7, 103),
    /** テンキー 8 */
    NUMPAD_8(KeyEvent.KEYCODE_NUMPAD_8, 104),
    /** テンキー 9 */
    NUMPAD_9(KeyEvent.KEYCODE_NUMPAD_9, 105),
    /** テンキー '/' */
    NUMPAD_DIVIDE(KeyEvent.KEYCODE_NUMPAD_DIVIDE, 111),
    /** テンキー '*' */
    NUMPAD_MULTIPLY(KeyEvent.KEYCODE_NUMPAD_MULTIPLY, 106),
    /** テンキー '-' */
    NUMPAD_SUBTRACT(KeyEvent.KEYCODE_NUMPAD_SUBTRACT, 109),
    /** テンキー '+' */
    NUMPAD_ADD(KeyEvent.KEYCODE_NUMPAD_ADD, 107),
    /** テンキー '.' */
    NUMPAD_DOT(KeyEvent.KEYCODE_NUMPAD_DOT, 110),
    /** テンキー Enter */
    NUMPAD_ENTER(KeyEvent.KEYCODE_NUMPAD_ENTER, 108),

    /** Enter */
    ENTER(KeyEvent.KEYCODE_ENTER, 13),
    /** Ctrl */
    CTRL_LEFT(KeyEvent.KEYCODE_CTRL_LEFT, 17),
    /** Ctrl */
    CTRL_RIGHT(KeyEvent.KEYCODE_CTRL_RIGHT, 17),
    /** Alt */
    ALT_LEFT(KeyEvent.KEYCODE_ALT_LEFT, 18),
    /** Alt */
    ALT_RIGHT(KeyEvent.KEYCODE_ALT_RIGHT, 18),
    /** 矢印キー Up */
    ARROW_UP(KeyEvent.KEYCODE_DPAD_UP, 38),
    /** 矢印キー Down */
    ARROW_DOWN(KeyEvent.KEYCODE_DPAD_DOWN, 40),
    /** 矢印キー Left */
    ARROW_LEFT(KeyEvent.KEYCODE_DPAD_LEFT, 37),
    /** 矢印キー Right */
    ARROW_RIGHT(KeyEvent.KEYCODE_DPAD_RIGHT, 39);

    private int mAndroidKeyCode;
    private int mWindowsKeyCode;

    KeyCode(int androidKeyCode, int windowsKeyCode) {
        mAndroidKeyCode = androidKeyCode;
        mWindowsKeyCode = windowsKeyCode;
    }

    /**
     * Androidでのキーコードを取得する
     *
     * @return Androidでのキーコード
     */
    public int getAndroidKeyCode() {
        return mAndroidKeyCode;
    }

    /**
     * Windowsでのキーコードを取得する
     *
     * @return Windowsでのキーコード
     */
    public int getWindowsKeyCode() {
        return mWindowsKeyCode;
    }

    /**
     * AndroidでのキーコードからKeyCodeを取得する
     *
     * @param androidKeyCode Androidでのキーコード
     * @return KeyCode
     */
    public static KeyCode getKeyCodeByAndroidKeyCode(int androidKeyCode) {
        KeyCode result = UNKNOWN;
        for (KeyCode key : KeyCode.values()) {
            if (key.getAndroidKeyCode() == androidKeyCode) {
                result = key;
                break;
            }
        }
        return result;
    }

    /**
     * WindowsでのキーコードからKeyCodeを取得する
     *
     * @param windowsKeyCode Windowsでのキーコード
     * @return KeyCode
     */
    public static KeyCode getKetCodeByWindowsKeyCode(int windowsKeyCode) {
        KeyCode result = UNKNOWN;
        for (KeyCode key : KeyCode.values()) {
            if (key.getWindowsKeyCode() == windowsKeyCode) {
                result = key;
                break;
            }
        }
        return result;
    }
}
